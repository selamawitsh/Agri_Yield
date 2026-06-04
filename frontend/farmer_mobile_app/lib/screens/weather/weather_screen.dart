import 'package:flutter/material.dart';
import '../../models/weather_model.dart';
import '../../services/weather_service.dart';
import '../../services/farm_service.dart';
import '../../services/geospatial_service.dart';
import '../../models/farm_model.dart';
import '../../models/ndvi_reading_model.dart';
import '../../models/yield_prediction_model.dart';
import '../../widgets/ndvi_history_chart.dart';

class WeatherScreen extends StatefulWidget {
  const WeatherScreen({super.key});
  @override
  State<WeatherScreen> createState() => _WeatherScreenState();
}

class _WeatherScreenState extends State<WeatherScreen>
    with SingleTickerProviderStateMixin {
  final _weatherService = WeatherService();
  final _farmService = FarmService();
  final _geospatialService = GeospatialService();

  late TabController _tabController;

  List<FarmModel> _farms = [];
  FarmModel? _selectedFarm;

  WeatherReading? _current;
  List<WeatherReading> _forecast = [];
  List<WeatherReading> _rainfall = [];
  List<WeatherReading> _history = [];
  DroughtStatus? _drought;
  WeatherRisk? _risk;
  List<WeatherAlert> _alerts = [];
  NdviReadingModel? _latestNdvi;
  List<NdviReadingModel> _ndviHistory = [];
  YieldPredictionModel? _yieldPrediction;
  bool _ndviLoading = false;

  bool _loading = true;

  @override
  void initState() {
    super.initState();
    _tabController = TabController(length: 6, vsync: this);
    _loadFarms();
  }

  @override
  void dispose() {
    _tabController.dispose();
    super.dispose();
  }

  Future<void> _loadFarms() async {
    final result = await _farmService.getMyFarms();
    if (mounted) {
      if (result['success'] == true) {
        final farms = result['farms'] as List<FarmModel>;
        setState(() {
          _farms = farms;
          _selectedFarm = farms.isNotEmpty ? farms.first : null;
        });
        if (_selectedFarm != null) {
          _loadWeather(_selectedFarm!.id);
          _loadNdvi(_selectedFarm!.id);
        }
      } else {
        setState(() => _loading = false);
      }
    }
  }

  Future<void> _loadNdvi(String farmId) async {
    setState(() => _ndviLoading = true);
    final results = await Future.wait([
      _geospatialService.getLatestNdvi(farmId),
      _geospatialService.getNdviHistory(farmId, days: 90),
      _geospatialService.getYieldPrediction(farmId),
    ]);
    if (mounted) {
      setState(() {
        _latestNdvi = results[0] as NdviReadingModel?;
        _ndviHistory = results[1] as List<NdviReadingModel>;
        _yieldPrediction = results[2] as YieldPredictionModel?;
        _ndviLoading = false;
      });
    }
  }

  Future<void> _loadWeather(String farmId) async {
    setState(() => _loading = true);
    final results = await Future.wait([
      _weatherService.getCurrentWeather(farmId),
      _weatherService.getForecast(farmId),
      _weatherService.getRainfall(farmId),
      _weatherService.getDroughtStatus(farmId),
      _weatherService.getWeatherRisk(farmId),
      _weatherService.getAlerts(farmId),
      _weatherService.getHistory(farmId),
    ]);
    if (mounted) {
      setState(() {
        _current    = results[0] as WeatherReading?;
        _forecast   = results[1] as List<WeatherReading>;
        _rainfall   = results[2] as List<WeatherReading>;
        _drought    = results[3] as DroughtStatus?;
        _risk       = results[4] as WeatherRisk?;
        _alerts     = results[5] as List<WeatherAlert>;
        _history    = results[6] as List<WeatherReading>;
        _loading    = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFF4F7F5),
      appBar: AppBar(
        title: const Text('Weather & Climate'),
        backgroundColor: const Color(0xFF1B4332),
        foregroundColor: Colors.white,
        bottom: TabBar(
          controller: _tabController,
          indicatorColor: Colors.white,
          labelColor: Colors.white,
          unselectedLabelColor: Colors.white60,
          isScrollable: true,
          tabs: const [
            Tab(text: 'Current'),
            Tab(text: 'Forecast'),
            Tab(text: 'NDVI'),
            Tab(text: 'Drought'),
            Tab(text: 'Alerts'),
            Tab(text: 'History'),
          ],
        ),
      ),
      body: Column(
        children: [
          // Farm selector
          if (_farms.length > 1) _buildFarmSelector(),

          // Tabs
          Expanded(
            child: _loading
                ? const Center(child: CircularProgressIndicator(
                    color: Color(0xFF1B4332)))
                : TabBarView(
                    controller: _tabController,
                    children: [
                      _buildCurrentTab(),
                      _buildForecastTab(),
                      _buildNdviTab(),
                      _buildDroughtTab(),
                      _buildAlertsTab(),
                      _buildHistoryTab(),
                    ],
                  ),
          ),
        ],
      ),
    );
  }

  Widget _buildFarmSelector() {
    return Container(
      color: Colors.white,
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
      child: DropdownButtonFormField<FarmModel>(
        value: _selectedFarm,
        decoration: const InputDecoration(
          labelText: 'Select Farm',
          border: OutlineInputBorder(),
          isDense: true,
        ),
        items: _farms.map((f) => DropdownMenuItem(
          value: f,
          child: Text(f.farmName ?? f.cropType),
        )).toList(),
        onChanged: (farm) {
          setState(() => _selectedFarm = farm);
          if (farm != null) {
            _loadWeather(farm.id);
            _loadNdvi(farm.id);
          }
        },
      ),
    );
  }

  // ── Tab 1: Current Weather ─────────────────────────────────────────────────
  Widget _buildCurrentTab() {
    if (_current == null) {
      return _emptyState('No weather data', 'Weather data will appear once fetched', Icons.wb_sunny_outlined);
    }
    return RefreshIndicator(
      onRefresh: () => _selectedFarm != null ? _loadWeather(_selectedFarm!.id) : Future.value(),
      child: SingleChildScrollView(
        physics: const AlwaysScrollableScrollPhysics(),
        padding: const EdgeInsets.all(16),
        child: Column(
          children: [
            // Big weather card
            Container(
              width: double.infinity,
              padding: const EdgeInsets.all(24),
              decoration: BoxDecoration(
                gradient: const LinearGradient(
                  begin: Alignment.topLeft,
                  end: Alignment.bottomRight,
                  colors: [Color(0xFF1B4332), Color(0xFF2D6A4F)],
                ),
                borderRadius: BorderRadius.circular(20),
              ),
              child: Column(
                children: [
                  Icon(
                    _getWeatherIcon(_current!.rainfallMm, _current!.temperatureC),
                    size: 64, color: Colors.white,
                  ),
                  const SizedBox(height: 12),
                  Text('${_current!.temperatureC.toStringAsFixed(1)}°C',
                      style: const TextStyle(
                          color: Colors.white, fontSize: 48,
                          fontWeight: FontWeight.bold)),
                  Text(_current!.recordedDate,
                      style: const TextStyle(color: Colors.white70, fontSize: 13)),
                ],
              ),
            ),
            const SizedBox(height: 16),

            // Metric cards row
            Row(children: [
              Expanded(child: _metricCard('Rainfall', '${_current!.rainfallMm.toStringAsFixed(1)} mm',
                  Icons.water_drop, Colors.blue)),
              const SizedBox(width: 12),
              Expanded(child: _metricCard('Humidity',
                  _current!.humidityPct != null ? '${_current!.humidityPct!.toStringAsFixed(0)}%' : '—',
                  Icons.water, Colors.cyan)),
            ]),
            const SizedBox(height: 12),
            Row(children: [
              Expanded(child: _metricCard('Dry Day',
                  _current!.isDryDay ? 'Yes' : 'No',
                  Icons.wb_sunny, _current!.isDryDay ? Colors.orange : Colors.green)),
              const SizedBox(width: 12),
              Expanded(child: _metricCard('Risk Level',
                  _risk?.riskLevel ?? '—',
                  Icons.warning_amber,
                  _riskColor(_risk?.riskLevel ?? 'LOW'))),
            ]),

            // Drought summary
            if (_drought != null) ...[
              const SizedBox(height: 16),
              _droughtSummaryCard(),
            ],
          ],
        ),
      ),
    );
  }

  // ── Tab 2: 7-day Forecast ─────────────────────────────────────────────────
  Widget _buildForecastTab() {
    if (_forecast.isEmpty) {
      return _emptyState('No forecast data', 'Forecast will load from OpenWeather API', Icons.cloud_outlined);
    }
    return ListView.builder(
      padding: const EdgeInsets.all(16),
      itemCount: _forecast.length,
      itemBuilder: (ctx, i) {
        final f = _forecast[i];
        return Card(
          margin: const EdgeInsets.only(bottom: 8),
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
          child: ListTile(
            leading: CircleAvatar(
              backgroundColor: const Color(0xFF1B4332).withOpacity(0.1),
              child: Icon(_getWeatherIcon(f.rainfallMm, f.temperatureC),
                  color: const Color(0xFF1B4332), size: 20),
            ),
            title: Text(
              f.forecastHorizonDays != null
                  ? 'Day +${f.forecastHorizonDays}'
                  : f.recordedDate,
              style: const TextStyle(fontWeight: FontWeight.bold),
            ),
            subtitle: Text('Rain: ${f.rainfallMm.toStringAsFixed(1)}mm  •  '
                'Humidity: ${f.humidityPct?.toStringAsFixed(0) ?? '—'}%'),
            trailing: Text('${f.temperatureC.toStringAsFixed(1)}°C',
                style: const TextStyle(
                    fontWeight: FontWeight.bold,
                    fontSize: 16,
                    color: Color(0xFF1B4332))),
          ),
        );
      },
    );
  }

  // ── Tab 3: NDVI & Yield ───────────────────────────────────────────────────
  Widget _buildNdviTab() {
    if (_ndviLoading) {
      return const Center(
          child: CircularProgressIndicator(color: Color(0xFF1B4332)));
    }
    if (_latestNdvi == null && _ndviHistory.isEmpty) {
      return _emptyState(
        'No satellite data yet',
        'NDVI readings sync every few days after your farm is registered',
        Icons.satellite_alt,
      );
    }
    return RefreshIndicator(
      onRefresh: () => _selectedFarm != null
          ? _loadNdvi(_selectedFarm!.id)
          : Future.value(),
      child: SingleChildScrollView(
        physics: const AlwaysScrollableScrollPhysics(),
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            if (_latestNdvi != null) ...[
              Container(
                width: double.infinity,
                padding: const EdgeInsets.all(20),
                decoration: BoxDecoration(
                  gradient: LinearGradient(
                    colors: [
                      _latestNdvi!.healthColor,
                      _latestNdvi!.healthColor.withOpacity(0.7),
                    ],
                  ),
                  borderRadius: BorderRadius.circular(16),
                ),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    const Text('Latest NDVI',
                        style: TextStyle(color: Colors.white70, fontSize: 12)),
                    const SizedBox(height: 4),
                    Text(
                      _latestNdvi!.ndviValue.toStringAsFixed(3),
                      style: const TextStyle(
                          color: Colors.white,
                          fontSize: 36,
                          fontWeight: FontWeight.bold),
                    ),
                    Text(
                      '${_latestNdvi!.healthLabel} · '
                      '${_latestNdvi!.cloudCoverage.toStringAsFixed(0)}% cloud',
                      style: const TextStyle(color: Colors.white, fontSize: 13),
                    ),
                    if (_latestNdvi!.recordedDate.isNotEmpty)
                      Text('Recorded ${_latestNdvi!.recordedDate}',
                          style: const TextStyle(
                              color: Colors.white70, fontSize: 11)),
                  ],
                ),
              ),
              const SizedBox(height: 16),
            ],
            if (_yieldPrediction != null) ...[
              Card(
                shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(12)),
                child: Padding(
                  padding: const EdgeInsets.all(16),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      const Text('Yield forecast',
                          style: TextStyle(
                              fontWeight: FontWeight.bold, fontSize: 16)),
                      const SizedBox(height: 8),
                      Text(
                        '${_yieldPrediction!.totalYieldMeanQuintals.toStringAsFixed(1)} quintals (est.)',
                        style: const TextStyle(
                            fontSize: 18, color: Color(0xFF1B4332)),
                      ),
                      Text(
                        'Confidence ${_yieldPrediction!.confidencePct}%'
                        '${_yieldPrediction!.weeksToHarvest != null ? ' · ~${_yieldPrediction!.weeksToHarvest} wks to harvest' : ''}',
                        style: TextStyle(color: Colors.grey.shade600),
                      ),
                    ],
                  ),
                ),
              ),
              const SizedBox(height: 16),
            ],
            const Text('NDVI history (90 days)',
                style: TextStyle(fontWeight: FontWeight.bold, fontSize: 15)),
            const SizedBox(height: 12),
            Card(
              shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(12)),
              child: Padding(
                padding: const EdgeInsets.all(16),
                child: NdviHistoryChart(readings: _ndviHistory),
              ),
            ),
          ],
        ),
      ),
    );
  }

  // ── Tab 4: Drought Monitor ────────────────────────────────────────────────
  Widget _buildDroughtTab() {
    if (_drought == null) {
      return _emptyState('No drought data', 'Drought monitoring will begin after weather data is collected', Icons.water_drop_outlined);
    }
    return SingleChildScrollView(
      padding: const EdgeInsets.all(16),
      child: Column(
        children: [
          // Status card
          Container(
            width: double.infinity,
            padding: const EdgeInsets.all(20),
            decoration: BoxDecoration(
              color: _drought!.isTriggered ? Colors.red[50] : Colors.green[50],
              borderRadius: BorderRadius.circular(16),
              border: Border.all(
                color: _drought!.isTriggered ? Colors.red[200]! : Colors.green[200]!,
              ),
            ),
            child: Column(
              children: [
                Icon(
                  _drought!.isTriggered ? Icons.warning : Icons.check_circle,
                  size: 48,
                  color: _drought!.isTriggered ? Colors.red : Colors.green,
                ),
                const SizedBox(height: 8),
                Text(
                  _drought!.isTriggered ? 'DROUGHT TRIGGERED' : 'Normal Conditions',
                  style: TextStyle(
                    fontWeight: FontWeight.bold, fontSize: 18,
                    color: _drought!.isTriggered ? Colors.red[700] : Colors.green[700],
                  ),
                ),
                if (_drought!.isTriggered)
                  const Padding(
                    padding: EdgeInsets.only(top: 8),
                    child: Text(
                      'Parametric insurance refund process has been initiated',
                      textAlign: TextAlign.center,
                      style: TextStyle(color: Colors.red, fontSize: 12),
                    ),
                  ),
              ],
            ),
          ),
          const SizedBox(height: 16),

          // Dry days progress
          Container(
            padding: const EdgeInsets.all(20),
            decoration: BoxDecoration(
              color: Colors.white,
              borderRadius: BorderRadius.circular(16),
              boxShadow: [BoxShadow(color: Colors.black.withOpacity(0.05), blurRadius: 8)],
            ),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                const Text('Consecutive Dry Days',
                    style: TextStyle(fontWeight: FontWeight.bold, fontSize: 15)),
                const SizedBox(height: 12),
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Text('${_drought!.consecutiveDryDays} days',
                        style: const TextStyle(fontSize: 28, fontWeight: FontWeight.bold)),
                    Text('Threshold: ${_drought!.droughtThresholdDays} days',
                        style: const TextStyle(color: Colors.grey, fontSize: 12)),
                  ],
                ),
                const SizedBox(height: 8),
                LinearProgressIndicator(
                  value: _drought!.droughtProgress.clamp(0.0, 1.0),
                  backgroundColor: Colors.grey[200],
                  color: _drought!.isTriggered ? Colors.red
                      : _drought!.droughtProgress > 0.6 ? Colors.orange
                      : Colors.green,
                  minHeight: 10,
                  borderRadius: BorderRadius.circular(5),
                ),
                const SizedBox(height: 8),
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Text('Warning at ${(0.67 * _drought!.droughtThresholdDays).toInt()} days',
                        style: const TextStyle(color: Colors.orange, fontSize: 11)),
                    Text('Trigger at ${_drought!.droughtThresholdDays} days',
                        style: const TextStyle(color: Colors.red, fontSize: 11)),
                  ],
                ),
              ],
            ),
          ),
          const SizedBox(height: 16),

          // Rainfall history
          if (_rainfall.isNotEmpty) ...[
            const Align(
              alignment: Alignment.centerLeft,
              child: Text('Last 30 Days Rainfall',
                  style: TextStyle(fontWeight: FontWeight.bold, fontSize: 15)),
            ),
            const SizedBox(height: 8),
            ...(_rainfall.take(10).map((r) => Container(
              margin: const EdgeInsets.only(bottom: 6),
              padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 10),
              decoration: BoxDecoration(
                color: Colors.white,
                borderRadius: BorderRadius.circular(10),
              ),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Text(r.recordedDate,
                      style: const TextStyle(fontWeight: FontWeight.w500)),
                  Row(children: [
                    Icon(Icons.water_drop,
                        size: 14,
                        color: r.rainfallMm > 0 ? Colors.blue : Colors.grey),
                    const SizedBox(width: 4),
                    Text('${r.rainfallMm.toStringAsFixed(1)} mm',
                        style: TextStyle(
                            color: r.rainfallMm > 0 ? Colors.blue : Colors.grey,
                            fontWeight: FontWeight.bold)),
                  ]),
                ],
              ),
            ))),
          ],
        ],
      ),
    );
  }

  // ── Tab 4: Alerts ─────────────────────────────────────────────────────────
  Widget _buildAlertsTab() {
    if (_alerts.isEmpty) {
      return _emptyState('No alerts', 'You will be notified when weather conditions require attention', Icons.notifications_none);
    }
    return ListView.builder(
      padding: const EdgeInsets.all(16),
      itemCount: _alerts.length,
      itemBuilder: (ctx, i) {
        final alert = _alerts[i];
        return Card(
          margin: const EdgeInsets.only(bottom: 12),
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
          child: Padding(
            padding: const EdgeInsets.all(16),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Row(children: [
                  Container(
                    padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                    decoration: BoxDecoration(
                      color: _severityColor(alert.severity).withOpacity(0.1),
                      borderRadius: BorderRadius.circular(6),
                    ),
                    child: Text(alert.severity,
                        style: TextStyle(
                            color: _severityColor(alert.severity),
                            fontWeight: FontWeight.bold,
                            fontSize: 11)),
                  ),
                  const SizedBox(width: 8),
                  Expanded(
                    child: Text(_alertTypeLabel(alert.alertType),
                        style: const TextStyle(fontWeight: FontWeight.bold)),
                  ),
                ]),
                const SizedBox(height: 8),
                Text(alert.messageEn,
                    style: const TextStyle(color: Colors.black87, fontSize: 13)),
                if (alert.messageAm != null) ...[
                  const SizedBox(height: 4),
                  Text(alert.messageAm!,
                      style: const TextStyle(color: Colors.grey, fontSize: 12)),
                ],
                const SizedBox(height: 8),
                Text(alert.createdAt.substring(0, 10),
                    style: const TextStyle(color: Colors.grey, fontSize: 11)),
              ],
            ),
          ),
        );
      },
    );
  }

  Widget _buildHistoryTab() {
    if (_history.isEmpty) {
      return _emptyState(
        'No historical weather data',
        'Historical weather records will appear here when available',
        Icons.history,
      );
    }

    return RefreshIndicator(
      onRefresh: () => _selectedFarm != null
          ? _loadWeather(_selectedFarm!.id)
          : Future.value(),
      child: ListView.builder(
        padding: const EdgeInsets.all(16),
        itemCount: _history.length,
        itemBuilder: (context, index) {
          final weather = _history[index];

          return Card(
            margin: const EdgeInsets.only(bottom: 10),
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(12),
            ),
            child: ListTile(
              leading: CircleAvatar(
                backgroundColor:
                const Color(0xFF1B4332).withOpacity(0.1),
                child: Icon(
                  _getWeatherIcon(
                    weather.rainfallMm,
                    weather.temperatureC,
                  ),
                  color: const Color(0xFF1B4332),
                ),
              ),
              title: Text(
                weather.recordedDate,
                style: const TextStyle(
                  fontWeight: FontWeight.bold,
                ),
              ),
              subtitle: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    'Rainfall: ${weather.rainfallMm.toStringAsFixed(1)} mm',
                  ),
                  Text(
                    'Humidity: ${weather.humidityPct?.toStringAsFixed(0) ?? 'N/A'}%',
                  ),
                  Text(
                    weather.isDryDay
                        ? 'Dry Day'
                        : 'Rain Recorded',
                  ),
                ],
              ),
              trailing: Text(
                '${weather.temperatureC.toStringAsFixed(1)}°C',
                style: const TextStyle(
                  fontWeight: FontWeight.bold,
                  color: Color(0xFF1B4332),
                ),
              ),
            ),
          );
        },
      ),
    );
  }
  // ── Helpers ───────────────────────────────────────────────────────────────
  Widget _droughtSummaryCard() {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: _drought!.isTriggered ? Colors.red[50] : Colors.orange[50],
        borderRadius: BorderRadius.circular(12),
        border: Border.all(
          color: _drought!.isTriggered ? Colors.red[200]! : Colors.orange[200]!),
      ),
      child: Row(children: [
        Icon(_drought!.isTriggered ? Icons.warning : Icons.info_outline,
            color: _drought!.isTriggered ? Colors.red : Colors.orange),
        const SizedBox(width: 12),
        Expanded(child: Text(
          _drought!.isTriggered
              ? 'Drought triggered! ${_drought!.consecutiveDryDays} consecutive dry days.'
              : '${_drought!.consecutiveDryDays} dry days. Threshold: ${_drought!.droughtThresholdDays}.',
          style: TextStyle(
              color: _drought!.isTriggered ? Colors.red[700] : Colors.orange[700],
              fontWeight: FontWeight.w500,
              fontSize: 13),
        )),
      ]),
    );
  }

  Widget _metricCard(String label, String value, IconData icon, Color color) {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(12),
        boxShadow: [BoxShadow(color: Colors.black.withOpacity(0.04), blurRadius: 8)],
      ),
      child: Row(children: [
        Container(
          padding: const EdgeInsets.all(8),
          decoration: BoxDecoration(
            color: color.withOpacity(0.1), shape: BoxShape.circle),
          child: Icon(icon, color: color, size: 20),
        ),
        const SizedBox(width: 12),
        Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
          Text(label, style: const TextStyle(color: Colors.grey, fontSize: 11)),
          Text(value, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
        ]),
      ]),
    );
  }

  Widget _emptyState(String title, String subtitle, IconData icon) {
    return Center(
      child: Column(mainAxisAlignment: MainAxisAlignment.center, children: [
        Icon(icon, size: 64, color: Colors.grey[300]),
        const SizedBox(height: 16),
        Text(title, style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold, color: Colors.grey)),
        const SizedBox(height: 8),
        Padding(
          padding: const EdgeInsets.symmetric(horizontal: 40),
          child: Text(subtitle, textAlign: TextAlign.center,
              style: const TextStyle(color: Colors.grey, fontSize: 13)),
        ),
      ]),
    );
  }

  IconData _getWeatherIcon(double rain, double temp) {
    if (rain > 10) return Icons.thunderstorm;
    if (rain > 1) return Icons.grain;
    if (temp > 35) return Icons.wb_sunny;
    if (temp < 5) return Icons.ac_unit;
    return Icons.wb_cloudy;
  }

  Color _riskColor(String level) {
    switch (level) {
      case 'CRITICAL': return Colors.red;
      case 'HIGH': return Colors.orange;
      case 'MEDIUM': return Colors.amber;
      default: return Colors.green;
    }
  }

  Color _severityColor(String severity) {
    switch (severity) {
      case 'CRITICAL': return Colors.red;
      case 'HIGH': return Colors.orange;
      case 'MEDIUM': return Colors.amber[700]!;
      default: return Colors.green;
    }
  }

  String _alertTypeLabel(String type) {
    switch (type) {
      case 'FROST_WARNING': return '❄️ Frost Warning';
      case 'HEAVY_RAIN': return '🌧️ Heavy Rain';
      case 'DROUGHT_WARNING': return '☀️ Drought Warning';
      case 'DROUGHT_TRIGGER': return '🚨 Drought Triggered';
      case 'HEATWAVE': return '🌡️ Heatwave';
      default: return type;
    }
  }
}

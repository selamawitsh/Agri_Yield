import 'package:flutter/material.dart';
import '../../models/farm_model.dart';
import '../../models/input_need_model.dart';
import '../../services/farm_service.dart';
import 'input_needs_screen.dart';

class FarmDetailScreen extends StatefulWidget {
  final String farmId;
  const FarmDetailScreen({super.key, required this.farmId});

  @override
  State<FarmDetailScreen> createState() => _FarmDetailScreenState();
}

class _FarmDetailScreenState extends State<FarmDetailScreen>
    with SingleTickerProviderStateMixin {
  final _farmService = FarmService();

  FarmModel? _farm;
  Map<String, dynamic>? _digitalTwin;
  Map<String, dynamic>? _agriScore;
  List<InputNeedModel> _inputNeeds = [];

  bool _isLoading = true;
  String? _error;
  late TabController _tabController;

  @override
  void initState() {
    super.initState();
    _tabController = TabController(length: 4, vsync: this);
    _loadFarmData();
  }

  @override
  void dispose() {
    _tabController.dispose();
    super.dispose();
  }

  Future<void> _loadFarmData() async {
    setState(() {
      _isLoading = true;
      _error = null;
    });

    final farmResult = await _farmService.getFarmById(widget.farmId);
    final twinResult = await _farmService.getDigitalTwin(widget.farmId);
    final scoreResult = await _farmService.getAgriScore(widget.farmId);
    final inputNeedsResult =
        await _farmService.getInputNeeds(widget.farmId);

    if (mounted) {
      setState(() {
        _isLoading = false;
        if (farmResult['success'] == true) {
          _farm = farmResult['farm'] as FarmModel;
        } else {
          _error = farmResult['message'];
        }
        if (twinResult['success'] == true) {
          _digitalTwin = twinResult['digitalTwin'];
        }
        if (scoreResult['success'] == true) {
          _agriScore = scoreResult['agriScore'];
        }
        if (inputNeedsResult['success'] == true) {
          _inputNeeds =
              inputNeedsResult['inputNeeds'] as List<InputNeedModel>;
        }
      });
    }
  }

  Future<void> _confirmPlanting() async {
    final date = await showDatePicker(
      context: context,
      initialDate: DateTime.now(),
      firstDate: DateTime.now().subtract(const Duration(days: 30)),
      lastDate: DateTime.now(),
    );

    if (date == null || !mounted) return;

    final dateStr =
        '${date.year}-${date.month.toString().padLeft(2, '0')}-'
        '${date.day.toString().padLeft(2, '0')}';

    final result = await _farmService.confirmPlanting(
      farmId: widget.farmId,
      plantingDate: dateStr,
    );

    if (mounted) {
      if (result['success'] == true) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(
            content: Text('Planting confirmed!'),
            backgroundColor: Colors.green,
          ),
        );
        _loadFarmData();
      } else {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content:
                Text(result['message'] ?? 'Failed to confirm planting'),
            backgroundColor: Colors.red,
          ),
        );
      }
    }
  }

  Color _statusColor(String status) {
    switch (status) {
      case 'PENDING_VERIFICATION':
        return Colors.orange;
      case 'VERIFIED':
        return Colors.blue;
      case 'ACTIVE':
        return Colors.green;
      case 'GROWING':
        return Colors.green.shade700;
      case 'HARVESTED':
        return Colors.teal;
      case 'FAILED':
        return Colors.red;
      default:
        return Colors.grey;
    }
  }

  Color _inputNeedStatusColor(String status) {
    switch (status) {
      case 'OPEN':
        return Colors.orange;
      case 'PARTIALLY_FUNDED':
        return Colors.blue;
      case 'FULLY_FUNDED':
        return Colors.green;
      case 'CANCELLED':
        return Colors.red;
      default:
        return Colors.grey;
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(_farm?.displayName ?? 'Farm Details'),
        backgroundColor: Colors.green,
        foregroundColor: Colors.white,
        actions: [
          IconButton(
            icon: const Icon(Icons.refresh),
            onPressed: _loadFarmData,
          ),
        ],
        bottom: TabBar(
          controller: _tabController,
          indicatorColor: Colors.white,
          labelColor: Colors.white,
          unselectedLabelColor: Colors.white70,
          isScrollable: true,
          tabs: const [
            Tab(text: 'Overview'),
            Tab(text: 'Input Needs'),
            Tab(text: 'Digital Twin'),
            Tab(text: 'Agri-Score'),
          ],
        ),
      ),
      body: _isLoading
          ? const Center(
              child: CircularProgressIndicator(color: Colors.green))
          : _error != null
              ? Center(
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      const Icon(Icons.error_outline,
                          size: 64, color: Colors.red),
                      const SizedBox(height: 16),
                      Text(_error!),
                      const SizedBox(height: 16),
                      ElevatedButton(
                        onPressed: _loadFarmData,
                        child: const Text('Retry'),
                      ),
                    ],
                  ),
                )
              : TabBarView(
                  controller: _tabController,
                  children: [
                    _buildOverviewTab(),
                    _buildInputNeedsTab(),
                    _buildDigitalTwinTab(),
                    _buildAgriScoreTab(),
                  ],
                ),
    );
  }

  // ── Tab 1: Overview ──────────────────────────────────────────────────────
  Widget _buildOverviewTab() {
    if (_farm == null) return const SizedBox();
    final farm = _farm!;
    final statusColor = _statusColor(farm.status);

    return SingleChildScrollView(
      padding: const EdgeInsets.all(16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Card(
            shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(12)),
            child: Padding(
              padding: const EdgeInsets.all(16),
              child: Row(
                children: [
                  CircleAvatar(
                    backgroundColor: statusColor.withOpacity(0.1),
                    child: Icon(Icons.agriculture, color: statusColor),
                  ),
                  const SizedBox(width: 12),
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(
                          farm.displayName,
                          style: const TextStyle(
                            fontSize: 18,
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                        const SizedBox(height: 4),
                        Container(
                          padding: const EdgeInsets.symmetric(
                              horizontal: 8, vertical: 2),
                          decoration: BoxDecoration(
                            color: statusColor.withOpacity(0.1),
                            borderRadius: BorderRadius.circular(12),
                            border: Border.all(color: statusColor),
                          ),
                          child: Text(
                            farm.statusLabel,
                            style: TextStyle(
                              color: statusColor,
                              fontSize: 12,
                              fontWeight: FontWeight.bold,
                            ),
                          ),
                        ),
                      ],
                    ),
                  ),
                ],
              ),
            ),
          ),
          const SizedBox(height: 16),

          Card(
            shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(12)),
            child: Padding(
              padding: const EdgeInsets.all(16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  const Text(
                    'Farm Details',
                    style: TextStyle(
                        fontSize: 16, fontWeight: FontWeight.bold),
                  ),
                  const Divider(),
                  _buildDetailRow('Crop Type', farm.cropType),
                  _buildDetailRow('Region', farm.region),
                  _buildDetailRow('Kebele', farm.kebeleCode),
                  _buildDetailRow(
                    'Area',
                    '${farm.areaHectares.toStringAsFixed(4)} hectares',
                  ),
                  _buildDetailRow(
                    'GPS Center',
                    '${farm.gpsCentroidLat.toStringAsFixed(4)}, '
                        '${farm.gpsCentroidLng.toStringAsFixed(4)}',
                  ),
                  _buildDetailRow(
                    'Satellite Verified',
                    farm.satelliteVerified ? 'Yes ✓' : 'Pending',
                  ),
                ],
              ),
            ),
          ),
          const SizedBox(height: 16),

          if (farm.status == 'ACTIVE' || farm.status == 'VERIFIED') ...[
            SizedBox(
              width: double.infinity,
              child: ElevatedButton.icon(
                onPressed: _confirmPlanting,
                icon: const Icon(Icons.eco),
                label: const Text('Confirm Planting'),
                style: ElevatedButton.styleFrom(
                  backgroundColor: Colors.green,
                  foregroundColor: Colors.white,
                  padding: const EdgeInsets.symmetric(vertical: 14),
                  shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(8)),
                ),
              ),
            ),
            const SizedBox(height: 12),
          ],

          SizedBox(
            width: double.infinity,
            child: OutlinedButton.icon(
              onPressed: () async {
                final submitted = await Navigator.push<bool>(
                  context,
                  MaterialPageRoute(
                    builder: (_) =>
                        InputNeedsScreen(farmId: widget.farmId),
                  ),
                );
                if (submitted == true) {
                  _loadFarmData();
                  _tabController.animateTo(1);
                }
              },
              icon: const Icon(Icons.list_alt, color: Colors.green),
              label: const Text(
                'Submit Input Needs',
                style: TextStyle(color: Colors.green),
              ),
              style: OutlinedButton.styleFrom(
                padding: const EdgeInsets.symmetric(vertical: 14),
                side: const BorderSide(color: Colors.green),
                shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(8)),
              ),
            ),
          ),
        ],
      ),
    );
  }

  // ── Tab 2: Input Needs ───────────────────────────────────────────────────
  Widget _buildInputNeedsTab() {
    return Scaffold(
      body: _inputNeeds.isEmpty
          ? Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Icon(Icons.inventory_2_outlined,
                      size: 64, color: Colors.grey.shade400),
                  const SizedBox(height: 16),
                  const Text(
                    'No input needs submitted yet',
                    style: TextStyle(
                        fontSize: 16, fontWeight: FontWeight.bold),
                  ),
                  const SizedBox(height: 8),
                  Text(
                    'Submit your seeds, fertilizers and tools\nto attract investors',
                    style: TextStyle(color: Colors.grey.shade500),
                    textAlign: TextAlign.center,
                  ),
                  const SizedBox(height: 24),
                  ElevatedButton.icon(
                    onPressed: () async {
                      final submitted = await Navigator.push<bool>(
                        context,
                        MaterialPageRoute(
                          builder: (_) =>
                              InputNeedsScreen(farmId: widget.farmId),
                        ),
                      );
                      if (submitted == true) _loadFarmData();
                    },
                    icon: const Icon(Icons.add),
                    label: const Text('Submit Input Needs'),
                    style: ElevatedButton.styleFrom(
                      backgroundColor: Colors.green,
                      foregroundColor: Colors.white,
                      padding: const EdgeInsets.symmetric(
                          horizontal: 24, vertical: 12),
                    ),
                  ),
                ],
              ),
            )
          : RefreshIndicator(
              onRefresh: _loadFarmData,
              color: Colors.green,
              child: ListView.builder(
                padding: const EdgeInsets.all(16),
                itemCount: _inputNeeds.length,
                itemBuilder: (context, index) =>
                    _buildInputNeedCard(_inputNeeds[index]),
              ),
            ),
      floatingActionButton: _inputNeeds.isNotEmpty
          ? FloatingActionButton.extended(
              onPressed: () async {
                final submitted = await Navigator.push<bool>(
                  context,
                  MaterialPageRoute(
                    builder: (_) =>
                        InputNeedsScreen(farmId: widget.farmId),
                  ),
                );
                if (submitted == true) _loadFarmData();
              },
              backgroundColor: Colors.green,
              icon: const Icon(Icons.add, color: Colors.white),
              label: const Text('Add More',
                  style: TextStyle(color: Colors.white)),
            )
          : null,
    );
  }

  Widget _buildInputNeedCard(InputNeedModel inputNeed) {
    final statusColor = _inputNeedStatusColor(inputNeed.status);

    return Card(
      margin: const EdgeInsets.only(bottom: 16),
      shape:
          RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
      elevation: 2,
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Header row
            Row(
              children: [
                const Icon(Icons.inventory_2, color: Colors.green),
                const SizedBox(width: 8),
                Expanded(
                  child: Text(
                    'Season Input Package',
                    style: const TextStyle(
                      fontSize: 16,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                ),
                Container(
                  padding: const EdgeInsets.symmetric(
                      horizontal: 10, vertical: 4),
                  decoration: BoxDecoration(
                    color: statusColor.withOpacity(0.1),
                    borderRadius: BorderRadius.circular(20),
                    border: Border.all(color: statusColor),
                  ),
                  child: Text(
                    inputNeed.statusLabel,
                    style: TextStyle(
                      color: statusColor,
                      fontSize: 12,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                ),
              ],
            ),
            const SizedBox(height: 12),

            // Funding progress
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text(
                  'ETB ${inputNeed.fundedAmountEtb.toStringAsFixed(0)} '
                  '/ ETB ${inputNeed.totalAmountEtb.toStringAsFixed(0)}',
                  style: const TextStyle(fontWeight: FontWeight.w500),
                ),
                Text(
                  '${inputNeed.fundingPercentage.toStringAsFixed(0)}%',
                  style: TextStyle(
                    color: inputNeed.fundingPercentage >= 100
                        ? Colors.green
                        : Colors.orange,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ],
            ),
            const SizedBox(height: 6),
            LinearProgressIndicator(
              value: inputNeed.fundingPercentage / 100,
              backgroundColor: Colors.grey.shade200,
              color: inputNeed.fundingPercentage >= 100
                  ? Colors.green
                  : Colors.orange,
              minHeight: 8,
              borderRadius: BorderRadius.circular(4),
            ),
            const SizedBox(height: 16),

            // Items list
            const Text(
              'Items',
              style: TextStyle(
                fontWeight: FontWeight.bold,
                fontSize: 14,
              ),
            ),
            const SizedBox(height: 8),
            ...inputNeed.items.map((item) => _buildItemRow(item)),
          ],
        ),
      ),
    );
  }

  Widget _buildItemRow(InputNeedItemModel item) {
    final categoryIcons = {
      'SEED': Icons.grass,
      'FERTILIZER': Icons.science,
      'PESTICIDE': Icons.bug_report,
      'TOOL': Icons.build,
      'OTHER': Icons.category,
    };

    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 6),
      child: Row(
        children: [
          Container(
            padding: const EdgeInsets.all(6),
            decoration: BoxDecoration(
              color: Colors.green.shade50,
              borderRadius: BorderRadius.circular(6),
            ),
            child: Icon(
              categoryIcons[item.productCategory] ?? Icons.category,
              size: 18,
              color: Colors.green,
            ),
          ),
          const SizedBox(width: 10),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  item.productName,
                  style: const TextStyle(fontWeight: FontWeight.w500),
                ),
                Text(
                  '${item.quantity.toStringAsFixed(1)} ${item.unit} '
                  '· ${item.productCategory}',
                  style: TextStyle(
                    color: Colors.grey.shade600,
                    fontSize: 12,
                  ),
                ),
              ],
            ),
          ),
          Column(
            crossAxisAlignment: CrossAxisAlignment.end,
            children: [
              Text(
                'ETB ${item.estimatedPriceEtb.toStringAsFixed(0)}',
                style: const TextStyle(
                  fontWeight: FontWeight.bold,
                  color: Colors.green,
                ),
              ),
              Text(
                'Order #${item.sequenceOrder}',
                style: TextStyle(
                  color: Colors.grey.shade500,
                  fontSize: 11,
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }

  // ── Tab 3: Digital Twin ──────────────────────────────────────────────────
  Widget _buildDigitalTwinTab() {
    if (_digitalTwin == null) {
      return Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(Icons.satellite_alt, size: 64, color: Colors.grey.shade400),
            const SizedBox(height: 16),
            const Text('Digital twin not available yet'),
            Text(
              'Data will appear after satellite verification',
              style:
                  TextStyle(color: Colors.grey.shade500, fontSize: 13),
            ),
          ],
        ),
      );
    }

    final ndviHistory = _digitalTwin!['ndviHistory'] as List? ?? [];
    final photoHistory = _digitalTwin!['photoHistory'] as List? ?? [];

    return SingleChildScrollView(
      padding: const EdgeInsets.all(16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Card(
            shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(12)),
            child: Padding(
              padding: const EdgeInsets.all(16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  const Text(
                    'NDVI History',
                    style: TextStyle(
                        fontSize: 16, fontWeight: FontWeight.bold),
                  ),
                  const SizedBox(height: 8),
                  if (ndviHistory.isEmpty)
                    Text('No NDVI readings yet',
                        style:
                            TextStyle(color: Colors.grey.shade500))
                  else
                    ...ndviHistory.map((r) => ListTile(
                          leading: const Icon(Icons.bar_chart,
                              color: Colors.green),
                          title: Text('NDVI: ${r['ndvi']}'),
                          subtitle: Text(r['date'] ?? ''),
                          trailing: Text('${r['cloudCoverage']}% cloud',
                              style: const TextStyle(fontSize: 12)),
                        )),
                ],
              ),
            ),
          ),
          const SizedBox(height: 16),

          Card(
            shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(12)),
            child: Padding(
              padding: const EdgeInsets.all(16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  const Text(
                    'Photo History',
                    style: TextStyle(
                        fontSize: 16, fontWeight: FontWeight.bold),
                  ),
                  const SizedBox(height: 8),
                  if (photoHistory.isEmpty)
                    Text('No photos uploaded yet',
                        style: TextStyle(color: Colors.grey.shade500))
                  else
                    ...photoHistory.map((p) => ListTile(
                          leading:
                              const Icon(Icons.photo, color: Colors.blue),
                          title: Text(p['type'] ?? ''),
                          subtitle: Text(p['uploadedAt'] ?? ''),
                        )),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }

  // ── Tab 4: Agri-Score ────────────────────────────────────────────────────
  Widget _buildAgriScoreTab() {
    if (_agriScore == null) {
      return Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(Icons.score, size: 64, color: Colors.grey.shade400),
            const SizedBox(height: 16),
            const Text('No Agri-Score yet'),
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 32),
              child: Text(
                'Your score will be calculated after completing your first season',
                style:
                    TextStyle(color: Colors.grey.shade500, fontSize: 13),
                textAlign: TextAlign.center,
              ),
            ),
          ],
        ),
      );
    }

    final score = _agriScore!['score'] ?? 0;
    const maxScore = 900;
    final percentage = (score / maxScore).clamp(0.0, 1.0);

    return SingleChildScrollView(
      padding: const EdgeInsets.all(16),
      child: Card(
        shape:
            RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
        child: Padding(
          padding: const EdgeInsets.all(24),
          child: Column(
            children: [
              const Text(
                'Current Agri-Score',
                style:
                    TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
              ),
              const SizedBox(height: 16),
              Stack(
                alignment: Alignment.center,
                children: [
                  SizedBox(
                    height: 120,
                    width: 120,
                    child: CircularProgressIndicator(
                      value: percentage,
                      strokeWidth: 12,
                      backgroundColor: Colors.grey.shade200,
                      color: percentage > 0.6
                          ? Colors.green
                          : percentage > 0.3
                              ? Colors.orange
                              : Colors.red,
                    ),
                  ),
                  Column(
                    children: [
                      Text(
                        '$score',
                        style: const TextStyle(
                          fontSize: 32,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                      Text(
                        '/ $maxScore',
                        style:
                            TextStyle(color: Colors.grey.shade500),
                      ),
                    ],
                  ),
                ],
              ),
              const SizedBox(height: 24),
              const Text('Score Breakdown',
                  style: TextStyle(fontWeight: FontWeight.bold)),
              const SizedBox(height: 12),
              _buildScoreBar('Voucher Discipline',
                  _agriScore!['voucherDisciplinePts'] ?? 0, 150),
              _buildScoreBar('Yield Accuracy',
                  _agriScore!['yieldAccuracyPts'] ?? 0, 200),
              _buildScoreBar('Contract Fulfillment',
                  _agriScore!['contractFulfillmentPts'] ?? 0, 200),
              _buildScoreBar('Repayment Completion',
                  _agriScore!['repaymentCompletionPts'] ?? 0, 200),
              _buildScoreBar('Season Completion',
                  _agriScore!['seasonCompletionPts'] ?? 0, 100),
              _buildScoreBar('Agronomist Assessment',
                  _agriScore!['agronomistAssessmentPts'] ?? 0, 50),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildScoreBar(String label, int value, int max) {
    final pct = max > 0 ? (value / max).clamp(0.0, 1.0) : 0.0;
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 6),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Text(label, style: const TextStyle(fontSize: 13)),
              Text('$value / $max',
                  style: const TextStyle(
                      fontSize: 13, fontWeight: FontWeight.bold)),
            ],
          ),
          const SizedBox(height: 4),
          LinearProgressIndicator(
            value: pct,
            backgroundColor: Colors.grey.shade200,
            color: Colors.green,
            minHeight: 8,
            borderRadius: BorderRadius.circular(4),
          ),
        ],
      ),
    );
  }

  Widget _buildDetailRow(String label, String value) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 6),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          SizedBox(
            width: 130,
            child: Text(
              label,
              style: TextStyle(
                color: Colors.grey.shade600,
                fontWeight: FontWeight.w500,
              ),
            ),
          ),
          Expanded(
            child: Text(value,
                style: const TextStyle(fontWeight: FontWeight.bold)),
          ),
        ],
      ),
    );
  }
}

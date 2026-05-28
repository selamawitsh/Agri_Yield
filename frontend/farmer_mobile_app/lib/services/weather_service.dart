import '../models/weather_model.dart';
import 'api_service.dart';

class WeatherService {
  final _api = ApiService();

  // WS-01: 7-day forecast
  Future<List<WeatherReading>> getForecast(String farmId, {int days = 7}) async {
    try {
      final response = await _api.get('/weather/forecast/$farmId?days=$days');
      if (response['success'] == true) {
        return (response['data'] as List)
            .map((e) => WeatherReading.fromJson(e))
            .toList();
      }
      return [];
    } catch (_) { return []; }
  }

  // WS-02: Rainfall data
  Future<List<WeatherReading>> getRainfall(String farmId) async {
    try {
      final response = await _api.get('/weather/rainfall/$farmId');
      if (response['success'] == true) {
        return (response['data'] as List)
            .map((e) => WeatherReading.fromJson(e))
            .toList();
      }
      return [];
    } catch (_) { return []; }
  }

  // WS-03: Drought status
  Future<DroughtStatus?> getDroughtStatus(String farmId) async {
    try {
      final response = await _api.get('/weather/drought/$farmId');
      if (response['success'] == true) {
        return DroughtStatus.fromJson(response['data']);
      }
      return null;
    } catch (_) { return null; }
  }

  // WS-05: Current weather
  Future<WeatherReading?> getCurrentWeather(String farmId) async {
    try {
      final response = await _api.get('/weather/current/$farmId');
      if (response['success'] == true) {
        return WeatherReading.fromJson(response['data']);
      }
      return null;
    } catch (_) { return null; }
  }

  // WS-07: Risk score
  Future<WeatherRisk?> getWeatherRisk(String farmId) async {
    try {
      final response = await _api.get('/weather/risk/$farmId');
      if (response['success'] == true) {
        return WeatherRisk.fromJson(response['data']);
      }
      return null;
    } catch (_) { return null; }
  }

  // WS-08: Historical weather
  Future<List<WeatherReading>> getHistory(String farmId) async {
    try {
      final response = await _api.get('/weather/history/$farmId');
      if (response['success'] == true) {
        return (response['data'] as List)
            .map((e) => WeatherReading.fromJson(e))
            .toList();
      }
      return [];
    } catch (_) { return []; }
  }

  // WS-09: Weather alerts
  Future<List<WeatherAlert>> getAlerts(String farmId) async {
    try {
      final response = await _api.get('/weather/alerts/$farmId');
      if (response['success'] == true) {
        return (response['data'] as List)
            .map((e) => WeatherAlert.fromJson(e))
            .toList();
      }
      return [];
    } catch (_) { return []; }
  }
}

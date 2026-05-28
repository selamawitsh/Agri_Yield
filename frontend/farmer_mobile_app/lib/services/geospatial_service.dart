import 'api_service.dart';
import '../models/farm_map_model.dart';
import '../models/harvest_readiness_model.dart';
import '../models/ndvi_reading_model.dart';
import '../models/yield_prediction_model.dart';

/// Geospatial analytics via API gateway → geospatial-service.
class GeospatialService {
  final ApiService _api = ApiService();

  static const String _base = '/geospatial';

  Future<NdviReadingModel?> getLatestNdvi(String farmId) async {
    try {
      final response = await _api.get('$_base/farms/$farmId/ndvi');
      if (response['success'] == true && response['data'] != null) {
        return NdviReadingModel.fromJson(
            Map<String, dynamic>.from(response['data'] as Map));
      }
      return null;
    } catch (_) {
      return null;
    }
  }

  Future<List<NdviReadingModel>> getNdviHistory(String farmId,
      {int days = 90}) async {
    try {
      final response =
          await _api.get('$_base/ndvi-history/$farmId?days=$days');
      if (response['success'] == true && response['data'] != null) {
        final list = response['data'] as List;
        return list
            .map((e) => NdviReadingModel.fromJson(
                Map<String, dynamic>.from(e as Map)))
            .toList();
      }
      return [];
    } catch (_) {
      return [];
    }
  }

  Future<YieldPredictionModel?> getYieldPrediction(String farmId) async {
    try {
      final response = await _api.get('$_base/farms/$farmId/yield');
      if (response['success'] == true && response['data'] != null) {
        return YieldPredictionModel.fromJson(
            Map<String, dynamic>.from(response['data'] as Map));
      }
      return null;
    } catch (_) {
      return null;
    }
  }

  Future<HarvestReadinessModel?> getHarvestReadiness(String farmId) async {
    try {
      final response =
          await _api.get('$_base/farms/$farmId/harvest-readiness');
      if (response['success'] == true && response['data'] != null) {
        return HarvestReadinessModel.fromJson(
            Map<String, dynamic>.from(response['data'] as Map));
      }
      return null;
    } catch (_) {
      return null;
    }
  }

  Future<FarmMapModel?> getFarmMap(String farmId) async {
    try {
      final response = await _api.get('$_base/farm-map/$farmId');
      if (response['success'] == true && response['data'] != null) {
        return FarmMapModel.fromJson(
            Map<String, dynamic>.from(response['data'] as Map));
      }
      return null;
    } catch (_) {
      return null;
    }
  }
}

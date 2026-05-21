import 'api_service.dart';
import '../models/farm_model.dart';
import '../models/crop_cycle_model.dart';
import '../models/input_need_model.dart';

class FarmService {
  final ApiService _api = ApiService();

  // SRS Page 22 — POST /api/v1/farms
  Future<Map<String, dynamic>> registerFarm({
    required String cropType,
    required String kebeleCode,
    required String region,
    required String expectedHarvestDate,
    required String geoJsonPolygon,
    String? farmName,
  }) async {
    try {
      final response = await _api.post('/farms', {
        if (farmName != null) 'farmName': farmName,
        'cropType': cropType,
        'kebeleCode': kebeleCode,
        'region': region,
        'expectedHarvestDate': expectedHarvestDate,
        'geoJsonPolygon': geoJsonPolygon,
      });

      if (response['success'] == true) {
        return {
          'success': true,
          'farm': FarmModel.fromJson(response['data']),
        };
      }
      return {
        'success': false,
        'message': response['message'] ?? 'Registration failed',
      };
    } catch (e) {
      return {'success': false, 'message': e.toString()};
    }
  }

  // SRS Page 22 — GET /api/v1/farms/my
  Future<Map<String, dynamic>> getMyFarms() async {
    try {
      final response = await _api.get('/farms/my');
      if (response['success'] == true) {
        final List<dynamic> data = response['data'] ?? [];
        return {
          'success': true,
          'farms': data.map((e) => FarmModel.fromJson(e)).toList(),
        };
      }
      return {
        'success': false,
        'message': response['message'] ?? 'Failed to load farms',
      };
    } catch (e) {
      return {'success': false, 'message': e.toString()};
    }
  }

  // SRS Page 22 — GET /api/v1/farms/{farm_id}
  Future<Map<String, dynamic>> getFarmById(String farmId) async {
    try {
      final response = await _api.get('/farms/$farmId');
      if (response['success'] == true) {
        return {
          'success': true,
          'farm': FarmModel.fromJson(response['data']),
        };
      }
      return {
        'success': false,
        'message': response['message'] ?? 'Farm not found',
      };
    } catch (e) {
      return {'success': false, 'message': e.toString()};
    }
  }

  // SRS Page 22 — GET /api/v1/farms/{farm_id}/digital-twin
  Future<Map<String, dynamic>> getDigitalTwin(String farmId) async {
    try {
      final response = await _api.get('/farms/$farmId/digital-twin');
      if (response['success'] == true) {
        return {
          'success': true,
          'digitalTwin': response['data'],
        };
      }
      return {
        'success': false,
        'message': response['message'] ?? 'Digital twin not found',
      };
    } catch (e) {
      return {'success': false, 'message': e.toString()};
    }
  }

  // SRS Page 22 — POST /api/v1/farms/{farm_id}/confirm-planting
  Future<Map<String, dynamic>> confirmPlanting({
    required String farmId,
    required String plantingDate,
  }) async {
    try {
      final response = await _api.post(
        '/farms/$farmId/confirm-planting',
        {'plantingDate': plantingDate},
      );
      if (response['success'] == true) {
        return {
          'success': true,
          'cropCycle': CropCycleModel.fromJson(response['data']),
        };
      }
      return {
        'success': false,
        'message': response['message'] ?? 'Failed to confirm planting',
      };
    } catch (e) {
      return {'success': false, 'message': e.toString()};
    }
  }

  // SRS Page 22 — POST /api/v1/farms/{farm_id}/input-needs
  Future<Map<String, dynamic>> submitInputNeeds({
    required String farmId,
    required String cropCycleId,
    required List<Map<String, dynamic>> items,
  }) async {
    try {
      final response = await _api.post('/farms/$farmId/input-needs', {
        'cropCycleId': cropCycleId,
        'items': items,
      });
      if (response['success'] == true) {
        return {
          'success': true,
          'inputNeed': InputNeedModel.fromJson(response['data']),
        };
      }
      return {
        'success': false,
        'message': response['message'] ?? 'Failed to submit input needs',
      };
    } catch (e) {
      return {'success': false, 'message': e.toString()};
    }
  }

  // SRS Page 22 — POST /api/v1/farms/{farm_id}/photos
  Future<Map<String, dynamic>> uploadPhoto({
    required String farmId,
    required String photoType,
    required String filePath,
  }) async {
    try {
      final response = await _api.postMultipart(
        '/farms/$farmId/photos',
        {'photo_type': photoType},
        filePath,
        'photo',
      );
      if (response['success'] == true) {
        return {'success': true, 'photo': response['data']};
      }
      return {
        'success': false,
        'message': response['message'] ?? 'Photo upload failed',
      };
    } catch (e) {
      return {'success': false, 'message': e.toString()};
    }
  }

  // SRS Page 22 — GET /api/v1/farms/{farm_id}/agri-score
  Future<Map<String, dynamic>> getAgriScore(String farmId) async {
    try {
      final response = await _api.get('/farms/$farmId/agri-score');
      if (response['success'] == true) {
        return {'success': true, 'agriScore': response['data']};
      }
      return {
        'success': false,
        'message': response['message'] ?? 'Agri-score not found',
      };
    } catch (e) {
      return {'success': false, 'message': e.toString()};
    }
  }
}

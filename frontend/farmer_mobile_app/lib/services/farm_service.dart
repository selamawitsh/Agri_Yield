import 'package:dio/dio.dart';
import 'api_service.dart';
import '../models/farm_model.dart';
import '../models/crop_cycle_model.dart';
import '../models/input_need_model.dart';

class FarmService {
  final ApiService _api = ApiService();

  // FS-01
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
        return {'success': true, 'farm': FarmModel.fromJson(response['data'])};
      }
      return {'success': false, 'message': response['message'] ?? 'Failed'};
    } on DioException catch (e) {
      return {
        'success': false,
        'message': _extractErrorMessage(e),
      };
    } catch (e) {
      return {'success': false, 'message': e.toString()};
    }
  }

  String _extractErrorMessage(DioException e) {
    final data = e.response?.data;
    if (data is Map) {
      final msg = data['message']?.toString();
      if (msg != null && msg.isNotEmpty) return msg;
    }
    return e.message ?? 'Request failed';
  }

  // FS-03
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
      return {'success': false, 'message': response['message'] ?? 'Failed'};
    } catch (e) {
      return {'success': false, 'message': e.toString()};
    }
  }

  // FS-02
  Future<Map<String, dynamic>> getFarmById(String farmId) async {
    try {
      final response = await _api.get('/farms/$farmId');
      if (response['success'] == true) {
        return {'success': true, 'farm': FarmModel.fromJson(response['data'])};
      }
      return {'success': false, 'message': response['message'] ?? 'Failed'};
    } catch (e) {
      return {'success': false, 'message': e.toString()};
    }
  }

  // FS-08
  Future<Map<String, dynamic>> getDigitalTwin(String farmId) async {
    try {
      final response = await _api.get('/farms/$farmId/digital-twin');
      if (response['success'] == true) {
        return {'success': true, 'digitalTwin': response['data']};
      }
      return {'success': false, 'message': response['message'] ?? 'Failed'};
    } catch (e) {
      return {'success': false, 'message': e.toString()};
    }
  }

  // View input needs
  Future<Map<String, dynamic>> getInputNeeds(String farmId) async {
    try {
      final response = await _api.get('/farms/$farmId/input-needs');
      if (response['success'] == true) {
        final List<dynamic> data = response['data'] ?? [];
        return {
          'success': true,
          'inputNeeds': data.map((e) => InputNeedModel.fromJson(e)).toList(),
        };
      }
      return {'success': false, 'message': response['message'] ?? 'Failed'};
    } catch (e) {
      return {'success': false, 'message': e.toString()};
    }
  }

  // FS-07
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
      return {'success': false, 'message': response['message'] ?? 'Failed'};
    } catch (e) {
      return {'success': false, 'message': e.toString()};
    }
  }

  // FS-06
  Future<Map<String, dynamic>> submitInputNeeds({
    required String farmId,
    required List<Map<String, dynamic>> items,
  }) async {
    try {
      final response = await _api.post(
        '/farms/$farmId/input-needs',
        {'items': items},
      );
      if (response['success'] == true) {
        return {
          'success': true,
          'inputNeed': InputNeedModel.fromJson(response['data']),
        };
      }
      return {'success': false, 'message': response['message'] ?? 'Failed'};
    } catch (e) {
      return {'success': false, 'message': e.toString()};
    }
  }

  // FS-04
  Future<Map<String, dynamic>> uploadPhoto({
    required String farmId,
    required String photoType,
    required String filePath,
  }) async {
    try {
      final response = await _api.postMultipart(
        '/farms/$farmId/photos',
        {'photo_type': photoType},
        'photo',
        filePath,
      );
      if (response['success'] == true) {
        return {'success': true, 'photo': response['data']};
      }
      return {'success': false, 'message': response['message'] ?? 'Failed'};
    } catch (e) {
      return {'success': false, 'message': e.toString()};
    }
  }

  // FS-09
  Future<Map<String, dynamic>> getAgriScore(String farmId) async {
    try {
      final response = await _api.get('/farms/$farmId/agri-score');
      if (response['success'] == true) {
        return {'success': true, 'agriScore': response['data']};
      }
      return {'success': false, 'message': response['message'] ?? 'Failed'};
    } catch (e) {
      return {'success': false, 'message': e.toString()};
    }
  }

  // FS-05 — get all crop cycles
  Future<Map<String, dynamic>> getCropCycles(String farmId) async {
    try {
      final response = await _api.get('/farms/$farmId/crop-cycles');
      if (response['success'] == true) {
        final List<dynamic> data = response['data'] ?? [];
        return {
          'success': true,
          'cropCycles': data.map((e) => CropCycleModel.fromJson(e)).toList(),
        };
      }
      return {'success': false, 'message': response['message'] ?? 'Failed'};
    } catch (e) {
      return {'success': false, 'message': e.toString()};
    }
  }

  // FS-05 — create new crop cycle
  Future<Map<String, dynamic>> createCropCycle({
    required String farmId,
    required String expectedHarvestDate,
    String? seasonName,
  }) async {
    try {
      final response = await _api.post('/farms/$farmId/crop-cycles', {
        'expectedHarvestDate': expectedHarvestDate,
        if (seasonName != null) 'seasonName': seasonName,
      });
      if (response['success'] == true) {
        return {
          'success': true,
          'cropCycle': CropCycleModel.fromJson(response['data']),
        };
      }
      return {'success': false, 'message': response['message'] ?? 'Failed'};
    } catch (e) {
      return {'success': false, 'message': e.toString()};
    }
  }

  // FS-11 — search farms
  Future<Map<String, dynamic>> searchFarms({
    String? region,
    String? cropType,
    String? status,
  }) async {
    try {
      final params = <String>[];
      if (region != null && region.isNotEmpty) {
        params.add('region=$region');
      }
      if (cropType != null && cropType.isNotEmpty) {
        params.add('cropType=$cropType');
      }
      if (status != null && status.isNotEmpty) {
        params.add('status=$status');
      }
      final query = params.isNotEmpty ? '?${params.join('&')}' : '';
      final response = await _api.get('/farms/search$query');
      if (response['success'] == true) {
        final List<dynamic> data = response['data'] ?? [];
        return {
          'success': true,
          'farms': data.map((e) => FarmModel.fromJson(e)).toList(),
        };
      }
      return {'success': false, 'message': response['message'] ?? 'Failed'};
    } catch (e) {
      return {'success': false, 'message': e.toString()};
    }
  }
}

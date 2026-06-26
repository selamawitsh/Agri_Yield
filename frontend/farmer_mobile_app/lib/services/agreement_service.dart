import 'api_service.dart';
import '../models/agreement_model.dart';
import '../models/dispatch_model.dart';

class AgreementService {
  final _api = ApiService();

  Future<Map<String, dynamic>> getAgreement(String agreementId) async {
    try {
      final response = await _api.get('/agreements/$agreementId');
      if (response['success'] == true) {
        return {
          'success': true,
          'agreement': AgreementModel.fromJson(response['data'] as Map<String, dynamic>),
        };
      }
      return {'success': false, 'message': response['message'] ?? 'Failed'};
    } catch (e) {
      return {'success': false, 'message': e.toString()};
    }
  }

  Future<Map<String, dynamic>> signAgreement(String agreementId) async {
    try {
      final response = await _api.post('/agreements/$agreementId/sign', {});
      if (response['success'] == true) {
        return {
          'success': true,
          'agreement': AgreementModel.fromJson(response['data'] as Map<String, dynamic>),
        };
      }
      return {'success': false, 'message': response['message'] ?? 'Failed'};
    } catch (e) {
      return {'success': false, 'message': e.toString()};
    }
  }

  Future<Map<String, dynamic>> getDispatches(String agreementId) async {
    try {
      final response = await _api.get('/offtaker/dispatches/$agreementId');
      if (response['success'] == true) {
        final List<dynamic> data = response['data'] ?? [];
        return {
          'success': true,
          'dispatches': data
              .map((e) => DispatchModel.fromJson(e as Map<String, dynamic>))
              .toList(),
        };
      }
      return {'success': false, 'message': response['message'] ?? 'Failed'};
    } catch (e) {
      return {'success': false, 'message': e.toString()};
    }
  }

  Future<Map<String, dynamic>> confirmArrival(String dispatchId) async {
    try {
      final response =
          await _api.post('/farmer/dispatches/$dispatchId/confirm-arrival', {});
      if (response['success'] == true) {
        return {
          'success': true,
          'dispatch': DispatchModel.fromJson(response['data'] as Map<String, dynamic>),
        };
      }
      return {'success': false, 'message': response['message'] ?? 'Failed'};
    } catch (e) {
      return {'success': false, 'message': e.toString()};
    }
  }

  Future<Map<String, dynamic>> confirmLoading(String dispatchId) async {
    try {
      final response =
          await _api.post('/farmer/dispatches/$dispatchId/confirm-loading', {});
      if (response['success'] == true) {
        return {
          'success': true,
          'dispatch': DispatchModel.fromJson(response['data'] as Map<String, dynamic>),
        };
      }
      return {'success': false, 'message': response['message'] ?? 'Failed'};
    } catch (e) {
      return {'success': false, 'message': e.toString()};
    }
  }
}

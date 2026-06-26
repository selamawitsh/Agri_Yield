import 'api_service.dart';
import '../models/bid_model.dart';

class BidService {
  final _api = ApiService();

  Future<Map<String, dynamic>> getBidsForMyFarm(String farmId) async {
    try {
      final response = await _api.get('/offtaker/bids/farm/$farmId');
      if (response['success'] == true) {
        final List<dynamic> data = response['data'] ?? [];
        return {
          'success': true,
          'bids': data.map((e) => BidModel.fromJson(e as Map<String, dynamic>)).toList(),
        };
      }
      return {'success': false, 'message': response['message'] ?? 'Failed'};
    } catch (e) {
      return {'success': false, 'message': e.toString()};
    }
  }

  Future<Map<String, dynamic>> acceptBid(String bidId) async {
    try {
      final response = await _api.post('/farmer/bids/$bidId/accept', {});
      if (response['success'] == true) {
        return {'success': true, 'bid': BidModel.fromJson(response['data'])};
      }
      return {'success': false, 'message': response['message'] ?? 'Failed'};
    } catch (e) {
      return {'success': false, 'message': e.toString()};
    }
  }

  Future<Map<String, dynamic>> rejectBid(String bidId) async {
    try {
      final response = await _api.post('/farmer/bids/$bidId/reject', {});
      if (response['success'] == true) {
        return {'success': true, 'bid': BidModel.fromJson(response['data'])};
      }
      return {'success': false, 'message': response['message'] ?? 'Failed'};
    } catch (e) {
      return {'success': false, 'message': e.toString()};
    }
  }
}

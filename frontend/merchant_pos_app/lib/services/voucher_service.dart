import 'api_service.dart';

class VoucherService {
  final _api = ApiService();

  /// MS-06 / MS-07: Validate and redeem a voucher QR payload
  /// The actual six-check validation runs in voucher-service backend.
  /// Merchant app just sends the raw QR string to the gateway.
  Future<Map<String, dynamic>> validateAndRedeem(String qrPayload) async {
    try {
      final response = await _api.post('/vouchers/validate', {
        'qr_payload': qrPayload,
      });
      return {
        'success': response['success'] ?? false,
        'message': response['message'] ?? '',
        'data': response['data'],
      };
    } catch (e) {
      return {'success': false, 'message': e.toString()};
    }
  }

  /// MS-07: Verify voucher authenticity only (no redemption)
  Future<Map<String, dynamic>> verifyVoucher(String qrPayload) async {
    try {
      final response = await _api.post('/merchant/verify-voucher', {
        'qr_payload': qrPayload,
      });
      return {
        'success': response['success'] ?? false,
        'message': response['message'] ?? '',
        'data': response['data'],
      };
    } catch (e) {
      return {'success': false, 'message': e.toString()};
    }
  }
}

import 'api_service.dart';
import '../models/voucher_model.dart';
 
class VoucherService {
  final _api = ApiService();
 
  // VS-04: Validate and redeem — sends raw QR payload or alphanumeric code.
  // Backend runs all 6 checks (signature, duplicate, category, GPS, expiry, sequence).
  Future<VoucherRedemptionResult> validateAndRedeem(String qrPayload) async {
    try {
      final response = await _api.post('/vouchers/redeem', {
        'qr_payload': qrPayload,
      });
      return VoucherRedemptionResult.fromJson(response);
    } catch (e) {
      final msg = _parseError(e);
      return VoucherRedemptionResult.error(msg);
    }
  }
 
  // VS-03: Get single voucher details by ID (merchant view)
  Future<Map<String, dynamic>?> getVoucherDetails(String voucherId) async {
    try {
      final response = await _api.get('/vouchers/$voucherId');
      return response['data'];
    } catch (_) {
      return null;
    }
  }
 
  // VS-10: Merchant redemption history — GET /api/v1/merchant/redemptions
  Future<List<MerchantRedemptionSummary>> getRedemptionHistory({
    String? dateFrom,
    String? dateTo,
    String? status,
    int page = 0,
    int size = 20,
  }) async {
    try {
      final params = <String, dynamic>{
        'page': page,
        'size': size,
        if (dateFrom != null) 'dateFrom': dateFrom,
        if (dateTo != null) 'dateTo': dateTo,
        if (status != null) 'status': status,
      };
      final response = await _api.get(
        '/merchant/redemptions',
        queryParameters: params,
      );
      final List data = response['data'] ?? [];
      return data.map((e) => MerchantRedemptionSummary.fromJson(e)).toList();
    } catch (_) {
      return [];
    }
  }
 
  // Offline-safe: verify signature only without redemption (VS-05)
  Future<Map<String, dynamic>> verifySignatureOnly(String qrPayload) async {
    try {
      final response = await _api.post('/vouchers/verify', {
        'qr_payload': qrPayload,
      });
      return {
        'valid': response['success'] ?? false,
        'message': response['message'] ?? '',
      };
    } catch (e) {
      return {'valid': false, 'message': _parseError(e)};
    }
  }
 
  String _parseError(dynamic e) {
    final str = e.toString();
    if (str.contains('DUPLICATE_SCAN'))       return 'DUPLICATE_SCAN';
    if (str.contains('INVALID_SIGNATURE'))    return 'INVALID_SIGNATURE';
    if (str.contains('CATEGORY_MISMATCH'))    return 'CATEGORY_MISMATCH';
    if (str.contains('MERCHANT_TOO_FAR'))     return 'MERCHANT_TOO_FAR';
    if (str.contains('VOUCHER_EXPIRED'))      return 'VOUCHER_EXPIRED';
    if (str.contains('PRECEDING_VOUCHER'))    return 'PRECEDING_VOUCHER_NOT_REDEEMED';
    if (str.contains('SocketException') ||
        str.contains('Connection refused'))   return 'NETWORK_ERROR';
    return 'UNKNOWN_ERROR';
  }
}

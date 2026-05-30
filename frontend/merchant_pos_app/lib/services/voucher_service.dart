// lib/services/voucher_service.dart
import 'api_service.dart';
import '../models/voucher_model.dart';

class VoucherService {
  final _api = ApiService();

  /// VS-04: Validate and redeem a voucher.
  /// Backend endpoint: POST /api/v1/vouchers/redeem
  /// Backend request:  RedeemVoucherRequest { voucherCode, notes }
  ///
  /// qrPayload can be either:
  ///   - The full voucher code from QR scan (e.g. "AGY-9E3F-9F19-9367")
  ///   - The alphanumeric code typed manually
  Future<VoucherRedemptionResult> validateAndRedeem(
      String qrPayload, {
        String? notes,
      }) async {
    try {
      // FIX: backend expects "voucherCode" not "qr_payload"
      final response = await _api.post('/vouchers/redeem', {
        'voucherCode': qrPayload.trim(),
        if (notes != null) 'notes': notes,
      });
      return VoucherRedemptionResult.fromJson(response);
    } catch (e) {
      final msg = _parseError(e);
      return VoucherRedemptionResult.error(msg);
    }
  }

  /// VS-03: Get single voucher details by code (merchant can look up before redeeming)
  /// Backend endpoint: GET /api/v1/vouchers/code/{voucherCode}
  Future<Map<String, dynamic>?> getVoucherByCode(String voucherCode) async {
    try {
      final response = await _api.get('/vouchers/code/$voucherCode');
      return response['data'] as Map<String, dynamic>?;
    } catch (_) {
      return null;
    }
  }

  /// VS-03: Get single voucher details by ID
  /// Backend endpoint: GET /api/v1/vouchers/{voucherId}
  Future<Map<String, dynamic>?> getVoucherDetails(String voucherId) async {
    try {
      final response = await _api.get('/vouchers/$voucherId');
      return response['data'] as Map<String, dynamic>?;
    } catch (_) {
      return null;
    }
  }

  /// VS-10: Merchant redemption history
  /// Backend endpoint: GET /api/v1/vouchers/{voucherId}/redemptions
  /// Note: full merchant redemption history endpoint may not be built yet —
  /// falls back gracefully to empty list.
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
        if (dateTo != null)   'dateTo':   dateTo,
        if (status  != null)  'status':   status,
      };
      final response = await _api.get(
        '/merchant/redemptions',
        queryParameters: params,
      );
      final List data = response['data'] ?? [];
      return data
          .map((e) => MerchantRedemptionSummary.fromJson(e))
          .toList();
    } catch (_) {
      // Endpoint may not be implemented yet — return empty gracefully
      return [];
    }
  }

  /// Parse Dio errors into readable rejection reason strings
  String _parseError(dynamic e) {
    final str = e.toString();
    if (str.contains('DUPLICATE_SCAN'))           return 'DUPLICATE_SCAN';
    if (str.contains('INVALID_SIGNATURE'))         return 'INVALID_SIGNATURE';
    if (str.contains('CATEGORY_MISMATCH'))         return 'CATEGORY_MISMATCH';
    if (str.contains('MERCHANT_TOO_FAR'))          return 'MERCHANT_TOO_FAR';
    if (str.contains('VOUCHER_EXPIRED'))           return 'VOUCHER_EXPIRED';
    if (str.contains('PRECEDING_VOUCHER'))         return 'PRECEDING_VOUCHER_NOT_REDEEMED';
    if (str.contains('INVALID_VOUCHER'))           return 'INVALID_VOUCHER';
    if (str.contains('INVESTMENT_NOT_FUNDED'))     return 'INVESTMENT_NOT_FUNDED';
    if (str.contains('SocketException') ||
        str.contains('Connection refused') ||
        str.contains('Network is unreachable'))    return 'NETWORK_ERROR';
    if (str.contains('400'))                       return 'BAD_REQUEST';
    if (str.contains('401'))                       return 'UNAUTHORIZED';
    if (str.contains('404'))                       return 'VOUCHER_NOT_FOUND';
    if (str.contains('503'))                       return 'SERVICE_UNAVAILABLE';
    return 'UNKNOWN_ERROR';
  }
}
// lib/services/voucher_service.dart
import 'package:dio/dio.dart';
import 'package:flutter/foundation.dart';
import 'api_service.dart';
import '../models/voucher_model.dart';

class VoucherService {
  final _api = ApiService();

  Future<VoucherRedemptionResult> validateAndRedeem(
    String qrPayload, {
    String? notes,
  }) async {
    try {
      final response = await _api.post('/vouchers/redeem', {
        'voucherCode': qrPayload.trim(),
        if (notes != null) 'notes': notes,
      });
      debugPrint('=== validateAndRedeem RESPONSE ===');
      debugPrint('success: ${response['success']}');
      debugPrint('message: ${response['message']}');
      debugPrint('data:    ${response['data']}');
      debugPrint('==================================');
      return VoucherRedemptionResult.fromJson(response as Map<String, dynamic>);
    } on DioException catch (e, stack) {
      debugPrint('❌ validateAndRedeem DioException:');
      debugPrint('   status: ${e.response?.statusCode}');
      debugPrint('   data:   ${e.response?.data}');
      debugPrint('$stack');
      return _resultFromDioException(e);
    } catch (e, stack) {
      debugPrint('❌ validateAndRedeem unexpected: $e\n$stack');
      return VoucherRedemptionResult.error('UNKNOWN_ERROR');
    }
  }

  Future<Map<String, dynamic>?> getVoucherByCode(String voucherCode) async {
    try {
      final response = await _api.get('/vouchers/code/$voucherCode');
      return response['data'] as Map<String, dynamic>?;
    } on DioException catch (e) {
      debugPrint('⚠ getVoucherByCode: ${e.response?.statusCode}');
      return null;
    } catch (e) {
      debugPrint('⚠ getVoucherByCode: $e');
      return null;
    }
  }

  Future<Map<String, dynamic>?> getVoucherDetails(String voucherId) async {
    try {
      final response = await _api.get('/vouchers/$voucherId');
      return response['data'] as Map<String, dynamic>?;
    } catch (e) {
      debugPrint('⚠ getVoucherDetails: $e');
      return null;
    }
  }

  // FIXED: calls /vouchers/merchant/my — returns all vouchers redeemed
  // by this merchant, mapped to MerchantRedemptionSummary for history screen.
  Future<List<MerchantRedemptionSummary>> getRedemptionHistory({
    String? dateFrom,
    String? dateTo,
    String? status,
    int page = 0,
    int size = 20,
  }) async {
    try {
      final response = await _api.get('/vouchers/merchant/my');

      debugPrint('=== getRedemptionHistory RESPONSE ===');
      debugPrint('success: ${response['success']}');
      debugPrint('count:   ${(response['data'] as List?)?.length ?? 0}');
      debugPrint('=====================================');

      if (response['success'] == true) {
        final List<dynamic> data = response['data'] ?? [];

        final summaries = <MerchantRedemptionSummary>[];
        for (final v in data) {
          // Only show REDEEMED vouchers in history
          if (v['status'] != 'REDEEMED') continue;

          final redeemedAt = v['redeemedAt']?.toString() ?? '';

          // Client-side date filter
          if (dateFrom != null && redeemedAt.isNotEmpty) {
            try {
              if (DateTime.parse(redeemedAt)
                  .isBefore(DateTime.parse(dateFrom))) continue;
            } catch (_) {}
          }
          if (dateTo != null && redeemedAt.isNotEmpty) {
            try {
              if (DateTime.parse(redeemedAt)
                  .isAfter(DateTime.parse('${dateTo}T23:59:59'))) continue;
            } catch (_) {}
          }

          summaries.add(MerchantRedemptionSummary(
            voucherId:          v['id']?.toString() ?? '',
            farmerName:         'Farmer',
            productCategory:    v['productCategory']?.toString() ?? 'OTHER',
            productDescription: v['productName']?.toString()
                                ?? 'Agricultural Input',
            amountEtb:          (v['amountEtb'] as num?)?.toDouble() ?? 0,
            redeemedAt:         redeemedAt,
            paymentReference:   v['id']?.toString() ?? '',
            status:             'COMPLETED',
          ));
        }

        debugPrint('✅ Mapped ${summaries.length} redeemed vouchers');
        return summaries;
      }
      return [];
    } catch (e, stack) {
      debugPrint('❌ getRedemptionHistory error: $e\n$stack');
      return [];
    }
  }

  VoucherRedemptionResult _resultFromDioException(DioException e) {
    final body       = e.response?.data;
    final statusCode = e.response?.statusCode ?? 0;

    if (body is Map<String, dynamic>) {
      final errorCode = body['error_code']?.toString()
                     ?? body['errorCode']?.toString()
                     ?? body['message']?.toString()
                     ?? '';
      final message = body['message']?.toString() ?? errorCode;
      final reason  = _mapToRejectionReason(errorCode);
      debugPrint('   error_code="$errorCode" → reason="$reason"');
      return VoucherRedemptionResult(
        success:        false,
        message:        message,
        rejectionReason: reason,
        fraudSeverity:  body['fraudSeverity']?.toString()
                     ?? body['fraud_severity']?.toString(),
      );
    }
    return VoucherRedemptionResult.error(_mapStatusCode(statusCode, e));
  }

  String _mapToRejectionReason(String raw) {
    final s = raw.toUpperCase();
    if (s.contains('DUPLICATE'))                     return 'DUPLICATE_SCAN';
    if (s.contains('INVALID_SIGNATURE'))              return 'INVALID_SIGNATURE';
    if (s.contains('CATEGORY_MISMATCH'))              return 'CATEGORY_MISMATCH';
    if (s.contains('TOO_FAR') || s.contains('GPS'))  return 'MERCHANT_TOO_FAR';
    if (s.contains('EXPIRED'))                        return 'VOUCHER_EXPIRED';
    if (s.contains('PRECEDING') ||
        s.contains('SEQUENCE'))                       return 'PRECEDING_VOUCHER_NOT_REDEEMED';
    if (s.contains('REDEEMED'))                       return 'DUPLICATE_SCAN';
    if (s.contains('INVALID_VOUCHER') ||
        s.contains('NOT_VALID'))                      return 'INVALID_VOUCHER';
    if (s.contains('NOT_FOUND'))                      return 'VOUCHER_NOT_FOUND';
    if (s.contains('UNAUTHORIZED') ||
        s.contains('FORBIDDEN'))                      return 'UNAUTHORIZED';
    if (s.isNotEmpty)                                 return raw;
    return 'UNKNOWN_ERROR';
  }

  String _mapStatusCode(int code, DioException e) {
    if (e.type == DioExceptionType.connectionTimeout ||
        e.type == DioExceptionType.receiveTimeout    ||
        e.type == DioExceptionType.sendTimeout       ||
        e.type == DioExceptionType.connectionError)   return 'NETWORK_ERROR';
    switch (code) {
      case 400: return 'INVALID_VOUCHER';
      case 401: return 'UNAUTHORIZED';
      case 403: return 'UNAUTHORIZED';
      case 404: return 'VOUCHER_NOT_FOUND';
      case 409: return 'DUPLICATE_SCAN';
      case 503: return 'NETWORK_ERROR';
      default:  return 'UNKNOWN_ERROR';
    }
  }
}

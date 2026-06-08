import 'dart:convert';
import 'package:flutter/foundation.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'api_service.dart';
import '../models/voucher_model.dart';

class VoucherService {
  final ApiService _api = ApiService();

  static const String _cachePrefix = 'vouchers_cache_';

  // ── VS-02: Get all vouchers for authenticated farmer ─────────────────────
  Future<List<VoucherModel>> getMyVouchers() async {
    try {
      final response = await _api.get('/vouchers/my');

      // ── DIAGNOSTIC: print the raw response so you can see exactly
      //    what the backend returned vs what the model expects.
      debugPrint('=== VoucherService.getMyVouchers() RAW RESPONSE ===');
      debugPrint('success: ${response['success']}');
      debugPrint('data type: ${response['data']?.runtimeType}');
      debugPrint('data: ${response['data']}');
      debugPrint('====================================================');

      if (response['success'] == true) {
        final List<dynamic> data = response['data'] ?? [];
        final vouchers = <VoucherModel>[];

        // Parse each voucher individually so one bad record doesn't
        // wipe out the whole list.
        for (int i = 0; i < data.length; i++) {
          try {
            vouchers.add(VoucherModel.fromJson(data[i] as Map<String, dynamic>));
          } catch (e, stack) {
            // FIXED: was catch(_) — now logs the exact field that failed.
            debugPrint('⚠ VoucherService: failed to parse voucher[$i]: $e');
            debugPrint('  raw item: ${data[i]}');
            debugPrint('  $stack');
          }
        }

        debugPrint('✅ Parsed ${vouchers.length}/${data.length} vouchers OK');
        await _cacheVouchers(vouchers);
        return vouchers;
      }

      // success == false — backend returned an error body
      debugPrint('⚠ VoucherService: success=false — message: ${response['message']}');
      return await _getCachedVouchers();

    } catch (e, stack) {
      // FIXED: was catch(_) — now you can see the real error in the
      // Flutter debug console instead of silently getting an empty list.
      debugPrint('❌ VoucherService.getMyVouchers() FAILED: $e');
      debugPrint('$stack');
      return await _getCachedVouchers();
    }
  }

  // ── VS-03: Get single voucher detail ─────────────────────────────────────
  Future<VoucherModel?> getVoucherDetail(String voucherId) async {
    try {
      final response = await _api.get('/vouchers/$voucherId');
      if (response['success'] == true) {
        return VoucherModel.fromJson(response['data'] as Map<String, dynamic>);
      }
      debugPrint('⚠ VoucherService.getVoucherDetail($voucherId): success=false');
      return null;
    } catch (e, stack) {
      debugPrint('❌ VoucherService.getVoucherDetail($voucherId) FAILED: $e');
      debugPrint('$stack');
      final cached = await _getCachedVouchers();
      try {
        return cached.firstWhere((v) => v.id == voucherId);
      } catch (_) {
        return null;
      }
    }
  }

  // ── Offline cache helpers (SRS §7.6) ─────────────────────────────────────
  Future<void> _cacheVouchers(List<VoucherModel> vouchers) async {
    try {
      final prefs = await SharedPreferences.getInstance();
      final json = vouchers.map((v) => v.toJson()).toList();
      await prefs.setString(_cachePrefix + 'all', jsonEncode(json));
      await prefs.setString(
          _cachePrefix + 'timestamp', DateTime.now().toIso8601String());
    } catch (e) {
      debugPrint('⚠ VoucherService: cache write failed: $e');
    }
  }

  Future<List<VoucherModel>> _getCachedVouchers() async {
    try {
      final prefs = await SharedPreferences.getInstance();
      final raw = prefs.getString(_cachePrefix + 'all');
      if (raw == null) return [];
      final List<dynamic> json = jsonDecode(raw);
      return json.map((e) => VoucherModel.fromJson(e as Map<String, dynamic>)).toList();
    } catch (e) {
      debugPrint('⚠ VoucherService: cache read failed: $e');
      return [];
    }
  }

  Future<DateTime?> getCacheTimestamp() async {
    try {
      final prefs = await SharedPreferences.getInstance();
      final ts = prefs.getString(_cachePrefix + 'timestamp');
      if (ts == null) return null;
      return DateTime.parse(ts);
    } catch (_) {
      return null;
    }
  }

  Future<bool> hasCachedVouchers() async {
    final cached = await _getCachedVouchers();
    return cached.isNotEmpty;
  }
}
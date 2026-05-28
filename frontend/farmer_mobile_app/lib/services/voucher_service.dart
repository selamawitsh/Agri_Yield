import 'dart:convert';
import 'package:shared_preferences/shared_preferences.dart';
import 'api_service.dart';
import '../models/voucher_model.dart';

class VoucherService {
  final ApiService _api = ApiService();

  // Cache key prefix — stored per farm
  static const String _cachePrefix = 'vouchers_cache_';

  // ── VS-02: Get all vouchers for authenticated farmer ─────────────────────
  Future<List<VoucherModel>> getMyVouchers() async {
    try {
      final response = await _api.get('/vouchers/my');
      if (response['success'] == true) {
        final List<dynamic> data = response['data'] ?? [];
        final vouchers = data.map((e) => VoucherModel.fromJson(e)).toList();
        // Cache for offline use (SRS §7.6)
        await _cacheVouchers(vouchers);
        return vouchers;
      }
      return await _getCachedVouchers();
    } catch (_) {
      // Offline fallback — return cached
      return await _getCachedVouchers();
    }
  }

  // ── VS-03: Get single voucher detail ─────────────────────────────────────
  Future<VoucherModel?> getVoucherDetail(String voucherId) async {
    try {
      final response = await _api.get('/vouchers/$voucherId');
      if (response['success'] == true) {
        return VoucherModel.fromJson(response['data']);
      }
      return null;
    } catch (_) {
      // Try to find in cache
      final cached = await _getCachedVouchers();
      try {
        return cached.firstWhere((v) => v.id == voucherId);
      } catch (_) {
        return null;
      }
    }
  }

  // ── Offline cache helpers (SRS §7.6 — QR codes must work offline) ────────
  Future<void> _cacheVouchers(List<VoucherModel> vouchers) async {
    try {
      final prefs = await SharedPreferences.getInstance();
      final json = vouchers.map((v) => v.toJson()).toList();
      await prefs.setString(_cachePrefix + 'all', jsonEncode(json));
      await prefs.setString(
          _cachePrefix + 'timestamp', DateTime.now().toIso8601String());
    } catch (_) {}
  }

  Future<List<VoucherModel>> _getCachedVouchers() async {
    try {
      final prefs = await SharedPreferences.getInstance();
      final raw = prefs.getString(_cachePrefix + 'all');
      if (raw == null) return [];
      final List<dynamic> json = jsonDecode(raw);
      return json.map((e) => VoucherModel.fromJson(e)).toList();
    } catch (_) {
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

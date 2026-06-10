import 'package:flutter/foundation.dart';
import 'api_service.dart';
import 'voucher_service.dart';
import '../models/merchant_profile_model.dart';
import '../models/product_model.dart';
import '../models/price_anomaly_model.dart';
import '../models/analytics_model.dart';
import '../models/voucher_model.dart';
import '../utils/constants.dart';

class MerchantService {
  final _api = ApiService();
  final _voucherService = VoucherService();

  // ── Profile ───────────────────────────────────────────────────────────────

  Future<Map<String, dynamic>> registerMerchant({
    required String businessName,
    required String businessLicenseNumber,
    required double storeGpsLat,
    required double storeGpsLng,
    required String telebirrAccount,
    String? kebeleCode,
  }) async {
    try {
      final response = await _api.post(Constants.merchantRegister, {
        'businessName':          businessName,
        'businessLicenseNumber': businessLicenseNumber,
        'storeGpsLat':           storeGpsLat,
        'storeGpsLng':           storeGpsLng,
        'telebirrAccount':       telebirrAccount,
        'kebeleCode':            kebeleCode,
      });
      return {'success': true, 'data': response['data']};
    } catch (e) {
      return {'success': false, 'message': e.toString()};
    }
  }

  Future<MerchantProfile?> getMyProfile() async {
    try {
      final response = await _api.get(Constants.merchantMe);
      if (response['success'] == true) {
        return MerchantProfile.fromJson(response['data']);
      }
      return null;
    } catch (e) {
      debugPrint('getMyProfile error: $e');
      return null;
    }
  }

  Future<Map<String, dynamic>> updateMerchantProfile({
    String? businessName,
    String? telebirrAccount,
    double? storeGpsLat,
    double? storeGpsLng,
    String? kebeleCode,
  }) async {
    try {
      final body = <String, dynamic>{};
      if (businessName != null)    body['businessName']    = businessName;
      if (telebirrAccount != null) body['telebirrAccount'] = telebirrAccount;
      if (storeGpsLat != null)     body['storeGpsLat']     = storeGpsLat;
      if (storeGpsLng != null)     body['storeGpsLng']     = storeGpsLng;
      if (kebeleCode != null)      body['kebeleCode']       = kebeleCode;
      final response = await _api.patch(Constants.merchantMe, body);
      return {'success': true, 'data': response['data']};
    } catch (e) {
      return {'success': false, 'message': e.toString()};
    }
  }

  Future<Map<String, dynamic>> updateProfile(Map<String, dynamic> data) async {
    try {
      final response = await _api.patch(Constants.merchantMe, data);
      return {'success': true, 'data': response['data']};
    } catch (e) {
      return {'success': false, 'message': e.toString()};
    }
  }

  // ── Inventory ─────────────────────────────────────────────────────────────

  Future<List<Product>> getInventory() async {
    try {
      final response = await _api.get(Constants.merchantInventory);
      if (response['success'] == true) {
        return (response['data'] as List)
            .map((e) => Product.fromJson(e))
            .toList();
      }
      return [];
    } catch (e) {
      debugPrint('getInventory error: $e');
      return [];
    }
  }

  Future<Map<String, dynamic>> createProduct({
    required String productName,
    required String productCategory,
    required String unit,
    required double currentPriceEtb,
  }) async {
    try {
      final response = await _api.post(Constants.merchantInventory, {
        'productName':     productName,
        'productCategory': productCategory,
        'unit':            unit,
        'currentPriceEtb': currentPriceEtb,
      });
      return {'success': true, 'data': response['data']};
    } catch (e) {
      return {'success': false, 'message': e.toString()};
    }
  }

  Future<Map<String, dynamic>> updateProduct(
      String productId, Map<String, dynamic> data) async {
    try {
      final response =
          await _api.patch('${Constants.merchantInventory}/$productId', data);
      return {'success': true, 'data': response['data']};
    } catch (e) {
      return {'success': false, 'message': e.toString()};
    }
  }

  Future<bool> deleteProduct(String productId) async {
    try {
      await _api.delete('${Constants.merchantInventory}/$productId');
      return true;
    } catch (e) {
      return false;
    }
  }

  // ── Redemption History ────────────────────────────────────────────────────
  // Delegates to VoucherService which calls GET /vouchers/merchant/my

  Future<List<MerchantRedemptionSummary>> getRedemptionHistory({
    String? dateFrom,
    String? dateTo,
    String? status,
    int page = 0,
    int size = 20,
  }) async {
    return _voucherService.getRedemptionHistory(
      dateFrom: dateFrom,
      dateTo:   dateTo,
      status:   status,
      page:     page,
      size:     size,
    );
  }

  // ── Price Anomalies ───────────────────────────────────────────────────────
  // GET /merchant/redemptions actually returns price anomalies on this backend

  Future<List<PriceAnomaly>> getPriceAnomalies() async {
    try {
      final response = await _api.get(Constants.merchantRedemptions);
      if (response['success'] == true) {
        return (response['data'] as List)
            .map((e) => PriceAnomaly.fromJson(e))
            .toList();
      }
      return [];
    } catch (e) {
      debugPrint('getPriceAnomalies error: $e');
      return [];
    }
  }

  // ── Settlements ───────────────────────────────────────────────────────────
  // Reuses redemption history — maps redeemed vouchers as settlement records

  Future<List<SettlementRecord>> getSettlements({
    String? dateFrom,
    String? dateTo,
    int page = 0,
    int size = 20,
  }) async {
    try {
      final redemptions = await getRedemptionHistory(
          dateFrom: dateFrom, dateTo: dateTo);
      return redemptions
          .map((r) => SettlementRecord(
                id:                 r.voucherId,
                voucherId:          r.voucherId,
                farmerName:         r.farmerName,
                productDescription: r.productDescription,
                amountEtb:          r.amountEtb,
                settledAt:          r.redeemedAt,
                paymentReference:   r.paymentReference,
                status:             'SETTLED',
              ))
          .toList();
    } catch (e) {
      debugPrint('getSettlements error: $e');
      return [];
    }
  }

  // ── Analytics ─────────────────────────────────────────────────────────────
  // Combines merchant-service analytics (products/anomalies) with
  // redemption history (revenue stats) computed client-side.

  Future<MerchantAnalytics?> getAnalytics() async {
    try {
      // Fetch base analytics from merchant-service
      final analyticsResponse = await _api.get(Constants.merchantAnalytics);
      // Fetch all redemptions for revenue calculation
      final redemptions = await getRedemptionHistory(size: 1000);

      final now = DateTime.now();
      final weekAgo  = now.subtract(const Duration(days: 7));
      final monthAgo = DateTime(now.year, now.month, 1);

      double totalRevenue      = 0;
      double revenueThisMonth  = 0;
      double revenueThisWeek   = 0;
      int    totalRedeemed     = 0;
      int    redeemedThisMonth = 0;
      int    redeemedThisWeek  = 0;

      // Group by date for trend
      final Map<String, double> trendMap = {};
      final Map<String, int>    trendCountMap = {};

      for (final r in redemptions) {
        totalRevenue += r.amountEtb;
        totalRedeemed++;

        DateTime? dt;
        try { dt = DateTime.parse(r.redeemedAt); } catch (_) {}

        if (dt != null) {
          if (dt.isAfter(monthAgo)) {
            revenueThisMonth += r.amountEtb;
            redeemedThisMonth++;
          }
          if (dt.isAfter(weekAgo)) {
            revenueThisWeek += r.amountEtb;
            redeemedThisWeek++;
          }
          // Daily trend key
          final dayKey = '${dt.year}-${dt.month.toString().padLeft(2,'0')}-${dt.day.toString().padLeft(2,'0')}';
          trendMap[dayKey]      = (trendMap[dayKey]      ?? 0) + r.amountEtb;
          trendCountMap[dayKey] = (trendCountMap[dayKey] ?? 0) + 1;
        }
      }

      // Build revenue trend (last 30 days sorted)
      final trend = trendMap.entries
          .where((e) {
            try {
              return DateTime.parse(e.key).isAfter(
                  now.subtract(const Duration(days: 30)));
            } catch (_) { return false; }
          })
          .map((e) => RevenuePoint(
                date:           e.key,
                revenueEtb:     e.value,
                redemptionCount: trendCountMap[e.key] ?? 0,
              ))
          .toList()
        ..sort((a, b) => a.date.compareTo(b.date));

      // Top products by revenue
      final Map<String, double>  productRevenue = {};
      final Map<String, int>     productCount   = {};
      final Map<String, String>  productCat     = {};
      for (final r in redemptions) {
        productRevenue[r.productDescription] =
            (productRevenue[r.productDescription] ?? 0) + r.amountEtb;
        productCount[r.productDescription]   =
            (productCount[r.productDescription]   ?? 0) + 1;
        productCat[r.productDescription]     = r.productCategory;
      }
      final topProducts = productRevenue.entries
          .map((e) => TopProduct(
                productName:     e.key,
                productCategory: productCat[e.key] ?? 'OTHER',
                redemptionCount: productCount[e.key] ?? 0,
                totalRevenueEtb: e.value,
              ))
          .toList()
        ..sort((a, b) => b.totalRevenueEtb.compareTo(a.totalRevenueEtb));

      // Merge with backend product/anomaly counts
      int    totalProducts    = 0;
      int    availableProducts = 0;
      int    anomaliesCount   = 0;
      double avgPrice         = 0;

      if (analyticsResponse['success'] == true) {
        final d = analyticsResponse['data'] as Map<String, dynamic>? ?? {};
        totalProducts     = (d['totalProducts']     as num?)?.toInt() ?? 0;
        availableProducts = (d['availableProducts'] as num?)?.toInt() ?? 0;
        anomaliesCount    = (d['priceAnomaliesCount'] as num?)?.toInt() ?? 0;
        avgPrice = (d['averageProductPrice'] as num?)?.toDouble() ?? 0;
      }

      return MerchantAnalytics(
        totalProducts:             totalProducts,
        availableProducts:         availableProducts,
        priceAnomaliesCount:       anomaliesCount,
        averageProductPrice:       avgPrice,
        totalRevenueEtb:           totalRevenue,
        revenueThisMonthEtb:       revenueThisMonth,
        revenueThisWeekEtb:        revenueThisWeek,
        totalVouchersRedeemed:     totalRedeemed,
        vouchersRedeemedThisMonth: redeemedThisMonth,
        vouchersRedeemedThisWeek:  redeemedThisWeek,
        topRedeemedProducts:       topProducts.take(5).toList(),
        revenueTrend:              trend,
      );
    } catch (e, stack) {
      debugPrint('getAnalytics error: $e\n$stack');
      return null;
    }
  }
}

// ── SettlementRecord ──────────────────────────────────────────────────────────
class SettlementRecord {
  final String id;
  final String voucherId;
  final String farmerName;
  final String productDescription;
  final double amountEtb;
  final String settledAt;
  final String paymentReference;
  final String status;

  SettlementRecord({
    required this.id,
    required this.voucherId,
    required this.farmerName,
    required this.productDescription,
    required this.amountEtb,
    required this.settledAt,
    required this.paymentReference,
    required this.status,
  });

  factory SettlementRecord.fromJson(Map<String, dynamic> json) {
    return SettlementRecord(
      id:                 json['id']?.toString() ?? '',
      voucherId:          json['voucherId']?.toString() ?? '',
      farmerName:         json['farmerName']?.toString() ?? 'Farmer',
      productDescription: json['productDescription']?.toString() ?? '',
      amountEtb:          (json['amountEtb'] as num?)?.toDouble() ?? 0,
      settledAt:          json['settledAt']?.toString() ?? '',
      paymentReference:   json['paymentReference']?.toString() ?? '',
      status:             json['status']?.toString() ?? 'SETTLED',
    );
  }
}

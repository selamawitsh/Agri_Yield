import 'api_service.dart';
import '../models/merchant_profile_model.dart';
import '../models/product_model.dart';
import '../models/price_anomaly_model.dart';
import '../models/analytics_model.dart';
import '../utils/constants.dart';

class MerchantService {
  final _api = ApiService();

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
        'businessName':           businessName,
        'businessLicenseNumber':  businessLicenseNumber,
        'storeGpsLat':            storeGpsLat,
        'storeGpsLng':            storeGpsLng,
        'telebirrAccount':        telebirrAccount,
        'kebeleCode':             kebeleCode,
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
      return null;
    }
  }

  // MS-03: update merchant-specific fields via PATCH /api/v1/merchants/me
  Future<Map<String, dynamic>> updateMerchantProfile({
    String? businessName,
    String? telebirrAccount,
    double? storeGpsLat,
    double? storeGpsLng,
    String? kebeleCode,
  }) async {
    try {
      final body = <String, dynamic>{};
      if (businessName != null)     body['businessName']    = businessName;
      if (telebirrAccount != null)  body['telebirrAccount'] = telebirrAccount;
      if (storeGpsLat != null)      body['storeGpsLat']     = storeGpsLat;
      if (storeGpsLng != null)      body['storeGpsLng']     = storeGpsLng;
      if (kebeleCode != null)       body['kebeleCode']       = kebeleCode;
      final response = await _api.patch(Constants.merchantMe, body);
      return {'success': true, 'data': response['data']};
    } catch (e) {
      return {'success': false, 'message': e.toString()};
    }
  }

  // kept for backward compatibility — used by auth profile fields
  Future<Map<String, dynamic>> updateProfile(Map<String, dynamic> data) async {
    try {
      final response = await _api.patch(Constants.merchantMe, data);
      return {'success': true, 'data': response['data']};
    } catch (e) {
      return {'success': false, 'message': e.toString()};
    }
  }

  // ── Inventory / Products ──────────────────────────────────────────────────

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
        'productName':      productName,
        'productCategory':  productCategory,
        'unit':             unit,
        'currentPriceEtb':  currentPriceEtb,
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

  // ── Redemptions ───────────────────────────────────────────────────────────

  Future<List<PriceAnomaly>> getRedemptionHistory() async {
    try {
      final response = await _api.get(Constants.merchantRedemptions);
      if (response['success'] == true) {
        return (response['data'] as List)
            .map((e) => PriceAnomaly.fromJson(e))
            .toList();
      }
      return [];
    } catch (e) {
      return [];
    }
  }

  // MS-09: fetch real settlement records from backend
  // GET /api/v1/merchant/settlements
  Future<List<SettlementRecord>> getSettlements({
    String? dateFrom,
    String? dateTo,
    int page = 0,
    int size = 20,
  }) async {
    try {
      final params = <String, dynamic>{
        'page': page,
        'size': size,
        if (dateFrom != null) 'dateFrom': dateFrom,
        if (dateTo != null)   'dateTo':   dateTo,
      };
      final response = await _api.get(
        '/merchant/settlements',
        queryParameters: params,
      );
      final List data = response['data'] ?? [];
      return data.map((e) => SettlementRecord.fromJson(e)).toList();
    } catch (e) {
      return [];
    }
  }

  // ── Analytics ─────────────────────────────────────────────────────────────

  Future<MerchantAnalytics?> getAnalytics() async {
    try {
      final response = await _api.get(Constants.merchantAnalytics);
      if (response['success'] == true) {
        return MerchantAnalytics.fromJson(response['data']);
      }
      return null;
    } catch (e) {
      return null;
    }
  }
}

// MS-09: settlement record model
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

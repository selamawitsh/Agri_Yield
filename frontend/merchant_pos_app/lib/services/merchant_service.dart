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
        'businessName': businessName,
        'businessLicenseNumber': businessLicenseNumber,
        'storeGpsLat': storeGpsLat,
        'storeGpsLng': storeGpsLng,
        'telebirrAccount': telebirrAccount,
        'kebeleCode': kebeleCode,
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
        'productName': productName,
        'productCategory': productCategory,
        'unit': unit,
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

  // ── Redemptions & Anomalies ───────────────────────────────────────────────

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

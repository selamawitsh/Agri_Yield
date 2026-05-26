import 'package:dio/dio.dart';
import '../models/user_model.dart';
import 'api_service.dart';
import 'token_storage.dart';

class AuthService {
  final ApiService _api = ApiService();

  Future<Map<String, dynamic>> register({
    required String phone,
    required String faydaId,
    required String password,
    required String fullName,
    String? businessName,
    String? businessLicenseNumber,
    double? storeGpsLat,
    double? storeGpsLng,
    String? telebirrAccount,
    String? kebeleCode,
  }) async {
    try {
      final body = <String, dynamic>{
        'phone': phone,
        'faydaId': faydaId,
        'password': password,
        'fullName': fullName,
        'role': 'MERCHANT',
      };
      // Only add merchant fields if provided
      if (businessName != null) body['businessName'] = businessName;
      if (businessLicenseNumber != null) body['businessLicenseNumber'] = businessLicenseNumber;
      if (storeGpsLat != null) body['storeGpsLat'] = storeGpsLat;
      if (storeGpsLng != null) body['storeGpsLng'] = storeGpsLng;
      if (telebirrAccount != null) body['telebirrAccount'] = telebirrAccount;
      if (kebeleCode != null && kebeleCode.isNotEmpty) body['kebeleCode'] = kebeleCode;

      final response = await _api.post('/auth/register', body);
      return {'success': response['success'] == true, 'message': response['message'], 'data': response['data']};
    } on DioException catch (e) {
      final msg = e.response?.data?['message'] ?? e.message ?? 'Registration failed';
      return {'success': false, 'message': msg};
    } catch (e) {
      return {'success': false, 'message': e.toString()};
    }
  }

  Future<Map<String, dynamic>> verifyOtp({
    required String phone,
    required String otpCode,
    String purpose = 'REGISTRATION', // backend requires this field
  }) async {
    try {
      final response = await _api.post('/auth/otp/verify', {
        'phone': phone,
        'otpCode': otpCode,
        'purpose': purpose, // required by OtpVerifyRequest
      });
      return {'success': response['success'] == true, 'message': response['message']};
    } on DioException catch (e) {
      final msg = e.response?.data?['message'] ?? e.message ?? 'OTP verification failed';
      return {'success': false, 'message': msg};
    } catch (e) {
      return {'success': false, 'message': e.toString()};
    }
  }

  Future<Map<String, dynamic>> login({
    required String phone,
    required String password,
  }) async {
    try {
      final response = await _api.post('/auth/login', {
        'phone': phone,
        'password': password,
      });
      final data = response['data'];
      if (data != null) {
        await TokenStorage.saveAccessToken(data['accessToken']);
        if (data['refreshToken'] != null) {
          await TokenStorage.saveRefreshToken(data['refreshToken']);
        }
      }
      return {'success': response['success'] == true, 'data': data};
    } on DioException catch (e) {
      final msg = e.response?.data?['message'] ?? e.message ?? 'Login failed';
      return {'success': false, 'message': msg};
    } catch (e) {
      return {'success': false, 'message': e.toString()};
    }
  }

  Future<UserModel?> getCurrentUser() async {
    try {
      final response = await _api.get('/users/me');
      final data = response['data'];
      if (data == null) return null;
      return UserModel.fromJson(data);
    } catch (_) {
      return null;
    }
  }

  Future<Map<String, dynamic>> updateProfile({
    String? email,
    String? preferredLanguage,
  }) async {
    try {
      final body = <String, dynamic>{};
      if (email != null) body['email'] = email;
      if (preferredLanguage != null) body['preferredLanguage'] = preferredLanguage;
      final response = await _api.patch('/users/me', body);
      return {'success': true, 'data': response['data']};
    } on DioException catch (e) {
      return {'success': false, 'message': e.response?.data?['message'] ?? e.message};
    } catch (e) {
      return {'success': false, 'message': e.toString()};
    }
  }

  Future<void> logout() async {
    try { await _api.post('/auth/logout', {}); } catch (_) {}
    await TokenStorage.clearTokens();
  }

  Future<bool> isLoggedIn() async => await TokenStorage.hasToken();
}

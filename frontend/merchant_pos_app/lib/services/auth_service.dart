import 'api_service.dart';
import 'token_storage.dart';
import '../models/user_model.dart';

class AuthService {
  final ApiService _api = ApiService();
  UserModel? _currentUser;
  
  Future<Map<String, dynamic>> register({
    required String phone,
    required String faydaId,
    required String password,
    required String fullName,
    required String businessName,
    required String businessLicenseNumber,
  }) async {
    try {
      final response = await _api.post('/auth/register', {
        'phone': phone,
        'faydaId': faydaId,
        'password': password,
        'role': 'MERCHANT',
        'fullName': fullName,
        'businessName': businessName,
        'businessLicenseNumber': businessLicenseNumber,
      });
      return {
        'success': response['success'],
        'message': response['message'],
        'userId': response['data'],
      };
    } catch (e) {
      return {'success': false, 'message': e.toString()};
    }
  }
  
  Future<Map<String, dynamic>> verifyOtp({
    required String phone,
    required String otpCode,
  }) async {
    try {
      final response = await _api.post('/auth/otp/verify', {
        'phone': phone,
        'otpCode': otpCode,
        'purpose': 'REGISTRATION',
      });
      return {
        'success': response['success'],
        'message': response['message'],
      };
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
      
      if (response['success'] == true) {
        final data = response['data'];
        final accessToken = data['accessToken'];
        final refreshToken = data['refreshToken'];
        
        await TokenStorage.saveAccessToken(accessToken);
        await TokenStorage.saveRefreshToken(refreshToken);
        
        await _fetchUserProfile();
        return {'success': true};
      }
      return {'success': false, 'message': 'Invalid credentials'};
    } catch (e) {
      return {'success': false, 'message': e.toString()};
    }
  }
  
  Future<void> _fetchUserProfile() async {
    try {
      final response = await _api.get('/users/me');
      if (response['success']) {
        _currentUser = UserModel.fromJson(response['data']);
      }
    } catch (e) {
      print('Failed to fetch profile: $e');
    }
  }
  
  Future<UserModel?> getCurrentUser() async {
    if (_currentUser == null) {
      await _fetchUserProfile();
    }
    return _currentUser;
  }
  
  Future<void> updateProfile({String? email, String? preferredLanguage}) async {
    final Map<String, dynamic> updates = {};
    if (email != null) updates['email'] = email;
    if (preferredLanguage != null) updates['preferredLanguage'] = preferredLanguage;
    
    await _api.patch('/users/me', updates);
    await _fetchUserProfile();
  }
  
  Future<void> logout() async {
    await TokenStorage.clearTokens();
    _currentUser = null;
  }
  
  Future<bool> isLoggedIn() async {
    return await TokenStorage.hasToken();
  }
}

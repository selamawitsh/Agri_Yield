import 'package:dio/dio.dart';
import 'dart:convert';
import 'token_storage.dart';
import '../utils/constants.dart';

class ApiService {
  static final ApiService _instance = ApiService._internal();
  factory ApiService() => _instance;
  ApiService._internal();

  late Dio _dio;

  Future<void> init() async {
    _dio = Dio(BaseOptions(
      baseUrl: Constants.apiBaseUrl,
      connectTimeout: const Duration(seconds: 30),
      receiveTimeout: const Duration(seconds: 30),
      headers: {'Content-Type': 'application/json'},
    ));

    _dio.interceptors.add(InterceptorsWrapper(
      onRequest: (options, handler) async {
        final token = await TokenStorage.getAccessToken();
        if (token != null) {
          // 1. Bearer token
          options.headers['Authorization'] = 'Bearer $token';

          // 2. X-User-Id decoded from JWT — required by every backend endpoint
          final userId = _extractUserIdFromJwt(token);
          if (userId != null) {
            options.headers['X-User-Id'] = userId;
          } else {
            // This means JWT has no recognisable user ID claim — check debugJwt()
            print('⚠️  X-User-Id could not be extracted from JWT');
          }
        }
        return handler.next(options);
      },
      onError: (error, handler) async {
        if (error.response?.statusCode == 401) {
          final refreshed = await _tryRefreshToken();
          if (refreshed) {
            final token = await TokenStorage.getAccessToken();
            if (token != null) {
              error.requestOptions.headers['Authorization'] =
                  'Bearer $token';
              final userId = _extractUserIdFromJwt(token);
              if (userId != null) {
                error.requestOptions.headers['X-User-Id'] = userId;
              }
            }
            try {
              final response = await _dio.fetch(error.requestOptions);
              return handler.resolve(response);
            } catch (e) {
              return handler.next(error);
            }
          }
        }
        return handler.next(error);
      },
    ));
  }

  /// Decodes the JWT payload (base64url) and returns the user UUID.
  /// Spring Security puts the UUID in the 'sub' claim by default.
  /// Falls back to 'userId' or 'user_id' if your auth service uses those.
  String? _extractUserIdFromJwt(String token) {
    try {
      final parts = token.split('.');
      if (parts.length != 3) return null;

      // base64url → base64 (restore padding)
      String payload = parts[1]
          .replaceAll('-', '+')
          .replaceAll('_', '/');
      switch (payload.length % 4) {
        case 2: payload += '=='; break;
        case 3: payload += '=';  break;
      }

      final decoded = utf8.decode(base64Decode(payload));
      final Map<String, dynamic> claims = jsonDecode(decoded);

      // Try the three most common claim names Spring boots use
      final id = claims['sub']
               ?? claims['userId']
               ?? claims['user_id'];

      return id?.toString();
    } catch (e) {
      print('❌ JWT decode error: $e');
      return null;
    }
  }

  Future<bool> _tryRefreshToken() async {
    try {
      final refreshToken = await TokenStorage.getRefreshToken();
      if (refreshToken == null) return false;

      final response = await Dio().post(
        '${Constants.apiBaseUrl}/auth/refresh',
        data: {'refresh_token': refreshToken},
        options: Options(headers: {'Content-Type': 'application/json'}),
      );
      if (response.data['success'] == true) {
        await TokenStorage.saveAccessToken(
            response.data['data']['accessToken']);
        return true;
      }
      return false;
    } catch (_) {
      await TokenStorage.clearTokens();
      return false;
    }
  }

  Future<dynamic> get(String endpoint,
      {Map<String, dynamic>? queryParameters}) async {
    final response =
        await _dio.get(endpoint, queryParameters: queryParameters);
    return response.data;
  }

  Future<dynamic> post(String endpoint, dynamic data) async {
    final response = await _dio.post(endpoint, data: data);
    return response.data;
  }

  Future<dynamic> patch(String endpoint, dynamic data) async {
    final response = await _dio.patch(endpoint, data: data);
    return response.data;
  }

  Future<dynamic> delete(String endpoint) async {
    final response = await _dio.delete(endpoint);
    return response.data;
  }
}

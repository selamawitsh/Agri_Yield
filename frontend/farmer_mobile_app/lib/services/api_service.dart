import 'package:dio/dio.dart';
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
          options.headers['Authorization'] = 'Bearer $token';
        }
        return handler.next(options);
      },
      onError: (error, handler) async {
        if (error.response?.statusCode == 401) {
          final refreshed = await _tryRefreshToken();
          if (refreshed) {
            final token = await TokenStorage.getAccessToken();
            error.requestOptions.headers['Authorization'] = 'Bearer $token';
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
        final newToken = response.data['data']['accessToken'];
        await TokenStorage.saveAccessToken(newToken);
        return true;
      }
      return false;
    } catch (_) {
      await TokenStorage.clearTokens();
      return false;
    }
  }

  Future<dynamic> get(String endpoint) async {
    final response = await _dio.get(endpoint);
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

  /// Used by farm_service.dart for photo uploads (multipart/form-data)
  Future<dynamic> postMultipart(
    String endpoint,
    Map<String, dynamic> fields,
    String fileField,
    String filePath, {
    String? fileName,
  }) async {
    final formData = FormData.fromMap({
      ...fields,
      fileField: await MultipartFile.fromFile(
        filePath,
        filename: fileName ?? filePath.split('/').last,
      ),
    });
    final response = await _dio.post(endpoint, data: formData,
        options: Options(headers: {'Content-Type': 'multipart/form-data'}));
    return response.data;
  }

  /// Accepts a pre-built [FormData] instance. This matches usages that
  /// construct FormData directly (e.g. voice advisory with an audio file).
  Future<dynamic> postMultipartForm(String endpoint, FormData formData,
      {String? token}) async {
    final headers = {'Content-Type': 'multipart/form-data'};
    if (token != null) headers['Authorization'] = 'Bearer $token';

    final response = await _dio.post(endpoint,
        data: formData, options: Options(headers: headers));
    return response.data;
  }
}

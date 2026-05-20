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
    ));
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
}

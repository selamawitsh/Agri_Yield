import 'dart:io';
import 'dart:ui';
import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';

class AiService {
  final Dio _dio;

  // Change this to your actual API gateway URL
  static const String _baseUrl = 'http://10.0.2.2:8080/api/v1/ai';

  AiService() : _dio = Dio(BaseOptions(
    baseUrl: _baseUrl,
    connectTimeout: const Duration(seconds: 30),
    receiveTimeout: const Duration(seconds: 60), // AI calls can be slow
  ));

  Future<String> _getToken() async {
    final prefs = await SharedPreferences.getInstance();
    return prefs.getString('access_token') ?? '';
  }

  Future<Map<String, String>> _authHeaders() async {
    final token = await _getToken();
    return {'Authorization': 'Bearer $token'};
  }

  // -----------------------------------------------------------------------
  // UC-AI-01: Voice Advisory
  // Uploads audio file and gets advisory text + audio response URL
  // -----------------------------------------------------------------------
  Future<AdvisoryResult> submitVoiceAdvisory({
    required String farmId,
    required File audioFile,
    String language = 'am',
  }) async {
    final headers = await _authHeaders();

    final formData = FormData.fromMap({
      'farm_id': farmId,
      'language': language,
      'audio_file': await MultipartFile.fromFile(
        audioFile.path,
        filename: 'advisory_audio.m4a',
      ),
    });

    final response = await _dio.post(
      '/advisory/voice',
      data: formData,
      options: Options(
        headers: headers,
        contentType: 'multipart/form-data',
      ),
    );

    if (response.data['success'] == true) {
      return AdvisoryResult.fromJson(response.data['data']);
    }
    throw Exception(response.data['message'] ?? 'Voice advisory failed');
  }

  // -----------------------------------------------------------------------
  // UC-AI-02: Text Advisory
  // -----------------------------------------------------------------------
  Future<AdvisoryResult> submitTextAdvisory({
    required String farmId,
    required String query,
    String language = 'am',
  }) async {
    final headers = await _authHeaders();

    final response = await _dio.post(
      '/advisory/text',
      data: {
        'farmId': farmId,
        'query': query,
        'language': language,
      },
      options: Options(headers: headers),
    );

    if (response.data['success'] == true) {
      return AdvisoryResult.fromJson(response.data['data']);
    }
    throw Exception(response.data['message'] ?? 'Text advisory failed');
  }

  // -----------------------------------------------------------------------
  // UC-AI-03: Crop Disease Diagnosis from image
  // -----------------------------------------------------------------------
  Future<DiagnosisResult> diagnoseCropImage({
    required String farmId,
    required File imageFile,
  }) async {
    final headers = await _authHeaders();

    final formData = FormData.fromMap({
      'farm_id': farmId,
      'image_file': await MultipartFile.fromFile(
        imageFile.path,
        filename: 'crop_photo.jpg',
      ),
    });

    final response = await _dio.post(
      '/diagnose/image',
      data: formData,
      options: Options(
        headers: headers,
        contentType: 'multipart/form-data',
      ),
    );

    if (response.data['success'] == true) {
      return DiagnosisResult.fromJson(response.data['data']);
    }
    throw Exception(response.data['message'] ?? 'Diagnosis failed');
  }

  // -----------------------------------------------------------------------
  // Get advisory history for a farm
  // -----------------------------------------------------------------------
  Future<List<AdvisoryResult>> getAdvisoryHistory(String farmId) async {
    final headers = await _authHeaders();
    final response = await _dio.get(
      '/advisory/$farmId/history',
      options: Options(headers: headers),
    );
    if (response.data['success'] == true) {
      final List data = response.data['data'] ?? [];
      return data.map((e) => AdvisoryResult.fromJson(e)).toList();
    }
    return [];
  }
}

// -----------------------------------------------------------------------
// Data models
// -----------------------------------------------------------------------
class AdvisoryResult {
  final String sessionId;
  final String farmId;
  final String queryType;      // VOICE or TEXT
  final String language;
  final String originalQuery;
  final String advisoryText;
  final String? audioResponseUrl;
  final String? cropType;
  final double? currentNdvi;
  final String? createdAt;

  AdvisoryResult({
    required this.sessionId,
    required this.farmId,
    required this.queryType,
    required this.language,
    required this.originalQuery,
    required this.advisoryText,
    this.audioResponseUrl,
    this.cropType,
    this.currentNdvi,
    this.createdAt,
  });

  factory AdvisoryResult.fromJson(Map<String, dynamic> json) {
    return AdvisoryResult(
      sessionId: json['sessionId'] ?? '',
      farmId: json['farmId'] ?? '',
      queryType: json['queryType'] ?? 'TEXT',
      language: json['language'] ?? 'am',
      originalQuery: json['originalQuery'] ?? '',
      advisoryText: json['advisoryText'] ?? '',
      audioResponseUrl: json['audioResponseUrl'],
      cropType: json['cropType'],
      currentNdvi: json['currentNdvi']?.toDouble(),
      createdAt: json['createdAt'],
    );
  }
}

class DiagnosisResult {
  final String diagnosisId;
  final String farmId;
  final String? photoId;
  final String? cropType;
  final double? currentNdvi;
  final String diseaseName;
  final int confidencePct;
  final String symptomsObserved;
  final String recommendedTreatment;
  final String severity;             // LOW, MEDIUM, HIGH
  final bool escalateToAgronomist;
  final String? diagnosedAt;

  DiagnosisResult({
    required this.diagnosisId,
    required this.farmId,
    this.photoId,
    this.cropType,
    this.currentNdvi,
    required this.diseaseName,
    required this.confidencePct,
    required this.symptomsObserved,
    required this.recommendedTreatment,
    required this.severity,
    required this.escalateToAgronomist,
    this.diagnosedAt,
  });

  factory DiagnosisResult.fromJson(Map<String, dynamic> json) {
    return DiagnosisResult(
      diagnosisId: json['diagnosisId'] ?? '',
      farmId: json['farmId'] ?? '',
      photoId: json['photoId'],
      cropType: json['cropType'],
      currentNdvi: json['currentNdvi']?.toDouble(),
      diseaseName: json['diseaseName'] ?? 'Unknown',
      confidencePct: json['confidencePct'] ?? 0,
      symptomsObserved: json['symptomsObserved'] ?? '',
      recommendedTreatment: json['recommendedTreatment'] ?? '',
      severity: json['severity'] ?? 'MEDIUM',
      escalateToAgronomist: json['escalateToAgronomist'] ?? false,
      diagnosedAt: json['diagnosedAt'],
    );
  }

  Color get severityColor {
    switch (severity) {
      case 'HIGH': return const Color(0xFFE53E3E);
      case 'MEDIUM': return const Color(0xFFDD6B20);
      default: return const Color(0xFF38A169);
    }
  }
}
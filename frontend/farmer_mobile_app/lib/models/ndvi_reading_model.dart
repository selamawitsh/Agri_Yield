import 'package:flutter/material.dart';

class NdviReadingModel {
  final String? farmId;
  final double ndviValue;
  final double cloudCoverage;
  final String healthStatus;
  final String? sentinelSceneId;
  final String recordedDate;

  NdviReadingModel({
    this.farmId,
    required this.ndviValue,
    required this.cloudCoverage,
    required this.healthStatus,
    this.sentinelSceneId,
    required this.recordedDate,
  });

  factory NdviReadingModel.fromJson(Map<String, dynamic> json) {
    return NdviReadingModel(
      farmId: json['farmId']?.toString(),
      ndviValue: _toDouble(json['ndviValue']),
      cloudCoverage: _toDouble(json['cloudCoverage']),
      healthStatus: json['healthStatus']?.toString() ?? 'UNKNOWN',
      sentinelSceneId: json['sentinelSceneId']?.toString(),
      recordedDate: json['recordedDate']?.toString() ?? '',
    );
  }

  static double _toDouble(dynamic v) {
    if (v == null) return 0;
    if (v is num) return v.toDouble();
    return double.tryParse(v.toString()) ?? 0;
  }

  String get healthLabel {
    switch (healthStatus.toUpperCase()) {
      case 'EXCELLENT':
        return 'Excellent';
      case 'GOOD':
        return 'Good';
      case 'MODERATE':
        return 'Moderate';
      case 'POOR':
        return 'Poor';
      case 'STRESSED':
        return 'Stressed';
      default:
        return healthStatus;
    }
  }

  Color get healthColor {
    switch (healthStatus.toUpperCase()) {
      case 'EXCELLENT':
      case 'GOOD':
        return const Color(0xFF2D6A4F);
      case 'MODERATE':
        return const Color(0xFFE9C46A);
      case 'POOR':
      case 'STRESSED':
        return const Color(0xFFE76F51);
      default:
        return const Color(0xFF64748B);
    }
  }
}

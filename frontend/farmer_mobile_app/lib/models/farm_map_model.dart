import 'ndvi_reading_model.dart';

class FarmMapModel {
  final String farmId;
  final String? geoJsonPolygon;
  final double centroidLat;
  final double centroidLng;
  final double areaHectares;
  final NdviReadingModel? latestNdvi;

  FarmMapModel({
    required this.farmId,
    this.geoJsonPolygon,
    required this.centroidLat,
    required this.centroidLng,
    required this.areaHectares,
    this.latestNdvi,
  });

  factory FarmMapModel.fromJson(Map<String, dynamic> json) {
    return FarmMapModel(
      farmId: json['farmId']?.toString() ?? '',
      geoJsonPolygon: json['geoJsonPolygon']?.toString(),
      centroidLat: _toDouble(json['centroidLat']),
      centroidLng: _toDouble(json['centroidLng']),
      areaHectares: _toDouble(json['areaHectares']),
      latestNdvi: json['latestNdvi'] != null
          ? NdviReadingModel.fromJson(
              Map<String, dynamic>.from(json['latestNdvi'] as Map))
          : null,
    );
  }

  static double _toDouble(dynamic v) {
    if (v == null) return 0;
    if (v is num) return v.toDouble();
    return double.tryParse(v.toString()) ?? 0;
  }

  bool get hasPolygon =>
      geoJsonPolygon != null && geoJsonPolygon!.trim().isNotEmpty;
}

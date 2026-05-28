class YieldPredictionModel {
  final String? farmId;
  final String? cropType;
  final double predictedYieldMean;
  final double totalYieldMeanQuintals;
  final int confidencePct;
  final int? weeksToHarvest;
  final String? modelVersion;
  final String? predictedAt;

  YieldPredictionModel({
    this.farmId,
    this.cropType,
    required this.predictedYieldMean,
    required this.totalYieldMeanQuintals,
    required this.confidencePct,
    this.weeksToHarvest,
    this.modelVersion,
    this.predictedAt,
  });

  factory YieldPredictionModel.fromJson(Map<String, dynamic> json) {
    return YieldPredictionModel(
      farmId: json['farmId']?.toString(),
      cropType: json['cropType']?.toString(),
      predictedYieldMean: _toDouble(json['predictedYieldMean']),
      totalYieldMeanQuintals: _toDouble(json['totalYieldMeanQuintals']),
      confidencePct: _toInt(json['confidencePct']),
      weeksToHarvest: json['weeksToHarvest'] != null
          ? _toInt(json['weeksToHarvest'])
          : null,
      modelVersion: json['modelVersion']?.toString(),
      predictedAt: json['predictedAt']?.toString(),
    );
  }

  static double _toDouble(dynamic v) {
    if (v == null) return 0;
    if (v is num) return v.toDouble();
    return double.tryParse(v.toString()) ?? 0;
  }

  static int _toInt(dynamic v) {
    if (v == null) return 0;
    if (v is int) return v;
    if (v is num) return v.toInt();
    return int.tryParse(v.toString()) ?? 0;
  }
}

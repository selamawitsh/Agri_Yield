class HarvestReadinessModel {
  final bool ready;
  final String? estimatedDateFrom;
  final String? estimatedDateTo;
  final double currentNdvi;
  final double peakNdvi;
  final String readinessSignal;

  HarvestReadinessModel({
    required this.ready,
    this.estimatedDateFrom,
    this.estimatedDateTo,
    required this.currentNdvi,
    required this.peakNdvi,
    required this.readinessSignal,
  });

  factory HarvestReadinessModel.fromJson(Map<String, dynamic> json) {
    return HarvestReadinessModel(
      ready: json['ready'] == true,
      estimatedDateFrom: json['estimatedDateFrom']?.toString(),
      estimatedDateTo: json['estimatedDateTo']?.toString(),
      currentNdvi: _toDouble(json['currentNdvi']),
      peakNdvi: _toDouble(json['peakNdvi']),
      readinessSignal: json['readinessSignal']?.toString() ?? '',
    );
  }

  static double _toDouble(dynamic v) {
    if (v == null) return 0;
    if (v is num) return v.toDouble();
    return double.tryParse(v.toString()) ?? 0;
  }

  String get signalLabel {
    switch (readinessSignal) {
      case 'NDVI_DECLINING':
        return 'Crop may be approaching harvest';
      case 'STILL_GROWING':
        return 'Crop still in growth phase';
      case 'INSUFFICIENT_DATA':
        return 'More satellite readings needed';
      default:
        return readinessSignal;
    }
  }
}

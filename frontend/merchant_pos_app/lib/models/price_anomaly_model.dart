class PriceAnomaly {
  final String id;
  final String merchantId;
  final String productId;
  final double merchantPriceEtb;
  final double regionalMedianEtb;
  final double deviationPct;
  final String flaggedAt;
  final String? resolvedAt;

  PriceAnomaly({
    required this.id,
    required this.merchantId,
    required this.productId,
    required this.merchantPriceEtb,
    required this.regionalMedianEtb,
    required this.deviationPct,
    required this.flaggedAt,
    this.resolvedAt,
  });

  factory PriceAnomaly.fromJson(Map<String, dynamic> json) {
    return PriceAnomaly(
      id:               json['id']?.toString() ?? '',
      merchantId:       json['merchantId']?.toString() ?? '',
      productId:        json['productId']?.toString() ?? '',
      // Backend sends BigDecimal — may be String, int, or double
      merchantPriceEtb: _toDouble(json['merchantPriceEtb']),
      regionalMedianEtb: _toDouble(json['regionalMedianEtb']),
      deviationPct:     _toDouble(json['deviationPct']),
      flaggedAt:        json['flaggedAt']?.toString() ?? '',
      resolvedAt:       json['resolvedAt']?.toString(),
    );
  }

  static double _toDouble(dynamic v) {
    if (v == null) return 0.0;
    if (v is double) return v;
    if (v is int)    return v.toDouble();
    if (v is String) return double.tryParse(v) ?? 0.0;
    if (v is num)    return v.toDouble();
    return 0.0;
  }
}

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
      id: json['id'] ?? '',
      merchantId: json['merchantId'] ?? '',
      productId: json['productId'] ?? '',
      merchantPriceEtb: (json['merchantPriceEtb'] ?? 0).toDouble(),
      regionalMedianEtb: (json['regionalMedianEtb'] ?? 0).toDouble(),
      deviationPct: (json['deviationPct'] ?? 0).toDouble(),
      flaggedAt: json['flaggedAt'] ?? '',
      resolvedAt: json['resolvedAt'],
    );
  }
}

class MerchantAnalytics {
  final int totalProducts;
  final int availableProducts;
  final int priceAnomaliesCount;
  final double averageProductPrice;

  MerchantAnalytics({
    required this.totalProducts,
    required this.availableProducts,
    required this.priceAnomaliesCount,
    required this.averageProductPrice,
  });

  factory MerchantAnalytics.fromJson(Map<String, dynamic> json) {
    return MerchantAnalytics(
      totalProducts: json['totalProducts'] ?? 0,
      availableProducts: json['availableProducts'] ?? 0,
      priceAnomaliesCount: json['priceAnomaliesCount'] ?? 0,
      averageProductPrice: (json['averageProductPrice'] ?? 0).toDouble(),
    );
  }
}

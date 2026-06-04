class MerchantAnalytics {
  final int totalProducts;
  final int availableProducts;
  final int priceAnomaliesCount;
  final double averageProductPrice;

  // MS-10: revenue and redemption statistics
  final double totalRevenueEtb;
  final double revenueThisMonthEtb;
  final double revenueThisWeekEtb;
  final int totalVouchersRedeemed;
  final int vouchersRedeemedThisMonth;
  final int vouchersRedeemedThisWeek;
  final List<TopProduct> topRedeemedProducts;
  final List<RevenuePoint> revenueTrend;

  MerchantAnalytics({
    required this.totalProducts,
    required this.availableProducts,
    required this.priceAnomaliesCount,
    required this.averageProductPrice,
    this.totalRevenueEtb = 0,
    this.revenueThisMonthEtb = 0,
    this.revenueThisWeekEtb = 0,
    this.totalVouchersRedeemed = 0,
    this.vouchersRedeemedThisMonth = 0,
    this.vouchersRedeemedThisWeek = 0,
    this.topRedeemedProducts = const [],
    this.revenueTrend = const [],
  });

  factory MerchantAnalytics.fromJson(Map<String, dynamic> json) {
    return MerchantAnalytics(
      totalProducts:              json['totalProducts'] ?? 0,
      availableProducts:          json['availableProducts'] ?? 0,
      priceAnomaliesCount:        json['priceAnomaliesCount'] ?? 0,
      averageProductPrice:        (json['averageProductPrice'] ?? 0).toDouble(),
      totalRevenueEtb:            (json['totalRevenueEtb'] ?? 0).toDouble(),
      revenueThisMonthEtb:        (json['revenueThisMonthEtb'] ?? 0).toDouble(),
      revenueThisWeekEtb:         (json['revenueThisWeekEtb'] ?? 0).toDouble(),
      totalVouchersRedeemed:      json['totalVouchersRedeemed'] ?? 0,
      vouchersRedeemedThisMonth:  json['vouchersRedeemedThisMonth'] ?? 0,
      vouchersRedeemedThisWeek:   json['vouchersRedeemedThisWeek'] ?? 0,
      topRedeemedProducts: (json['topRedeemedProducts'] as List<dynamic>? ?? [])
          .map((e) => TopProduct.fromJson(e))
          .toList(),
      revenueTrend: (json['revenueTrend'] as List<dynamic>? ?? [])
          .map((e) => RevenuePoint.fromJson(e))
          .toList(),
    );
  }
}

class TopProduct {
  final String productName;
  final String productCategory;
  final int redemptionCount;
  final double totalRevenueEtb;

  TopProduct({
    required this.productName,
    required this.productCategory,
    required this.redemptionCount,
    required this.totalRevenueEtb,
  });

  factory TopProduct.fromJson(Map<String, dynamic> json) {
    return TopProduct(
      productName:     json['productName']?.toString() ?? '',
      productCategory: json['productCategory']?.toString() ?? '',
      redemptionCount: json['redemptionCount'] ?? 0,
      totalRevenueEtb: (json['totalRevenueEtb'] ?? 0).toDouble(),
    );
  }
}

class RevenuePoint {
  final String date;
  final double revenueEtb;
  final int redemptionCount;

  RevenuePoint({
    required this.date,
    required this.revenueEtb,
    required this.redemptionCount,
  });

  factory RevenuePoint.fromJson(Map<String, dynamic> json) {
    return RevenuePoint(
      date:           json['date']?.toString() ?? '',
      revenueEtb:     (json['revenueEtb'] ?? 0).toDouble(),
      redemptionCount: json['redemptionCount'] ?? 0,
    );
  }
}

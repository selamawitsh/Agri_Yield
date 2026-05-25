class Product {
  final String id;
  final String merchantId;
  final String productName;
  final String productCategory;
  final String unit;
  final double currentPriceEtb;
  final bool isAvailable;
  final String createdAt;
  final String updatedAt;

  Product({
    required this.id,
    required this.merchantId,
    required this.productName,
    required this.productCategory,
    required this.unit,
    required this.currentPriceEtb,
    required this.isAvailable,
    required this.createdAt,
    required this.updatedAt,
  });

  factory Product.fromJson(Map<String, dynamic> json) {
    return Product(
      id: json['id'] ?? '',
      merchantId: json['merchantId'] ?? '',
      productName: json['productName'] ?? '',
      productCategory: json['productCategory'] ?? '',
      unit: json['unit'] ?? '',
      currentPriceEtb: (json['currentPriceEtb'] ?? 0).toDouble(),
      isAvailable: json['isAvailable'] ?? true,
      createdAt: json['createdAt'] ?? '',
      updatedAt: json['updatedAt'] ?? '',
    );
  }

  Map<String, dynamic> toJson() => {
    'productName': productName,
    'productCategory': productCategory,
    'unit': unit,
    'currentPriceEtb': currentPriceEtb,
    'isAvailable': isAvailable,
  };
}

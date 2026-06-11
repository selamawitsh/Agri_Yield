class Product {
  final String id;
  final String merchantId;
  final String productName;
  final String productCategory;
  final String unit;
  final double currentPriceEtb;
  final bool isAvailable;
  final double quantityInStock;
  final String unitOfMeasure;
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
    required this.quantityInStock,
    required this.unitOfMeasure,
    required this.createdAt,
    required this.updatedAt,
  });

  factory Product.fromJson(Map<String, dynamic> json) {
    return Product(
      id:               json['id']?.toString() ?? '',
      merchantId:       json['merchantId']?.toString() ?? '',
      productName:      json['productName']?.toString() ?? '',
      productCategory:  json['productCategory']?.toString() ?? '',
      unit:             json['unit']?.toString() ?? 'kg',
      currentPriceEtb:  (json['currentPriceEtb'] as num?)?.toDouble() ?? 0,
      isAvailable:      json['isAvailable'] as bool? ?? true,
      quantityInStock:  (json['quantityInStock'] as num?)?.toDouble() ?? 0,
      unitOfMeasure:    json['unitOfMeasure']?.toString()
                        ?? json['unit']?.toString() ?? 'kg',
      createdAt:        json['createdAt']?.toString() ?? '',
      updatedAt:        json['updatedAt']?.toString() ?? '',
    );
  }

  Map<String, dynamic> toJson() => {
    'productName':     productName,
    'productCategory': productCategory,
    'unit':            unit,
    'currentPriceEtb': currentPriceEtb,
    'isAvailable':     isAvailable,
    'quantityInStock': quantityInStock,
  };

  bool get isLowStock => quantityInStock > 0 && quantityInStock < 5;
  bool get isOutOfStock => quantityInStock <= 0;
}

class InputNeedModel {
  final String id;
  final String farmId;
  final String cropCycleId;
  final double totalAmountEtb;
  final double fundedAmountEtb;
  final String status;
  final String createdAt;
  final List<InputNeedItemModel> items;

  InputNeedModel({
    required this.id,
    required this.farmId,
    required this.cropCycleId,
    required this.totalAmountEtb,
    required this.fundedAmountEtb,
    required this.status,
    required this.createdAt,
    required this.items,
  });

  factory InputNeedModel.fromJson(Map<String, dynamic> json) {
    return InputNeedModel(
      id: json['id'] ?? '',
      farmId: json['farmId'] ?? '',
      cropCycleId: json['cropCycleId'] ?? '',
      totalAmountEtb: (json['totalAmountEtb'] ?? 0).toDouble(),
      fundedAmountEtb: (json['fundedAmountEtb'] ?? 0).toDouble(),
      status: json['status'] ?? '',
      createdAt: json['createdAt'] ?? '',
      items: (json['items'] as List<dynamic>? ?? [])
          .map((e) => InputNeedItemModel.fromJson(e))
          .toList(),
    );
  }

  double get fundingPercentage {
    if (totalAmountEtb == 0) return 0;
    return (fundedAmountEtb / totalAmountEtb * 100).clamp(0, 100);
  }

  String get statusLabel {
    switch (status) {
      case 'OPEN': return 'Open';
      case 'PARTIALLY_FUNDED': return 'Partially Funded';
      case 'FULLY_FUNDED': return 'Fully Funded';
      case 'CANCELLED': return 'Cancelled';
      default: return status;
    }
  }
}

class InputNeedItemModel {
  final String id;
  final String productCategory;
  final String productName;
  final double quantity;
  final String unit;
  final double estimatedPriceEtb;
  final int sequenceOrder;

  InputNeedItemModel({
    required this.id,
    required this.productCategory,
    required this.productName,
    required this.quantity,
    required this.unit,
    required this.estimatedPriceEtb,
    required this.sequenceOrder,
  });

  factory InputNeedItemModel.fromJson(Map<String, dynamic> json) {
    return InputNeedItemModel(
      id: json['id'] ?? '',
      productCategory: json['productCategory'] ?? '',
      productName: json['productName'] ?? '',
      quantity: (json['quantity'] ?? 0).toDouble(),
      unit: json['unit'] ?? '',
      estimatedPriceEtb: (json['estimatedPriceEtb'] ?? 0).toDouble(),
      sequenceOrder: json['sequenceOrder'] ?? 1,
    );
  }
}

class VoucherModel {
  final String id;
  final String farmId;
  final String cropCycleId;
  final String inputNeedItemId;
  final double amountEtb;
  final String productCategory;
  final String productDescription;
  final int sequenceOrder;
  final String status;
  final String alphanumericCode;
  final String validUntil;
  final String? redeemedAt;
  final String? redeemedMerchantId;
  final String createdAt;

  VoucherModel({
    required this.id,
    required this.farmId,
    required this.cropCycleId,
    required this.inputNeedItemId,
    required this.amountEtb,
    required this.productCategory,
    required this.productDescription,
    required this.sequenceOrder,
    required this.status,
    required this.alphanumericCode,
    required this.validUntil,
    this.redeemedAt,
    this.redeemedMerchantId,
    required this.createdAt,
  });

  factory VoucherModel.fromJson(Map<String, dynamic> json) {
    return VoucherModel(
      id: json['id'] ?? '',
      farmId: json['farmId'] ?? '',
      cropCycleId: json['cropCycleId'] ?? '',
      inputNeedItemId: json['inputNeedItemId'] ?? '',
      amountEtb: (json['amountEtb'] ?? 0).toDouble(),
      productCategory: json['productCategory'] ?? '',
      productDescription: json['productDescription'] ?? '',
      sequenceOrder: json['sequenceOrder'] ?? 1,
      status: json['status'] ?? '',
      alphanumericCode: json['alphanumericCode'] ?? '',
      validUntil: json['validUntil'] ?? '',
      redeemedAt: json['redeemedAt'],
      redeemedMerchantId: json['redeemedMerchantId'],
      createdAt: json['createdAt'] ?? '',
    );
  }

  Map<String, dynamic> toJson() => {
        'id': id,
        'farmId': farmId,
        'cropCycleId': cropCycleId,
        'inputNeedItemId': inputNeedItemId,
        'amountEtb': amountEtb,
        'productCategory': productCategory,
        'productDescription': productDescription,
        'sequenceOrder': sequenceOrder,
        'status': status,
        'alphanumericCode': alphanumericCode,
        'validUntil': validUntil,
        'redeemedAt': redeemedAt,
        'redeemedMerchantId': redeemedMerchantId,
        'createdAt': createdAt,
      };

  // ── Helpers ──────────────────────────────────────────────

  bool get isActive    => status == 'ACTIVE';
  bool get isRedeemed  => status == 'REDEEMED';
  bool get isLocked    => status == 'GENERATED';
  bool get isExpired   => status == 'EXPIRED';
  bool get isCancelled => status == 'CANCELLED';

  bool get isUsable => isActive;

  String get statusLabel {
    switch (status) {
      case 'ACTIVE':    return 'Active';
      case 'REDEEMED':  return 'Redeemed';
      case 'GENERATED': return 'Locked';
      case 'EXPIRED':   return 'Expired';
      case 'CANCELLED': return 'Cancelled';
      case 'REJECTED':  return 'Rejected';
      default:          return status;
    }
  }

  String get categoryEmoji {
    switch (productCategory) {
      case 'SEED':       return '🌾';
      case 'FERTILIZER': return '🪣';
      case 'PESTICIDE':  return '🛡️';
      case 'TOOL':       return '🔧';
      default:           return '📦';
    }
  }

  DateTime? get validUntilDate {
    try { return DateTime.parse(validUntil); } catch (_) { return null; }
  }

  bool get isExpiringSoon {
    final d = validUntilDate;
    if (d == null) return false;
    return d.difference(DateTime.now()).inDays <= 7 && isActive;
  }
}

class VoucherSummaryModel {
  final int totalVouchers;
  final int activeCount;
  final int redeemedCount;
  final int lockedCount;
  final int expiredCount;
  final double totalValueEtb;
  final double redeemedValueEtb;
  final double pendingValueEtb;

  VoucherSummaryModel({
    required this.totalVouchers,
    required this.activeCount,
    required this.redeemedCount,
    required this.lockedCount,
    required this.expiredCount,
    required this.totalValueEtb,
    required this.redeemedValueEtb,
    required this.pendingValueEtb,
  });

  factory VoucherSummaryModel.fromList(List<VoucherModel> vouchers) {
    return VoucherSummaryModel(
      totalVouchers:    vouchers.length,
      activeCount:      vouchers.where((v) => v.isActive).length,
      redeemedCount:    vouchers.where((v) => v.isRedeemed).length,
      lockedCount:      vouchers.where((v) => v.isLocked).length,
      expiredCount:     vouchers.where((v) => v.isExpired).length,
      totalValueEtb:    vouchers.fold(0, (s, v) => s + v.amountEtb),
      redeemedValueEtb: vouchers.where((v) => v.isRedeemed).fold(0, (s, v) => s + v.amountEtb),
      pendingValueEtb:  vouchers.where((v) => v.isActive || v.isLocked).fold(0, (s, v) => s + v.amountEtb),
    );
  }

  int get redemptionPct =>
      totalVouchers == 0 ? 0 : ((redeemedCount / totalVouchers) * 100).round();
}

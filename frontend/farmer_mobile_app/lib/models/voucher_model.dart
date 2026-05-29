// lib/models/voucher_model.dart
// Updated to match actual API response from VoucherResponse.java

class VoucherModel {
  final String id;
  final String voucherCode;       // API: voucherCode (was alphanumericCode)
  final String investmentId;
  final String farmId;
  final String farmerId;
  final String inputNeedId;       // API: inputNeedId (was inputNeedItemId)
  final String cropCycleId;
  final double amountEtb;
  final String productCategory;
  final String productName;       // API: productName (was productDescription)
  final int sequenceOrder;        // not in API — default to 1
  final String status;
  final String validUntil;        // API: expiresAt (was validUntil)
  final String? redeemedAt;
  final String? redeemedMerchantId;
  final String createdAt;

  VoucherModel({
    required this.id,
    required this.voucherCode,
    required this.investmentId,
    required this.farmId,
    required this.farmerId,
    required this.inputNeedId,
    required this.cropCycleId,
    required this.amountEtb,
    required this.productCategory,
    required this.productName,
    required this.sequenceOrder,
    required this.status,
    required this.validUntil,
    this.redeemedAt,
    this.redeemedMerchantId,
    required this.createdAt,
  });

  factory VoucherModel.fromJson(Map<String, dynamic> json) {
    return VoucherModel(
      id:               json['id']?.toString() ?? '',
      voucherCode:      json['voucherCode']?.toString() ?? '',
      investmentId:     json['investmentId']?.toString() ?? '',
      farmId:           json['farmId']?.toString() ?? '',
      farmerId:         json['farmerId']?.toString() ?? '',
      inputNeedId:      json['inputNeedId']?.toString() ?? '',
      cropCycleId:      json['cropCycleId']?.toString() ?? '',
      amountEtb:        (json['amountEtb'] ?? 0).toDouble(),
      productCategory:  json['productCategory']?.toString() ?? 'OTHER',
      productName:      json['productName']?.toString() ?? 'Agricultural Input',
      // sequenceOrder not in API yet — default 1 until backend adds it
      sequenceOrder:    json['sequenceOrder'] ?? 1,
      status:           json['status']?.toString() ?? '',
      // API uses expiresAt, SRS uses validUntil — handle both
      validUntil:       json['expiresAt']?.toString()
          ?? json['validUntil']?.toString()
          ?? json['valid_until']?.toString()
          ?? '',
      redeemedAt:       json['redeemedAt']?.toString(),
      redeemedMerchantId: json['merchantId']?.toString(),
      createdAt:        json['createdAt']?.toString() ?? '',
    );
  }

  Map<String, dynamic> toJson() => {
    'id':                 id,
    'voucherCode':        voucherCode,
    'investmentId':       investmentId,
    'farmId':             farmId,
    'farmerId':           farmerId,
    'inputNeedId':        inputNeedId,
    'cropCycleId':        cropCycleId,
    'amountEtb':          amountEtb,
    'productCategory':    productCategory,
    'productName':        productName,
    'sequenceOrder':      sequenceOrder,
    'status':             status,
    'expiresAt':          validUntil,
    'redeemedAt':         redeemedAt,
    'merchantId':         redeemedMerchantId,
    'createdAt':          createdAt,
  };

  // ── Status helpers ─────────────────────────────────────────────────────────
  bool get isActive    => status == 'ACTIVE';
  bool get isRedeemed  => status == 'REDEEMED';
  bool get isLocked    => status == 'GENERATED';   // GENERATED = waiting for sequence
  bool get isExpired   => status == 'EXPIRED';
  bool get isCancelled => status == 'CANCELLED';
  bool get isUsable    => isActive;

  // ── Display helpers ────────────────────────────────────────────────────────

  /// The code shown in the UI and used for USSD
  String get alphanumericCode => voucherCode;

  /// The description shown in the card
  String get productDescription => productName;

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
      redeemedValueEtb: vouchers
          .where((v) => v.isRedeemed)
          .fold(0, (s, v) => s + v.amountEtb),
      pendingValueEtb:  vouchers
          .where((v) => v.isActive || v.isLocked)
          .fold(0, (s, v) => s + v.amountEtb),
    );
  }

  int get redemptionPct =>
      totalVouchers == 0 ? 0 : ((redeemedCount / totalVouchers) * 100).round();
}
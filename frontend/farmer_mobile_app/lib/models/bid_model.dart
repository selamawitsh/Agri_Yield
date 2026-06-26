class BidModel {
  final String id;
  final String offtakerId;
  final String farmId;
  final String? cropCycleId;
  final double quantityQuintals;
  final double pricePerQuintalEtb;
  final double totalValueEtb;
  final double bidDepositEtb;
  final String status;
  final String? expiresAt;
  final String? acceptedAt;
  final String? createdAt;
  final String? agreementId;

  BidModel({
    required this.id,
    required this.offtakerId,
    required this.farmId,
    this.cropCycleId,
    required this.quantityQuintals,
    required this.pricePerQuintalEtb,
    required this.totalValueEtb,
    required this.bidDepositEtb,
    required this.status,
    this.expiresAt,
    this.acceptedAt,
    this.createdAt,
    this.agreementId,
  });

  factory BidModel.fromJson(Map<String, dynamic> json) {
    final qty = _toDouble(json['quantityQuintals']);
    final price = _toDouble(json['pricePerQuintalEtb']);
    // Compute locally if backend returns null (GENERATED ALWAYS columns
    // are not refreshed after INSERT by JPA unless @Column(insertable=false)
    // and a fresh SELECT is done — calculate here as fallback)
    final total = _toDouble(json['totalValueEtb']) > 0
        ? _toDouble(json['totalValueEtb'])
        : qty * price;
    final deposit = _toDouble(json['bidDepositEtb']) > 0
        ? _toDouble(json['bidDepositEtb'])
        : total * 0.10;

    return BidModel(
      id: json['id']?.toString() ?? '',
      offtakerId: json['offtakerId']?.toString() ?? '',
      farmId: json['farmId']?.toString() ?? '',
      cropCycleId: json['cropCycleId']?.toString(),
      quantityQuintals: qty,
      pricePerQuintalEtb: price,
      totalValueEtb: total,
      bidDepositEtb: deposit,
      status: json['status']?.toString() ?? 'PENDING',
      expiresAt: json['expiresAt']?.toString(),
      acceptedAt: json['acceptedAt']?.toString(),
      createdAt: json['createdAt']?.toString(),
      agreementId: json['agreementId']?.toString(),
    );
  }

  static double _toDouble(dynamic v) {
    if (v == null) return 0.0;
    if (v is double) return v;
    if (v is int) return v.toDouble();
    return double.tryParse(v.toString()) ?? 0.0;
  }

  bool get isPending => status == 'PENDING';
  bool get isAccepted => status == 'ACCEPTED';
  bool get isRejected => status == 'REJECTED';
  bool get isContractSigned => status == 'CONTRACT_SIGNED';
  bool get isCompleted => status == 'COMPLETED';
  bool get isDefaulted => status == 'DEFAULTED';
  bool get isExpired => status == 'EXPIRED';
  bool get isActive => isPending || isAccepted || isContractSigned;

  bool get isExpiringSoon {
    if (expiresAt == null) return false;
    try {
      final expiry = DateTime.parse(expiresAt!);
      return expiry.difference(DateTime.now()).inHours < 48 && !isExpired;
    } catch (_) {
      return false;
    }
  }
}

class DispatchModel {
  final String id;
  final String agreementId;
  final String driverFaydaId;
  final int truckCount;
  final String? scheduledPickupDate;
  final String? actualPickupDate;
  final double driverPenaltyEscrowEtb;
  final String status;
  final String? createdAt;

  DispatchModel({
    required this.id,
    required this.agreementId,
    required this.driverFaydaId,
    required this.truckCount,
    this.scheduledPickupDate,
    this.actualPickupDate,
    required this.driverPenaltyEscrowEtb,
    required this.status,
    this.createdAt,
  });

  factory DispatchModel.fromJson(Map<String, dynamic> json) {
    return DispatchModel(
      id: json['id']?.toString() ?? '',
      agreementId: json['agreementId']?.toString() ?? '',
      driverFaydaId: json['driverFaydaId']?.toString() ?? '',
      truckCount: json['truckCount'] is int ? json['truckCount'] : int.tryParse(json['truckCount'].toString()) ?? 1,
      scheduledPickupDate: json['scheduledPickupDate']?.toString(),
      actualPickupDate: json['actualPickupDate']?.toString(),
      driverPenaltyEscrowEtb: _toDouble(json['driverPenaltyEscrowEtb']),
      status: json['status']?.toString() ?? 'SCHEDULED',
      createdAt: json['createdAt']?.toString(),
    );
  }

  static double _toDouble(dynamic v) {
    if (v == null) return 0.0;
    if (v is double) return v;
    if (v is int) return v.toDouble();
    return double.tryParse(v.toString()) ?? 0.0;
  }

  bool get isScheduled => status == 'SCHEDULED';
  bool get isArrived => status == 'ARRIVED';
  bool get isLoaded => status == 'LOADED';
  bool get isDelivered => status == 'DELIVERED';
  bool get isDefaulted => status == 'DRIVER_DEFAULTED';
}

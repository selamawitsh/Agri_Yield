class FarmModel {
  final String id;
  final String farmerId;
  final String? farmName;
  final String cropType;
  final double areaHectares;
  final String status;
  final String kebeleCode;
  final String region;
  final double gpsCentroidLat;
  final double gpsCentroidLng;
  final bool satelliteVerified;
  final String? satelliteVerifiedAt;
  final String createdAt;
  final String updatedAt;

  FarmModel({
    required this.id,
    required this.farmerId,
    this.farmName,
    required this.cropType,
    required this.areaHectares,
    required this.status,
    required this.kebeleCode,
    required this.region,
    required this.gpsCentroidLat,
    required this.gpsCentroidLng,
    required this.satelliteVerified,
    this.satelliteVerifiedAt,
    required this.createdAt,
    required this.updatedAt,
  });

  factory FarmModel.fromJson(Map<String, dynamic> json) {
    return FarmModel(
      id: json['id'] ?? '',
      farmerId: json['farmerId'] ?? '',
      farmName: json['farmName'],
      cropType: json['cropType'] ?? '',
      areaHectares: (json['areaHectares'] ?? 0).toDouble(),
      status: json['status'] ?? '',
      kebeleCode: json['kebeleCode'] ?? '',
      region: json['region'] ?? '',
      gpsCentroidLat: (json['gpsCentroidLat'] ?? 0).toDouble(),
      gpsCentroidLng: (json['gpsCentroidLng'] ?? 0).toDouble(),
      satelliteVerified: json['satelliteVerified'] ?? false,
      satelliteVerifiedAt: json['satelliteVerifiedAt'],
      createdAt: json['createdAt'] ?? '',
      updatedAt: json['updatedAt'] ?? '',
    );
  }

  // Human-readable status label
  String get statusLabel {
    switch (status) {
      case 'PENDING_VERIFICATION': return 'Pending Verification';
      case 'VERIFIED': return 'Verified';
      case 'ACTIVE': return 'Active';
      case 'GROWING': return 'Growing';
      case 'HARVESTED': return 'Harvested';
      case 'DORMANT': return 'Dormant';
      case 'FAILED': return 'Failed';
      default: return status;
    }
  }

  // Color coding per status
  String get statusColor {
    switch (status) {
      case 'PENDING_VERIFICATION': return 'orange';
      case 'VERIFIED': return 'blue';
      case 'ACTIVE': return 'green';
      case 'GROWING': return 'green';
      case 'HARVESTED': return 'teal';
      case 'DORMANT': return 'grey';
      case 'FAILED': return 'red';
      default: return 'grey';
    }
  }

  String get displayName => farmName ?? 'Farm ${id.substring(0, 8)}';
}

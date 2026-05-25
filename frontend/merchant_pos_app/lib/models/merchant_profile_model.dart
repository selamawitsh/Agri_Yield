class MerchantProfile {
  final String id;
  final String userId;
  final String businessName;
  final String businessLicenseNumber;
  final double storeGpsLat;
  final double storeGpsLng;
  final bool isPhysicallyVerified;
  final String subscriptionTier;
  final String telebirrAccount;
  final String? physicallyVerifiedAt;
  final String createdAt;

  MerchantProfile({
    required this.id,
    required this.userId,
    required this.businessName,
    required this.businessLicenseNumber,
    required this.storeGpsLat,
    required this.storeGpsLng,
    required this.isPhysicallyVerified,
    required this.subscriptionTier,
    required this.telebirrAccount,
    this.physicallyVerifiedAt,
    required this.createdAt,
  });

  factory MerchantProfile.fromJson(Map<String, dynamic> json) {
    return MerchantProfile(
      id: json['id'] ?? '',
      userId: json['userId'] ?? '',
      businessName: json['businessName'] ?? '',
      businessLicenseNumber: json['businessLicenseNumber'] ?? '',
      storeGpsLat: (json['storeGpsLat'] ?? 0).toDouble(),
      storeGpsLng: (json['storeGpsLng'] ?? 0).toDouble(),
      isPhysicallyVerified: json['isPhysicallyVerified'] ?? false,
      subscriptionTier: json['subscriptionTier'] ?? 'BASIC',
      telebirrAccount: json['telebirrAccount'] ?? '',
      physicallyVerifiedAt: json['physicallyVerifiedAt'],
      createdAt: json['createdAt'] ?? '',
    );
  }
}

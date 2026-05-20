class UserModel {
  final String id;
  final String phone;
  final String? email;
  final String fullName;
  final String role;
  final String kycStatus;
  final String preferredLanguage;
  final String? businessName;
  final String? businessLicenseNumber;
  
  UserModel({
    required this.id,
    required this.phone,
    this.email,
    required this.fullName,
    required this.role,
    required this.kycStatus,
    required this.preferredLanguage,
    this.businessName,
    this.businessLicenseNumber,
  });
  
  factory UserModel.fromJson(Map<String, dynamic> json) {
    return UserModel(
      id: json['id'],
      phone: json['phone'],
      email: json['email'],
      fullName: json['fullName'] ?? '',
      role: json['role'],
      kycStatus: json['kycStatus'] ?? 'PENDING',
      preferredLanguage: json['preferredLanguage'] ?? 'am',
      businessName: json['businessName'],
      businessLicenseNumber: json['businessLicenseNumber'],
    );
  }
}

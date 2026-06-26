class AgreementModel {
  final String id;
  final String bidId;
  final String? contractHash;
  final String? contractPdfUrl;
  final String? farmerSignedAt;
  final String? offtakerSignedAt;
  final bool fullyExecuted;
  final String? createdAt;

  AgreementModel({
    required this.id,
    required this.bidId,
    this.contractHash,
    this.contractPdfUrl,
    this.farmerSignedAt,
    this.offtakerSignedAt,
    required this.fullyExecuted,
    this.createdAt,
  });

  factory AgreementModel.fromJson(Map<String, dynamic> json) {
    return AgreementModel(
      id: json['id']?.toString() ?? '',
      bidId: json['bidId']?.toString() ?? '',
      contractHash: json['contractHash']?.toString(),
      contractPdfUrl: json['contractPdfUrl']?.toString(),
      farmerSignedAt: json['farmerSignedAt']?.toString(),
      offtakerSignedAt: json['offtakerSignedAt']?.toString(),
      fullyExecuted: json['fullyExecuted'] == true,
      createdAt: json['createdAt']?.toString(),
    );
  }

  bool get farmerHasSigned => farmerSignedAt != null;
  bool get offtakerHasSigned => offtakerSignedAt != null;
}

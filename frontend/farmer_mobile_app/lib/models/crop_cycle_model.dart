class CropCycleModel {
  final String id;
  final String farmId;
  final String seasonName;
  final String? plantingDate;
  final String expectedHarvestDate;
  final String? actualHarvestDate;
  final String status;
  final String createdAt;

  CropCycleModel({
    required this.id,
    required this.farmId,
    required this.seasonName,
    this.plantingDate,
    required this.expectedHarvestDate,
    this.actualHarvestDate,
    required this.status,
    required this.createdAt,
  });

  factory CropCycleModel.fromJson(Map<String, dynamic> json) {
    return CropCycleModel(
      id: json['id'] ?? '',
      farmId: json['farmId'] ?? '',
      seasonName: json['seasonName'] ?? '',
      plantingDate: json['plantingDate'],
      expectedHarvestDate: json['expectedHarvestDate'] ?? '',
      actualHarvestDate: json['actualHarvestDate'],
      status: json['status'] ?? '',
      createdAt: json['createdAt'] ?? '',
    );
  }

  String get statusLabel {
    switch (status) {
      case 'PLANNING': return 'Planning';
      case 'FUNDED': return 'Funded';
      case 'PLANTED': return 'Planted';
      case 'GROWING': return 'Growing';
      case 'HARVESTED': return 'Harvested';
      case 'FAILED': return 'Failed';
      default: return status;
    }
  }

  bool get isPlanted => status == 'PLANTED' || status == 'GROWING';
  bool get canConfirmPlanting => status == 'PLANNING' || status == 'FUNDED';
}

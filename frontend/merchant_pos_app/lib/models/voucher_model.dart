class RedemptionRecord {
  final String id;
  final String voucherId;
  final String merchantId;
  final double merchantGpsLat;
  final double merchantGpsLng;
  final List<DispensedProduct> productsDispensed;
  final String paymentReference;
  final String scanTimestamp;
 
  RedemptionRecord({
    required this.id,
    required this.voucherId,
    required this.merchantId,
    required this.merchantGpsLat,
    required this.merchantGpsLng,
    required this.productsDispensed,
    required this.paymentReference,
    required this.scanTimestamp,
  });
 
  factory RedemptionRecord.fromJson(Map<String, dynamic> json) {
    return RedemptionRecord(
      id: json['id'] ?? '',
      voucherId: json['voucherId'] ?? '',
      merchantId: json['merchantId'] ?? '',
      merchantGpsLat: (json['merchantGpsLat'] ?? 0).toDouble(),
      merchantGpsLng: (json['merchantGpsLng'] ?? 0).toDouble(),
      productsDispensed: (json['productsDispensed'] as List<dynamic>? ?? [])
          .map((e) => DispensedProduct.fromJson(e))
          .toList(),
      paymentReference: json['paymentReference'] ?? '',
      scanTimestamp: json['scanTimestamp'] ?? '',
    );
  }
}
 
class DispensedProduct {
  final String productName;
  final String quantity;
  final String unit;
  final String lotNumber;
 
  DispensedProduct({
    required this.productName,
    required this.quantity,
    required this.unit,
    required this.lotNumber,
  });
 
  factory DispensedProduct.fromJson(Map<String, dynamic> json) {
    return DispensedProduct(
      productName: json['product_name'] ?? '',
      quantity: json['quantity']?.toString() ?? '',
      unit: json['unit'] ?? '',
      lotNumber: json['lot_number'] ?? '',
    );
  }
}
 
class VoucherRedemptionResult {
  final bool success;
  final String message;
  final String? voucherId;
  final String? farmerName;
  final String? productDescription;
  final String? productCategory;
  final double? amountEtb;
  final String? merchantName;
  final String? paymentReference;
  final String? rejectionReason;
  final String? fraudSeverity;
 
  VoucherRedemptionResult({
    required this.success,
    required this.message,
    this.voucherId,
    this.farmerName,
    this.productDescription,
    this.productCategory,
    this.amountEtb,
    this.merchantName,
    this.paymentReference,
    this.rejectionReason,
    this.fraudSeverity,
  });
 
  factory VoucherRedemptionResult.fromJson(Map<String, dynamic> json) {
    final data = json['data'] as Map<String, dynamic>? ?? {};
    return VoucherRedemptionResult(
      success: json['success'] ?? false,
      message: json['message'] ?? '',
      voucherId: data['voucher_id'],
      farmerName: data['farmer_name'],
      productDescription: data['product_description'],
      productCategory: data['product_category'],
      amountEtb: (data['amount_etb'] as num?)?.toDouble(),
      merchantName: data['merchant_name'],
      paymentReference: data['payment_reference'],
      rejectionReason: data['rejection_reason'],
      fraudSeverity: data['fraud_severity'],
    );
  }
 
  factory VoucherRedemptionResult.error(String message) {
    return VoucherRedemptionResult(success: false, message: message);
  }
}
 
class MerchantRedemptionSummary {
  final String voucherId;
  final String farmerName;
  final String productCategory;
  final String productDescription;
  final double amountEtb;
  final String redeemedAt;
  final String paymentReference;
  final String status;
 
  MerchantRedemptionSummary({
    required this.voucherId,
    required this.farmerName,
    required this.productCategory,
    required this.productDescription,
    required this.amountEtb,
    required this.redeemedAt,
    required this.paymentReference,
    required this.status,
  });
 
  factory MerchantRedemptionSummary.fromJson(Map<String, dynamic> json) {
    return MerchantRedemptionSummary(
      voucherId: json['voucherId'] ?? '',
      farmerName: json['farmerName'] ?? '',
      productCategory: json['productCategory'] ?? '',
      productDescription: json['productDescription'] ?? '',
      amountEtb: (json['amountEtb'] as num?)?.toDouble() ?? 0,
      redeemedAt: json['redeemedAt'] ?? '',
      paymentReference: json['paymentReference'] ?? '',
      status: json['status'] ?? 'COMPLETED',
    );
  }
}

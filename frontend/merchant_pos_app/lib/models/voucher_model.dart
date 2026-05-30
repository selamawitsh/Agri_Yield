// lib/models/voucher_model.dart
// Matches VoucherRedemptionResponse from VoucherController.java

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
      id:               json['id']?.toString() ?? '',
      voucherId:        json['voucherId']?.toString() ?? '',
      merchantId:       json['merchantId']?.toString() ?? '',
      merchantGpsLat:   (json['merchantGpsLat'] ?? 0).toDouble(),
      merchantGpsLng:   (json['merchantGpsLng'] ?? 0).toDouble(),
      productsDispensed: (json['productsDispensed'] as List<dynamic>? ?? [])
          .map((e) => DispensedProduct.fromJson(e))
          .toList(),
      paymentReference: json['paymentReference']?.toString() ?? '',
      scanTimestamp:    json['redeemedAt']?.toString()
          ?? json['scanTimestamp']?.toString() ?? '',
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
      productName: json['product_name']?.toString() ?? '',
      quantity:    json['quantity']?.toString() ?? '',
      unit:        json['unit']?.toString() ?? '',
      lotNumber:   json['lot_number']?.toString() ?? '',
    );
  }
}

/// Result returned after merchant scans and redeems a voucher.
/// Maps to VoucherRedemptionResponse from the backend.
class VoucherRedemptionResult {
  final bool success;
  final String message;

  // From VoucherRedemptionResponse (the redemption record)
  final String? redemptionId;
  final String? voucherId;
  final String? merchantId;
  final double? amountEtb;
  final bool escrowReleased;
  final String? notes;
  final String? redeemedAt;

  // Extra display fields — fetched from voucher lookup or enriched by backend
  final String? farmerName;
  final String? productDescription;
  final String? productCategory;
  final String? merchantName;
  final String? paymentReference;

  // Rejection fields
  final String? rejectionReason;
  final String? fraudSeverity;

  VoucherRedemptionResult({
    required this.success,
    required this.message,
    this.redemptionId,
    this.voucherId,
    this.merchantId,
    this.amountEtb,
    this.escrowReleased = false,
    this.notes,
    this.redeemedAt,
    this.farmerName,
    this.productDescription,
    this.productCategory,
    this.merchantName,
    this.paymentReference,
    this.rejectionReason,
    this.fraudSeverity,
  });

  /// Parse from POST /api/v1/vouchers/redeem response.
  /// Backend wraps result in ApiResponse<VoucherRedemptionResponse>.
  factory VoucherRedemptionResult.fromJson(Map<String, dynamic> json) {
    final success = json['success'] == true;
    final message = json['message']?.toString() ?? '';

    if (!success) {
      // Parse rejection details from error response
      final data = json['data'] as Map<String, dynamic>? ?? {};
      return VoucherRedemptionResult(
        success: false,
        message: message,
        rejectionReason: data['rejectionReason']?.toString()
            ?? data['rejection_reason']?.toString()
            ?? message,
        fraudSeverity:   data['fraudSeverity']?.toString()
            ?? data['fraud_severity']?.toString(),
      );
    }

    // Success — data is VoucherRedemptionResponse
    final data = json['data'] as Map<String, dynamic>? ?? {};

    return VoucherRedemptionResult(
      success:         true,
      message:         message,
      redemptionId:    data['id']?.toString(),
      voucherId:       data['voucherId']?.toString(),
      merchantId:      data['merchantId']?.toString(),
      // amountEtb comes from VoucherRedemptionResponse
      amountEtb:       (data['amountEtb'] as num?)?.toDouble(),
      escrowReleased:  data['escrowReleased'] == true,
      notes:           data['notes']?.toString(),
      redeemedAt:      data['redeemedAt']?.toString(),
      // These may not be in the redemption response yet —
      // display fallback text if missing
      farmerName:      data['farmerName']?.toString() ?? 'Farmer',
      productDescription: data['productDescription']?.toString()
          ?? data['productName']?.toString()
          ?? 'Agricultural Input',
      productCategory: data['productCategory']?.toString() ?? 'OTHER',
      merchantName:    data['merchantName']?.toString(),
      paymentReference: data['paymentReference']?.toString()
          ?? data['id']?.toString(), // use redemption ID as ref fallback
    );
  }

  factory VoucherRedemptionResult.error(String message) {
    return VoucherRedemptionResult(
      success: false,
      message: message,
      rejectionReason: message,
    );
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
      voucherId:          json['voucherId']?.toString() ?? '',
      farmerName:         json['farmerName']?.toString() ?? 'Farmer',
      productCategory:    json['productCategory']?.toString() ?? '',
      productDescription: json['productDescription']?.toString()
          ?? json['productName']?.toString() ?? '',
      amountEtb:          (json['amountEtb'] as num?)?.toDouble() ?? 0,
      redeemedAt:         json['redeemedAt']?.toString() ?? '',
      paymentReference:   json['paymentReference']?.toString() ?? '',
      status:             json['status']?.toString() ?? 'COMPLETED',
    );
  }
}
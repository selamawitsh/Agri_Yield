import 'package:flutter/material.dart';

class ScanRejectionScreen extends StatelessWidget {
  final String reason;
  const ScanRejectionScreen({super.key, required this.reason});

  String _friendlyReason(String reason) {
    switch (reason) {
      case 'INVALID_SIGNATURE':
        return 'This QR code is invalid or has been tampered with.';
      case 'DUPLICATE_SCAN':
        return 'This voucher has already been redeemed. Duplicate scan detected.';
      case 'CATEGORY_MISMATCH':
        return 'This voucher is for a different product category. You are not certified to redeem it.';
      case 'MERCHANT_TOO_FAR':
        return 'Your store is too far from the farmer\'s farm. Distance exceeds 50km limit.';
      case 'VOUCHER_EXPIRED':
        return 'This voucher has expired and is no longer valid.';
      case 'PRECEDING_VOUCHER_NOT_REDEEMED':
        return 'A previous voucher in this sequence must be redeemed first.';
      default:
        return reason.isNotEmpty ? reason : 'Voucher validation failed. Please try again.';
    }
  }

  String _guidance(String reason) {
    switch (reason) {
      case 'INVALID_SIGNATURE':
        return 'Do not accept this voucher. Report to Agri-Yield admin.';
      case 'DUPLICATE_SCAN':
        return 'Ask the farmer to show a different voucher. This one was already used.';
      case 'CATEGORY_MISMATCH':
        return 'Direct the farmer to a merchant certified for this product type.';
      case 'MERCHANT_TOO_FAR':
        return 'This farmer must visit a merchant closer to their farm.';
      case 'VOUCHER_EXPIRED':
        return 'Advise the farmer to contact Agri-Yield support for a replacement.';
      case 'PRECEDING_VOUCHER_NOT_REDEEMED':
        return 'The farmer must first redeem earlier vouchers in the correct order.';
      default:
        return 'Contact Agri-Yield support if the issue persists.';
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFC62828),
      body: SafeArea(
        child: Padding(
          padding: const EdgeInsets.all(32),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              const Icon(Icons.cancel, size: 100, color: Colors.white),
              const SizedBox(height: 24),
              const Text('Voucher Rejected',
                  style: TextStyle(
                      fontSize: 32,
                      fontWeight: FontWeight.bold,
                      color: Colors.white)),
              const SizedBox(height: 32),
              Container(
                width: double.infinity,
                padding: const EdgeInsets.all(20),
                decoration: BoxDecoration(
                  color: Colors.white.withOpacity(0.15),
                  borderRadius: BorderRadius.circular(16),
                  border: Border.all(color: Colors.white30),
                ),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    const Text('Reason',
                        style: TextStyle(
                            color: Colors.white70,
                            fontSize: 12,
                            fontWeight: FontWeight.w600,
                            letterSpacing: 1.2)),
                    const SizedBox(height: 8),
                    Text(_friendlyReason(reason),
                        style: const TextStyle(
                            color: Colors.white,
                            fontSize: 16,
                            fontWeight: FontWeight.w500)),
                    const SizedBox(height: 16),
                    const Divider(color: Colors.white30),
                    const SizedBox(height: 8),
                    const Text('What to do',
                        style: TextStyle(
                            color: Colors.white70,
                            fontSize: 12,
                            fontWeight: FontWeight.w600,
                            letterSpacing: 1.2)),
                    const SizedBox(height: 8),
                    Text(_guidance(reason),
                        style: const TextStyle(
                            color: Colors.white70, fontSize: 14)),
                  ],
                ),
              ),
              const SizedBox(height: 16),
              const Text(
                'Do NOT dispense any product for rejected vouchers',
                style: TextStyle(
                    color: Colors.white,
                    fontSize: 13,
                    fontWeight: FontWeight.bold),
                textAlign: TextAlign.center,
              ),
              const SizedBox(height: 32),
              SizedBox(
                width: double.infinity,
                child: ElevatedButton(
                  onPressed: () => Navigator.pop(context),
                  style: ElevatedButton.styleFrom(
                    backgroundColor: Colors.white,
                    padding: const EdgeInsets.symmetric(vertical: 16),
                  ),
                  child: const Text('Back to Scanner',
                      style: TextStyle(
                          color: Color(0xFFC62828),
                          fontSize: 16,
                          fontWeight: FontWeight.bold)),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}

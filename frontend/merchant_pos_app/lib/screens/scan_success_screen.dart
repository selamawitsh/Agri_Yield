import 'package:flutter/material.dart';

class ScanSuccessScreen extends StatelessWidget {
  final dynamic data;
  const ScanSuccessScreen({super.key, required this.data});

  @override
  Widget build(BuildContext context) {
    final farmerName   = data?['farmer_name'] ?? 'Farmer';
    final product      = data?['product_description'] ?? 'Agricultural Input';
    final category     = data?['product_category'] ?? '';
    final amount       = data?['amount_etb']?.toString() ?? '0';
    final merchant     = data?['merchant_name'] ?? '';

    return Scaffold(
      backgroundColor: const Color(0xFF2E7D32),
      body: SafeArea(
        child: Padding(
          padding: const EdgeInsets.all(32),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              const Icon(Icons.check_circle, size: 100, color: Colors.white),
              const SizedBox(height: 24),
              const Text('Voucher Accepted!',
                  style: TextStyle(
                      fontSize: 32,
                      fontWeight: FontWeight.bold,
                      color: Colors.white)),
              const SizedBox(height: 32),
              _buildInfoCard(farmerName, product, category, amount, merchant),
              const SizedBox(height: 32),
              const Text(
                'Payment has been released to your Telebirr account',
                style: TextStyle(color: Colors.white70, fontSize: 14),
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
                  child: const Text('Scan Next Voucher',
                      style: TextStyle(
                          color: Color(0xFF2E7D32),
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

  Widget _buildInfoCard(String farmer, String product, String category,
      String amount, String merchant) {
    return Container(
      width: double.infinity,
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        color: Colors.white.withOpacity(0.15),
        borderRadius: BorderRadius.circular(16),
        border: Border.all(color: Colors.white30),
      ),
      child: Column(
        children: [
          _row('Farmer', farmer),
          const Divider(color: Colors.white30),
          _row('Product', product),
          const Divider(color: Colors.white30),
          _row('Category', category),
          const Divider(color: Colors.white30),
          _row('Amount', 'ETB $amount',
              valueStyle: const TextStyle(
                  color: Colors.white,
                  fontSize: 22,
                  fontWeight: FontWeight.bold)),
        ],
      ),
    );
  }

  Widget _row(String label, String value, {TextStyle? valueStyle}) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 6),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Text(label,
              style: const TextStyle(color: Colors.white70, fontSize: 14)),
          Text(value,
              style: valueStyle ??
                  const TextStyle(
                      color: Colors.white,
                      fontSize: 16,
                      fontWeight: FontWeight.w600)),
        ],
      ),
    );
  }
}

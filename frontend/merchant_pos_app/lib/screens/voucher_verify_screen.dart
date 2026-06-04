import 'package:flutter/material.dart';
import '../services/voucher_service.dart';
import '../widgets/voucher_category_badge.dart';
import 'scanner_screen.dart';

class VoucherVerifyScreen extends StatefulWidget {
  const VoucherVerifyScreen({super.key});

  @override
  State<VoucherVerifyScreen> createState() =>
      _VoucherVerifyScreenState();
}

class _VoucherVerifyScreenState extends State<VoucherVerifyScreen> {
  final _voucherService  = VoucherService();
  final _codeCtrl        = TextEditingController();
  Map<String, dynamic>?  _voucherData;
  bool   _loading        = false;
  String _error          = '';

  @override
  void dispose() {
    _codeCtrl.dispose();
    super.dispose();
  }

  Future<void> _verify() async {
    final code = _codeCtrl.text.trim();
    if (code.isEmpty) return;

    setState(() { _loading = true; _error = ''; _voucherData = null; });

    final data = await _voucherService.getVoucherByCode(code);

    if (mounted) {
      setState(() {
        _loading     = false;
        _voucherData = data;
        if (data == null) {
          _error = 'Voucher not found. Check the code and try again.';
        }
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Verify Voucher'),
        backgroundColor: Colors.orange,
        foregroundColor: Colors.white,
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Explanation banner
            Container(
              width: double.infinity,
              padding: const EdgeInsets.all(16),
              decoration: BoxDecoration(
                color: Colors.blue.withOpacity(0.05),
                borderRadius: BorderRadius.circular(12),
                border:
                    Border.all(color: Colors.blue.withOpacity(0.2)),
              ),
              child: const Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text('Check Before You Dispense',
                      style: TextStyle(
                          fontWeight: FontWeight.bold,
                          color: Colors.blue,
                          fontSize: 15)),
                  SizedBox(height: 8),
                  Text(
                    'Use this screen to verify a voucher is genuine and '
                    'not already redeemed — before handing over any product. '
                    'Verification does NOT redeem the voucher.',
                    style: TextStyle(
                        color: Colors.blue, fontSize: 13),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 20),

            // Code input
            const Text('Enter Voucher Code',
                style: TextStyle(
                    fontWeight: FontWeight.bold, fontSize: 15)),
            const SizedBox(height: 8),
            Row(children: [
              Expanded(
                child: TextField(
                  controller: _codeCtrl,
                  textCapitalization: TextCapitalization.characters,
                  style: const TextStyle(
                      fontFamily: 'monospace',
                      fontSize: 16,
                      letterSpacing: 1.5),
                  decoration: InputDecoration(
                    hintText: 'e.g. AGY-9E3F-9F19-9367',
                    border: OutlineInputBorder(
                        borderRadius: BorderRadius.circular(10)),
                    focusedBorder: OutlineInputBorder(
                      borderRadius: BorderRadius.circular(10),
                      borderSide: const BorderSide(
                          color: Colors.orange, width: 2),
                    ),
                    prefixIcon: const Icon(
                        Icons.confirmation_number_outlined,
                        color: Colors.orange),
                    suffixIcon: _codeCtrl.text.isNotEmpty
                        ? IconButton(
                            icon: const Icon(Icons.clear,
                                size: 18),
                            onPressed: () {
                              _codeCtrl.clear();
                              setState(() {
                                _voucherData = null;
                                _error = '';
                              });
                            })
                        : null,
                  ),
                  onSubmitted: (_) => _verify(),
                  onChanged: (_) => setState(() {}),
                ),
              ),
              const SizedBox(width: 10),
              ElevatedButton(
                onPressed: _loading ? null : _verify,
                style: ElevatedButton.styleFrom(
                  backgroundColor: Colors.orange,
                  padding: const EdgeInsets.symmetric(
                      horizontal: 20, vertical: 16),
                  shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(10)),
                ),
                child: _loading
                    ? const SizedBox(
                        width: 20,
                        height: 20,
                        child: CircularProgressIndicator(
                            color: Colors.white, strokeWidth: 2))
                    : const Text('Check',
                        style: TextStyle(color: Colors.white)),
              ),
            ]),
            const SizedBox(height: 8),
            TextButton.icon(
              onPressed: () => Navigator.push(
                context,
                MaterialPageRoute(
                    builder: (_) => const ScannerScreen()),
              ),
              icon: const Icon(Icons.qr_code_scanner,
                  color: Colors.orange, size: 18),
              label: const Text('Scan QR instead',
                  style: TextStyle(color: Colors.orange)),
            ),

            // Error
            if (_error.isNotEmpty) ...[
              const SizedBox(height: 16),
              Container(
                width: double.infinity,
                padding: const EdgeInsets.all(14),
                decoration: BoxDecoration(
                  color: Colors.red.shade50,
                  borderRadius: BorderRadius.circular(10),
                  border: Border.all(color: Colors.red.shade200),
                ),
                child: Row(children: [
                  Icon(Icons.error_outline,
                      color: Colors.red.shade700),
                  const SizedBox(width: 10),
                  Expanded(
                    child: Text(_error,
                        style: TextStyle(
                            color: Colors.red.shade700,
                            fontSize: 13)),
                  ),
                ]),
              ),
            ],

            // Result
            if (_voucherData != null) ...[
              const SizedBox(height: 20),
              _buildVoucherResult(_voucherData!),
            ],
          ],
        ),
      ),
    );
  }

  Widget _buildVoucherResult(Map<String, dynamic> data) {
    final status    = data['status']?.toString() ?? 'UNKNOWN';
    final isValid   = status == 'ACTIVE' || status == 'ISSUED';
    final isRedeemed = status == 'REDEEMED';

    final statusColor = isValid
        ? Colors.green
        : isRedeemed
            ? Colors.blue
            : Colors.red;
    final statusLabel = isValid
        ? 'Valid — Ready to Redeem'
        : isRedeemed
            ? 'Already Redeemed'
            : 'Not Valid ($status)';

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        // Status banner
        Container(
          width: double.infinity,
          padding: const EdgeInsets.all(16),
          decoration: BoxDecoration(
            color: statusColor.withOpacity(0.08),
            borderRadius: BorderRadius.circular(12),
            border: Border.all(
                color: statusColor.withOpacity(0.3)),
          ),
          child: Row(children: [
            Icon(
              isValid
                  ? Icons.verified
                  : isRedeemed
                      ? Icons.check_circle
                      : Icons.cancel,
              color: statusColor,
              size: 32,
            ),
            const SizedBox(width: 12),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(statusLabel,
                      style: TextStyle(
                          color: statusColor,
                          fontWeight: FontWeight.bold,
                          fontSize: 15)),
                  if (isValid)
                    const Text(
                      'This voucher has passed authenticity check',
                      style: TextStyle(
                          color: Colors.green, fontSize: 12),
                    ),
                  if (isRedeemed)
                    Text(
                      'Redeemed on: ${data['redeemedAt'] ?? 'unknown date'}',
                      style: const TextStyle(
                          color: Colors.blue, fontSize: 12),
                    ),
                ],
              ),
            ),
          ]),
        ),
        const SizedBox(height: 16),

        // Voucher details card
        Container(
          width: double.infinity,
          padding: const EdgeInsets.all(16),
          decoration: BoxDecoration(
            color: Colors.white,
            borderRadius: BorderRadius.circular(12),
            border: Border.all(color: Colors.grey.shade200),
          ),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              const Text('Voucher Details',
                  style: TextStyle(
                      fontWeight: FontWeight.bold,
                      fontSize: 15)),
              const SizedBox(height: 12),
              if (data['voucherCode'] != null)
                _row('Voucher Code',
                    data['voucherCode'].toString(),
                    mono: true),
              if (data['farmerName'] != null)
                _row('Farmer', data['farmerName'].toString()),
              if (data['productCategory'] != null) ...[
                const SizedBox(height: 4),
                Row(children: [
                  const SizedBox(
                    width: 120,
                    child: Text('Category',
                        style:
                            TextStyle(color: Colors.grey)),
                  ),
                  VoucherCategoryBadge(
                      category:
                          data['productCategory'].toString()),
                ]),
                const SizedBox(height: 4),
              ],
              if (data['productDescription'] != null)
                _row('Product',
                    data['productDescription'].toString()),
              if (data['amountEtb'] != null)
                _row('Amount',
                    'ETB ${(data['amountEtb'] as num).toStringAsFixed(2)}'),
              if (data['expiresAt'] != null)
                _row('Expires', data['expiresAt'].toString()),
              if (data['investmentId'] != null)
                _row('Investment ID',
                    data['investmentId'].toString(),
                    mono: true),
            ],
          ),
        ),

        // Proceed to redeem button — only shown if valid
        if (isValid) ...[
          const SizedBox(height: 16),
          SizedBox(
            width: double.infinity,
            child: ElevatedButton.icon(
              onPressed: () {
                Navigator.pushReplacement(
                  context,
                  MaterialPageRoute(
                      builder: (_) => const ScannerScreen()),
                );
              },
              icon: const Icon(Icons.qr_code_scanner,
                  color: Colors.white),
              label: const Text(
                'Proceed to Scan and Redeem',
                style: TextStyle(
                    color: Colors.white, fontSize: 15),
              ),
              style: ElevatedButton.styleFrom(
                backgroundColor: Colors.orange,
                padding:
                    const EdgeInsets.symmetric(vertical: 14),
                shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(10)),
              ),
            ),
          ),
        ],
      ],
    );
  }

  Widget _row(String label, String value,
      {bool mono = false}) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 6),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          SizedBox(
            width: 120,
            child: Text(label,
                style: const TextStyle(color: Colors.grey)),
          ),
          Expanded(
            child: Text(
              value,
              style: TextStyle(
                fontWeight: FontWeight.w500,
                fontFamily: mono ? 'monospace' : null,
              ),
            ),
          ),
        ],
      ),
    );
  }
}

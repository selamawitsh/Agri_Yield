import 'package:flutter/material.dart';
import 'package:mobile_scanner/mobile_scanner.dart';
import '../services/voucher_service.dart';
import 'scan_success_screen.dart';
import 'scan_rejection_screen.dart';

class ScannerScreen extends StatefulWidget {
  const ScannerScreen({super.key});

  @override
  State<ScannerScreen> createState() => _ScannerScreenState();
}

class _ScannerScreenState extends State<ScannerScreen> {
  final _voucherService = VoucherService();
  final _controller = MobileScannerController();
  final _manualController = TextEditingController();
  bool _processing = false;
  bool _torchOn = false;

  @override
  void dispose() {
    _controller.dispose();
    _manualController.dispose();
    super.dispose();
  }

  Future<void> _handleScan(String qrPayload) async {
    if (_processing) return;
    setState(() => _processing = true);
    _controller.stop();

    final result = await _voucherService.validateAndRedeem(qrPayload);

    if (!mounted) return;

    if (result['success'] == true) {
      await Navigator.push(
        context,
        MaterialPageRoute(
          builder: (_) => ScanSuccessScreen(data: result['data']),
        ),
      );
    } else {
      await Navigator.push(
        context,
        MaterialPageRoute(
          builder: (_) => ScanRejectionScreen(reason: result['message']),
        ),
      );
    }

    setState(() => _processing = false);
    _controller.start();
  }

  void _showManualEntry() {
    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      builder: (ctx) => Padding(
        padding: EdgeInsets.only(
          bottom: MediaQuery.of(ctx).viewInsets.bottom,
          left: 24, right: 24, top: 24,
        ),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text('Enter Voucher Code',
                style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
            const SizedBox(height: 8),
            const Text('Enter the 12-character alphanumeric code',
                style: TextStyle(color: Colors.grey)),
            const SizedBox(height: 16),
            TextField(
              controller: _manualController,
              decoration: const InputDecoration(
                labelText: 'e.g. 8K4F-2M9P-3R7Q',
                border: OutlineInputBorder(),
                prefixIcon: Icon(Icons.keyboard),
              ),
              textCapitalization: TextCapitalization.characters,
            ),
            const SizedBox(height: 16),
            SizedBox(
              width: double.infinity,
              child: ElevatedButton(
                onPressed: () {
                  final code = _manualController.text.trim();
                  if (code.isNotEmpty) {
                    Navigator.pop(ctx);
                    _handleScan(code);
                  }
                },
                style: ElevatedButton.styleFrom(
                  backgroundColor: Colors.orange,
                  padding: const EdgeInsets.symmetric(vertical: 14),
                ),
                child: const Text('Validate Voucher',
                    style: TextStyle(color: Colors.white, fontSize: 16)),
              ),
            ),
            const SizedBox(height: 24),
          ],
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Scan Voucher'),
        backgroundColor: Colors.orange,
        foregroundColor: Colors.white,
        actions: [
          IconButton(
            icon: Icon(_torchOn ? Icons.flash_on : Icons.flash_off),
            onPressed: () {
              _controller.toggleTorch();
              setState(() => _torchOn = !_torchOn);
            },
          ),
        ],
      ),
      body: Stack(
        children: [
          MobileScanner(
            controller: _controller,
            onDetect: (capture) {
              final barcode = capture.barcodes.firstOrNull;
              if (barcode?.rawValue != null) {
                _handleScan(barcode!.rawValue!);
              }
            },
          ),
          // Overlay frame
          Center(
            child: Container(
              width: 260,
              height: 260,
              decoration: BoxDecoration(
                border: Border.all(color: Colors.orange, width: 3),
                borderRadius: BorderRadius.circular(12),
              ),
            ),
          ),
          // Bottom hint
          Positioned(
            bottom: 0,
            left: 0,
            right: 0,
            child: Container(
              color: Colors.black87,
              padding: const EdgeInsets.all(20),
              child: Column(
                children: [
                  const Text(
                    'Point camera at farmer\'s QR voucher',
                    style: TextStyle(color: Colors.white, fontSize: 16),
                    textAlign: TextAlign.center,
                  ),
                  const SizedBox(height: 12),
                  OutlinedButton.icon(
                    onPressed: _showManualEntry,
                    icon: const Icon(Icons.keyboard, color: Colors.orange),
                    label: const Text('Enter Code Manually',
                        style: TextStyle(color: Colors.orange)),
                    style: OutlinedButton.styleFrom(
                        side: const BorderSide(color: Colors.orange)),
                  ),
                ],
              ),
            ),
          ),
          if (_processing)
            Container(
              color: Colors.black54,
              child: const Center(
                child: Column(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    CircularProgressIndicator(color: Colors.orange),
                    SizedBox(height: 16),
                    Text('Validating voucher...',
                        style: TextStyle(color: Colors.white, fontSize: 16)),
                  ],
                ),
              ),
            ),
        ],
      ),
    );
  }
}

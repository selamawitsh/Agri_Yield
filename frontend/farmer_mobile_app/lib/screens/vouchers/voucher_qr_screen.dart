import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:qr_flutter/qr_flutter.dart';
import '../../models/voucher_model.dart';
import '../../widgets/voucher_category_chip.dart';

class VoucherQrScreen extends StatelessWidget {
  final VoucherModel voucher;

  const VoucherQrScreen({super.key, required this.voucher});

  static const _primary = Color(0xFF1B4332);

  @override
  Widget build(BuildContext context) {
    // Force portrait for QR display
    SystemChrome.setPreferredOrientations([DeviceOrientation.portraitUp]);

    return Scaffold(
      backgroundColor: _primary,
      appBar: AppBar(
        backgroundColor: _primary,
        foregroundColor: Colors.white,
        elevation: 0,
        title: const Text('Show to Merchant',
            style: TextStyle(fontWeight: FontWeight.w900, fontSize: 16)),
        leading: IconButton(
          icon: const Icon(Icons.close_rounded),
          onPressed: () {
            SystemChrome.setPreferredOrientations(DeviceOrientation.values);
            Navigator.pop(context);
          },
        ),
      ),
      body: SafeArea(
        child: SingleChildScrollView(
          padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 16),
          child: Column(
            children: [
              // Instructions
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 10),
                decoration: BoxDecoration(
                  color: Colors.white.withOpacity(0.08),
                  borderRadius: BorderRadius.circular(12),
                ),
                child: const Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Icon(Icons.qr_code_scanner_rounded,
                        color: Colors.white70, size: 16),
                    SizedBox(width: 8),
                    Text(
                      'Let the merchant scan this QR code',
                      style: TextStyle(color: Colors.white70, fontSize: 13),
                    ),
                  ],
                ),
              ),

              const SizedBox(height: 28),

              // QR Code
              Container(
                padding: const EdgeInsets.all(20),
                decoration: BoxDecoration(
                  color: Colors.white,
                  borderRadius: BorderRadius.circular(24),
                ),
                child: Column(
                  children: [
                    // Real QR — encodes voucher id + alphanumeric code
                    QrImageView(
                      data: '${voucher.id}|${voucher.alphanumericCode}',
                      version: QrVersions.auto,
                      size: 240,
                      eyeStyle: const QrEyeStyle(
                        eyeShape: QrEyeShape.square,
                        color: Color(0xFF1B4332),
                      ),
                      dataModuleStyle: const QrDataModuleStyle(
                        dataModuleShape: QrDataModuleShape.square,
                        color: Color(0xFF1B4332),
                      ),
                    ),
                    const SizedBox(height: 16),
                    // Alphanumeric code
                    GestureDetector(
                      onTap: () => Clipboard.setData(
                          ClipboardData(text: voucher.alphanumericCode)),
                      child: Container(
                        padding: const EdgeInsets.symmetric(
                            horizontal: 16, vertical: 10),
                        decoration: BoxDecoration(
                          color: const Color(0xFFF4F7F5),
                          borderRadius: BorderRadius.circular(10),
                        ),
                        child: Row(
                          mainAxisSize: MainAxisSize.min,
                          children: [
                            Text(
                              voucher.alphanumericCode,
                              style: const TextStyle(
                                fontFamily: 'monospace',
                                fontSize: 18,
                                fontWeight: FontWeight.w900,
                                letterSpacing: 3,
                                color: Color(0xFF1B4332),
                              ),
                            ),
                            const SizedBox(width: 8),
                            const Icon(Icons.copy_rounded,
                                size: 14, color: Color(0xFF64748B)),
                          ],
                        ),
                      ),
                    ),
                    const SizedBox(height: 6),
                    const Text('Tap code to copy · Works offline',
                        style: TextStyle(fontSize: 11, color: Color(0xFF94A3B8))),
                  ],
                ),
              ),

              const SizedBox(height: 24),

              // Voucher info
              Container(
                width: double.infinity,
                padding: const EdgeInsets.all(18),
                decoration: BoxDecoration(
                  color: Colors.white.withOpacity(0.1),
                  borderRadius: BorderRadius.circular(18),
                  border: Border.all(color: Colors.white24),
                ),
                child: Column(
                  children: [
                    _infoRow('Product', voucher.productDescription),
                    _divider(),
                    Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        const Text('Category',
                            style: TextStyle(color: Colors.white60, fontSize: 13)),
                        VoucherCategoryChip(category: voucher.productCategory),
                      ],
                    ),
                    _divider(),
                    _infoRow(
                      'Amount',
                      '${voucher.amountEtb.toStringAsFixed(2)} ETB',
                      valueStyle: const TextStyle(
                        color: Colors.white,
                        fontSize: 20,
                        fontWeight: FontWeight.w900,
                        fontFamily: 'monospace',
                      ),
                    ),
                    _divider(),
                    _infoRow('Expires', _formatDate(voucher.validUntil)),
                    _divider(),
                    _infoRow('Sequence', '#${voucher.sequenceOrder}'),
                  ],
                ),
              ),

              const SizedBox(height: 20),

              // 6-check reminder
              Container(
                padding: const EdgeInsets.all(14),
                decoration: BoxDecoration(
                  color: Colors.white.withOpacity(0.06),
                  borderRadius: BorderRadius.circular(14),
                  border: Border.all(color: Colors.white12),
                ),
                child: const Row(
                  children: [
                    Icon(Icons.verified_user_rounded,
                        color: Colors.greenAccent, size: 18),
                    SizedBox(width: 10),
                    Expanded(
                      child: Text(
                        'Merchant app runs 6 security checks before payment. '
                        'Do not leave until you receive confirmation.',
                        style: TextStyle(
                            color: Colors.white60, fontSize: 11, height: 1.5),
                      ),
                    ),
                  ],
                ),
              ),

              const SizedBox(height: 32),
            ],
          ),
        ),
      ),
    );
  }

  Widget _infoRow(String label, String value, {TextStyle? valueStyle}) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        Text(label,
            style: const TextStyle(color: Colors.white60, fontSize: 13)),
        Flexible(
          child: Text(
            value,
            textAlign: TextAlign.right,
            style: valueStyle ??
                const TextStyle(
                  color: Colors.white,
                  fontSize: 14,
                  fontWeight: FontWeight.w700,
                ),
          ),
        ),
      ],
    );
  }

  Widget _divider() => Divider(
      color: Colors.white.withOpacity(0.1), height: 16, thickness: 0.5);

  String _formatDate(String raw) {
    try {
      final d = DateTime.parse(raw);
      const months = [
        'Jan','Feb','Mar','Apr','May','Jun',
        'Jul','Aug','Sep','Oct','Nov','Dec'
      ];
      return '${d.day} ${months[d.month - 1]} ${d.year}';
    } catch (_) {
      return raw;
    }
  }
}

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import '../../models/voucher_model.dart';
import '../../widgets/voucher_status_chip.dart';
import '../../widgets/voucher_category_chip.dart';
import 'voucher_qr_screen.dart';

class VoucherDetailScreen extends StatelessWidget {
  final VoucherModel voucher;

  const VoucherDetailScreen({super.key, required this.voucher});

  static const _primary     = Color(0xFF1B4332);
  static const _amber       = Color(0xFF78350F);
  static const _surface     = Color(0xFFF4F7F5);
  static const _cardBorder  = Color(0xFFE2E8F0);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: _surface,
      appBar: AppBar(
        backgroundColor: _primary,
        foregroundColor: Colors.white,
        title: Text(
          voucher.productCategory,
          style: const TextStyle(fontWeight: FontWeight.w900, fontSize: 18, letterSpacing: -0.5),
        ),
        actions: [
          if (voucher.isActive)
            IconButton(
              icon: const Icon(Icons.fullscreen_rounded),
              tooltip: 'Full screen QR',
              onPressed: () => Navigator.push(
                context,
                MaterialPageRoute(
                  builder: (_) => VoucherQrScreen(voucher: voucher),
                ),
              ),
            ),
        ],
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(20),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Status / category header
            _buildHeaderCard(),
            const SizedBox(height: 16),

            // QR or status panel
            if (voucher.isActive) _buildQrCard(context),
            if (voucher.isRedeemed) _buildRedeemedCard(),
            if (voucher.isLocked) _buildLockedCard(),
            if (voucher.isExpired) _buildExpiredCard(),

            const SizedBox(height: 16),

            // Voucher info rows
            _buildInfoCard(),

            const SizedBox(height: 16),

            // Security note
            _buildSecurityNote(),

            const SizedBox(height: 24),

            // CTA
            if (voucher.isActive)
              SizedBox(
                width: double.infinity,
                child: ElevatedButton.icon(
                  onPressed: () => Navigator.push(
                    context,
                    MaterialPageRoute(
                      builder: (_) => VoucherQrScreen(voucher: voucher),
                    ),
                  ),
                  icon: const Icon(Icons.qr_code_2_rounded, size: 22),
                  label: const Text('Show QR to Merchant'),
                  style: ElevatedButton.styleFrom(
                    backgroundColor: _primary,
                    foregroundColor: Colors.white,
                    padding: const EdgeInsets.symmetric(vertical: 16),
                    shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(16)),
                    textStyle: const TextStyle(
                        fontSize: 15, fontWeight: FontWeight.bold),
                  ),
                ),
              ),
          ],
        ),
      ),
    );
  }

  Widget _buildHeaderCard() {
    return Container(
      width: double.infinity,
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        gradient: const LinearGradient(
          colors: [Color(0xFF1B4332), Color(0xFF2D6A4F)],
          begin: Alignment.topLeft,
          end: Alignment.bottomRight,
        ),
        borderRadius: BorderRadius.circular(20),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              Text(voucher.categoryEmoji, style: const TextStyle(fontSize: 32)),
              const SizedBox(width: 12),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      voucher.productDescription,
                      style: const TextStyle(
                          color: Colors.white,
                          fontSize: 16,
                          fontWeight: FontWeight.w900,
                          letterSpacing: -0.3),
                    ),
                    const SizedBox(height: 4),
                    VoucherCategoryChip(category: voucher.productCategory),
                  ],
                ),
              ),
              VoucherStatusChip(status: voucher.status, fontSize: 11),
            ],
          ),
          const SizedBox(height: 16),
          Text(
            '${voucher.amountEtb.toStringAsFixed(2)} ETB',
            style: const TextStyle(
                color: Colors.white,
                fontSize: 36,
                fontWeight: FontWeight.w900,
                fontFamily: 'monospace',
                letterSpacing: -1),
          ),
          const SizedBox(height: 2),
          Text(
            'Sequence #${voucher.sequenceOrder} · locked to ${voucher.productCategory}',
            style: const TextStyle(color: Colors.white60, fontSize: 11),
          ),
        ],
      ),
    );
  }

  Widget _buildQrCard(BuildContext context) {
    return GestureDetector(
      onTap: () => Navigator.push(
        context,
        MaterialPageRoute(builder: (_) => VoucherQrScreen(voucher: voucher)),
      ),
      child: Container(
        width: double.infinity,
        padding: const EdgeInsets.all(24),
        decoration: BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.circular(20),
          border: Border.all(color: _cardBorder),
        ),
        child: Column(
          children: [
            // Placeholder QR visual — replaced by real QR in VoucherQrScreen
            Container(
              width: 180,
              height: 180,
              decoration: BoxDecoration(
                color: const Color(0xFFF8FAFC),
                borderRadius: BorderRadius.circular(12),
                border: Border.all(color: _cardBorder, width: 2),
              ),
              child: const Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Icon(Icons.qr_code_2_rounded, size: 100, color: Color(0xFF1B4332)),
                  SizedBox(height: 6),
                  Text('Tap to expand',
                      style: TextStyle(fontSize: 11, color: Color(0xFF64748B))),
                ],
              ),
            ),
            const SizedBox(height: 16),
            // Alphanumeric code
            GestureDetector(
              onTap: () {
                Clipboard.setData(ClipboardData(text: voucher.alphanumericCode));
              },
              child: Container(
                padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 10),
                decoration: BoxDecoration(
                  color: const Color(0xFFF1F5F9),
                  borderRadius: BorderRadius.circular(10),
                ),
                child: Row(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    Text(
                      voucher.alphanumericCode,
                      style: const TextStyle(
                          fontFamily: 'monospace',
                          fontSize: 16,
                          fontWeight: FontWeight.w800,
                          letterSpacing: 2,
                          color: Color(0xFF1B4332)),
                    ),
                    const SizedBox(width: 8),
                    const Icon(Icons.copy_rounded, size: 14, color: Color(0xFF64748B)),
                  ],
                ),
              ),
            ),
            const SizedBox(height: 8),
            const Text(
              'Works offline · Show this to the merchant',
              style: TextStyle(fontSize: 11, color: Color(0xFF64748B)),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildRedeemedCard() {
    return Container(
      width: double.infinity,
      padding: const EdgeInsets.all(18),
      decoration: BoxDecoration(
        color: const Color(0xFFF0FDF4),
        borderRadius: BorderRadius.circular(16),
        border: Border.all(color: const Color(0xFFBBF7D0)),
      ),
      child: Row(
        children: [
          const Icon(Icons.check_circle_rounded, color: Color(0xFF16A34A), size: 28),
          const SizedBox(width: 12),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                const Text('Redeemed',
                    style: TextStyle(
                        fontWeight: FontWeight.w800,
                        color: Color(0xFF14532D),
                        fontSize: 14)),
                if (voucher.redeemedAt != null)
                  Text(
                    'on ${voucher.redeemedAt}',
                    style: const TextStyle(fontSize: 11, color: Color(0xFF16A34A)),
                  ),
                const SizedBox(height: 4),
                const Text(
                  'Payment released to merchant · Input delivered',
                  style: TextStyle(fontSize: 11, color: Color(0xFF15803D)),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildLockedCard() {
    return Container(
      width: double.infinity,
      padding: const EdgeInsets.all(18),
      decoration: BoxDecoration(
        color: const Color(0xFFEFF6FF),
        borderRadius: BorderRadius.circular(16),
        border: Border.all(color: const Color(0xFFBFDBFE)),
      ),
      child: Row(
        children: [
          const Icon(Icons.lock_rounded, color: Color(0xFF1D4ED8), size: 28),
          const SizedBox(width: 12),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                const Text('Locked',
                    style: TextStyle(
                        fontWeight: FontWeight.w800,
                        color: Color(0xFF1E3A8A),
                        fontSize: 14)),
                Text(
                  'Unlocks after voucher #${voucher.sequenceOrder - 1} is redeemed',
                  style: const TextStyle(fontSize: 11, color: Color(0xFF1D4ED8)),
                ),
                const SizedBox(height: 4),
                const Text(
                  'Agronomic order must be followed — seeds before fertilizer',
                  style: TextStyle(fontSize: 11, color: Color(0xFF3B82F6)),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildExpiredCard() {
    return Container(
      width: double.infinity,
      padding: const EdgeInsets.all(18),
      decoration: BoxDecoration(
        color: const Color(0xFFFEF2F2),
        borderRadius: BorderRadius.circular(16),
        border: Border.all(color: const Color(0xFFFECACA)),
      ),
      child: const Row(
        children: [
          Icon(Icons.schedule_rounded, color: Color(0xFFDC2626), size: 28),
          SizedBox(width: 12),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text('Expired',
                    style: TextStyle(
                        fontWeight: FontWeight.w800,
                        color: Color(0xFF991B1B),
                        fontSize: 14)),
                Text(
                  'This voucher has passed its validity date',
                  style: TextStyle(fontSize: 11, color: Color(0xFFDC2626)),
                ),
                SizedBox(height: 4),
                Text(
                  'Contact Agri-Yield support for assistance',
                  style: TextStyle(fontSize: 11, color: Color(0xFFEF4444)),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildInfoCard() {
    final rows = [
      ['Voucher ID', voucher.id, true],
      ['Category Lock', '${voucher.productCategory} only', false],
      ['Amount', '${voucher.amountEtb.toStringAsFixed(2)} ETB', false],
      ['Sequence', '#${voucher.sequenceOrder}', false],
      ['Valid Until', voucher.validUntil, false],
      ['Created', voucher.createdAt, false],
      if (voucher.isRedeemed && voucher.redeemedAt != null)
        ['Redeemed At', voucher.redeemedAt!, false],
    ];

    return Container(
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(20),
        border: Border.all(color: _cardBorder),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Padding(
            padding: EdgeInsets.fromLTRB(16, 14, 16, 0),
            child: Text('Voucher Details',
                style: TextStyle(
                    fontSize: 12,
                    fontWeight: FontWeight.w800,
                    color: Color(0xFF64748B),
                    letterSpacing: 0.5)),
          ),
          const SizedBox(height: 8),
          ...rows.asMap().entries.map((entry) {
            final i = entry.key;
            final row = entry.value;
            final isMono = row[2] as bool;
            return Column(
              children: [
                Padding(
                  padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 10),
                  child: Row(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      SizedBox(
                        width: 100,
                        child: Text(row[0] as String,
                            style: const TextStyle(
                                fontSize: 12,
                                color: Color(0xFF94A3B8),
                                fontWeight: FontWeight.w500)),
                      ),
                      Expanded(
                        child: Text(
                          row[1] as String,
                          style: TextStyle(
                            fontSize: isMono ? 10 : 13,
                            fontWeight: FontWeight.w700,
                            color: const Color(0xFF0F291B),
                            fontFamily: isMono ? 'monospace' : null,
                            letterSpacing: isMono ? 0.3 : 0,
                          ),
                        ),
                      ),
                    ],
                  ),
                ),
                if (i < rows.length - 1)
                  const Divider(height: 1, indent: 16, color: Color(0xFFF8FAFC)),
              ],
            );
          }),
        ],
      ),
    );
  }

  Widget _buildSecurityNote() {
    return Container(
      padding: const EdgeInsets.all(14),
      decoration: BoxDecoration(
        color: const Color(0xFFFFFBEB),
        borderRadius: BorderRadius.circular(14),
        border: Border.all(color: const Color(0xFFFDE68A)),
      ),
      child: const Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text('🔐', style: TextStyle(fontSize: 16)),
          SizedBox(width: 10),
          Expanded(
            child: Text(
              'This voucher is cryptographically signed. The merchant\'s app '
              'runs 6 security checks before releasing any payment. '
              'Never share your voucher code with anyone other than certified merchants.',
              style: TextStyle(fontSize: 11, color: Color(0xFF92400E), height: 1.5),
            ),
          ),
        ],
      ),
    );
  }
}

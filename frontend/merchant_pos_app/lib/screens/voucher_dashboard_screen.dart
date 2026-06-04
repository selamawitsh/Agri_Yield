import 'package:flutter/material.dart';
import '../services/voucher_service.dart';
import '../models/voucher_model.dart';
import '../widgets/voucher_category_badge.dart';
import 'scanner_screen.dart';

class VoucherDashboardScreen extends StatefulWidget {
  const VoucherDashboardScreen({super.key});

  @override
  State<VoucherDashboardScreen> createState() =>
      _VoucherDashboardScreenState();
}

class _VoucherDashboardScreenState
    extends State<VoucherDashboardScreen> {
  final _voucherService = VoucherService();
  List<MerchantRedemptionSummary> _recent = [];
  bool _loading = true;

  @override
  void initState() {
    super.initState();
    _load();
  }

  Future<void> _load() async {
    setState(() => _loading = true);
    final data = await _voucherService.getRedemptionHistory(size: 10);
    if (mounted) {
      setState(() { _recent = data; _loading = false; });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Voucher Redemptions'),
        backgroundColor: Colors.orange,
        foregroundColor: Colors.white,
        actions: [
          IconButton(
              icon: const Icon(Icons.refresh), onPressed: _load),
        ],
      ),
      body: RefreshIndicator(
        onRefresh: _load,
        child: SingleChildScrollView(
          physics: const AlwaysScrollableScrollPhysics(),
          padding: const EdgeInsets.all(16),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              // Scan action card
              Container(
                width: double.infinity,
                padding: const EdgeInsets.all(20),
                decoration: BoxDecoration(
                  gradient: const LinearGradient(
                      colors: [Colors.orange, Colors.deepOrange]),
                  borderRadius: BorderRadius.circular(16),
                ),
                child: Column(
                  children: [
                    const Icon(Icons.qr_code_scanner,
                        color: Colors.white, size: 48),
                    const SizedBox(height: 12),
                    const Text(
                      'Ready to Redeem',
                      style: TextStyle(
                          color: Colors.white,
                          fontSize: 20,
                          fontWeight: FontWeight.bold),
                    ),
                    const SizedBox(height: 6),
                    const Text(
                      'Scan a farmer QR voucher to begin redemption',
                      style: TextStyle(
                          color: Colors.white70, fontSize: 13),
                      textAlign: TextAlign.center,
                    ),
                    const SizedBox(height: 20),
                    SizedBox(
                      width: double.infinity,
                      child: ElevatedButton.icon(
                        onPressed: () => Navigator.push(
                          context,
                          MaterialPageRoute(
                              builder: (_) => const ScannerScreen()),
                        ),
                        icon: const Icon(Icons.qr_code_scanner,
                            color: Colors.orange),
                        label: const Text(
                          'Scan Voucher Now',
                          style: TextStyle(
                              color: Colors.orange,
                              fontWeight: FontWeight.bold,
                              fontSize: 16),
                        ),
                        style: ElevatedButton.styleFrom(
                          backgroundColor: Colors.white,
                          padding: const EdgeInsets.symmetric(
                              vertical: 14),
                          shape: RoundedRectangleBorder(
                              borderRadius:
                                  BorderRadius.circular(10)),
                        ),
                      ),
                    ),
                  ],
                ),
              ),
              const SizedBox(height: 24),

              // How voucher redemption works
              _buildInfoCard(),
              const SizedBox(height: 24),

              // Recent redemptions
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  const Text('Recent Redemptions',
                      style: TextStyle(
                          fontSize: 16,
                          fontWeight: FontWeight.bold)),
                  if (_recent.isNotEmpty)
                    Text('${_recent.length} latest',
                        style: const TextStyle(
                            color: Colors.grey, fontSize: 13)),
                ],
              ),
              const SizedBox(height: 12),

              if (_loading)
                const Center(
                  child: CircularProgressIndicator(
                      color: Colors.orange))
              else if (_recent.isEmpty)
                Container(
                  width: double.infinity,
                  padding: const EdgeInsets.all(32),
                  decoration: BoxDecoration(
                    color: Colors.grey.shade50,
                    borderRadius: BorderRadius.circular(12),
                    border:
                        Border.all(color: Colors.grey.shade200),
                  ),
                  child: const Column(
                    children: [
                      Icon(Icons.confirmation_number_outlined,
                          size: 48, color: Colors.grey),
                      SizedBox(height: 12),
                      Text('No redemptions yet',
                          style: TextStyle(color: Colors.grey)),
                      SizedBox(height: 4),
                      Text(
                        'Scan your first voucher to get started',
                        style: TextStyle(
                            color: Colors.grey, fontSize: 12),
                      ),
                    ],
                  ),
                )
              else
                ..._recent.map((r) => _buildRecentCard(r)),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildInfoCard() {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Colors.blue.withOpacity(0.05),
        borderRadius: BorderRadius.circular(12),
        border: Border.all(color: Colors.blue.withOpacity(0.2)),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Text('Voucher Redemption Process',
              style: TextStyle(
                  fontWeight: FontWeight.bold,
                  color: Colors.blue,
                  fontSize: 15)),
          const SizedBox(height: 12),
          _step('1', 'Farmer shows QR code from their app'),
          _step('2', 'Tap Scan Voucher — system runs 6-step validation'),
          _step('3', 'Dispense product shown on confirmation screen'),
          _step('4', 'Payment released from escrow to your Telebirr'),
        ],
      ),
    );
  }

  Widget _step(String num, String text) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 8),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Container(
            width: 22,
            height: 22,
            margin: const EdgeInsets.only(right: 10, top: 1),
            decoration: BoxDecoration(
              color: Colors.blue,
              borderRadius: BorderRadius.circular(99),
            ),
            child: Center(
              child: Text(num,
                  style: const TextStyle(
                      color: Colors.white,
                      fontSize: 11,
                      fontWeight: FontWeight.bold)),
            ),
          ),
          Expanded(
              child: Text(text,
                  style: const TextStyle(
                      color: Colors.blue, fontSize: 13))),
        ],
      ),
    );
  }

  Widget _buildRecentCard(MerchantRedemptionSummary r) {
    final isCompleted = r.status == 'COMPLETED';
    return Container(
      margin: const EdgeInsets.only(bottom: 8),
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(10),
        border: Border.all(color: Colors.grey.shade200),
      ),
      child: Row(children: [
        Container(
          width: 36,
          height: 36,
          decoration: BoxDecoration(
            color: isCompleted
                ? Colors.green.withOpacity(0.1)
                : Colors.red.withOpacity(0.1),
            shape: BoxShape.circle,
          ),
          child: Icon(
            isCompleted ? Icons.check_circle : Icons.cancel,
            color: isCompleted ? Colors.green : Colors.red,
            size: 20,
          ),
        ),
        const SizedBox(width: 12),
        Expanded(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(r.farmerName,
                  style: const TextStyle(
                      fontWeight: FontWeight.w600,
                      fontSize: 14)),
              Row(children: [
                VoucherCategoryBadge(
                    category: r.productCategory,
                    fontSize: 10),
                const SizedBox(width: 6),
                Expanded(
                  child: Text(
                    r.productDescription,
                    style: const TextStyle(
                        fontSize: 12, color: Colors.grey),
                    overflow: TextOverflow.ellipsis,
                  ),
                ),
              ]),
            ],
          ),
        ),
        Column(
          crossAxisAlignment: CrossAxisAlignment.end,
          children: [
            Text(
              'ETB ${r.amountEtb.toStringAsFixed(2)}',
              style: TextStyle(
                  fontWeight: FontWeight.bold,
                  fontSize: 14,
                  color: isCompleted
                      ? Colors.green
                      : Colors.red),
            ),
            Text(
              _shortDate(r.redeemedAt),
              style: const TextStyle(
                  fontSize: 11, color: Colors.grey),
            ),
          ],
        ),
      ]),
    );
  }

  String _shortDate(String raw) {
    try {
      final dt = DateTime.parse(raw);
      final now = DateTime.now();
      final diff = now.difference(dt);
      if (diff.inMinutes < 60) return '${diff.inMinutes}m ago';
      if (diff.inHours < 24)   return '${diff.inHours}h ago';
      return '${diff.inDays}d ago';
    } catch (_) {
      return raw;
    }
  }
}

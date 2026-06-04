import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import '../models/merchant_profile_model.dart';
import '../services/merchant_service.dart';

class WalletScreen extends StatefulWidget {
  const WalletScreen({super.key});

  @override
  State<WalletScreen> createState() => _WalletScreenState();
}

class _WalletScreenState extends State<WalletScreen> {
  final _merchantService = MerchantService();
  MerchantProfile? _profile;
  List<SettlementRecord> _settlements = [];
  bool _loading = true;

  double get _totalSettled =>
      _settlements.fold(0, (sum, s) => sum + s.amountEtb);

  double get _settledThisMonth {
    final now = DateTime.now();
    final prefix = DateFormat('yyyy-MM').format(now);
    return _settlements
        .where((s) => s.settledAt.startsWith(prefix))
        .fold(0, (sum, s) => sum + s.amountEtb);
  }

  @override
  void initState() {
    super.initState();
    _load();
  }

  Future<void> _load() async {
    setState(() => _loading = true);
    final profile     = await _merchantService.getMyProfile();
    final settlements = await _merchantService.getSettlements();
    if (mounted) {
      setState(() {
        _profile     = profile;
        _settlements = settlements;
        _loading     = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    final fmt = NumberFormat('#,##0.00');

    return Scaffold(
      appBar: AppBar(
        title: const Text('Wallet'),
        backgroundColor: Colors.orange,
        foregroundColor: Colors.white,
        actions: [
          IconButton(icon: const Icon(Icons.refresh), onPressed: _load),
        ],
      ),
      body: _loading
          ? const Center(
              child: CircularProgressIndicator(color: Colors.orange))
          : RefreshIndicator(
              onRefresh: _load,
              child: SingleChildScrollView(
                physics: const AlwaysScrollableScrollPhysics(),
                padding: const EdgeInsets.all(16),
                child: Column(
                  children: [
                    // Telebirr card
                    Container(
                      width: double.infinity,
                      padding: const EdgeInsets.all(24),
                      decoration: BoxDecoration(
                        gradient: const LinearGradient(
                            colors: [Colors.orange, Colors.deepOrange]),
                        borderRadius: BorderRadius.circular(16),
                      ),
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          const Row(children: [
                            Icon(Icons.account_balance_wallet,
                                color: Colors.white, size: 28),
                            SizedBox(width: 12),
                            Text('Telebirr Account',
                                style: TextStyle(
                                    color: Colors.white70, fontSize: 14)),
                          ]),
                          const SizedBox(height: 16),
                          Text(
                            _profile?.telebirrAccount ?? 'Not linked',
                            style: const TextStyle(
                                color: Colors.white,
                                fontSize: 22,
                                fontWeight: FontWeight.bold,
                                letterSpacing: 1.5),
                          ),
                          const SizedBox(height: 8),
                          const Text(
                            'Voucher payments are sent here instantly',
                            style: TextStyle(
                                color: Colors.white70, fontSize: 12),
                          ),
                        ],
                      ),
                    ),
                    const SizedBox(height: 16),

                    // MS-09: settlement summary
                    Row(children: [
                      Expanded(
                        child: _summaryCard(
                          label: 'Total Received',
                          value: 'ETB ${fmt.format(_totalSettled)}',
                          color: Colors.green,
                          icon:  Icons.payments,
                        ),
                      ),
                      const SizedBox(width: 12),
                      Expanded(
                        child: _summaryCard(
                          label: 'This Month',
                          value: 'ETB ${fmt.format(_settledThisMonth)}',
                          color: Colors.blue,
                          icon:  Icons.calendar_month,
                        ),
                      ),
                    ]),
                    const SizedBox(height: 16),

                    // Settlement records list
                    Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        const Text('Settlement Records',
                            style: TextStyle(
                                fontSize: 16,
                                fontWeight: FontWeight.bold)),
                        Text('${_settlements.length} total',
                            style: const TextStyle(
                                color: Colors.grey, fontSize: 13)),
                      ],
                    ),
                    const SizedBox(height: 12),

                    if (_settlements.isEmpty)
                      Container(
                        width: double.infinity,
                        padding: const EdgeInsets.all(32),
                        decoration: BoxDecoration(
                          color: Colors.grey.shade50,
                          borderRadius: BorderRadius.circular(12),
                          border: Border.all(color: Colors.grey.shade200),
                        ),
                        child: const Column(
                          children: [
                            Icon(Icons.receipt_long_outlined,
                                size: 48, color: Colors.grey),
                            SizedBox(height: 12),
                            Text('No settlements yet',
                                style: TextStyle(color: Colors.grey)),
                            SizedBox(height: 4),
                            Text(
                              'Payments appear here after voucher redemption',
                              style: TextStyle(
                                  color: Colors.grey, fontSize: 12),
                              textAlign: TextAlign.center,
                            ),
                          ],
                        ),
                      )
                    else
                      ..._settlements
                          .map((s) => _buildSettlementCard(s, fmt)),

                    const SizedBox(height: 16),
                    _buildHowItWorksCard(),
                  ],
                ),
              ),
            ),
    );
  }

  Widget _summaryCard({
    required String label,
    required String value,
    required Color color,
    required IconData icon,
  }) {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: color.withOpacity(0.08),
        borderRadius: BorderRadius.circular(12),
        border: Border.all(color: color.withOpacity(0.2)),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Icon(icon, color: color, size: 22),
          const SizedBox(height: 8),
          Text(value,
              style: TextStyle(
                  color: color,
                  fontSize: 16,
                  fontWeight: FontWeight.bold)),
          Text(label,
              style: TextStyle(
                  color: color.withOpacity(0.7), fontSize: 11)),
        ],
      ),
    );
  }

  Widget _buildSettlementCard(
      SettlementRecord s, NumberFormat fmt) {
    return Container(
      margin: const EdgeInsets.only(bottom: 10),
      padding: const EdgeInsets.all(14),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(10),
        border: Border.all(color: Colors.grey.shade200),
      ),
      child: Row(children: [
        Container(
          width: 40,
          height: 40,
          decoration: BoxDecoration(
            color: Colors.green.withOpacity(0.1),
            shape: BoxShape.circle,
          ),
          child: const Icon(Icons.check_circle,
              color: Colors.green, size: 20),
        ),
        const SizedBox(width: 12),
        Expanded(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(s.farmerName,
                  style: const TextStyle(fontWeight: FontWeight.w600)),
              Text(s.productDescription,
                  style: const TextStyle(
                      fontSize: 12, color: Colors.grey)),
              Text(_formatDate(s.settledAt),
                  style: const TextStyle(
                      fontSize: 11, color: Colors.grey)),
            ],
          ),
        ),
        Column(
          crossAxisAlignment: CrossAxisAlignment.end,
          children: [
            Text(
              'ETB ${fmt.format(s.amountEtb)}',
              style: const TextStyle(
                  fontWeight: FontWeight.bold,
                  color: Colors.green,
                  fontSize: 15),
            ),
            Container(
              padding: const EdgeInsets.symmetric(
                  horizontal: 6, vertical: 2),
              decoration: BoxDecoration(
                color: Colors.green.withOpacity(0.1),
                borderRadius: BorderRadius.circular(4),
              ),
              child: Text(s.status,
                  style: const TextStyle(
                      fontSize: 10,
                      color: Colors.green,
                      fontWeight: FontWeight.w600)),
            ),
          ],
        ),
      ]),
    );
  }

  Widget _buildHowItWorksCard() {
    return Container(
      width: double.infinity,
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Colors.blue.withOpacity(0.05),
        borderRadius: BorderRadius.circular(12),
        border: Border.all(color: Colors.blue.withOpacity(0.2)),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Text('How Payments Work',
              style: TextStyle(
                  fontWeight: FontWeight.bold,
                  color: Colors.blue,
                  fontSize: 15)),
          const SizedBox(height: 12),
          _bullet(
              'Farmer presents QR voucher at your shop'),
          _bullet(
              'You scan and dispense the agricultural product'),
          _bullet(
              'Payment is instantly released from escrow to your Telebirr'),
          _bullet(
              'Settlement record appears here within seconds'),
        ],
      ),
    );
  }

  Widget _bullet(String text) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 6),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Text('- ',
              style: TextStyle(color: Colors.blue, fontSize: 16)),
          Expanded(
              child: Text(text,
                  style: const TextStyle(
                      fontSize: 13, color: Colors.blue))),
        ],
      ),
    );
  }

  String _formatDate(String iso) {
    try {
      final dt = DateTime.parse(iso);
      return DateFormat('dd MMM yyyy, HH:mm').format(dt);
    } catch (_) {
      return iso;
    }
  }
}

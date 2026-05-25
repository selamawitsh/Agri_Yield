import 'package:flutter/material.dart';
import '../models/price_anomaly_model.dart';
import '../services/merchant_service.dart';

class TransactionHistoryScreen extends StatefulWidget {
  const TransactionHistoryScreen({super.key});

  @override
  State<TransactionHistoryScreen> createState() =>
      _TransactionHistoryScreenState();
}

class _TransactionHistoryScreenState extends State<TransactionHistoryScreen>
    with SingleTickerProviderStateMixin {
  final _merchantService = MerchantService();
  List<PriceAnomaly> _anomalies = [];
  bool _loading = true;
  late TabController _tabController;

  @override
  void initState() {
    super.initState();
    _tabController = TabController(length: 2, vsync: this);
    _load();
  }

  @override
  void dispose() {
    _tabController.dispose();
    super.dispose();
  }

  Future<void> _load() async {
    setState(() => _loading = true);
    final anomalies = await _merchantService.getRedemptionHistory();
    if (mounted) {
      setState(() { _anomalies = anomalies; _loading = false; });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Transaction History'),
        backgroundColor: Colors.orange,
        foregroundColor: Colors.white,
        bottom: TabBar(
          controller: _tabController,
          indicatorColor: Colors.white,
          labelColor: Colors.white,
          unselectedLabelColor: Colors.white70,
          tabs: const [
            Tab(text: 'Price Flags'),
            Tab(text: 'Vouchers'),
          ],
        ),
      ),
      body: _loading
          ? const Center(child: CircularProgressIndicator(color: Colors.orange))
          : TabBarView(
              controller: _tabController,
              children: [
                _buildAnomaliesTab(),
                _buildVouchersTab(),
              ],
            ),
    );
  }

  Widget _buildAnomaliesTab() {
    if (_anomalies.isEmpty) {
      return const Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(Icons.check_circle, size: 64, color: Colors.green),
            SizedBox(height: 16),
            Text('No price flags', style: TextStyle(fontSize: 18, color: Colors.grey)),
            SizedBox(height: 8),
            Text('Your prices are within regional median',
                style: TextStyle(color: Colors.grey)),
          ],
        ),
      );
    }

    return RefreshIndicator(
      onRefresh: _load,
      child: ListView.builder(
        padding: const EdgeInsets.all(16),
        itemCount: _anomalies.length,
        itemBuilder: (ctx, i) {
          final a = _anomalies[i];
          final isResolved = a.resolvedAt != null;
          return Card(
            margin: const EdgeInsets.only(bottom: 12),
            child: Padding(
              padding: const EdgeInsets.all(16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Row(
                    children: [
                      Icon(
                        isResolved ? Icons.check_circle : Icons.warning,
                        color: isResolved ? Colors.green : Colors.orange,
                      ),
                      const SizedBox(width: 8),
                      Expanded(
                        child: Text(
                          'Price Flag — ${a.deviationPct.toStringAsFixed(1)}% above median',
                          style: TextStyle(
                            fontWeight: FontWeight.bold,
                            color: isResolved ? Colors.green : Colors.orange,
                          ),
                        ),
                      ),
                    ],
                  ),
                  const SizedBox(height: 8),
                  _infoRow('Your Price', 'ETB ${a.merchantPriceEtb.toStringAsFixed(2)}'),
                  _infoRow('Regional Median', 'ETB ${a.regionalMedianEtb.toStringAsFixed(2)}'),
                  _infoRow('Status', isResolved ? 'Resolved' : 'Active'),
                  _infoRow('Flagged', _formatDate(a.flaggedAt)),
                ],
              ),
            ),
          );
        },
      ),
    );
  }

  Widget _buildVouchersTab() {
    return const Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Icon(Icons.qr_code, size: 64, color: Colors.grey),
          SizedBox(height: 16),
          Text('Voucher history', style: TextStyle(fontSize: 18, color: Colors.grey)),
          SizedBox(height: 8),
          Text('Redeemed vouchers are tracked by voucher service',
              style: TextStyle(color: Colors.grey), textAlign: TextAlign.center),
        ],
      ),
    );
  }

  Widget _infoRow(String label, String value) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 2),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Text(label, style: const TextStyle(color: Colors.grey, fontSize: 13)),
          Text(value, style: const TextStyle(fontWeight: FontWeight.w500, fontSize: 13)),
        ],
      ),
    );
  }

  String _formatDate(String iso) {
    try {
      final dt = DateTime.parse(iso);
      return '${dt.day}/${dt.month}/${dt.year}';
    } catch (_) {
      return iso;
    }
  }
}

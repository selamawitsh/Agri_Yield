import 'package:flutter/material.dart';
import '../models/analytics_model.dart';
import '../services/merchant_service.dart';

class AnalyticsScreen extends StatefulWidget {
  const AnalyticsScreen({super.key});

  @override
  State<AnalyticsScreen> createState() => _AnalyticsScreenState();
}

class _AnalyticsScreenState extends State<AnalyticsScreen> {
  final _merchantService = MerchantService();
  MerchantAnalytics? _analytics;
  bool _loading = true;

  @override
  void initState() {
    super.initState();
    _load();
  }

  Future<void> _load() async {
    setState(() => _loading = true);
    final analytics = await _merchantService.getAnalytics();
    if (mounted) setState(() { _analytics = analytics; _loading = false; });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Analytics'),
        backgroundColor: Colors.orange,
        foregroundColor: Colors.white,
        actions: [
          IconButton(icon: const Icon(Icons.refresh), onPressed: _load),
        ],
      ),
      body: _loading
          ? const Center(child: CircularProgressIndicator(color: Colors.orange))
          : _analytics == null
              ? const Center(child: Text('Could not load analytics'))
              : RefreshIndicator(
                  onRefresh: _load,
                  child: SingleChildScrollView(
                    physics: const AlwaysScrollableScrollPhysics(),
                    padding: const EdgeInsets.all(16),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        const Text('Business Overview',
                            style: TextStyle(
                                fontSize: 20, fontWeight: FontWeight.bold)),
                        const SizedBox(height: 16),
                        GridView.count(
                          shrinkWrap: true,
                          physics: const NeverScrollableScrollPhysics(),
                          crossAxisCount: 2,
                          mainAxisSpacing: 12,
                          crossAxisSpacing: 12,
                          childAspectRatio: 1.4,
                          children: [
                            _statCard('Total Products',
                                _analytics!.totalProducts.toString(),
                                Icons.inventory_2, Colors.blue),
                            _statCard('Available',
                                _analytics!.availableProducts.toString(),
                                Icons.check_circle, Colors.green),
                            _statCard('Price Flags',
                                _analytics!.priceAnomaliesCount.toString(),
                                Icons.warning, Colors.orange),
                            _statCard('Avg. Price',
                                'ETB ${_analytics!.averageProductPrice.toStringAsFixed(0)}',
                                Icons.price_change, Colors.purple),
                          ],
                        ),
                        const SizedBox(height: 24),
                        _buildStatusCard(),
                        const SizedBox(height: 16),
                        _buildTipsCard(),
                      ],
                    ),
                  ),
                ),
    );
  }

  Widget _statCard(String label, String value, IconData icon, Color color) {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: color.withOpacity(0.08),
        borderRadius: BorderRadius.circular(12),
        border: Border.all(color: color.withOpacity(0.2)),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Icon(icon, color: color, size: 28),
          Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(value,
                  style: TextStyle(
                      color: color,
                      fontSize: 24,
                      fontWeight: FontWeight.bold)),
              Text(label,
                  style: TextStyle(color: color.withOpacity(0.7), fontSize: 12)),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildStatusCard() {
    final hasFlags = (_analytics?.priceAnomaliesCount ?? 0) > 0;
    return Container(
      width: double.infinity,
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: hasFlags
            ? Colors.orange.withOpacity(0.08)
            : Colors.green.withOpacity(0.08),
        borderRadius: BorderRadius.circular(12),
        border: Border.all(
          color: hasFlags
              ? Colors.orange.withOpacity(0.3)
              : Colors.green.withOpacity(0.3),
        ),
      ),
      child: Row(
        children: [
          Icon(
            hasFlags ? Icons.warning_amber : Icons.verified,
            color: hasFlags ? Colors.orange : Colors.green,
            size: 32,
          ),
          const SizedBox(width: 16),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  hasFlags ? 'Price Review Needed' : 'All Prices Compliant',
                  style: TextStyle(
                    fontWeight: FontWeight.bold,
                    color: hasFlags ? Colors.orange : Colors.green,
                  ),
                ),
                Text(
                  hasFlags
                      ? '${_analytics!.priceAnomaliesCount} product(s) flagged for pricing above regional median'
                      : 'Your product prices are within acceptable range',
                  style: TextStyle(
                    color: hasFlags
                        ? Colors.orange.withOpacity(0.8)
                        : Colors.green.withOpacity(0.8),
                    fontSize: 13,
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildTipsCard() {
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
          const Row(
            children: [
              Icon(Icons.lightbulb, color: Colors.blue, size: 20),
              SizedBox(width: 8),
              Text('Tips',
                  style: TextStyle(
                      fontWeight: FontWeight.bold, color: Colors.blue)),
            ],
          ),
          const SizedBox(height: 12),
          _tip('Keep prices within 15% of regional median to avoid flags'),
          _tip('Mark unavailable products to keep your catalogue accurate'),
          _tip('Add all product categories you sell to accept more vouchers'),
        ],
      ),
    );
  }

  Widget _tip(String text) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 8),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Text('• ', style: TextStyle(color: Colors.blue)),
          Expanded(
              child: Text(text,
                  style: const TextStyle(color: Colors.blue, fontSize: 13))),
        ],
      ),
    );
  }
}

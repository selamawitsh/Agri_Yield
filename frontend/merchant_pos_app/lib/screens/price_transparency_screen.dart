import 'package:flutter/material.dart';
import '../models/price_anomaly_model.dart';
import '../models/product_model.dart';
import '../services/merchant_service.dart';

class PriceTransparencyScreen extends StatefulWidget {
  const PriceTransparencyScreen({super.key});

  @override
  State<PriceTransparencyScreen> createState() =>
      _PriceTransparencyScreenState();
}

class _PriceTransparencyScreenState
    extends State<PriceTransparencyScreen> {
  final _merchantService = MerchantService();
  List<Product> _products = [];
  List<PriceAnomaly> _anomalies = [];
  bool _loading = true;

  @override
  void initState() {
    super.initState();
    _load();
  }

  Future<void> _load() async {
    setState(() => _loading = true);
    // FIX: use getPriceAnomalies() not getRedemptionHistory()
    // getRedemptionHistory() returns MerchantRedemptionSummary, not PriceAnomaly.
    final results = await Future.wait([
      _merchantService.getInventory(),
      _merchantService.getPriceAnomalies(),
    ]);
    if (mounted) {
      setState(() {
        _products  = results[0] as List<Product>;
        _anomalies = results[1] as List<PriceAnomaly>;
        _loading   = false;
      });
    }
  }

  bool _isFlagged(String productId) =>
      _anomalies.any((a) => a.productId == productId && a.resolvedAt == null);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Price Transparency'),
        backgroundColor: Colors.orange,
        foregroundColor: Colors.white,
        actions: [
          IconButton(icon: const Icon(Icons.refresh), onPressed: _load),
        ],
      ),
      body: _loading
          ? const Center(
              child: CircularProgressIndicator(color: Colors.orange))
          : Column(
              children: [
                if (_anomalies.isNotEmpty)
                  Container(
                    width: double.infinity,
                    margin: const EdgeInsets.all(16),
                    padding: const EdgeInsets.all(16),
                    decoration: BoxDecoration(
                      color: Colors.orange.withOpacity(0.1),
                      borderRadius: BorderRadius.circular(12),
                      border:
                          Border.all(color: Colors.orange.withOpacity(0.3)),
                    ),
                    child: Row(
                      children: [
                        const Icon(Icons.warning_amber,
                            color: Colors.orange),
                        const SizedBox(width: 12),
                        Expanded(
                          child: Text(
                            '${_anomalies.where((a) => a.resolvedAt == null).length} product(s) priced above regional median by more than 15%',
                            style:
                                const TextStyle(color: Colors.orange),
                          ),
                        ),
                      ],
                    ),
                  ),
                if (_products.isEmpty && !_loading)
                  const Expanded(
                    child: Center(
                      child: Text('No products found',
                          style: TextStyle(color: Colors.grey)),
                    ),
                  )
                else
                  Expanded(
                    child: RefreshIndicator(
                      onRefresh: _load,
                      child: ListView.builder(
                        padding:
                            const EdgeInsets.symmetric(horizontal: 16),
                        itemCount: _products.length,
                        itemBuilder: (ctx, i) {
                          final p = _products[i];
                          final flagged = _isFlagged(p.id);
                          final anomaly = flagged
                              ? _anomalies.firstWhere(
                                  (a) => a.productId == p.id)
                              : null;

                          return Card(
                            margin:
                                const EdgeInsets.only(bottom: 12),
                            shape: RoundedRectangleBorder(
                              borderRadius: BorderRadius.circular(12),
                              side: BorderSide(
                                color: flagged
                                    ? Colors.orange
                                    : Colors.transparent,
                                width: flagged ? 1.5 : 0,
                              ),
                            ),
                            child: Padding(
                              padding: const EdgeInsets.all(16),
                              child: Column(
                                crossAxisAlignment:
                                    CrossAxisAlignment.start,
                                children: [
                                  Row(
                                    children: [
                                      Expanded(
                                        child: Text(p.productName,
                                            style: const TextStyle(
                                                fontWeight:
                                                    FontWeight.bold,
                                                fontSize: 16)),
                                      ),
                                      if (flagged)
                                        Container(
                                          padding:
                                              const EdgeInsets.symmetric(
                                                  horizontal: 8,
                                                  vertical: 4),
                                          decoration: BoxDecoration(
                                            color: Colors.orange
                                                .withOpacity(0.1),
                                            borderRadius:
                                                BorderRadius.circular(8),
                                          ),
                                          child: const Text(
                                              '⚠ Price Flag',
                                              style: TextStyle(
                                                  color: Colors.orange,
                                                  fontSize: 12,
                                                  fontWeight:
                                                      FontWeight.bold)),
                                        ),
                                    ],
                                  ),
                                  const SizedBox(height: 8),
                                  Text(p.productCategory,
                                      style: const TextStyle(
                                          color: Colors.grey,
                                          fontSize: 12)),
                                  const SizedBox(height: 12),
                                  Row(
                                    children: [
                                      _priceBox(
                                          'Your Price',
                                          'ETB ${p.currentPriceEtb.toStringAsFixed(2)}',
                                          flagged
                                              ? Colors.orange
                                              : Colors.green),
                                      const SizedBox(width: 12),
                                      if (anomaly != null)
                                        _priceBox(
                                          'Regional Median',
                                          'ETB ${anomaly.regionalMedianEtb.toStringAsFixed(2)}',
                                          Colors.blue,
                                        ),
                                    ],
                                  ),
                                  if (flagged && anomaly != null) ...[
                                    const SizedBox(height: 8),
                                    Text(
                                      'Your price is ${anomaly.deviationPct.toStringAsFixed(1)}% above the regional median.',
                                      style: const TextStyle(
                                          color: Colors.orange,
                                          fontSize: 12),
                                    ),
                                  ],
                                ],
                              ),
                            ),
                          );
                        },
                      ),
                    ),
                  ),
              ],
            ),
    );
  }

  Widget _priceBox(String label, String value, Color color) {
    return Expanded(
      child: Container(
        padding: const EdgeInsets.all(10),
        decoration: BoxDecoration(
          color: color.withOpacity(0.08),
          borderRadius: BorderRadius.circular(8),
        ),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(label,
                style: TextStyle(color: color, fontSize: 11)),
            const SizedBox(height: 4),
            Text(value,
                style: TextStyle(
                    color: color,
                    fontWeight: FontWeight.bold,
                    fontSize: 15)),
          ],
        ),
      ),
    );
  }
}

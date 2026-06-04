import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import '../models/analytics_model.dart';
import '../services/merchant_service.dart';

class AnalyticsScreen extends StatefulWidget {
  const AnalyticsScreen({super.key});

  @override
  State<AnalyticsScreen> createState() => _AnalyticsScreenState();
}

class _AnalyticsScreenState extends State<AnalyticsScreen>
    with SingleTickerProviderStateMixin {
  final _merchantService = MerchantService();
  MerchantAnalytics? _analytics;
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
        bottom: TabBar(
          controller: _tabController,
          labelColor: Colors.white,
          unselectedLabelColor: Colors.white70,
          indicatorColor: Colors.white,
          tabs: const [
            Tab(text: 'Revenue'),
            Tab(text: 'Inventory'),
          ],
        ),
      ),
      body: _loading
          ? const Center(
              child: CircularProgressIndicator(color: Colors.orange))
          : _analytics == null
              ? const Center(child: Text('Could not load analytics'))
              : TabBarView(
                  controller: _tabController,
                  children: [
                    _buildRevenueTab(),
                    _buildInventoryTab(),
                  ],
                ),
    );
  }

  // ── Tab 1: Revenue and redemption stats (MS-10) ───────────────────────────

  Widget _buildRevenueTab() {
    final a = _analytics!;
    final fmt = NumberFormat('#,##0.00');
    final fmtInt = NumberFormat('#,##0');

    return RefreshIndicator(
      onRefresh: _load,
      child: SingleChildScrollView(
        physics: const AlwaysScrollableScrollPhysics(),
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text('Revenue Overview',
                style:
                    TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
            const SizedBox(height: 16),

            // Revenue cards row
            Row(children: [
              Expanded(
                child: _revenueCard(
                  label:    'Total Revenue',
                  value:    'ETB ${fmt.format(a.totalRevenueEtb)}',
                  sub:      'All time',
                  color:    Colors.green,
                  icon:     Icons.payments,
                ),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: _revenueCard(
                  label:    'This Month',
                  value:    'ETB ${fmt.format(a.revenueThisMonthEtb)}',
                  sub:      DateFormat('MMMM').format(DateTime.now()),
                  color:    Colors.blue,
                  icon:     Icons.calendar_month,
                ),
              ),
            ]),
            const SizedBox(height: 12),
            Row(children: [
              Expanded(
                child: _revenueCard(
                  label: 'This Week',
                  value: 'ETB ${fmt.format(a.revenueThisWeekEtb)}',
                  sub:   'Last 7 days',
                  color: Colors.purple,
                  icon:  Icons.date_range,
                ),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: _revenueCard(
                  label: 'Total Vouchers',
                  value: fmtInt.format(a.totalVouchersRedeemed),
                  sub:   'All time',
                  color: Colors.orange,
                  icon:  Icons.confirmation_number,
                ),
              ),
            ]),
            const SizedBox(height: 12),
            Row(children: [
              Expanded(
                child: _revenueCard(
                  label: 'This Month',
                  value: fmtInt.format(a.vouchersRedeemedThisMonth),
                  sub:   'Vouchers redeemed',
                  color: Colors.teal,
                  icon:  Icons.qr_code_scanner,
                ),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: _revenueCard(
                  label: 'This Week',
                  value: fmtInt.format(a.vouchersRedeemedThisWeek),
                  sub:   'Vouchers redeemed',
                  color: Colors.deepOrange,
                  icon:  Icons.qr_code,
                ),
              ),
            ]),

            // Revenue trend
            if (a.revenueTrend.isNotEmpty) ...[
              const SizedBox(height: 24),
              const Text('Revenue Trend (30 days)',
                  style: TextStyle(
                      fontSize: 16, fontWeight: FontWeight.bold)),
              const SizedBox(height: 12),
              _buildRevenueTrend(a.revenueTrend),
            ],

            // Top redeemed products
            if (a.topRedeemedProducts.isNotEmpty) ...[
              const SizedBox(height: 24),
              const Text('Top Products',
                  style: TextStyle(
                      fontSize: 16, fontWeight: FontWeight.bold)),
              const SizedBox(height: 12),
              ...a.topRedeemedProducts
                  .asMap()
                  .entries
                  .map((e) => _buildTopProductRow(e.key + 1, e.value)),
            ],
          ],
        ),
      ),
    );
  }

  Widget _revenueCard({
    required String label,
    required String value,
    required String sub,
    required Color color,
    required IconData icon,
  }) {
    return Container(
      padding: const EdgeInsets.all(14),
      decoration: BoxDecoration(
        color: color.withOpacity(0.07),
        borderRadius: BorderRadius.circular(12),
        border: Border.all(color: color.withOpacity(0.2)),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(children: [
            Icon(icon, color: color, size: 18),
            const SizedBox(width: 6),
            Expanded(
              child: Text(label,
                  style: TextStyle(
                      color: color.withOpacity(0.8),
                      fontSize: 11,
                      fontWeight: FontWeight.w500),
                  overflow: TextOverflow.ellipsis),
            ),
          ]),
          const SizedBox(height: 8),
          Text(value,
              style: TextStyle(
                  color: color,
                  fontSize: 16,
                  fontWeight: FontWeight.bold)),
          Text(sub,
              style: TextStyle(
                  color: color.withOpacity(0.6), fontSize: 11)),
        ],
      ),
    );
  }

  Widget _buildRevenueTrend(List<RevenuePoint> trend) {
    if (trend.isEmpty) return const SizedBox.shrink();
    final maxRevenue = trend
        .map((p) => p.revenueEtb)
        .reduce((a, b) => a > b ? a : b);
    if (maxRevenue == 0) return const SizedBox.shrink();

    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(12),
        border: Border.all(color: Colors.grey.shade200),
      ),
      child: Column(
        children: trend.map((p) {
          final pct = p.revenueEtb / maxRevenue;
          return Padding(
            padding: const EdgeInsets.only(bottom: 8),
            child: Row(children: [
              SizedBox(
                width: 60,
                child: Text(
                  _shortDate(p.date),
                  style: const TextStyle(
                      fontSize: 11, color: Colors.grey),
                ),
              ),
              Expanded(
                child: ClipRRect(
                  borderRadius: BorderRadius.circular(4),
                  child: LinearProgressIndicator(
                    value: pct,
                    minHeight: 14,
                    backgroundColor: Colors.grey.shade100,
                    valueColor: const AlwaysStoppedAnimation<Color>(
                        Colors.orange),
                  ),
                ),
              ),
              const SizedBox(width: 8),
              SizedBox(
                width: 70,
                child: Text(
                  'ETB ${NumberFormat('#,##0').format(p.revenueEtb)}',
                  style: const TextStyle(
                      fontSize: 11,
                      fontWeight: FontWeight.w600,
                      color: Colors.orange),
                  textAlign: TextAlign.right,
                ),
              ),
            ]),
          );
        }).toList(),
      ),
    );
  }

  Widget _buildTopProductRow(int rank, TopProduct p) {
    final color = rank == 1
        ? Colors.amber
        : rank == 2
            ? Colors.grey
            : Colors.brown.shade300;
    return Container(
      margin: const EdgeInsets.only(bottom: 8),
      padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 12),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(10),
        border: Border.all(color: Colors.grey.shade200),
      ),
      child: Row(children: [
        CircleAvatar(
          radius: 14,
          backgroundColor: color.withOpacity(0.15),
          child: Text(
            '$rank',
            style: TextStyle(
                color: color,
                fontWeight: FontWeight.bold,
                fontSize: 13),
          ),
        ),
        const SizedBox(width: 12),
        Expanded(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(p.productName,
                  style: const TextStyle(fontWeight: FontWeight.w600)),
              Text(p.productCategory,
                  style: const TextStyle(
                      fontSize: 11, color: Colors.grey)),
            ],
          ),
        ),
        Column(
          crossAxisAlignment: CrossAxisAlignment.end,
          children: [
            Text(
              '${p.redemptionCount} redeemed',
              style: const TextStyle(
                  fontSize: 12, color: Colors.orange),
            ),
            Text(
              'ETB ${NumberFormat('#,##0').format(p.totalRevenueEtb)}',
              style: const TextStyle(
                  fontSize: 12,
                  fontWeight: FontWeight.bold,
                  color: Colors.green),
            ),
          ],
        ),
      ]),
    );
  }

  // ── Tab 2: Inventory stats ────────────────────────────────────────────────

  Widget _buildInventoryTab() {
    final a = _analytics!;
    return RefreshIndicator(
      onRefresh: _load,
      child: SingleChildScrollView(
        physics: const AlwaysScrollableScrollPhysics(),
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text('Inventory Overview',
                style:
                    TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
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
                    a.totalProducts.toString(),
                    Icons.inventory_2, Colors.blue),
                _statCard('Available',
                    a.availableProducts.toString(),
                    Icons.check_circle, Colors.green),
                _statCard('Price Flags',
                    a.priceAnomaliesCount.toString(),
                    Icons.warning, Colors.orange),
                _statCard('Avg Price',
                    'ETB ${a.averageProductPrice.toStringAsFixed(0)}',
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
    );
  }

  Widget _statCard(
      String label, String value, IconData icon, Color color) {
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
                  style: TextStyle(
                      color: color.withOpacity(0.7), fontSize: 12)),
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
                : Colors.green.withOpacity(0.3)),
      ),
      child: Row(children: [
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
                    color: hasFlags ? Colors.orange : Colors.green),
              ),
              Text(
                hasFlags
                    ? '${_analytics!.priceAnomaliesCount} product(s) flagged'
                    : 'Your prices are within acceptable range',
                style: TextStyle(
                    color: hasFlags
                        ? Colors.orange.withOpacity(0.8)
                        : Colors.green.withOpacity(0.8),
                    fontSize: 13),
              ),
            ],
          ),
        ),
      ]),
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
          const Row(children: [
            Icon(Icons.lightbulb, color: Colors.blue, size: 20),
            SizedBox(width: 8),
            Text('Tips',
                style: TextStyle(
                    fontWeight: FontWeight.bold, color: Colors.blue)),
          ]),
          const SizedBox(height: 12),
          _tip('Keep prices within 15% of regional median to avoid flags'),
          _tip('Mark unavailable products to keep your catalogue accurate'),
          _tip(
              'Add all product categories you sell to accept more vouchers'),
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
          const Text('- ',
              style: TextStyle(color: Colors.blue)),
          Expanded(
              child: Text(text,
                  style: const TextStyle(
                      color: Colors.blue, fontSize: 13))),
        ],
      ),
    );
  }

  String _shortDate(String iso) {
    try {
      final dt = DateTime.parse(iso);
      return DateFormat('dd MMM').format(dt);
    } catch (_) {
      return iso;
    }
  }
}

import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import '../services/auth_service.dart';
import '../services/merchant_service.dart';
import '../models/analytics_model.dart';
import '../models/voucher_model.dart';
import '../widgets/voucher_category_badge.dart';
import 'profile_screen.dart';
import 'scanner_screen.dart';
import 'products_screen.dart';
import 'analytics_screen.dart';
import 'transaction_history_screen.dart';
import 'price_transparency_screen.dart';
import 'wallet_screen.dart';

class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});
  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  final _authService     = AuthService();
  final _merchantService = MerchantService();

  String _businessName = 'Merchant';
  bool   _isVerified   = false;
  MerchantAnalytics? _analytics;
  List<MerchantRedemptionSummary> _recentRedemptions = [];
  bool _loading = true;

  @override
  void initState() {
    super.initState();
    _loadData();
  }

  Future<void> _loadData() async {
    setState(() => _loading = true);
    final results = await Future.wait([
      _authService.getCurrentUser(),
      _merchantService.getMyProfile(),
      _merchantService.getAnalytics(),
      _merchantService.getRedemptionHistory(size: 5),
    ]);

    if (mounted) {
      final user      = results[0];
      final profile   = results[1];
      final analytics = results[2] as MerchantAnalytics?;
      final recent    = results[3] as List<MerchantRedemptionSummary>;

      setState(() {
        _businessName       = (profile as dynamic)?.businessName
                           ?? (user as dynamic)?.fullName
                           ?? 'Merchant';
        _isVerified         = (profile as dynamic)?.isPhysicallyVerified ?? false;
        _analytics          = analytics;
        _recentRedemptions  = recent;
        _loading            = false;
      });
    }
  }

  Future<void> _logout() async {
    await _authService.logout();
    if (mounted) Navigator.pushReplacementNamed(context, '/login');
  }

  // Today's stats from analytics
  int    get _todayCount   => _analytics?.vouchersRedeemedThisWeek  ?? 0;
  double get _todayRevenue => _analytics?.revenueThisWeekEtb        ?? 0;

  @override
  Widget build(BuildContext context) {
    final fmt = NumberFormat('#,##0.00');

    return Scaffold(
      appBar: AppBar(
        title: const Text('Merchant POS'),
        backgroundColor: Colors.orange,
        foregroundColor: Colors.white,
        actions: [
          IconButton(
            icon: const Icon(Icons.account_balance_wallet),
            onPressed: () => Navigator.push(context,
                MaterialPageRoute(builder: (_) => const WalletScreen())),
          ),
          IconButton(
            icon: const Icon(Icons.person),
            onPressed: () => Navigator.push(context,
                MaterialPageRoute(builder: (_) => const ProfileScreen())),
          ),
          IconButton(
              icon: const Icon(Icons.logout), onPressed: _logout),
        ],
      ),
      body: RefreshIndicator(
        onRefresh: _loadData,
        color: Colors.orange,
        child: SingleChildScrollView(
          physics: const AlwaysScrollableScrollPhysics(),
          child: Column(
            children: [
              // ── Hero header ────────────────────────────────────────────
              Container(
                width: double.infinity,
                padding: const EdgeInsets.fromLTRB(24, 24, 24, 28),
                decoration: const BoxDecoration(
                  gradient: LinearGradient(
                    colors: [Colors.orange, Colors.deepOrange],
                    begin: Alignment.topLeft,
                    end: Alignment.bottomRight,
                  ),
                ),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    const Text('Welcome,',
                        style: TextStyle(color: Colors.white70, fontSize: 14)),
                    Text(_businessName,
                        style: const TextStyle(
                            color: Colors.white,
                            fontSize: 26,
                            fontWeight: FontWeight.bold)),
                    const SizedBox(height: 6),
                    Row(children: [
                      Icon(
                        _isVerified ? Icons.verified : Icons.pending,
                        color: _isVerified
                            ? Colors.greenAccent
                            : Colors.white70,
                        size: 16),
                      const SizedBox(width: 4),
                      Text(
                        _isVerified
                            ? 'Verified Merchant'
                            : 'Verification Pending',
                        style: TextStyle(
                          color: _isVerified
                              ? Colors.greenAccent
                              : Colors.white70,
                          fontSize: 12)),
                    ]),
                  ],
                ),
              ),

              // ── Today's stats (SRS §6.2.2) ─────────────────────────────
              Container(
                margin: const EdgeInsets.all(16),
                padding: const EdgeInsets.all(16),
                decoration: BoxDecoration(
                  color: Colors.white,
                  borderRadius: BorderRadius.circular(14),
                  boxShadow: [
                    BoxShadow(
                      color: Colors.black.withOpacity(0.05),
                      blurRadius: 8,
                      offset: const Offset(0, 2)),
                  ],
                ),
                child: _loading
                    ? const Center(
                        child: Padding(
                          padding: EdgeInsets.all(16),
                          child: CircularProgressIndicator(
                              color: Colors.orange, strokeWidth: 2)))
                    : Row(
                        mainAxisAlignment: MainAxisAlignment.spaceAround,
                        children: [
                          _statTile(
                            label:  'This Week',
                            value:  '$_todayCount',
                            sub:    'vouchers',
                            icon:   Icons.qr_code_scanner,
                            color:  Colors.orange),
                          _vDivider(),
                          _statTile(
                            label:  'This Week',
                            value:  'ETB ${fmt.format(_todayRevenue)}',
                            sub:    'revenue',
                            icon:   Icons.payments,
                            color:  Colors.green),
                          _vDivider(),
                          _statTile(
                            label:  'Price Flags',
                            value:  '${_analytics?.priceAnomaliesCount ?? 0}',
                            sub:    'products',
                            icon:   Icons.warning_amber,
                            color:  (_analytics?.priceAnomaliesCount ?? 0) > 0
                                ? Colors.red
                                : Colors.grey),
                        ],
                      ),
              ),

              // ── Quick actions ──────────────────────────────────────────
              Padding(
                padding: const EdgeInsets.symmetric(horizontal: 16),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    const Text('Quick Actions',
                        style: TextStyle(
                            fontWeight: FontWeight.bold, fontSize: 16)),
                    const SizedBox(height: 12),
                    GridView.count(
                      shrinkWrap: true,
                      physics: const NeverScrollableScrollPhysics(),
                      crossAxisCount: 3,
                      mainAxisSpacing: 10,
                      crossAxisSpacing: 10,
                      childAspectRatio: 0.95,
                      children: [
                        _menuCard(Icons.qr_code_scanner, 'Scan', Colors.orange,
                            () => Navigator.push(context,
                                MaterialPageRoute(
                                    builder: (_) => const ScannerScreen()))),
                        _menuCard(Icons.inventory_2, 'Products', Colors.green,
                            () => Navigator.push(context,
                                MaterialPageRoute(
                                    builder: (_) => const ProductsScreen()))),
                        _menuCard(Icons.receipt_long, 'History', Colors.blue,
                            () => Navigator.push(context,
                                MaterialPageRoute(
                                    builder: (_) =>
                                        const TransactionHistoryScreen()))),
                        _menuCard(Icons.analytics, 'Analytics', Colors.purple,
                            () => Navigator.push(context,
                                MaterialPageRoute(
                                    builder: (_) => const AnalyticsScreen()))),
                        _menuCard(Icons.price_check, 'Prices', Colors.teal,
                            () => Navigator.push(context,
                                MaterialPageRoute(
                                    builder: (_) =>
                                        const PriceTransparencyScreen()))),
                        _menuCard(
                            Icons.account_balance_wallet,
                            'Wallet',
                            Colors.deepPurple,
                            () => Navigator.push(context,
                                MaterialPageRoute(
                                    builder: (_) => const WalletScreen()))),
                      ],
                    ),
                  ],
                ),
              ),

              // ── Recent transactions (SRS §6.2.2) ──────────────────────
              if (_recentRedemptions.isNotEmpty) ...[
                Padding(
                  padding: const EdgeInsets.fromLTRB(16, 20, 16, 8),
                  child: Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      const Text('Recent Redemptions',
                          style: TextStyle(
                              fontWeight: FontWeight.bold, fontSize: 16)),
                      TextButton(
                        onPressed: () => Navigator.push(context,
                            MaterialPageRoute(
                                builder: (_) =>
                                    const TransactionHistoryScreen())),
                        child: const Text('See all',
                            style: TextStyle(color: Colors.orange)),
                      ),
                    ],
                  ),
                ),
                ..._recentRedemptions.map((r) => _recentCard(r, fmt)),
                const SizedBox(height: 8),
              ] else if (!_loading) ...[
                Padding(
                  padding: const EdgeInsets.all(24),
                  child: Column(
                    children: [
                      Icon(Icons.receipt_long_outlined,
                          size: 48, color: Colors.grey.shade300),
                      const SizedBox(height: 8),
                      Text('No redemptions yet',
                          style: TextStyle(
                              color: Colors.grey.shade400, fontSize: 14)),
                      const SizedBox(height: 4),
                      Text('Scan a farmer voucher to get started',
                          style: TextStyle(
                              color: Colors.grey.shade400, fontSize: 12)),
                    ],
                  ),
                ),
              ],

              const SizedBox(height: 24),
            ],
          ),
        ),
      ),
    );
  }

  Widget _statTile({
    required String label,
    required String value,
    required String sub,
    required IconData icon,
    required Color color,
  }) {
    return Column(
      children: [
        Icon(icon, color: color, size: 22),
        const SizedBox(height: 4),
        Text(value,
            style: TextStyle(
                color: color,
                fontSize: 16,
                fontWeight: FontWeight.bold)),
        Text(label,
            style: const TextStyle(color: Colors.grey, fontSize: 10)),
        Text(sub,
            style: const TextStyle(color: Colors.grey, fontSize: 10)),
      ],
    );
  }

  Widget _vDivider() => Container(
      height: 50, width: 0.5, color: Colors.grey.withOpacity(0.2));

  Widget _recentCard(MerchantRedemptionSummary r, NumberFormat fmt) {
    return Container(
      margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 4),
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
            color: Colors.green.withOpacity(0.1),
            shape: BoxShape.circle,
          ),
          child: const Icon(Icons.check_circle,
              color: Colors.green, size: 20),
        ),
        const SizedBox(width: 10),
        Expanded(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(r.productDescription,
                  style: const TextStyle(
                      fontWeight: FontWeight.w600, fontSize: 13),
                  maxLines: 1,
                  overflow: TextOverflow.ellipsis),
              Row(children: [
                VoucherCategoryBadge(
                    category: r.productCategory, fontSize: 10),
                const SizedBox(width: 6),
                Text(_timeAgo(r.redeemedAt),
                    style: const TextStyle(
                        fontSize: 11, color: Colors.grey)),
              ]),
            ],
          ),
        ),
        Text(
          'ETB ${fmt.format(r.amountEtb)}',
          style: const TextStyle(
              fontWeight: FontWeight.bold,
              color: Colors.green,
              fontSize: 14)),
      ]),
    );
  }

  Widget _menuCard(
      IconData icon, String title, Color color, VoidCallback onTap) {
    return Card(
      elevation: 1,
      shape:
          RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
      child: InkWell(
        onTap: onTap,
        borderRadius: BorderRadius.circular(12),
        child: Padding(
          padding: const EdgeInsets.all(10),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Container(
                padding: const EdgeInsets.all(8),
                decoration: BoxDecoration(
                  color: color.withOpacity(0.1),
                  shape: BoxShape.circle,
                ),
                child: Icon(icon, size: 24, color: color),
              ),
              const SizedBox(height: 6),
              Text(title,
                  style: TextStyle(
                      fontWeight: FontWeight.bold,
                      color: color,
                      fontSize: 11),
                  textAlign: TextAlign.center),
            ],
          ),
        ),
      ),
    );
  }

  String _timeAgo(String raw) {
    try {
      final dt   = DateTime.parse(raw);
      final diff = DateTime.now().difference(dt);
      if (diff.inMinutes < 60) return '${diff.inMinutes}m ago';
      if (diff.inHours < 24)   return '${diff.inHours}h ago';
      return '${diff.inDays}d ago';
    } catch (_) {
      return '';
    }
  }
}

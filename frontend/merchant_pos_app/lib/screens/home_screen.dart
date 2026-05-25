import 'package:flutter/material.dart';
import '../services/auth_service.dart';
import '../services/merchant_service.dart';
import '../models/analytics_model.dart';
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
  final _authService = AuthService();
  final _merchantService = MerchantService();
  String _businessName = 'Merchant';
  MerchantAnalytics? _analytics;
  bool _isVerified = false;

  @override
  void initState() {
    super.initState();
    _loadData();
  }

  Future<void> _loadData() async {
    final user = await _authService.getCurrentUser();
    final profile = await _merchantService.getMyProfile();
    final analytics = await _merchantService.getAnalytics();

    if (mounted) {
      setState(() {
        _businessName = profile?.businessName ?? user?.fullName ?? 'Merchant';
        _isVerified = profile?.isPhysicallyVerified ?? false;
        _analytics = analytics;
      });
    }
  }

  Future<void> _logout() async {
    await _authService.logout();
    if (mounted) Navigator.pushReplacementNamed(context, '/login');
  }

  @override
  Widget build(BuildContext context) {
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
          IconButton(icon: const Icon(Icons.logout), onPressed: _logout),
        ],
      ),
      body: RefreshIndicator(
        onRefresh: _loadData,
        child: SingleChildScrollView(
          physics: const AlwaysScrollableScrollPhysics(),
          child: Column(
            children: [
              // Header
              Container(
                width: double.infinity,
                padding: const EdgeInsets.fromLTRB(24, 24, 24, 32),
                decoration: const BoxDecoration(
                  gradient: LinearGradient(
                    begin: Alignment.topLeft,
                    end: Alignment.bottomRight,
                    colors: [Colors.orange, Colors.deepOrange],
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
                    const SizedBox(height: 8),
                    Row(
                      children: [
                        Icon(
                          _isVerified ? Icons.verified : Icons.pending,
                          color: _isVerified ? Colors.greenAccent : Colors.white70,
                          size: 16,
                        ),
                        const SizedBox(width: 4),
                        Text(
                          _isVerified ? 'Verified Merchant' : 'Verification Pending',
                          style: TextStyle(
                            color: _isVerified ? Colors.greenAccent : Colors.white70,
                            fontSize: 12,
                          ),
                        ),
                      ],
                    ),
                  ],
                ),
              ),

              // Quick stats
              if (_analytics != null)
                Container(
                  margin: const EdgeInsets.all(16),
                  padding: const EdgeInsets.all(16),
                  decoration: BoxDecoration(
                    color: Colors.white,
                    borderRadius: BorderRadius.circular(12),
                    boxShadow: [
                      BoxShadow(
                          color: Colors.black.withOpacity(0.05), blurRadius: 8),
                    ],
                  ),
                  child: Row(
                    mainAxisAlignment: MainAxisAlignment.spaceAround,
                    children: [
                      _quickStat('Products', _analytics!.totalProducts.toString(),
                          Colors.blue),
                      _divider(),
                      _quickStat('Available', _analytics!.availableProducts.toString(),
                          Colors.green),
                      _divider(),
                      _quickStat('Price Flags',
                          _analytics!.priceAnomaliesCount.toString(),
                          _analytics!.priceAnomaliesCount > 0
                              ? Colors.orange
                              : Colors.grey),
                    ],
                  ),
                ),

              // Main menu grid
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
                      crossAxisCount: 2,
                      mainAxisSpacing: 12,
                      crossAxisSpacing: 12,
                      childAspectRatio: 1.1,
                      children: [
                        _menuCard(Icons.qr_code_scanner, 'Scan Voucher',
                            Colors.orange, () {
                          Navigator.push(
                              context,
                              MaterialPageRoute(
                                  builder: (_) => const ScannerScreen()));
                        }),
                        _menuCard(Icons.inventory_2, 'Products', Colors.green,
                            () {
                          Navigator.push(
                              context,
                              MaterialPageRoute(
                                  builder: (_) => const ProductsScreen()));
                        }),
                        _menuCard(Icons.receipt_long, 'History', Colors.blue,
                            () {
                          Navigator.push(
                              context,
                              MaterialPageRoute(
                                  builder: (_) =>
                                      const TransactionHistoryScreen()));
                        }),
                        _menuCard(Icons.analytics, 'Analytics', Colors.purple,
                            () {
                          Navigator.push(
                              context,
                              MaterialPageRoute(
                                  builder: (_) => const AnalyticsScreen()));
                        }),
                        _menuCard(Icons.price_check, 'Prices', Colors.teal,
                            () {
                          Navigator.push(
                              context,
                              MaterialPageRoute(
                                  builder: (_) =>
                                      const PriceTransparencyScreen()));
                        }),
                        _menuCard(Icons.account_balance_wallet, 'Wallet',
                            Colors.deepPurple, () {
                          Navigator.push(
                              context,
                              MaterialPageRoute(
                                  builder: (_) => const WalletScreen()));
                        }),
                      ],
                    ),
                    const SizedBox(height: 24),
                  ],
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _quickStat(String label, String value, Color color) {
    return Column(
      children: [
        Text(value,
            style: TextStyle(
                color: color, fontSize: 24, fontWeight: FontWeight.bold)),
        Text(label, style: const TextStyle(color: Colors.grey, fontSize: 12)),
      ],
    );
  }

  Widget _divider() {
    return Container(height: 40, width: 1, color: Colors.grey.withOpacity(0.2));
  }

  Widget _menuCard(
      IconData icon, String title, Color color, VoidCallback onTap) {
    return Card(
      elevation: 2,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
      child: InkWell(
        onTap: onTap,
        borderRadius: BorderRadius.circular(12),
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Container(
                padding: const EdgeInsets.all(12),
                decoration: BoxDecoration(
                  color: color.withOpacity(0.1),
                  shape: BoxShape.circle,
                ),
                child: Icon(icon, size: 32, color: color),
              ),
              const SizedBox(height: 10),
              Text(title,
                  style: TextStyle(
                      fontWeight: FontWeight.bold, color: color, fontSize: 13)),
            ],
          ),
        ),
      ),
    );
  }
}

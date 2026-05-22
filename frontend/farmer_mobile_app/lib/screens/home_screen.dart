import 'package:flutter/material.dart';
import 'profile_screen.dart';
import 'farm/my_farms_screen.dart';
import '../services/auth_service.dart';

class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  final _authService = AuthService();
  String _userName = 'Farmer';

  @override
  void initState() {
    super.initState();
    _loadUser();
  }

  Future<void> _loadUser() async {
    final user = await _authService.getCurrentUser();
    if (mounted) {
      setState(() => _userName = user?.fullName ?? 'Farmer');
    }
  }

  Future<void> _logout() async {
    await _authService.logout();
    if (mounted) {
      Navigator.pushReplacementNamed(context, '/login');
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text('Yogyakarta Sector', style: TextStyle(fontSize: 16, fontWeight: FontWeight.w900)),
            Text('Central Registry Console'.toUpperCase(), style: TextStyle(fontSize: 10, color: Colors.green.shade200, fontWeight: FontWeight.bold, letterSpacing: 1)),
          ],
        ),
        actions: [
          IconButton(
            icon: const Icon(Icons.account_circle_outlined, size: 26),
            onPressed: () => Navigator.push(context, MaterialPageRoute(builder: (_) => const ProfileScreen())),
          ),
          IconButton(
            icon: const Icon(Icons.power_settings_new_rounded, size: 24),
            onPressed: _logout,
          ),
          const SizedBox(width: 8),
        ],
      ),
      body: Column(
        children: [
          // Premium Multi-toned Header Banner Section
          Container(
            width: double.infinity,
            decoration: const BoxDecoration(
              color: Color(0xFF1B4332),
              borderRadius: BorderRadius.only(
                bottomLeft: Radius.circular(32),
                bottomRight: Radius.circular(32),
              ),
            ),
            padding: const EdgeInsets.only(left: 24, right: 24, bottom: 32, top: 8),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                const Text('Welcome back,', style: TextStyle(color: Colors.white60, fontSize: 14, fontWeight: FontWeight.w500)),
                const SizedBox(height: 2),
                Text(
                  _userName,
                  style: const TextStyle(color: Colors.white, fontSize: 32, fontWeight: FontWeight.w900, letterSpacing: -1),
                ),
                const SizedBox(height: 12),
                Container(
                  padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
                  decoration: BoxDecoration(
                    color: Colors.white.withOpacity(0.07),
                    borderRadius: BorderRadius.circular(12),
                  ),
                  child: Row(
                    children: [
                      Icon(Icons.gpp_good, color: Colors.green.shade300, size: 16),
                      const SizedBox(width: 8),
                      const Expanded(
                        child: Text(
                          'Ecosystem Identity Ledger Active',
                          style: TextStyle(color: Colors.white, fontSize: 11, fontWeight: FontWeight.bold),
                        ),
                      ),
                    ],
                  ),
                ),
              ],
            ),
          ),

          // Action Cards Grid Framework
          Expanded(
            child: GridView.count(
              padding: const EdgeInsets.all(20),
              crossAxisCount: 2,
              mainAxisSpacing: 16,
              crossAxisSpacing: 16,
              childAspectRatio: 1.05,
              children: [
                _buildMenuCard(
                  Icons.agriculture_rounded,
                  'My Farms',
                  'Cultivation Nodes',
                  const Color(0xFF2D6A4F),
                      () => Navigator.push(context, MaterialPageRoute(builder: (_) => const MyFarmsScreen())),
                ),
                _buildMenuCard(
                  Icons.qr_code_scanner_rounded,
                  'Vouchers',
                  'Logistics Pipeline',
                  const Color(0xFF78350F),
                      () => _showToast(context, 'Vouchers setup coming online soon'),
                ),
                _buildMenuCard(
                  Icons.blur_circular_rounded,
                  'AI Advisor',
                  'Yield Topology',
                  const Color(0xFF334155),
                      () => _showToast(context, 'AI Vector tracking coming online soon'),
                ),
                _buildMenuCard(
                  Icons.wb_sunny_outlined,
                  'Weather & NDVI',
                  'Satellite Sync',
                  const Color(0xFF0F291B),
                      () => _showToast(context, 'NDVI Maps integration coming online soon'),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  void _showToast(BuildContext context, String message) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        backgroundColor: const Color(0xFF0F291B),
        behavior: SnackBarBehavior.floating,
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
        content: Text(message, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 12, color: Colors.white)),
      ),
    );
  }

  Widget _buildMenuCard(IconData icon, String title, String subtitle, Color color, VoidCallback onTap) {
    return Container(
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(24),
        border: Border.all(color: const Color(0xFFE2E8F0), width: 1),
        boxShadow: [
          BoxShadow(color: const Color(0xFF0F291B).withOpacity(0.02), offset: const Offset(0, 4), blurRadius: 12),
        ],
      ),
      child: Material(
        color: Colors.transparent,
        child: InkWell(
          onTap: onTap,
          borderRadius: BorderRadius.circular(24),
          child: Padding(
            padding: const EdgeInsets.all(20),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Container(
                  padding: const EdgeInsets.all(10),
                  decoration: BoxDecoration(color: color.withOpacity(0.08), borderRadius: BorderRadius.circular(14)),
                  child: Icon(icon, size: 26, color: color),
                ),
                Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(title, style: const TextStyle(fontWeight: FontWeight.w900, fontSize: 15, color: Color(0xFF0F291B), letterSpacing: -0.4)),
                    const SizedBox(height: 2),
                    Text(subtitle, style: TextStyle(fontWeight: FontWeight.bold, fontSize: 10, color: Colors.blueGrey.shade400, letterSpacing: 0.1)),
                  ],
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
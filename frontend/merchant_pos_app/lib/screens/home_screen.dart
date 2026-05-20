import 'package:flutter/material.dart';
import 'profile_screen.dart';
import '../services/auth_service.dart';

class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  final _authService = AuthService();
  String _businessName = 'Merchant';

  @override
  void initState() {
    super.initState();
    _loadUser();
  }

  Future<void> _loadUser() async {
    final user = await _authService.getCurrentUser();
    if (mounted) {
      setState(() => _businessName = user?.businessName ?? user?.fullName ?? 'Merchant');
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
        title: const Text('Merchant POS'),
        backgroundColor: Colors.orange,
        foregroundColor: Colors.white,
        actions: [
          IconButton(
            icon: const Icon(Icons.person),
            onPressed: () => Navigator.push(context, MaterialPageRoute(builder: (_) => const ProfileScreen())),
          ),
          IconButton(
            icon: const Icon(Icons.logout),
            onPressed: _logout,
          ),
        ],
      ),
      body: Column(
        children: [
          Container(
            width: double.infinity,
            padding: const EdgeInsets.all(24),
            color: Colors.orange,
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                const Text('Welcome,', style: TextStyle(color: Colors.white70, fontSize: 16)),
                Text(_businessName, style: const TextStyle(color: Colors.white, fontSize: 28, fontWeight: FontWeight.bold)),
                const Text('Process voucher payments and manage your shop', style: TextStyle(color: Colors.white70)),
              ],
            ),
          ),
          Expanded(
            child: GridView.count(
              padding: const EdgeInsets.all(16),
              crossAxisCount: 2,
              mainAxisSpacing: 16,
              crossAxisSpacing: 16,
              children: [
                _buildMenuCard(Icons.qr_code_scanner, 'Scan Voucher', Colors.orange, () => {}),
                _buildMenuCard(Icons.receipt, 'Today\'s Sales', Colors.blue, () => {}),
                _buildMenuCard(Icons.inventory, 'Products', Colors.green, () => {}),
                _buildMenuCard(Icons.analytics, 'Reports', Colors.purple, () => {}),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildMenuCard(IconData icon, String title, Color color, VoidCallback onTap) {
    return Card(
      child: InkWell(
        onTap: onTap,
        borderRadius: BorderRadius.circular(12),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(icon, size: 48, color: color),
            const SizedBox(height: 12),
            Text(title, style: TextStyle(fontWeight: FontWeight.bold, color: color)),
          ],
        ),
      ),
    );
  }
}

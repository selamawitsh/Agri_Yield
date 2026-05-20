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
        title: const Text('Farmer Dashboard'),
        backgroundColor: Colors.green,
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
            color: Colors.green,
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                const Text('Welcome,', style: TextStyle(color: Colors.white70, fontSize: 16)),
                Text(_userName, style: const TextStyle(color: Colors.white, fontSize: 28, fontWeight: FontWeight.bold)),
                const Text('Manage your farms and track crop health', style: TextStyle(color: Colors.white70)),
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
                _buildMenuCard(Icons.agriculture, 'My Farms', Colors.green, () => {}),
                _buildMenuCard(Icons.qr_code, 'Vouchers', Colors.blue, () => {}),
                _buildMenuCard(Icons.mic, 'AI Advisor', Colors.purple, () => {}),
                _buildMenuCard(Icons.cloud, 'Weather & NDVI', Colors.orange, () => {}),
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

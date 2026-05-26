import 'package:flutter/material.dart';
import '../services/auth_service.dart';

class RegisterScreen extends StatefulWidget {
  const RegisterScreen({super.key});
  @override
  State<RegisterScreen> createState() => _RegisterScreenState();
}

class _RegisterScreenState extends State<RegisterScreen> {
  final _nameCtrl    = TextEditingController();
  final _phoneCtrl   = TextEditingController();
  final _faydaCtrl   = TextEditingController();
  final _passCtrl    = TextEditingController();
  final _confirmCtrl = TextEditingController();
  final _otpCtrl     = TextEditingController();
  final _auth        = AuthService();

  bool _loading  = false;
  bool _otpSent  = false;
  bool _obscure  = true;
  String _error  = '';

  Future<void> _register() async {
    if (_passCtrl.text != _confirmCtrl.text) {
      setState(() => _error = 'Passwords do not match'); return;
    }
    if (_passCtrl.text.length < 8) {
      setState(() => _error = 'Password must be at least 8 characters'); return;
    }
    setState(() { _loading = true; _error = ''; });
    final result = await _auth.register(
      phone: _phoneCtrl.text,
      faydaId: _faydaCtrl.text,
      password: _passCtrl.text,
      fullName: _nameCtrl.text,
    );
    setState(() => _loading = false);
    if (result['success'] == true) {
      setState(() => _otpSent = true);
    } else {
      setState(() => _error = result['message'] ?? 'Registration failed');
    }
  }

  Future<void> _verifyOtp() async {
    setState(() { _loading = true; _error = ''; });
    final result = await _auth.verifyOtp(
      phone: _phoneCtrl.text,
      otpCode: _otpCtrl.text,
    );
    setState(() => _loading = false);
    if (result['success'] == true) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Account created! Please login.')));
        Navigator.pushReplacementNamed(context, '/login');
      }
    } else {
      setState(() => _error = result['message'] ?? 'OTP verification failed');
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.grey[50],
      appBar: AppBar(
        title: Text(_otpSent ? 'Verify Phone' : 'Create Account'),
        backgroundColor: Colors.green[700],
        foregroundColor: Colors.white,
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(24),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            if (_error.isNotEmpty)
              Container(
                padding: const EdgeInsets.all(12),
                margin: const EdgeInsets.only(bottom: 16),
                decoration: BoxDecoration(
                  color: Colors.red[50],
                  border: Border.all(color: Colors.red[200]!),
                  borderRadius: BorderRadius.circular(8),
                ),
                child: Text(_error, style: TextStyle(color: Colors.red[700], fontSize: 13)),
              ),

            if (!_otpSent) ...[
              const Text('Personal Information',
                  style: TextStyle(fontWeight: FontWeight.bold, fontSize: 16, color: Colors.black87)),
              const SizedBox(height: 12),
              _field(_nameCtrl, 'Full Name', Icons.person),
              const SizedBox(height: 12),
              _field(_phoneCtrl, 'Phone Number (+251...)', Icons.phone, type: TextInputType.phone),
              const SizedBox(height: 12),
              _field(_faydaCtrl, 'Fayda National ID', Icons.badge),
              const SizedBox(height: 20),
              const Text('Security',
                  style: TextStyle(fontWeight: FontWeight.bold, fontSize: 16, color: Colors.black87)),
              const SizedBox(height: 12),
              TextField(
                controller: _passCtrl,
                obscureText: _obscure,
                decoration: InputDecoration(
                  labelText: 'Password (min 8 characters)',
                  prefixIcon: const Icon(Icons.lock),
                  suffixIcon: IconButton(
                    icon: Icon(_obscure ? Icons.visibility : Icons.visibility_off),
                    onPressed: () => setState(() => _obscure = !_obscure),
                  ),
                  border: const OutlineInputBorder(),
                  filled: true, fillColor: Colors.white,
                ),
              ),
              const SizedBox(height: 12),
              TextField(
                controller: _confirmCtrl,
                obscureText: true,
                decoration: const InputDecoration(
                  labelText: 'Confirm Password',
                  prefixIcon: Icon(Icons.lock_outline),
                  border: OutlineInputBorder(),
                  filled: true, fillColor: Colors.white,
                ),
              ),
              const SizedBox(height: 24),
              ElevatedButton(
                onPressed: _loading ? null : _register,
                style: ElevatedButton.styleFrom(
                  backgroundColor: Colors.green[700],
                  padding: const EdgeInsets.symmetric(vertical: 16),
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
                ),
                child: _loading
                    ? const CircularProgressIndicator(color: Colors.white)
                    : const Text('Create Account', style: TextStyle(fontSize: 16, color: Colors.white)),
              ),
            ] else ...[
              const Icon(Icons.sms, size: 64, color: Colors.green),
              const SizedBox(height: 16),
              Text('Enter the 6-digit code sent to ${_phoneCtrl.text}',
                  textAlign: TextAlign.center,
                  style: const TextStyle(fontSize: 16, color: Colors.black87)),
              const SizedBox(height: 24),
              TextField(
                controller: _otpCtrl,
                keyboardType: TextInputType.number,
                maxLength: 6,
                textAlign: TextAlign.center,
                style: const TextStyle(fontSize: 28, fontWeight: FontWeight.bold, letterSpacing: 8),
                decoration: const InputDecoration(
                  labelText: 'OTP Code',
                  border: OutlineInputBorder(),
                  filled: true, fillColor: Colors.white,
                  counterText: '',
                ),
              ),
              const SizedBox(height: 24),
              ElevatedButton(
                onPressed: _loading ? null : _verifyOtp,
                style: ElevatedButton.styleFrom(
                  backgroundColor: Colors.green[700],
                  padding: const EdgeInsets.symmetric(vertical: 16),
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
                ),
                child: _loading
                    ? const CircularProgressIndicator(color: Colors.white)
                    : const Text('Verify OTP', style: TextStyle(fontSize: 16, color: Colors.white)),
              ),
              const SizedBox(height: 12),
              TextButton(
                onPressed: () => setState(() => _otpSent = false),
                child: const Text('← Back to registration'),
              ),
            ],
          ],
        ),
      ),
    );
  }

  Widget _field(TextEditingController ctrl, String label, IconData icon,
      {TextInputType type = TextInputType.text}) {
    return TextField(
      controller: ctrl,
      keyboardType: type,
      decoration: InputDecoration(
        labelText: label,
        prefixIcon: Icon(icon),
        border: const OutlineInputBorder(),
        filled: true, fillColor: Colors.white,
      ),
    );
  }
}

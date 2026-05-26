import 'package:flutter/material.dart';
import '../services/auth_service.dart';

class RegisterScreen extends StatefulWidget {
  const RegisterScreen({super.key});
  @override
  State<RegisterScreen> createState() => _RegisterScreenState();
}

class _RegisterScreenState extends State<RegisterScreen> {
  final _auth = AuthService();

  // Step: 1=account form, 2=OTP
  int _step = 1;
  bool _loading = false;
  bool _obscure = true;
  String _error = '';

  // Account fields
  final _nameCtrl     = TextEditingController();
  final _phoneCtrl    = TextEditingController();
  final _faydaCtrl    = TextEditingController();
  final _passCtrl     = TextEditingController();
  final _confirmCtrl  = TextEditingController();

  // Merchant fields
  final _bizNameCtrl  = TextEditingController();
  final _licenseCtrl  = TextEditingController();
  final _latCtrl      = TextEditingController();
  final _lngCtrl      = TextEditingController();
  final _telebirrCtrl = TextEditingController();
  final _kebeleCtrl   = TextEditingController();

  // OTP
  final _otpCtrl = TextEditingController();

  @override
  void dispose() {
    for (final c in [_nameCtrl, _phoneCtrl, _faydaCtrl, _passCtrl,
        _confirmCtrl, _bizNameCtrl, _licenseCtrl, _latCtrl, _lngCtrl,
        _telebirrCtrl, _kebeleCtrl, _otpCtrl]) {
      c.dispose();
    }
    super.dispose();
  }

  String? _validatePassword(String pass) {
    if (pass.length < 8) return 'Password must be at least 8 characters';
    if (!pass.contains(RegExp(r'[A-Z]'))) return 'Password must contain an uppercase letter';
    if (!pass.contains(RegExp(r'[a-z]'))) return 'Password must contain a lowercase letter';
    if (!pass.contains(RegExp(r'[0-9]'))) return 'Password must contain a digit';
    return null;
  }

  Future<void> _register() async {
    setState(() => _error = '');

    if (_passCtrl.text != _confirmCtrl.text) {
      setState(() => _error = 'Passwords do not match'); return;
    }
    final passError = _validatePassword(_passCtrl.text);
    if (passError != null) { setState(() => _error = passError); return; }

    final lat = double.tryParse(_latCtrl.text.trim());
    final lng = double.tryParse(_lngCtrl.text.trim());
    if (lat == null || lng == null) {
      setState(() => _error = 'GPS coordinates must be valid numbers'); return;
    }

    setState(() => _loading = true);
    final result = await _auth.register(
      phone: _phoneCtrl.text.trim(),
      faydaId: _faydaCtrl.text.trim(),
      password: _passCtrl.text,
      fullName: _nameCtrl.text.trim(),
      businessName: _bizNameCtrl.text.trim(),
      businessLicenseNumber: _licenseCtrl.text.trim(),
      storeGpsLat: lat,
      storeGpsLng: lng,
      telebirrAccount: _telebirrCtrl.text.trim(),
      kebeleCode: _kebeleCtrl.text.trim().isEmpty ? null : _kebeleCtrl.text.trim(),
    );
    setState(() => _loading = false);

    if (result['success'] == true) {
      setState(() => _step = 2);
    } else {
      setState(() => _error = result['message'] ?? 'Registration failed');
    }
  }

  Future<void> _verifyOtp() async {
    setState(() { _loading = true; _error = ''; });
    final result = await _auth.verifyOtp(
      phone: _phoneCtrl.text.trim(),
      otpCode: _otpCtrl.text.trim(),
      purpose: 'REGISTRATION',
    );
    setState(() => _loading = false);

    if (result['success'] == true) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Account created! Please login.'),
              backgroundColor: Colors.green));
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
        title: Text(_step == 1 ? 'Merchant Registration' : 'Verify Phone'),
        backgroundColor: Colors.orange,
        foregroundColor: Colors.white,
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(20),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            // Progress bar
            Row(children: [
              Expanded(child: Container(height: 4,
                  decoration: BoxDecoration(color: Colors.orange,
                      borderRadius: BorderRadius.circular(2)))),
              const SizedBox(width: 4),
              Expanded(child: Container(height: 4,
                  decoration: BoxDecoration(
                      color: _step == 2 ? Colors.orange : Colors.grey[300],
                      borderRadius: BorderRadius.circular(2)))),
            ]),
            const SizedBox(height: 20),

            // Error
            if (_error.isNotEmpty)
              Container(
                padding: const EdgeInsets.all(12),
                margin: const EdgeInsets.only(bottom: 16),
                decoration: BoxDecoration(
                  color: Colors.red[50],
                  border: Border.all(color: Colors.red[200]!),
                  borderRadius: BorderRadius.circular(8),
                ),
                child: Text(_error,
                    style: TextStyle(color: Colors.red[700], fontSize: 13)),
              ),

            if (_step == 1) ...[
              _section('Business Information'),
              _field(_bizNameCtrl, 'Business Name', Icons.store),
              _field(_licenseCtrl, 'Business License Number', Icons.article),
              _field(_telebirrCtrl, 'Telebirr Account', Icons.phone_android,
                  type: TextInputType.phone),
              Row(children: [
                Expanded(child: _field(_latCtrl, 'GPS Latitude', Icons.location_on,
                    type: const TextInputType.numberWithOptions(decimal: true))),
                const SizedBox(width: 12),
                Expanded(child: _field(_lngCtrl, 'GPS Longitude', Icons.location_on,
                    type: const TextInputType.numberWithOptions(decimal: true))),
              ]),
              _field(_kebeleCtrl, 'Kebele Code (Optional)', Icons.map),

              _section('Personal Information'),
              _field(_nameCtrl, 'Full Name', Icons.person),
              _field(_phoneCtrl, 'Phone (+251XXXXXXXXX)', Icons.phone,
                  type: TextInputType.phone),
              _field(_faydaCtrl, 'Fayda National ID', Icons.badge),

              _section('Password'),
              const Text(
                'Must have: 8+ chars, uppercase, lowercase, digit',
                style: TextStyle(color: Colors.grey, fontSize: 12),
              ),
              const SizedBox(height: 8),
              TextField(
                controller: _passCtrl,
                obscureText: _obscure,
                decoration: InputDecoration(
                  labelText: 'Password',
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
              _field(_confirmCtrl, 'Confirm Password', Icons.lock_outline, obscure: true),
              const SizedBox(height: 24),
              ElevatedButton(
                onPressed: _loading ? null : _register,
                style: ElevatedButton.styleFrom(
                  backgroundColor: Colors.orange,
                  padding: const EdgeInsets.symmetric(vertical: 16),
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
                ),
                child: _loading
                    ? const CircularProgressIndicator(color: Colors.white)
                    : const Text('Register', style: TextStyle(fontSize: 16, color: Colors.white)),
              ),
              TextButton(
                onPressed: () => Navigator.pushReplacementNamed(context, '/login'),
                child: const Text('Already have an account? Login'),
              ),
            ] else ...[
              const Icon(Icons.sms_outlined, size: 64, color: Colors.orange),
              const SizedBox(height: 16),
              Text('Enter the 6-digit OTP sent to ${_phoneCtrl.text}',
                  textAlign: TextAlign.center,
                  style: const TextStyle(fontSize: 15)),
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
                  backgroundColor: Colors.orange,
                  padding: const EdgeInsets.symmetric(vertical: 16),
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
                ),
                child: _loading
                    ? const CircularProgressIndicator(color: Colors.white)
                    : const Text('Verify OTP', style: TextStyle(fontSize: 16, color: Colors.white)),
              ),
              TextButton(
                onPressed: () => setState(() { _step = 1; _error = ''; }),
                child: const Text('← Back'),
              ),
            ],
          ],
        ),
      ),
    );
  }

  Widget _section(String title) => Padding(
    padding: const EdgeInsets.only(top: 8, bottom: 12),
    child: Text(title, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 15)),
  );

  Widget _field(TextEditingController ctrl, String label, IconData icon,
      {TextInputType type = TextInputType.text, bool obscure = false}) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 12),
      child: TextField(
        controller: ctrl,
        keyboardType: type,
        obscureText: obscure,
        decoration: InputDecoration(
          labelText: label,
          prefixIcon: Icon(icon),
          border: const OutlineInputBorder(),
          filled: true, fillColor: Colors.white,
        ),
      ),
    );
  }
}

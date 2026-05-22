import 'package:flutter/material.dart';
import '../services/auth_service.dart';

class RegisterScreen extends StatefulWidget {
  const RegisterScreen({super.key});

  @override
  State<RegisterScreen> createState() => _RegisterScreenState();
}

class _RegisterScreenState extends State<RegisterScreen> {
  final _nameController = TextEditingController();
  final _phoneController = TextEditingController();
  final _faydaController = TextEditingController();
  final _passwordController = TextEditingController();
  final _confirmController = TextEditingController();
  final _otpController = TextEditingController();
  final _authService = AuthService();

  bool _loading = false;
  bool _otpSent = false;

  Future<void> _register() async {
    if (_passwordController.text != _confirmController.text) return;
    setState(() => _loading = true);

    final result = await _authService.register(
      phone: _phoneController.text,
      faydaId: _faydaController.text,
      password: _passwordController.text,
      fullName: _nameController.text,
    );

    setState(() => _loading = false);

    if (result['success']) {
      setState(() => _otpSent = true);
    }
  }

  Future<void> _verifyOtp() async {
    setState(() => _loading = true);
    final result = await _authService.verifyOtp(phone: _phoneController.text, otpCode: _otpController.text);
    setState(() => _loading = false);
    if (result['success']) {
      Navigator.pop(context);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Ecosystem Registration')),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(24),
        child: Container(
          decoration: BoxDecoration(
            color: Colors.white,
            borderRadius: BorderRadius.circular(28),
            border: Border.all(color: const Color(0xFFE2E8F0)),
          ),
          padding: const EdgeInsets.all(24),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              Text(_otpSent ? 'Enter Gateway Verification' : 'Initialize Node Mapping', style: const TextStyle(fontSize: 18, fontWeight: FontWeight.w900, color: Color(0xFF0F291B))),
              const SizedBox(height: 20),
              if (!_otpSent) ...[
                TextField(controller: _nameController, decoration: const InputDecoration(labelText: 'Full Signature Legal Name')),
                const SizedBox(height: 12),
                TextField(controller: _phoneController, decoration: const InputDecoration(labelText: 'Phone Identifier Route (+251...)')),
                const SizedBox(height: 12),
                TextField(controller: _faydaController, decoration: const InputDecoration(labelText: 'Ethiopian National Fayda ID Token')),
                const SizedBox(height: 12),
                TextField(controller: _passwordController, decoration: const InputDecoration(labelText: 'Master Security Pin'), obscureText: true),
                const SizedBox(height: 12),
                TextField(controller: _confirmController, decoration: const InputDecoration(labelText: 'Confirm Master Pin'), obscureText: true),
                const SizedBox(height: 24),
                ElevatedButton(onPressed: _register, child: _loading ? const CircularProgressIndicator(color: Colors.white) : const Text('SUBMIT PROFILE REGISTRY')),
              ] else ...[
                TextField(controller: _otpController, decoration: const InputDecoration(labelText: 'Enter Terminal OTP Pin'), keyboardType: TextInputType.number),
                const SizedBox(height: 24),
                ElevatedButton(onPressed: _verifyOtp, child: _loading ? const CircularProgressIndicator(color: Colors.white) : const Text('VERIFY SECURE OTP')),
              ],
            ],
          ),
        ),
      ),
    );
  }
}
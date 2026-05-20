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
  String _userId = '';

  Future<void> _register() async {
    if (_passwordController.text != _confirmController.text) {
      ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Passwords do not match')));
      return;
    }
    
    setState(() => _loading = true);
    
    final result = await _authService.register(
      phone: _phoneController.text,
      faydaId: _faydaController.text,
      password: _passwordController.text,
      fullName: _nameController.text,
    );
    
    setState(() => _loading = false);
    
    if (result['success']) {
      setState(() {
        _otpSent = true;
        _userId = result['userId'];
      });
      ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('OTP sent! Check backend terminal')));
    } else {
      ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text(result['message'])));
    }
  }

  Future<void> _verifyOtp() async {
    setState(() => _loading = true);
    
    final result = await _authService.verifyOtp(
      phone: _phoneController.text,
      otpCode: _otpController.text,
    );
    
    setState(() => _loading = false);
    
    if (result['success']) {
      ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Registration complete! Please login')));
      Navigator.pop(context);
    } else {
      ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text(result['message'])));
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Register'), backgroundColor: Colors.green, foregroundColor: Colors.white),
      body: Padding(
        padding: const EdgeInsets.all(16),
        child: SingleChildScrollView(
          child: Column(
            children: [
              if (!_otpSent) ...[
                TextField(controller: _nameController, decoration: const InputDecoration(labelText: 'Full Name', border: OutlineInputBorder())),
                const SizedBox(height: 12),
                TextField(controller: _phoneController, decoration: const InputDecoration(labelText: 'Phone (+251XXXXXXXXX)', border: OutlineInputBorder())),
                const SizedBox(height: 12),
                TextField(controller: _faydaController, decoration: const InputDecoration(labelText: 'Fayda ID', border: OutlineInputBorder())),
                const SizedBox(height: 12),
                TextField(controller: _passwordController, decoration: const InputDecoration(labelText: 'Password', border: OutlineInputBorder()), obscureText: true),
                const SizedBox(height: 12),
                TextField(controller: _confirmController, decoration: const InputDecoration(labelText: 'Confirm Password', border: OutlineInputBorder()), obscureText: true),
                const SizedBox(height: 24),
                SizedBox(width: double.infinity, child: ElevatedButton(onPressed: _register, style: ElevatedButton.styleFrom(backgroundColor: Colors.green, padding: const EdgeInsets.symmetric(vertical: 16)), child: _loading ? const CircularProgressIndicator() : const Text('Register'))),
              ] else ...[
                TextField(controller: _otpController, decoration: const InputDecoration(labelText: 'Enter OTP', border: OutlineInputBorder()), keyboardType: TextInputType.number),
                const SizedBox(height: 24),
                SizedBox(width: double.infinity, child: ElevatedButton(onPressed: _verifyOtp, style: ElevatedButton.styleFrom(backgroundColor: Colors.green), child: _loading ? const CircularProgressIndicator() : const Text('Verify OTP'))),
              ],
            ],
          ),
        ),
      ),
    );
  }
}

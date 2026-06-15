import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../services/auth_service.dart';
import '../services/language_service.dart';

class LoginScreen extends StatefulWidget {
  const LoginScreen({super.key});

  @override
  State<LoginScreen> createState() => _LoginScreenState();
}

class _LoginScreenState extends State<LoginScreen> {
  final _phoneController    = TextEditingController();
  final _passwordController = TextEditingController();
  final _authService        = AuthService();
  bool _loading             = false;
  bool _obscurePassword     = true;

  // ── Localised strings ────────────────────────────────────────────────────
  static const Map<String, Map<String, String>> _strings = {
    'am': {
      'subtitle':       'ወደ እርስዎ መለያ ይግቡ',
      'cardTitle':      'ግባ',
      'phoneLabel':     'ስልክ ቁጥር',
      'passwordLabel':  'የሚስጥር ቃል',
      'forgotPassword': 'የሚስጥር ቃል ረሳህ?',
      'loginButton':    'ግባ',
      'noAccount':      'መለያ የለህም? ተመዝገብ',
    },
    'om': {
      'subtitle':       'Herrega keessan seenaa',
      'cardTitle':      'Seeni',
      'phoneLabel':     'Lakkoofsa Bilbilaa',
      'passwordLabel':  'Jecha Darbii',
      'forgotPassword': 'Jecha darbii dagatte?',
      'loginButton':    'Seeni',
      'noAccount':      'Herrega hin qabduu? Galmeessi',
    },
    'en': {
      'subtitle':       'Sign in to your account',
      'cardTitle':      'Sign In',
      'phoneLabel':     'Phone Number',
      'passwordLabel':  'Password',
      'forgotPassword': 'Forgot password?',
      'loginButton':    'Sign In',
      'noAccount':      'Don\'t have an account? Register',
    },
  };

  String _t(String key) {
    final code = context.read<LanguageService>().languageCode;
    return _strings[code]?[key] ?? _strings['en']![key]!;
  }

  Future<void> _login() async {
    if (_phoneController.text.isEmpty || _passwordController.text.isEmpty) return;
    setState(() => _loading = true);

    final result = await _authService.login(
      phone:    _phoneController.text.trim(),
      password: _passwordController.text,
    );

    setState(() => _loading = false);

    if (!mounted) return;

    if (result['success'] == true) {
      Navigator.pushReplacementNamed(context, '/home');
    } else {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          backgroundColor: const Color(0xFF991B1B),
          content: Text(
            result['message'] ?? _t('loginButton'),
            style: const TextStyle(fontWeight: FontWeight.bold),
          ),
        ),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    // Watch so the screen rebuilds if language changes mid-session
    context.watch<LanguageService>();

    return Scaffold(
      backgroundColor: const Color(0xFF1B4332),
      body: SafeArea(
        child: LayoutBuilder(
          builder: (context, constraints) {
            return SingleChildScrollView(
              padding: const EdgeInsets.symmetric(horizontal: 24),
              child: ConstrainedBox(
                constraints: BoxConstraints(minHeight: constraints.maxHeight),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.stretch,
                  children: [
                    const SizedBox(height: 60),

                    // ── Branding ──────────────────────────────────────────
                    Center(
                      child: Container(
                        padding: const EdgeInsets.all(16),
                        decoration: BoxDecoration(
                          color: Colors.white.withOpacity(0.08),
                          shape: BoxShape.circle,
                        ),
                        child: const Icon(
                          Icons.wb_twilight_rounded,
                          size: 54,
                          color: Colors.white,
                        ),
                      ),
                    ),
                    const SizedBox(height: 20),
                    const Text(
                      'Agri-Yield',
                      textAlign: TextAlign.center,
                      style: TextStyle(
                        fontSize: 36,
                        fontWeight: FontWeight.w900,
                        color: Colors.white,
                        letterSpacing: -1,
                      ),
                    ),
                    const SizedBox(height: 4),
                    Text(
                      _t('subtitle'),
                      textAlign: TextAlign.center,
                      style: const TextStyle(
                        fontSize: 13,
                        color: Colors.white60,
                        fontWeight: FontWeight.w500,
                      ),
                    ),

                    const SizedBox(height: 40),

                    // ── Input card ────────────────────────────────────────
                    Container(
                      decoration: BoxDecoration(
                        color: Colors.white,
                        borderRadius: BorderRadius.circular(28),
                        boxShadow: [
                          BoxShadow(
                            color: Colors.black.withOpacity(0.15),
                            blurRadius: 30,
                            offset: const Offset(0, 10),
                          ),
                        ],
                      ),
                      padding: const EdgeInsets.all(24),
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.stretch,
                        children: [
                          Text(
                            _t('cardTitle'),
                            style: const TextStyle(
                              fontSize: 18,
                              fontWeight: FontWeight.w800,
                              color: Color(0xFF0F291B),
                              letterSpacing: -0.5,
                            ),
                          ),
                          const SizedBox(height: 20),

                          // Phone field
                          TextField(
                            controller: _phoneController,
                            keyboardType: TextInputType.phone,
                            decoration: InputDecoration(
                              labelText: _t('phoneLabel'),
                              prefixIcon: const Icon(
                                Icons.phone_iphone_rounded,
                                size: 20,
                              ),
                            ),
                          ),
                          const SizedBox(height: 16),

                          // Password field
                          TextField(
                            controller: _passwordController,
                            obscureText: _obscurePassword,
                            decoration: InputDecoration(
                              labelText: _t('passwordLabel'),
                              prefixIcon: const Icon(
                                Icons.lock_outline_rounded,
                                size: 20,
                              ),
                              suffixIcon: IconButton(
                                icon: Icon(
                                  _obscurePassword
                                      ? Icons.visibility_outlined
                                      : Icons.visibility_off_outlined,
                                  size: 20,
                                ),
                                onPressed: () => setState(
                                        () => _obscurePassword = !_obscurePassword),
                              ),
                            ),
                          ),

                          // Forgot password
                          Align(
                            alignment: Alignment.centerRight,
                            child: TextButton(
                              onPressed: () {},
                              child: Text(
                                _t('forgotPassword'),
                                style: const TextStyle(
                                  fontSize: 12,
                                  color: Color(0xFF1B4332),
                                ),
                              ),
                            ),
                          ),

                          const SizedBox(height: 8),

                          // Login button
                          ElevatedButton(
                            onPressed: _loading ? null : _login,
                            style: ElevatedButton.styleFrom(
                              backgroundColor: const Color(0xFF1B4332),
                              foregroundColor: Colors.white,
                              padding:
                              const EdgeInsets.symmetric(vertical: 16),
                              shape: RoundedRectangleBorder(
                                borderRadius: BorderRadius.circular(14),
                              ),
                              elevation: 0,
                            ),
                            child: _loading
                                ? const SizedBox(
                              width: 20,
                              height: 20,
                              child: CircularProgressIndicator(
                                color: Colors.white,
                                strokeWidth: 2.5,
                              ),
                            )
                                : Text(
                              _t('loginButton'),
                              style: const TextStyle(
                                fontSize: 16,
                                fontWeight: FontWeight.w700,
                              ),
                            ),
                          ),
                        ],
                      ),
                    ),

                    const SizedBox(height: 24),

                    // ── Register link ─────────────────────────────────────
                    TextButton(
                      onPressed: () =>
                          Navigator.pushNamed(context, '/register'),
                      child: Text(
                        _t('noAccount'),
                        style: const TextStyle(
                          color: Colors.white70,
                          fontWeight: FontWeight.bold,
                          fontSize: 13,
                        ),
                      ),
                    ),

                    const SizedBox(height: 20),
                  ],
                ),
              ),
            );
          },
        ),
      ),
    );
  }

  @override
  void dispose() {
    _phoneController.dispose();
    _passwordController.dispose();
    super.dispose();
  }
}
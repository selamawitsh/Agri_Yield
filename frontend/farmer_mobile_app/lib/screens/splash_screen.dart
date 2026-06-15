import 'package:flutter/material.dart';
import '../services/auth_service.dart';
import '../services/language_service.dart';

/// SRS §6.1.2 — Splash screen.
///
/// Routing logic (in order):
///   1. First launch (no language saved) → /language
///   2. Language saved but not logged in → /login
///   3. Language saved and logged in     → /home
class SplashScreen extends StatefulWidget {
  const SplashScreen({super.key});

  @override
  State<SplashScreen> createState() => _SplashScreenState();
}

class _SplashScreenState extends State<SplashScreen> {
  @override
  void initState() {
    super.initState();
    _route();
  }

  Future<void> _route() async {
    // Minimum display time so the splash doesn't flash
    await Future.delayed(const Duration(milliseconds: 1800));
    if (!mounted) return;

    // SRS §6.1.2: check language first — first-time users go to /language
    final hasLanguage = await LanguageService.hasSelectedLanguage();
    if (!mounted) return;

    if (!hasLanguage) {
      Navigator.pushReplacementNamed(context, '/language');
      return;
    }

    // Returning user — check JWT token
    final isLoggedIn = await AuthService().isLoggedIn();
    if (!mounted) return;

    Navigator.pushReplacementNamed(
      context,
      isLoggedIn ? '/home' : '/login',
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFF1B4332),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Container(
              padding: const EdgeInsets.all(24),
              decoration: BoxDecoration(
                color: Colors.white.withOpacity(0.06),
                shape: BoxShape.circle,
              ),
              child: const Icon(
                Icons.wb_twilight_rounded,
                size: 84,
                color: Colors.white,
              ),
            ),
            const SizedBox(height: 24),
            const Text(
              'Agri-Yield',
              style: TextStyle(
                fontSize: 38,
                fontWeight: FontWeight.w900,
                color: Colors.white,
                letterSpacing: -1.5,
              ),
            ),
            const SizedBox(height: 4),
            Text(
              'Empowering Ethiopian Farmers'.toUpperCase(),
              style: const TextStyle(
                color: Colors.white54,
                fontSize: 10,
                fontWeight: FontWeight.bold,
                letterSpacing: 1.5,
              ),
            ),
            const SizedBox(height: 60),
            const SizedBox(
              width: 24,
              height: 24,
              child: CircularProgressIndicator(
                color: Colors.white,
                strokeWidth: 2.5,
              ),
            ),
          ],
        ),
      ),
    );
  }
}

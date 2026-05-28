import 'package:flutter/material.dart';
import '../services/auth_service.dart';

class AccountSuspendedScreen extends StatelessWidget {
  final String? reason;
  const AccountSuspendedScreen({super.key, this.reason});

  static const _primary = Color(0xFF1B4332);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFFEF2F2),
      body: SafeArea(
        child: Padding(
          padding: const EdgeInsets.all(32),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Container(
                width: 80, height: 80,
                decoration: BoxDecoration(
                  color: const Color(0xFFFECACA),
                  shape: BoxShape.circle,
                ),
                child: const Icon(Icons.block_rounded,
                    size: 40, color: Color(0xFFDC2626)),
              ),
              const SizedBox(height: 24),
              const Text(
                'Account Suspended',
                style: TextStyle(
                  fontSize: 24,
                  fontWeight: FontWeight.w900,
                  color: Color(0xFF7F1D1D),
                  letterSpacing: -0.5,
                ),
                textAlign: TextAlign.center,
              ),
              const SizedBox(height: 12),
              Text(
                reason ??
                  'Your account has been suspended due to suspicious activity detected by our fraud prevention system.',
                style: const TextStyle(
                  fontSize: 14,
                  color: Color(0xFF991B1B),
                  height: 1.6,
                ),
                textAlign: TextAlign.center,
              ),
              const SizedBox(height: 32),
              Container(
                padding: const EdgeInsets.all(16),
                decoration: BoxDecoration(
                  color: Colors.white,
                  borderRadius: BorderRadius.circular(16),
                  border: Border.all(color: const Color(0xFFFECACA)),
                ),
                child: const Column(
                  children: [
                    Row(
                      children: [
                        Icon(Icons.info_outline_rounded,
                            size: 16, color: Color(0xFF991B1B)),
                        SizedBox(width: 8),
                        Expanded(
                          child: Text(
                            'What to do next',
                            style: TextStyle(
                              fontWeight: FontWeight.w800,
                              fontSize: 13,
                              color: Color(0xFF7F1D1D),
                            ),
                          ),
                        ),
                      ],
                    ),
                    SizedBox(height: 8),
                    Text(
                      'Contact Agri-Yield support at support@agriyield.et '
                      'or call 0800-AGRIYIELD. '
                      'Have your Fayda ID ready for identity verification.',
                      style: TextStyle(
                        fontSize: 12,
                        color: Color(0xFF991B1B),
                        height: 1.5,
                      ),
                    ),
                  ],
                ),
              ),
              const SizedBox(height: 32),
              SizedBox(
                width: double.infinity,
                child: OutlinedButton(
                  onPressed: () async {
                    await AuthService().logout();
                    if (context.mounted) {
                      Navigator.pushNamedAndRemoveUntil(
                          context, '/login', (route) => false);
                    }
                  },
                  style: OutlinedButton.styleFrom(
                    side: const BorderSide(color: Color(0xFF991B1B)),
                    padding: const EdgeInsets.symmetric(vertical: 14),
                    shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(16)),
                  ),
                  child: const Text(
                    'Sign Out',
                    style: TextStyle(
                      color: Color(0xFF991B1B),
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}

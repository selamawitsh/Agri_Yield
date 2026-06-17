import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../services/auth_service.dart';
import '../services/language_service.dart';

class AccountSuspendedScreen extends StatelessWidget {
  final String? reason;
  const AccountSuspendedScreen({super.key, this.reason});

  static const Map<String, Map<String, String>> _strings = {
    'am': {
      'title':      'መለያ ታግዷል',
      'defaultMsg': 'የእርስዎ መለያ በማጭበርበር መከላከያ ስርዓታችን አጠራጣሪ እንቅስቃሴ ስለተገኘ ታግዷል።',
      'whatNext':   'ቀጥሎ ምን ይደረጋል',
      'contactMsg': 'support@agriyield.et ላይ ያግኙን ወይም 0800-AGRIYIELD ይደውሉ። የፋይዳ መታወቂያዎን ይዘው ይምጡ።',
      'signOut':    'ውጣ',
    },
    'om': {
      'title':      'Herrega Dhorkame',
      'defaultMsg': 'Herreegni keessan sababii hojii shakkisiisaa dhaban dhorkame.',
      'whatNext':   'Itti aansee maal gochuu',
      'contactMsg': 'support@agriyield.et quunnamaa yookiin 0800-AGRIYIELD bilbilaa. Eenyummaa Faydaa qabadhaa.',
      'signOut':    'Ba\'i',
    },
    'en': {
      'title':      'Account Suspended',
      'defaultMsg': 'Your account has been suspended due to suspicious activity detected by our fraud prevention system.',
      'whatNext':   'What to do next',
      'contactMsg': 'Contact Agri-Yield support at support@agriyield.et or call 0800-AGRIYIELD. Have your Fayda ID ready.',
      'signOut':    'Sign Out',
    },
  };

  String _t(BuildContext context, String key) {
    final code = context.read<LanguageService>().languageCode;
    return _strings[code]?[key] ?? _strings['en']![key]!;
  }

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
                decoration: const BoxDecoration(
                    color: Color(0xFFFECACA), shape: BoxShape.circle),
                child: const Icon(Icons.block_rounded,
                    size: 40, color: Color(0xFFDC2626)),
              ),
              const SizedBox(height: 24),
              Text(_t(context, 'title'),
                  style: const TextStyle(
                      fontSize: 24,
                      fontWeight: FontWeight.w900,
                      color: Color(0xFF7F1D1D),
                      letterSpacing: -0.5),
                  textAlign: TextAlign.center),
              const SizedBox(height: 12),
              Text(reason ?? _t(context, 'defaultMsg'),
                  style: const TextStyle(
                      fontSize: 14, color: Color(0xFF991B1B), height: 1.6),
                  textAlign: TextAlign.center),
              const SizedBox(height: 32),
              Container(
                padding: const EdgeInsets.all(16),
                decoration: BoxDecoration(
                  color: Colors.white,
                  borderRadius: BorderRadius.circular(16),
                  border: Border.all(color: const Color(0xFFFECACA)),
                ),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Row(children: [
                      const Icon(Icons.info_outline_rounded,
                          size: 16, color: Color(0xFF991B1B)),
                      const SizedBox(width: 8),
                      Text(_t(context, 'whatNext'),
                          style: const TextStyle(
                              fontWeight: FontWeight.w800,
                              fontSize: 13,
                              color: Color(0xFF7F1D1D))),
                    ]),
                    const SizedBox(height: 8),
                    Text(_t(context, 'contactMsg'),
                        style: const TextStyle(
                            fontSize: 12,
                            color: Color(0xFF991B1B),
                            height: 1.5)),
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
                  child: Text(_t(context, 'signOut'),
                      style: const TextStyle(
                          color: Color(0xFF991B1B),
                          fontWeight: FontWeight.bold)),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
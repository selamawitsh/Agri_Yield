import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../services/language_service.dart';

/// SRS §6.1.2 — Language Selection screen (/language)
///
/// Shown only on first launch (before the user has chosen a language).
/// Presents 3 buttons: Amharic, Oromiffa, English.
/// Stores the choice locally via LanguageService → shared_preferences.
/// Routes to /login after selection.
class LanguageScreen extends StatefulWidget {
  const LanguageScreen({super.key});

  @override
  State<LanguageScreen> createState() => _LanguageScreenState();
}

class _LanguageScreenState extends State<LanguageScreen>
    with SingleTickerProviderStateMixin {
  String? _selectedCode;
  bool _saving = false;
  late AnimationController _fadeController;
  late Animation<double> _fadeAnimation;

  @override
  void initState() {
    super.initState();
    _fadeController = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 600),
    );
    _fadeAnimation = CurvedAnimation(
      parent: _fadeController,
      curve: Curves.easeIn,
    );
    _fadeController.forward();
  }

  @override
  void dispose() {
    _fadeController.dispose();
    super.dispose();
  }

  // The three languages required by SRS §6.1.2
  static const List<_LanguageOption> _languages = [
    _LanguageOption(
      code: 'am',
      nativeName: 'አማርኛ',
      englishName: 'Amharic',
      flag: '🇪🇹',
      greeting: 'እንኳን ደህና መጡ',
    ),
    _LanguageOption(
      code: 'om',
      nativeName: 'Afaan Oromoo',
      englishName: 'Oromiffa',
      flag: '🌿',
      greeting: 'Baga nagaan dhuftan',
    ),
    _LanguageOption(
      code: 'en',
      nativeName: 'English',
      englishName: 'English',
      flag: '🔤',
      greeting: 'Welcome',
    ),
  ];

  Future<void> _onContinue() async {
    if (_selectedCode == null || _saving) return;
    setState(() => _saving = true);

    final languageService =
        Provider.of<LanguageService>(context, listen: false);
    await languageService.setLanguage(_selectedCode!);

    if (mounted) {
      Navigator.pushReplacementNamed(context, '/login');
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFF1B4332),
      body: FadeTransition(
        opacity: _fadeAnimation,
        child: SafeArea(
          child: Padding(
            padding: const EdgeInsets.symmetric(horizontal: 28.0),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                const SizedBox(height: 56),

                // ── Logo + App name ──────────────────────────────────────
                Row(
                  children: [
                    Container(
                      padding: const EdgeInsets.all(10),
                      decoration: BoxDecoration(
                        color: Colors.white.withOpacity(0.1),
                        borderRadius: BorderRadius.circular(12),
                      ),
                      child: const Icon(
                        Icons.wb_twilight_rounded,
                        color: Colors.white,
                        size: 28,
                      ),
                    ),
                    const SizedBox(width: 12),
                    const Text(
                      'Agri-Yield',
                      style: TextStyle(
                        color: Colors.white,
                        fontSize: 24,
                        fontWeight: FontWeight.w800,
                        letterSpacing: -0.5,
                      ),
                    ),
                  ],
                ),

                const SizedBox(height: 52),

                // ── Heading ──────────────────────────────────────────────
                const Text(
                  'Choose your\nlanguage',
                  style: TextStyle(
                    color: Colors.white,
                    fontSize: 34,
                    fontWeight: FontWeight.w800,
                    height: 1.15,
                    letterSpacing: -1.0,
                  ),
                ),
                const SizedBox(height: 8),
                Text(
                  'ቋንቋዎን ይምረጡ  •  Afaan filadhu',
                  style: TextStyle(
                    color: Colors.white.withOpacity(0.55),
                    fontSize: 13,
                    letterSpacing: 0.2,
                  ),
                ),

                const SizedBox(height: 40),

                // ── Language option cards ────────────────────────────────
                Expanded(
                  child: Column(
                    children: _languages.map((lang) {
                      final isSelected = _selectedCode == lang.code;
                      return Padding(
                        padding: const EdgeInsets.only(bottom: 14),
                        child: _LanguageCard(
                          option: lang,
                          isSelected: isSelected,
                          onTap: () =>
                              setState(() => _selectedCode = lang.code),
                        ),
                      );
                    }).toList(),
                  ),
                ),

                // ── Continue button ──────────────────────────────────────
                AnimatedOpacity(
                  opacity: _selectedCode != null ? 1.0 : 0.35,
                  duration: const Duration(milliseconds: 200),
                  child: SizedBox(
                    width: double.infinity,
                    height: 56,
                    child: ElevatedButton(
                      onPressed: _selectedCode != null ? _onContinue : null,
                      style: ElevatedButton.styleFrom(
                        backgroundColor: const Color(0xFF52B788),
                        foregroundColor: Colors.white,
                        disabledBackgroundColor:
                            const Color(0xFF52B788).withOpacity(0.4),
                        shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(14),
                        ),
                        elevation: 0,
                      ),
                      child: _saving
                          ? const SizedBox(
                              width: 22,
                              height: 22,
                              child: CircularProgressIndicator(
                                color: Colors.white,
                                strokeWidth: 2.5,
                              ),
                            )
                          : const Text(
                              'Continue  →',
                              style: TextStyle(
                                fontSize: 17,
                                fontWeight: FontWeight.w700,
                                letterSpacing: 0.2,
                              ),
                            ),
                    ),
                  ),
                ),

                const SizedBox(height: 32),
              ],
            ),
          ),
        ),
      ),
    );
  }
}

// ── Language card widget ─────────────────────────────────────────────────────

class _LanguageCard extends StatelessWidget {
  final _LanguageOption option;
  final bool isSelected;
  final VoidCallback onTap;

  const _LanguageCard({
    required this.option,
    required this.isSelected,
    required this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onTap,
      child: AnimatedContainer(
        duration: const Duration(milliseconds: 180),
        curve: Curves.easeOut,
        padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 18),
        decoration: BoxDecoration(
          color: isSelected
              ? const Color(0xFF52B788)
              : Colors.white.withOpacity(0.07),
          borderRadius: BorderRadius.circular(16),
          border: Border.all(
            color: isSelected
                ? const Color(0xFF52B788)
                : Colors.white.withOpacity(0.15),
            width: 1.5,
          ),
        ),
        child: Row(
          children: [
            // Flag / icon
            Text(option.flag, style: const TextStyle(fontSize: 28)),
            const SizedBox(width: 16),

            // Names
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    option.nativeName,
                    style: TextStyle(
                      color: isSelected ? Colors.white : Colors.white,
                      fontSize: 18,
                      fontWeight: FontWeight.w700,
                    ),
                  ),
                  const SizedBox(height: 2),
                  Text(
                    option.greeting,
                    style: TextStyle(
                      color: isSelected
                          ? Colors.white.withOpacity(0.8)
                          : Colors.white.withOpacity(0.45),
                      fontSize: 13,
                    ),
                  ),
                ],
              ),
            ),

            // Selection indicator
            AnimatedContainer(
              duration: const Duration(milliseconds: 180),
              width: 24,
              height: 24,
              decoration: BoxDecoration(
                shape: BoxShape.circle,
                color: isSelected ? Colors.white : Colors.transparent,
                border: Border.all(
                  color: isSelected
                      ? Colors.white
                      : Colors.white.withOpacity(0.3),
                  width: 2,
                ),
              ),
              child: isSelected
                  ? const Icon(
                      Icons.check,
                      size: 14,
                      color: Color(0xFF1B4332),
                    )
                  : null,
            ),
          ],
        ),
      ),
    );
  }
}

// ── Data class ───────────────────────────────────────────────────────────────

class _LanguageOption {
  final String code;
  final String nativeName;
  final String englishName;
  final String flag;
  final String greeting;

  const _LanguageOption({
    required this.code,
    required this.nativeName,
    required this.englishName,
    required this.flag,
    required this.greeting,
  });
}

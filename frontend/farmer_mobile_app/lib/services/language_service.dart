import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';

/// Manages the user's language preference.
///
/// Language is stored locally in shared_preferences under the key
/// 'selected_language'. On first launch the key does not exist —
/// that is the signal for SplashScreen to route to LanguageScreen.
class LanguageService extends ChangeNotifier {
  static const String _key = 'selected_language';

  Locale _locale = const Locale('am'); // default: Amharic

  Locale get locale => _locale;

  /// Returns true if the user has already chosen a language (not first launch).
  static Future<bool> hasSelectedLanguage() async {
    final prefs = await SharedPreferences.getInstance();
    return prefs.containsKey(_key);
  }

  /// Loads the saved language from disk. Call once at app startup.
  Future<void> loadSavedLanguage() async {
    final prefs = await SharedPreferences.getInstance();
    final saved = prefs.getString(_key);
    if (saved != null) {
      _locale = Locale(saved);
      notifyListeners();
    }
  }

  /// Saves the chosen language locally and updates the app locale immediately.
  Future<void> setLanguage(String languageCode) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString(_key, languageCode);
    _locale = Locale(languageCode);
    notifyListeners();
  }

  /// Convenience getter for the raw language code string e.g. 'am', 'om', 'en'.
  String get languageCode => _locale.languageCode;
}

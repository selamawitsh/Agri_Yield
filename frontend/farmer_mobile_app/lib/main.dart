import 'package:flutter/material.dart';
import 'package:flutter_localizations/flutter_localizations.dart';
import 'package:provider/provider.dart';
import 'services/api_service.dart';
import 'services/auth_service.dart';
import 'services/language_service.dart';
import 'screens/splash_screen.dart';
import 'screens/language_screen.dart';
import 'screens/login_screen.dart';
import 'screens/register_screen.dart';
import 'screens/home_screen.dart';
import 'screens/profile_screen.dart';
import 'screens/farm/my_farms_screen.dart';
import 'screens/vouchers/voucher_wallet_screen.dart';
import 'screens/weather/weather_screen.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();

  final apiService = ApiService();
  await apiService.init();

  // Load saved language before the first frame so there is no locale flicker
  final languageService = LanguageService();
  await languageService.loadSavedLanguage();

  runApp(
    ChangeNotifierProvider<LanguageService>.value(
      value: languageService,
      child: MyApp(authService: AuthService()),
    ),
  );
}

class MyApp extends StatelessWidget {
  final AuthService authService;
  const MyApp({super.key, required this.authService});

  @override
  Widget build(BuildContext context) {
    // Rebuild MaterialApp whenever the user switches language
    final languageService = context.watch<LanguageService>();

    return MaterialApp(
      title: 'Agri-Yield Farmer',
      debugShowCheckedModeBanner: false,

      // ── Theme ──────────────────────────────────────────────────────────
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: const Color(0xFF1B4332)),
        useMaterial3: true,
        appBarTheme: const AppBarTheme(
          backgroundColor: Color(0xFF1B4332),
          foregroundColor: Colors.white,
          elevation: 0,
        ),
      ),

      // ── Localisation ───────────────────────────────────────────────────
      // SRS §6.1.1: must support am, om, en
      locale: languageService.locale,
      supportedLocales: const [
        Locale('am'), // Amharic
        Locale('om'), // Oromiffa
        Locale('en'), // English
      ],
      localizationsDelegates: const [
        GlobalMaterialLocalizations.delegate,
        GlobalWidgetsLocalizations.delegate,
        GlobalCupertinoLocalizations.delegate,
      ],

      // ── Routing ────────────────────────────────────────────────────────
      home: const SplashScreen(),
      routes: {
        '/language': (_) => const LanguageScreen(), // SRS §6.1.2 first launch
        '/login':    (_) => const LoginScreen(),
        '/register': (_) => const RegisterScreen(),
        '/home':     (_) => const HomeScreen(),
        '/profile':  (_) => const ProfileScreen(),
        '/farms':    (_) => const MyFarmsScreen(),
        '/vouchers': (_) => const VoucherWalletScreen(),
        '/weather':  (_) => const WeatherScreen(),
      },
    );
  }
}
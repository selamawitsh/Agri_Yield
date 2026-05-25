import 'package:flutter/material.dart';
import 'package:flutter_localizations/flutter_localizations.dart';
import 'services/api_service.dart';
import 'services/auth_service.dart';
import 'screens/splash_screen.dart';
import 'screens/login_screen.dart';
import 'screens/register_screen.dart';
import 'screens/home_screen.dart';
import 'screens/scanner_screen.dart';
import 'screens/products_screen.dart';
import 'screens/analytics_screen.dart';
import 'screens/transaction_history_screen.dart';
import 'screens/price_transparency_screen.dart';
import 'screens/wallet_screen.dart';
import 'screens/profile_screen.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  final apiService = ApiService();
  await apiService.init();
  runApp(MyApp(authService: AuthService()));
}

class MyApp extends StatelessWidget {
  final AuthService authService;
  const MyApp({super.key, required this.authService});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Agri-Yield Merchant',
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.orange),
        useMaterial3: true,
        appBarTheme: const AppBarTheme(
          backgroundColor: Colors.orange,
          foregroundColor: Colors.white,
          elevation: 0,
        ),
      ),
      supportedLocales: const [Locale('am'), Locale('om'), Locale('en')],
      localizationsDelegates: const [
        GlobalMaterialLocalizations.delegate,
        GlobalWidgetsLocalizations.delegate,
        GlobalCupertinoLocalizations.delegate,
      ],
      home: const SplashScreen(),
      routes: {
        '/login':               (_) => const LoginScreen(),
        '/register':            (_) => const RegisterScreen(),
        '/home':                (_) => const HomeScreen(),
        '/scanner':             (_) => const ScannerScreen(),
        '/products':            (_) => const ProductsScreen(),
        '/analytics':           (_) => const AnalyticsScreen(),
        '/history':             (_) => const TransactionHistoryScreen(),
        '/prices':              (_) => const PriceTransparencyScreen(),
        '/wallet':              (_) => const WalletScreen(),
        '/profile':             (_) => const ProfileScreen(),
      },
    );
  }
}

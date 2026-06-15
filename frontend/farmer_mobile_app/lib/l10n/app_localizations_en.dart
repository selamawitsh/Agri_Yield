// ignore: unused_import
import 'package:intl/intl.dart' as intl;
import 'app_localizations.dart';

// ignore_for_file: type=lint

/// The translations for English (`en`).
class AppLocalizationsEn extends AppLocalizations {
  AppLocalizationsEn([String locale = 'en']) : super(locale);

  @override
  String get appName => 'Agri-Yield';

  @override
  String get tagline => 'Empowering Ethiopian Farmers';

  @override
  String get selectLanguage => 'Choose your language';

  @override
  String get selectLanguageSubtitle => 'You can change this later in Settings';

  @override
  String get continueButton => 'Continue';

  @override
  String get login => 'Login';

  @override
  String get register => 'Register';

  @override
  String get phoneNumber => 'Phone Number';

  @override
  String get password => 'Password';

  @override
  String get fullName => 'Full Name';

  @override
  String get faydaId => 'Fayda National ID';

  @override
  String get forgotPassword => 'Forgot password?';

  @override
  String get loginButton => 'Sign In';

  @override
  String get registerButton => 'Create Account';

  @override
  String get otpTitle => 'Enter OTP';

  @override
  String otpSubtitle(String phone) {
    return 'A 6-digit code was sent to $phone';
  }

  @override
  String get otpVerify => 'Verify';

  @override
  String get alreadyHaveAccount => 'Already have an account?';

  @override
  String get dontHaveAccount => 'Don\'t have an account?';

  @override
  String get home => 'Home';

  @override
  String get myFarms => 'My Farms';

  @override
  String get vouchers => 'Vouchers';

  @override
  String get weather => 'Weather';

  @override
  String get profile => 'Profile';

  @override
  String get advisor => 'AI Advisor';

  @override
  String get bids => 'Bids';

  @override
  String get earnings => 'Earnings';

  @override
  String get farmStatus => 'Farm Status';

  @override
  String get ndviScore => 'NDVI Score';

  @override
  String get agriScore => 'Agri-Score';

  @override
  String get season => 'Season';

  @override
  String get funded => 'Funded';

  @override
  String get investors => 'Investors';

  @override
  String get activeVouchers => 'Active Vouchers';

  @override
  String get registerFarm => 'Register Farm';

  @override
  String get farmName => 'Farm Name';

  @override
  String get cropType => 'Crop Type';

  @override
  String get kebeleCode => 'Kebele Code';

  @override
  String get region => 'Region';

  @override
  String get expectedHarvest => 'Expected Harvest Date';

  @override
  String get drawPolygon => 'Draw Farm Boundary';

  @override
  String get uploadPhotos => 'Upload Farm Photos';

  @override
  String get submitFarm => 'Submit Farm';

  @override
  String get inputNeeds => 'Input Needs';

  @override
  String get addItem => 'Add Item';

  @override
  String get productName => 'Product Name';

  @override
  String get category => 'Category';

  @override
  String get quantity => 'Quantity';

  @override
  String get unit => 'Unit';

  @override
  String get estimatedPrice => 'Estimated Price (ETB)';

  @override
  String get submitNeeds => 'Submit Needs';

  @override
  String get voucherWallet => 'Voucher Wallet';

  @override
  String get active => 'Active';

  @override
  String get pending => 'Pending';

  @override
  String get redeemed => 'Redeemed';

  @override
  String get expired => 'Expired';

  @override
  String get showQrCode => 'Show QR Code';

  @override
  String get voucherCode => 'Code';

  @override
  String validUntil(String date) {
    return 'Valid until $date';
  }

  @override
  String get weatherAlerts => 'Weather Alerts';

  @override
  String get forecast => '7-Day Forecast';

  @override
  String get temperature => 'Temperature';

  @override
  String get rainfall => 'Rainfall';

  @override
  String get humidity => 'Humidity';

  @override
  String get settings => 'Settings';

  @override
  String get language => 'Language';

  @override
  String get notifications => 'Notifications';

  @override
  String get linkedAccounts => 'Linked Accounts';

  @override
  String get changePassword => 'Change Password';

  @override
  String get logout => 'Logout';

  @override
  String get offlineBanner => 'You are offline — showing cached data';

  @override
  String get syncingData => 'Syncing data...';

  @override
  String get noData => 'No data available';

  @override
  String get retry => 'Retry';

  @override
  String get save => 'Save';

  @override
  String get cancel => 'Cancel';

  @override
  String get confirm => 'Confirm';

  @override
  String get back => 'Back';

  @override
  String get next => 'Next';

  @override
  String get submit => 'Submit';

  @override
  String get loading => 'Loading...';

  @override
  String get errorOccurred => 'Something went wrong. Please try again.';

  @override
  String get etb => 'ETB';
}

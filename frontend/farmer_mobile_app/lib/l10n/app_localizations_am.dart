// ignore: unused_import
import 'package:intl/intl.dart' as intl;
import 'app_localizations.dart';

// ignore_for_file: type=lint

/// The translations for Amharic (`am`).
class AppLocalizationsAm extends AppLocalizations {
  AppLocalizationsAm([String locale = 'am']) : super(locale);

  @override
  String get appName => 'አግሪ-ይልድ';

  @override
  String get tagline => 'የኢትዮጵያ ገበሬዎችን ማብቃት';

  @override
  String get selectLanguage => 'ቋንቋዎን ይምረጡ';

  @override
  String get selectLanguageSubtitle => 'ይህን በኋላ በቅንብሮች ውስጥ መቀየር ይችላሉ';

  @override
  String get continueButton => 'ቀጥል';

  @override
  String get login => 'ግባ';

  @override
  String get register => 'ተመዝገብ';

  @override
  String get phoneNumber => 'ስልክ ቁጥር';

  @override
  String get password => 'የሚስጥር ቃል';

  @override
  String get fullName => 'ሙሉ ስም';

  @override
  String get faydaId => 'የፋይዳ ብሔራዊ መታወቂያ';

  @override
  String get forgotPassword => 'የሚስጥር ቃል ረሳህ?';

  @override
  String get loginButton => 'ግባ';

  @override
  String get registerButton => 'መለያ ፍጠር';

  @override
  String get otpTitle => 'OTP አስገባ';

  @override
  String otpSubtitle(String phone) {
    return '6 አሃዝ ኮድ ወደ $phone ተልኳል';
  }

  @override
  String get otpVerify => 'አረጋግጥ';

  @override
  String get alreadyHaveAccount => 'መለያ አለህ?';

  @override
  String get dontHaveAccount => 'መለያ የለህም?';

  @override
  String get home => 'ዋና ገጽ';

  @override
  String get myFarms => 'የእኔ እርሻዎች';

  @override
  String get vouchers => 'ቫውቸሮች';

  @override
  String get weather => 'የአየር ሁኔታ';

  @override
  String get profile => 'መገለጫ';

  @override
  String get advisor => 'AI አማካሪ';

  @override
  String get bids => 'ጨረታዎች';

  @override
  String get earnings => 'ገቢ';

  @override
  String get farmStatus => 'የእርሻ ሁኔታ';

  @override
  String get ndviScore => 'NDVI ነጥብ';

  @override
  String get agriScore => 'አግሪ-ነጥብ';

  @override
  String get season => 'ወቅት';

  @override
  String get funded => 'ተሸፍኗል';

  @override
  String get investors => 'ባለሀብቶች';

  @override
  String get activeVouchers => 'ንቁ ቫውቸሮች';

  @override
  String get registerFarm => 'እርሻ ተመዝግብ';

  @override
  String get farmName => 'የእርሻ ስም';

  @override
  String get cropType => 'የሰብል አይነት';

  @override
  String get kebeleCode => 'የቀበሌ ኮድ';

  @override
  String get region => 'ክልል';

  @override
  String get expectedHarvest => 'የተጠበቀ የምርት ቀን';

  @override
  String get drawPolygon => 'የእርሻ ድንበር ይሳሉ';

  @override
  String get uploadPhotos => 'የእርሻ ፎቶዎች ይስቀሉ';

  @override
  String get submitFarm => 'እርሻ አስገባ';

  @override
  String get inputNeeds => 'የግብዓት ፍላጎቶች';

  @override
  String get addItem => 'ንጥል ጨምር';

  @override
  String get productName => 'የምርት ስም';

  @override
  String get category => 'ምድብ';

  @override
  String get quantity => 'መጠን';

  @override
  String get unit => 'ክፍል';

  @override
  String get estimatedPrice => 'የተገመተ ዋጋ (ብር)';

  @override
  String get submitNeeds => 'ፍላጎቶች አስገባ';

  @override
  String get voucherWallet => 'የቫውቸር ቦርሳ';

  @override
  String get active => 'ንቁ';

  @override
  String get pending => 'በመጠባበቅ ላይ';

  @override
  String get redeemed => 'ተቀብሏል';

  @override
  String get expired => 'ጊዜው አልፏል';

  @override
  String get showQrCode => 'QR ኮድ አሳይ';

  @override
  String get voucherCode => 'ኮድ';

  @override
  String validUntil(String date) {
    return 'እስከ $date ድረስ ይሰራል';
  }

  @override
  String get weatherAlerts => 'የአየር ማስጠንቀቂያዎች';

  @override
  String get forecast => 'የ7 ቀን ትንበያ';

  @override
  String get temperature => 'የሙቀት መጠን';

  @override
  String get rainfall => 'የዝናብ መጠን';

  @override
  String get humidity => 'እርጥበት';

  @override
  String get settings => 'ቅንብሮች';

  @override
  String get language => 'ቋንቋ';

  @override
  String get notifications => 'ማሳወቂያዎች';

  @override
  String get linkedAccounts => 'የተያያዙ መለያዎች';

  @override
  String get changePassword => 'የሚስጥር ቃል ቀይር';

  @override
  String get logout => 'ውጣ';

  @override
  String get offlineBanner => 'ከኢንተርኔት ተቋርጠዋል — የተቀመጡ መረጃዎችን እያሳዩ ነው';

  @override
  String get syncingData => 'መረጃ በማመሳሰል ላይ...';

  @override
  String get noData => 'ምንም መረጃ የለም';

  @override
  String get retry => 'እንደገና ሞክር';

  @override
  String get save => 'አስቀምጥ';

  @override
  String get cancel => 'ሰርዝ';

  @override
  String get confirm => 'አረጋግጥ';

  @override
  String get back => 'ተመለስ';

  @override
  String get next => 'ቀጥል';

  @override
  String get submit => 'አስገባ';

  @override
  String get loading => 'በመጫን ላይ...';

  @override
  String get errorOccurred => 'ስህተት ተፈጥሯል። እንደገና ይሞክሩ።';

  @override
  String get etb => 'ብር';
}

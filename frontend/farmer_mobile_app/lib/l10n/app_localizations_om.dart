// ignore: unused_import
import 'package:intl/intl.dart' as intl;
import 'app_localizations.dart';

// ignore_for_file: type=lint

/// The translations for Oromo (`om`).
class AppLocalizationsOm extends AppLocalizations {
  AppLocalizationsOm([String locale = 'om']) : super(locale);

  @override
  String get appName => 'Agri-Yield';

  @override
  String get tagline => 'Qonnaan Bultoota Itoophiyaa Gargaaruu';

  @override
  String get selectLanguage => 'Afaan filadhu';

  @override
  String get selectLanguageSubtitle =>
      'Kana booda Qindaa\'ina keessatti jijjiiruu dandeessa';

  @override
  String get continueButton => 'Itti fufi';

  @override
  String get login => 'Seeni';

  @override
  String get register => 'Galmeessi';

  @override
  String get phoneNumber => 'Lakkoofsa Bilbilaa';

  @override
  String get password => 'Jecha Darbii';

  @override
  String get fullName => 'Maqaa Guutuu';

  @override
  String get faydaId => 'Eenyummaa Biyyaalessaa Faydaa';

  @override
  String get forgotPassword => 'Jecha darbii dagatte?';

  @override
  String get loginButton => 'Seeni';

  @override
  String get registerButton => 'Herrega Uumi';

  @override
  String get otpTitle => 'OTP Galchi';

  @override
  String otpSubtitle(String phone) {
    return 'Koodii lakkoofsa 6 $phone irraa ergame';
  }

  @override
  String get otpVerify => 'Mirkaneessi';

  @override
  String get alreadyHaveAccount => 'Herrega qabdaa?';

  @override
  String get dontHaveAccount => 'Herrega hin qabduu?';

  @override
  String get home => 'Fuula Mana';

  @override
  String get myFarms => 'Lafa Qonnaa Koo';

  @override
  String get vouchers => 'Waraqaa Ragaa';

  @override
  String get weather => 'Haala Qilleensaa';

  @override
  String get profile => 'Profaayilii';

  @override
  String get advisor => 'Gorsaa AI';

  @override
  String get bids => 'Dhiyeessii';

  @override
  String get earnings => 'Galii';

  @override
  String get farmStatus => 'Haala Lafa Qonnaa';

  @override
  String get ndviScore => 'Qabxii NDVI';

  @override
  String get agriScore => 'Qabxii Agri';

  @override
  String get season => 'Yeroo';

  @override
  String get funded => 'Maallaqni Argame';

  @override
  String get investors => 'Mamuultota';

  @override
  String get activeVouchers => 'Waraqaa Ragaa Hojjechaa Jiran';

  @override
  String get registerFarm => 'Lafa Qonnaa Galmeessi';

  @override
  String get farmName => 'Maqaa Lafa Qonnaa';

  @override
  String get cropType => 'Gosa Midhaan';

  @override
  String get kebeleCode => 'Koodii Ganda';

  @override
  String get region => 'Naannoo';

  @override
  String get expectedHarvest => 'Guyyaa Midhaan Sassaabuu Eegamu';

  @override
  String get drawPolygon => 'Daangaa Lafa Qonnaa Kaabi';

  @override
  String get uploadPhotos => 'Suuraa Lafa Qonnaa Fe\'i';

  @override
  String get submitFarm => 'Lafa Qonnaa Galchi';

  @override
  String get inputNeeds => 'Fedhii Meeshaalee';

  @override
  String get addItem => 'Meeshaa Dabalii';

  @override
  String get productName => 'Maqaa Meeshaa';

  @override
  String get category => 'Gosa';

  @override
  String get quantity => 'Baay\'ina';

  @override
  String get unit => 'Safartuuu';

  @override
  String get estimatedPrice => 'Gatii Tilmaamame (ETB)';

  @override
  String get submitNeeds => 'Fedhii Galchi';

  @override
  String get voucherWallet => 'Hordofaa Waraqaa Ragaa';

  @override
  String get active => 'Hojjechaa Jira';

  @override
  String get pending => 'Eegaa Jira';

  @override
  String get redeemed => 'Fudhataame';

  @override
  String get expired => 'Yeroon Darbeera';

  @override
  String get showQrCode => 'Koodii QR Agarsiisi';

  @override
  String get voucherCode => 'Koodii';

  @override
  String validUntil(String date) {
    return 'Hanga $date hojjeta';
  }

  @override
  String get weatherAlerts => 'Akeekkachiisa Haala Qilleensaa';

  @override
  String get forecast => 'Tilmaama Guyyaa 7';

  @override
  String get temperature => 'Ho\'a';

  @override
  String get rainfall => 'Rooba';

  @override
  String get humidity => 'Jiidha';

  @override
  String get settings => 'Qindaa\'ina';

  @override
  String get language => 'Afaan';

  @override
  String get notifications => 'Beeksisa';

  @override
  String get linkedAccounts => 'Herregaalee Walqabatan';

  @override
  String get changePassword => 'Jecha Darbii Jijjiiri';

  @override
  String get logout => 'Ba\'i';

  @override
  String get offlineBanner =>
      'Interneetii irraa citteetta — Deetaa kuufame agarsiisaa jira';

  @override
  String get syncingData => 'Deetaa walqixxeessaa jira...';

  @override
  String get noData => 'Deetaa hin jiru';

  @override
  String get retry => 'Irra deebi\'i yaalii';

  @override
  String get save => 'Kuusi';

  @override
  String get cancel => 'Haquu';

  @override
  String get confirm => 'Mirkaneessi';

  @override
  String get back => 'Deebi\'i';

  @override
  String get next => 'Itti fufi';

  @override
  String get submit => 'Galchi';

  @override
  String get loading => 'Fe\'aa jira...';

  @override
  String get errorOccurred => 'Dogoggora uumame. Irra deebi\'i yaalii.';

  @override
  String get etb => 'ETB';
}

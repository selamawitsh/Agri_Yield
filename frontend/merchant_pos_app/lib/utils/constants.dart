class Constants {
  static const String apiBaseUrl = 'http://localhost:8080/api/v1';
  static const String appName = 'Agri-Yield Merchant';

  static const String merchantRegister    = '/merchants/register';
  static const String merchantMe          = '/merchants/me';
  static const String merchantInventory   = '/merchant/inventory';
  static const String merchantVouchers    = '/merchant/vouchers';
  static const String merchantRedemptions = '/merchant/redemptions';
  static const String merchantAnalytics   = '/merchant/analytics';

  static const String login     = '/auth/login';
  static const String register  = '/auth/register';
  static const String logout    = '/auth/logout';
  static const String otpVerify = '/auth/otp/verify';
}

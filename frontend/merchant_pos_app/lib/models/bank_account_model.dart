class BankAccountModel {
  final String id;
  final String accountType;
  final String accountNumber;
  final String accountHolderName;
  final bool isVerified;
  final bool isDefault;
  
  BankAccountModel({
    required this.id,
    required this.accountType,
    required this.accountNumber,
    required this.accountHolderName,
    required this.isVerified,
    required this.isDefault,
  });
  
  factory BankAccountModel.fromJson(Map<String, dynamic> json) {
    return BankAccountModel(
      id: json['id'],
      accountType: json['accountType'],
      accountNumber: json['accountNumber'],
      accountHolderName: json['accountHolderName'] ?? '',
      isVerified: json['verified'] ?? false,
      isDefault: json['default'] ?? false,
    );
  }
  
  String getMaskedNumber() {
    if (accountNumber.length < 4) return '****';
    return '****' + accountNumber.substring(accountNumber.length - 4);
  }
}

import 'api_service.dart';
import '../models/bank_account_model.dart';

class BankAccountService {
  final ApiService _api = ApiService();
  
  Future<List<BankAccountModel>> getBankAccounts() async {
    try {
      final response = await _api.get('/users/me/bank');
      if (response['success']) {
        final List<dynamic> accounts = response['data'];
        return accounts.map((json) => BankAccountModel.fromJson(json)).toList();
      }
      return [];
    } catch (e) {
      return [];
    }
  }
  
  Future<Map<String, dynamic>> addBankAccount({
    required String accountType,
    required String accountNumber,
    required String accountHolderName,
  }) async {
    try {
      final response = await _api.post('/users/me/bank', {
        'account_type': accountType,
        'account_number': accountNumber,
        'account_holder_name': accountHolderName,
      });
      return {
        'success': response['success'],
        'message': response['message'],
      };
    } catch (e) {
      return {'success': false, 'message': e.toString()};
    }
  }
  
  Future<Map<String, dynamic>> verifyBankAccount({
    required String accountId,
    required String verificationCode,
  }) async {
    try {
      final response = await _api.post('/users/me/bank/verify', {
        'account_id': accountId,
        'verification_code': verificationCode,
      });
      return {
        'success': response['success'],
        'message': response['message'],
      };
    } catch (e) {
      return {'success': false, 'message': e.toString()};
    }
  }
  
  Future<Map<String, dynamic>> setDefaultAccount(String accountId) async {
    try {
      final response = await _api.post('/users/me/bank/default', {
        'account_id': accountId,
      });
      return {
        'success': response['success'],
        'message': response['message'],
      };
    } catch (e) {
      return {'success': false, 'message': e.toString()};
    }
  }
  
  Future<Map<String, dynamic>> deleteBankAccount(String accountId) async {
    try {
      final response = await _api.delete('/users/me/bank/$accountId');
      return {
        'success': response['success'],
        'message': response['message'],
      };
    } catch (e) {
      return {'success': false, 'message': e.toString()};
    }
  }
}

import 'package:flutter/material.dart';
import '../services/auth_service.dart';
import '../services/bank_account_service.dart';
import '../models/bank_account_model.dart';

class ProfileScreen extends StatefulWidget {
  const ProfileScreen({super.key});

  @override
  State<ProfileScreen> createState() => _ProfileScreenState();
}

class _ProfileScreenState extends State<ProfileScreen> {
  final _authService = AuthService();
  final _bankService = BankAccountService();

  String _phone = '';
  String _email = '';
  String _kycStatus = '';
  String _language = 'am';
  List<BankAccountModel> _accounts = [];
  bool _loading = true;
  bool _editing = false;
  final _emailController = TextEditingController();

  bool _showAddForm = false;
  final _accountNumberController = TextEditingController();
  final _accountHolderController = TextEditingController();
  String _selectedAccountType = 'TELEBIRR';

  @override
  void initState() {
    super.initState();
    _loadData();
  }

  Future<void> _loadData() async {
    setState(() => _loading = true);
    final user = await _authService.getCurrentUser();
    if (user != null) {
      _phone = user.phone;
      _email = user.email ?? '';
      _kycStatus = user.kycStatus;
      _language = user.preferredLanguage;
      _emailController.text = _email;
    }
    _accounts = await _bankService.getBankAccounts();
    setState(() => _loading = false);
  }

  Future<void> _updateProfile() async {
    await _authService.updateProfile(
      email: _emailController.text.isEmpty ? null : _emailController.text,
      preferredLanguage: _language,
    );
    setState(() => _editing = false);
    await _loadData();
    ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Identity Profile updated')));
  }

  Future<void> _addAccount() async {
    if (_accountNumberController.text.isEmpty) return;
    setState(() => _loading = true);
    await _bankService.addBankAccount(
      accountType: _selectedAccountType,
      accountNumber: _accountNumberController.text,
      accountHolderName: _accountHolderController.text,
    );
    setState(() {
      _showAddForm = false;
      _accountNumberController.clear();
      _accountHolderController.clear();
    });
    await _loadData();
  }

  Future<void> _verifyAccount(String id) async {
    final code = await showDialog<String>(
      context: context,
      builder: (context) => AlertDialog(
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(24)),
        title: const Text('Verify Node Account', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
        content: TextField(
          onSubmitted: (value) => Navigator.pop(context, value),
          decoration: const InputDecoration(labelText: 'Verification Code', hintText: 'Enter internal verification identifier'),
        ),
        actions: [
          TextButton(onPressed: () => Navigator.pop(context, null), child: const Text('Cancel')),
          TextButton(onPressed: () => Navigator.pop(context, 'ETB1'), child: const Text('Verify Code')),
        ],
      ),
    );
    if (code == null) return;
    await _bankService.verifyBankAccount(accountId: id, verificationCode: code);
    await _loadData();
  }

  Future<void> _setDefault(String id) async {
    await _bankService.setDefaultAccount(id);
    await _loadData();
  }

  Future<void> _deleteAccount(String id) async {
    await _bankService.deleteBankAccount(id);
    await _loadData();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('My Identity Profile'),
        actions: [
          IconButton(
            icon: Icon(_editing ? Icons.close_rounded : Icons.mode_edit_outline_rounded),
            onPressed: () => setState(() => _editing = !_editing),
          ),
          const SizedBox(width: 8),
        ],
      ),
      body: RefreshIndicator(
        onRefresh: _loadData,
        color: const Color(0xFF1B4332),
        child: SingleChildScrollView(
          padding: const EdgeInsets.all(20),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              // Identity Overview Block Card
              Card(
                child: Padding(
                  padding: const EdgeInsets.all(20),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      const Text('System Ledger Credentials', style: TextStyle(fontSize: 15, fontWeight: FontWeight.w900, color: Color(0xFF0F291B))),
                      const SizedBox(height: 16),
                      _buildInfoRow(Icons.phone_forwarded_rounded, 'Phone Identity', _phone),
                      const Divider(height: 24, color: Color(0xFFF1F5F9)),
                      if (_editing) ...[
                        TextField(
                          controller: _emailController,
                          decoration: const InputDecoration(labelText: 'Secure Mail Route'),
                          keyboardType: TextInputType.emailAddress,
                        ),
                        const SizedBox(height: 16),
                        DropdownButtonFormField<String>(
                          value: _language,
                          items: const [
                            DropdownMenuItem(value: 'am', child: Text('አማርኛ')),
                            DropdownMenuItem(value: 'en', child: Text('English')),
                          ],
                          onChanged: (v) => setState(() => _language = v!),
                          decoration: const InputDecoration(labelText: 'System Translation'),
                        ),
                        const SizedBox(height: 20),
                        SizedBox(
                          width: double.infinity,
                          child: ElevatedButton(onPressed: _updateProfile, child: const Text('COMMIT CHANGES')),
                        ),
                      ] else ...[
                        _buildInfoRow(Icons.alternate_email_rounded, 'Mail Node', _email.isEmpty ? 'Not assigned' : _email),
                        const Divider(height: 24, color: Color(0xFFF1F5F9)),
                        _buildKycRow(),
                        const Divider(height: 24, color: Color(0xFFF1F5F9)),
                        _buildInfoRow(Icons.translate_rounded, 'Language Vector', _language == 'am' ? 'አማርኛ (AM)' : 'English (EN)'),
                      ],
                    ],
                  ),
                ),
              ),
              const SizedBox(height: 20),

              // Settlement Channels Header
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  const Text('Settlement Channels', style: TextStyle(fontSize: 16, fontWeight: FontWeight.w900, color: Color(0xFF0F291B), letterSpacing: -0.4)),
                  if (!_showAddForm)
                    IconButton(
                      icon: const Icon(Icons.add_circle_rounded, color: Color(0xFF1B4332), size: 28),
                      onPressed: () => setState(() => _showAddForm = true),
                    ),
                ],
              ),
              const SizedBox(height: 12),

              if (_showAddForm) _buildAddAccountForm(),

              if (_loading)
                const Center(child: Padding(padding: EdgeInsets.all(40), child: CircularProgressIndicator(color: Color(0xFF1B4332))))
              else if (_accounts.isEmpty && !_showAddForm)
                _buildEmptyAccountsPlaceholder(),

              ..._accounts.map((acc) => _buildBankAccountItem(acc)),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildInfoRow(IconData icon, String label, String value) {
    return Row(
      children: [
        Icon(icon, size: 18, color: const Color(0xFF64748B)),
        const SizedBox(width: 12),
        Expanded(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(label, style: const TextStyle(fontSize: 11, color: Color(0xFF94A3B8), fontWeight: FontWeight.bold)),
              const SizedBox(height: 1),
              Text(value, style: const TextStyle(fontSize: 14, color: Color(0xFF0F291B), fontWeight: FontWeight.bold)),
            ],
          ),
        ),
      ],
    );
  }

  Widget _buildKycRow() {
    bool isVerified = _kycStatus == 'VERIFIED';
    return Row(
      children: [
        Icon(Icons.verified_user_rounded, size: 18, color: isVerified ? const Color(0xFF15803D) : const Color(0xFFB45309)),
        const SizedBox(width: 12),
        Expanded(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              const Text('Identity Verification', style: TextStyle(fontSize: 11, color: Color(0xFF94A3B8), fontWeight: FontWeight.bold)),
              const SizedBox(height: 4),
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
                decoration: BoxDecoration(
                  color: isVerified ? const Color(0xFFDCFCE7) : const Color(0xFFFEF3C7),
                  borderRadius: BorderRadius.circular(8),
                ),
                child: Text(
                  isVerified ? 'VERIFIED IDENTITY POOL' : 'PENDING LEDGER RE-INDEX',
                  style: TextStyle(fontSize: 10,  fontWeight: FontWeight.w900, color: isVerified ? const Color(0xFF166534) : const Color(0xFF92400E)),
                ),
              ),
            ],
          ),
        ),
      ],
    );
  }

  Widget _buildAddAccountForm() {
    return Container(
      margin: const EdgeInsets.only(bottom: 16),
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(24),
        border: Border.all(color: const Color(0xFF1B4332), width: 1.5),
      ),
      child: Column(
        children: [
          DropdownButtonFormField<String>(
            value: _selectedAccountType,
            items: const [
              DropdownMenuItem(value: 'TELEBIRR', child: Text('Telebirr Mobile Money')),
              DropdownMenuItem(value: 'CBE', child: Text('Commercial Bank of Ethiopia'))
            ],
            onChanged: (v) => setState(() => _selectedAccountType = v!),
            decoration: const InputDecoration(labelText: 'Channel Provider'),
          ),
          const SizedBox(height: 12),
          TextField(controller: _accountNumberController, decoration: const InputDecoration(labelText: 'Target Account / Phone Identifier'), keyboardType: TextInputType.phone),
          const SizedBox(height: 12),
          TextField(controller: _accountHolderController, decoration: const InputDecoration(labelText: 'Legal Name Signature')),
          const SizedBox(height: 16),
          Row(
            children: [
              Expanded(child: ElevatedButton(onPressed: _addAccount, child: const Text('LINK NODE'))),
              const SizedBox(width: 12),
              Expanded(child: OutlinedButton(onPressed: () => setState(() => _showAddForm = false), child: const Text('CANCEL'))),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildEmptyAccountsPlaceholder() {
    return Container(
      padding: const EdgeInsets.all(32),
      child: Container(
        child: Text(
          'No settlement channels linked yet.',
          textAlign: TextAlign.center,
          style: TextStyle(fontSize: 13, color: Colors.blueGrey.shade400, fontWeight: FontWeight.bold),
        ),
      ),
    );
  }

  Widget _buildBankAccountItem(BankAccountModel acc) {
    return Container(
      margin: const EdgeInsets.only(bottom: 12),
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(20),
        border: Border.all(color: acc.isDefault ? const Color(0xFF1B4332) : const Color(0xFFE2E8F0), width: acc.isDefault ? 1.5 : 1),
      ),
      child: Row(
        children: [
          Container(
            padding: const EdgeInsets.all(12),
            decoration: BoxDecoration(color: const Color(0xFFF4F7F5), borderRadius: BorderRadius.circular(14)),
            child: Icon(acc.accountType == 'TELEBIRR' ? Icons.phone_android_rounded : Icons.account_balance_rounded, color: const Color(0xFF1B4332)),
          ),
          const SizedBox(width: 16),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Row(
                  children: [
                    Text(acc.accountType, style: const TextStyle(fontWeight: FontWeight.w900, fontSize: 14, color: Color(0xFF0F291B))),
                    const SizedBox(width: 8),
                    if (acc.isDefault) const Text('DEFAULT', style: TextStyle(fontSize: 9, fontWeight: FontWeight.w900, color: Color(0xFF1B4332))),
                  ],
                ),
                const SizedBox(height: 2),
                Text(acc.getMaskedNumber(), style: const TextStyle(fontFamily: 'monospace', color: Color(0xFF64748B), fontSize: 13, fontWeight: FontWeight.bold)),
              ],
            ),
          ),
          PopupMenuButton<String>(
            shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
            onSelected: (v) {
              if (v == 'verify') _verifyAccount(acc.id);
              if (v == 'default') _setDefault(acc.id);
              if (v == 'delete') _deleteAccount(acc.id);
            },
            itemBuilder: (context) => [
              if (!acc.isVerified) const PopupMenuItem(value: 'verify', child: Text('Verify Channel', style: TextStyle(fontSize: 13, fontWeight: FontWeight.bold))),
              if (!acc.isDefault && acc.isVerified) const PopupMenuItem(value: 'default', child: Text('Promote to Default', style: TextStyle(fontSize: 13, fontWeight: FontWeight.bold))),
              const PopupMenuItem(value: 'delete', child: Text('Sever Link', style: TextStyle(color: Colors.red, fontSize: 13, fontWeight: FontWeight.bold))),
            ],
          ),
        ],
      ),
    );
  }
}
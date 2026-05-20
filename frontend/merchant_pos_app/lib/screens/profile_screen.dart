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
  String _businessName = '';
  String _licenseNumber = '';
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
      // FIX: normalize language to lowercase so it matches dropdown values 'am'/'en'
      final lang = (user.preferredLanguage ?? 'am').toLowerCase();
      _language = (lang == 'am' || lang == 'en') ? lang : 'am';
      _businessName = user.businessName ?? '';
      _licenseNumber = user.businessLicenseNumber ?? '';
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
    ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Profile updated')));
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
    ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Account added! Check terminal for verification code (ETB1)')));
  }

  Future<void> _verifyAccount(String id) async {
    final codeController = TextEditingController();
    final code = await showDialog<String>(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Verify Account'),
        content: TextField(
          controller: codeController,
          decoration: const InputDecoration(labelText: 'Verification Code', hintText: 'Enter ETB1'),
        ),
        actions: [
          TextButton(onPressed: () => Navigator.pop(context, null), child: const Text('Cancel')),
          TextButton(onPressed: () => Navigator.pop(context, codeController.text), child: const Text('Verify')),
        ],
      ),
    );
    if (code == null || code.isEmpty) return;
    await _bankService.verifyBankAccount(accountId: id, verificationCode: code);
    await _loadData();
    if (mounted) ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Account verified!')));
  }

  Future<void> _setDefault(String id) async {
    await _bankService.setDefaultAccount(id);
    await _loadData();
    if (mounted) ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Default account set')));
  }

  Future<void> _deleteAccount(String id) async {
    final confirm = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Delete Account'),
        content: const Text('Are you sure?'),
        actions: [
          TextButton(onPressed: () => Navigator.pop(context, false), child: const Text('Cancel')),
          TextButton(onPressed: () => Navigator.pop(context, true), child: const Text('Delete', style: TextStyle(color: Colors.red))),
        ],
      ),
    );
    if (confirm != true) return;
    await _bankService.deleteBankAccount(id);
    await _loadData();
    if (mounted) ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Account deleted')));
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('My Profile'),
        backgroundColor: Colors.orange,
        foregroundColor: Colors.white,
        actions: [
          IconButton(
            icon: Icon(_editing ? Icons.close : Icons.edit),
            onPressed: () => setState(() => _editing = !_editing),
          ),
        ],
      ),
      body: _loading
          ? const Center(child: CircularProgressIndicator())
          : RefreshIndicator(
              onRefresh: _loadData,
              child: SingleChildScrollView(
                padding: const EdgeInsets.all(16),
                child: Column(
                  children: [
                    // Profile Card
                    Card(
                      child: Padding(
                        padding: const EdgeInsets.all(16),
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            const Text('Profile', style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
                            const SizedBox(height: 12),
                            _buildInfoRow('Phone', _phone),
                            if (_businessName.isNotEmpty) _buildInfoRow('Business', _businessName),
                            if (_licenseNumber.isNotEmpty) _buildInfoRow('License', _licenseNumber),
                            _buildInfoRow('KYC Status', _kycStatus == 'VERIFIED' ? '✅ Verified' : '⏳ Pending'),
                            const SizedBox(height: 8),
                            if (_editing) ...[
                              TextField(
                                controller: _emailController,
                                decoration: const InputDecoration(labelText: 'Email', border: OutlineInputBorder()),
                                keyboardType: TextInputType.emailAddress,
                              ),
                              const SizedBox(height: 12),
                              DropdownButtonFormField<String>(
                                value: _language,
                                items: const [
                                  DropdownMenuItem(value: 'am', child: Text('አማርኛ')),
                                  DropdownMenuItem(value: 'en', child: Text('English')),
                                ],
                                onChanged: (v) => setState(() => _language = v!),
                                decoration: const InputDecoration(labelText: 'Language', border: OutlineInputBorder()),
                              ),
                              const SizedBox(height: 12),
                              SizedBox(
                                width: double.infinity,
                                child: ElevatedButton(
                                  onPressed: _updateProfile,
                                  style: ElevatedButton.styleFrom(backgroundColor: Colors.orange),
                                  child: const Text('Save Changes', style: TextStyle(color: Colors.white)),
                                ),
                              ),
                            ] else ...[
                              _buildInfoRow('Email', _email.isEmpty ? 'Not set' : _email),
                              _buildInfoRow('Language', _language == 'am' ? 'አማርኛ' : 'English'),
                            ],
                          ],
                        ),
                      ),
                    ),
                    const SizedBox(height: 16),
                    // Bank Accounts Card
                    Card(
                      child: Padding(
                        padding: const EdgeInsets.all(16),
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Row(
                              mainAxisAlignment: MainAxisAlignment.spaceBetween,
                              children: [
                                const Text('Bank Accounts', style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
                                if (!_showAddForm)
                                  IconButton(
                                    icon: const Icon(Icons.add, color: Colors.orange),
                                    onPressed: () => setState(() => _showAddForm = true),
                                  ),
                              ],
                            ),
                            if (_showAddForm)
                              Container(
                                margin: const EdgeInsets.only(bottom: 16),
                                padding: const EdgeInsets.all(12),
                                decoration: BoxDecoration(
                                  border: Border.all(color: Colors.orange),
                                  borderRadius: BorderRadius.circular(8),
                                ),
                                child: Column(
                                  children: [
                                    DropdownButtonFormField<String>(
                                      value: _selectedAccountType,
                                      items: const [
                                        DropdownMenuItem(value: 'TELEBIRR', child: Text('Telebirr')),
                                        DropdownMenuItem(value: 'CBE', child: Text('CBE Bank')),
                                      ],
                                      onChanged: (v) => setState(() => _selectedAccountType = v!),
                                      decoration: const InputDecoration(labelText: 'Account Type'),
                                    ),
                                    TextField(
                                      controller: _accountNumberController,
                                      decoration: const InputDecoration(labelText: 'Account Number'),
                                      keyboardType: TextInputType.phone,
                                    ),
                                    TextField(
                                      controller: _accountHolderController,
                                      decoration: const InputDecoration(labelText: 'Holder Name (Optional)'),
                                    ),
                                    const SizedBox(height: 12),
                                    Row(
                                      children: [
                                        Expanded(
                                          child: ElevatedButton(
                                            onPressed: _addAccount,
                                            style: ElevatedButton.styleFrom(backgroundColor: Colors.orange),
                                            child: const Text('Add', style: TextStyle(color: Colors.white)),
                                          ),
                                        ),
                                        const SizedBox(width: 12),
                                        Expanded(
                                          child: OutlinedButton(
                                            onPressed: () => setState(() => _showAddForm = false),
                                            child: const Text('Cancel'),
                                          ),
                                        ),
                                      ],
                                    ),
                                  ],
                                ),
                              ),
                            if (_accounts.isEmpty && !_showAddForm)
                              const Padding(
                                padding: EdgeInsets.all(32),
                                child: Center(child: Text('No bank accounts linked')),
                              ),
                            ..._accounts.map((acc) => Container(
                              margin: const EdgeInsets.only(bottom: 12),
                              padding: const EdgeInsets.all(12),
                              decoration: BoxDecoration(
                                border: Border.all(color: acc.isDefault ? Colors.orange : Colors.grey),
                                borderRadius: BorderRadius.circular(8),
                                color: acc.isDefault ? Colors.orange.shade50 : null,
                              ),
                              child: Column(
                                crossAxisAlignment: CrossAxisAlignment.start,
                                children: [
                                  Row(
                                    children: [
                                      Text(acc.accountType, style: const TextStyle(fontWeight: FontWeight.bold)),
                                      const SizedBox(width: 8),
                                      if (acc.isDefault)
                                        const Chip(
                                          label: Text('Default'),
                                          backgroundColor: Colors.orange,
                                          labelStyle: TextStyle(color: Colors.white),
                                          materialTapTargetSize: MaterialTapTargetSize.shrinkWrap,
                                        ),
                                      if (acc.isVerified)
                                        const Chip(
                                          label: Text('Verified'),
                                          backgroundColor: Colors.green,
                                          labelStyle: TextStyle(color: Colors.white),
                                          materialTapTargetSize: MaterialTapTargetSize.shrinkWrap,
                                        ),
                                      const Spacer(),
                                      PopupMenuButton(
                                        onSelected: (v) {
                                          if (v == 'verify') _verifyAccount(acc.id);
                                          else if (v == 'default') _setDefault(acc.id);
                                          else if (v == 'delete') _deleteAccount(acc.id);
                                        },
                                        itemBuilder: (context) => [
                                          if (!acc.isVerified) const PopupMenuItem(value: 'verify', child: Text('Verify')),
                                          if (!acc.isDefault && acc.isVerified) const PopupMenuItem(value: 'default', child: Text('Set Default')),
                                          const PopupMenuItem(value: 'delete', child: Text('Delete', style: TextStyle(color: Colors.red))),
                                        ],
                                      ),
                                    ],
                                  ),
                                  const SizedBox(height: 8),
                                  Text('Account: ${acc.getMaskedNumber()}'),
                                  if (acc.accountHolderName.isNotEmpty) Text('Holder: ${acc.accountHolderName}'),
                                ],
                              ),
                            )),
                          ],
                        ),
                      ),
                    ),
                  ],
                ),
              ),
            ),
    );
  }

  Widget _buildInfoRow(String label, String value) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 4),
      child: Row(
        children: [
          SizedBox(width: 100, child: Text(label, style: const TextStyle(color: Colors.grey))),
          Expanded(child: Text(value)),
        ],
      ),
    );
  }
}

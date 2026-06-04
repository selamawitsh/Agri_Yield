import 'package:flutter/material.dart';
import '../services/auth_service.dart';
import '../services/bank_account_service.dart';
import '../services/merchant_service.dart';
import '../models/bank_account_model.dart';
import '../models/merchant_profile_model.dart';

class ProfileScreen extends StatefulWidget {
  const ProfileScreen({super.key});

  @override
  State<ProfileScreen> createState() => _ProfileScreenState();
}

class _ProfileScreenState extends State<ProfileScreen>
    with SingleTickerProviderStateMixin {
  final _authService     = AuthService();
  final _bankService     = BankAccountService();
  final _merchantService = MerchantService();

  late TabController _tabController;

  // user fields
  String _phone    = '';
  String _email    = '';
  String _kycStatus = '';
  String _language = 'am';
  bool   _loading  = true;

  // merchant fields
  MerchantProfile? _profile;
  bool _editingUser     = false;
  bool _editingMerchant = false;

  // user edit controllers
  final _emailCtrl    = TextEditingController();

  // merchant edit controllers
  final _bizNameCtrl   = TextEditingController();
  final _telebirrCtrl  = TextEditingController();
  final _latCtrl       = TextEditingController();
  final _lngCtrl       = TextEditingController();
  final _kebeleCtrl    = TextEditingController();

  // bank accounts
  List<BankAccountModel> _accounts    = [];
  bool   _showAddForm                 = false;
  final  _accountNumberCtrl           = TextEditingController();
  final  _accountHolderCtrl           = TextEditingController();
  String _selectedAccountType         = 'TELEBIRR';

  @override
  void initState() {
    super.initState();
    _tabController = TabController(length: 3, vsync: this);
    _loadData();
  }

  @override
  void dispose() {
    _tabController.dispose();
    _emailCtrl.dispose();
    _bizNameCtrl.dispose();
    _telebirrCtrl.dispose();
    _latCtrl.dispose();
    _lngCtrl.dispose();
    _kebeleCtrl.dispose();
    _accountNumberCtrl.dispose();
    _accountHolderCtrl.dispose();
    super.dispose();
  }

  Future<void> _loadData() async {
    setState(() => _loading = true);
    final user    = await _authService.getCurrentUser();
    final profile = await _merchantService.getMyProfile();
    final accounts = await _bankService.getBankAccounts();

    if (mounted) {
      setState(() {
        if (user != null) {
          _phone     = user.phone;
          _email     = user.email ?? '';
          _kycStatus = user.kycStatus;
          final lang = (user.preferredLanguage ?? 'am').toLowerCase();
          _language  = (lang == 'am' || lang == 'en') ? lang : 'am';
          _emailCtrl.text = _email;
        }
        _profile  = profile;
        _accounts = accounts;
        if (profile != null) {
          _bizNameCtrl.text  = profile.businessName;
          _telebirrCtrl.text = profile.telebirrAccount;
          _latCtrl.text      = profile.storeGpsLat.toString();
          _lngCtrl.text      = profile.storeGpsLng.toString();
        }
        _loading = false;
      });
    }
  }

  Future<void> _saveUserProfile() async {
    await _authService.updateProfile(
      email:             _emailCtrl.text.isEmpty ? null : _emailCtrl.text,
      preferredLanguage: _language,
    );
    setState(() => _editingUser = false);
    await _loadData();
    if (mounted) {
      ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Profile updated')));
    }
  }

  // MS-03: save merchant-specific fields
  Future<void> _saveMerchantProfile() async {
    final lat = double.tryParse(_latCtrl.text.trim());
    final lng = double.tryParse(_lngCtrl.text.trim());
    if (lat == null || lng == null) {
      ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('GPS coordinates must be valid numbers')));
      return;
    }
    final result = await _merchantService.updateMerchantProfile(
      businessName:    _bizNameCtrl.text.trim().isEmpty ? null : _bizNameCtrl.text.trim(),
      telebirrAccount: _telebirrCtrl.text.trim().isEmpty ? null : _telebirrCtrl.text.trim(),
      storeGpsLat:     lat,
      storeGpsLng:     lng,
      kebeleCode:      _kebeleCtrl.text.trim().isEmpty ? null : _kebeleCtrl.text.trim(),
    );
    if (result['success'] == true) {
      setState(() => _editingMerchant = false);
      await _loadData();
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(content: Text('Business profile updated')));
      }
    } else {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(content: Text(result['message'] ?? 'Update failed')));
      }
    }
  }

  Future<void> _addAccount() async {
    if (_accountNumberCtrl.text.isEmpty) return;
    setState(() => _loading = true);
    await _bankService.addBankAccount(
      accountType:       _selectedAccountType,
      accountNumber:     _accountNumberCtrl.text,
      accountHolderName: _accountHolderCtrl.text,
    );
    setState(() {
      _showAddForm = false;
      _accountNumberCtrl.clear();
      _accountHolderCtrl.clear();
    });
    await _loadData();
    if (mounted) {
      ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Account added')));
    }
  }

  Future<void> _verifyAccount(String id) async {
    final codeCtrl = TextEditingController();
    final code = await showDialog<String>(
      context: context,
      builder: (ctx) => AlertDialog(
        title: const Text('Verify Account'),
        content: TextField(
          controller: codeCtrl,
          decoration: const InputDecoration(labelText: 'Verification Code'),
        ),
        actions: [
          TextButton(onPressed: () => Navigator.pop(ctx), child: const Text('Cancel')),
          TextButton(
              onPressed: () => Navigator.pop(ctx, codeCtrl.text),
              child: const Text('Verify')),
        ],
      ),
    );
    if (code == null || code.isEmpty) return;
    await _bankService.verifyBankAccount(accountId: id, verificationCode: code);
    await _loadData();
    if (mounted) {
      ScaffoldMessenger.of(context)
          .showSnackBar(const SnackBar(content: Text('Account verified')));
    }
  }

  Future<void> _setDefault(String id) async {
    await _bankService.setDefaultAccount(id);
    await _loadData();
    if (mounted) {
      ScaffoldMessenger.of(context)
          .showSnackBar(const SnackBar(content: Text('Default account set')));
    }
  }

  Future<void> _deleteAccount(String id) async {
    final confirm = await showDialog<bool>(
      context: context,
      builder: (ctx) => AlertDialog(
        title: const Text('Delete Account'),
        content: const Text('Are you sure?'),
        actions: [
          TextButton(
              onPressed: () => Navigator.pop(ctx, false),
              child: const Text('Cancel')),
          TextButton(
              onPressed: () => Navigator.pop(ctx, true),
              child: const Text('Delete',
                  style: TextStyle(color: Colors.red))),
        ],
      ),
    );
    if (confirm != true) return;
    await _bankService.deleteBankAccount(id);
    await _loadData();
    if (mounted) {
      ScaffoldMessenger.of(context)
          .showSnackBar(const SnackBar(content: Text('Account deleted')));
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('My Profile'),
        backgroundColor: Colors.orange,
        foregroundColor: Colors.white,
        bottom: TabBar(
          controller: _tabController,
          labelColor: Colors.white,
          unselectedLabelColor: Colors.white70,
          indicatorColor: Colors.white,
          tabs: const [
            Tab(text: 'Account'),
            Tab(text: 'Business'),
            Tab(text: 'Payments'),
          ],
        ),
      ),
      body: _loading
          ? const Center(child: CircularProgressIndicator(color: Colors.orange))
          : TabBarView(
              controller: _tabController,
              children: [
                _buildAccountTab(),
                _buildBusinessTab(),
                _buildPaymentsTab(),
              ],
            ),
    );
  }

  // ── Tab 1: Account ────────────────────────────────────────────────────────

  Widget _buildAccountTab() {
    return RefreshIndicator(
      onRefresh: _loadData,
      child: SingleChildScrollView(
        physics: const AlwaysScrollableScrollPhysics(),
        padding: const EdgeInsets.all(16),
        child: Column(
          children: [
            // Identity card
            _card(
              title: 'Identity',
              trailing: IconButton(
                icon: Icon(_editingUser ? Icons.close : Icons.edit,
                    color: Colors.orange),
                onPressed: () => setState(() => _editingUser = !_editingUser),
              ),
              children: [
                _infoRow('Phone', _phone),
                _infoRow('KYC',
                    _kycStatus == 'VERIFIED' ? 'Verified' : 'Pending'),
                if (_editingUser) ...[
                  const SizedBox(height: 12),
                  TextField(
                    controller: _emailCtrl,
                    decoration: const InputDecoration(
                        labelText: 'Email', border: OutlineInputBorder()),
                    keyboardType: TextInputType.emailAddress,
                  ),
                  const SizedBox(height: 12),
                  DropdownButtonFormField<String>(
                    value: _language,
                    items: const [
                      DropdownMenuItem(value: 'am', child: Text('Amharic')),
                      DropdownMenuItem(value: 'en', child: Text('English')),
                    ],
                    onChanged: (v) => setState(() => _language = v!),
                    decoration: const InputDecoration(
                        labelText: 'Language',
                        border: OutlineInputBorder()),
                  ),
                  const SizedBox(height: 12),
                  _saveBtn('Save Account', _saveUserProfile),
                ] else ...[
                  _infoRow('Email', _email.isEmpty ? 'Not set' : _email),
                  _infoRow(
                      'Language', _language == 'am' ? 'Amharic' : 'English'),
                ],
              ],
            ),
          ],
        ),
      ),
    );
  }

  // ── Tab 2: Business ───────────────────────────────────────────────────────

  Widget _buildBusinessTab() {
    final p = _profile;
    return RefreshIndicator(
      onRefresh: _loadData,
      child: SingleChildScrollView(
        physics: const AlwaysScrollableScrollPhysics(),
        padding: const EdgeInsets.all(16),
        child: Column(
          children: [
            _card(
              title: 'Business Information',
              trailing: IconButton(
                icon: Icon(_editingMerchant ? Icons.close : Icons.edit,
                    color: Colors.orange),
                onPressed: () =>
                    setState(() => _editingMerchant = !_editingMerchant),
              ),
              children: [
                if (_editingMerchant) ...[
                  _editField(_bizNameCtrl, 'Business Name', Icons.store),
                  _editField(_telebirrCtrl, 'Telebirr Account',
                      Icons.phone_android,
                      type: TextInputType.phone),
                  Row(children: [
                    Expanded(
                        child: _editField(_latCtrl, 'GPS Latitude',
                            Icons.location_on,
                            type: const TextInputType.numberWithOptions(
                                decimal: true))),
                    const SizedBox(width: 12),
                    Expanded(
                        child: _editField(_lngCtrl, 'GPS Longitude',
                            Icons.location_on,
                            type: const TextInputType.numberWithOptions(
                                decimal: true))),
                  ]),
                  _editField(_kebeleCtrl, 'Kebele Code (optional)',
                      Icons.map),
                  const SizedBox(height: 4),
                  _saveBtn('Save Business Profile', _saveMerchantProfile),
                ] else ...[
                  _infoRow('Business Name', p?.businessName ?? 'Not set'),
                  _infoRow('License', p?.businessLicenseNumber ?? 'Not set'),
                  _infoRow('Telebirr', p?.telebirrAccount ?? 'Not set'),
                  _infoRow('Store Latitude',
                      p != null ? p.storeGpsLat.toStringAsFixed(6) : 'Not set'),
                  _infoRow('Store Longitude',
                      p != null ? p.storeGpsLng.toStringAsFixed(6) : 'Not set'),
                ],
              ],
            ),
            const SizedBox(height: 16),
            _card(
              title: 'Verification Status',
              children: [
                _infoRow('Physical Verification',
                    (p?.isPhysicallyVerified ?? false)
                        ? 'Verified'
                        : 'Pending'),
                if (p?.physicallyVerifiedAt != null)
                  _infoRow('Verified On',
                      _formatDate(p!.physicallyVerifiedAt!)),
                _infoRow(
                    'Subscription', p?.subscriptionTier ?? 'BASIC'),
                _infoRow('Member Since',
                    p != null ? _formatDate(p.createdAt) : 'Unknown'),
              ],
            ),
          ],
        ),
      ),
    );
  }

  // ── Tab 3: Payments (bank accounts) ──────────────────────────────────────

  Widget _buildPaymentsTab() {
    return RefreshIndicator(
      onRefresh: _loadData,
      child: SingleChildScrollView(
        physics: const AlwaysScrollableScrollPhysics(),
        padding: const EdgeInsets.all(16),
        child: Column(
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                const Text('Bank Accounts',
                    style: TextStyle(
                        fontSize: 16, fontWeight: FontWeight.bold)),
                if (!_showAddForm)
                  TextButton.icon(
                    onPressed: () =>
                        setState(() => _showAddForm = true),
                    icon: const Icon(Icons.add, color: Colors.orange),
                    label: const Text('Add',
                        style: TextStyle(color: Colors.orange)),
                  ),
              ],
            ),
            const SizedBox(height: 8),
            if (_showAddForm) _buildAddAccountForm(),
            if (_accounts.isEmpty && !_showAddForm)
              const Padding(
                padding: EdgeInsets.all(32),
                child: Center(child: Text('No bank accounts linked')),
              ),
            ..._accounts.map((acc) => _buildAccountCard(acc)),
          ],
        ),
      ),
    );
  }

  Widget _buildAddAccountForm() {
    return Container(
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
              DropdownMenuItem(value: 'CBE',      child: Text('CBE Bank')),
            ],
            onChanged: (v) => setState(() => _selectedAccountType = v!),
            decoration:
                const InputDecoration(labelText: 'Account Type'),
          ),
          TextField(
            controller: _accountNumberCtrl,
            decoration:
                const InputDecoration(labelText: 'Account Number'),
            keyboardType: TextInputType.phone,
          ),
          TextField(
            controller: _accountHolderCtrl,
            decoration: const InputDecoration(
                labelText: 'Holder Name (optional)'),
          ),
          const SizedBox(height: 12),
          Row(children: [
            Expanded(
              child: ElevatedButton(
                onPressed: _addAccount,
                style: ElevatedButton.styleFrom(
                    backgroundColor: Colors.orange),
                child: const Text('Add',
                    style: TextStyle(color: Colors.white)),
              ),
            ),
            const SizedBox(width: 12),
            Expanded(
              child: OutlinedButton(
                onPressed: () =>
                    setState(() => _showAddForm = false),
                child: const Text('Cancel'),
              ),
            ),
          ]),
        ],
      ),
    );
  }

  Widget _buildAccountCard(BankAccountModel acc) {
    return Container(
      margin: const EdgeInsets.only(bottom: 12),
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(
        border: Border.all(
            color: acc.isDefault ? Colors.orange : Colors.grey.shade300),
        borderRadius: BorderRadius.circular(8),
        color: acc.isDefault ? Colors.orange.shade50 : null,
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(children: [
            Text(acc.accountType,
                style: const TextStyle(fontWeight: FontWeight.bold)),
            const SizedBox(width: 8),
            if (acc.isDefault)
              _chip('Default', Colors.orange),
            if (acc.isVerified)
              _chip('Verified', Colors.green),
            const Spacer(),
            PopupMenuButton(
              onSelected: (v) {
                if (v == 'verify')  _verifyAccount(acc.id);
                if (v == 'default') _setDefault(acc.id);
                if (v == 'delete')  _deleteAccount(acc.id);
              },
              itemBuilder: (_) => [
                if (!acc.isVerified)
                  const PopupMenuItem(
                      value: 'verify', child: Text('Verify')),
                if (!acc.isDefault && acc.isVerified)
                  const PopupMenuItem(
                      value: 'default', child: Text('Set Default')),
                const PopupMenuItem(
                    value: 'delete',
                    child: Text('Delete',
                        style: TextStyle(color: Colors.red))),
              ],
            ),
          ]),
          const SizedBox(height: 8),
          Text('Account: ${acc.getMaskedNumber()}'),
          if (acc.accountHolderName.isNotEmpty)
            Text('Holder: ${acc.accountHolderName}'),
        ],
      ),
    );
  }

  // ── Helpers ───────────────────────────────────────────────────────────────

  Widget _card({
    required String title,
    required List<Widget> children,
    Widget? trailing,
  }) {
    return Card(
      margin: const EdgeInsets.only(bottom: 16),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                Text(title,
                    style: const TextStyle(
                        fontSize: 16, fontWeight: FontWeight.bold)),
                const Spacer(),
                if (trailing != null) trailing,
              ],
            ),
            const SizedBox(height: 12),
            ...children,
          ],
        ),
      ),
    );
  }

  Widget _infoRow(String label, String value) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 4),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          SizedBox(
              width: 130,
              child: Text(label,
                  style: const TextStyle(color: Colors.grey))),
          Expanded(child: Text(value)),
        ],
      ),
    );
  }

  Widget _editField(
    TextEditingController ctrl,
    String label,
    IconData icon, {
    TextInputType type = TextInputType.text,
  }) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 12),
      child: TextField(
        controller: ctrl,
        keyboardType: type,
        decoration: InputDecoration(
          labelText: label,
          prefixIcon: Icon(icon),
          border: const OutlineInputBorder(),
        ),
      ),
    );
  }

  Widget _saveBtn(String label, VoidCallback onTap) {
    return SizedBox(
      width: double.infinity,
      child: ElevatedButton(
        onPressed: onTap,
        style: ElevatedButton.styleFrom(
            backgroundColor: Colors.orange,
            padding: const EdgeInsets.symmetric(vertical: 14)),
        child: Text(label,
            style: const TextStyle(color: Colors.white)),
      ),
    );
  }

  Widget _chip(String label, Color color) {
    return Container(
      margin: const EdgeInsets.only(right: 6),
      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
      decoration: BoxDecoration(
          color: color, borderRadius: BorderRadius.circular(99)),
      child: Text(label,
          style: const TextStyle(
              color: Colors.white, fontSize: 11)),
    );
  }

  String _formatDate(String iso) {
    try {
      final dt = DateTime.parse(iso);
      return '${dt.day}/${dt.month}/${dt.year}';
    } catch (_) {
      return iso;
    }
  }
}

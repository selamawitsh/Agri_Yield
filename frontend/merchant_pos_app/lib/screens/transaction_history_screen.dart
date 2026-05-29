import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import '../services/voucher_service.dart';
import '../models/voucher_model.dart';
import '../widgets/voucher_category_badge.dart';
 
class TransactionHistoryScreen extends StatefulWidget {
  const TransactionHistoryScreen({super.key});
 
  @override
  State<TransactionHistoryScreen> createState() =>
      _TransactionHistoryScreenState();
}
 
class _TransactionHistoryScreenState extends State<TransactionHistoryScreen> {
  final _voucherService = VoucherService();
  List<MerchantRedemptionSummary> _items = [];
  bool _loading = true;
  String _filterStatus = 'ALL';
  String _searchQuery = '';
  final _searchCtrl = TextEditingController();
 
  double get _totalEtb =>
      _items.fold(0, (sum, r) => sum + r.amountEtb);
 
  List<MerchantRedemptionSummary> get _filtered {
    return _items.where((r) {
      final matchStatus =
          _filterStatus == 'ALL' || r.status == _filterStatus;
      final matchSearch = _searchQuery.isEmpty ||
          r.farmerName
              .toLowerCase()
              .contains(_searchQuery.toLowerCase()) ||
          r.productDescription
              .toLowerCase()
              .contains(_searchQuery.toLowerCase()) ||
          r.voucherId.toLowerCase().contains(_searchQuery.toLowerCase());
      return matchStatus && matchSearch;
    }).toList();
  }
 
  @override
  void initState() {
    super.initState();
    _load();
  }
 
  @override
  void dispose() {
    _searchCtrl.dispose();
    super.dispose();
  }
 
  Future<void> _load() async {
    setState(() => _loading = true);
    final data = await _voucherService.getRedemptionHistory();
    if (mounted) setState(() { _items = data; _loading = false; });
  }
 
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Redemption History'),
        backgroundColor: Colors.orange,
        foregroundColor: Colors.white,
        actions: [
          IconButton(
            icon: const Icon(Icons.refresh),
            onPressed: _load,
          ),
        ],
      ),
      body: Column(
        children: [
          // Summary banner
          Container(
            width: double.infinity,
            padding: const EdgeInsets.fromLTRB(20, 16, 20, 16),
            decoration: const BoxDecoration(
              gradient: LinearGradient(
                colors: [Colors.orange, Colors.deepOrange],
              ),
            ),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceAround,
              children: [
                _stat('Total Redeemed', _items.length.toString()),
                _statDivider(),
                _stat('Total Earned',
                    'ETB ${NumberFormat('#,##0.00').format(_totalEtb)}'),
                _statDivider(),
                _stat('This Month',
                    _items
                        .where((r) => r.redeemedAt.startsWith(
                            DateFormat('yyyy-MM').format(DateTime.now())))
                        .length
                        .toString()),
              ],
            ),
          ),
 
          // Search bar
          Padding(
            padding: const EdgeInsets.fromLTRB(16, 12, 16, 0),
            child: TextField(
              controller: _searchCtrl,
              decoration: InputDecoration(
                hintText: 'Search by farmer, product, or voucher ID',
                hintStyle:
                    const TextStyle(fontSize: 13, color: Colors.grey),
                prefixIcon:
                    const Icon(Icons.search, color: Colors.orange),
                suffixIcon: _searchQuery.isNotEmpty
                    ? IconButton(
                        icon: const Icon(Icons.clear, size: 18),
                        onPressed: () {
                          _searchCtrl.clear();
                          setState(() => _searchQuery = '');
                        })
                    : null,
                border: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(10)),
                focusedBorder: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(10),
                  borderSide:
                      const BorderSide(color: Colors.orange, width: 2),
                ),
                contentPadding: const EdgeInsets.symmetric(
                    horizontal: 14, vertical: 10),
              ),
              onChanged: (v) => setState(() => _searchQuery = v),
            ),
          ),
 
          // Filter chips
          SingleChildScrollView(
            scrollDirection: Axis.horizontal,
            padding:
                const EdgeInsets.symmetric(horizontal: 16, vertical: 10),
            child: Row(
              children: ['ALL', 'COMPLETED', 'REJECTED'].map((s) {
                final active = _filterStatus == s;
                return Padding(
                  padding: const EdgeInsets.only(right: 8),
                  child: FilterChip(
                    label: Text(s),
                    selected: active,
                    onSelected: (_) =>
                        setState(() => _filterStatus = s),
                    selectedColor: Colors.orange.withOpacity(0.15),
                    checkmarkColor: Colors.orange,
                    labelStyle: TextStyle(
                      color: active ? Colors.orange : Colors.grey[700],
                      fontSize: 12,
                      fontWeight:
                          active ? FontWeight.bold : FontWeight.normal,
                    ),
                  ),
                );
              }).toList(),
            ),
          ),
 
          // List
          Expanded(
            child: _loading
                ? const Center(
                    child: CircularProgressIndicator(color: Colors.orange))
                : _filtered.isEmpty
                    ? _emptyState()
                    : RefreshIndicator(
                        onRefresh: _load,
                        color: Colors.orange,
                        child: ListView.separated(
                          padding: const EdgeInsets.fromLTRB(16, 0, 16, 24),
                          itemCount: _filtered.length,
                          separatorBuilder: (_, __) =>
                              const SizedBox(height: 8),
                          itemBuilder: (_, i) =>
                              _buildCard(_filtered[i]),
                        ),
                      ),
          ),
        ],
      ),
    );
  }
 
  Widget _buildCard(MerchantRedemptionSummary r) {
    final isCompleted = r.status == 'COMPLETED';
    return Card(
      elevation: 1,
      shape:
          RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
      child: InkWell(
        borderRadius: BorderRadius.circular(12),
        onTap: () => _showDetail(r),
        child: Padding(
          padding: const EdgeInsets.all(14),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Row(
                children: [
                  Expanded(
                    child: Text(
                      r.farmerName,
                      style: const TextStyle(
                          fontWeight: FontWeight.bold, fontSize: 15),
                    ),
                  ),
                  Text(
                    'ETB ${NumberFormat('#,##0.00').format(r.amountEtb)}',
                    style: TextStyle(
                      fontWeight: FontWeight.bold,
                      fontSize: 16,
                      color: isCompleted
                          ? Colors.green[700]
                          : Colors.red[700],
                      fontFamily: 'monospace',
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 6),
              Text(
                r.productDescription,
                style: TextStyle(
                    color: Colors.grey[600], fontSize: 13),
              ),
              const SizedBox(height: 10),
              Row(
                children: [
                  VoucherCategoryBadge(
                      category: r.productCategory, fontSize: 11),
                  const Spacer(),
                  Icon(
                    isCompleted
                        ? Icons.check_circle
                        : Icons.cancel,
                    size: 14,
                    color: isCompleted
                        ? Colors.green
                        : Colors.red,
                  ),
                  const SizedBox(width: 4),
                  Text(
                    r.status,
                    style: TextStyle(
                      fontSize: 11,
                      fontWeight: FontWeight.w600,
                      color: isCompleted
                          ? Colors.green[700]
                          : Colors.red[700],
                    ),
                  ),
                  const SizedBox(width: 12),
                  Icon(Icons.access_time,
                      size: 13, color: Colors.grey[400]),
                  const SizedBox(width: 3),
                  Text(
                    _formatDate(r.redeemedAt),
                    style: TextStyle(
                        fontSize: 11, color: Colors.grey[500]),
                  ),
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }
 
  void _showDetail(MerchantRedemptionSummary r) {
    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      backgroundColor: Colors.white,
      shape: const RoundedRectangleBorder(
          borderRadius: BorderRadius.vertical(top: Radius.circular(20))),
      builder: (_) => DraggableScrollableSheet(
        expand: false,
        initialChildSize: 0.6,
        maxChildSize: 0.9,
        builder: (_, ctrl) => SingleChildScrollView(
          controller: ctrl,
          padding: const EdgeInsets.all(24),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Center(
                child: Container(
                  width: 36, height: 4,
                  decoration: BoxDecoration(
                    color: Colors.grey[300],
                    borderRadius: BorderRadius.circular(2),
                  ),
                ),
              ),
              const SizedBox(height: 20),
              const Text('Redemption Detail',
                  style: TextStyle(
                      fontSize: 20, fontWeight: FontWeight.bold)),
              const SizedBox(height: 20),
              _detailRow('Farmer', r.farmerName),
              _detailRow('Product', r.productDescription),
              _detailRow('Category', r.productCategory),
              _detailRow('Amount',
                  'ETB ${NumberFormat('#,##0.00').format(r.amountEtb)}'),
              _detailRow('Status', r.status),
              _detailRow('Redeemed At', r.redeemedAt),
              _detailRow('Voucher ID', r.voucherId, mono: true),
              _detailRow('Payment Ref', r.paymentReference, mono: true),
            ],
          ),
        ),
      ),
    );
  }
 
  Widget _detailRow(String label, String value, {bool mono = false}) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 8),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          SizedBox(
            width: 110,
            child: Text(label,
                style: TextStyle(
                    color: Colors.grey[500], fontSize: 13)),
          ),
          Expanded(
            child: Text(
              value,
              style: TextStyle(
                fontWeight: FontWeight.w600,
                fontSize: 13,
                fontFamily: mono ? 'monospace' : null,
              ),
            ),
          ),
        ],
      ),
    );
  }
 
  Widget _emptyState() => Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(Icons.receipt_long_outlined,
                size: 64, color: Colors.grey[300]),
            const SizedBox(height: 12),
            Text(
              _searchQuery.isNotEmpty
                  ? 'No results for "$_searchQuery"'
                  : 'No redemptions yet',
              style: TextStyle(color: Colors.grey[400], fontSize: 15),
            ),
          ],
        ),
      );
 
  Widget _stat(String label, String value) => Column(
        children: [
          Text(value,
              style: const TextStyle(
                  color: Colors.white,
                  fontWeight: FontWeight.bold,
                  fontSize: 16)),
          Text(label,
              style: const TextStyle(
                  color: Colors.white70, fontSize: 11)),
        ],
      );
 
  Widget _statDivider() => Container(
      height: 30,
      width: 0.5,
      color: Colors.white38);
 
  String _formatDate(String raw) {
    try {
      final dt = DateTime.parse(raw);
      return DateFormat('dd MMM, HH:mm').format(dt);
    } catch (_) {
      return raw;
    }
  }
}

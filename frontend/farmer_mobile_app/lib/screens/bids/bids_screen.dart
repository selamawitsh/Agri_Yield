import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../models/bid_model.dart';
import '../../models/farm_model.dart';
import '../../services/bid_service.dart';
import '../../services/farm_service.dart';
import '../agreements/agreement_screen.dart';
import '../../services/language_service.dart';

class BidsScreen extends StatefulWidget {
  const BidsScreen({super.key});

  @override
  State<BidsScreen> createState() => _BidsScreenState();
}

class _BidsScreenState extends State<BidsScreen>
    with SingleTickerProviderStateMixin {
  final _bidService = BidService();
  final _farmService = FarmService();
  late final TabController _tabController;

  List<FarmModel> _farms = [];
  String? _selectedFarmId;
  List<BidModel> _bids = [];
  bool _loading = true;
  bool _actionLoading = false;
  String? _error;

  static const _primary = Color(0xFF1B4332);
  static const _surface = Color(0xFFF4F7F5);
  static const _cardBorder = Color(0xFFE2E8F0);

  static const Map<String, Map<String, String>> _strings = {
    'am': {
      'title': 'ጨረታዎች',
      'all': 'ሁሉም',
      'pending': 'በመጠባበቅ',
      'accepted': 'ተቀብሏል',
      'completed': 'ተጠናቋል',
      'noBids': 'ምንም ጨረታ የለም',
      'accept': 'ተቀበል',
      'reject': 'አትቀበል',
      'selectFarm': 'እርሻ ይምረጡ',
      'quantity': 'መጠን',
      'price': 'ዋጋ ለኩንታል',
      'total': 'ጠቅላላ',
      'deposit': 'ቅድሚያ ክፍያ',
      'expires': 'ያልቃል',
      'accepted_on': 'ተቀብሏል',
      'error': 'ጨረታዎችን መጫን አልተቻለም',
      'tryAgain': 'እንደገና ሞክር',
      'confirmAccept': 'ይህን ጨረታ ይቀበሉ?',
      'confirmReject': 'ይህን ጨረታ ይሰርዙ?',
      'yes': 'አዎ',
      'no': 'አይ',
      'successAccept': 'ጨረታው ተቀብሏል!',
      'successReject': 'ጨረታው ተሰርዟል',
      'deposit10': '10% ዋስትና ቅድሚያ ክፍያ በኢስክሮ ተቆልፏል',
      'quintals': 'ኩንታል',
    },
    'om': {
      'title': 'Biidhawwan',
      'all': 'Hunda',
      'pending': 'Eegaa Jira',
      'accepted': 'Fudhataame',
      'completed': 'Xumuraame',
      'noBids': 'Biidhi hin jiru',
      'accept': 'Fudhad',
      'reject': 'Dida',
      'selectFarm': 'Lafa qonnaa filadhu',
      'quantity': 'Hamma',
      'price': 'Gatii Kuuntalaa',
      'total': 'Waliigala',
      'deposit': 'Mirkaneessa Duraa',
      'expires': 'Yeroon Darbaa',
      'accepted_on': 'Fudhataame',
      'error': 'Biidhawwan fe\'uu hin dandeenye',
      'tryAgain': 'Irra deebi\'i yaalii',
      'confirmAccept': 'Biidhi kana fudhachuu barbaaddaa?',
      'confirmReject': 'Biidhi kana diduu barbaaddaa?',
      'yes': 'Eeyyee',
      'no': 'Lakki',
      'successAccept': 'Biidhi fudhataame!',
      'successReject': 'Biidhi didame',
      'deposit10': '10% mirkaneessa duraa escrow keessa cufame',
      'quintals': 'Kuuntala',
    },
    'en': {
      'title': 'Bids',
      'all': 'All',
      'pending': 'Pending',
      'accepted': 'Accepted',
      'completed': 'Completed',
      'noBids': 'No bids yet',
      'accept': 'Accept',
      'reject': 'Reject',
      'selectFarm': 'Select farm',
      'quantity': 'Quantity',
      'price': 'Price / quintal',
      'total': 'Total value',
      'deposit': 'Guarantee deposit',
      'expires': 'Expires',
      'accepted_on': 'Accepted on',
      'error': 'Could not load bids',
      'tryAgain': 'Try Again',
      'confirmAccept': 'Accept this bid?',
      'confirmReject': 'Reject this bid?',
      'yes': 'Yes',
      'no': 'No',
      'successAccept': 'Bid accepted!',
      'successReject': 'Bid rejected',
      'deposit10': '10% guarantee deposit locked in escrow',
      'quintals': 'quintals',
    },
  };

  String _t(String key) {
    final code = context.read<LanguageService>().languageCode;
    return _strings[code]?[key] ?? _strings['en']![key]!;
  }

  @override
  void initState() {
    super.initState();
    _tabController = TabController(length: 4, vsync: this);
    _loadFarms();
  }

  @override
  void dispose() {
    _tabController.dispose();
    super.dispose();
  }

  Future<void> _loadFarms() async {
    setState(() { _loading = true; _error = null; });
    try {
      final result = await _farmService.getMyFarms();
      if (!mounted) return;
      if (result['success'] == true) {
        final farms = (result['farms'] as List<dynamic>).cast<FarmModel>();
        setState(() {
          _farms = farms;
          if (farms.isNotEmpty) _selectedFarmId = farms.first.id;
          _loading = false;
        });
        if (_selectedFarmId != null) await _loadBids();
      } else {
        setState(() { _error = result['message'] ?? 'Failed'; _loading = false; });
      }
    } catch (e) {
      if (mounted) setState(() { _error = e.toString(); _loading = false; });
    }
  }

  Future<void> _loadBids() async {
    if (_selectedFarmId == null) return;
    setState(() { _loading = true; _error = null; });
    try {
      final result = await _bidService.getBidsForMyFarm(_selectedFarmId!);
      if (!mounted) return;
      if (result['success'] == true) {
        setState(() {
          _bids = (result['bids'] as List<dynamic>).cast<BidModel>();
          _loading = false;
        });
      } else {
        setState(() { _error = result['message'] ?? 'Failed'; _loading = false; });
      }
    } catch (e) {
      if (mounted) setState(() { _error = e.toString(); _loading = false; });
    }
  }

  List<BidModel> _filtered(String tab) {
    if (tab == _t('pending')) return _bids.where((b) => b.isPending).toList();
    if (tab == _t('accepted')) return _bids.where((b) => b.isAccepted || b.isContractSigned).toList();
    if (tab == _t('completed')) return _bids.where((b) => b.isCompleted || b.isDefaulted || b.isExpired || b.isRejected).toList();
    return _bids;
  }

  Future<void> _confirmAction(BidModel bid, bool accept) async {
    final confirmed = await showDialog<bool>(
      context: context,
      builder: (_) => AlertDialog(
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
        title: Text(accept ? _t('confirmAccept') : _t('confirmReject'),
            style: const TextStyle(fontSize: 16, fontWeight: FontWeight.w800)),
        content: accept
            ? Text(_t('deposit10'),
                style: const TextStyle(fontSize: 13, color: Color(0xFF64748B)))
            : null,
        actions: [
          TextButton(onPressed: () => Navigator.pop(context, false), child: Text(_t('no'))),
          ElevatedButton(
            onPressed: () => Navigator.pop(context, true),
            style: ElevatedButton.styleFrom(
              backgroundColor: accept ? _primary : const Color(0xFFC62828),
              foregroundColor: Colors.white,
              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
            ),
            child: Text(_t('yes')),
          ),
        ],
      ),
    );
    if (confirmed != true) return;
    setState(() => _actionLoading = true);
    try {
      final result = accept
          ? await _bidService.acceptBid(bid.id)
          : await _bidService.rejectBid(bid.id);
      if (!mounted) return;
      if (result['success'] == true) {
        ScaffoldMessenger.of(context).showSnackBar(SnackBar(
          content: Text(accept ? _t('successAccept') : _t('successReject')),
          backgroundColor: accept ? _primary : const Color(0xFF64748B),
          behavior: SnackBarBehavior.floating,
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
        ));
        await _loadBids();
      } else {
        ScaffoldMessenger.of(context).showSnackBar(SnackBar(
          content: Text(result['message'] ?? 'Action failed'),
          backgroundColor: const Color(0xFFC62828),
          behavior: SnackBarBehavior.floating,
        ));
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(SnackBar(
          content: Text(e.toString()),
          backgroundColor: const Color(0xFFC62828),
          behavior: SnackBarBehavior.floating,
        ));
      }
    } finally {
      if (mounted) setState(() => _actionLoading = false);
    }
  }

  Color _statusColor(String status) {
    switch (status) {
      case 'PENDING': return const Color(0xFF1E40AF);
      case 'ACCEPTED':
      case 'CONTRACT_SIGNED': return _primary;
      case 'COMPLETED': return const Color(0xFF64748B);
      case 'REJECTED':
      case 'EXPIRED': return const Color(0xFFC62828);
      case 'DEFAULTED': return const Color(0xFF78350F);
      default: return const Color(0xFF64748B);
    }
  }

  String _formatDate(String? raw) {
    if (raw == null) return '-';
    try {
      final d = DateTime.parse(raw);
      const months = ['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec'];
      return '${d.day} ${months[d.month - 1]} ${d.year}';
    } catch (_) { return raw; }
  }

  @override
  Widget build(BuildContext context) {
    context.watch<LanguageService>();
    return Scaffold(
      backgroundColor: _surface,
      appBar: AppBar(
        backgroundColor: _primary,
        foregroundColor: Colors.white,
        title: Text(_t('title'),
            style: const TextStyle(fontWeight: FontWeight.w900, fontSize: 18, letterSpacing: -0.5)),
        actions: [
          IconButton(icon: const Icon(Icons.refresh_rounded), onPressed: _loadBids)
        ],
        bottom: TabBar(
          controller: _tabController,
          isScrollable: true,
          tabAlignment: TabAlignment.start,
          indicator: const UnderlineTabIndicator(
              borderSide: BorderSide(color: Colors.white, width: 2.5)),
          labelColor: Colors.white,
          unselectedLabelColor: Colors.white54,
          labelStyle: const TextStyle(fontWeight: FontWeight.w700, fontSize: 12),
          tabs: [_t('all'), _t('pending'), _t('accepted'), _t('completed')].map((t) {
            final count = t == _t('all') ? _bids.length : _filtered(t).length;
            return Tab(text: count > 0 ? '$t ($count)' : t);
          }).toList(),
        ),
      ),
      body: Column(children: [
        if (_farms.length > 1) _buildFarmSelector(),
        if (_actionLoading) const LinearProgressIndicator(color: Color(0xFF1B4332)),
        Expanded(
          child: _loading
              ? const Center(child: CircularProgressIndicator(color: Color(0xFF1B4332)))
              : _error != null
              ? _buildError()
              : TabBarView(
                  controller: _tabController,
                  children: [_t('all'), _t('pending'), _t('accepted'), _t('completed')]
                      .map((t) => _buildList(_filtered(t)))
                      .toList(),
                ),
        ),
      ]),
    );
  }

  Widget _buildFarmSelector() {
    return Container(
      color: Colors.white,
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 10),
      child: DropdownButtonFormField<String>(
        value: _selectedFarmId,
        decoration: InputDecoration(
          labelText: _t('selectFarm'),
          border: OutlineInputBorder(borderRadius: BorderRadius.circular(10)),
          contentPadding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
          isDense: true,
        ),
        items: _farms.map((f) => DropdownMenuItem(
          value: f.id,
          child: Text(f.farmName ?? f.cropType, style: const TextStyle(fontSize: 13)),
        )).toList(),
        onChanged: (v) {
          setState(() => _selectedFarmId = v);
          _loadBids();
        },
      ),
    );
  }

  Widget _buildError() {
    return Center(child: Padding(
      padding: const EdgeInsets.all(32),
      child: Column(mainAxisAlignment: MainAxisAlignment.center, children: [
        const Icon(Icons.error_outline_rounded, size: 48, color: Color(0xFFDC2626)),
        const SizedBox(height: 12),
        Text(_t('error'),
            style: const TextStyle(fontWeight: FontWeight.w800, fontSize: 15)),
        const SizedBox(height: 8),
        Text(_error!, textAlign: TextAlign.center,
            style: const TextStyle(fontSize: 12, color: Color(0xFF64748B))),
        const SizedBox(height: 20),
        ElevatedButton.icon(
          onPressed: _loadBids,
          icon: const Icon(Icons.refresh_rounded),
          label: Text(_t('tryAgain')),
          style: ElevatedButton.styleFrom(
              backgroundColor: _primary, foregroundColor: Colors.white,
              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10))),
        ),
      ]),
    ));
  }

  Widget _buildList(List<BidModel> bids) {
    if (bids.isEmpty) {
      return Center(child: Column(mainAxisAlignment: MainAxisAlignment.center, children: [
        Icon(Icons.handshake_outlined, size: 48, color: Colors.grey.shade300),
        const SizedBox(height: 8),
        Text(_t('noBids'),
            style: TextStyle(color: Colors.grey.shade500, fontSize: 14)),
      ]));
    }
    return RefreshIndicator(
      color: _primary,
      onRefresh: _loadBids,
      child: ListView.separated(
        padding: const EdgeInsets.fromLTRB(16, 12, 16, 24),
        itemCount: bids.length,
        separatorBuilder: (_, __) => const SizedBox(height: 10),
        itemBuilder: (_, i) => _buildBidCard(bids[i]),
      ),
    );
  }

  Widget _buildBidCard(BidModel bid) {
    final statusColor = _statusColor(bid.status);
    return Container(
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(20),
        border: Border.all(color: _cardBorder),
      ),
      child: ClipRRect(
        borderRadius: BorderRadius.circular(19),
        child: IntrinsicHeight(
          child: Row(crossAxisAlignment: CrossAxisAlignment.stretch, children: [
            Container(width: 4, color: statusColor),
            Expanded(child: Padding(
              padding: const EdgeInsets.all(16),
              child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
                Row(mainAxisAlignment: MainAxisAlignment.spaceBetween, children: [
                  Row(children: [
                    Container(
                      width: 38, height: 38,
                      decoration: BoxDecoration(
                          color: statusColor.withOpacity(0.08),
                          borderRadius: BorderRadius.circular(10)),
                      child: Icon(Icons.handshake_rounded, size: 20, color: statusColor),
                    ),
                    const SizedBox(width: 10),
                    Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
                      Text('${bid.quantityQuintals.toStringAsFixed(1)} ${_t('quintals')}',
                          style: const TextStyle(fontWeight: FontWeight.w800,
                              fontSize: 15, color: Color(0xFF0F291B))),
                      Text('${bid.pricePerQuintalEtb.toStringAsFixed(0)} ETB / ${_t('quintals')}',
                          style: const TextStyle(fontSize: 12, color: Color(0xFF64748B))),
                    ]),
                  ]),
                  Container(
                    padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
                    decoration: BoxDecoration(
                        color: statusColor.withOpacity(0.1),
                        borderRadius: BorderRadius.circular(20)),
                    child: Text(bid.status,
                        style: TextStyle(fontSize: 10,
                            fontWeight: FontWeight.w800, color: statusColor)),
                  ),
                ]),
                const SizedBox(height: 12),
                const Divider(height: 1, color: Color(0xFFF1F5F9)),
                const SizedBox(height: 10),
                Row(children: [
                  Expanded(child: _infoTile(_t('total'),
                      '${bid.totalValueEtb.toStringAsFixed(0)} ETB', _primary)),
                  Expanded(child: _infoTile(_t('deposit'),
                      '${bid.bidDepositEtb.toStringAsFixed(0)} ETB',
                      const Color(0xFF78350F))),
                ]),
                const SizedBox(height: 10),
                Row(children: [
                  Icon(Icons.access_time_rounded, size: 12, color: Colors.grey.shade400),
                  const SizedBox(width: 4),
                  Text(
                    bid.isAccepted && bid.acceptedAt != null
                        ? '${_t('accepted_on')} ${_formatDate(bid.acceptedAt)}'
                        : '${_t('expires')} ${_formatDate(bid.expiresAt)}',
                    style: TextStyle(fontSize: 11, color: Colors.grey.shade500),
                  ),
                  if (bid.isExpiringSoon) ...[
                    const SizedBox(width: 6),
                    Container(
                      padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                      decoration: BoxDecoration(
                          color: const Color(0xFFFFF7ED),
                          borderRadius: BorderRadius.circular(8)),
                      child: const Text('Expiring soon',
                          style: TextStyle(fontSize: 9,
                              color: Color(0xFF9A3412), fontWeight: FontWeight.bold)),
                    ),
                  ],
                ]),
                if (bid.isPending) ...[
                  const SizedBox(height: 12),
                  Row(children: [
                    Expanded(
                      child: OutlinedButton(
                        onPressed: _actionLoading ? null : () => _confirmAction(bid, false),
                        style: OutlinedButton.styleFrom(
                          foregroundColor: const Color(0xFFC62828),
                          side: const BorderSide(color: Color(0xFFC62828)),
                          shape: RoundedRectangleBorder(
                              borderRadius: BorderRadius.circular(10)),
                          padding: const EdgeInsets.symmetric(vertical: 10),
                        ),
                        child: Text(_t('reject'),
                            style: const TextStyle(fontWeight: FontWeight.w700, fontSize: 13)),
                      ),
                    ),
                    const SizedBox(width: 10),
                    Expanded(
                      child: ElevatedButton(
                        onPressed: _actionLoading ? null : () => _confirmAction(bid, true),
                        style: ElevatedButton.styleFrom(
                          backgroundColor: _primary,
                          foregroundColor: Colors.white,
                          shape: RoundedRectangleBorder(
                              borderRadius: BorderRadius.circular(10)),
                          padding: const EdgeInsets.symmetric(vertical: 10),
                        ),
                        child: Text(_t('accept'),
                            style: const TextStyle(fontWeight: FontWeight.w700, fontSize: 13)),
                      ),
                    ),
                  ]),
                ],
                if ((bid.isAccepted || bid.isContractSigned) && bid.agreementId != null) ...[
                  const SizedBox(height: 12),
                  SizedBox(
                    width: double.infinity,
                    child: ElevatedButton.icon(
                      onPressed: () => Navigator.push(
                        context,
                        MaterialPageRoute(
                          builder: (_) => AgreementScreen(bid: bid),
                        ),
                      ).then((_) => _loadBids()),
                      icon: const Icon(Icons.draw_rounded, size: 16),
                      label: const Text('View & Sign Agreement',
                          style: TextStyle(fontWeight: FontWeight.w700, fontSize: 13)),
                      style: ElevatedButton.styleFrom(
                        backgroundColor: const Color(0xFF1E40AF),
                        foregroundColor: Colors.white,
                        shape: RoundedRectangleBorder(
                            borderRadius: BorderRadius.circular(10)),
                        padding: const EdgeInsets.symmetric(vertical: 12),
                      ),
                    ),
                  ),
                ],
              ]),
            )),
          ]),
        ),
      ),
    );
  }

  Widget _infoTile(String label, String value, Color valueColor) {
    return Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
      Text(label, style: const TextStyle(fontSize: 10, color: Color(0xFF64748B))),
      const SizedBox(height: 2),
      Text(value,
          style: TextStyle(fontSize: 13, fontWeight: FontWeight.w800, color: valueColor)),
    ]);
  }
}
// agreement navigation extension — ignore this comment, added by patch

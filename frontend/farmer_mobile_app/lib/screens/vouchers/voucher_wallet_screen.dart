import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:connectivity_plus/connectivity_plus.dart';
import '../../models/voucher_model.dart';
import '../../services/voucher_service.dart';
import '../../services/language_service.dart';
import '../../widgets/voucher_status_chip.dart';
import '../../widgets/voucher_category_chip.dart';
import 'voucher_detail_screen.dart';

class VoucherWalletScreen extends StatefulWidget {
  const VoucherWalletScreen({super.key});
  @override
  State<VoucherWalletScreen> createState() => _VoucherWalletScreenState();
}

class _VoucherWalletScreenState extends State<VoucherWalletScreen>
    with SingleTickerProviderStateMixin {
  final _voucherService = VoucherService();
  late final TabController _tabController;
  List<VoucherModel> _vouchers = [];
  bool _loading = true;
  String? _errorMessage;
  bool _isOffline = false;
  DateTime? _cacheTimestamp;

  static const _primary    = Color(0xFF1B4332);
  static const _amber      = Color(0xFF78350F);
  static const _surface    = Color(0xFFF4F7F5);
  static const _cardBorder = Color(0xFFE2E8F0);

  static const Map<String, Map<String, String>> _strings = {
    'am': {
      'title':          'የቫውቸር ቦርሳ',
      'all':            'ሁሉም',
      'active':         'ንቁ',
      'redeemed':       'ተቀብሏል',
      'locked':         'ተቆልፏል',
      'expired':        'ጊዜው አልፏል',
      'offline':        'ከኢንተርኔት ተቋርጠዋል — የተቀመጡ ቫውቸሮችን እያሳዩ ነው',
      'lastSynced':     'የመጨረሻ ማመሳሰል {t} — QR ኮዶች ከኢንተርኔት ውጪ ይገኛሉ',
      'totalValue':     'ጠቅላላ ዋጋ',
      'redeemVal':      'ተቀብሏል',
      'pending':        'በመጠባበቅ ላይ',
      'progress':       'የቅናሽ እድገት',
      'sequence':       'የቅናሽ ቅደም ተከተል',
      'noVouchers':     'ምንም ቫውቸሮች የሉም',
      'couldNotLoad':   'ቫውቸሮችን መጫን አልተቻለም',
      'tryAgain':       'እንደገና ሞክር',
      'expiringSoon':   'ቀኑ እየቀረበ ነው',
      'redeemed_on':    'ተቀብሏል {d}',
      'expires':        'ጊዜ ያልፋል {d}',
    },
    'om': {
      'title':          'Hordofaa Waraqaa Ragaa',
      'all':            'Hunda',
      'active':         'Hojjechaa Jira',
      'redeemed':       'Fudhataame',
      'locked':         'Cufame',
      'expired':        'Yeroon Darbeera',
      'offline':        'Interneetii irraa citteetta — waraqaa ragaa kuufame agarsiisaa jira',
      'lastSynced':     'Walqixxeessa dhumaa {t} — koodiin QR interneetii malee argama',
      'totalValue':     'Gatii Waliigalaa',
      'redeemVal':      'Fudhataame',
      'pending':        'Eegaa Jira',
      'progress':       'Fudhatama Dabalaa',
      'sequence':       'Tartiiba Fudhatama',
      'noVouchers':     'Waraqaan ragaa hin jiru',
      'couldNotLoad':   'Waraqaa ragaa fe\'uu hin dandeenye',
      'tryAgain':       'Irra deebi\'i yaalii',
      'expiringSoon':   'Yeroon dhufaa jira',
      'redeemed_on':    'Fudhataame {d}',
      'expires':        'Yeroon darbaa {d}',
    },
    'en': {
      'title':          'Voucher Wallet',
      'all':            'All',
      'active':         'Active',
      'redeemed':       'Redeemed',
      'locked':         'Locked',
      'expired':        'Expired',
      'offline':        'Offline — showing cached vouchers',
      'lastSynced':     'Last synced {t} — QR codes available offline',
      'totalValue':     'Total Value',
      'redeemVal':      'Redeemed',
      'pending':        'Pending',
      'progress':       'Redemption Progress',
      'sequence':       'Redemption Sequence',
      'noVouchers':     'No vouchers here',
      'couldNotLoad':   'Could not load vouchers',
      'tryAgain':       'Try Again',
      'expiringSoon':   'Expiring soon',
      'redeemed_on':    'Redeemed {d}',
      'expires':        'Expires {d}',
    },
  };

  String _t(String key, {String t = '', String d = ''}) {
    final code = context.read<LanguageService>().languageCode;
    return (_strings[code]?[key] ?? _strings['en']![key]!)
        .replaceAll('{t}', t)
        .replaceAll('{d}', d);
  }

  List<String> get _tabs => [
    _t('all'), _t('active'), _t('redeemed'), _t('locked'), _t('expired'),
  ];

  @override
  void initState() {
    super.initState();
    _tabController = TabController(length: 5, vsync: this);
    _checkConnectivity();
    _load();
  }

  @override
  void dispose() { _tabController.dispose(); super.dispose(); }

  Future<void> _checkConnectivity() async {
    final result = await Connectivity().checkConnectivity();
    if (mounted) setState(() => _isOffline = result == ConnectivityResult.none);
    Connectivity().onConnectivityChanged.listen((r) {
      if (mounted) setState(() => _isOffline = r == ConnectivityResult.none);
    });
  }

  Future<void> _load() async {
    setState(() { _loading = true; _errorMessage = null; });
    try {
      final vouchers = await _voucherService.getMyVouchers();
      final ts = await _voucherService.getCacheTimestamp();
      if (mounted) setState(() {
        _vouchers = vouchers..sort((a, b) => a.sequenceOrder.compareTo(b.sequenceOrder));
        _cacheTimestamp = ts;
        _loading = false;
      });
    } catch (e) {
      if (mounted) setState(() { _errorMessage = e.toString(); _loading = false; });
    }
  }

  List<VoucherModel> _filtered(String tab) {
    if (tab == _t('active'))   return _vouchers.where((v) => v.isActive).toList();
    if (tab == _t('redeemed')) return _vouchers.where((v) => v.isRedeemed).toList();
    if (tab == _t('locked'))   return _vouchers.where((v) => v.isLocked).toList();
    if (tab == _t('expired'))  return _vouchers.where((v) => v.isExpired || v.isCancelled).toList();
    return _vouchers;
  }

  VoucherSummaryModel get _summary => VoucherSummaryModel.fromList(_vouchers);

  IconData _categoryIcon(String category) {
    switch (category) {
      case 'SEED': return Icons.grass_rounded;
      case 'FERTILIZER': return Icons.science_rounded;
      case 'PESTICIDE': return Icons.bug_report_rounded;
      case 'TOOL': return Icons.handyman_rounded;
      default: return Icons.inventory_2_rounded;
    }
  }

  @override
  Widget build(BuildContext context) {
    context.watch<LanguageService>();
    final tabs = _tabs;
    return Scaffold(
      backgroundColor: _surface,
      appBar: AppBar(
        backgroundColor: _primary, foregroundColor: Colors.white,
        title: Text(_t('title'), style: const TextStyle(fontWeight: FontWeight.w900, fontSize: 18, letterSpacing: -0.5)),
        actions: [IconButton(icon: const Icon(Icons.refresh_rounded), onPressed: _load, tooltip: 'Refresh')],
        bottom: TabBar(
          controller: _tabController,
          isScrollable: true, tabAlignment: TabAlignment.start,
          indicator: const UnderlineTabIndicator(borderSide: BorderSide(color: Colors.white, width: 2.5)),
          labelColor: Colors.white, unselectedLabelColor: Colors.white54,
          labelStyle: const TextStyle(fontWeight: FontWeight.w700, fontSize: 12),
          tabs: tabs.map((t) {
            final count = t == _t('all') ? _vouchers.length : _filtered(t).length;
            return Tab(text: count > 0 ? '$t ($count)' : t);
          }).toList(),
        ),
      ),
      body: _loading
          ? const Center(child: CircularProgressIndicator(color: Color(0xFF1B4332)))
          : _errorMessage != null
          ? _buildErrorState()
          : Column(children: [
        if (_isOffline) _buildOfflineBanner(),
        if (_cacheTimestamp != null && _isOffline) _buildCacheNote(),
        _buildSummaryHeader(),
        _buildSequenceBar(),
        Expanded(child: TabBarView(
          controller: _tabController,
          children: tabs.map((t) => _buildVoucherList(_filtered(t))).toList(),
        )),
      ]),
    );
  }

  Widget _buildErrorState() {
    return Center(child: Padding(padding: const EdgeInsets.all(32), child: Column(mainAxisAlignment: MainAxisAlignment.center, children: [
      const Icon(Icons.error_outline_rounded, size: 56, color: Color(0xFFDC2626)),
      const SizedBox(height: 16),
      Text(_t('couldNotLoad'), style: const TextStyle(fontSize: 16, fontWeight: FontWeight.w800, color: Color(0xFF0F291B))),
      const SizedBox(height: 8),
      Text(_errorMessage!, textAlign: TextAlign.center, style: const TextStyle(fontSize: 12, color: Color(0xFF64748B))),
      const SizedBox(height: 24),
      ElevatedButton.icon(onPressed: _load, icon: const Icon(Icons.refresh_rounded), label: Text(_t('tryAgain')),
          style: ElevatedButton.styleFrom(backgroundColor: const Color(0xFF1B4332), foregroundColor: Colors.white,
              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)))),
    ])));
  }

  Widget _buildOfflineBanner() {
    return Container(width: double.infinity, color: const Color(0xFFC62828),
        padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
        child: Row(mainAxisAlignment: MainAxisAlignment.center, children: [
          const Icon(Icons.wifi_off, color: Colors.white, size: 14), const SizedBox(width: 6),
          Text(_t('offline'), style: const TextStyle(color: Colors.white, fontSize: 12, fontWeight: FontWeight.w600)),
        ]));
  }

  Widget _buildCacheNote() {
    final ts = _cacheTimestamp!;
    final diff = DateTime.now().difference(ts);
    final label = diff.inMinutes < 60 ? '${diff.inMinutes}m ago' : '${diff.inHours}h ago';
    return Container(color: const Color(0xFFFFF8F0), padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 6),
        child: Row(children: [
          const Icon(Icons.access_time, size: 12, color: Color(0xFF78350F)), const SizedBox(width: 4),
          Text(_t('lastSynced', t: label), style: const TextStyle(fontSize: 11, color: Color(0xFF78350F))),
        ]));
  }

  Widget _buildSummaryHeader() {
    final s = _summary;
    return Container(margin: const EdgeInsets.all(16), padding: const EdgeInsets.all(18),
        decoration: BoxDecoration(
            gradient: const LinearGradient(colors: [Color(0xFF1B4332), Color(0xFF2D6A4F)], begin: Alignment.topLeft, end: Alignment.bottomRight),
            borderRadius: BorderRadius.circular(20)),
        child: Column(children: [
          Row(mainAxisAlignment: MainAxisAlignment.spaceBetween, children: [
            _summaryTile(_t('totalValue'), '${s.totalValueEtb.toStringAsFixed(0)} ETB', Colors.white, Colors.white70),
            _summaryTile(_t('redeemVal'), '${s.redeemedValueEtb.toStringAsFixed(0)} ETB', const Color(0xFF86EFAC), Colors.white60),
            _summaryTile(_t('pending'), '${s.pendingValueEtb.toStringAsFixed(0)} ETB', const Color(0xFFFCD34D), Colors.white60),
          ]),
          const SizedBox(height: 14),
          Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
            Row(mainAxisAlignment: MainAxisAlignment.spaceBetween, children: [
              Text(_t('progress'), style: const TextStyle(color: Colors.white70, fontSize: 11)),
              Text('${s.redemptionPct}%', style: const TextStyle(color: Colors.white, fontSize: 11, fontWeight: FontWeight.bold)),
            ]),
            const SizedBox(height: 6),
            ClipRRect(borderRadius: BorderRadius.circular(4),
                child: LinearProgressIndicator(value: s.redemptionPct / 100, backgroundColor: Colors.white24,
                    valueColor: const AlwaysStoppedAnimation<Color>(Color(0xFF86EFAC)), minHeight: 6)),
          ]),
        ]));
  }

  Widget _summaryTile(String label, String value, Color valueColor, Color labelColor) {
    return Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
      Text(label, style: TextStyle(color: labelColor, fontSize: 10)),
      const SizedBox(height: 2),
      Text(value, style: TextStyle(color: valueColor, fontSize: 14, fontWeight: FontWeight.w900)),
    ]);
  }

  Widget _buildSequenceBar() {
    if (_vouchers.isEmpty) return const SizedBox.shrink();
    return Container(margin: const EdgeInsets.only(left: 16, right: 16, bottom: 8), padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 12),
        decoration: BoxDecoration(color: Colors.white, borderRadius: BorderRadius.circular(16), border: Border.all(color: _cardBorder)),
        child: Column(crossAxisAlignment: CrossAxisAlignment.start, mainAxisSize: MainAxisSize.min, children: [
          Text(_t('sequence'), style: const TextStyle(fontSize: 10, fontWeight: FontWeight.w800, color: Color(0xFF64748B), letterSpacing: 0.5)),
          const SizedBox(height: 10),
          SizedBox(height: 52, child: ListView.separated(
            scrollDirection: Axis.horizontal, itemCount: _vouchers.length,
            separatorBuilder: (_, __) => Center(child: Container(width: 16, height: 1.5, color: const Color(0xFFE2E8F0))),
            itemBuilder: (_, i) {
              final v = _vouchers[i];
              final dotColor = v.isRedeemed ? _primary : v.isActive ? _amber : const Color(0xFFCBD5E1);
              return Column(mainAxisAlignment: MainAxisAlignment.center, mainAxisSize: MainAxisSize.min, children: [
                Container(width: 26, height: 26, decoration: BoxDecoration(color: dotColor, shape: BoxShape.circle),
                    child: Center(child: v.isRedeemed
                        ? const Icon(Icons.check, size: 14, color: Colors.white)
                        : v.isLocked
                        ? const Icon(Icons.lock, size: 12, color: Colors.white)
                        : Text('${v.sequenceOrder}', style: const TextStyle(color: Colors.white, fontSize: 10, fontWeight: FontWeight.bold)))),
                const SizedBox(height: 4),
                Icon(_categoryIcon(v.productCategory), size: 12, color: const Color(0xFF64748B)),
              ]);
            },
          )),
        ]));
  }

  Widget _buildVoucherList(List<VoucherModel> vouchers) {
    if (vouchers.isEmpty) return Center(child: Column(mainAxisAlignment: MainAxisAlignment.center, children: [
      Icon(Icons.receipt_long_rounded, size: 48, color: Colors.grey.shade300),
      const SizedBox(height: 8),
      Text(_t('noVouchers'), style: TextStyle(color: Colors.grey.shade500, fontSize: 14)),
    ]));
    return RefreshIndicator(color: _primary, onRefresh: _load,
        child: ListView.separated(
          padding: const EdgeInsets.fromLTRB(16, 4, 16, 24), itemCount: vouchers.length,
          separatorBuilder: (_, __) => const SizedBox(height: 10),
          itemBuilder: (_, i) => _buildVoucherCard(vouchers[i]),
        ));
  }

  Widget _buildVoucherCard(VoucherModel v) {
    final accentColor = v.isRedeemed ? const Color(0xFF64748B) : v.isActive ? _primary
        : v.isExpired ? const Color(0xFFC62828) : const Color(0xFF1E40AF);
    return GestureDetector(
      onTap: () => Navigator.push(context, MaterialPageRoute(builder: (_) => VoucherDetailScreen(voucher: v))),
      child: Container(
        decoration: BoxDecoration(color: Colors.white, borderRadius: BorderRadius.circular(20), border: Border.all(color: _cardBorder),
            boxShadow: [BoxShadow(color: const Color(0xFF0F291B).withOpacity(0.03), blurRadius: 8, offset: const Offset(0, 2))]),
        child: ClipRRect(borderRadius: BorderRadius.circular(19),
            child: IntrinsicHeight(child: Row(crossAxisAlignment: CrossAxisAlignment.stretch, children: [
              Container(width: 4, color: accentColor),
              Expanded(child: Padding(padding: const EdgeInsets.all(16), child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
                Row(children: [
                  Container(width: 40, height: 40,
                      decoration: BoxDecoration(color: accentColor.withOpacity(0.08), borderRadius: BorderRadius.circular(10)),
                      child: Icon(_categoryIcon(v.productCategory), size: 22, color: accentColor)),
                  const SizedBox(width: 10),
                  Expanded(child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
                    Text(v.productDescription, style: const TextStyle(fontWeight: FontWeight.w800, fontSize: 14, color: Color(0xFF0F291B), letterSpacing: -0.3),
                        maxLines: 1, overflow: TextOverflow.ellipsis),
                    const SizedBox(height: 3),
                    Row(children: [
                      VoucherCategoryChip(category: v.productCategory), const SizedBox(width: 6),
                      Text('#${v.sequenceOrder}', style: TextStyle(fontSize: 10, color: Colors.grey.shade400, fontWeight: FontWeight.bold)),
                    ]),
                  ])),
                  Column(crossAxisAlignment: CrossAxisAlignment.end, children: [
                    VoucherStatusChip(status: v.status), const SizedBox(height: 4),
                    Text('${v.amountEtb.toStringAsFixed(0)} ETB',
                        style: TextStyle(fontWeight: FontWeight.w900, fontSize: 15, color: accentColor, fontFamily: 'monospace')),
                  ]),
                ]),
                const SizedBox(height: 12),
                const Divider(height: 1, color: Color(0xFFF1F5F9)),
                const SizedBox(height: 10),
                Row(mainAxisAlignment: MainAxisAlignment.spaceBetween, children: [
                  Text(v.alphanumericCode, style: const TextStyle(fontFamily: 'monospace', fontSize: 12, letterSpacing: 1, color: Color(0xFF64748B), fontWeight: FontWeight.w600)),
                  Row(children: [
                    if (v.isExpiringSoon) Container(margin: const EdgeInsets.only(right: 8),
                        padding: const EdgeInsets.symmetric(horizontal: 7, vertical: 2),
                        decoration: BoxDecoration(color: const Color(0xFFFFF7ED), borderRadius: BorderRadius.circular(10)),
                        child: Text(_t('expiringSoon'), style: const TextStyle(fontSize: 9, color: Color(0xFF9A3412), fontWeight: FontWeight.bold))),
                    Icon(Icons.chevron_right_rounded, size: 18, color: Colors.grey.shade400),
                  ]),
                ]),
                const SizedBox(height: 4),
                Text(
                  v.isRedeemed && v.redeemedAt != null
                      ? _t('redeemed_on', d: _formatDate(v.redeemedAt!))
                      : _t('expires', d: _formatDate(v.validUntil)),
                  style: TextStyle(fontSize: 10, color: Colors.grey.shade400),
                ),
              ]))),
            ]))),
      ),
    );
  }

  String _formatDate(String raw) {
    try {
      final d = DateTime.parse(raw);
      const months = ['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec'];
      return '${d.day} ${months[d.month - 1]} ${d.year}';
    } catch (_) { return raw; }
  }
}


// import 'package:flutter/material.dart';
// import 'package:connectivity_plus/connectivity_plus.dart';
// import '../../models/voucher_model.dart';
// import '../../services/voucher_service.dart';
// import '../../widgets/voucher_status_chip.dart';
// import '../../widgets/voucher_category_chip.dart';
// import 'voucher_detail_screen.dart';
//
// class VoucherWalletScreen extends StatefulWidget {
//   const VoucherWalletScreen({super.key});
//
//   @override
//   State<VoucherWalletScreen> createState() => _VoucherWalletScreenState();
// }
//
// class _VoucherWalletScreenState extends State<VoucherWalletScreen>
//     with SingleTickerProviderStateMixin {
//   final _voucherService = VoucherService();
//   late final TabController _tabController;
//
//   List<VoucherModel> _vouchers = [];
//   bool _loading = true;
//   // FIXED: track the real error message so the screen shows it
//   // instead of silently displaying "No vouchers here".
//   String? _errorMessage;
//   bool _isOffline = false;
//   DateTime? _cacheTimestamp;
//
//   static const _primary    = Color(0xFF1B4332);
//   static const _amber      = Color(0xFF78350F);
//   static const _surface    = Color(0xFFF4F7F5);
//   static const _cardBorder = Color(0xFFE2E8F0);
//
//   final _tabs = const ['All', 'Active', 'Redeemed', 'Locked', 'Expired'];
//
//   @override
//   void initState() {
//     super.initState();
//     _tabController = TabController(length: _tabs.length, vsync: this);
//     _checkConnectivity();
//     _load();
//   }
//
//   @override
//   void dispose() {
//     _tabController.dispose();
//     super.dispose();
//   }
//
//   Future<void> _checkConnectivity() async {
//     final result = await Connectivity().checkConnectivity();
//     if (mounted) setState(() => _isOffline = result == ConnectivityResult.none);
//     Connectivity().onConnectivityChanged.listen((r) {
//       if (mounted) setState(() => _isOffline = r == ConnectivityResult.none);
//     });
//   }
//
//   Future<void> _load() async {
//     setState(() {
//       _loading = true;
//       _errorMessage = null;   // clear previous error on refresh
//     });
//
//     try {
//       final vouchers = await _voucherService.getMyVouchers();
//       final ts       = await _voucherService.getCacheTimestamp();
//       if (mounted) {
//         setState(() {
//           _vouchers = vouchers
//             ..sort((a, b) => a.sequenceOrder.compareTo(b.sequenceOrder));
//           _cacheTimestamp = ts;
//           _loading        = false;
//         });
//       }
//     } catch (e) {
//       // FIXED: surface the real error message in the UI.
//       if (mounted) {
//         setState(() {
//           _errorMessage = e.toString();
//           _loading      = false;
//         });
//       }
//     }
//   }
//
//   List<VoucherModel> _filtered(String tab) {
//     switch (tab) {
//       case 'Active':   return _vouchers.where((v) => v.isActive).toList();
//       case 'Redeemed': return _vouchers.where((v) => v.isRedeemed).toList();
//       case 'Locked':   return _vouchers.where((v) => v.isLocked).toList();
//       case 'Expired':
//         return _vouchers.where((v) => v.isExpired || v.isCancelled).toList();
//       default:         return _vouchers;
//     }
//   }
//
//   VoucherSummaryModel get _summary => VoucherSummaryModel.fromList(_vouchers);
//
//   IconData _categoryIcon(String category) {
//     switch (category) {
//       case 'SEED':       return Icons.grass_rounded;
//       case 'FERTILIZER': return Icons.science_rounded;
//       case 'PESTICIDE':  return Icons.bug_report_rounded;
//       case 'TOOL':       return Icons.handyman_rounded;
//       default:           return Icons.inventory_2_rounded;
//     }
//   }
//
//   @override
//   Widget build(BuildContext context) {
//     return Scaffold(
//       backgroundColor: _surface,
//       appBar: AppBar(
//         backgroundColor: _primary,
//         foregroundColor: Colors.white,
//         title: const Text('Voucher Wallet',
//             style: TextStyle(
//                 fontWeight: FontWeight.w900,
//                 fontSize: 18,
//                 letterSpacing: -0.5)),
//         actions: [
//           IconButton(
//             icon: const Icon(Icons.refresh_rounded),
//             onPressed: _load,
//             tooltip: 'Refresh',
//           ),
//         ],
//         bottom: TabBar(
//           controller: _tabController,
//           isScrollable: true,
//           tabAlignment: TabAlignment.start,
//           indicator: const UnderlineTabIndicator(
//             borderSide: BorderSide(color: Colors.white, width: 2.5),
//           ),
//           labelColor: Colors.white,
//           unselectedLabelColor: Colors.white54,
//           labelStyle:
//           const TextStyle(fontWeight: FontWeight.w700, fontSize: 12),
//           tabs: _tabs.map((t) {
//             final count =
//             t == 'All' ? _vouchers.length : _filtered(t).length;
//             return Tab(text: count > 0 ? '$t ($count)' : t);
//           }).toList(),
//         ),
//       ),
//       body: _loading
//           ? const Center(
//           child: CircularProgressIndicator(color: Color(0xFF1B4332)))
//       // FIXED: show the real error instead of an empty list.
//           : _errorMessage != null
//           ? _buildErrorState()
//           : Column(
//         children: [
//           if (_isOffline) _buildOfflineBanner(),
//           if (_cacheTimestamp != null && _isOffline) _buildCacheNote(),
//           _buildSummaryHeader(),
//           _buildSequenceBar(),
//           Expanded(
//             child: TabBarView(
//               controller: _tabController,
//               children: _tabs
//                   .map((t) => _buildVoucherList(_filtered(t)))
//                   .toList(),
//             ),
//           ),
//         ],
//       ),
//     );
//   }
//
//   // ── Error state — shows the actual exception so you know what failed ─────
//   Widget _buildErrorState() {
//     return Center(
//       child: Padding(
//         padding: const EdgeInsets.all(32),
//         child: Column(
//           mainAxisAlignment: MainAxisAlignment.center,
//           children: [
//             const Icon(Icons.error_outline_rounded,
//                 size: 56, color: Color(0xFFDC2626)),
//             const SizedBox(height: 16),
//             const Text('Could not load vouchers',
//                 style: TextStyle(
//                     fontSize: 16,
//                     fontWeight: FontWeight.w800,
//                     color: Color(0xFF0F291B))),
//             const SizedBox(height: 8),
//             // Shows the real error — check Flutter debug console for full stack.
//             Text(
//               _errorMessage!,
//               textAlign: TextAlign.center,
//               style: const TextStyle(fontSize: 12, color: Color(0xFF64748B)),
//             ),
//             const SizedBox(height: 24),
//             ElevatedButton.icon(
//               onPressed: _load,
//               icon: const Icon(Icons.refresh_rounded),
//               label: const Text('Try Again'),
//               style: ElevatedButton.styleFrom(
//                 backgroundColor: const Color(0xFF1B4332),
//                 foregroundColor: Colors.white,
//                 shape: RoundedRectangleBorder(
//                     borderRadius: BorderRadius.circular(12)),
//               ),
//             ),
//           ],
//         ),
//       ),
//     );
//   }
//
//   // ── Offline banners ──────────────────────────────────────────────────────
//
//   Widget _buildOfflineBanner() {
//     return Container(
//       width: double.infinity,
//       color: const Color(0xFFC62828),
//       padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
//       child: const Row(
//         mainAxisAlignment: MainAxisAlignment.center,
//         children: [
//           Icon(Icons.wifi_off, color: Colors.white, size: 14),
//           SizedBox(width: 6),
//           Text('Offline — showing cached vouchers',
//               style: TextStyle(
//                   color: Colors.white,
//                   fontSize: 12,
//                   fontWeight: FontWeight.w600)),
//         ],
//       ),
//     );
//   }
//
//   Widget _buildCacheNote() {
//     final ts   = _cacheTimestamp!;
//     final diff = DateTime.now().difference(ts);
//     final label = diff.inMinutes < 60
//         ? '${diff.inMinutes}m ago'
//         : '${diff.inHours}h ago';
//     return Container(
//       color: const Color(0xFFFFF8F0),
//       padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 6),
//       child: Row(
//         children: [
//           const Icon(Icons.access_time, size: 12, color: Color(0xFF78350F)),
//           const SizedBox(width: 4),
//           Text('Last synced $label — QR codes available offline',
//               style: const TextStyle(
//                   fontSize: 11, color: Color(0xFF78350F))),
//         ],
//       ),
//     );
//   }
//
//   // ── Summary header ───────────────────────────────────────────────────────
//
//   Widget _buildSummaryHeader() {
//     final s = _summary;
//     return Container(
//       margin: const EdgeInsets.all(16),
//       padding: const EdgeInsets.all(18),
//       decoration: BoxDecoration(
//         gradient: const LinearGradient(
//           colors: [Color(0xFF1B4332), Color(0xFF2D6A4F)],
//           begin: Alignment.topLeft,
//           end: Alignment.bottomRight,
//         ),
//         borderRadius: BorderRadius.circular(20),
//       ),
//       child: Column(
//         children: [
//           Row(
//             mainAxisAlignment: MainAxisAlignment.spaceBetween,
//             children: [
//               _summaryTile('Total Value',
//                   '${s.totalValueEtb.toStringAsFixed(0)} ETB',
//                   Colors.white, Colors.white70),
//               _summaryTile('Redeemed',
//                   '${s.redeemedValueEtb.toStringAsFixed(0)} ETB',
//                   const Color(0xFF86EFAC), Colors.white60),
//               _summaryTile('Pending',
//                   '${s.pendingValueEtb.toStringAsFixed(0)} ETB',
//                   const Color(0xFFFCD34D), Colors.white60),
//             ],
//           ),
//           const SizedBox(height: 14),
//           Column(
//             crossAxisAlignment: CrossAxisAlignment.start,
//             children: [
//               Row(
//                 mainAxisAlignment: MainAxisAlignment.spaceBetween,
//                 children: [
//                   const Text('Redemption Progress',
//                       style: TextStyle(
//                           color: Colors.white70, fontSize: 11)),
//                   Text('${s.redemptionPct}%',
//                       style: const TextStyle(
//                           color: Colors.white,
//                           fontSize: 11,
//                           fontWeight: FontWeight.bold)),
//                 ],
//               ),
//               const SizedBox(height: 6),
//               ClipRRect(
//                 borderRadius: BorderRadius.circular(4),
//                 child: LinearProgressIndicator(
//                   value: s.redemptionPct / 100,
//                   backgroundColor: Colors.white24,
//                   valueColor: const AlwaysStoppedAnimation<Color>(
//                       Color(0xFF86EFAC)),
//                   minHeight: 6,
//                 ),
//               ),
//             ],
//           ),
//         ],
//       ),
//     );
//   }
//
//   Widget _summaryTile(
//       String label, String value, Color valueColor, Color labelColor) {
//     return Column(
//       crossAxisAlignment: CrossAxisAlignment.start,
//       children: [
//         Text(label, style: TextStyle(color: labelColor, fontSize: 10)),
//         const SizedBox(height: 2),
//         Text(value,
//             style: TextStyle(
//                 color: valueColor,
//                 fontSize: 14,
//                 fontWeight: FontWeight.w900)),
//       ],
//     );
//   }
//
//   // ── Sequence bar ─────────────────────────────────────────────────────────
//
//   Widget _buildSequenceBar() {
//     if (_vouchers.isEmpty) return const SizedBox.shrink();
//     return Container(
//       margin: const EdgeInsets.only(left: 16, right: 16, bottom: 8),
//       padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 12),
//       decoration: BoxDecoration(
//         color: Colors.white,
//         borderRadius: BorderRadius.circular(16),
//         border: Border.all(color: _cardBorder),
//       ),
//       child: Column(
//         crossAxisAlignment: CrossAxisAlignment.start,
//         mainAxisSize: MainAxisSize.min,
//         children: [
//           const Text('Redemption Sequence',
//               style: TextStyle(
//                   fontSize: 10,
//                   fontWeight: FontWeight.w800,
//                   color: Color(0xFF64748B),
//                   letterSpacing: 0.5)),
//           const SizedBox(height: 10),
//           SizedBox(
//             height: 52,
//             child: ListView.separated(
//               scrollDirection: Axis.horizontal,
//               itemCount: _vouchers.length,
//               separatorBuilder: (_, __) => Center(
//                 child: Container(
//                     width: 16, height: 1.5, color: const Color(0xFFE2E8F0)),
//               ),
//               itemBuilder: (_, i) {
//                 final v = _vouchers[i];
//                 final dotColor = v.isRedeemed
//                     ? _primary
//                     : v.isActive
//                     ? _amber
//                     : const Color(0xFFCBD5E1);
//                 return Column(
//                   mainAxisAlignment: MainAxisAlignment.center,
//                   mainAxisSize: MainAxisSize.min,
//                   children: [
//                     Container(
//                       width: 26,
//                       height: 26,
//                       decoration: BoxDecoration(
//                           color: dotColor, shape: BoxShape.circle),
//                       child: Center(
//                         child: v.isRedeemed
//                             ? const Icon(Icons.check,
//                             size: 14, color: Colors.white)
//                             : v.isLocked
//                             ? const Icon(Icons.lock,
//                             size: 12, color: Colors.white)
//                             : Text('${v.sequenceOrder}',
//                             style: const TextStyle(
//                                 color: Colors.white,
//                                 fontSize: 10,
//                                 fontWeight: FontWeight.bold)),
//                       ),
//                     ),
//                     const SizedBox(height: 4),
//                     Icon(
//                       _categoryIcon(v.productCategory),
//                       size: 12,
//                       color: const Color(0xFF64748B),
//                     ),
//                   ],
//                 );
//               },
//             ),
//           ),
//         ],
//       ),
//     );
//   }
//
//   // ── Voucher list ─────────────────────────────────────────────────────────
//
//   Widget _buildVoucherList(List<VoucherModel> vouchers) {
//     if (vouchers.isEmpty) {
//       return Center(
//         child: Column(
//           mainAxisAlignment: MainAxisAlignment.center,
//           children: [
//             Icon(Icons.receipt_long_rounded,
//                 size: 48, color: Colors.grey.shade300),
//             const SizedBox(height: 8),
//             Text('No vouchers here',
//                 style: TextStyle(
//                     color: Colors.grey.shade500, fontSize: 14)),
//           ],
//         ),
//       );
//     }
//
//     return RefreshIndicator(
//       color: _primary,
//       onRefresh: _load,
//       child: ListView.separated(
//         padding: const EdgeInsets.fromLTRB(16, 4, 16, 24),
//         itemCount: vouchers.length,
//         separatorBuilder: (_, __) => const SizedBox(height: 10),
//         itemBuilder: (_, i) => _buildVoucherCard(vouchers[i]),
//       ),
//     );
//   }
//
//   // ── Voucher card ─────────────────────────────────────────────────────────
//
//   Widget _buildVoucherCard(VoucherModel v) {
//     final accentColor = v.isRedeemed
//         ? const Color(0xFF64748B)
//         : v.isActive
//         ? _primary
//         : v.isExpired
//         ? const Color(0xFFC62828)
//         : const Color(0xFF1E40AF);
//
//     return GestureDetector(
//       onTap: () => Navigator.push(
//         context,
//         MaterialPageRoute(builder: (_) => VoucherDetailScreen(voucher: v)),
//       ),
//       child: Container(
//         decoration: BoxDecoration(
//           color: Colors.white,
//           borderRadius: BorderRadius.circular(20),
//           border: Border.all(color: _cardBorder),
//           boxShadow: [
//             BoxShadow(
//               color: const Color(0xFF0F291B).withOpacity(0.03),
//               blurRadius: 8,
//               offset: const Offset(0, 2),
//             ),
//           ],
//         ),
//         child: ClipRRect(
//           borderRadius: BorderRadius.circular(19),
//           child: IntrinsicHeight(
//             child: Row(
//               crossAxisAlignment: CrossAxisAlignment.stretch,
//               children: [
//                 Container(width: 4, color: accentColor),
//                 Expanded(
//                   child: Padding(
//                     padding: const EdgeInsets.all(16),
//                     child: Column(
//                       crossAxisAlignment: CrossAxisAlignment.start,
//                       children: [
//                         Row(
//                           children: [
//                             Container(
//                               width: 40,
//                               height: 40,
//                               decoration: BoxDecoration(
//                                 color: accentColor.withOpacity(0.08),
//                                 borderRadius: BorderRadius.circular(10),
//                               ),
//                               child: Icon(
//                                 _categoryIcon(v.productCategory),
//                                 size: 22,
//                                 color: accentColor,
//                               ),
//                             ),
//                             const SizedBox(width: 10),
//                             Expanded(
//                               child: Column(
//                                 crossAxisAlignment: CrossAxisAlignment.start,
//                                 children: [
//                                   Text(v.productDescription,
//                                       style: const TextStyle(
//                                           fontWeight: FontWeight.w800,
//                                           fontSize: 14,
//                                           color: Color(0xFF0F291B),
//                                           letterSpacing: -0.3),
//                                       maxLines: 1,
//                                       overflow: TextOverflow.ellipsis),
//                                   const SizedBox(height: 3),
//                                   Row(
//                                     children: [
//                                       VoucherCategoryChip(
//                                           category: v.productCategory),
//                                       const SizedBox(width: 6),
//                                       Text('#${v.sequenceOrder}',
//                                           style: TextStyle(
//                                               fontSize: 10,
//                                               color: Colors.grey.shade400,
//                                               fontWeight: FontWeight.bold)),
//                                     ],
//                                   ),
//                                 ],
//                               ),
//                             ),
//                             Column(
//                               crossAxisAlignment: CrossAxisAlignment.end,
//                               children: [
//                                 VoucherStatusChip(status: v.status),
//                                 const SizedBox(height: 4),
//                                 Text(
//                                   '${v.amountEtb.toStringAsFixed(0)} ETB',
//                                   style: TextStyle(
//                                       fontWeight: FontWeight.w900,
//                                       fontSize: 15,
//                                       color: accentColor,
//                                       fontFamily: 'monospace'),
//                                 ),
//                               ],
//                             ),
//                           ],
//                         ),
//
//                         const SizedBox(height: 12),
//                         const Divider(height: 1, color: Color(0xFFF1F5F9)),
//                         const SizedBox(height: 10),
//
//                         Row(
//                           mainAxisAlignment: MainAxisAlignment.spaceBetween,
//                           children: [
//                             Text(
//                               v.alphanumericCode,
//                               style: const TextStyle(
//                                   fontFamily: 'monospace',
//                                   fontSize: 12,
//                                   letterSpacing: 1,
//                                   color: Color(0xFF64748B),
//                                   fontWeight: FontWeight.w600),
//                             ),
//                             Row(
//                               children: [
//                                 if (v.isExpiringSoon)
//                                   Container(
//                                     margin: const EdgeInsets.only(right: 8),
//                                     padding: const EdgeInsets.symmetric(
//                                         horizontal: 7, vertical: 2),
//                                     decoration: BoxDecoration(
//                                       color: const Color(0xFFFFF7ED),
//                                       borderRadius:
//                                       BorderRadius.circular(10),
//                                     ),
//                                     child: const Text('Expiring soon',
//                                         style: TextStyle(
//                                             fontSize: 9,
//                                             color: Color(0xFF9A3412),
//                                             fontWeight: FontWeight.bold)),
//                                   ),
//                                 Icon(Icons.chevron_right_rounded,
//                                     size: 18,
//                                     color: Colors.grey.shade400),
//                               ],
//                             ),
//                           ],
//                         ),
//
//                         const SizedBox(height: 4),
//                         Text(
//                           v.isRedeemed && v.redeemedAt != null
//                               ? 'Redeemed ${_formatDate(v.redeemedAt!)}'
//                               : 'Expires ${_formatDate(v.validUntil)}',
//                           style: TextStyle(
//                               fontSize: 10, color: Colors.grey.shade400),
//                         ),
//                       ],
//                     ),
//                   ),
//                 ),
//               ],
//             ),
//           ),
//         ),
//       ),
//     );
//   }
//
//   String _formatDate(String raw) {
//     try {
//       final d = DateTime.parse(raw);
//       return '${d.day} ${_months[d.month - 1]} ${d.year}';
//     } catch (_) {
//       return raw;
//     }
//   }
//
//   static const _months = [
//     'Jan','Feb','Mar','Apr','May','Jun',
//     'Jul','Aug','Sep','Oct','Nov','Dec',
//   ];
// }
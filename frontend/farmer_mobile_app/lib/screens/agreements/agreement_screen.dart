import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../models/agreement_model.dart';
import '../../models/dispatch_model.dart';
import '../../models/bid_model.dart';
import '../../services/agreement_service.dart';
import '../../services/language_service.dart';

class AgreementScreen extends StatefulWidget {
  final BidModel bid;
  const AgreementScreen({super.key, required this.bid});

  @override
  State<AgreementScreen> createState() => _AgreementScreenState();
}

class _AgreementScreenState extends State<AgreementScreen> {
  final _agreementService = AgreementService();

  AgreementModel? _agreement;
  List<DispatchModel> _dispatches = [];
  bool _loading = true;
  bool _signing = false;
  bool _actionLoading = false;
  String? _error;
  bool _confirmed = false;

  static const _primary = Color(0xFF1B4332);
  static const _surface = Color(0xFFF4F7F5);
  static const _cardBorder = Color(0xFFE2E8F0);

  static const Map<String, Map<String, String>> _strings = {
    'am': {
      'title': 'የግዢ ስምምነት',
      'bidDetails': 'የጨረታ ዝርዝሮች',
      'quantity': 'መጠን',
      'price': 'ዋጋ ለኩንታል',
      'total': 'ጠቅላላ',
      'deposit': 'ቅድሚያ ክፍያ',
      'contractStatus': 'የውል ሁኔታ',
      'farmerSig': 'የገበሬ ፊርማ',
      'offtakerSig': 'የገዢ ፊርማ',
      'signed': 'ተፈርሟል',
      'awaiting': 'በመጠባበቅ ላይ',
      'fullyExecuted': 'ውሉ ሙሉ በሙሉ ተፈጻሚ ሆኗል',
      'bothSigned': 'ሁለቱም ወገኖች ፈርመዋል። መኪናዎች ሲላኩ ያሳውቅዎታል።',
      'confirmCheck': 'ስምምነቱን ገምግሜ ተስማምቻለሁ',
      'sign': 'ፊርማ ፈርም',
      'signing': 'በፊርማ ላይ...',
      'waitOfftaker': 'ፈርመዋል — ገዢው ፊርማ እየጠበቀ ነው',
      'dispatches': 'የትራንስፖርት ሁኔታ',
      'noDispatches': 'ምንም ትራንስፖርት አልተያዘም',
      'trucks': 'መኪናዎች',
      'driver': 'ሹፌር ፋይዳ ID',
      'scheduledDate': 'የቀጠሮ ቀን',
      'actualDate': 'ትክክለኛ ቀን',
      'confirmArrival': 'መምጣትን አረጋግጥ',
      'confirmLoading': 'መጫንን አረጋግጥ',
      'error': 'መጫን አልተቻለም',
      'tryAgain': 'እንደገና ሞክር',
      'contractHash': 'የውል SHA-256 ሃሽ',
      'quintals': 'ኩንታል',
      'successSign': 'ፊርማ ተመዝግቧል!',
      'successArrival': 'መምጣት ተረጋግጧል!',
      'successLoading': 'መጫን ተረጋግጧል!',
    },
    'om': {
      'title': 'Waliigaltee Bitaa',
      'bidDetails': 'Ibsa Biidhii',
      'quantity': 'Hamma',
      'price': 'Gatii Kuuntalaa',
      'total': 'Waliigala',
      'deposit': 'Mirkaneessa Duraa',
      'contractStatus': 'Haala Waliigaltee',
      'farmerSig': 'Mallatoo Qonnaan Bulaa',
      'offtakerSig': 'Mallatoo Bitataa',
      'signed': 'Mallataa\'ame',
      'awaiting': 'Eegaa Jira',
      'fullyExecuted': 'Waliigalteen guutumaan raawwatame',
      'bothSigned': 'Lamaan isaaniitu mallataa\'aniiru.',
      'confirmCheck': 'Waliigaltee ilaalee nan fudhata',
      'sign': 'Mallattessi',
      'signing': 'Mallatteessaa jira...',
      'waitOfftaker': 'Mallatteessite — bitataa eegaa jira',
      'dispatches': 'Haala Geejjibaa',
      'noDispatches': 'Geejjibi hin qabamne',
      'trucks': 'Konkolaatota',
      'driver': 'Fayda ID Oofaa',
      'scheduledDate': 'Guyyaa Karoorfame',
      'actualDate': 'Guyyaa Dhugaa',
      'confirmArrival': 'Ga\'umsa Mirkaneessi',
      'confirmLoading': 'Fe\'uu Mirkaneessi',
      'error': 'Fe\'uu hin dandeenye',
      'tryAgain': 'Irra deebi\'i yaalii',
      'contractHash': 'Haashii Waliigaltee',
      'quintals': 'Kuuntala',
      'successSign': 'Mallattoon galma\'e!',
      'successArrival': 'Ga\'umsi mirkana\'e!',
      'successLoading': 'Fe\'uun mirkana\'e!',
    },
    'en': {
      'title': 'Purchase Agreement',
      'bidDetails': 'Bid Details',
      'quantity': 'Quantity',
      'price': 'Price / quintal',
      'total': 'Total value',
      'deposit': 'Deposit (10%)',
      'contractStatus': 'Contract Status',
      'farmerSig': 'Farmer signature',
      'offtakerSig': 'Buyer signature',
      'signed': 'Signed',
      'awaiting': 'Awaiting',
      'fullyExecuted': 'Contract fully executed',
      'bothSigned': 'Both parties have signed. You will be notified when trucks are dispatched.',
      'confirmCheck': 'I have reviewed the agreement and agree to the terms',
      'sign': 'Sign Agreement',
      'signing': 'Signing...',
      'waitOfftaker': 'You have signed — waiting for buyer to sign',
      'dispatches': 'Logistics Status',
      'noDispatches': 'No trucks dispatched yet',
      'trucks': 'trucks',
      'driver': 'Driver Fayda ID',
      'scheduledDate': 'Scheduled pickup',
      'actualDate': 'Actual pickup',
      'confirmArrival': 'Confirm Trucks Arrived',
      'confirmLoading': 'Confirm Harvest Loaded',
      'error': 'Could not load agreement',
      'tryAgain': 'Try Again',
      'contractHash': 'Contract integrity hash (SHA-256)',
      'quintals': 'quintals',
      'successSign': 'Signature recorded!',
      'successArrival': 'Arrival confirmed!',
      'successLoading': 'Loading confirmed!',
    },
  };

  String _t(String key) {
    final code = context.read<LanguageService>().languageCode;
    return _strings[code]?[key] ?? _strings['en']![key]!;
  }

  @override
  void initState() {
    super.initState();
    _load();
  }

  Future<void> _load() async {
    setState(() { _loading = true; _error = null; });
    final agreementId = widget.bid.agreementId;
    if (agreementId == null) {
      setState(() { _error = 'No agreement found for this bid'; _loading = false; });
      return;
    }
    try {
      final agResult = await _agreementService.getAgreement(agreementId);
      if (!mounted) return;
      if (agResult['success'] == true) {
        _agreement = agResult['agreement'] as AgreementModel;
      } else {
        setState(() { _error = agResult['message']; _loading = false; });
        return;
      }
      final dpResult = await _agreementService.getDispatches(agreementId);
      if (mounted && dpResult['success'] == true) {
        _dispatches = (dpResult['dispatches'] as List<dynamic>).cast<DispatchModel>();
      }
      if (mounted) setState(() => _loading = false);
    } catch (e) {
      if (mounted) setState(() { _error = e.toString(); _loading = false; });
    }
  }

  Future<void> _sign() async {
    if (!_confirmed) {
      ScaffoldMessenger.of(context).showSnackBar(SnackBar(
        content: Text(_t('confirmCheck')),
        backgroundColor: const Color(0xFF78350F),
        behavior: SnackBarBehavior.floating,
      ));
      return;
    }
    setState(() => _signing = true);
    try {
      final result = await _agreementService.signAgreement(_agreement!.id);
      if (!mounted) return;
      if (result['success'] == true) {
        setState(() => _agreement = result['agreement'] as AgreementModel);
        ScaffoldMessenger.of(context).showSnackBar(SnackBar(
          content: Text(_t('successSign')),
          backgroundColor: _primary,
          behavior: SnackBarBehavior.floating,
        ));
      } else {
        ScaffoldMessenger.of(context).showSnackBar(SnackBar(
          content: Text(result['message'] ?? 'Failed'),
          backgroundColor: const Color(0xFFC62828),
          behavior: SnackBarBehavior.floating,
        ));
      }
    } catch (e) {
      if (mounted) ScaffoldMessenger.of(context).showSnackBar(SnackBar(
        content: Text(e.toString()),
        backgroundColor: const Color(0xFFC62828),
        behavior: SnackBarBehavior.floating,
      ));
    } finally {
      if (mounted) setState(() => _signing = false);
    }
  }

  Future<void> _confirmArrival(DispatchModel dispatch) async {
    setState(() => _actionLoading = true);
    try {
      final result = await _agreementService.confirmArrival(dispatch.id);
      if (!mounted) return;
      if (result['success'] == true) {
        ScaffoldMessenger.of(context).showSnackBar(SnackBar(
          content: Text(_t('successArrival')),
          backgroundColor: _primary,
          behavior: SnackBarBehavior.floating,
        ));
        await _load();
      } else {
        ScaffoldMessenger.of(context).showSnackBar(SnackBar(
          content: Text(result['message'] ?? 'Failed'),
          backgroundColor: const Color(0xFFC62828),
          behavior: SnackBarBehavior.floating,
        ));
      }
    } catch (e) {
      if (mounted) ScaffoldMessenger.of(context).showSnackBar(SnackBar(
        content: Text(e.toString()),
        backgroundColor: const Color(0xFFC62828),
        behavior: SnackBarBehavior.floating,
      ));
    } finally {
      if (mounted) setState(() => _actionLoading = false);
    }
  }

  Future<void> _confirmLoading(DispatchModel dispatch) async {
    setState(() => _actionLoading = true);
    try {
      final result = await _agreementService.confirmLoading(dispatch.id);
      if (!mounted) return;
      if (result['success'] == true) {
        ScaffoldMessenger.of(context).showSnackBar(SnackBar(
          content: Text(_t('successLoading')),
          backgroundColor: _primary,
          behavior: SnackBarBehavior.floating,
        ));
        await _load();
      } else {
        ScaffoldMessenger.of(context).showSnackBar(SnackBar(
          content: Text(result['message'] ?? 'Failed'),
          backgroundColor: const Color(0xFFC62828),
          behavior: SnackBarBehavior.floating,
        ));
      }
    } catch (e) {
      if (mounted) ScaffoldMessenger.of(context).showSnackBar(SnackBar(
        content: Text(e.toString()),
        backgroundColor: const Color(0xFFC62828),
        behavior: SnackBarBehavior.floating,
      ));
    } finally {
      if (mounted) setState(() => _actionLoading = false);
    }
  }

  String _formatDate(String? raw) {
    if (raw == null) return '-';
    try {
      final d = DateTime.parse(raw);
      const months = ['Jan','Feb','Mar','Apr','May','Jun',
                      'Jul','Aug','Sep','Oct','Nov','Dec'];
      return '${d.day} ${months[d.month - 1]} ${d.year}';
    } catch (_) { return raw; }
  }

  Color _dispatchStatusColor(String status) {
    switch (status) {
      case 'SCHEDULED': return const Color(0xFF1E40AF);
      case 'ARRIVED': return const Color(0xFF78350F);
      case 'LOADED': return const Color(0xFF0F6E56);
      case 'DELIVERED': return _primary;
      case 'DRIVER_DEFAULTED': return const Color(0xFFC62828);
      default: return const Color(0xFF64748B);
    }
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
            style: const TextStyle(fontWeight: FontWeight.w900, fontSize: 18)),
        actions: [
          IconButton(icon: const Icon(Icons.refresh_rounded), onPressed: _load)
        ],
      ),
      body: _loading
          ? const Center(child: CircularProgressIndicator(color: Color(0xFF1B4332)))
          : _error != null
          ? _buildError()
          : _actionLoading
          ? const Center(child: CircularProgressIndicator(color: Color(0xFF1B4332)))
          : SingleChildScrollView(
              padding: const EdgeInsets.all(16),
              child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
                _buildBidCard(),
                const SizedBox(height: 16),
                _buildContractStatusCard(),
                const SizedBox(height: 16),
                _buildDispatchesCard(),
                if (_agreement?.contractHash != null) ...[
                  const SizedBox(height: 16),
                  _buildHashCard(),
                ],
                const SizedBox(height: 32),
              ]),
            ),
    );
  }

  Widget _buildBidCard() {
    final bid = widget.bid;
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.circular(16),
          border: Border.all(color: _cardBorder)),
      child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
        Text(_t('bidDetails'),
            style: const TextStyle(fontSize: 14, fontWeight: FontWeight.w800,
                color: Color(0xFF0F291B))),
        const SizedBox(height: 12),
        const Divider(height: 1, color: Color(0xFFF1F5F9)),
        const SizedBox(height: 12),
        Row(children: [
          Expanded(child: _infoTile(_t('quantity'),
              '${bid.quantityQuintals.toStringAsFixed(1)} ${_t('quintals')}',
              const Color(0xFF0F291B))),
          Expanded(child: _infoTile(_t('price'),
              '${bid.pricePerQuintalEtb.toStringAsFixed(0)} ETB',
              const Color(0xFF0F291B))),
        ]),
        const SizedBox(height: 10),
        Row(children: [
          Expanded(child: _infoTile(_t('total'),
              '${bid.totalValueEtb.toStringAsFixed(0)} ETB', _primary)),
          Expanded(child: _infoTile(_t('deposit'),
              '${bid.bidDepositEtb.toStringAsFixed(0)} ETB',
              const Color(0xFF78350F))),
        ]),
      ]),
    );
  }

  Widget _buildContractStatusCard() {
    final ag = _agreement!;
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.circular(16),
          border: Border.all(color: _cardBorder)),
      child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
        Text(_t('contractStatus'),
            style: const TextStyle(fontSize: 14, fontWeight: FontWeight.w800,
                color: Color(0xFF0F291B))),
        const SizedBox(height: 12),

        // Farmer signature row
        _buildSigRow(
          label: _t('farmerSig'),
          signed: ag.farmerHasSigned,
          signedAt: ag.farmerSignedAt,
        ),
        const SizedBox(height: 8),

        // Offtaker signature row
        _buildSigRow(
          label: _t('offtakerSig'),
          signed: ag.offtakerHasSigned,
          signedAt: ag.offtakerSignedAt,
        ),
        const SizedBox(height: 16),

        // Fully executed
        if (ag.fullyExecuted)
          Container(
            width: double.infinity,
            padding: const EdgeInsets.all(14),
            decoration: BoxDecoration(
                color: const Color(0xFFE1F5EE),
                borderRadius: BorderRadius.circular(12),
                border: Border.all(color: const Color(0xFF9FE1CB))),
            child: Column(children: [
              const Icon(Icons.verified_rounded, color: Color(0xFF0F6E56), size: 32),
              const SizedBox(height: 8),
              Text(_t('fullyExecuted'),
                  style: const TextStyle(fontWeight: FontWeight.w800,
                      color: Color(0xFF0F6E56), fontSize: 14)),
              const SizedBox(height: 4),
              Text(_t('bothSigned'),
                  textAlign: TextAlign.center,
                  style: const TextStyle(fontSize: 12, color: Color(0xFF085041))),
            ]),
          )
        else if (ag.farmerHasSigned)
          Container(
            width: double.infinity,
            padding: const EdgeInsets.all(12),
            decoration: BoxDecoration(
                color: const Color(0xFFFFF8F0),
                borderRadius: BorderRadius.circular(12),
                border: Border.all(color: const Color(0xFFFCD34D))),
            child: Text(_t('waitOfftaker'),
                textAlign: TextAlign.center,
                style: const TextStyle(fontSize: 13, color: Color(0xFF78350F),
                    fontWeight: FontWeight.w600)),
          )
        else ...[
          // Sign checkbox + button
          GestureDetector(
            onTap: () => setState(() => _confirmed = !_confirmed),
            child: Container(
              padding: const EdgeInsets.all(12),
              decoration: BoxDecoration(
                  color: const Color(0xFFFFF8F0),
                  borderRadius: BorderRadius.circular(12),
                  border: Border.all(color: const Color(0xFFFCD34D))),
              child: Row(crossAxisAlignment: CrossAxisAlignment.start, children: [
                Checkbox(
                  value: _confirmed,
                  onChanged: (v) => setState(() => _confirmed = v ?? false),
                  activeColor: _primary,
                  materialTapTargetSize: MaterialTapTargetSize.shrinkWrap,
                ),
                const SizedBox(width: 8),
                Expanded(child: Text(_t('confirmCheck'),
                    style: const TextStyle(fontSize: 13, color: Color(0xFF78350F)))),
              ]),
            ),
          ),
          const SizedBox(height: 12),
          SizedBox(
            width: double.infinity,
            child: ElevatedButton.icon(
              onPressed: (_signing || !_confirmed) ? null : _sign,
              icon: _signing
                  ? const SizedBox(width: 16, height: 16,
                      child: CircularProgressIndicator(strokeWidth: 2, color: Colors.white))
                  : const Icon(Icons.draw_rounded, size: 18),
              label: Text(_signing ? _t('signing') : _t('sign'),
                  style: const TextStyle(fontWeight: FontWeight.w800)),
              style: ElevatedButton.styleFrom(
                backgroundColor: _primary,
                foregroundColor: Colors.white,
                disabledBackgroundColor: Colors.grey.shade300,
                padding: const EdgeInsets.symmetric(vertical: 14),
                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
              ),
            ),
          ),
          const SizedBox(height: 8),
          const Center(
            child: Text(
              'Your Fayda National ID is used as the digital signature',
              style: TextStyle(fontSize: 11, color: Color(0xFF94A3B8)),
              textAlign: TextAlign.center,
            ),
          ),
        ],
      ]),
    );
  }

  Widget _buildSigRow({required String label, required bool signed, String? signedAt}) {
    return Container(
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(
        color: signed ? const Color(0xFFE1F5EE) : const Color(0xFFF8FAFC),
        borderRadius: BorderRadius.circular(10),
        border: Border.all(
            color: signed ? const Color(0xFF9FE1CB) : _cardBorder),
      ),
      child: Row(children: [
        Icon(
          signed ? Icons.check_circle_rounded : Icons.radio_button_unchecked_rounded,
          color: signed ? const Color(0xFF0F6E56) : const Color(0xFF94A3B8),
          size: 20,
        ),
        const SizedBox(width: 10),
        Expanded(child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
          Text(label,
              style: TextStyle(
                  fontSize: 13, fontWeight: FontWeight.w600,
                  color: signed ? const Color(0xFF0F6E56) : const Color(0xFF64748B))),
          if (signed && signedAt != null)
            Text(_formatDate(signedAt),
                style: const TextStyle(fontSize: 11, color: Color(0xFF9FE1CB))),
          if (!signed)
            Text(_t('awaiting'),
                style: const TextStyle(fontSize: 11, color: Color(0xFF94A3B8))),
        ])),
      ]),
    );
  }

  Widget _buildDispatchesCard() {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.circular(16),
          border: Border.all(color: _cardBorder)),
      child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
        Text(_t('dispatches'),
            style: const TextStyle(fontSize: 14, fontWeight: FontWeight.w800,
                color: Color(0xFF0F291B))),
        const SizedBox(height: 12),
        if (_dispatches.isEmpty)
          Center(child: Padding(
            padding: const EdgeInsets.symmetric(vertical: 20),
            child: Column(children: [
              Icon(Icons.local_shipping_outlined, size: 40,
                  color: Colors.grey.shade300),
              const SizedBox(height: 8),
              Text(_t('noDispatches'),
                  style: TextStyle(color: Colors.grey.shade500, fontSize: 13)),
            ]),
          ))
        else
          ...(_dispatches.map((d) => _buildDispatchCard(d))),
      ]),
    );
  }

  Widget _buildDispatchCard(DispatchModel d) {
    final statusColor = _dispatchStatusColor(d.status);
    return Container(
      margin: const EdgeInsets.only(bottom: 10),
      padding: const EdgeInsets.all(14),
      decoration: BoxDecoration(
          color: const Color(0xFFF8FAFC),
          borderRadius: BorderRadius.circular(12),
          border: Border.all(color: _cardBorder)),
      child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
        Row(mainAxisAlignment: MainAxisAlignment.spaceBetween, children: [
          Row(children: [
            Icon(Icons.local_shipping_rounded, size: 18, color: statusColor),
            const SizedBox(width: 8),
            Text('${d.truckCount} ${_t('trucks')}',
                style: const TextStyle(fontWeight: FontWeight.w700, fontSize: 14)),
          ]),
          Container(
            padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
            decoration: BoxDecoration(
                color: statusColor.withOpacity(0.1),
                borderRadius: BorderRadius.circular(20)),
            child: Text(d.status.replaceAll('_', ' '),
                style: TextStyle(fontSize: 10, fontWeight: FontWeight.w800,
                    color: statusColor)),
          ),
        ]),
        const SizedBox(height: 10),
        Text('${_t('driver')}: ${d.driverFaydaId}',
            style: const TextStyle(fontSize: 12, color: Color(0xFF64748B))),
        const SizedBox(height: 6),
        Row(children: [
          Expanded(child: _infoTile(_t('scheduledDate'),
              _formatDate(d.scheduledPickupDate?.toString()), const Color(0xFF0F291B))),
          if (d.actualPickupDate != null)
            Expanded(child: _infoTile(_t('actualDate'),
                _formatDate(d.actualPickupDate?.toString()),
                const Color(0xFF0F6E56))),
        ]),

        // Farmer actions
        if (d.isScheduled) ...[
          const SizedBox(height: 12),
          SizedBox(
            width: double.infinity,
            child: ElevatedButton.icon(
              onPressed: _actionLoading ? null : () => _confirmArrival(d),
              icon: const Icon(Icons.where_to_vote_rounded, size: 16),
              label: Text(_t('confirmArrival'),
                  style: const TextStyle(fontWeight: FontWeight.w700, fontSize: 13)),
              style: ElevatedButton.styleFrom(
                backgroundColor: const Color(0xFF1E40AF),
                foregroundColor: Colors.white,
                padding: const EdgeInsets.symmetric(vertical: 12),
                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
              ),
            ),
          ),
        ],
        if (d.isArrived) ...[
          const SizedBox(height: 12),
          SizedBox(
            width: double.infinity,
            child: ElevatedButton.icon(
              onPressed: _actionLoading ? null : () => _confirmLoading(d),
              icon: const Icon(Icons.inventory_rounded, size: 16),
              label: Text(_t('confirmLoading'),
                  style: const TextStyle(fontWeight: FontWeight.w700, fontSize: 13)),
              style: ElevatedButton.styleFrom(
                backgroundColor: const Color(0xFF0F6E56),
                foregroundColor: Colors.white,
                padding: const EdgeInsets.symmetric(vertical: 12),
                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
              ),
            ),
          ),
        ],
      ]),
    );
  }

  Widget _buildHashCard() {
    return Container(
      padding: const EdgeInsets.all(14),
      decoration: BoxDecoration(
          color: const Color(0xFFF8FAFC),
          borderRadius: BorderRadius.circular(12),
          border: Border.all(color: _cardBorder)),
      child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
        Text(_t('contractHash'),
            style: const TextStyle(fontSize: 11, fontWeight: FontWeight.w600,
                color: Color(0xFF94A3B8))),
        const SizedBox(height: 6),
        Text(_agreement!.contractHash!,
            style: const TextStyle(fontSize: 11, fontFamily: 'monospace',
                color: Color(0xFF64748B))),
      ]),
    );
  }

  Widget _infoTile(String label, String value, Color valueColor) {
    return Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
      Text(label, style: const TextStyle(fontSize: 10, color: Color(0xFF64748B))),
      const SizedBox(height: 2),
      Text(value, style: TextStyle(fontSize: 13, fontWeight: FontWeight.w700,
          color: valueColor)),
    ]);
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
          onPressed: _load,
          icon: const Icon(Icons.refresh_rounded),
          label: Text(_t('tryAgain')),
          style: ElevatedButton.styleFrom(
              backgroundColor: const Color(0xFF1B4332), foregroundColor: Colors.white,
              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10))),
        ),
      ]),
    ));
  }
}

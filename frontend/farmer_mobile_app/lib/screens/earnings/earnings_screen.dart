import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../models/farm_model.dart';
import '../../services/farm_service.dart';
import '../../services/language_service.dart';

class EarningsScreen extends StatefulWidget {
  const EarningsScreen({super.key});

  @override
  State<EarningsScreen> createState() => _EarningsScreenState();
}

class _EarningsScreenState extends State<EarningsScreen> {
  final _farmService = FarmService();

  List<FarmModel> _farms = [];
  String? _selectedFarmId;
  Map<String, dynamic>? _agriScore;
  bool _loading = true;
  String? _error;

  static const _primary = Color(0xFF1B4332);
  static const _surface = Color(0xFFF4F7F5);
  static const _cardBorder = Color(0xFFE2E8F0);

  static const Map<String, Map<String, String>> _strings = {
    'am': {
      'title': 'ገቢ እና ውጤቶች',
      'agriScore': 'አግሪ ስኮር',
      'yourScore': 'የእርስዎ ነጥብ',
      'outOf': 'ከ 900',
      'breakdown': 'የነጥብ ዝርዝር',
      'voucherDiscipline': 'የቫውቸር ቅደም ተከተል',
      'yieldAccuracy': 'የምርት ትክክለኛነት',
      'contractFulfillment': 'የውል አፈጻጸም',
      'repaymentCompletion': 'የክፍያ ጠቀሜታ',
      'seasonCompletion': 'የወቅት ማጠናቀቅ',
      'agronomistAssessment': 'የባለሙያ ግምገማ',
      'selectFarm': 'እርሻ ይምረጡ',
      'noScore': 'ምንም ውጤት የለም — ወቅቱ ሲጠናቀቅ ይሰላል',
      'error': 'መጫን አልተቻለም',
      'tryAgain': 'እንደገና ሞክር',
      'howScored': 'ስኮር እንዴት ይሰላል?',
      'scoreInfo': 'አግሪ ስኮር እስከ 900 ነጥብ ሊደርስ ይችላል። ከፍተኛ ስኮር ለወደፊት ወቅቶች የተሻለ ኢንቨስትመንት ሁኔታ ያስገኛል።',
    },
    'om': {
      'title': 'Galii fi Bu\'aa',
      'agriScore': 'Qabxii Qonnaa',
      'yourScore': 'Qabxii Kee',
      'outOf': 'Keessaa 900',
      'breakdown': 'Ibsa Qabxii',
      'voucherDiscipline': 'Tartiiba Waraqaa Ragaa',
      'yieldAccuracy': 'Sirriitti Moo\'ina',
      'contractFulfillment': 'Raawwii Waliigaltee',
      'repaymentCompletion': 'Gumeessa Deebisuu',
      'seasonCompletion': 'Xumuura Yeroo',
      'agronomistAssessment': 'Madaallii Ogeessa',
      'selectFarm': 'Lafa qonnaa filadhu',
      'noScore': 'Qabxii hin jiru — yeroon xumuramee ni herregama',
      'error': 'Fe\'uu hin dandeenye',
      'tryAgain': 'Irra deebi\'i yaalii',
      'howScored': 'Akkamitti qabxiin herregama?',
      'scoreInfo': 'Qabxiin qonnaa hanga 900 ga\'uu danda\'a, naannoo 6 cufaa jira.',
    },
    'en': {
      'title': 'Earnings & Score',
      'agriScore': 'Agri-Score',
      'yourScore': 'Your Score',
      'outOf': 'out of 900',
      'breakdown': 'Score Breakdown',
      'voucherDiscipline': 'Voucher discipline',
      'yieldAccuracy': 'Yield accuracy',
      'contractFulfillment': 'Contract fulfillment',
      'repaymentCompletion': 'Repayment completion',
      'seasonCompletion': 'Season completion',
      'agronomistAssessment': 'Agronomist assessment',
      'selectFarm': 'Select farm',
      'noScore': 'No score yet — calculated when season completes',
      'error': 'Could not load score',
      'tryAgain': 'Try Again',
      'howScored': 'How is my score calculated?',
      'scoreInfo': 'Your Agri-Score reaches up to 900 points across 6 areas. A higher score unlocks better investment terms in future seasons and builds your financial reputation on the platform.',
    },
  };

  String _t(String key) {
    final code = context.read<LanguageService>().languageCode;
    return _strings[code]?[key] ?? _strings['en']![key]!;
  }

  final List<Map<String, dynamic>> _scoreComponents = [
    {'key': 'voucherDisciplinePts', 'labelKey': 'voucherDiscipline', 'max': 150,
      'icon': Icons.qr_code_rounded, 'color': const Color(0xFF1B4332)},
    {'key': 'yieldAccuracyPts', 'labelKey': 'yieldAccuracy', 'max': 200,
      'icon': Icons.trending_up_rounded, 'color': const Color(0xFF0F6E56)},
    {'key': 'contractFulfillmentPts', 'labelKey': 'contractFulfillment', 'max': 200,
      'icon': Icons.handshake_rounded, 'color': const Color(0xFF1E40AF)},
    {'key': 'repaymentCompletionPts', 'labelKey': 'repaymentCompletion', 'max': 200,
      'icon': Icons.payments_rounded, 'color': const Color(0xFF78350F)},
    {'key': 'seasonCompletionPts', 'labelKey': 'seasonCompletion', 'max': 100,
      'icon': Icons.check_circle_rounded, 'color': const Color(0xFF15803D)},
    {'key': 'agronomistAssessmentPts', 'labelKey': 'agronomistAssessment', 'max': 50,
      'icon': Icons.person_rounded, 'color': const Color(0xFF7C3AED)},
  ];

  @override
  void initState() {
    super.initState();
    _loadFarms();
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
        if (_selectedFarmId != null) await _loadScore();
      } else {
        setState(() { _error = result['message'] ?? 'Failed'; _loading = false; });
      }
    } catch (e) {
      if (mounted) setState(() { _error = e.toString(); _loading = false; });
    }
  }

  Future<void> _loadScore() async {
    if (_selectedFarmId == null) return;
    setState(() { _loading = true; _error = null; });
    try {
      final result = await _farmService.getAgriScore(_selectedFarmId!);
      if (!mounted) return;
      if (result['success'] == true) {
        setState(() { _agriScore = result['agriScore'] as Map<String, dynamic>?; _loading = false; });
      } else {
        setState(() { _agriScore = null; _loading = false; });
      }
    } catch (e) {
      if (mounted) setState(() { _agriScore = null; _loading = false; });
    }
  }

  int _toInt(dynamic v) {
    if (v == null) return 0;
    if (v is int) return v;
    return int.tryParse(v.toString()) ?? 0;
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
          IconButton(icon: const Icon(Icons.refresh_rounded), onPressed: _loadScore)
        ],
      ),
      body: _loading
          ? const Center(child: CircularProgressIndicator(color: Color(0xFF1B4332)))
          : _error != null
          ? _buildError()
          : SingleChildScrollView(
              padding: const EdgeInsets.all(16),
              child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
                if (_farms.length > 1) ...[_buildFarmSelector(), const SizedBox(height: 16)],
                _agriScore == null ? _buildNoScore() : _buildScoreContent(),
              ]),
            ),
    );
  }

  Widget _buildFarmSelector() {
    return DropdownButtonFormField<String>(
      value: _selectedFarmId,
      decoration: InputDecoration(
        labelText: _t('selectFarm'),
        border: OutlineInputBorder(borderRadius: BorderRadius.circular(10)),
        filled: true, fillColor: Colors.white,
        contentPadding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
      ),
      items: _farms.map((f) => DropdownMenuItem(
        value: f.id,
        child: Text(f.farmName ?? f.cropType, style: const TextStyle(fontSize: 13)),
      )).toList(),
      onChanged: (v) { setState(() => _selectedFarmId = v); _loadScore(); },
    );
  }

  Widget _buildNoScore() {
    return Container(
      padding: const EdgeInsets.all(32),
      decoration: BoxDecoration(color: Colors.white,
          borderRadius: BorderRadius.circular(16),
          border: Border.all(color: _cardBorder)),
      child: Center(child: Column(children: [
        Icon(Icons.emoji_events_rounded, size: 56, color: Colors.grey.shade300),
        const SizedBox(height: 12),
        Text(_t('noScore'), textAlign: TextAlign.center,
            style: const TextStyle(fontSize: 14, color: Color(0xFF64748B))),
      ])),
    );
  }

  Widget _buildScoreContent() {
    final score = _toInt(_agriScore?['score']);
    final pct = (score / 900).clamp(0.0, 1.0);
    final isGood = score >= 700;
    final scoreHighlight = isGood ? const Color(0xFF86EFAC) : const Color(0xFFFCD34D);

    return Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
      // Score hero card
      Container(
        padding: const EdgeInsets.all(24),
        decoration: BoxDecoration(
          gradient: const LinearGradient(
              colors: [Color(0xFF1B4332), Color(0xFF2D6A4F)],
              begin: Alignment.topLeft, end: Alignment.bottomRight),
          borderRadius: BorderRadius.circular(20),
        ),
        child: Column(children: [
          Row(mainAxisAlignment: MainAxisAlignment.spaceBetween, children: [
            Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
              Text(_t('agriScore'),
                  style: const TextStyle(color: Colors.white70, fontSize: 12)),
              const SizedBox(height: 4),
              Text(_t('yourScore'),
                  style: const TextStyle(color: Colors.white,
                      fontSize: 16, fontWeight: FontWeight.w800)),
            ]),
            Column(crossAxisAlignment: CrossAxisAlignment.end, children: [
              Text('$score',
                  style: TextStyle(color: scoreHighlight,
                      fontSize: 48, fontWeight: FontWeight.w900)),
              Text(_t('outOf'),
                  style: const TextStyle(color: Colors.white60, fontSize: 12)),
            ]),
          ]),
          const SizedBox(height: 16),
          ClipRRect(
            borderRadius: BorderRadius.circular(6),
            child: LinearProgressIndicator(
              value: pct,
              backgroundColor: Colors.white24,
              valueColor: AlwaysStoppedAnimation<Color>(scoreHighlight),
              minHeight: 10,
            ),
          ),
        ]),
      ),
      const SizedBox(height: 20),

      // Breakdown
      Text(_t('breakdown'),
          style: const TextStyle(fontSize: 15, fontWeight: FontWeight.w800,
              color: Color(0xFF0F291B))),
      const SizedBox(height: 10),
      Container(
        decoration: BoxDecoration(color: Colors.white,
            borderRadius: BorderRadius.circular(16),
            border: Border.all(color: _cardBorder)),
        child: Column(
          children: _scoreComponents.asMap().entries.map((entry) {
            final i = entry.key;
            final comp = entry.value;
            final pts = _toInt(_agriScore?[comp['key']]);
            final max = comp['max'] as int;
            final color = comp['color'] as Color;
            final isLast = i == _scoreComponents.length - 1;
            return Column(children: [
              Padding(
                padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 14),
                child: Row(children: [
                  Container(
                    width: 36, height: 36,
                    decoration: BoxDecoration(
                        color: color.withOpacity(0.1),
                        borderRadius: BorderRadius.circular(8)),
                    child: Icon(comp['icon'] as IconData, size: 18, color: color),
                  ),
                  const SizedBox(width: 12),
                  Expanded(child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
                    Text(_t(comp['labelKey'] as String),
                        style: const TextStyle(fontSize: 13,
                            fontWeight: FontWeight.w600, color: Color(0xFF0F291B))),
                    const SizedBox(height: 5),
                    ClipRRect(
                      borderRadius: BorderRadius.circular(4),
                      child: LinearProgressIndicator(
                        value: max > 0 ? (pts / max).clamp(0.0, 1.0) : 0,
                        backgroundColor: const Color(0xFFF1F5F9),
                        valueColor: AlwaysStoppedAnimation<Color>(color),
                        minHeight: 5,
                      ),
                    ),
                  ])),
                  const SizedBox(width: 12),
                  Column(crossAxisAlignment: CrossAxisAlignment.end, children: [
                    Text('$pts',
                        style: TextStyle(fontSize: 14,
                            fontWeight: FontWeight.w900, color: color)),
                    Text('/ $max',
                        style: const TextStyle(fontSize: 10,
                            color: Color(0xFF94A3B8))),
                  ]),
                ]),
              ),
              if (!isLast) const Divider(height: 1, color: Color(0xFFF1F5F9)),
            ]);
          }).toList(),
        ),
      ),
      const SizedBox(height: 16),

      // Info card
      Container(
        padding: const EdgeInsets.all(14),
        decoration: BoxDecoration(
            color: const Color(0xFFE1F5EE),
            borderRadius: BorderRadius.circular(12),
            border: Border.all(color: const Color(0xFF9FE1CB))),
        child: Row(crossAxisAlignment: CrossAxisAlignment.start, children: [
          const Icon(Icons.info_outline_rounded, size: 18, color: Color(0xFF0F6E56)),
          const SizedBox(width: 10),
          Expanded(child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
            Text(_t('howScored'),
                style: const TextStyle(fontSize: 12, fontWeight: FontWeight.w700,
                    color: Color(0xFF0F6E56))),
            const SizedBox(height: 4),
            Text(_t('scoreInfo'),
                style: const TextStyle(fontSize: 12,
                    color: Color(0xFF085041), height: 1.5)),
          ])),
        ]),
      ),
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
        const SizedBox(height: 20),
        ElevatedButton.icon(
          onPressed: _loadScore,
          icon: const Icon(Icons.refresh_rounded),
          label: Text(_t('tryAgain')),
          style: ElevatedButton.styleFrom(
              backgroundColor: _primary, foregroundColor: Colors.white,
              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10))),
        ),
      ]),
    ));
  }
}

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:provider/provider.dart';
import 'package:qr_flutter/qr_flutter.dart';
import '../../models/voucher_model.dart';
import '../../services/language_service.dart';
import '../../widgets/voucher_status_chip.dart';
import '../../widgets/voucher_category_chip.dart';
import 'voucher_qr_screen.dart';

class VoucherDetailScreen extends StatelessWidget {
  final VoucherModel voucher;
  const VoucherDetailScreen({super.key, required this.voucher});

  static const _primary    = Color(0xFF1B4332);
  static const _surface    = Color(0xFFF4F7F5);
  static const _cardBorder = Color(0xFFE2E8F0);

  static const Map<String, Map<String, String>> _strings = {
    'am': {
      'fullscreen':     'ሙሉ ስክሪን QR',
      'activeReady':    'ንቁ — ለቅናሽ ዝግጁ',
      'statusLabel':    'ሁኔታ: {s}',
      'tapExpand':      'ሙሉ ስክሪን ለማስፋት ጠቅ ያድርጉ',
      'noQrData':       'QR ዳታ የለም',
      'noQrHint':       'voucherCode ባዶ ነው። API field name ያረጋግጡ።',
      'copyCode':       'ኮድ ኮፒ ያድርጉ',
      'worksOffline':   'ከኢንተርኔት ውጪ ይሰራል | ይህን ለነጋዴ ያሳዩ',
      'showToMerchant': 'QR ለነጋዴ ያሳዩ',
      'redeemed':       'ተቀብሏል',
      'redeemedOn':     'ቀን {d}',
      'paymentRel':     'ክፍያ ለነጋዴ ተለቋል',
      'locked':         'ተቆልፏል',
      'lockedHint':     'ቫውቸር #{n} ከተቀበለ በኋላ ይከፈታል',
      'agronOrder':     'የእርሻ ቅደም ተከተል መከተል አለበት',
      'expired':        'ጊዜው አልፏል',
      'expiredHint':    'ይህ ቫውቸር የቀነ ቀን አልፏል',
      'contactSupport': 'ለድጋፍ ድርጅቱን ያነጋግሩ',
      'details':        'የቫውቸር ዝርዝሮች',
      'voucherId':      'ቫውቸር መለያ',
      'code':           'ኮድ',
      'category':       'ምድብ',
      'amount':         'መጠን',
      'sequence':       'ቅደም ተከተል',
      'validUntil':     'እስከ',
      'created':        'ተፈጥሯል',
      'redeemedAt':     'ተቀብሏል',
      'securityNote':   'ይህ ቫውቸር ክሪፕቶግራፊካሊ ተፈርሟል። የነጋዴ መተግበሪያ ማንኛውም ክፍያ ከመለቀቁ በፊት 6 የደህንነት ምርመራዎችን ያካሂዳል። ኮድዎን ከተረጋገጡ ነጋዴዎች ውጪ ከማንም ጋር አይጋሩ።',
      'noCode':         'ኮድ የለም',
      'only':           'ብቻ',
      'seqNum':         'ቅደም #{n}',
    },
    'om': {
      'fullscreen':     'QR Guutuu Skiriinii',
      'activeReady':    'Hojjechaa Jira — fudhatuuf qophaa\'e',
      'statusLabel':    'Haala: {s}',
      'tapExpand':      'Guutuu skiriiniitti bal\'isuuf tuqi',
      'noQrData':       'Deetaa QR hin jiru',
      'noQrHint':       'voucherCode duwwaadha. Maqaa dirree API mirkaneessi.',
      'copyCode':       'Koodii Garagalchi',
      'worksOffline':   'Interneetii malee hojjeta | Kana gurgurtaaf agarsiisi',
      'showToMerchant': 'QR gurgurtaaf agarsiisi',
      'redeemed':       'Fudhataame',
      'redeemedOn':     'Guyyaa {d}',
      'paymentRel':     'Kafaltiin gurgurtaaf gadhiifame',
      'locked':         'Cufame',
      'lockedHint':     'Waraqaa ragaa #{n} fudhataame booda banama',
      'agronOrder':     'Tartiibni qonnaa hordofamuu qaba',
      'expired':        'Yeroon Darbeera',
      'expiredHint':    'Waraqaan ragaa kun guyyaa haqamuu darbee jira',
      'contactSupport': 'Deeggarsa Agri-Yield quunnamaa',
      'details':        'Bal\'ina Waraqaa Ragaa',
      'voucherId':      'ID Waraqaa Ragaa',
      'code':           'Koodii',
      'category':       'Gosa',
      'amount':         'Baay\'ina',
      'sequence':       'Tartiiba',
      'validUntil':     'Hanga',
      'created':        'Uumame',
      'redeemedAt':     'Fudhataame',
      'securityNote':   'Waraqaan ragaa kun mallattoo cryptographic qaba. Appiin gurgurtaa kafaltii kamiyyuu gadhiifamu dura sakatta\'iinsa nageenyaa 6 raawwata.',
      'noCode':         'KOODII HIN JIRU',
      'only':           'qofa',
      'seqNum':         'Tartiiba #{n}',
    },
    'en': {
      'fullscreen':     'Full screen QR',
      'activeReady':    'ACTIVE — ready to redeem',
      'statusLabel':    'Status: {s}',
      'tapExpand':      'Tap to expand to full screen',
      'noQrData':       'No QR data',
      'noQrHint':       'voucherCode is empty. Check API field name.',
      'copyCode':       'Copy code',
      'worksOffline':   'Works offline  |  Show this to the merchant',
      'showToMerchant': 'Show QR to Merchant',
      'redeemed':       'Redeemed',
      'redeemedOn':     'on {d}',
      'paymentRel':     'Payment released to merchant',
      'locked':         'Locked',
      'lockedHint':     'Unlocks after voucher #{n} is redeemed',
      'agronOrder':     'Agronomic order must be followed',
      'expired':        'Expired',
      'expiredHint':    'This voucher has passed its validity date',
      'contactSupport': 'Contact Agri-Yield support for assistance',
      'details':        'Voucher Details',
      'voucherId':      'Voucher ID',
      'code':           'Code',
      'category':       'Category',
      'amount':         'Amount',
      'sequence':       'Sequence',
      'validUntil':     'Valid Until',
      'created':        'Created',
      'redeemedAt':     'Redeemed At',
      'securityNote':   'This voucher is cryptographically signed. The merchant app runs 6 security checks before releasing any payment. Never share your voucher code with anyone other than certified merchants.',
      'noCode':         'NO CODE',
      'only':           'only',
      'seqNum':         'Sequence #{n}',
    },
  };

  String _t(BuildContext context, String key, {String s = '', String d = '', String n = ''}) {
    final code = context.read<LanguageService>().languageCode;
    return (_strings[code]?[key] ?? _strings['en']![key]!)
        .replaceAll('{s}', s).replaceAll('{d}', d).replaceAll('{n}', n);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: _surface,
      appBar: AppBar(
        backgroundColor: _primary, foregroundColor: Colors.white,
        title: Text(voucher.categoryLabel, style: const TextStyle(fontWeight: FontWeight.w900, fontSize: 18, letterSpacing: -0.5)),
        actions: [
          if (voucher.isActive)
            IconButton(icon: const Icon(Icons.fullscreen_rounded), tooltip: _t(context, 'fullscreen'),
                onPressed: () => Navigator.push(context, MaterialPageRoute(builder: (_) => VoucherQrScreen(voucher: voucher)))),
        ],
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(20),
        child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
          _buildHeaderCard(),
          const SizedBox(height: 16),
          _buildQrCard(context),
          const SizedBox(height: 16),
          if (voucher.isRedeemed) _buildRedeemedCard(context),
          if (voucher.isLocked)   _buildLockedCard(context),
          if (voucher.isExpired)  _buildExpiredCard(context),
          const SizedBox(height: 16),
          _buildInfoCard(context),
          const SizedBox(height: 16),
          _buildSecurityNote(context),
          const SizedBox(height: 24),
          if (voucher.isActive)
            SizedBox(width: double.infinity, child: ElevatedButton.icon(
              onPressed: () => Navigator.push(context, MaterialPageRoute(builder: (_) => VoucherQrScreen(voucher: voucher))),
              icon: const Icon(Icons.qr_code_2_rounded, size: 22),
              label: Text(_t(context, 'showToMerchant')),
              style: ElevatedButton.styleFrom(backgroundColor: _primary, foregroundColor: Colors.white,
                  padding: const EdgeInsets.symmetric(vertical: 16),
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
                  textStyle: const TextStyle(fontSize: 15, fontWeight: FontWeight.bold)),
            )),
        ]),
      ),
    );
  }

  Widget _buildHeaderCard() {
    return Container(width: double.infinity, padding: const EdgeInsets.all(20),
        decoration: BoxDecoration(
            gradient: const LinearGradient(colors: [Color(0xFF1B4332), Color(0xFF2D6A4F)], begin: Alignment.topLeft, end: Alignment.bottomRight),
            borderRadius: BorderRadius.circular(20)),
        child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
          Row(children: [
            Expanded(child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
              Text(voucher.productName, style: const TextStyle(color: Colors.white, fontSize: 16, fontWeight: FontWeight.w900, letterSpacing: -0.3)),
              const SizedBox(height: 4),
              VoucherCategoryChip(category: voucher.productCategory),
            ])),
            VoucherStatusChip(status: voucher.status, fontSize: 11),
          ]),
          const SizedBox(height: 16),
          Text('${voucher.amountEtb.toStringAsFixed(2)} ETB',
              style: const TextStyle(color: Colors.white, fontSize: 36, fontWeight: FontWeight.w900, fontFamily: 'monospace', letterSpacing: -1)),
        ]));
  }

  Widget _buildQrCard(BuildContext context) {
    final code = voucher.voucherCode;
    final hasCode = code.isNotEmpty;
    return GestureDetector(
      onTap: hasCode && voucher.isActive
          ? () => Navigator.push(context, MaterialPageRoute(builder: (_) => VoucherQrScreen(voucher: voucher)))
          : null,
      child: Container(width: double.infinity, padding: const EdgeInsets.all(24),
          decoration: BoxDecoration(color: Colors.white, borderRadius: BorderRadius.circular(20), border: Border.all(color: _cardBorder)),
          child: Column(children: [
            Container(padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
                decoration: BoxDecoration(
                    color: voucher.isActive ? const Color(0xFFDCFCE7) : const Color(0xFFFEF9C3),
                    borderRadius: BorderRadius.circular(20)),
                child: Text(
                    voucher.isActive ? _t(context, 'activeReady') : _t(context, 'statusLabel', s: voucher.statusLabel),
                    style: TextStyle(fontSize: 11, fontWeight: FontWeight.w800,
                        color: voucher.isActive ? const Color(0xFF15803D) : const Color(0xFF92400E)))),
            const SizedBox(height: 20),
            if (hasCode)
              Container(padding: const EdgeInsets.all(12),
                  decoration: BoxDecoration(color: Colors.white, borderRadius: BorderRadius.circular(12), border: Border.all(color: _cardBorder, width: 1.5)),
                  child: QrImageView(data: code, version: QrVersions.auto, size: 200, backgroundColor: Colors.white,
                      eyeStyle: const QrEyeStyle(eyeShape: QrEyeShape.square, color: Color(0xFF1B4332)),
                      dataModuleStyle: const QrDataModuleStyle(dataModuleShape: QrDataModuleShape.square, color: Color(0xFF1B4332))))
            else
              Container(width: 200, height: 200,
                  decoration: BoxDecoration(color: const Color(0xFFFEF2F2), borderRadius: BorderRadius.circular(12), border: Border.all(color: const Color(0xFFFECACA))),
                  child: Column(mainAxisAlignment: MainAxisAlignment.center, children: [
                    const Icon(Icons.qr_code_rounded, size: 48, color: Color(0xFFEF4444)),
                    const SizedBox(height: 10),
                    Text(_t(context, 'noQrData'), style: const TextStyle(fontWeight: FontWeight.w800, color: Color(0xFF991B1B))),
                    const SizedBox(height: 6),
                    Padding(padding: const EdgeInsets.symmetric(horizontal: 16),
                        child: Text(_t(context, 'noQrHint'), textAlign: TextAlign.center,
                            style: const TextStyle(fontSize: 11, color: Color(0xFFDC2626)))),
                  ])),
            const SizedBox(height: 8),
            if (hasCode && voucher.isActive)
              Text(_t(context, 'tapExpand'), style: const TextStyle(fontSize: 11, color: Color(0xFF94A3B8))),
            const SizedBox(height: 16),
            GestureDetector(
              onTap: hasCode ? () => Clipboard.setData(ClipboardData(text: voucher.voucherCode)) : null,
              child: Container(padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 10),
                  decoration: BoxDecoration(color: const Color(0xFFF1F5F9), borderRadius: BorderRadius.circular(10)),
                  child: Row(mainAxisSize: MainAxisSize.min, children: [
                    Text(hasCode ? voucher.voucherCode : _t(context, 'noCode'),
                        style: TextStyle(fontFamily: 'monospace', fontSize: 16, fontWeight: FontWeight.w800, letterSpacing: 2,
                            color: hasCode ? const Color(0xFF1B4332) : const Color(0xFFEF4444))),
                    const SizedBox(width: 8),
                    Icon(hasCode ? Icons.copy_rounded : Icons.warning_rounded, size: 14, color: const Color(0xFF64748B)),
                  ])),
            ),
            const SizedBox(height: 8),
            Text(_t(context, 'worksOffline'), style: const TextStyle(fontSize: 11, color: Color(0xFF64748B))),
          ])),
    );
  }

  Widget _buildRedeemedCard(BuildContext context) {
    return Container(width: double.infinity, padding: const EdgeInsets.all(18),
        decoration: BoxDecoration(color: const Color(0xFFF0FDF4), borderRadius: BorderRadius.circular(16), border: Border.all(color: const Color(0xFFBBF7D0))),
        child: Row(children: [
          const Icon(Icons.check_circle_rounded, color: Color(0xFF16A34A), size: 28), const SizedBox(width: 12),
          Expanded(child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
            Text(_t(context, 'redeemed'), style: const TextStyle(fontWeight: FontWeight.w800, color: Color(0xFF14532D), fontSize: 14)),
            if (voucher.redeemedAt != null)
              Text(_t(context, 'redeemedOn', d: voucher.redeemedAt!), style: const TextStyle(fontSize: 11, color: Color(0xFF16A34A))),
            const SizedBox(height: 4),
            Text(_t(context, 'paymentRel'), style: const TextStyle(fontSize: 11, color: Color(0xFF15803D))),
          ])),
        ]));
  }

  Widget _buildLockedCard(BuildContext context) {
    return Container(width: double.infinity, padding: const EdgeInsets.all(18),
        decoration: BoxDecoration(color: const Color(0xFFEFF6FF), borderRadius: BorderRadius.circular(16), border: Border.all(color: const Color(0xFFBFDBFE))),
        child: Row(children: [
          const Icon(Icons.lock_rounded, color: Color(0xFF1D4ED8), size: 28), const SizedBox(width: 12),
          Expanded(child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
            Text(_t(context, 'locked'), style: const TextStyle(fontWeight: FontWeight.w800, color: Color(0xFF1E3A8A), fontSize: 14)),
            Text(_t(context, 'lockedHint', n: '${voucher.sequenceOrder - 1}'), style: const TextStyle(fontSize: 11, color: Color(0xFF1D4ED8))),
            const SizedBox(height: 4),
            Text(_t(context, 'agronOrder'), style: const TextStyle(fontSize: 11, color: Color(0xFF3B82F6))),
          ])),
        ]));
  }

  Widget _buildExpiredCard(BuildContext context) {
    return Container(width: double.infinity, padding: const EdgeInsets.all(18),
        decoration: BoxDecoration(color: const Color(0xFFFEF2F2), borderRadius: BorderRadius.circular(16), border: Border.all(color: const Color(0xFFFECACA))),
        child: Row(children: [
          const Icon(Icons.schedule_rounded, color: Color(0xFFDC2626), size: 28), const SizedBox(width: 12),
          Expanded(child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
            Text(_t(context, 'expired'), style: const TextStyle(fontWeight: FontWeight.w800, color: Color(0xFF991B1B), fontSize: 14)),
            Text(_t(context, 'expiredHint'), style: const TextStyle(fontSize: 11, color: Color(0xFFDC2626))),
            const SizedBox(height: 4),
            Text(_t(context, 'contactSupport'), style: const TextStyle(fontSize: 11, color: Color(0xFFEF4444))),
          ])),
        ]));
  }

  Widget _buildInfoCard(BuildContext context) {
    final rows = [
      [_t(context, 'voucherId'), voucher.id, true],
      [_t(context, 'code'), voucher.voucherCode.isEmpty ? '⚠ EMPTY' : voucher.voucherCode, true],
      [_t(context, 'category'), '${voucher.categoryLabel} ${_t(context, 'only')}', false],
      [_t(context, 'amount'), '${voucher.amountEtb.toStringAsFixed(2)} ETB', false],
      [_t(context, 'sequence'), _t(context, 'seqNum', n: '${voucher.sequenceOrder}'), false],
      [_t(context, 'validUntil'), voucher.validUntil, false],
      [_t(context, 'created'), voucher.createdAt, false],
      if (voucher.isRedeemed && voucher.redeemedAt != null) [_t(context, 'redeemedAt'), voucher.redeemedAt!, false],
    ];
    return Container(decoration: BoxDecoration(color: Colors.white, borderRadius: BorderRadius.circular(20), border: Border.all(color: _cardBorder)),
        child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
          Padding(padding: const EdgeInsets.fromLTRB(16, 14, 16, 0),
              child: Text(_t(context, 'details'), style: const TextStyle(fontSize: 12, fontWeight: FontWeight.w800, color: Color(0xFF64748B), letterSpacing: 0.5))),
          const SizedBox(height: 8),
          ...rows.asMap().entries.map((entry) {
            final i = entry.key; final row = entry.value; final isMono = row[2] as bool;
            return Column(children: [
              Padding(padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 10),
                  child: Row(crossAxisAlignment: CrossAxisAlignment.start, children: [
                    SizedBox(width: 100, child: Text(row[0] as String, style: const TextStyle(fontSize: 12, color: Color(0xFF94A3B8), fontWeight: FontWeight.w500))),
                    Expanded(child: Text(row[1] as String, style: TextStyle(fontSize: isMono ? 10 : 13, fontWeight: FontWeight.w700,
                        color: const Color(0xFF0F291B), fontFamily: isMono ? 'monospace' : null, letterSpacing: isMono ? 0.3 : 0))),
                  ])),
              if (i < rows.length - 1) const Divider(height: 1, indent: 16, color: Color(0xFFF8FAFC)),
            ]);
          }),
        ]));
  }

  Widget _buildSecurityNote(BuildContext context) {
    return Container(padding: const EdgeInsets.all(14),
        decoration: BoxDecoration(color: const Color(0xFFFFFBEB), borderRadius: BorderRadius.circular(14), border: Border.all(color: const Color(0xFFFDE68A))),
        child: Row(crossAxisAlignment: CrossAxisAlignment.start, children: [
          const Icon(Icons.lock_outline_rounded, size: 16, color: Color(0xFF92400E)), const SizedBox(width: 10),
          Expanded(child: Text(_t(context, 'securityNote'), style: const TextStyle(fontSize: 11, color: Color(0xFF92400E), height: 1.5))),
        ]));
  }
}

// import 'package:flutter/material.dart';
// import 'package:flutter/services.dart';
// import 'package:qr_flutter/qr_flutter.dart';
// import '../../models/voucher_model.dart';
// import '../../widgets/voucher_status_chip.dart';
// import '../../widgets/voucher_category_chip.dart';
// import 'voucher_qr_screen.dart';
//
// class VoucherDetailScreen extends StatelessWidget {
//   final VoucherModel voucher;
//   const VoucherDetailScreen({super.key, required this.voucher});
//
//   static const _primary    = Color(0xFF1B4332);
//   static const _surface    = Color(0xFFF4F7F5);
//   static const _cardBorder = Color(0xFFE2E8F0);
//
//   @override
//   Widget build(BuildContext context) {
//     // ── DEBUG: print raw voucher fields so you can see what came from API ──
//     debugPrint('=== VoucherDetail ===');
//     debugPrint('id:          ${voucher.id}');
//     debugPrint('voucherCode: "${voucher.voucherCode}"');
//     debugPrint('status:      "${voucher.status}"');
//     debugPrint('isActive:    ${voucher.isActive}');
//     debugPrint('====================');
//
//     return Scaffold(
//       backgroundColor: _surface,
//       appBar: AppBar(
//         backgroundColor: _primary,
//         foregroundColor: Colors.white,
//         title: Text(
//           voucher.categoryLabel,
//           style: const TextStyle(
//               fontWeight: FontWeight.w900, fontSize: 18, letterSpacing: -0.5),
//         ),
//         actions: [
//           if (voucher.isActive)
//             IconButton(
//               icon: const Icon(Icons.fullscreen_rounded),
//               tooltip: 'Full screen QR',
//               onPressed: () => Navigator.push(
//                 context,
//                 MaterialPageRoute(
//                     builder: (_) => VoucherQrScreen(voucher: voucher)),
//               ),
//             ),
//         ],
//       ),
//       body: SingleChildScrollView(
//         padding: const EdgeInsets.all(20),
//         child: Column(
//           crossAxisAlignment: CrossAxisAlignment.start,
//           children: [
//             _buildHeaderCard(),
//             const SizedBox(height: 16),
//
//             // Always show QR section — with a clear error if code is missing
//             _buildQrCard(context),
//
//             const SizedBox(height: 16),
//             if (voucher.isRedeemed) _buildRedeemedCard(),
//             if (voucher.isLocked)   _buildLockedCard(),
//             if (voucher.isExpired)  _buildExpiredCard(),
//             const SizedBox(height: 16),
//             _buildInfoCard(),
//             const SizedBox(height: 16),
//             _buildSecurityNote(),
//             const SizedBox(height: 24),
//             if (voucher.isActive)
//               SizedBox(
//                 width: double.infinity,
//                 child: ElevatedButton.icon(
//                   onPressed: () => Navigator.push(
//                     context,
//                     MaterialPageRoute(
//                         builder: (_) => VoucherQrScreen(voucher: voucher)),
//                   ),
//                   icon: const Icon(Icons.qr_code_2_rounded, size: 22),
//                   label: const Text('Show QR to Merchant'),
//                   style: ElevatedButton.styleFrom(
//                     backgroundColor: _primary,
//                     foregroundColor: Colors.white,
//                     padding: const EdgeInsets.symmetric(vertical: 16),
//                     shape: RoundedRectangleBorder(
//                         borderRadius: BorderRadius.circular(16)),
//                     textStyle: const TextStyle(
//                         fontSize: 15, fontWeight: FontWeight.bold),
//                   ),
//                 ),
//               ),
//           ],
//         ),
//       ),
//     );
//   }
//
//   Widget _buildHeaderCard() {
//     return Container(
//       width: double.infinity,
//       padding: const EdgeInsets.all(20),
//       decoration: BoxDecoration(
//         gradient: const LinearGradient(
//           colors: [Color(0xFF1B4332), Color(0xFF2D6A4F)],
//           begin: Alignment.topLeft,
//           end: Alignment.bottomRight,
//         ),
//         borderRadius: BorderRadius.circular(20),
//       ),
//       child: Column(
//         crossAxisAlignment: CrossAxisAlignment.start,
//         children: [
//           Row(
//             children: [
//               Expanded(
//                 child: Column(
//                   crossAxisAlignment: CrossAxisAlignment.start,
//                   children: [
//                     Text(
//                       voucher.productName,
//                       style: const TextStyle(
//                           color: Colors.white,
//                           fontSize: 16,
//                           fontWeight: FontWeight.w900,
//                           letterSpacing: -0.3),
//                     ),
//                     const SizedBox(height: 4),
//                     VoucherCategoryChip(category: voucher.productCategory),
//                   ],
//                 ),
//               ),
//               VoucherStatusChip(status: voucher.status, fontSize: 11),
//             ],
//           ),
//           const SizedBox(height: 16),
//           Text(
//             '${voucher.amountEtb.toStringAsFixed(2)} ETB',
//             style: const TextStyle(
//                 color: Colors.white,
//                 fontSize: 36,
//                 fontWeight: FontWeight.w900,
//                 fontFamily: 'monospace',
//                 letterSpacing: -1),
//           ),
//           const SizedBox(height: 2),
//           Text(
//             'Sequence #${voucher.sequenceOrder}  |  ${voucher.categoryLabel} only',
//             style: const TextStyle(color: Colors.white60, fontSize: 11),
//           ),
//         ],
//       ),
//     );
//   }
//
//   Widget _buildQrCard(BuildContext context) {
//     final code = voucher.voucherCode;
//     final hasCode = code.isNotEmpty;
//
//     return GestureDetector(
//       onTap: hasCode && voucher.isActive
//           ? () => Navigator.push(
//                 context,
//                 MaterialPageRoute(
//                     builder: (_) => VoucherQrScreen(voucher: voucher)),
//               )
//           : null,
//       child: Container(
//         width: double.infinity,
//         padding: const EdgeInsets.all(24),
//         decoration: BoxDecoration(
//           color: Colors.white,
//           borderRadius: BorderRadius.circular(20),
//           border: Border.all(color: _cardBorder),
//         ),
//         child: Column(
//           children: [
//             // Status badge above QR
//             Container(
//               padding:
//                   const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
//               decoration: BoxDecoration(
//                 color: voucher.isActive
//                     ? const Color(0xFFDCFCE7)
//                     : const Color(0xFFFEF9C3),
//                 borderRadius: BorderRadius.circular(20),
//               ),
//               child: Text(
//                 voucher.isActive
//                     ? 'ACTIVE — ready to redeem'
//                     : 'Status: ${voucher.statusLabel}',
//                 style: TextStyle(
//                   fontSize: 11,
//                   fontWeight: FontWeight.w800,
//                   color: voucher.isActive
//                       ? const Color(0xFF15803D)
//                       : const Color(0xFF92400E),
//                 ),
//               ),
//             ),
//
//             const SizedBox(height: 20),
//
//             // QR or error state
//             if (hasCode)
//               Container(
//                 padding: const EdgeInsets.all(12),
//                 decoration: BoxDecoration(
//                   color: Colors.white,
//                   borderRadius: BorderRadius.circular(12),
//                   border: Border.all(color: _cardBorder, width: 1.5),
//                 ),
//                 child: QrImageView(
//                   data: code,
//                   version: QrVersions.auto,
//                   size: 200,
//                   backgroundColor: Colors.white,
//                   eyeStyle: const QrEyeStyle(
//                     eyeShape: QrEyeShape.square,
//                     color: Color(0xFF1B4332),
//                   ),
//                   dataModuleStyle: const QrDataModuleStyle(
//                     dataModuleShape: QrDataModuleShape.square,
//                     color: Color(0xFF1B4332),
//                   ),
//                 ),
//               )
//             else
//               // Visible error — tells you exactly what went wrong
//               Container(
//                 width: 200,
//                 height: 200,
//                 decoration: BoxDecoration(
//                   color: const Color(0xFFFEF2F2),
//                   borderRadius: BorderRadius.circular(12),
//                   border: Border.all(color: const Color(0xFFFECACA)),
//                 ),
//                 child: const Column(
//                   mainAxisAlignment: MainAxisAlignment.center,
//                   children: [
//                     Icon(Icons.qr_code_rounded,
//                         size: 48, color: Color(0xFFEF4444)),
//                     SizedBox(height: 10),
//                     Text(
//                       'No QR data',
//                       style: TextStyle(
//                           fontWeight: FontWeight.w800,
//                           color: Color(0xFF991B1B)),
//                     ),
//                     SizedBox(height: 6),
//                     Padding(
//                       padding: EdgeInsets.symmetric(horizontal: 16),
//                       child: Text(
//                         'voucherCode is empty.\nCheck API field name.',
//                         textAlign: TextAlign.center,
//                         style: TextStyle(
//                             fontSize: 11, color: Color(0xFFDC2626)),
//                       ),
//                     ),
//                   ],
//                 ),
//               ),
//
//             const SizedBox(height: 8),
//
//             if (hasCode && voucher.isActive)
//               const Text(
//                 'Tap to expand to full screen',
//                 style: TextStyle(fontSize: 11, color: Color(0xFF94A3B8)),
//               ),
//
//             const SizedBox(height: 16),
//
//             // Code pill — always visible so you can see the raw value
//             GestureDetector(
//               onTap: hasCode
//                   ? () => Clipboard.setData(
//                       ClipboardData(text: voucher.voucherCode))
//                   : null,
//               child: Container(
//                 padding: const EdgeInsets.symmetric(
//                     horizontal: 16, vertical: 10),
//                 decoration: BoxDecoration(
//                   color: const Color(0xFFF1F5F9),
//                   borderRadius: BorderRadius.circular(10),
//                 ),
//                 child: Row(
//                   mainAxisSize: MainAxisSize.min,
//                   children: [
//                     Text(
//                       hasCode ? voucher.voucherCode : 'NO CODE',
//                       style: TextStyle(
//                           fontFamily: 'monospace',
//                           fontSize: 16,
//                           fontWeight: FontWeight.w800,
//                           letterSpacing: 2,
//                           color: hasCode
//                               ? const Color(0xFF1B4332)
//                               : const Color(0xFFEF4444)),
//                     ),
//                     const SizedBox(width: 8),
//                     Icon(
//                       hasCode
//                           ? Icons.copy_rounded
//                           : Icons.warning_rounded,
//                       size: 14,
//                       color: const Color(0xFF64748B),
//                     ),
//                   ],
//                 ),
//               ),
//             ),
//
//             const SizedBox(height: 8),
//             const Text(
//               'Works offline  |  Show this to the merchant',
//               style: TextStyle(fontSize: 11, color: Color(0xFF64748B)),
//             ),
//           ],
//         ),
//       ),
//     );
//   }
//
//   Widget _buildRedeemedCard() {
//     return Container(
//       width: double.infinity,
//       padding: const EdgeInsets.all(18),
//       decoration: BoxDecoration(
//         color: const Color(0xFFF0FDF4),
//         borderRadius: BorderRadius.circular(16),
//         border: Border.all(color: const Color(0xFFBBF7D0)),
//       ),
//       child: Row(
//         children: [
//           const Icon(Icons.check_circle_rounded,
//               color: Color(0xFF16A34A), size: 28),
//           const SizedBox(width: 12),
//           Expanded(
//             child: Column(
//               crossAxisAlignment: CrossAxisAlignment.start,
//               children: [
//                 const Text('Redeemed',
//                     style: TextStyle(
//                         fontWeight: FontWeight.w800,
//                         color: Color(0xFF14532D),
//                         fontSize: 14)),
//                 if (voucher.redeemedAt != null)
//                   Text('on ${voucher.redeemedAt}',
//                       style: const TextStyle(
//                           fontSize: 11, color: Color(0xFF16A34A))),
//                 const SizedBox(height: 4),
//                 const Text('Payment released to merchant',
//                     style:
//                         TextStyle(fontSize: 11, color: Color(0xFF15803D))),
//               ],
//             ),
//           ),
//         ],
//       ),
//     );
//   }
//
//   Widget _buildLockedCard() {
//     return Container(
//       width: double.infinity,
//       padding: const EdgeInsets.all(18),
//       decoration: BoxDecoration(
//         color: const Color(0xFFEFF6FF),
//         borderRadius: BorderRadius.circular(16),
//         border: Border.all(color: const Color(0xFFBFDBFE)),
//       ),
//       child: Row(
//         children: [
//           const Icon(Icons.lock_rounded,
//               color: Color(0xFF1D4ED8), size: 28),
//           const SizedBox(width: 12),
//           Expanded(
//             child: Column(
//               crossAxisAlignment: CrossAxisAlignment.start,
//               children: [
//                 const Text('Locked',
//                     style: TextStyle(
//                         fontWeight: FontWeight.w800,
//                         color: Color(0xFF1E3A8A),
//                         fontSize: 14)),
//                 Text(
//                   'Unlocks after voucher #${voucher.sequenceOrder - 1} is redeemed',
//                   style: const TextStyle(
//                       fontSize: 11, color: Color(0xFF1D4ED8)),
//                 ),
//                 const SizedBox(height: 4),
//                 const Text('Agronomic order must be followed',
//                     style:
//                         TextStyle(fontSize: 11, color: Color(0xFF3B82F6))),
//               ],
//             ),
//           ),
//         ],
//       ),
//     );
//   }
//
//   Widget _buildExpiredCard() {
//     return Container(
//       width: double.infinity,
//       padding: const EdgeInsets.all(18),
//       decoration: BoxDecoration(
//         color: const Color(0xFFFEF2F2),
//         borderRadius: BorderRadius.circular(16),
//         border: Border.all(color: const Color(0xFFFECACA)),
//       ),
//       child: const Row(
//         children: [
//           Icon(Icons.schedule_rounded,
//               color: Color(0xFFDC2626), size: 28),
//           SizedBox(width: 12),
//           Expanded(
//             child: Column(
//               crossAxisAlignment: CrossAxisAlignment.start,
//               children: [
//                 Text('Expired',
//                     style: TextStyle(
//                         fontWeight: FontWeight.w800,
//                         color: Color(0xFF991B1B),
//                         fontSize: 14)),
//                 Text('This voucher has passed its validity date',
//                     style: TextStyle(
//                         fontSize: 11, color: Color(0xFFDC2626))),
//                 SizedBox(height: 4),
//                 Text('Contact Agri-Yield support for assistance',
//                     style: TextStyle(
//                         fontSize: 11, color: Color(0xFFEF4444))),
//               ],
//             ),
//           ),
//         ],
//       ),
//     );
//   }
//
//   Widget _buildInfoCard() {
//     final rows = [
//       ['Voucher ID',  voucher.id,                                   true],
//       ['Code',        voucher.voucherCode.isEmpty
//                         ? '⚠ EMPTY — check API'
//                         : voucher.voucherCode,                      true],
//       ['Category',    '${voucher.categoryLabel} only',              false],
//       ['Amount',      '${voucher.amountEtb.toStringAsFixed(2)} ETB',false],
//       ['Sequence',    '#${voucher.sequenceOrder}',                  false],
//       ['Valid Until', voucher.validUntil,                           false],
//       ['Created',     voucher.createdAt,                            false],
//       if (voucher.isRedeemed && voucher.redeemedAt != null)
//         ['Redeemed At', voucher.redeemedAt!,                        false],
//     ];
//
//     return Container(
//       decoration: BoxDecoration(
//         color: Colors.white,
//         borderRadius: BorderRadius.circular(20),
//         border: Border.all(color: _cardBorder),
//       ),
//       child: Column(
//         crossAxisAlignment: CrossAxisAlignment.start,
//         children: [
//           const Padding(
//             padding: EdgeInsets.fromLTRB(16, 14, 16, 0),
//             child: Text('Voucher Details',
//                 style: TextStyle(
//                     fontSize: 12,
//                     fontWeight: FontWeight.w800,
//                     color: Color(0xFF64748B),
//                     letterSpacing: 0.5)),
//           ),
//           const SizedBox(height: 8),
//           ...rows.asMap().entries.map((entry) {
//             final i      = entry.key;
//             final row    = entry.value;
//             final isMono = row[2] as bool;
//             return Column(
//               children: [
//                 Padding(
//                   padding: const EdgeInsets.symmetric(
//                       horizontal: 16, vertical: 10),
//                   child: Row(
//                     crossAxisAlignment: CrossAxisAlignment.start,
//                     children: [
//                       SizedBox(
//                         width: 100,
//                         child: Text(row[0] as String,
//                             style: const TextStyle(
//                                 fontSize: 12,
//                                 color: Color(0xFF94A3B8),
//                                 fontWeight: FontWeight.w500)),
//                       ),
//                       Expanded(
//                         child: Text(
//                           row[1] as String,
//                           style: TextStyle(
//                             fontSize: isMono ? 10 : 13,
//                             fontWeight: FontWeight.w700,
//                             color: const Color(0xFF0F291B),
//                             fontFamily: isMono ? 'monospace' : null,
//                             letterSpacing: isMono ? 0.3 : 0,
//                           ),
//                         ),
//                       ),
//                     ],
//                   ),
//                 ),
//                 if (i < rows.length - 1)
//                   const Divider(
//                       height: 1,
//                       indent: 16,
//                       color: Color(0xFFF8FAFC)),
//               ],
//             );
//           }),
//         ],
//       ),
//     );
//   }
//
//   Widget _buildSecurityNote() {
//     return Container(
//       padding: const EdgeInsets.all(14),
//       decoration: BoxDecoration(
//         color: const Color(0xFFFFFBEB),
//         borderRadius: BorderRadius.circular(14),
//         border: Border.all(color: const Color(0xFFFDE68A)),
//       ),
//       child: const Row(
//         crossAxisAlignment: CrossAxisAlignment.start,
//         children: [
//           Icon(Icons.lock_outline_rounded,
//               size: 16, color: Color(0xFF92400E)),
//           SizedBox(width: 10),
//           Expanded(
//             child: Text(
//               'This voucher is cryptographically signed. The merchant app '
//               'runs 6 security checks before releasing any payment. '
//               'Never share your voucher code with anyone other than certified merchants.',
//               style: TextStyle(
//                   fontSize: 11,
//                   color: Color(0xFF92400E),
//                   height: 1.5),
//             ),
//           ),
//         ],
//       ),
//     );
//   }
// }

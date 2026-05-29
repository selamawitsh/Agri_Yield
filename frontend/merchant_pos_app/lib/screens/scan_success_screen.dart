import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import '../models/voucher_model.dart';
import '../widgets/voucher_category_badge.dart';
import 'scanner_screen.dart';
 
class ScanSuccessScreen extends StatefulWidget {
  final VoucherRedemptionResult result;
  const ScanSuccessScreen({super.key, required this.result});
 
  @override
  State<ScanSuccessScreen> createState() => _ScanSuccessScreenState();
}
 
class _ScanSuccessScreenState extends State<ScanSuccessScreen>
    with SingleTickerProviderStateMixin {
  late final AnimationController _anim;
  late final Animation<double> _scale;
  late final Animation<double> _fade;
 
  @override
  void initState() {
    super.initState();
    // Lock orientation to portrait on this screen
    SystemChrome.setSystemUIOverlayStyle(
      const SystemUiOverlayStyle(statusBarColor: Color(0xFF1B5E20)),
    );
    _anim = AnimationController(
        vsync: this, duration: const Duration(milliseconds: 600));
    _scale = CurvedAnimation(parent: _anim, curve: Curves.elasticOut);
    _fade  = CurvedAnimation(parent: _anim, curve: Curves.easeIn);
    _anim.forward();
  }
 
  @override
  void dispose() {
    _anim.dispose();
    super.dispose();
  }
 
  @override
  Widget build(BuildContext context) {
    final r = widget.result;
    final amount = r.amountEtb?.toStringAsFixed(2) ?? '0.00';
 
    return Scaffold(
      backgroundColor: const Color(0xFF1B5E20),
      body: SafeArea(
        child: FadeTransition(
          opacity: _fade,
          child: SingleChildScrollView(
            padding: const EdgeInsets.fromLTRB(28, 40, 28, 28),
            child: Column(
              children: [
                // Animated checkmark
                ScaleTransition(
                  scale: _scale,
                  child: Container(
                    width: 100, height: 100,
                    decoration: BoxDecoration(
                      shape: BoxShape.circle,
                      color: Colors.white.withOpacity(0.15),
                      border: Border.all(color: Colors.white38, width: 2),
                    ),
                    child: const Icon(Icons.check_rounded,
                        size: 60, color: Colors.white),
                  ),
                ),
                const SizedBox(height: 20),
                const Text(
                  'Voucher Accepted!',
                  style: TextStyle(
                    fontSize: 30,
                    fontWeight: FontWeight.bold,
                    color: Colors.white,
                    letterSpacing: -0.5,
                  ),
                ),
                const SizedBox(height: 6),
                Text(
                  'Payment released to your Telebirr account',
                  style: TextStyle(
                      color: Colors.white.withOpacity(0.7), fontSize: 13),
                  textAlign: TextAlign.center,
                ),
 
                const SizedBox(height: 28),
 
                // Amount hero
                Container(
                  width: double.infinity,
                  padding: const EdgeInsets.symmetric(vertical: 20),
                  decoration: BoxDecoration(
                    color: Colors.white.withOpacity(0.12),
                    borderRadius: BorderRadius.circular(16),
                    border: Border.all(color: Colors.white24),
                  ),
                  child: Column(
                    children: [
                      Text(
                        'ETB $amount',
                        style: const TextStyle(
                          color: Colors.white,
                          fontSize: 40,
                          fontWeight: FontWeight.bold,
                          fontFamily: 'monospace',
                          letterSpacing: 1,
                        ),
                      ),
                      const SizedBox(height: 4),
                      Text(
                        'transferred via Telebirr',
                        style: TextStyle(
                            color: Colors.white.withOpacity(0.6),
                            fontSize: 12),
                      ),
                    ],
                  ),
                ),
 
                const SizedBox(height: 16),
 
                // Info card
                Container(
                  width: double.infinity,
                  padding: const EdgeInsets.all(18),
                  decoration: BoxDecoration(
                    color: Colors.white.withOpacity(0.1),
                    borderRadius: BorderRadius.circular(16),
                    border: Border.all(color: Colors.white24),
                  ),
                  child: Column(
                    children: [
                      _row('Farmer', r.farmerName ?? '—'),
                      _divider(),
                      _row('Product', r.productDescription ?? '—'),
                      _divider(),
                      Row(
                        mainAxisAlignment: MainAxisAlignment.spaceBetween,
                        children: [
                          Text('Category',
                              style: TextStyle(
                                  color: Colors.white.withOpacity(0.7),
                                  fontSize: 13)),
                          if (r.productCategory != null)
                            VoucherCategoryBadge(
                                category: r.productCategory!, fontSize: 11)
                          else
                            const Text('—',
                                style: TextStyle(
                                    color: Colors.white, fontSize: 14)),
                        ],
                      ),
                      _divider(),
                      _row('Voucher ID',
                          r.voucherId ?? '—',
                          mono: true, small: true),
                      _divider(),
                      _row('Payment Ref',
                          r.paymentReference ?? '—',
                          mono: true, small: true),
                    ],
                  ),
                ),
 
                const SizedBox(height: 16),
 
                // Validation passed chip
                Container(
                  padding: const EdgeInsets.symmetric(
                      horizontal: 16, vertical: 8),
                  decoration: BoxDecoration(
                    color: Colors.white.withOpacity(0.1),
                    borderRadius: BorderRadius.circular(24),
                    border: Border.all(color: Colors.white24),
                  ),
                  child: const Row(
                    mainAxisSize: MainAxisSize.min,
                    children: [
                      Icon(Icons.verified_user,
                          color: Colors.greenAccent, size: 16),
                      SizedBox(width: 6),
                      Text(
                        '6/6 security checks passed',
                        style: TextStyle(
                            color: Colors.white70, fontSize: 12),
                      ),
                    ],
                  ),
                ),
 
                const SizedBox(height: 32),
 
                // CTA buttons
                SizedBox(
                  width: double.infinity,
                  child: ElevatedButton.icon(
                    onPressed: () => Navigator.pushReplacement(
                      context,
                      MaterialPageRoute(
                          builder: (_) => const ScannerScreen()),
                    ),
                    icon: const Icon(Icons.qr_code_scanner,
                        color: Color(0xFF1B5E20)),
                    label: const Text(
                      'Scan Next Voucher',
                      style: TextStyle(
                          color: Color(0xFF1B5E20),
                          fontSize: 16,
                          fontWeight: FontWeight.bold),
                    ),
                    style: ElevatedButton.styleFrom(
                      backgroundColor: Colors.white,
                      padding: const EdgeInsets.symmetric(vertical: 15),
                      shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(12)),
                    ),
                  ),
                ),
 
                const SizedBox(height: 12),
 
                SizedBox(
                  width: double.infinity,
                  child: OutlinedButton(
                    onPressed: () => Navigator.popUntil(
                        context, ModalRoute.withName('/home')),
                    style: OutlinedButton.styleFrom(
                      side: const BorderSide(color: Colors.white38),
                      padding: const EdgeInsets.symmetric(vertical: 14),
                      shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(12)),
                    ),
                    child: const Text('Back to Home',
                        style:
                            TextStyle(color: Colors.white70, fontSize: 15)),
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
 
  Widget _row(String label, String value,
      {bool mono = false, bool small = false}) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        Text(label,
            style: TextStyle(
                color: Colors.white.withOpacity(0.7), fontSize: 13)),
        Flexible(
          child: Text(
            value,
            textAlign: TextAlign.right,
            style: TextStyle(
              color: Colors.white,
              fontSize: small ? 11 : 14,
              fontWeight: FontWeight.w600,
              fontFamily: mono ? 'monospace' : null,
              letterSpacing: mono ? 0.5 : 0,
            ),
            overflow: TextOverflow.ellipsis,
          ),
        ),
      ],
    );
  }
 
  Widget _divider() => Divider(
      color: Colors.white.withOpacity(0.15), height: 16, thickness: 0.5);
}

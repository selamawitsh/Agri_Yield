import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'scanner_screen.dart';
 
class ScanRejectionScreen extends StatefulWidget {
  final String reason;
  final String? fraudSeverity;
  final String? voucherId;
 
  const ScanRejectionScreen({
    super.key,
    required this.reason,
    this.fraudSeverity,
    this.voucherId,
  });
 
  @override
  State<ScanRejectionScreen> createState() => _ScanRejectionScreenState();
}
 
class _ScanRejectionScreenState extends State<ScanRejectionScreen>
    with SingleTickerProviderStateMixin {
  late final AnimationController _anim;
  late final Animation<double> _shake;
 
  @override
  void initState() {
    super.initState();
    SystemChrome.setSystemUIOverlayStyle(
      const SystemUiOverlayStyle(statusBarColor: Color(0xFF7F0000)),
    );
    _anim = AnimationController(
        vsync: this, duration: const Duration(milliseconds: 500));
    _shake = Tween<double>(begin: 0, end: 1).animate(
        CurvedAnimation(parent: _anim, curve: Curves.elasticOut));
    _anim.forward();
  }
 
  @override
  void dispose() {
    _anim.dispose();
    super.dispose();
  }
 
  static const _reasons = {
    'INVALID_SIGNATURE': _RejectionInfo(
      title: 'Invalid QR Code',
      description:
          'This QR code is invalid or may have been tampered with. The cryptographic signature failed verification.',
      guidance:
          'Do NOT accept this voucher or dispense any product. Report this incident to Agri-Yield admin immediately.',
      icon: Icons.gpp_bad,
    ),
    'DUPLICATE_SCAN': _RejectionInfo(
      title: 'Already Redeemed',
      description:
          'This voucher has already been scanned and redeemed. A duplicate scan attempt has been logged.',
      guidance:
          'Ask the farmer to show a different, unredeemed voucher. This incident has been flagged.',
      icon: Icons.content_copy,
    ),
    'CATEGORY_MISMATCH': _RejectionInfo(
      title: 'Wrong Product Category',
      description:
          'This voucher is locked to a specific product category that does not match your certified categories.',
      guidance:
          'Direct the farmer to a merchant certified for this product type.',
      icon: Icons.category,
    ),
    'MERCHANT_TOO_FAR': _RejectionInfo(
      title: 'Store Too Far From Farm',
      description:
          'Your store location is more than 50km from the farmer\'s registered farm.',
      guidance:
          'The farmer must use a merchant closer to their farm. This is an anti-fraud geographic control.',
      icon: Icons.location_off,
    ),
    'VOUCHER_EXPIRED': _RejectionInfo(
      title: 'Voucher Expired',
      description:
          'This voucher\'s validity period has passed. It can no longer be redeemed.',
      guidance:
          'Advise the farmer to contact Agri-Yield support to request a replacement.',
      icon: Icons.schedule,
    ),
    'PRECEDING_VOUCHER_NOT_REDEEMED': _RejectionInfo(
      title: 'Out of Sequence',
      description:
          'Vouchers must be redeemed in a specific agronomic order. An earlier voucher in this sequence has not been redeemed yet.',
      guidance:
          'The farmer must first redeem the earlier vouchers in the correct sequence before this one becomes active.',
      icon: Icons.format_list_numbered,
    ),
    'NETWORK_ERROR': _RejectionInfo(
      title: 'Connection Error',
      description:
          'Could not reach the Agri-Yield server to validate this voucher.',
      guidance:
          'Do NOT dispense any product. Try again once your internet connection is restored.',
      icon: Icons.wifi_off,
    ),
  };
 
  @override
  Widget build(BuildContext context) {
    final info = _reasons[widget.reason] ??
        _RejectionInfo(
          title: 'Validation Failed',
          description: widget.reason.isNotEmpty
              ? widget.reason
              : 'Voucher validation failed.',
          guidance: 'Contact Agri-Yield support if the issue persists.',
          icon: Icons.error_outline,
        );
 
    final isCritical = widget.fraudSeverity == 'CRITICAL' ||
        widget.fraudSeverity == 'HIGH' ||
        widget.reason == 'DUPLICATE_SCAN' ||
        widget.reason == 'INVALID_SIGNATURE';
 
    return Scaffold(
      backgroundColor: const Color(0xFFC62828),
      body: SafeArea(
        child: AnimatedBuilder(
          animation: _shake,
          builder: (_, child) => child!,
          child: SingleChildScrollView(
            padding: const EdgeInsets.fromLTRB(28, 40, 28, 28),
            child: Column(
              children: [
                // X icon with pulse
                Container(
                  width: 100, height: 100,
                  decoration: BoxDecoration(
                    shape: BoxShape.circle,
                    color: Colors.white.withOpacity(0.15),
                    border: Border.all(color: Colors.white38, width: 2),
                  ),
                  child: Icon(info.icon, size: 52, color: Colors.white),
                ),
 
                const SizedBox(height: 20),
 
                Text(
                  info.title,
                  style: const TextStyle(
                    fontSize: 28,
                    fontWeight: FontWeight.bold,
                    color: Colors.white,
                    letterSpacing: -0.5,
                  ),
                  textAlign: TextAlign.center,
                ),
 
                if (widget.voucherId != null) ...[
                  const SizedBox(height: 6),
                  Text(
                    widget.voucherId!,
                    style: TextStyle(
                        color: Colors.white.withOpacity(0.5),
                        fontSize: 11,
                        fontFamily: 'monospace'),
                  ),
                ],
 
                const SizedBox(height: 28),
 
                // Reason card
                Container(
                  width: double.infinity,
                  padding: const EdgeInsets.all(18),
                  decoration: BoxDecoration(
                    color: Colors.white.withOpacity(0.12),
                    borderRadius: BorderRadius.circular(16),
                    border: Border.all(color: Colors.white24),
                  ),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      const Text(
                        'REASON',
                        style: TextStyle(
                          color: Colors.white54,
                          fontSize: 10,
                          fontWeight: FontWeight.w700,
                          letterSpacing: 1.5,
                        ),
                      ),
                      const SizedBox(height: 8),
                      Text(
                        info.description,
                        style: const TextStyle(
                            color: Colors.white,
                            fontSize: 15,
                            height: 1.5),
                      ),
                    ],
                  ),
                ),
 
                const SizedBox(height: 12),
 
                // Guidance card
                Container(
                  width: double.infinity,
                  padding: const EdgeInsets.all(18),
                  decoration: BoxDecoration(
                    color: Colors.black.withOpacity(0.2),
                    borderRadius: BorderRadius.circular(16),
                    border: Border.all(color: Colors.white24),
                  ),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      const Row(
                        children: [
                          Icon(Icons.tips_and_updates,
                              color: Colors.amber, size: 16),
                          SizedBox(width: 6),
                          Text(
                            'WHAT TO DO',
                            style: TextStyle(
                              color: Colors.amber,
                              fontSize: 10,
                              fontWeight: FontWeight.w700,
                              letterSpacing: 1.5,
                            ),
                          ),
                        ],
                      ),
                      const SizedBox(height: 8),
                      Text(
                        info.guidance,
                        style: const TextStyle(
                            color: Colors.white70,
                            fontSize: 14,
                            height: 1.5),
                      ),
                    ],
                  ),
                ),
 
                const SizedBox(height: 16),
 
                // Critical fraud banner
                if (isCritical)
                  Container(
                    width: double.infinity,
                    padding: const EdgeInsets.all(14),
                    decoration: BoxDecoration(
                      color: Colors.black54,
                      borderRadius: BorderRadius.circular(12),
                      border: Border.all(
                          color: Colors.redAccent.withOpacity(0.6)),
                    ),
                    child: const Row(
                      children: [
                        Icon(Icons.warning_amber_rounded,
                            color: Colors.orangeAccent, size: 20),
                        SizedBox(width: 10),
                        Expanded(
                          child: Text(
                            'FRAUD ALERT: This incident has been reported to the platform admin.',
                            style: TextStyle(
                                color: Colors.white,
                                fontSize: 12,
                                fontWeight: FontWeight.w600),
                          ),
                        ),
                      ],
                    ),
                  ),
 
                const SizedBox(height: 16),
 
                // No-dispense reminder
                Container(
                  width: double.infinity,
                  padding: const EdgeInsets.symmetric(
                      horizontal: 16, vertical: 10),
                  decoration: BoxDecoration(
                    color: Colors.white.withOpacity(0.08),
                    borderRadius: BorderRadius.circular(24),
                    border: Border.all(color: Colors.white24),
                  ),
                  child: const Row(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      Icon(Icons.block, color: Colors.white54, size: 15),
                      SizedBox(width: 6),
                      Text(
                        'Do NOT dispense any product for this voucher',
                        style:
                            TextStyle(color: Colors.white70, fontSize: 12),
                      ),
                    ],
                  ),
                ),
 
                const SizedBox(height: 32),
 
                SizedBox(
                  width: double.infinity,
                  child: ElevatedButton.icon(
                    onPressed: () => Navigator.pushReplacement(
                      context,
                      MaterialPageRoute(
                          builder: (_) => const ScannerScreen()),
                    ),
                    icon: const Icon(Icons.qr_code_scanner,
                        color: Color(0xFFC62828)),
                    label: const Text(
                      'Back to Scanner',
                      style: TextStyle(
                          color: Color(0xFFC62828),
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
}
 
class _RejectionInfo {
  final String title;
  final String description;
  final String guidance;
  final IconData icon;
  const _RejectionInfo({
    required this.title,
    required this.description,
    required this.guidance,
    required this.icon,
  });
}

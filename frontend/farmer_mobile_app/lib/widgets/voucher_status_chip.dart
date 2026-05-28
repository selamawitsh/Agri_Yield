import 'package:flutter/material.dart';

class VoucherStatusChip extends StatelessWidget {
  final String status;
  final double fontSize;

  const VoucherStatusChip({
    super.key,
    required this.status,
    this.fontSize = 10,
  });

  static const _config = {
    'ACTIVE':    {'label': 'Active',    'bg': Color(0xFFDCFCE7), 'fg': Color(0xFF14532D)},
    'REDEEMED':  {'label': 'Redeemed', 'bg': Color(0xFFF1F5F9), 'fg': Color(0xFF475569)},
    'GENERATED': {'label': 'Locked',   'bg': Color(0xFFEFF6FF), 'fg': Color(0xFF1E40AF)},
    'EXPIRED':   {'label': 'Expired',  'bg': Color(0xFFFEF2F2), 'fg': Color(0xFF991B1B)},
    'CANCELLED': {'label': 'Cancelled','bg': Color(0xFFFEF2F2), 'fg': Color(0xFF991B1B)},
    'REJECTED':  {'label': 'Rejected', 'bg': Color(0xFFFFF7ED), 'fg': Color(0xFF9A3412)},
  };

  @override
  Widget build(BuildContext context) {
    final cfg = _config[status] ??
        {'label': status, 'bg': const Color(0xFFF1F5F9), 'fg': const Color(0xFF475569)};
    return Container(
      padding: EdgeInsets.symmetric(horizontal: fontSize, vertical: fontSize * 0.4),
      decoration: BoxDecoration(
        color: cfg['bg'] as Color,
        borderRadius: BorderRadius.circular(20),
      ),
      child: Text(
        cfg['label'] as String,
        style: TextStyle(
          color: cfg['fg'] as Color,
          fontSize: fontSize,
          fontWeight: FontWeight.w700,
          letterSpacing: 0.2,
        ),
      ),
    );
  }
}

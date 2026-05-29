import 'package:flutter/material.dart';
 
class VoucherCategoryBadge extends StatelessWidget {
  final String category;
  final double fontSize;
 
  const VoucherCategoryBadge({
    super.key,
    required this.category,
    this.fontSize = 12,
  });
 
  static const _config = {
    'SEED':       {'label': '🌾 SEED',       'bg': Color(0xFF9FE1CB), 'fg': Color(0xFF085041)},
    'FERTILIZER': {'label': '🪣 FERTILIZER', 'bg': Color(0xFFB5D4F4), 'fg': Color(0xFF0C447C)},
    'PESTICIDE':  {'label': '🛡️ PESTICIDE',  'bg': Color(0xFFF5C4B3), 'fg': Color(0xFF993C1D)},
    'TOOL':       {'label': '🔧 TOOL',       'bg': Color(0xFFD3D1C7), 'fg': Color(0xFF2C2C2A)},
    'OTHER':      {'label': '📦 OTHER',      'bg': Color(0xFFD3D1C7), 'fg': Color(0xFF2C2C2A)},
  };
 
  @override
  Widget build(BuildContext context) {
    final cfg = _config[category.toUpperCase()] ?? _config['OTHER']!;
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
      decoration: BoxDecoration(
        color: cfg['bg'] as Color,
        borderRadius: BorderRadius.circular(20),
      ),
      child: Text(
        cfg['label'] as String,
        style: TextStyle(
          color: cfg['fg'] as Color,
          fontSize: fontSize,
          fontWeight: FontWeight.w600,
          letterSpacing: 0.3,
        ),
      ),
    );
  }
}

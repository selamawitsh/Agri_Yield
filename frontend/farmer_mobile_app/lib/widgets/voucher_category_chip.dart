import 'package:flutter/material.dart';

class VoucherCategoryChip extends StatelessWidget {
  final String category;

  const VoucherCategoryChip({super.key, required this.category});

  static const _config = {
    'SEED':       {'emoji': '🌾', 'bg': Color(0xFFD1FAE5), 'fg': Color(0xFF065F46)},
    'FERTILIZER': {'emoji': '🪣', 'bg': Color(0xFFDBEAFE), 'fg': Color(0xFF1E3A8A)},
    'PESTICIDE':  {'emoji': '🛡️', 'bg': Color(0xFFFFEDD5), 'fg': Color(0xFF9A3412)},
    'TOOL':       {'emoji': '🔧', 'bg': Color(0xFFF1F5F9), 'fg': Color(0xFF334155)},
    'OTHER':      {'emoji': '📦', 'bg': Color(0xFFF1F5F9), 'fg': Color(0xFF334155)},
  };

  @override
  Widget build(BuildContext context) {
    final cfg = _config[category] ?? _config['OTHER']!;
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
      decoration: BoxDecoration(
        color: cfg['bg'] as Color,
        borderRadius: BorderRadius.circular(20),
      ),
      child: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          Text(cfg['emoji'] as String, style: const TextStyle(fontSize: 11)),
          const SizedBox(width: 4),
          Text(
            category,
            style: TextStyle(
              color: cfg['fg'] as Color,
              fontSize: 10,
              fontWeight: FontWeight.w700,
              letterSpacing: 0.3,
            ),
          ),
        ],
      ),
    );
  }
}

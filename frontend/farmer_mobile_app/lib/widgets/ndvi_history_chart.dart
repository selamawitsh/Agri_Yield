import 'package:flutter/material.dart';
import '../models/ndvi_reading_model.dart';

/// Simple bar chart for NDVI time series (no extra chart package).
class NdviHistoryChart extends StatelessWidget {
  final List<NdviReadingModel> readings;

  const NdviHistoryChart({super.key, required this.readings});

  @override
  Widget build(BuildContext context) {
    if (readings.isEmpty) {
      return const SizedBox(
        height: 120,
        child: Center(
          child: Text('No NDVI readings yet',
              style: TextStyle(color: Colors.grey)),
        ),
      );
    }

    final sorted = List<NdviReadingModel>.from(readings)
      ..sort((a, b) => a.recordedDate.compareTo(b.recordedDate));
    final display = sorted.length > 12
        ? sorted.sublist(sorted.length - 12)
        : sorted;

    final maxNdvi = display
        .map((r) => r.ndviValue)
        .reduce((a, b) => a > b ? a : b)
        .clamp(0.1, 1.0);

    return SizedBox(
      height: 140,
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.end,
        children: display.map((r) {
          final h = (r.ndviValue / maxNdvi * 100).clamp(8.0, 100.0);
          return Expanded(
            child: Padding(
              padding: const EdgeInsets.symmetric(horizontal: 2),
              child: Column(
                mainAxisAlignment: MainAxisAlignment.end,
                children: [
                  Text(
                    r.ndviValue.toStringAsFixed(2),
                    style: const TextStyle(fontSize: 8, color: Colors.grey),
                  ),
                  const SizedBox(height: 2),
                  Container(
                    height: h,
                    decoration: BoxDecoration(
                      color: r.healthColor.withOpacity(0.85),
                      borderRadius: BorderRadius.circular(4),
                    ),
                  ),
                  const SizedBox(height: 4),
                  Text(
                    _shortDate(r.recordedDate),
                    style: const TextStyle(fontSize: 8, color: Colors.grey),
                    maxLines: 1,
                    overflow: TextOverflow.ellipsis,
                  ),
                ],
              ),
            ),
          );
        }).toList(),
      ),
    );
  }

  String _shortDate(String date) {
    if (date.length >= 10) return date.substring(5);
    return date;
  }
}

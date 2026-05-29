import 'package:flutter/material.dart';
import 'package:connectivity_plus/connectivity_plus.dart';
 
class OfflineBanner extends StatefulWidget {
  const OfflineBanner({super.key});
 
  @override
  State<OfflineBanner> createState() => _OfflineBannerState();
}
 
class _OfflineBannerState extends State<OfflineBanner> {
  bool _isOffline = false;
 
  @override
  void initState() {
    super.initState();
    Connectivity().onConnectivityChanged.listen((result) {
      if (mounted) {
        setState(() => _isOffline = result == ConnectivityResult.none);
      }
    });
  }
 
  @override
  Widget build(BuildContext context) {
    if (!_isOffline) return const SizedBox.shrink();
    return Container(
      width: double.infinity,
      padding: const EdgeInsets.symmetric(vertical: 8, horizontal: 16),
      color: const Color(0xFFC62828),
      child: const Row(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Icon(Icons.wifi_off, color: Colors.white, size: 16),
          SizedBox(width: 8),
          Text(
            'No connection — voucher scanning disabled',
            style: TextStyle(color: Colors.white, fontSize: 13),
          ),
        ],
      ),
    );
  }
}

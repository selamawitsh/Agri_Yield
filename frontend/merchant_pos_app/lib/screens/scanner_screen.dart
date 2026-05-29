import 'package:flutter/material.dart';
import 'package:mobile_scanner/mobile_scanner.dart';
import 'package:connectivity_plus/connectivity_plus.dart';
import 'package:audioplayers/audioplayers.dart';
import '../services/voucher_service.dart';
import '../models/voucher_model.dart';
import '../widgets/offline_banner.dart';
import 'scan_success_screen.dart';
import 'scan_rejection_screen.dart';
 
class ScannerScreen extends StatefulWidget {
  const ScannerScreen({super.key});
 
  @override
  State<ScannerScreen> createState() => _ScannerScreenState();
}
 
class _ScannerScreenState extends State<ScannerScreen>
    with WidgetsBindingObserver {
  final _voucherService = VoucherService();
  final _controller = MobileScannerController();
  final _manualController = TextEditingController();
  final _audioPlayer = AudioPlayer();
 
  bool _processing = false;
  bool _torchOn = false;
  bool _isOffline = false;
 
  // Corner animation for the scan frame
  late final AnimationController _pulseController;
 
  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addObserver(this);
    _checkConnectivity();
    Connectivity().onConnectivityChanged.listen((result) {
      if (mounted) {
        setState(() => _isOffline = result == ConnectivityResult.none);
      }
    });
  }
 
  Future<void> _checkConnectivity() async {
    final result = await Connectivity().checkConnectivity();
    if (mounted) setState(() => _isOffline = result == ConnectivityResult.none);
  }
 
  @override
  void dispose() {
    WidgetsBinding.instance.removeObserver(this);
    _controller.dispose();
    _manualController.dispose();
    _audioPlayer.dispose();
    super.dispose();
  }
 
  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    if (state == AppLifecycleState.resumed) _controller.start();
    if (state == AppLifecycleState.paused)  _controller.stop();
  }
 
  Future<void> _handleScan(String qrPayload) async {
    if (_processing) return;
 
    // SRS §7.6: Never process vouchers offline
    if (_isOffline) {
      _showOfflineDialog();
      return;
    }
 
    setState(() => _processing = true);
    _controller.stop();
 
    final result = await _voucherService.validateAndRedeem(qrPayload);
 
    if (!mounted) return;
 
    if (result.success) {
      await _audioPlayer.play(AssetSource('sounds/success.mp3'));
      await Navigator.pushReplacement(
        context,
        MaterialPageRoute(
          builder: (_) => ScanSuccessScreen(result: result),
        ),
      );
    } else {
      await _audioPlayer.play(AssetSource('sounds/error.mp3'));
      await Navigator.pushReplacement(
        context,
        MaterialPageRoute(
          builder: (_) => ScanRejectionScreen(
            reason: result.rejectionReason ?? result.message,
            fraudSeverity: result.fraudSeverity,
            voucherId: result.voucherId,
          ),
        ),
      );
    }
 
    if (mounted) {
      setState(() => _processing = false);
      _controller.start();
    }
  }
 
  void _showOfflineDialog() {
    showDialog(
      context: context,
      builder: (_) => AlertDialog(
        title: const Row(
          children: [
            Icon(Icons.wifi_off, color: Color(0xFFC62828)),
            SizedBox(width: 8),
            Text('No Connection'),
          ],
        ),
        content: const Text(
          'Vouchers cannot be processed offline.\n\n'
          'Do NOT dispense any product until the scan is confirmed by the server.\n\n'
          'Please ask the farmer to return when you have internet connectivity.',
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('OK', style: TextStyle(color: Colors.orange)),
          ),
        ],
      ),
    );
  }
 
  void _showManualEntry() {
    _manualController.clear();
    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      backgroundColor: Colors.white,
      shape: const RoundedRectangleBorder(
        borderRadius: BorderRadius.vertical(top: Radius.circular(20)),
      ),
      builder: (ctx) => Padding(
        padding: EdgeInsets.only(
          bottom: MediaQuery.of(ctx).viewInsets.bottom + 24,
          left: 24, right: 24, top: 24,
        ),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                const Icon(Icons.keyboard, color: Colors.orange),
                const SizedBox(width: 8),
                const Text('Enter Voucher Code',
                    style:
                        TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
                const Spacer(),
                IconButton(
                  icon: const Icon(Icons.close),
                  onPressed: () => Navigator.pop(ctx),
                ),
              ],
            ),
            const SizedBox(height: 4),
            const Text(
              'Enter the 12-character code from the farmer\'s app',
              style: TextStyle(color: Colors.grey, fontSize: 13),
            ),
            const SizedBox(height: 16),
            TextField(
              controller: _manualController,
              decoration: InputDecoration(
                hintText: 'e.g. 8K4F-2M9P-3R7Q',
                border: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(10)),
                focusedBorder: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(10),
                  borderSide:
                      const BorderSide(color: Colors.orange, width: 2),
                ),
                prefixIcon: const Icon(Icons.confirmation_number_outlined,
                    color: Colors.orange),
              ),
              textCapitalization: TextCapitalization.characters,
              style: const TextStyle(
                  fontFamily: 'monospace',
                  fontSize: 16,
                  letterSpacing: 1.5),
            ),
            const SizedBox(height: 16),
            SizedBox(
              width: double.infinity,
              child: ElevatedButton.icon(
                onPressed: () {
                  final code = _manualController.text.trim();
                  if (code.isNotEmpty) {
                    Navigator.pop(ctx);
                    _handleScan(code);
                  }
                },
                icon: const Icon(Icons.check_circle_outline,
                    color: Colors.white),
                label: const Text('Validate Voucher',
                    style: TextStyle(color: Colors.white, fontSize: 16)),
                style: ElevatedButton.styleFrom(
                  backgroundColor: Colors.orange,
                  padding: const EdgeInsets.symmetric(vertical: 14),
                  shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(10)),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
 
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.black,
      appBar: AppBar(
        title: const Text('Scan Voucher'),
        backgroundColor: Colors.orange,
        foregroundColor: Colors.white,
        elevation: 0,
        actions: [
          IconButton(
            icon: Icon(_torchOn ? Icons.flash_on : Icons.flash_off),
            tooltip: _torchOn ? 'Torch off' : 'Torch on',
            onPressed: () {
              _controller.toggleTorch();
              setState(() => _torchOn = !_torchOn);
            },
          ),
          IconButton(
            icon: const Icon(Icons.flip_camera_ios),
            tooltip: 'Flip camera',
            onPressed: () => _controller.switchCamera(),
          ),
        ],
      ),
      body: Column(
        children: [
          const OfflineBanner(),
          Expanded(
            child: Stack(
              children: [
                // Camera feed
                MobileScanner(
                  controller: _controller,
                  onDetect: (capture) {
                    final barcode = capture.barcodes.firstOrNull;
                    if (barcode?.rawValue != null) {
                      _handleScan(barcode!.rawValue!);
                    }
                  },
                ),
 
                // Dark overlay with transparent scan window
                _buildScanOverlay(),
 
                // Corner brackets for the scan frame
                _buildScanFrame(),
 
                // Bottom controls panel
                _buildBottomPanel(),
 
                // Processing overlay
                if (_processing) _buildProcessingOverlay(),
              ],
            ),
          ),
        ],
      ),
    );
  }
 
  Widget _buildScanOverlay() {
    return CustomPaint(
      painter: _ScanOverlayPainter(),
      child: const SizedBox.expand(),
    );
  }
 
  Widget _buildScanFrame() {
    const frameSize = 260.0;
    const cornerLen = 28.0;
    const cornerWidth = 4.0;
    const color = Colors.orange;
 
    return Center(
      child: SizedBox(
        width: frameSize,
        height: frameSize,
        child: Stack(
          children: [
            // Top-left
            Positioned(top: 0, left: 0,
              child: _corner(color, cornerLen, cornerWidth,
                  top: true, left: true)),
            // Top-right
            Positioned(top: 0, right: 0,
              child: _corner(color, cornerLen, cornerWidth,
                  top: true, left: false)),
            // Bottom-left
            Positioned(bottom: 0, left: 0,
              child: _corner(color, cornerLen, cornerWidth,
                  top: false, left: true)),
            // Bottom-right
            Positioned(bottom: 0, right: 0,
              child: _corner(color, cornerLen, cornerWidth,
                  top: false, left: false)),
 
            // Scan line animation
            if (!_processing)
              _ScanLineWidget(frameSize: frameSize),
          ],
        ),
      ),
    );
  }
 
  Widget _corner(Color color, double len, double width,
      {required bool top, required bool left}) {
    return SizedBox(
      width: len,
      height: len,
      child: CustomPaint(
        painter: _CornerPainter(
            color: color, width: width, top: top, left: left),
      ),
    );
  }
 
  Widget _buildBottomPanel() {
    return Positioned(
      bottom: 0, left: 0, right: 0,
      child: Container(
        decoration: const BoxDecoration(
          color: Colors.black87,
          borderRadius: BorderRadius.vertical(top: Radius.circular(20)),
        ),
        padding: const EdgeInsets.fromLTRB(24, 20, 24, 32),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            // Handle
            Container(
              width: 36, height: 4,
              margin: const EdgeInsets.only(bottom: 16),
              decoration: BoxDecoration(
                color: Colors.white24,
                borderRadius: BorderRadius.circular(2),
              ),
            ),
            const Text(
              'Point at farmer\'s QR voucher',
              style: TextStyle(
                  color: Colors.white,
                  fontSize: 16,
                  fontWeight: FontWeight.w500),
            ),
            const SizedBox(height: 6),
            const Text(
              'Hold steady — auto-detects within 2 seconds',
              style: TextStyle(color: Colors.white54, fontSize: 12),
            ),
            const SizedBox(height: 20),
            Row(
              children: [
                Expanded(
                  child: OutlinedButton.icon(
                    onPressed: _showManualEntry,
                    icon: const Icon(Icons.keyboard,
                        color: Colors.orange, size: 18),
                    label: const Text('Manual Entry',
                        style:
                            TextStyle(color: Colors.orange, fontSize: 14)),
                    style: OutlinedButton.styleFrom(
                      side: const BorderSide(color: Colors.orange),
                      padding: const EdgeInsets.symmetric(vertical: 12),
                      shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(10)),
                    ),
                  ),
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: ElevatedButton.icon(
                    onPressed: () => Navigator.pop(context),
                    icon: const Icon(Icons.close,
                        color: Colors.white, size: 18),
                    label: const Text('Cancel',
                        style:
                            TextStyle(color: Colors.white, fontSize: 14)),
                    style: ElevatedButton.styleFrom(
                      backgroundColor: Colors.white24,
                      padding: const EdgeInsets.symmetric(vertical: 12),
                      shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(10)),
                    ),
                  ),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }
 
  Widget _buildProcessingOverlay() {
    return Container(
      color: Colors.black80,
      child: Center(
        child: Container(
          margin: const EdgeInsets.symmetric(horizontal: 40),
          padding: const EdgeInsets.all(28),
          decoration: BoxDecoration(
            color: Colors.grey[900],
            borderRadius: BorderRadius.circular(16),
            border: Border.all(color: Colors.orange.withOpacity(0.3)),
          ),
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              const CircularProgressIndicator(
                  color: Colors.orange, strokeWidth: 3),
              const SizedBox(height: 20),
              const Text('Validating Voucher',
                  style: TextStyle(
                      color: Colors.white,
                      fontSize: 18,
                      fontWeight: FontWeight.bold)),
              const SizedBox(height: 8),
              const Text(
                'Running 6-step security checks...',
                style: TextStyle(color: Colors.white54, fontSize: 13),
                textAlign: TextAlign.center,
              ),
              const SizedBox(height: 20),
              _buildValidationSteps(),
            ],
          ),
        ),
      ),
    );
  }
 
  Widget _buildValidationSteps() {
    final steps = [
      'Cryptographic signature',
      'Duplicate scan check',
      'Category verification',
      'GPS proximity',
      'Validity period',
      'Sequence unlock',
    ];
    return Column(
      children: steps.map((s) => Padding(
        padding: const EdgeInsets.symmetric(vertical: 2),
        child: Row(
          children: [
            const SizedBox(
              width: 14, height: 14,
              child: CircularProgressIndicator(
                  color: Colors.orange, strokeWidth: 2),
            ),
            const SizedBox(width: 10),
            Text(s,
                style: const TextStyle(
                    color: Colors.white54, fontSize: 11)),
          ],
        ),
      )).toList(),
    );
  }
}
 
// ── Painter: darkened overlay with transparent center hole ──
class _ScanOverlayPainter extends CustomPainter {
  @override
  void paint(Canvas canvas, Size size) {
    const holeSize = 260.0;
    final cx = size.width / 2;
    final cy = size.height / 2;
    final rect = Rect.fromCenter(
        center: Offset(cx, cy), width: holeSize, height: holeSize);
    final rrect = RRect.fromRectAndRadius(rect, const Radius.circular(12));
 
    final path = Path()
      ..addRect(Rect.fromLTWH(0, 0, size.width, size.height))
      ..addRRect(rrect)
      ..fillType = PathFillType.evenOdd;
 
    canvas.drawPath(path, Paint()..color = Colors.black.withOpacity(0.65));
  }
 
  @override
  bool shouldRepaint(_) => false;
}
 
// ── Painter: single corner bracket ──
class _CornerPainter extends CustomPainter {
  final Color color;
  final double width;
  final bool top;
  final bool left;
 
  _CornerPainter(
      {required this.color,
      required this.width,
      required this.top,
      required this.left});
 
  @override
  void paint(Canvas canvas, Size size) {
    final paint = Paint()
      ..color = color
      ..strokeWidth = width
      ..strokeCap = StrokeCap.square
      ..style = PaintingStyle.stroke;
 
    final x = left ? 0.0 : size.width;
    final y = top ? 0.0 : size.height;
    final dx = left ? size.width : -size.width;
    final dy = top ? size.height : -size.height;
 
    canvas.drawLine(Offset(x, y), Offset(x + dx, y), paint);
    canvas.drawLine(Offset(x, y), Offset(x, y + dy), paint);
  }
 
  @override
  bool shouldRepaint(_) => false;
}
 
// ── Animated scan line ──
class _ScanLineWidget extends StatefulWidget {
  final double frameSize;
  const _ScanLineWidget({required this.frameSize});
 
  @override
  State<_ScanLineWidget> createState() => _ScanLineWidgetState();
}
 
class _ScanLineWidgetState extends State<_ScanLineWidget>
    with SingleTickerProviderStateMixin {
  late final AnimationController _anim;
  late final Animation<double> _pos;
 
  @override
  void initState() {
    super.initState();
    _anim = AnimationController(
        vsync: this, duration: const Duration(seconds: 2))
      ..repeat(reverse: true);
    _pos = Tween<double>(begin: 8, end: widget.frameSize - 8).animate(
        CurvedAnimation(parent: _anim, curve: Curves.easeInOut));
  }
 
  @override
  void dispose() {
    _anim.dispose();
    super.dispose();
  }
 
  @override
  Widget build(BuildContext context) {
    return AnimatedBuilder(
      animation: _pos,
      builder: (_, __) => Positioned(
        top: _pos.value,
        left: 8,
        right: 8,
        child: Container(
          height: 2,
          decoration: BoxDecoration(
            gradient: LinearGradient(
              colors: [
                Colors.orange.withOpacity(0),
                Colors.orange,
                Colors.orange.withOpacity(0),
              ],
            ),
          ),
        ),
      ),
    );
  }
}

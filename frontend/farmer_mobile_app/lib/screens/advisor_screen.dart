import 'dart:io';
import 'package:flutter/material.dart';
import 'package:flutter_sound/flutter_sound.dart';
import 'package:just_audio/just_audio.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:path_provider/path_provider.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:dio/dio.dart';
import '../services/ai_service.dart';
import '../services/auth_service.dart';
import '../services/token_storage.dart';

class AdvisorScreen extends StatefulWidget {
  const AdvisorScreen({super.key});

  @override
  State<AdvisorScreen> createState() => _AdvisorScreenState();
}

class _AdvisorScreenState extends State<AdvisorScreen> {
  final AiService _aiService = AiService();
  final AuthService _authService = AuthService();
  final TextEditingController _textController = TextEditingController();
  final AudioPlayer _audioPlayer = AudioPlayer();
  final FlutterSoundRecorder _recorder = FlutterSoundRecorder();
  final ScrollController _scrollController = ScrollController();

  final List<Map<String, dynamic>> _messages = [];

  bool _isRecording = false;
  bool _isLoading = false;
  bool _recorderReady = false;
  bool _loadingFarms = true;
  String _selectedLanguage = 'am';
  String? _currentFarmId;
  String? _currentFarmName;
  String? _recordingPath;

  // All farms belonging to this farmer
  List<Map<String, dynamic>> _farms = [];

  static const Map<String, String> _languages = {
    'am': 'አማርኛ',
    'om': 'Afaan Oromoo',
    'ti': 'ትግርኛ',
    'en': 'English',
  };

  // Base URL — same as other services in this app
  static const String _baseUrl = 'http://10.0.2.2:8080';

  @override
  void initState() {
    super.initState();
    _initRecorder();
    _loadLanguage();
    _loadFarms();
  }

  // -------------------------------------------------------------------------
  // Load saved language preference
  // -------------------------------------------------------------------------
  Future<void> _loadLanguage() async {
    final prefs = await SharedPreferences.getInstance();
    final saved = prefs.getString('language') ?? 'am';
    if (mounted) setState(() => _selectedLanguage = saved);
  }

  // -------------------------------------------------------------------------
  // Load farms from API — auto-select first farm
  // -------------------------------------------------------------------------
  Future<void> _loadFarms() async {
    setState(() => _loadingFarms = true);

    try {
      print('================ FARM LOADING START ================');

      final token = await TokenStorage.getAccessToken();

      print('Token exists: ${token != null}');
      print('Token preview: ${token != null ? token.substring(0, 20) : "NULL"}');

      if (token == null || token.isEmpty) {
        print('No access token found');
        setState(() => _loadingFarms = false);
        return;
      }

      final dio = Dio(
        BaseOptions(
          baseUrl: _baseUrl,
          headers: {
            'Authorization': 'Bearer $token',
          },
          connectTimeout: const Duration(seconds: 10),
          receiveTimeout: const Duration(seconds: 10),
        ),
      );

      print('Calling: $_baseUrl/api/v1/farms/my');

      final response = await dio.get('/api/v1/farms/my');

      print('Status Code: ${response.statusCode}');
      print('Response Type: ${response.data.runtimeType}');
      print('Response Body:');
      print(response.data);

      if (response.statusCode == 200) {
        final data = response.data;

        List<dynamic> farmList = [];

        if (data is Map && data['data'] != null) {
          print('Detected response format: { data: [...] }');
          farmList = data['data'] as List;
        } else if (data is List) {
          print('Detected response format: [...]');
          farmList = data;
        } else {
          print('Unknown response format');
          print(data);
        }

        print('Farm count received: ${farmList.length}');

        final farms = farmList.map((f) {
          print('Farm item: $f');

          return {
            'id': f['id']?.toString() ?? '',
            'name':
            f['farmName'] ??
                f['name'] ??
                f['cropType'] ??
                'My Farm',
            'crop': f['cropType'] ?? '',
            'status': f['status'] ?? '',
          };
        }).toList();

        setState(() {
          _farms = farms;

          if (_farms.isNotEmpty) {
            final active = _farms.firstWhere(
                  (f) => [
                'ACTIVE',
                'GROWING',
                'FUNDED',
                'PLANTED',
              ].contains(f['status']),
              orElse: () => _farms.first,
            );

            _currentFarmId = active['id'];
            _currentFarmName = active['name'];

            print('Selected Farm ID: $_currentFarmId');
            print('Selected Farm Name: $_currentFarmName');
          } else {
            print('No farms returned from API');
          }

          _loadingFarms = false;
        });

        if (_currentFarmId != null) {
          final prefs = await SharedPreferences.getInstance();

          await prefs.setString(
            'current_farm_id',
            _currentFarmId!,
          );

          print('Saved farm id to SharedPreferences');
        }
      } else {
        print('Unexpected status code: ${response.statusCode}');
        setState(() => _loadingFarms = false);
      }

      print('================ FARM LOADING END =================');
    } on DioException catch (e) {
      print('================ DIO ERROR =================');

      print('Message: ${e.message}');
      print('Type: ${e.type}');

      if (e.response != null) {
        print('Status Code: ${e.response?.statusCode}');
        print('Response Data: ${e.response?.data}');
      }

      final prefs = await SharedPreferences.getInstance();
      final saved = prefs.getString('current_farm_id');

      if (saved != null && saved.isNotEmpty) {
        setState(() {
          _currentFarmId = saved;
          _currentFarmName = 'My Farm';
        });

        print('Loaded farm from SharedPreferences');
      }

      setState(() => _loadingFarms = false);
    } catch (e, stackTrace) {
      print('================ GENERAL ERROR =================');
      print(e);
      print(stackTrace);

      final prefs = await SharedPreferences.getInstance();
      final saved = prefs.getString('current_farm_id');

      if (saved != null && saved.isNotEmpty) {
        setState(() {
          _currentFarmId = saved;
          _currentFarmName = 'My Farm';
        });

        print('Loaded farm from SharedPreferences');
      }

      setState(() => _loadingFarms = false);
    }
  }

  // -------------------------------------------------------------------------
  // Show farm picker bottom sheet
  // -------------------------------------------------------------------------
  void _showFarmPicker() {
    if (_farms.isEmpty) {
      _showSnack('No farms found. Please register a farm first.');
      return;
    }
    showModalBottomSheet(
      context: context,
      shape: const RoundedRectangleBorder(
        borderRadius: BorderRadius.vertical(top: Radius.circular(20)),
      ),
      builder: (_) => Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          const SizedBox(height: 12),
          Container(
            width: 40, height: 4,
            decoration: BoxDecoration(
              color: Colors.grey[300],
              borderRadius: BorderRadius.circular(2),
            ),
          ),
          const SizedBox(height: 16),
          const Text('Select Farm',
              style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
          const SizedBox(height: 8),
          ..._farms.map((farm) => ListTile(
            leading: const CircleAvatar(
              backgroundColor: Color(0xFF2D6A4F),
              child: Icon(Icons.agriculture, color: Colors.white, size: 18),
            ),
            title: Text(farm['name'] ?? 'Farm'),
            subtitle: Text(farm['crop'] ?? ''),
            trailing: farm['id'] == _currentFarmId
                ? const Icon(Icons.check_circle, color: Color(0xFF2D6A4F))
                : null,
            onTap: () async {
              final prefs = await SharedPreferences.getInstance();
              await prefs.setString('current_farm_id', farm['id']);
              setState(() {
                _currentFarmId   = farm['id'];
                _currentFarmName = farm['name'];
              });
              if (mounted) Navigator.pop(context);
            },
          )),
          const SizedBox(height: 16),
        ],
      ),
    );
  }

  // -------------------------------------------------------------------------
  // Recorder
  // -------------------------------------------------------------------------
  Future<void> _initRecorder() async {
    final status = await Permission.microphone.request();
    if (status.isGranted) {
      await _recorder.openRecorder();
      setState(() => _recorderReady = true);
    }
  }

  Future<void> _startRecording() async {
    if (!_recorderReady) {
      _showSnack('Microphone permission required');
      return;
    }
    final dir = await getTemporaryDirectory();
    _recordingPath =
    '${dir.path}/advisory_${DateTime.now().millisecondsSinceEpoch}.m4a';
    await _recorder.startRecorder(toFile: _recordingPath, codec: Codec.aacMP4);
    setState(() => _isRecording = true);
  }

  Future<void> _stopRecordingAndSend() async {
    await _recorder.stopRecorder();
    setState(() => _isRecording = false);

    if (_recordingPath == null) return;
    if (_currentFarmId == null) {
      _showFarmPicker();
      return;
    }

    final file = File(_recordingPath!);
    if (!await file.exists() || await file.length() == 0) {
      _showSnack('Recording was empty — try again');
      return;
    }

    _addMessage({'type': 'user', 'text': '🎤 Voice question...', 'isLoading': false});
    _addMessage({'type': 'ai', 'text': '', 'isLoading': true});
    setState(() => _isLoading = true);

    try {
      final result = await _aiService.submitVoiceAdvisory(
        farmId: _currentFarmId!,
        audioFile: file,
        language: _selectedLanguage,
      );
      _replaceLastAiMessage({
        'type': 'ai',
        'text': result.advisoryText,
        'audioUrl': result.audioResponseUrl,
        'isLoading': false,
      });
    } catch (e) {
      _replaceLastAiMessage({
        'type': 'ai',
        'text': 'Could not process voice. Try typing instead.',
        'isLoading': false,
        'isError': true,
      });
    } finally {
      setState(() => _isLoading = false);
    }
  }

  // -------------------------------------------------------------------------
  // Text advisory
  // -------------------------------------------------------------------------
  Future<void> _sendTextQuery() async {
    final query = _textController.text.trim();
    if (query.isEmpty) return;

    // If no farm loaded yet, show picker
    if (_currentFarmId == null) {
      _showFarmPicker();
      return;
    }

    _textController.clear();
    _addMessage({'type': 'user', 'text': query, 'isLoading': false});
    _addMessage({'type': 'ai', 'text': '', 'isLoading': true});
    setState(() => _isLoading = true);

    try {
      final result = await _aiService.submitTextAdvisory(
        farmId: _currentFarmId!,
        query: query,
        language: _selectedLanguage,
      );
      _replaceLastAiMessage({
        'type': 'ai',
        'text': result.advisoryText,
        'audioUrl': result.audioResponseUrl,
        'isLoading': false,
      });
    } catch (e) {
      _replaceLastAiMessage({
        'type': 'ai',
        'text': 'Could not get advice. Check your connection and try again.',
        'isLoading': false,
        'isError': true,
      });
    } finally {
      setState(() => _isLoading = false);
    }
  }

  // -------------------------------------------------------------------------
  // Audio playback
  // -------------------------------------------------------------------------
  Future<void> _playAudio(String? url) async {
    if (url == null || url.isEmpty) return;
    try {
      await _audioPlayer.setUrl(url);
      await _audioPlayer.play();
    } catch (e) {
      _showSnack('Could not play audio');
    }
  }

  // -------------------------------------------------------------------------
  // Helpers
  // -------------------------------------------------------------------------
  void _addMessage(Map<String, dynamic> msg) {
    setState(() => _messages.add(msg));
    WidgetsBinding.instance.addPostFrameCallback((_) {
      if (_scrollController.hasClients) {
        _scrollController.animateTo(
          _scrollController.position.maxScrollExtent,
          duration: const Duration(milliseconds: 300),
          curve: Curves.easeOut,
        );
      }
    });
  }

  void _replaceLastAiMessage(Map<String, dynamic> msg) {
    setState(() {
      final idx = _messages.lastIndexWhere((m) => m['type'] == 'ai');
      if (idx != -1) _messages[idx] = msg;
      else _messages.add(msg);
    });
  }

  void _showSnack(String msg) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(content: Text(msg), duration: const Duration(seconds: 3)),
    );
  }

  @override
  void dispose() {
    _recorder.closeRecorder();
    _audioPlayer.dispose();
    _textController.dispose();
    _scrollController.dispose();
    super.dispose();
  }

  // =========================================================================
  // UI
  // =========================================================================
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFF7F9FC),
      appBar: AppBar(
        backgroundColor: const Color(0xFF1B4332),
        foregroundColor: Colors.white,
        title: const Text('AI Farm Advisor',
            style: TextStyle(fontWeight: FontWeight.bold)),
        actions: [
          // Language selector
          DropdownButton<String>(
            value: _selectedLanguage,
            dropdownColor: const Color(0xFF1B4332),
            icon: const Icon(Icons.language, color: Colors.white),
            underline: const SizedBox(),
            items: _languages.entries.map((e) => DropdownMenuItem(
              value: e.key,
              child: Text(e.value,
                  style: const TextStyle(color: Colors.white, fontSize: 13)),
            )).toList(),
            onChanged: (val) async {
              if (val == null) return;
              setState(() => _selectedLanguage = val);
              final prefs = await SharedPreferences.getInstance();
              await prefs.setString('language', val);
            },
          ),
          const SizedBox(width: 8),
        ],
      ),
      body: Column(
        children: [
          // ── Farm selector bar ──────────────────────────────────────────
          _buildFarmBar(),

          // ── Messages ──────────────────────────────────────────────────
          Expanded(
            child: _messages.isEmpty
                ? _buildEmptyState()
                : ListView.builder(
              controller: _scrollController,
              padding: const EdgeInsets.all(16),
              itemCount: _messages.length,
              itemBuilder: (ctx, i) => _buildMessageBubble(_messages[i]),
            ),
          ),

          // ── Input area ────────────────────────────────────────────────
          _buildInputArea(),
        ],
      ),
    );
  }

  // ── Farm bar ──────────────────────────────────────────────────────────────
  Widget _buildFarmBar() {
    if (_loadingFarms) {
      return Container(
        color: const Color(0xFFE8F5E9),
        padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 10),
        child: const Row(
          children: [
            SizedBox(
              width: 14, height: 14,
              child: CircularProgressIndicator(strokeWidth: 2,
                  color: Color(0xFF2D6A4F)),
            ),
            SizedBox(width: 10),
            Text('Loading your farms...',
                style: TextStyle(color: Color(0xFF2D6A4F), fontSize: 13)),
          ],
        ),
      );
    }

    if (_currentFarmId == null) {
      // No farm — tap to pick
      return GestureDetector(
        onTap: _showFarmPicker,
        child: Container(
          width: double.infinity,
          color: const Color(0xFFFFF3CD),
          padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 10),
          child: Row(
            children: [
              const Icon(Icons.warning_amber_rounded,
                  color: Color(0xFF856404), size: 16),
              const SizedBox(width: 8),
              const Expanded(
                child: Text(
                  'Tap here to select a farm before asking',
                  style: TextStyle(color: Color(0xFF856404), fontSize: 13),
                ),
              ),
              const Icon(Icons.chevron_right,
                  color: Color(0xFF856404), size: 18),
            ],
          ),
        ),
      );
    }

    // Farm selected — show name with change option
    return GestureDetector(
      onTap: _farms.length > 1 ? _showFarmPicker : null,
      child: Container(
        color: const Color(0xFFE8F5E9),
        padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 10),
        child: Row(
          children: [
            const Icon(Icons.agriculture, color: Color(0xFF2D6A4F), size: 16),
            const SizedBox(width: 8),
            Expanded(
              child: Text(
                _currentFarmName ?? 'My Farm',
                style: const TextStyle(
                    color: Color(0xFF1B4332),
                    fontWeight: FontWeight.w600,
                    fontSize: 13),
              ),
            ),
            if (_farms.length > 1)
              const Text('Change',
                  style: TextStyle(
                      color: Color(0xFF2D6A4F),
                      fontSize: 12,
                      decoration: TextDecoration.underline)),
          ],
        ),
      ),
    );
  }

  // ── Empty state ───────────────────────────────────────────────────────────
  Widget _buildEmptyState() {
    return SingleChildScrollView(
      child: Padding(
        padding: const EdgeInsets.all(24),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            const SizedBox(height: 40),
            Container(
              width: 80, height: 80,
              decoration: BoxDecoration(
                color: const Color(0xFF2D6A4F).withOpacity(0.1),
                shape: BoxShape.circle,
              ),
              child: const Icon(Icons.eco, size: 40, color: Color(0xFF2D6A4F)),
            ),
            const SizedBox(height: 16),
            const Text('Ask Your AI Farm Advisor',
                style: TextStyle(
                    fontSize: 18,
                    fontWeight: FontWeight.bold,
                    color: Color(0xFF1B4332))),
            const SizedBox(height: 8),
            const Text(
              'Ask anything about your crops, pests, weather, or farming practices.',
              textAlign: TextAlign.center,
              style: TextStyle(color: Colors.grey, fontSize: 13, height: 1.5),
            ),
            const SizedBox(height: 28),
            // Quick question chips
            _chip('When should I apply fertilizer?'),
            const SizedBox(height: 8),
            _chip('My crop leaves are turning yellow'),
            const SizedBox(height: 8),
            _chip('How do I control stem borers?'),
            const SizedBox(height: 8),
            _chip('Is it a good time to harvest?'),
            const SizedBox(height: 16),
            // Voice tip
            Container(
              padding: const EdgeInsets.all(12),
              decoration: BoxDecoration(
                color: const Color(0xFF2D6A4F).withOpacity(0.06),
                borderRadius: BorderRadius.circular(12),
              ),
              child: const Row(
                children: [
                  Icon(Icons.mic, color: Color(0xFF2D6A4F), size: 18),
                  SizedBox(width: 8),
                  Expanded(
                    child: Text(
                      'Hold the mic button to ask by voice in your language',
                      style: TextStyle(
                          color: Color(0xFF2D6A4F), fontSize: 12, height: 1.4),
                    ),
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _chip(String text) {
    return GestureDetector(
      onTap: () {
        _textController.text = text;
        _sendTextQuery();
      },
      child: Container(
        width: double.infinity,
        padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 10),
        decoration: BoxDecoration(
          color: Colors.white,
          border: Border.all(color: const Color(0xFF2D6A4F).withOpacity(0.4)),
          borderRadius: BorderRadius.circular(12),
        ),
        child: Row(
          children: [
            const Icon(Icons.chat_bubble_outline,
                size: 14, color: Color(0xFF2D6A4F)),
            const SizedBox(width: 8),
            Expanded(
              child: Text(text,
                  style: const TextStyle(
                      color: Color(0xFF1B4332), fontSize: 13)),
            ),
            const Icon(Icons.arrow_forward_ios,
                size: 12, color: Colors.grey),
          ],
        ),
      ),
    );
  }

  // ── Message bubble ────────────────────────────────────────────────────────
  Widget _buildMessageBubble(Map<String, dynamic> msg) {
    final isUser    = msg['type'] == 'user';
    final isLoading = msg['isLoading'] == true;
    final isError   = msg['isError'] == true;

    return Padding(
      padding: const EdgeInsets.only(bottom: 12),
      child: Row(
        mainAxisAlignment:
        isUser ? MainAxisAlignment.end : MainAxisAlignment.start,
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          if (!isUser) ...[
            const CircleAvatar(
              radius: 16,
              backgroundColor: Color(0xFF2D6A4F),
              child: Icon(Icons.eco, size: 16, color: Colors.white),
            ),
            const SizedBox(width: 8),
          ],
          Flexible(
            child: Container(
              constraints: BoxConstraints(
                  maxWidth: MediaQuery.of(context).size.width * 0.78),
              padding: const EdgeInsets.all(12),
              decoration: BoxDecoration(
                color: isUser
                    ? const Color(0xFF2D6A4F)
                    : (isError
                    ? const Color(0xFFFFF0F0)
                    : Colors.white),
                borderRadius: BorderRadius.only(
                  topLeft: const Radius.circular(16),
                  topRight: const Radius.circular(16),
                  bottomLeft: Radius.circular(isUser ? 16 : 4),
                  bottomRight: Radius.circular(isUser ? 4 : 16),
                ),
                boxShadow: [
                  BoxShadow(
                      color: Colors.black.withOpacity(0.05),
                      blurRadius: 4,
                      offset: const Offset(0, 2)),
                ],
              ),
              child: isLoading
                  ? Row(
                mainAxisSize: MainAxisSize.min,
                children: [
                  const SizedBox(
                    width: 14, height: 14,
                    child: CircularProgressIndicator(
                        strokeWidth: 2, color: Color(0xFF2D6A4F)),
                  ),
                  const SizedBox(width: 10),
                  Text('Thinking...',
                      style: TextStyle(
                          color: Colors.grey[600], fontSize: 13)),
                ],
              )
                  : Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    msg['text'] ?? '',
                    style: TextStyle(
                      color: isUser
                          ? Colors.white
                          : (isError
                          ? Colors.red[700]
                          : Colors.black87),
                      fontSize: 14,
                      height: 1.55,
                    ),
                  ),
                  if (!isUser && msg['audioUrl'] != null) ...[
                    const SizedBox(height: 8),
                    GestureDetector(
                      onTap: () => _playAudio(msg['audioUrl']),
                      child: Container(
                        padding: const EdgeInsets.symmetric(
                            horizontal: 10, vertical: 6),
                        decoration: BoxDecoration(
                          color: const Color(0xFF2D6A4F)
                              .withOpacity(0.12),
                          borderRadius: BorderRadius.circular(20),
                        ),
                        child: const Row(
                          mainAxisSize: MainAxisSize.min,
                          children: [
                            Icon(Icons.volume_up,
                                size: 14, color: Color(0xFF2D6A4F)),
                            SizedBox(width: 4),
                            Text('Play response',
                                style: TextStyle(
                                    color: Color(0xFF2D6A4F),
                                    fontSize: 12)),
                          ],
                        ),
                      ),
                    ),
                  ],
                ],
              ),
            ),
          ),
          if (isUser) ...[
            const SizedBox(width: 8),
            const CircleAvatar(
              radius: 16,
              backgroundColor: Color(0xFF52B788),
              child: Icon(Icons.person, size: 16, color: Colors.white),
            ),
          ],
        ],
      ),
    );
  }

  // ── Input area ────────────────────────────────────────────────────────────
  Widget _buildInputArea() {
    return Container(
      color: Colors.white,
      padding: const EdgeInsets.fromLTRB(12, 8, 12, 20),
      child: SafeArea(
        top: false,
        child: Row(
          children: [
            Expanded(
              child: Container(
                decoration: BoxDecoration(
                  color: const Color(0xFFF1F3F5),
                  borderRadius: BorderRadius.circular(24),
                ),
                child: Row(
                  children: [
                    Expanded(
                      child: TextField(
                        controller: _textController,
                        decoration: const InputDecoration(
                          hintText: 'Ask a farming question...',
                          hintStyle:
                          TextStyle(color: Colors.grey, fontSize: 14),
                          border: InputBorder.none,
                          contentPadding: EdgeInsets.symmetric(
                              horizontal: 16, vertical: 10),
                        ),
                        maxLines: null,
                        textInputAction: TextInputAction.send,
                        onSubmitted: (_) => _sendTextQuery(),
                      ),
                    ),
                    IconButton(
                      icon: const Icon(Icons.send_rounded,
                          color: Color(0xFF2D6A4F)),
                      onPressed: _isLoading ? null : _sendTextQuery,
                    ),
                  ],
                ),
              ),
            ),
            const SizedBox(width: 8),
            // Hold to record voice
            GestureDetector(
              onLongPressStart: (_) => _startRecording(),
              onLongPressEnd: (_) => _stopRecordingAndSend(),
              child: AnimatedContainer(
                duration: const Duration(milliseconds: 200),
                width: 48,
                height: 48,
                decoration: BoxDecoration(
                  color: _isRecording
                      ? Colors.red
                      : const Color(0xFF1B4332),
                  shape: BoxShape.circle,
                  boxShadow: _isRecording
                      ? [
                    BoxShadow(
                        color: Colors.red.withOpacity(0.4),
                        blurRadius: 12,
                        spreadRadius: 2)
                  ]
                      : [],
                ),
                child: Icon(
                  _isRecording ? Icons.stop : Icons.mic,
                  color: Colors.white,
                  size: 22,
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
import 'dart:io';
import 'package:flutter/material.dart';
import 'package:flutter_sound/flutter_sound.dart';
import 'package:just_audio/just_audio.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:path_provider/path_provider.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../services/ai_service.dart';

class AdvisorScreen extends StatefulWidget {
  const AdvisorScreen({super.key});

  @override
  State<AdvisorScreen> createState() => _AdvisorScreenState();
}

class _AdvisorScreenState extends State<AdvisorScreen> {
  final AiService _aiService = AiService();
  final TextEditingController _textController = TextEditingController();
  final AudioPlayer _audioPlayer = AudioPlayer();
  final FlutterSoundRecorder _recorder = FlutterSoundRecorder();
  final ScrollController _scrollController = ScrollController();

  // Chat messages: {type: 'user'|'ai', text: '...', audioUrl: '...', isLoading: bool}
  final List<Map<String, dynamic>> _messages = [];

  bool _isRecording = false;
  bool _isLoading = false;
  bool _recorderReady = false;
  String _selectedLanguage = 'am';
  String? _currentFarmId;
  String? _recordingPath;

  static const Map<String, String> _languages = {
    'am': 'አማርኛ',
    'om': 'Afaan Oromoo',
    'ti': 'ትግርኛ',
    'en': 'English',
  };

  @override
  void initState() {
    super.initState();
    _initRecorder();
    _loadFarmId();
  }

  Future<void> _loadFarmId() async {
    final prefs = await SharedPreferences.getInstance();
    setState(() {
      _currentFarmId = prefs.getString('current_farm_id');
    });
  }

  Future<void> _initRecorder() async {
    final status = await Permission.microphone.request();
    if (status.isGranted) {
      await _recorder.openRecorder();
      setState(() => _recorderReady = true);
    }
  }

  @override
  void dispose() {
    _recorder.closeRecorder();
    _audioPlayer.dispose();
    _textController.dispose();
    _scrollController.dispose();
    super.dispose();
  }

  // -----------------------------------------------------------------------
  // Recording
  // -----------------------------------------------------------------------
  Future<void> _startRecording() async {
    if (!_recorderReady) {
      _showSnack('Microphone permission required');
      return;
    }
    final dir = await getTemporaryDirectory();
    _recordingPath = '${dir.path}/advisory_${DateTime.now().millisecondsSinceEpoch}.m4a';
    await _recorder.startRecorder(toFile: _recordingPath, codec: Codec.aacMP4);
    setState(() => _isRecording = true);
  }

  Future<void> _stopRecordingAndSend() async {
    await _recorder.stopRecorder();
    setState(() => _isRecording = false);

    if (_recordingPath == null || _currentFarmId == null) {
      _showSnack('Farm not selected. Please select a farm first.');
      return;
    }

    final file = File(_recordingPath!);
    if (!await file.exists() || await file.length() == 0) {
      _showSnack('Recording was empty');
      return;
    }

    _addMessage({'type': 'user', 'text': '🎤 Voice question sent...', 'isLoading': false});
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
        'text': 'Sorry, I could not process your question. Please try again.',
        'isLoading': false,
        'isError': true,
      });
    } finally {
      setState(() => _isLoading = false);
    }
  }

  // -----------------------------------------------------------------------
  // Text submission
  // -----------------------------------------------------------------------
  Future<void> _sendTextQuery() async {
    final query = _textController.text.trim();
    if (query.isEmpty) return;
    if (_currentFarmId == null) {
      _showSnack('Please select a farm first');
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
        'text': 'Sorry, I could not answer your question. Please try again.',
        'isLoading': false,
        'isError': true,
      });
    } finally {
      setState(() => _isLoading = false);
    }
  }

  // -----------------------------------------------------------------------
  // Audio playback
  // -----------------------------------------------------------------------
  Future<void> _playAudio(String? url) async {
    if (url == null || url.isEmpty) return;
    try {
      await _audioPlayer.setUrl(url);
      await _audioPlayer.play();
    } catch (e) {
      _showSnack('Could not play audio response');
    }
  }

  // -----------------------------------------------------------------------
  // Helpers
  // -----------------------------------------------------------------------
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

  void _showSnack(String message) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(content: Text(message), duration: const Duration(seconds: 3)),
    );
  }

  // -----------------------------------------------------------------------
  // UI
  // -----------------------------------------------------------------------
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFF7F9FC),
      appBar: AppBar(
        backgroundColor: const Color(0xFF2D6A4F),
        title: const Text('AI Advisor', style: TextStyle(color: Colors.white)),
        actions: [
          Padding(
            padding: const EdgeInsets.only(right: 12),
            child: DropdownButton<String>(
              value: _selectedLanguage,
              dropdownColor: const Color(0xFF2D6A4F),
              icon: const Icon(Icons.language, color: Colors.white),
              underline: const SizedBox(),
              items: _languages.entries.map((e) => DropdownMenuItem(
                value: e.key,
                child: Text(e.value, style: const TextStyle(color: Colors.white, fontSize: 13)),
              )).toList(),
              onChanged: (val) => setState(() => _selectedLanguage = val ?? 'am'),
            ),
          ),
        ],
      ),
      body: Column(
        children: [
          // Farm selector banner
          if (_currentFarmId == null)
            Container(
              width: double.infinity,
              color: const Color(0xFFFFF3CD),
              padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
              child: const Text(
                'No farm selected. Go to My Farm to select your farm.',
                style: TextStyle(color: Color(0xFF856404), fontSize: 12),
              ),
            ),

          // Messages list
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

          // Input area
          _buildInputArea(),
        ],
      ),
    );
  }

  Widget _buildEmptyState() {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
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
              style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold, color: Color(0xFF2D6A4F))),
          const SizedBox(height: 8),
          const Padding(
            padding: EdgeInsets.symmetric(horizontal: 40),
            child: Text(
              'Ask about crop diseases, weather concerns, fertilizer timing, or any farming question.',
              textAlign: TextAlign.center,
              style: TextStyle(color: Colors.grey, fontSize: 13),
            ),
          ),
          const SizedBox(height: 24),
          _buildSuggestionChip('When should I apply fertilizer?'),
          const SizedBox(height: 8),
          _buildSuggestionChip('My crop leaves are turning yellow'),
          const SizedBox(height: 8),
          _buildSuggestionChip('How much water does my wheat need?'),
        ],
      ),
    );
  }

  Widget _buildSuggestionChip(String text) {
    return GestureDetector(
      onTap: () {
        _textController.text = text;
        _sendTextQuery();
      },
      child: Container(
        padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
        decoration: BoxDecoration(
          border: Border.all(color: const Color(0xFF2D6A4F)),
          borderRadius: BorderRadius.circular(20),
        ),
        child: Text(text, style: const TextStyle(color: Color(0xFF2D6A4F), fontSize: 13)),
      ),
    );
  }

  Widget _buildMessageBubble(Map<String, dynamic> msg) {
    final isUser = msg['type'] == 'user';
    final bool isLoading = msg['isLoading'] == true;
    final bool isError = msg['isError'] == true;

    return Padding(
      padding: const EdgeInsets.only(bottom: 12),
      child: Row(
        mainAxisAlignment: isUser ? MainAxisAlignment.end : MainAxisAlignment.start,
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
                maxWidth: MediaQuery.of(context).size.width * 0.75,
              ),
              padding: const EdgeInsets.all(12),
              decoration: BoxDecoration(
                color: isUser
                    ? const Color(0xFF2D6A4F)
                    : (isError ? const Color(0xFFFFF0F0) : Colors.white),
                borderRadius: BorderRadius.only(
                  topLeft: const Radius.circular(16),
                  topRight: const Radius.circular(16),
                  bottomLeft: Radius.circular(isUser ? 16 : 4),
                  bottomRight: Radius.circular(isUser ? 4 : 16),
                ),
                boxShadow: [
                  BoxShadow(color: Colors.black.withOpacity(0.05), blurRadius: 4, offset: const Offset(0, 2)),
                ],
              ),
              child: isLoading
                  ? const SizedBox(
                height: 20, width: 60,
                child: LinearProgressIndicator(
                  backgroundColor: Color(0xFFE8F5E9),
                  color: Color(0xFF2D6A4F),
                ),
              )
                  : Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    msg['text'] ?? '',
                    style: TextStyle(
                      color: isUser ? Colors.white : (isError ? Colors.red[700] : Colors.black87),
                      fontSize: 14,
                      height: 1.5,
                    ),
                  ),
                  // Audio playback button
                  if (!isUser && msg['audioUrl'] != null) ...[
                    const SizedBox(height: 8),
                    GestureDetector(
                      onTap: () => _playAudio(msg['audioUrl']),
                      child: Container(
                        padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
                        decoration: BoxDecoration(
                          color: const Color(0xFF2D6A4F).withOpacity(0.1),
                          borderRadius: BorderRadius.circular(20),
                        ),
                        child: const Row(
                          mainAxisSize: MainAxisSize.min,
                          children: [
                            Icon(Icons.volume_up, size: 16, color: Color(0xFF2D6A4F)),
                            SizedBox(width: 4),
                            Text('Play Audio', style: TextStyle(color: Color(0xFF2D6A4F), fontSize: 12)),
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

  Widget _buildInputArea() {
    return Container(
      color: Colors.white,
      padding: const EdgeInsets.fromLTRB(12, 8, 12, 16),
      child: SafeArea(
        top: false,
        child: Row(
          children: [
            // Text input
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
                          hintText: 'Ask a question...',
                          hintStyle: TextStyle(color: Colors.grey, fontSize: 14),
                          border: InputBorder.none,
                          contentPadding: EdgeInsets.symmetric(horizontal: 16, vertical: 10),
                        ),
                        maxLines: null,
                        textInputAction: TextInputAction.send,
                        onSubmitted: (_) => _sendTextQuery(),
                      ),
                    ),
                    // Send text button
                    IconButton(
                      icon: const Icon(Icons.send_rounded, color: Color(0xFF2D6A4F)),
                      onPressed: _isLoading ? null : _sendTextQuery,
                    ),
                  ],
                ),
              ),
            ),
            const SizedBox(width: 8),

            // Voice record button
            GestureDetector(
              onLongPressStart: (_) => _startRecording(),
              onLongPressEnd: (_) => _stopRecordingAndSend(),
              child: AnimatedContainer(
                duration: const Duration(milliseconds: 200),
                width: 48, height: 48,
                decoration: BoxDecoration(
                  color: _isRecording ? Colors.red : const Color(0xFF2D6A4F),
                  shape: BoxShape.circle,
                  boxShadow: _isRecording
                      ? [BoxShadow(color: Colors.red.withOpacity(0.4), blurRadius: 12, spreadRadius: 2)]
                      : [],
                ),
                child: Icon(
                  _isRecording ? Icons.stop : Icons.mic,
                  color: Colors.white, size: 22,
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
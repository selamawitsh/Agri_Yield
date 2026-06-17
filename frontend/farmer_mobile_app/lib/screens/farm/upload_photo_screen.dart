import 'dart:io';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:image_picker/image_picker.dart';
import '../../services/farm_service.dart';
import '../../services/language_service.dart';

class UploadPhotoScreen extends StatefulWidget {
  final String farmId;
  const UploadPhotoScreen({super.key, required this.farmId});
  @override
  State<UploadPhotoScreen> createState() => _UploadPhotoScreenState();
}

class _UploadPhotoScreenState extends State<UploadPhotoScreen> {
  final _farmService = FarmService();
  final _picker = ImagePicker();
  File? _selectedFile;
  String _selectedPhotoType = 'REGISTRATION';
  bool _isUploading = false;
  String? _error;

  static const Map<String, Map<String, String>> _strings = {
    'am': {
      'title':        'የእርሻ ፎቶ ስቀል',
      'infoMsg':      'ፎቶዎች እርሻዎን ለማረጋገጥ እና የሰብል ጤናን ለመከታተል ይረዳሉ። GPS ቦታ ራሱ ይያዛል።',
      'photoType':    'የፎቶ አይነት',
      'photo':        'ፎቶ',
      'tapToAdd':     'ፎቶ ለማከል ጠቅ ያድርጉ',
      'cameraOrGal':  'ካሜራ ወይም ጋለሪ',
      'changePhoto':  'ፎቶ ቀይር',
      'uploadBtn':    'ፎቶ ስቀል',
      'uploading':    'በመስቀል ላይ...',
      'successMsg':   'ፎቶ በተሳካ ሁኔታ ተሰቅሏል!',
      'noPhotoErr':   'እባክዎ ፎቶ ይምረጡ',
      'pickErr':      'ፎቶ ማምጣት አልተቻለም',
      'selectSource': 'የምስል ምንጭ ይምረጡ',
      'takePhoto':    'ፎቶ ያንሱ',
      'takePhotoSub': 'ካሜራዎን ይጠቀሙ',
      'fromGallery':  'ከጋለሪ ይምረጡ',
      'fromGalSub':   'ያለ ፎቶ ይምረጡ',
      'reg':          'ምዝገባ ፎቶ',
      'regDesc':      'የእርሻ ወሰን ፎቶ',
      'cropHealth':   'የሰብል ጤና',
      'cropHealthD':  'የሰብል ሁኔታ ፎቶ',
      'harvest':      'ምርት',
      'harvestDesc':  'የምርት ፎቶ',
    },
    'om': {
      'title':        'Suuraa Lafa Qonnaa Fe\'i',
      'infoMsg':      'Suuraan lafa qonnaa mirkaneessuu fi fayyaa midhaan hordofuuf gargaara. Bakki GPS ofumaan qabama.',
      'photoType':    'Gosa Suuraa',
      'photo':        'Suuraa',
      'tapToAdd':     'Suuraa dabaluuf tuqi',
      'cameraOrGal':  'Kaamera ykn gaaleerii',
      'changePhoto':  'Suuraa Jijjiiri',
      'uploadBtn':    'Suuraa Fe\'i',
      'uploading':    'Fe\'aa jira...',
      'successMsg':   'Suuraan milkaa\'inaan fe\'ame!',
      'noPhotoErr':   'Maaloo suuraa filadhu',
      'pickErr':      'Suuraa fiduu hin dandeenye',
      'selectSource': 'Madda suuraa filadhu',
      'takePhoto':    'Suuraa ka\'i',
      'takePhotoSub': 'Kaamera kee fayyadami',
      'fromGallery':  'Gaaleerirraa filadhu',
      'fromGalSub':   'Suuraa jiru filadhu',
      'reg':          'Galmeessa',
      'regDesc':      'Suuraa daangaa lafa qonnaa',
      'cropHealth':   'Fayyaa Midhaan',
      'cropHealthD':  'Haala midhaan agarsiisu',
      'harvest':      'Sassaabuu',
      'harvestDesc':  'Suuraa sassaabuu',
    },
    'en': {
      'title':        'Upload Farm Photo',
      'infoMsg':      'Photos help verify your farm and track crop health. GPS location is automatically captured.',
      'photoType':    'Photo Type',
      'photo':        'Photo',
      'tapToAdd':     'Tap to add photo',
      'cameraOrGal':  'Camera or gallery',
      'changePhoto':  'Change Photo',
      'uploadBtn':    'Upload Photo',
      'uploading':    'Uploading...',
      'successMsg':   'Photo uploaded successfully!',
      'noPhotoErr':   'Please select a photo first',
      'pickErr':      'Failed to pick image',
      'selectSource': 'Select Image Source',
      'takePhoto':    'Take Photo',
      'takePhotoSub': 'Use your camera',
      'fromGallery':  'Choose from Gallery',
      'fromGalSub':   'Pick an existing photo',
      'reg':          'Farm Registration',
      'regDesc':      'Photo of your farm boundary',
      'cropHealth':   'Crop Health',
      'cropHealthD':  'Photo showing crop condition',
      'harvest':      'Harvest',
      'harvestDesc':  'Photo of your harvest',
    },
  };

  String _t(String key) {
    final code = context.read<LanguageService>().languageCode;
    return _strings[code]?[key] ?? _strings['en']![key]!;
  }

  List<Map<String, dynamic>> get _photoTypes => [
    {'value': 'REGISTRATION', 'label': _t('reg'),        'description': _t('regDesc'),      'icon': Icons.agriculture},
    {'value': 'CROP_HEALTH',  'label': _t('cropHealth'), 'description': _t('cropHealthD'),   'icon': Icons.eco},
    {'value': 'HARVEST',      'label': _t('harvest'),    'description': _t('harvestDesc'),   'icon': Icons.grass},
  ];

  Future<void> _pickImage(ImageSource source) async {
    try {
      final XFile? picked = await _picker.pickImage(source: source, maxWidth: 1920, maxHeight: 1080, imageQuality: 85);
      if (picked != null) setState(() { _selectedFile = File(picked.path); _error = null; });
    } catch (e) {
      setState(() => _error = '${_t('pickErr')}: ${e.toString()}');
    }
  }

  void _showImageSourceDialog() {
    showModalBottomSheet(
      context: context,
      shape: const RoundedRectangleBorder(borderRadius: BorderRadius.vertical(top: Radius.circular(16))),
      builder: (ctx) => SafeArea(child: Column(mainAxisSize: MainAxisSize.min, children: [
        const SizedBox(height: 8),
        Container(width: 40, height: 4, decoration: BoxDecoration(color: Colors.grey.shade300, borderRadius: BorderRadius.circular(2))),
        const SizedBox(height: 16),
        Text(_t('selectSource'), style: const TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
        const SizedBox(height: 16),
        ListTile(
          leading: const CircleAvatar(backgroundColor: Colors.green, child: Icon(Icons.camera_alt, color: Colors.white)),
          title: Text(_t('takePhoto')), subtitle: Text(_t('takePhotoSub')),
          onTap: () { Navigator.pop(ctx); _pickImage(ImageSource.camera); },
        ),
        ListTile(
          leading: const CircleAvatar(backgroundColor: Colors.blue, child: Icon(Icons.photo_library, color: Colors.white)),
          title: Text(_t('fromGallery')), subtitle: Text(_t('fromGalSub')),
          onTap: () { Navigator.pop(ctx); _pickImage(ImageSource.gallery); },
        ),
        const SizedBox(height: 16),
      ])),
    );
  }

  Future<void> _uploadPhoto() async {
    if (_selectedFile == null) { setState(() => _error = _t('noPhotoErr')); return; }
    setState(() { _isUploading = true; _error = null; });
    final result = await _farmService.uploadPhoto(farmId: widget.farmId, photoType: _selectedPhotoType, filePath: _selectedFile!.path);
    if (mounted) {
      setState(() => _isUploading = false);
      if (result['success'] == true) {
        ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text(_t('successMsg')), backgroundColor: Colors.green));
        Navigator.pop(context, true);
      } else { setState(() => _error = result['message']); }
    }
  }

  @override
  Widget build(BuildContext context) {
    context.watch<LanguageService>();
    return Scaffold(
      appBar: AppBar(title: Text(_t('title')), backgroundColor: Colors.green, foregroundColor: Colors.white),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
          Container(padding: const EdgeInsets.all(12),
              decoration: BoxDecoration(color: Colors.green.shade50, borderRadius: BorderRadius.circular(8), border: Border.all(color: Colors.green.shade200)),
              child: Row(children: [const Icon(Icons.info_outline, color: Colors.green, size: 20), const SizedBox(width: 8),
                Expanded(child: Text(_t('infoMsg'), style: const TextStyle(color: Colors.green, fontSize: 13)))])),
          const SizedBox(height: 24),
          Text(_t('photoType'), style: const TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
          const SizedBox(height: 12),
          ..._photoTypes.map((type) => _buildTypeCard(type)),
          const SizedBox(height: 24),
          Text(_t('photo'), style: const TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
          const SizedBox(height: 12),
          GestureDetector(
            onTap: _showImageSourceDialog,
            child: Container(
              width: double.infinity, height: 220,
              decoration: BoxDecoration(color: Colors.grey.shade100, borderRadius: BorderRadius.circular(12),
                  border: Border.all(color: _selectedFile != null ? Colors.green : Colors.grey.shade300, width: _selectedFile != null ? 2 : 1)),
              child: _selectedFile != null
                  ? ClipRRect(borderRadius: BorderRadius.circular(11), child: Image.file(_selectedFile!, fit: BoxFit.cover))
                  : Column(mainAxisAlignment: MainAxisAlignment.center, children: [
                Icon(Icons.add_a_photo, size: 56, color: Colors.grey.shade400),
                const SizedBox(height: 12),
                Text(_t('tapToAdd'), style: TextStyle(color: Colors.grey.shade600, fontSize: 16, fontWeight: FontWeight.w500)),
                const SizedBox(height: 4),
                Text(_t('cameraOrGal'), style: TextStyle(color: Colors.grey.shade400, fontSize: 13)),
              ]),
            ),
          ),
          if (_selectedFile != null) ...[
            const SizedBox(height: 8),
            Row(mainAxisAlignment: MainAxisAlignment.end, children: [
              TextButton.icon(onPressed: _showImageSourceDialog,
                  icon: const Icon(Icons.refresh, size: 16), label: Text(_t('changePhoto')),
                  style: TextButton.styleFrom(foregroundColor: Colors.green)),
            ]),
          ],
          if (_error != null) ...[
            const SizedBox(height: 16),
            Container(padding: const EdgeInsets.all(12),
                decoration: BoxDecoration(color: Colors.red.shade50, borderRadius: BorderRadius.circular(8), border: Border.all(color: Colors.red.shade200)),
                child: Row(children: [const Icon(Icons.error_outline, color: Colors.red), const SizedBox(width: 8),
                  Expanded(child: Text(_error!, style: const TextStyle(color: Colors.red)))])),
          ],
          const SizedBox(height: 32),
          SizedBox(width: double.infinity, child: ElevatedButton.icon(
            onPressed: _isUploading ? null : _uploadPhoto,
            icon: _isUploading
                ? const SizedBox(height: 18, width: 18, child: CircularProgressIndicator(color: Colors.white, strokeWidth: 2))
                : const Icon(Icons.cloud_upload),
            label: Text(_isUploading ? _t('uploading') : _t('uploadBtn'), style: const TextStyle(fontSize: 16)),
            style: ElevatedButton.styleFrom(backgroundColor: Colors.green, foregroundColor: Colors.white,
                padding: const EdgeInsets.symmetric(vertical: 16), shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8))),
          )),
          const SizedBox(height: 32),
        ]),
      ),
    );
  }

  Widget _buildTypeCard(Map<String, dynamic> type) {
    final isSelected = _selectedPhotoType == type['value'];
    return GestureDetector(
      onTap: () => setState(() => _selectedPhotoType = type['value']),
      child: Container(
        margin: const EdgeInsets.only(bottom: 10),
        padding: const EdgeInsets.all(14),
        decoration: BoxDecoration(
            color: isSelected ? Colors.green.shade50 : Colors.white,
            borderRadius: BorderRadius.circular(10),
            border: Border.all(color: isSelected ? Colors.green : Colors.grey.shade300, width: isSelected ? 2 : 1)),
        child: Row(children: [
          CircleAvatar(
              backgroundColor: isSelected ? Colors.green : Colors.grey.shade200,
              child: Icon(type['icon'] as IconData, color: isSelected ? Colors.white : Colors.grey, size: 20)),
          const SizedBox(width: 12),
          Expanded(child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
            Text(type['label'], style: TextStyle(fontWeight: FontWeight.bold, color: isSelected ? Colors.green : Colors.black)),
            Text(type['description'], style: TextStyle(fontSize: 12, color: Colors.grey.shade600)),
          ])),
          if (isSelected) const Icon(Icons.check_circle, color: Colors.green),
        ]),
      ),
    );
  }
}
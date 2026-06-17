import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../services/farm_service.dart';
import '../../services/language_service.dart';

class RegisterFarmScreen extends StatefulWidget {
  const RegisterFarmScreen({super.key});
  @override
  State<RegisterFarmScreen> createState() => _RegisterFarmScreenState();
}

class _RegisterFarmScreenState extends State<RegisterFarmScreen> {
  final _farmService = FarmService();
  final _pageController = PageController();
  int _currentStep = 0;

  final _farmNameController   = TextEditingController();
  final _kebeleCodeController = TextEditingController();
  final _regionController     = TextEditingController();
  final _polygonController    = TextEditingController();
  String _selectedCropType    = 'WHEAT';
  DateTime _expectedHarvestDate = DateTime.now().add(const Duration(days: 120));
  bool _isLoading = false;
  String? _error;

  static const Map<String, Map<String, String>> _strings = {
    'am': {
      'title':          'እርሻ ተመዝግብ',
      'farmInfo':       'የእርሻ መረጃ',
      'farmInfoDesc':   'ስለ እርሻዎ እና ምን ሊያበቅሉ እንደሚፈልጉ ይንገሩን',
      'farmName':       'የእርሻ ስም (አማራጭ)',
      'farmNameHint':   'ለምሳሌ፡ የእኔ ጤፍ እርሻ',
      'cropType':       'የሰብል አይነት *',
      'kebele':         'የቀበሌ ኮድ *',
      'kebeleHint':     'ለምሳሌ፡ ORM-001',
      'region':         'ክልል *',
      'regionHint':     'ለምሳሌ፡ ኦሮሚያ',
      'harvestDate':    'የሚጠበቀው የምርት ቀን *',
      'nextLocation':   'ቀጣይ፡ ቦታ',
      'location':       'የእርሻ ቦታ',
      'locationDesc':   'የእርሻዎን GPS ወሰን እንደ GeoJSON ያስገቡ',
      'geoJsonLabel':   'GeoJSON Polygon',
      'infoMsg':        'የ GPS ወሰን ከተመዘገቡ በኋላ በሳተላይት ይረጋገጣል። ስፋቱ ራሱ ይሰላል።',
      'sampleCoords':   'ናሙና መጋጠሚያዎችን ተጠቀም',
      'sampleLoaded':   'ናሙና መጋጠሚያዎች ተጭነዋል። በእርስዎ ትክክለኛ ወሰን ይተኩ።',
      'back':           'ተመለስ',
      'nextReview':     'ቀጣይ፡ ግምገማ',
      'review':         'ግምገማ እና ማስገባት',
      'reviewDesc':     'ከማስገባትዎ በፊት የእርሻዎን ዝርዝሮች ያረጋግጡ',
      'farmNameLabel':  'የእርሻ ስም',
      'notSet':         'አልተቀናጀም',
      'gpsPolygon':     'GPS Polygon',
      'provided':       'ቀርቧል',
      'sampleUsed':     'ናሙና መጋጠሚያዎች',
      'submitFarm':     'እርሻ አስገባ',
      'successMsg':     'እርሻ በተሳካ ሁኔታ ተመዝግቧል!',
      'invalidGeo':     'ልክ ያልሆነ GeoJSON። የናሙና ቁልፍን ተጠቀም ወይም ትክክለኛ Polygon JSON ለጥፍ።',
      'stepFarmInfo':   'የእርሻ መረጃ',
      'stepLocation':   'ቦታ',
      'stepReview':     'ግምገማ',
    },
    'om': {
      'title':          'Lafa Qonnaa Galmeessi',
      'farmInfo':       'Odeeffannoo Lafa Qonnaa',
      'farmInfoDesc':   'Waa\'ee lafa qonnaa keessanii fi maalii biqilchuuf barbaaddan nuuf himaa',
      'farmName':       'Maqaa Lafa Qonnaa (dirqama miti)',
      'farmNameHint':   'Fkn: Lafa Xaafii Koo',
      'cropType':       'Gosa Midhaan *',
      'kebele':         'Koodii Ganda *',
      'kebeleHint':     'Fkn: ORM-001',
      'region':         'Naannoo *',
      'regionHint':     'Fkn: Oromia',
      'harvestDate':    'Guyyaa Sassaabuu Eegamu *',
      'nextLocation':   'Itti aansee: Bakka',
      'location':       'Bakka Lafa Qonnaa',
      'locationDesc':   'Daangaa GPS lafa qonnaa GeoJSON ta\'een galchi',
      'geoJsonLabel':   'GeoJSON Polygon',
      'infoMsg':        'Daangaan GPS galmeessa booda satellite\'n mirkanaawa. Bal\'inni ofumaan herregama.',
      'sampleCoords':   'Qindaa\'ina Fakkeenya Fayyadami',
      'sampleLoaded':   'Qindaa\'ina fakkeenya fe\'ame. Daangaa dhugaa keetiin bakka buusi.',
      'back':           'Deebi\'i',
      'nextReview':     'Itti aansee: Ilaali',
      'review':         'Ilaali fi Galchi',
      'reviewDesc':     'Osoo hin galchin dura bal\'ina lafa qonnaa kee mirkaneessi',
      'farmNameLabel':  'Maqaa Lafa Qonnaa',
      'notSet':         'Hin qindaa\'amne',
      'gpsPolygon':     'GPS Polygon',
      'provided':       'Dhiyaateera',
      'sampleUsed':     'Qindaa\'ina fakkeenya',
      'submitFarm':     'Lafa Qonnaa Galchi',
      'successMsg':     'Lafa qonnaan milkaa\'inaan galmaaye!',
      'invalidGeo':     'GeoJSON sirrii miti. Batara fakkeenya fayyadami ykn JSON sirrii maxxansi.',
      'stepFarmInfo':   'Odeeffannoo',
      'stepLocation':   'Bakka',
      'stepReview':     'Ilaali',
    },
    'en': {
      'title':          'Register Farm',
      'farmInfo':       'Farm Information',
      'farmInfoDesc':   'Tell us about your farm and what you plan to grow',
      'farmName':       'Farm Name (optional)',
      'farmNameHint':   'e.g. My Teff Farm',
      'cropType':       'Crop Type *',
      'kebele':         'Kebele Code *',
      'kebeleHint':     'e.g. ORM-001',
      'region':         'Region *',
      'regionHint':     'e.g. Oromia',
      'harvestDate':    'Expected Harvest Date *',
      'nextLocation':   'Next: Location',
      'location':       'Farm Location',
      'locationDesc':   'Enter your farm GPS boundary as GeoJSON',
      'geoJsonLabel':   'GeoJSON Polygon',
      'infoMsg':        'GPS boundary will be verified by satellite after registration. Area is calculated automatically.',
      'sampleCoords':   'Use Sample Coordinates',
      'sampleLoaded':   'Sample coordinates loaded. Replace with your actual farm boundary.',
      'back':           'Back',
      'nextReview':     'Next: Review',
      'review':         'Review & Submit',
      'reviewDesc':     'Please confirm your farm details before submitting',
      'farmNameLabel':  'Farm Name',
      'notSet':         'Not set',
      'gpsPolygon':     'GPS Polygon',
      'provided':       'Provided',
      'sampleUsed':     'Sample coordinates',
      'submitFarm':     'Register Farm',
      'successMsg':     'Farm registered successfully!',
      'invalidGeo':     'Invalid GeoJSON. Use the sample button or paste valid Polygon JSON.',
      'stepFarmInfo':   'Farm Info',
      'stepLocation':   'Location',
      'stepReview':     'Review',
    },
  };

  String _t(String key) {
    final code = context.read<LanguageService>().languageCode;
    return _strings[code]?[key] ?? _strings['en']![key]!;
  }

  final List<String> _cropTypes = ['WHEAT','TEFF','BARLEY','MAIZE','SORGHUM','COFFEE','BEANS','MILLET'];
  final List<String> _ethiopianRegions = [
    'Addis Ababa','Afar','Amhara','Benishangul-Gumuz',
    'Dire Dawa','Gambela','Harari','Oromia',
    'Sidama','Somali','South Ethiopia','Tigray','South West Ethiopia'
  ];

  static const String _samplePolygon =
      '{"type":"Polygon","coordinates":[[[38.7400,9.0300],[38.7500,9.0300],[38.7500,9.0400],[38.7400,9.0400],[38.7400,9.0300]]]}';

  @override
  void dispose() {
    _farmNameController.dispose(); _kebeleCodeController.dispose();
    _regionController.dispose(); _polygonController.dispose();
    _pageController.dispose(); super.dispose();
  }

  void _nextStep() {
    if (_currentStep == 0 && !_validateStep1()) return;
    if (_currentStep == 1 && !_validateStep2()) return;
    setState(() => _currentStep++);
    _pageController.nextPage(duration: const Duration(milliseconds: 300), curve: Curves.easeInOut);
  }

  void _previousStep() {
    setState(() => _currentStep--);
    _pageController.previousPage(duration: const Duration(milliseconds: 300), curve: Curves.easeInOut);
  }

  bool _validateStep1() {
    if (_kebeleCodeController.text.trim().isEmpty) { _showError(_t('kebele')); return false; }
    if (_regionController.text.trim().isEmpty) { _showError(_t('region')); return false; }
    return true;
  }

  String? _buildGeoJsonPolygon() {
    final raw = _polygonController.text.trim().isNotEmpty ? _polygonController.text.trim() : _samplePolygon;
    try {
      final json = jsonDecode(raw);
      if (json is! Map || json['type'] != 'Polygon') return null;
      final coords = json['coordinates'];
      if (coords is! List || coords.isEmpty) return null;
      return raw;
    } catch (_) { return null; }
  }

  bool _validateStep2() {
    if (_buildGeoJsonPolygon() == null) { _showError(_t('invalidGeo')); return false; }
    return true;
  }

  void _showError(String message) {
    ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text(message), backgroundColor: Colors.red));
  }

  Future<void> _submitFarm() async {
    setState(() { _isLoading = true; _error = null; });
    final geoJson = _buildGeoJsonPolygon();
    if (geoJson == null) {
      setState(() { _isLoading = false; _error = _t('invalidGeo'); }); return;
    }
    final result = await _farmService.registerFarm(
      farmName: _farmNameController.text.trim().isNotEmpty ? _farmNameController.text.trim() : null,
      cropType: _selectedCropType,
      kebeleCode: _kebeleCodeController.text.trim(),
      region: _regionController.text.trim(),
      expectedHarvestDate: _expectedHarvestDate.toIso8601String().split('T')[0],
      geoJsonPolygon: geoJson,
    );
    if (mounted) {
      setState(() => _isLoading = false);
      if (result['success'] == true) {
        ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(content: Text(_t('successMsg')), backgroundColor: Colors.green));
        Navigator.pop(context, true);
      } else { setState(() => _error = result['message']); }
    }
  }

  @override
  Widget build(BuildContext context) {
    context.watch<LanguageService>();
    return Scaffold(
      appBar: AppBar(title: Text(_t('title')), backgroundColor: Colors.green, foregroundColor: Colors.white),
      body: Column(children: [
        _buildStepIndicator(),
        Expanded(child: PageView(
          controller: _pageController,
          physics: const NeverScrollableScrollPhysics(),
          children: [_buildStep1(), _buildStep2(), _buildStep3Review()],
        )),
      ]),
    );
  }

  Widget _buildStepIndicator() {
    final steps = [_t('stepFarmInfo'), _t('stepLocation'), _t('stepReview')];
    return Container(
      padding: const EdgeInsets.symmetric(vertical: 16, horizontal: 24),
      color: Colors.green.shade50,
      child: Row(children: List.generate(steps.length, (index) {
        final isActive = index == _currentStep;
        final isDone = index < _currentStep;
        return Expanded(child: Row(children: [
          CircleAvatar(
            radius: 16,
            backgroundColor: isDone || isActive ? Colors.green : Colors.grey.shade300,
            child: isDone
                ? const Icon(Icons.check, color: Colors.white, size: 16)
                : Text('${index + 1}', style: TextStyle(
                color: isActive ? Colors.white : Colors.grey,
                fontWeight: FontWeight.bold, fontSize: 12)),
          ),
          const SizedBox(width: 6),
          Expanded(child: Text(steps[index], style: TextStyle(
              fontSize: 12,
              fontWeight: isActive ? FontWeight.bold : FontWeight.normal,
              color: isActive ? Colors.green : Colors.grey))),
          if (index < steps.length - 1)
            Expanded(child: Divider(color: isDone ? Colors.green : Colors.grey.shade300, thickness: 2)),
        ]));
      })),
    );
  }

  Widget _buildStep1() {
    return SingleChildScrollView(
      padding: const EdgeInsets.all(24),
      child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
        Text(_t('farmInfo'), style: const TextStyle(fontSize: 22, fontWeight: FontWeight.bold)),
        const SizedBox(height: 8),
        Text(_t('farmInfoDesc'), style: TextStyle(color: Colors.grey.shade600)),
        const SizedBox(height: 24),
        TextField(controller: _farmNameController,
            decoration: InputDecoration(labelText: _t('farmName'), hintText: _t('farmNameHint'),
                border: const OutlineInputBorder(), prefixIcon: const Icon(Icons.agriculture))),
        const SizedBox(height: 16),
        DropdownButtonFormField<String>(
          value: _selectedCropType,
          decoration: InputDecoration(labelText: _t('cropType'), border: const OutlineInputBorder(), prefixIcon: const Icon(Icons.grass)),
          items: _cropTypes.map((c) => DropdownMenuItem(value: c, child: Text(c))).toList(),
          onChanged: (val) => setState(() => _selectedCropType = val!),
        ),
        const SizedBox(height: 16),
        TextField(controller: _kebeleCodeController,
            decoration: InputDecoration(labelText: _t('kebele'), hintText: _t('kebeleHint'),
                border: const OutlineInputBorder(), prefixIcon: const Icon(Icons.map))),
        const SizedBox(height: 16),
        Autocomplete<String>(
          optionsBuilder: (v) => v.text.isEmpty ? _ethiopianRegions
              : _ethiopianRegions.where((r) => r.toLowerCase().contains(v.text.toLowerCase())),
          onSelected: (value) => _regionController.text = value,
          fieldViewBuilder: (context, controller, focusNode, onSubmit) {
            controller.text = _regionController.text;
            controller.addListener(() => _regionController.text = controller.text);
            return TextField(controller: controller, focusNode: focusNode,
                decoration: InputDecoration(labelText: _t('region'), hintText: _t('regionHint'),
                    border: const OutlineInputBorder(), prefixIcon: const Icon(Icons.location_city)));
          },
        ),
        const SizedBox(height: 16),
        InkWell(
          onTap: () async {
            final date = await showDatePicker(context: context, initialDate: _expectedHarvestDate,
                firstDate: DateTime.now().add(const Duration(days: 30)),
                lastDate: DateTime.now().add(const Duration(days: 365)));
            if (date != null) setState(() => _expectedHarvestDate = date);
          },
          child: InputDecorator(
            decoration: InputDecoration(labelText: _t('harvestDate'),
                border: const OutlineInputBorder(), prefixIcon: const Icon(Icons.calendar_today)),
            child: Text('${_expectedHarvestDate.year}-${_expectedHarvestDate.month.toString().padLeft(2,'0')}-${_expectedHarvestDate.day.toString().padLeft(2,'0')}'),
          ),
        ),
        const SizedBox(height: 32),
        SizedBox(width: double.infinity, child: ElevatedButton(
          onPressed: _nextStep,
          style: ElevatedButton.styleFrom(backgroundColor: Colors.green, foregroundColor: Colors.white,
              padding: const EdgeInsets.symmetric(vertical: 16), shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8))),
          child: Text(_t('nextLocation'), style: const TextStyle(fontSize: 16)),
        )),
      ]),
    );
  }

  Widget _buildStep2() {
    return SingleChildScrollView(
      padding: const EdgeInsets.all(24),
      child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
        Text(_t('location'), style: const TextStyle(fontSize: 22, fontWeight: FontWeight.bold)),
        const SizedBox(height: 8),
        Text(_t('locationDesc'), style: TextStyle(color: Colors.grey.shade600)),
        const SizedBox(height: 16),
        Container(
          padding: const EdgeInsets.all(12),
          decoration: BoxDecoration(color: Colors.blue.shade50, borderRadius: BorderRadius.circular(8),
              border: Border.all(color: Colors.blue.shade200)),
          child: Row(children: [
            const Icon(Icons.info_outline, color: Colors.blue, size: 20), const SizedBox(width: 8),
            Expanded(child: Text(_t('infoMsg'), style: const TextStyle(color: Colors.blue, fontSize: 13))),
          ]),
        ),
        const SizedBox(height: 20),
        TextField(controller: _polygonController, maxLines: 6,
            decoration: InputDecoration(labelText: _t('geoJsonLabel'),
                hintText: '{"type":"Polygon","coordinates":[[[38.74,9.03],[38.75,9.03],[38.75,9.04],[38.74,9.04],[38.74,9.03]]]}',
                border: const OutlineInputBorder(), prefixIcon: const Icon(Icons.satellite_alt))),
        const SizedBox(height: 12),
        OutlinedButton.icon(
          onPressed: () {
            _polygonController.text = _samplePolygon;
            ScaffoldMessenger.of(context).showSnackBar(
                SnackBar(content: Text(_t('sampleLoaded')), backgroundColor: Colors.orange));
          },
          style: OutlinedButton.styleFrom(side: const BorderSide(color: Colors.green), foregroundColor: Colors.green),
          icon: const Icon(Icons.my_location), label: Text(_t('sampleCoords')),
        ),
        const SizedBox(height: 32),
        Row(children: [
          Expanded(child: OutlinedButton(onPressed: _previousStep,
              style: OutlinedButton.styleFrom(padding: const EdgeInsets.symmetric(vertical: 16),
                  side: const BorderSide(color: Colors.green)),
              child: Text(_t('back'), style: const TextStyle(color: Colors.green)))),
          const SizedBox(width: 16),
          Expanded(child: ElevatedButton(onPressed: _nextStep,
              style: ElevatedButton.styleFrom(backgroundColor: Colors.green, foregroundColor: Colors.white,
                  padding: const EdgeInsets.symmetric(vertical: 16), shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8))),
              child: Text(_t('nextReview'), style: const TextStyle(fontSize: 16)))),
        ]),
      ]),
    );
  }

  Widget _buildStep3Review() {
    return SingleChildScrollView(
      padding: const EdgeInsets.all(24),
      child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
        Text(_t('review'), style: const TextStyle(fontSize: 22, fontWeight: FontWeight.bold)),
        const SizedBox(height: 8),
        Text(_t('reviewDesc'), style: TextStyle(color: Colors.grey.shade600)),
        const SizedBox(height: 24),
        Card(shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)), child: Padding(
          padding: const EdgeInsets.all(16),
          child: Column(children: [
            _buildReviewRow(_t('farmNameLabel'), _farmNameController.text.isNotEmpty ? _farmNameController.text : _t('notSet')),
            _buildReviewRow(_t('cropType'), _selectedCropType),
            _buildReviewRow(_t('kebele'), _kebeleCodeController.text),
            _buildReviewRow(_t('region'), _regionController.text),
            _buildReviewRow(_t('harvestDate'),
                '${_expectedHarvestDate.year}-${_expectedHarvestDate.month.toString().padLeft(2,'0')}-${_expectedHarvestDate.day.toString().padLeft(2,'0')}'),
            _buildReviewRow(_t('gpsPolygon'),
                _polygonController.text.isNotEmpty ? _t('provided') : _t('sampleUsed')),
          ]),
        )),
        if (_error != null) ...[
          const SizedBox(height: 16),
          Container(padding: const EdgeInsets.all(12),
              decoration: BoxDecoration(color: Colors.red.shade50, borderRadius: BorderRadius.circular(8),
                  border: Border.all(color: Colors.red.shade200)),
              child: Row(children: [const Icon(Icons.error_outline, color: Colors.red), const SizedBox(width: 8),
                Expanded(child: Text(_error!, style: const TextStyle(color: Colors.red)))])),
        ],
        const SizedBox(height: 32),
        Row(children: [
          Expanded(child: OutlinedButton(onPressed: _isLoading ? null : _previousStep,
              style: OutlinedButton.styleFrom(padding: const EdgeInsets.symmetric(vertical: 16),
                  side: const BorderSide(color: Colors.green)),
              child: Text(_t('back'), style: const TextStyle(color: Colors.green)))),
          const SizedBox(width: 16),
          Expanded(child: ElevatedButton(onPressed: _isLoading ? null : _submitFarm,
              style: ElevatedButton.styleFrom(backgroundColor: Colors.green, foregroundColor: Colors.white,
                  padding: const EdgeInsets.symmetric(vertical: 16), shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8))),
              child: _isLoading
                  ? const SizedBox(height: 20, width: 20, child: CircularProgressIndicator(color: Colors.white, strokeWidth: 2))
                  : Text(_t('submitFarm'), style: const TextStyle(fontSize: 16)))),
        ]),
      ]),
    );
  }

  Widget _buildReviewRow(String label, String value) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 8),
      child: Row(crossAxisAlignment: CrossAxisAlignment.start, children: [
        SizedBox(width: 130, child: Text(label, style: TextStyle(color: Colors.grey.shade600, fontWeight: FontWeight.w500))),
        Expanded(child: Text(value, style: const TextStyle(fontWeight: FontWeight.bold))),
      ]),
    );
  }
}
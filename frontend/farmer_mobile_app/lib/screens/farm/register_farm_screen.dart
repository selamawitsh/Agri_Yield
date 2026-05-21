import 'package:flutter/material.dart';
import '../../services/farm_service.dart';

class RegisterFarmScreen extends StatefulWidget {
  const RegisterFarmScreen({super.key});

  @override
  State<RegisterFarmScreen> createState() => _RegisterFarmScreenState();
}

class _RegisterFarmScreenState extends State<RegisterFarmScreen> {
  final _farmService = FarmService();
  final _pageController = PageController();
  int _currentStep = 0;

  // Step 1 controllers
  final _farmNameController = TextEditingController();
  final _kebeleCodeController = TextEditingController();
  final _regionController = TextEditingController();
  String _selectedCropType = 'WHEAT';
  DateTime _expectedHarvestDate = DateTime.now().add(const Duration(days: 120));

  // Step 2 — simplified polygon input
  final _polygonController = TextEditingController();

  bool _isLoading = false;
  String? _error;

  final List<String> _cropTypes = [
    'WHEAT', 'TEFF', 'BARLEY', 'MAIZE', 'SORGHUM', 'COFFEE', 'BEANS', 'MILLET'
  ];

  final List<String> _ethiopianRegions = [
    'Addis Ababa', 'Afar', 'Amhara', 'Benishangul-Gumuz',
    'Dire Dawa', 'Gambela', 'Harari', 'Oromia',
    'Sidama', 'Somali', 'South Ethiopia', 'Tigray',
    'South West Ethiopia'
  ];

  @override
  void dispose() {
    _farmNameController.dispose();
    _kebeleCodeController.dispose();
    _regionController.dispose();
    _polygonController.dispose();
    _pageController.dispose();
    super.dispose();
  }

  void _nextStep() {
    if (_currentStep == 0 && !_validateStep1()) return;
    if (_currentStep == 1 && !_validateStep2()) return;

    setState(() => _currentStep++);
    _pageController.nextPage(
      duration: const Duration(milliseconds: 300),
      curve: Curves.easeInOut,
    );
  }

  void _previousStep() {
    setState(() => _currentStep--);
    _pageController.previousPage(
      duration: const Duration(milliseconds: 300),
      curve: Curves.easeInOut,
    );
  }

  bool _validateStep1() {
    if (_kebeleCodeController.text.trim().isEmpty) {
      _showError('Please enter your kebele code');
      return false;
    }
    if (_regionController.text.trim().isEmpty) {
      _showError('Please select your region');
      return false;
    }
    return true;
  }

  bool _validateStep2() {
    if (_polygonController.text.trim().isEmpty) {
      _showError('Please enter farm GPS coordinates');
      return false;
    }
    return true;
  }

  void _showError(String message) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(content: Text(message), backgroundColor: Colors.red),
    );
  }

  Future<void> _submitFarm() async {
    setState(() {
      _isLoading = true;
      _error = null;
    });

    // Build a simple GeoJSON polygon from the input
    // In production this comes from GPS polygon mapping on a map
    final geoJson = _polygonController.text.trim().isNotEmpty
        ? _polygonController.text.trim()
        : '{"type":"Polygon","coordinates":[[[38.74,9.03],[38.75,9.03],[38.75,9.04],[38.74,9.03],[38.74,9.03]]]}';

    final result = await _farmService.registerFarm(
      farmName: _farmNameController.text.trim().isNotEmpty
          ? _farmNameController.text.trim()
          : null,
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
          const SnackBar(
            content: Text('Farm registered successfully!'),
            backgroundColor: Colors.green,
          ),
        );
        Navigator.pop(context, true);
      } else {
        setState(() => _error = result['message']);
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Register Farm'),
        backgroundColor: Colors.green,
        foregroundColor: Colors.white,
      ),
      body: Column(
        children: [
          _buildStepIndicator(),
          Expanded(
            child: PageView(
              controller: _pageController,
              physics: const NeverScrollableScrollPhysics(),
              children: [
                _buildStep1(),
                _buildStep2(),
                _buildStep3Review(),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildStepIndicator() {
    final steps = ['Farm Info', 'Location', 'Review'];
    return Container(
      padding: const EdgeInsets.symmetric(vertical: 16, horizontal: 24),
      color: Colors.green.shade50,
      child: Row(
        children: List.generate(steps.length, (index) {
          final isActive = index == _currentStep;
          final isDone = index < _currentStep;
          return Expanded(
            child: Row(
              children: [
                CircleAvatar(
                  radius: 16,
                  backgroundColor: isDone
                      ? Colors.green
                      : isActive
                          ? Colors.green
                          : Colors.grey.shade300,
                  child: isDone
                      ? const Icon(Icons.check, color: Colors.white, size: 16)
                      : Text(
                          '${index + 1}',
                          style: TextStyle(
                            color: isActive ? Colors.white : Colors.grey,
                            fontWeight: FontWeight.bold,
                            fontSize: 12,
                          ),
                        ),
                ),
                const SizedBox(width: 6),
                Expanded(
                  child: Text(
                    steps[index],
                    style: TextStyle(
                      fontSize: 12,
                      fontWeight: isActive
                          ? FontWeight.bold
                          : FontWeight.normal,
                      color: isActive ? Colors.green : Colors.grey,
                    ),
                  ),
                ),
                if (index < steps.length - 1)
                  Expanded(
                    child: Divider(
                      color: isDone ? Colors.green : Colors.grey.shade300,
                      thickness: 2,
                    ),
                  ),
              ],
            ),
          );
        }),
      ),
    );
  }

  Widget _buildStep1() {
    return SingleChildScrollView(
      padding: const EdgeInsets.all(24),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Text(
            'Farm Information',
            style: TextStyle(fontSize: 22, fontWeight: FontWeight.bold),
          ),
          const SizedBox(height: 8),
          Text(
            'Tell us about your farm and what you plan to grow',
            style: TextStyle(color: Colors.grey.shade600),
          ),
          const SizedBox(height: 24),

          // Farm name (optional)
          TextField(
            controller: _farmNameController,
            decoration: const InputDecoration(
              labelText: 'Farm Name (optional)',
              hintText: 'e.g. My Teff Farm',
              border: OutlineInputBorder(),
              prefixIcon: Icon(Icons.agriculture),
            ),
          ),
          const SizedBox(height: 16),

          // Crop type
          DropdownButtonFormField<String>(
            value: _selectedCropType,
            decoration: const InputDecoration(
              labelText: 'Crop Type *',
              border: OutlineInputBorder(),
              prefixIcon: Icon(Icons.grass),
            ),
            items: _cropTypes
                .map((c) => DropdownMenuItem(value: c, child: Text(c)))
                .toList(),
            onChanged: (val) => setState(() => _selectedCropType = val!),
          ),
          const SizedBox(height: 16),

          // Kebele code
          TextField(
            controller: _kebeleCodeController,
            decoration: const InputDecoration(
              labelText: 'Kebele Code *',
              hintText: 'e.g. ORM-001',
              border: OutlineInputBorder(),
              prefixIcon: Icon(Icons.map),
            ),
          ),
          const SizedBox(height: 16),

          // Region
          Autocomplete<String>(
            optionsBuilder: (textEditingValue) {
              if (textEditingValue.text.isEmpty) return _ethiopianRegions;
              return _ethiopianRegions.where((r) => r.toLowerCase()
                  .contains(textEditingValue.text.toLowerCase()));
            },
            onSelected: (value) => _regionController.text = value,
            fieldViewBuilder: (context, controller, focusNode, onSubmit) {
              _regionController.text = controller.text;
              return TextField(
                controller: controller,
                focusNode: focusNode,
                decoration: const InputDecoration(
                  labelText: 'Region *',
                  hintText: 'e.g. Oromia',
                  border: OutlineInputBorder(),
                  prefixIcon: Icon(Icons.location_city),
                ),
              );
            },
          ),
          const SizedBox(height: 16),

          // Expected harvest date
          InkWell(
            onTap: () async {
              final date = await showDatePicker(
                context: context,
                initialDate: _expectedHarvestDate,
                firstDate: DateTime.now().add(const Duration(days: 30)),
                lastDate: DateTime.now().add(const Duration(days: 365)),
              );
              if (date != null) {
                setState(() => _expectedHarvestDate = date);
              }
            },
            child: InputDecorator(
              decoration: const InputDecoration(
                labelText: 'Expected Harvest Date *',
                border: OutlineInputBorder(),
                prefixIcon: Icon(Icons.calendar_today),
              ),
              child: Text(
                '${_expectedHarvestDate.year}-'
                '${_expectedHarvestDate.month.toString().padLeft(2, '0')}-'
                '${_expectedHarvestDate.day.toString().padLeft(2, '0')}',
              ),
            ),
          ),
          const SizedBox(height: 32),

          SizedBox(
            width: double.infinity,
            child: ElevatedButton(
              onPressed: _nextStep,
              style: ElevatedButton.styleFrom(
                backgroundColor: Colors.green,
                foregroundColor: Colors.white,
                padding: const EdgeInsets.symmetric(vertical: 16),
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(8),
                ),
              ),
              child: const Text('Next: Location', style: TextStyle(fontSize: 16)),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildStep2() {
    return SingleChildScrollView(
      padding: const EdgeInsets.all(24),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Text(
            'Farm Location',
            style: TextStyle(fontSize: 22, fontWeight: FontWeight.bold),
          ),
          const SizedBox(height: 8),
          Text(
            'Enter your farm GPS boundary as GeoJSON',
            style: TextStyle(color: Colors.grey.shade600),
          ),
          const SizedBox(height: 16),

          Container(
            padding: const EdgeInsets.all(12),
            decoration: BoxDecoration(
              color: Colors.blue.shade50,
              borderRadius: BorderRadius.circular(8),
              border: Border.all(color: Colors.blue.shade200),
            ),
            child: const Row(
              children: [
                Icon(Icons.info_outline, color: Colors.blue, size: 20),
                SizedBox(width: 8),
                Expanded(
                  child: Text(
                    'GPS boundary will be verified by satellite after registration. '
                    'Area will be calculated automatically.',
                    style: TextStyle(color: Colors.blue, fontSize: 13),
                  ),
                ),
              ],
            ),
          ),
          const SizedBox(height: 20),

          TextField(
            controller: _polygonController,
            maxLines: 6,
            decoration: InputDecoration(
              labelText: 'GeoJSON Polygon',
              hintText: '{"type":"Polygon","coordinates":[[[38.74,9.03],'
                  '[38.75,9.03],[38.75,9.04],[38.74,9.03],[38.74,9.03]]]}',
              border: const OutlineInputBorder(),
              prefixIcon: const Icon(Icons.satellite_alt),
              helperText: 'Paste GeoJSON coordinates of your farm boundary',
              helperMaxLines: 2,
            ),
          ),
          const SizedBox(height: 12),

          // Use default placeholder button
          OutlinedButton.icon(
            onPressed: () {
              _polygonController.text =
                  '{"type":"Polygon","coordinates":[[[38.74,9.03],'
                  '[38.75,9.03],[38.75,9.04],[38.74,9.03],[38.74,9.03]]]}';
              ScaffoldMessenger.of(context).showSnackBar(
                const SnackBar(
                  content: Text('Sample coordinates loaded. '
                      'Replace with your actual farm boundary.'),
                  backgroundColor: Colors.orange,
                ),
              );
            },
            icon: const Icon(Icons.my_location),
            label: const Text('Use Sample Coordinates'),
          ),
          const SizedBox(height: 32),

          Row(
            children: [
              Expanded(
                child: OutlinedButton(
                  onPressed: _previousStep,
                  style: OutlinedButton.styleFrom(
                    padding: const EdgeInsets.symmetric(vertical: 16),
                    side: const BorderSide(color: Colors.green),
                  ),
                  child: const Text(
                    'Back',
                    style: TextStyle(color: Colors.green),
                  ),
                ),
              ),
              const SizedBox(width: 16),
              Expanded(
                child: ElevatedButton(
                  onPressed: _nextStep,
                  style: ElevatedButton.styleFrom(
                    backgroundColor: Colors.green,
                    foregroundColor: Colors.white,
                    padding: const EdgeInsets.symmetric(vertical: 16),
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(8),
                    ),
                  ),
                  child: const Text('Next: Review', style: TextStyle(fontSize: 16)),
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildStep3Review() {
    return SingleChildScrollView(
      padding: const EdgeInsets.all(24),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Text(
            'Review & Submit',
            style: TextStyle(fontSize: 22, fontWeight: FontWeight.bold),
          ),
          const SizedBox(height: 8),
          Text(
            'Please confirm your farm details before submitting',
            style: TextStyle(color: Colors.grey.shade600),
          ),
          const SizedBox(height: 24),

          Card(
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(12),
            ),
            child: Padding(
              padding: const EdgeInsets.all(16),
              child: Column(
                children: [
                  _buildReviewRow(
                    'Farm Name',
                    _farmNameController.text.isNotEmpty
                        ? _farmNameController.text
                        : 'Not set',
                  ),
                  _buildReviewRow('Crop Type', _selectedCropType),
                  _buildReviewRow('Kebele Code', _kebeleCodeController.text),
                  _buildReviewRow('Region', _regionController.text),
                  _buildReviewRow(
                    'Expected Harvest',
                    '${_expectedHarvestDate.year}-'
                    '${_expectedHarvestDate.month.toString().padLeft(2, '0')}-'
                    '${_expectedHarvestDate.day.toString().padLeft(2, '0')}',
                  ),
                  _buildReviewRow(
                    'GPS Polygon',
                    _polygonController.text.isNotEmpty
                        ? 'Provided ✓'
                        : 'Sample coordinates',
                  ),
                ],
              ),
            ),
          ),

          if (_error != null) ...[
            const SizedBox(height: 16),
            Container(
              padding: const EdgeInsets.all(12),
              decoration: BoxDecoration(
                color: Colors.red.shade50,
                borderRadius: BorderRadius.circular(8),
                border: Border.all(color: Colors.red.shade200),
              ),
              child: Row(
                children: [
                  const Icon(Icons.error_outline, color: Colors.red),
                  const SizedBox(width: 8),
                  Expanded(
                    child: Text(
                      _error!,
                      style: const TextStyle(color: Colors.red),
                    ),
                  ),
                ],
              ),
            ),
          ],

          const SizedBox(height: 32),

          Row(
            children: [
              Expanded(
                child: OutlinedButton(
                  onPressed: _isLoading ? null : _previousStep,
                  style: OutlinedButton.styleFrom(
                    padding: const EdgeInsets.symmetric(vertical: 16),
                    side: const BorderSide(color: Colors.green),
                  ),
                  child: const Text(
                    'Back',
                    style: TextStyle(color: Colors.green),
                  ),
                ),
              ),
              const SizedBox(width: 16),
              Expanded(
                child: ElevatedButton(
                  onPressed: _isLoading ? null : _submitFarm,
                  style: ElevatedButton.styleFrom(
                    backgroundColor: Colors.green,
                    foregroundColor: Colors.white,
                    padding: const EdgeInsets.symmetric(vertical: 16),
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(8),
                    ),
                  ),
                  child: _isLoading
                      ? const SizedBox(
                          height: 20,
                          width: 20,
                          child: CircularProgressIndicator(
                            color: Colors.white,
                            strokeWidth: 2,
                          ),
                        )
                      : const Text('Register Farm', style: TextStyle(fontSize: 16)),
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildReviewRow(String label, String value) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 8),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          SizedBox(
            width: 130,
            child: Text(
              label,
              style: TextStyle(
                color: Colors.grey.shade600,
                fontWeight: FontWeight.w500,
              ),
            ),
          ),
          Expanded(
            child: Text(
              value,
              style: const TextStyle(fontWeight: FontWeight.bold),
            ),
          ),
        ],
      ),
    );
  }
}

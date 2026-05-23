import 'package:flutter/material.dart';
import '../../models/farm_model.dart';
import '../../services/farm_service.dart';
import 'register_farm_screen.dart';
import 'farm_detail_screen.dart';

class MyFarmsScreen extends StatefulWidget {
  const MyFarmsScreen({super.key});

  @override
  State<MyFarmsScreen> createState() => _MyFarmsScreenState();
}

class _MyFarmsScreenState extends State<MyFarmsScreen> {
  final _farmService = FarmService();
  List<FarmModel> _farms = [];
  List<FarmModel> _filtered = [];
  bool _isLoading = true;
  String? _error;

  // Filter state
  String _searchText = '';
  String _selectedCropType = '';
  String _selectedStatus = '';
  bool _showFilters = false;

  final List<String> _cropTypes = [
    '', 'WHEAT', 'TEFF', 'BARLEY', 'MAIZE',
    'SORGHUM', 'COFFEE', 'BEANS', 'MILLET'
  ];

  final List<String> _statuses = [
    '', 'PENDING_VERIFICATION', 'VERIFIED',
    'ACTIVE', 'GROWING', 'HARVESTED', 'FAILED'
  ];

  @override
  void initState() {
    super.initState();
    _loadFarms();
  }

  Future<void> _loadFarms() async {
    setState(() { _isLoading = true; _error = null; });
    final result = await _farmService.getMyFarms();
    if (mounted) {
      setState(() {
        _isLoading = false;
        if (result['success'] == true) {
          _farms = result['farms'] as List<FarmModel>;
          _applyFilters();
        } else {
          _error = result['message'];
        }
      });
    }
  }

  void _applyFilters() {
    setState(() {
      _filtered = _farms.where((farm) {
        final matchesSearch = _searchText.isEmpty ||
            farm.displayName.toLowerCase()
                .contains(_searchText.toLowerCase()) ||
            farm.region.toLowerCase()
                .contains(_searchText.toLowerCase()) ||
            farm.kebeleCode.toLowerCase()
                .contains(_searchText.toLowerCase());

        final matchesCrop = _selectedCropType.isEmpty ||
            farm.cropType == _selectedCropType;

        final matchesStatus = _selectedStatus.isEmpty ||
            farm.status == _selectedStatus;

        return matchesSearch && matchesCrop && matchesStatus;
      }).toList();
    });
  }

  void _clearFilters() {
    setState(() {
      _searchText = '';
      _selectedCropType = '';
      _selectedStatus = '';
      _showFilters = false;
    });
    _applyFilters();
  }

  bool get _hasActiveFilters =>
      _searchText.isNotEmpty ||
      _selectedCropType.isNotEmpty ||
      _selectedStatus.isNotEmpty;

  Color _statusColor(String status) {
    switch (status) {
      case 'PENDING_VERIFICATION': return Colors.orange;
      case 'VERIFIED': return Colors.blue;
      case 'ACTIVE': return Colors.green;
      case 'GROWING': return Colors.green.shade700;
      case 'HARVESTED': return Colors.teal;
      case 'FAILED': return Colors.red;
      default: return Colors.grey;
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('My Farms'),
        backgroundColor: Colors.green,
        foregroundColor: Colors.white,
        actions: [
          Stack(
            children: [
              IconButton(
                icon: const Icon(Icons.filter_list),
                onPressed: () =>
                    setState(() => _showFilters = !_showFilters),
              ),
              if (_hasActiveFilters)
                Positioned(
                  right: 8,
                  top: 8,
                  child: Container(
                    width: 8, height: 8,
                    decoration: const BoxDecoration(
                      color: Colors.orange,
                      shape: BoxShape.circle,
                    ),
                  ),
                ),
            ],
          ),
          IconButton(
            icon: const Icon(Icons.refresh),
            onPressed: _loadFarms,
          ),
        ],
      ),
      floatingActionButton: FloatingActionButton.extended(
        onPressed: () async {
          final registered = await Navigator.push<bool>(
            context,
            MaterialPageRoute(
                builder: (_) => const RegisterFarmScreen()),
          );
          if (registered == true) _loadFarms();
        },
        backgroundColor: Colors.green,
        icon: const Icon(Icons.add, color: Colors.white),
        label: const Text('Register Farm',
            style: TextStyle(color: Colors.white)),
      ),
      body: Column(
        children: [
          // Search bar
          Container(
            padding:
                const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
            color: Colors.green.shade50,
            child: TextField(
              decoration: InputDecoration(
                hintText: 'Search by name, region, kebele...',
                prefixIcon: const Icon(Icons.search, color: Colors.green),
                suffixIcon: _searchText.isNotEmpty
                    ? IconButton(
                        icon: const Icon(Icons.clear),
                        onPressed: () {
                          setState(() => _searchText = '');
                          _applyFilters();
                        },
                      )
                    : null,
                filled: true,
                fillColor: Colors.white,
                border: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(8),
                  borderSide: BorderSide.none,
                ),
                contentPadding: const EdgeInsets.symmetric(vertical: 0),
              ),
              onChanged: (val) {
                setState(() => _searchText = val);
                _applyFilters();
              },
            ),
          ),

          // Filter panel
          if (_showFilters)
            Container(
              padding: const EdgeInsets.all(12),
              color: Colors.white,
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      const Text('Filters',
                          style: TextStyle(fontWeight: FontWeight.bold)),
                      if (_hasActiveFilters)
                        TextButton(
                          onPressed: _clearFilters,
                          child: const Text('Clear All',
                              style: TextStyle(color: Colors.red)),
                        ),
                    ],
                  ),
                  const SizedBox(height: 8),
                  Row(
                    children: [
                      Expanded(
                        child: DropdownButtonFormField<String>(
                          value: _selectedCropType,
                          decoration: const InputDecoration(
                            labelText: 'Crop Type',
                            border: OutlineInputBorder(),
                            isDense: true,
                          ),
                          items: _cropTypes.map((c) => DropdownMenuItem(
                            value: c,
                            child: Text(c.isEmpty ? 'All Crops' : c),
                          )).toList(),
                          onChanged: (val) {
                            setState(() =>
                                _selectedCropType = val ?? '');
                            _applyFilters();
                          },
                        ),
                      ),
                      const SizedBox(width: 8),
                      Expanded(
                        child: DropdownButtonFormField<String>(
                          value: _selectedStatus,
                          decoration: const InputDecoration(
                            labelText: 'Status',
                            border: OutlineInputBorder(),
                            isDense: true,
                          ),
                          items: _statuses.map((s) => DropdownMenuItem(
                            value: s,
                            child: Text(s.isEmpty
                                ? 'All Status'
                                : s.replaceAll('_', ' ')),
                          )).toList(),
                          onChanged: (val) {
                            setState(() =>
                                _selectedStatus = val ?? '');
                            _applyFilters();
                          },
                        ),
                      ),
                    ],
                  ),
                ],
              ),
            ),

          // Results count
          if (!_isLoading && _error == null)
            Container(
              padding: const EdgeInsets.symmetric(
                  horizontal: 16, vertical: 6),
              color: Colors.grey.shade50,
              child: Row(
                children: [
                  Text(
                    _hasActiveFilters
                        ? '${_filtered.length} of ${_farms.length} farms'
                        : '${_farms.length} farm${_farms.length != 1 ? 's' : ''}',
                    style: TextStyle(
                      color: Colors.grey.shade600,
                      fontSize: 13,
                    ),
                  ),
                ],
              ),
            ),

          Expanded(child: _buildBody()),
        ],
      ),
    );
  }

  Widget _buildBody() {
    if (_isLoading) {
      return const Center(
          child: CircularProgressIndicator(color: Colors.green));
    }

    if (_error != null) {
      return Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            const Icon(Icons.error_outline, size: 64, color: Colors.red),
            const SizedBox(height: 16),
            Text(_error!, textAlign: TextAlign.center),
            const SizedBox(height: 16),
            ElevatedButton(
              onPressed: _loadFarms,
              child: const Text('Retry'),
            ),
          ],
        ),
      );
    }

    if (_farms.isEmpty) {
      return Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(Icons.agriculture, size: 80, color: Colors.grey.shade400),
            const SizedBox(height: 16),
            Text(
              'No farms registered yet',
              style: TextStyle(
                fontSize: 18,
                color: Colors.grey.shade600,
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 8),
            Text(
              'Tap the button below to register your first farm',
              style: TextStyle(color: Colors.grey.shade500),
              textAlign: TextAlign.center,
            ),
          ],
        ),
      );
    }

    if (_filtered.isEmpty) {
      return Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(Icons.search_off, size: 64, color: Colors.grey.shade400),
            const SizedBox(height: 16),
            const Text('No farms match your filters'),
            const SizedBox(height: 8),
            TextButton(
              onPressed: _clearFilters,
              child: const Text('Clear Filters',
                  style: TextStyle(color: Colors.green)),
            ),
          ],
        ),
      );
    }

    return RefreshIndicator(
      onRefresh: _loadFarms,
      color: Colors.green,
      child: ListView.builder(
        padding: const EdgeInsets.fromLTRB(16, 8, 16, 100),
        itemCount: _filtered.length,
        itemBuilder: (context, index) =>
            _buildFarmCard(_filtered[index]),
      ),
    );
  }

  Widget _buildFarmCard(FarmModel farm) {
    final statusColor = _statusColor(farm.status);
    return Card(
      margin: const EdgeInsets.only(bottom: 12),
      shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(12)),
      elevation: 2,
      child: InkWell(
        borderRadius: BorderRadius.circular(12),
        onTap: () => Navigator.push(
          context,
          MaterialPageRoute(
            builder: (_) => FarmDetailScreen(farmId: farm.id),
          ),
        ),
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Row(
                children: [
                  const Icon(Icons.agriculture,
                      color: Colors.green, size: 28),
                  const SizedBox(width: 10),
                  Expanded(
                    child: Text(
                      farm.displayName,
                      style: const TextStyle(
                        fontSize: 18,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                  ),
                  Container(
                    padding: const EdgeInsets.symmetric(
                        horizontal: 10, vertical: 4),
                    decoration: BoxDecoration(
                      color: statusColor.withOpacity(0.1),
                      borderRadius: BorderRadius.circular(20),
                      border: Border.all(color: statusColor),
                    ),
                    child: Text(
                      farm.statusLabel,
                      style: TextStyle(
                        color: statusColor,
                        fontSize: 12,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 12),
              Row(
                children: [
                  _buildChip(Icons.grass, farm.cropType),
                  const SizedBox(width: 8),
                  _buildChip(Icons.location_on, farm.region),
                ],
              ),
              const SizedBox(height: 8),
              Row(
                children: [
                  _buildChip(
                    Icons.straighten,
                    '${farm.areaHectares.toStringAsFixed(2)} ha',
                  ),
                  const SizedBox(width: 8),
                  if (farm.satelliteVerified)
                    _buildChip(
                      Icons.satellite_alt,
                      'Verified',
                      color: Colors.blue,
                    ),
                ],
              ),
              const SizedBox(height: 8),
              Row(
                mainAxisAlignment: MainAxisAlignment.end,
                children: [
                  Text(
                    'Tap to view details',
                    style: TextStyle(
                        color: Colors.grey.shade500, fontSize: 12),
                  ),
                  Icon(Icons.arrow_forward_ios,
                      size: 12, color: Colors.grey.shade400),
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildChip(IconData icon, String label, {Color? color}) {
    return Row(
      mainAxisSize: MainAxisSize.min,
      children: [
        Icon(icon, size: 16, color: color ?? Colors.grey.shade600),
        const SizedBox(width: 4),
        Text(
          label,
          style: TextStyle(
              color: color ?? Colors.grey.shade700, fontSize: 13),
        ),
      ],
    );
  }
}

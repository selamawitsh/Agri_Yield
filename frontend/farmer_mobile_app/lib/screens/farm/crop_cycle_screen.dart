import 'package:flutter/material.dart';
import '../../models/crop_cycle_model.dart';
import '../../services/farm_service.dart';

class CropCycleScreen extends StatefulWidget {
  final String farmId;
  const CropCycleScreen({super.key, required this.farmId});

  @override
  State<CropCycleScreen> createState() => _CropCycleScreenState();
}

class _CropCycleScreenState extends State<CropCycleScreen> {
  final _farmService = FarmService();
  List<CropCycleModel> _cycles = [];
  bool _isLoading = true;
  String? _error;

  @override
  void initState() {
    super.initState();
    _loadCycles();
  }

  Future<void> _loadCycles() async {
    setState(() { _isLoading = true; _error = null; });
    final result = await _farmService.getCropCycles(widget.farmId);
    if (mounted) {
      setState(() {
        _isLoading = false;
        if (result['success'] == true) {
          _cycles = result['cropCycles'] as List<CropCycleModel>;
        } else {
          _error = result['message'];
        }
      });
    }
  }

  Future<void> _showCreateDialog() async {
    DateTime selectedDate =
        DateTime.now().add(const Duration(days: 120));
    final seasonController = TextEditingController();

    await showDialog(
      context: context,
      builder: (ctx) => StatefulBuilder(
        builder: (ctx, setDialogState) => AlertDialog(
          title: const Text('New Crop Season'),
          content: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              TextField(
                controller: seasonController,
                decoration: const InputDecoration(
                  labelText: 'Season Name (optional)',
                  hintText: 'e.g. Kiremt_2027',
                  border: OutlineInputBorder(),
                ),
              ),
              const SizedBox(height: 16),
              InkWell(
                onTap: () async {
                  final date = await showDatePicker(
                    context: ctx,
                    initialDate: selectedDate,
                    firstDate:
                        DateTime.now().add(const Duration(days: 30)),
                    lastDate:
                        DateTime.now().add(const Duration(days: 730)),
                  );
                  if (date != null) {
                    setDialogState(() => selectedDate = date);
                  }
                },
                child: InputDecorator(
                  decoration: const InputDecoration(
                    labelText: 'Expected Harvest Date *',
                    border: OutlineInputBorder(),
                    prefixIcon: Icon(Icons.calendar_today),
                  ),
                  child: Text(
                    '${selectedDate.year}-'
                    '${selectedDate.month.toString().padLeft(2, '0')}-'
                    '${selectedDate.day.toString().padLeft(2, '0')}',
                  ),
                ),
              ),
            ],
          ),
          actions: [
            TextButton(
              onPressed: () => Navigator.pop(ctx),
              child: const Text('Cancel'),
            ),
            ElevatedButton(
              onPressed: () async {
                Navigator.pop(ctx);
                final dateStr =
                    '${selectedDate.year}-'
                    '${selectedDate.month.toString().padLeft(2, '0')}-'
                    '${selectedDate.day.toString().padLeft(2, '0')}';
                final result = await _farmService.createCropCycle(
                  farmId: widget.farmId,
                  expectedHarvestDate: dateStr,
                  seasonName: seasonController.text.isNotEmpty
                      ? seasonController.text
                      : null,
                );
                if (mounted) {
                  if (result['success'] == true) {
                    ScaffoldMessenger.of(context).showSnackBar(
                      const SnackBar(
                        content: Text('New season created!'),
                        backgroundColor: Colors.green,
                      ),
                    );
                    _loadCycles();
                  } else {
                    ScaffoldMessenger.of(context).showSnackBar(
                      SnackBar(
                        content: Text(result['message'] ?? 'Failed'),
                        backgroundColor: Colors.red,
                      ),
                    );
                  }
                }
              },
              style: ElevatedButton.styleFrom(
                backgroundColor: Colors.green,
                foregroundColor: Colors.white,
              ),
              child: const Text('Create'),
            ),
          ],
        ),
      ),
    );
  }

  Color _cycleStatusColor(String status) {
    switch (status) {
      case 'PLANNING': return Colors.orange;
      case 'FUNDED': return Colors.blue;
      case 'PLANTED': return Colors.lightGreen;
      case 'GROWING': return Colors.green;
      case 'HARVESTED': return Colors.teal;
      case 'FAILED': return Colors.red;
      default: return Colors.grey;
    }
  }

  IconData _cycleStatusIcon(String status) {
    switch (status) {
      case 'PLANNING': return Icons.pending_outlined;
      case 'FUNDED': return Icons.account_balance_wallet;
      case 'PLANTED': return Icons.grass;
      case 'GROWING': return Icons.eco;
      case 'HARVESTED': return Icons.agriculture;
      case 'FAILED': return Icons.cancel_outlined;
      default: return Icons.help_outline;
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Crop Seasons'),
        backgroundColor: Colors.green,
        foregroundColor: Colors.white,
        actions: [
          IconButton(
            icon: const Icon(Icons.refresh),
            onPressed: _loadCycles,
          ),
        ],
      ),
      floatingActionButton: FloatingActionButton.extended(
        onPressed: _showCreateDialog,
        backgroundColor: Colors.green,
        icon: const Icon(Icons.add, color: Colors.white),
        label: const Text('New Season',
            style: TextStyle(color: Colors.white)),
      ),
      body: _isLoading
          ? const Center(
              child: CircularProgressIndicator(color: Colors.green))
          : _error != null
              ? Center(
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      const Icon(Icons.error_outline,
                          size: 48, color: Colors.red),
                      const SizedBox(height: 12),
                      Text(_error!),
                      ElevatedButton(
                        onPressed: _loadCycles,
                        child: const Text('Retry'),
                      ),
                    ],
                  ),
                )
              : _cycles.isEmpty
                  ? Center(
                      child: Column(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          Icon(Icons.loop,
                              size: 64, color: Colors.grey.shade400),
                          const SizedBox(height: 16),
                          const Text('No seasons yet'),
                          const SizedBox(height: 8),
                          ElevatedButton.icon(
                            onPressed: _showCreateDialog,
                            icon: const Icon(Icons.add),
                            label: const Text('Create First Season'),
                            style: ElevatedButton.styleFrom(
                              backgroundColor: Colors.green,
                              foregroundColor: Colors.white,
                            ),
                          ),
                        ],
                      ),
                    )
                  : RefreshIndicator(
                      onRefresh: _loadCycles,
                      color: Colors.green,
                      child: ListView.builder(
                        padding: const EdgeInsets.all(16),
                        itemCount: _cycles.length,
                        itemBuilder: (context, index) {
                          final cycle = _cycles[index];
                          final statusColor =
                              _cycleStatusColor(cycle.status);
                          final statusIcon =
                              _cycleStatusIcon(cycle.status);

                          return Card(
                            margin: const EdgeInsets.only(bottom: 12),
                            shape: RoundedRectangleBorder(
                                borderRadius: BorderRadius.circular(12)),
                            elevation: 2,
                            child: Padding(
                              padding: const EdgeInsets.all(16),
                              child: Column(
                                crossAxisAlignment:
                                    CrossAxisAlignment.start,
                                children: [
                                  Row(
                                    children: [
                                      CircleAvatar(
                                        backgroundColor:
                                            statusColor.withOpacity(0.1),
                                        child: Icon(statusIcon,
                                            color: statusColor, size: 20),
                                      ),
                                      const SizedBox(width: 12),
                                      Expanded(
                                        child: Text(
                                          cycle.seasonName,
                                          style: const TextStyle(
                                            fontSize: 16,
                                            fontWeight: FontWeight.bold,
                                          ),
                                        ),
                                      ),
                                      Container(
                                        padding:
                                            const EdgeInsets.symmetric(
                                                horizontal: 10,
                                                vertical: 4),
                                        decoration: BoxDecoration(
                                          color: statusColor
                                              .withOpacity(0.1),
                                          borderRadius:
                                              BorderRadius.circular(20),
                                          border: Border.all(
                                              color: statusColor),
                                        ),
                                        child: Text(
                                          cycle.statusLabel,
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
                                  const Divider(),
                                  const SizedBox(height: 8),
                                  _buildCycleRow(
                                    Icons.calendar_today,
                                    'Expected Harvest',
                                    cycle.expectedHarvestDate,
                                  ),
                                  if (cycle.plantingDate != null)
                                    _buildCycleRow(
                                      Icons.grass,
                                      'Planting Date',
                                      cycle.plantingDate!,
                                    ),
                                  if (cycle.actualHarvestDate != null)
                                    _buildCycleRow(
                                      Icons.agriculture,
                                      'Actual Harvest',
                                      cycle.actualHarvestDate!,
                                    ),
                                ],
                              ),
                            ),
                          );
                        },
                      ),
                    ),
    );
  }

  Widget _buildCycleRow(IconData icon, String label, String value) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 4),
      child: Row(
        children: [
          Icon(icon, size: 16, color: Colors.grey.shade600),
          const SizedBox(width: 8),
          Text(
            '$label: ',
            style: TextStyle(
                color: Colors.grey.shade600, fontSize: 13),
          ),
          Text(
            value,
            style: const TextStyle(
                fontWeight: FontWeight.bold, fontSize: 13),
          ),
        ],
      ),
    );
  }
}

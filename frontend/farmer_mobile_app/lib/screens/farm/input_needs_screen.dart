import 'package:flutter/material.dart';
import '../../services/farm_service.dart';

class InputNeedsScreen extends StatefulWidget {
  final String farmId;

  const InputNeedsScreen({super.key, required this.farmId});

  @override
  State<InputNeedsScreen> createState() => _InputNeedsScreenState();
}

class _InputNeedsScreenState extends State<InputNeedsScreen> {
  final _farmService = FarmService();
  bool _isLoading = false;
  String? _error;

  final List<Map<String, dynamic>> _items = [];

  final List<String> _categories = [
    'SEED', 'FERTILIZER', 'PESTICIDE', 'TOOL', 'OTHER'
  ];

  final List<String> _units = ['kg', 'litre', 'bag', 'piece'];

  void _addItem() {
    setState(() {
      _items.add({
        'productCategory': 'SEED',
        'productName': '',
        'quantity': '',
        'unit': 'kg',
        'estimatedPriceEtb': '',
        'sequenceOrder': _items.length + 1,
      });
    });
  }

  void _removeItem(int index) {
    setState(() {
      _items.removeAt(index);
      for (int i = 0; i < _items.length; i++) {
        _items[i]['sequenceOrder'] = i + 1;
      }
    });
  }

  bool _validateForm() {
    if (_items.isEmpty) {
      _showError('Please add at least one input item');
      return false;
    }
    for (int i = 0; i < _items.length; i++) {
      final item = _items[i];
      if ((item['productName'] as String).trim().isEmpty) {
        _showError('Item ${i + 1}: Product name is required');
        return false;
      }
      if ((item['quantity'] as String).trim().isEmpty) {
        _showError('Item ${i + 1}: Quantity is required');
        return false;
      }
      if ((item['estimatedPriceEtb'] as String).trim().isEmpty) {
        _showError('Item ${i + 1}: Price is required');
        return false;
      }
    }
    return true;
  }

  void _showError(String message) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(content: Text(message), backgroundColor: Colors.red),
    );
  }

  Future<void> _submitInputNeeds() async {
    if (!_validateForm()) return;

    setState(() {
      _isLoading = true;
      _error = null;
    });

    final formattedItems = _items.map((item) => {
      'productCategory': item['productCategory'],
      'productName': item['productName'],
      'quantity': double.tryParse(item['quantity']) ?? 0,
      'unit': item['unit'],
      'estimatedPriceEtb': double.tryParse(item['estimatedPriceEtb']) ?? 0,
      'sequenceOrder': item['sequenceOrder'],
    }).toList();

    final result = await _farmService.submitInputNeeds(
      farmId: widget.farmId,
      items: formattedItems,
    );

    if (mounted) {
      setState(() => _isLoading = false);
      if (result['success'] == true) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(
            content: Text('Input needs submitted successfully!'),
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
        title: const Text('Submit Input Needs'),
        backgroundColor: Colors.green,
        foregroundColor: Colors.white,
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Container(
              padding: const EdgeInsets.all(12),
              decoration: BoxDecoration(
                color: Colors.green.shade50,
                borderRadius: BorderRadius.circular(8),
                border: Border.all(color: Colors.green.shade200),
              ),
              child: const Row(
                children: [
                  Icon(Icons.info_outline, color: Colors.green, size: 20),
                  SizedBox(width: 8),
                  Expanded(
                    child: Text(
                      'List all agricultural inputs you need for this season. '
                      'Investors will fund these and vouchers will be generated.',
                      style: TextStyle(color: Colors.green, fontSize: 13),
                    ),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 20),

            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                const Text(
                  'Input Items',
                  style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
                ),
                ElevatedButton.icon(
                  onPressed: _addItem,
                  icon: const Icon(Icons.add, size: 18),
                  label: const Text('Add Item'),
                  style: ElevatedButton.styleFrom(
                    backgroundColor: Colors.green,
                    foregroundColor: Colors.white,
                    padding: const EdgeInsets.symmetric(
                        horizontal: 12, vertical: 8),
                  ),
                ),
              ],
            ),
            const SizedBox(height: 12),

            if (_items.isEmpty)
              Container(
                width: double.infinity,
                padding: const EdgeInsets.all(24),
                decoration: BoxDecoration(
                  border: Border.all(color: Colors.grey.shade300),
                  borderRadius: BorderRadius.circular(8),
                  color: Colors.grey.shade50,
                ),
                child: Column(
                  children: [
                    Icon(Icons.add_box_outlined,
                        size: 48, color: Colors.grey.shade400),
                    const SizedBox(height: 8),
                    Text('No items added yet',
                        style: TextStyle(color: Colors.grey.shade500)),
                    const SizedBox(height: 4),
                    Text(
                      'Tap "Add Item" to add seeds, fertilizers, etc.',
                      style: TextStyle(
                          color: Colors.grey.shade400, fontSize: 12),
                    ),
                  ],
                ),
              ),

            ...List.generate(_items.length,
                (index) => _buildItemCard(index, _items[index])),

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
                      child: Text(_error!,
                          style: const TextStyle(color: Colors.red)),
                    ),
                  ],
                ),
              ),
            ],

            const SizedBox(height: 24),

            if (_items.isNotEmpty) ...[
              Card(
                color: Colors.green.shade50,
                shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(8)),
                child: Padding(
                  padding: const EdgeInsets.all(16),
                  child: Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      const Text('Total Estimated Amount',
                          style: TextStyle(fontWeight: FontWeight.bold)),
                      Text(
                        'ETB ${_calculateTotal().toStringAsFixed(2)}',
                        style: const TextStyle(
                          fontWeight: FontWeight.bold,
                          fontSize: 18,
                          color: Colors.green,
                        ),
                      ),
                    ],
                  ),
                ),
              ),
              const SizedBox(height: 16),
            ],

            SizedBox(
              width: double.infinity,
              child: ElevatedButton(
                onPressed: _isLoading ? null : _submitInputNeeds,
                style: ElevatedButton.styleFrom(
                  backgroundColor: Colors.green,
                  foregroundColor: Colors.white,
                  padding: const EdgeInsets.symmetric(vertical: 16),
                  shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(8)),
                ),
                child: _isLoading
                    ? const SizedBox(
                        height: 20,
                        width: 20,
                        child: CircularProgressIndicator(
                            color: Colors.white, strokeWidth: 2))
                    : const Text('Submit Input Needs',
                        style: TextStyle(fontSize: 16)),
              ),
            ),
            const SizedBox(height: 32),
          ],
        ),
      ),
    );
  }

  Widget _buildItemCard(int index, Map<String, dynamic> item) {
    return Card(
      margin: const EdgeInsets.only(bottom: 12),
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text('Item ${index + 1}',
                    style: const TextStyle(
                        fontWeight: FontWeight.bold, fontSize: 16)),
                IconButton(
                  icon: const Icon(Icons.delete_outline, color: Colors.red),
                  onPressed: () => _removeItem(index),
                ),
              ],
            ),
            const SizedBox(height: 8),

            DropdownButtonFormField<String>(
              value: item['productCategory'],
              decoration: const InputDecoration(
                labelText: 'Category *',
                border: OutlineInputBorder(),
                isDense: true,
              ),
              items: _categories
                  .map((c) => DropdownMenuItem(value: c, child: Text(c)))
                  .toList(),
              onChanged: (val) =>
                  setState(() => item['productCategory'] = val!),
            ),
            const SizedBox(height: 10),

            TextField(
              decoration: const InputDecoration(
                labelText: 'Product Name *',
                hintText: 'e.g. Kakaba Wheat Seed',
                border: OutlineInputBorder(),
                isDense: true,
              ),
              onChanged: (val) => item['productName'] = val,
            ),
            const SizedBox(height: 10),

            Row(
              children: [
                Expanded(
                  flex: 2,
                  child: TextField(
                    keyboardType: TextInputType.number,
                    decoration: const InputDecoration(
                      labelText: 'Quantity *',
                      border: OutlineInputBorder(),
                      isDense: true,
                    ),
                    onChanged: (val) => item['quantity'] = val,
                  ),
                ),
                const SizedBox(width: 8),
                Expanded(
                  child: DropdownButtonFormField<String>(
                    value: item['unit'],
                    decoration: const InputDecoration(
                      labelText: 'Unit',
                      border: OutlineInputBorder(),
                      isDense: true,
                    ),
                    items: _units
                        .map((u) =>
                            DropdownMenuItem(value: u, child: Text(u)))
                        .toList(),
                    onChanged: (val) => setState(() => item['unit'] = val!),
                  ),
                ),
              ],
            ),
            const SizedBox(height: 10),

            TextField(
              keyboardType: TextInputType.number,
              decoration: const InputDecoration(
                labelText: 'Estimated Price (ETB) *',
                border: OutlineInputBorder(),
                isDense: true,
                prefixText: 'ETB ',
              ),
              onChanged: (val) => item['estimatedPriceEtb'] = val,
            ),
            const SizedBox(height: 8),

            Text(
              'Redemption order: ${item['sequenceOrder']}',
              style: TextStyle(
                fontSize: 12,
                color: Colors.grey.shade600,
                fontStyle: FontStyle.italic,
              ),
            ),
          ],
        ),
      ),
    );
  }

  double _calculateTotal() {
    double total = 0;
    for (final item in _items) {
      total += double.tryParse(item['estimatedPriceEtb'] ?? '') ?? 0;
    }
    return total;
  }
}

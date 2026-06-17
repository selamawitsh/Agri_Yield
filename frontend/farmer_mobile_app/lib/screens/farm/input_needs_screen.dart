// import 'package:flutter/material.dart';
// import '../../services/farm_service.dart';
//
// class InputNeedsScreen extends StatefulWidget {
//   final String farmId;
//
//   const InputNeedsScreen({super.key, required this.farmId});
//
//   @override
//   State<InputNeedsScreen> createState() => _InputNeedsScreenState();
// }
//
// class _InputNeedsScreenState extends State<InputNeedsScreen> {
//   final _farmService = FarmService();
//   bool _isLoading = false;
//   String? _error;
//
//   final List<Map<String, dynamic>> _items = [];
//
//   final List<String> _categories = [
//     'SEED', 'FERTILIZER', 'PESTICIDE', 'TOOL', 'OTHER'
//   ];
//
//   final List<String> _units = ['kg', 'litre', 'bag', 'piece'];
//
//   void _addItem() {
//     setState(() {
//       _items.add({
//         'productCategory': 'SEED',
//         'nameController': TextEditingController(),
//         'quantityController': TextEditingController(),
//         'unit': 'kg',
//         'priceController': TextEditingController(),
//         'sequenceOrder': _items.length + 1,
//       });
//     });
//   }
//
//   void _removeItem(int index) {
//     setState(() {
//       // Properly dispose controllers to prevent memory leaks
//       _items[index]['nameController']?.dispose();
//       _items[index]['quantityController']?.dispose();
//       _items[index]['priceController']?.dispose();
//
//       _items.removeAt(index);
//       for (int i = 0; i < _items.length; i++) {
//         _items[i]['sequenceOrder'] = i + 1;
//       }
//     });
//   }
//
//   bool _validateForm() {
//     if (_items.isEmpty) {
//       _showError('Please add at least one input item');
//       return false;
//     }
//     for (int i = 0; i < _items.length; i++) {
//       final item = _items[i];
//       final name = (item['nameController'] as TextEditingController).text;
//       final qty = (item['quantityController'] as TextEditingController).text;
//       final price = (item['priceController'] as TextEditingController).text;
//
//       if (name.trim().isEmpty) {
//         _showError('Item ${i + 1}: Product name is required');
//         return false;
//       }
//       if (qty.trim().isEmpty || double.tryParse(qty) == null) {
//         _showError('Item ${i + 1}: Enter a valid quantity');
//         return false;
//       }
//       if (price.trim().isEmpty || double.tryParse(price) == null) {
//         _showError('Item ${i + 1}: Enter a valid price');
//         return false;
//       }
//     }
//     return true;
//   }
//
//   void _showError(String message) {
//     ScaffoldMessenger.of(context).showSnackBar(
//       SnackBar(content: Text(message), backgroundColor: Colors.red),
//     );
//   }
//
//   Future<void> _submitInputNeeds() async {
//     if (!_validateForm()) return;
//
//     setState(() {
//       _isLoading = true;
//       _error = null;
//     });
//
//     final formattedItems = _items.map((item) {
//       final name = (item['nameController'] as TextEditingController).text;
//       final qty = (item['quantityController'] as TextEditingController).text;
//       final price = (item['priceController'] as TextEditingController).text;
//
//       return {
//         'productCategory': item['productCategory'],
//         'productName': name,
//         'quantity': double.tryParse(qty) ?? 0.0,
//         'unit': item['unit'],
//         'estimatedPriceEtb': double.tryParse(price) ?? 0.0,
//         'sequenceOrder': item['sequenceOrder'],
//       };
//     }).toList();
//
//     final result = await _farmService.submitInputNeeds(
//       farmId: widget.farmId,
//       items: formattedItems,
//     );
//
//     if (mounted) {
//       setState(() => _isLoading = false);
//       if (result['success'] == true) {
//         ScaffoldMessenger.of(context).showSnackBar(
//           const SnackBar(
//             content: Text('Input needs submitted successfully!'),
//             backgroundColor: Colors.green,
//           ),
//         );
//         Navigator.pop(context, true);
//       } else {
//         setState(() => _error = result['message']);
//       }
//     }
//   }
//
//   double _calculateTotal() {
//     double total = 0;
//     for (final item in _items) {
//       final priceStr = (item['priceController'] as TextEditingController?)?.text ?? '';
//       total += double.tryParse(priceStr) ?? 0.0;
//     }
//     return total;
//   }
//
//   @override
//   void dispose() {
//     // Clean up all controllers instantiated in state
//     for (final item in _items) {
//       item['nameController']?.dispose();
//       item['quantityController']?.dispose();
//       item['priceController']?.dispose();
//     }
//     super.dispose();
//   }
//
//   @override
//   Widget build(BuildContext context) {
//     return Scaffold(
//       appBar: AppBar(
//         title: const Text('Submit Input Needs'),
//         backgroundColor: Colors.green,
//         foregroundColor: Colors.white,
//       ),
//       body: SingleChildScrollView(
//         padding: const EdgeInsets.all(16),
//         child: Column(
//           crossAxisAlignment: CrossAxisAlignment.start,
//           children: [
//             Container(
//               padding: const EdgeInsets.all(12),
//               decoration: BoxDecoration(
//                 color: Colors.green.shade50,
//                 borderRadius: BorderRadius.circular(8),
//                 border: Border.all(color: Colors.green.shade200),
//               ),
//               child: const Row(
//                 children: [
//                   Icon(Icons.info_outline, color: Colors.green, size: 20),
//                   SizedBox(width: 8),
//                   Expanded(
//                     child: Text(
//                       'List all agricultural inputs you need for this season. '
//                           'Investors will fund these and vouchers will be generated.',
//                       style: TextStyle(color: Colors.green, fontSize: 13),
//                     ),
//                   ),
//                 ],
//               ),
//             ),
//             const SizedBox(height: 20),
//
//             Row(
//               mainAxisAlignment: MainAxisAlignment.spaceBetween,
//               children: [
//                 const Text(
//                   'Input Items',
//                   style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
//                 ),
//                 ElevatedButton.icon(
//                   onPressed: _addItem,
//                   icon: const Icon(Icons.add, size: 18),
//                   label: const Text('Add Item'),
//                   style: ElevatedButton.styleFrom(
//                     backgroundColor: Colors.green,
//                     foregroundColor: Colors.white,
//                     padding: const EdgeInsets.symmetric(
//                         horizontal: 12, vertical: 8),
//                   ),
//                 ),
//               ],
//             ),
//             const SizedBox(height: 12),
//
//             if (_items.isEmpty)
//               Container(
//                 width: double.infinity,
//                 padding: const EdgeInsets.all(24),
//                 decoration: BoxDecoration(
//                   border: Border.all(color: Colors.grey.shade300),
//                   borderRadius: BorderRadius.circular(8),
//                   color: Colors.grey.shade50,
//                 ),
//                 child: Column(
//                   children: [
//                     Icon(Icons.add_box_outlined,
//                         size: 48, color: Colors.grey.shade400),
//                     const SizedBox(height: 8),
//                     Text('No items added yet',
//                         style: TextStyle(color: Colors.grey.shade500)),
//                     const SizedBox(height: 4),
//                     Text(
//                       'Tap "Add Item" to add seeds, fertilizers, etc.',
//                       style: TextStyle(
//                           color: Colors.grey.shade400, fontSize: 12),
//                     ),
//                   ],
//                 ),
//               ),
//
//             ...List.generate(_items.length,
//                     (index) => _buildItemCard(index, _items[index])),
//
//             if (_error != null) ...[
//               const SizedBox(height: 16),
//               Container(
//                 padding: const EdgeInsets.all(12),
//                 decoration: BoxDecoration(
//                   color: Colors.red.shade50,
//                   borderRadius: BorderRadius.circular(8),
//                   border: Border.all(color: Colors.red.shade200),
//                 ),
//                 child: Row(
//                   children: [
//                     const Icon(Icons.error_outline, color: Colors.red),
//                     const SizedBox(width: 8),
//                     Expanded(
//                       child: Text(_error!,
//                           style: const TextStyle(color: Colors.red)),
//                     ),
//                   ],
//                 ),
//               ),
//             ],
//
//             const SizedBox(height: 24),
//
//             if (_items.isNotEmpty) ...[
//               Card(
//                 color: Colors.green.shade50,
//                 shape: RoundedRectangleBorder(
//                     borderRadius: BorderRadius.circular(8)),
//                 child: Padding(
//                   padding: const EdgeInsets.all(16),
//                   child: Row(
//                     mainAxisAlignment: MainAxisAlignment.spaceBetween,
//                     children: [
//                       const Text('Total Estimated Amount',
//                           style: TextStyle(fontWeight: FontWeight.bold)),
//                       Text(
//                         'ETB ${_calculateTotal().toStringAsFixed(2)}',
//                         style: const TextStyle(
//                           fontWeight: FontWeight.bold,
//                           fontSize: 18,
//                           color: Colors.green,
//                         ),
//                       ),
//                     ],
//                   ),
//                 ),
//               ),
//               const SizedBox(height: 16),
//             ],
//
//             SizedBox(
//               width: double.infinity,
//               child: ElevatedButton(
//                 onPressed: _isLoading ? null : _submitInputNeeds,
//                 style: ElevatedButton.styleFrom(
//                   backgroundColor: Colors.green,
//                   foregroundColor: Colors.white,
//                   padding: const EdgeInsets.symmetric(vertical: 16),
//                   shape: RoundedRectangleBorder(
//                       borderRadius: BorderRadius.circular(8)),
//                 ),
//                 child: _isLoading
//                     ? const SizedBox(
//                     height: 20,
//                     width: 20,
//                     child: CircularProgressIndicator(
//                         color: Colors.white, strokeWidth: 2))
//                     : const Text('Submit Input Needs',
//                     style: TextStyle(fontSize: 16)),
//               ),
//             ),
//             const SizedBox(height: 32),
//           ],
//         ),
//       ),
//     );
//   }
//
//   Widget _buildItemCard(int index, Map<String, dynamic> item) {
//     return Card(
//       margin: const EdgeInsets.only(bottom: 12),
//       shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
//       child: Padding(
//         padding: const EdgeInsets.all(16),
//         child: Column(
//           crossAxisAlignment: CrossAxisAlignment.start,
//           children: [
//             Row(
//               mainAxisAlignment: MainAxisAlignment.spaceBetween,
//               children: [
//                 Text('Item ${index + 1}',
//                     style: const TextStyle(
//                         fontWeight: FontWeight.bold, fontSize: 16)),
//                 IconButton(
//                   icon: const Icon(Icons.delete_outline, color: Colors.red),
//                   onPressed: () => _removeItem(index),
//                 ),
//               ],
//             ),
//             const SizedBox(height: 8),
//
//             DropdownButtonFormField<String>(
//               value: item['productCategory'],
//               decoration: const InputDecoration(
//                 labelText: 'Category *',
//                 border: OutlineInputBorder(),
//                 isDense: true,
//               ),
//               items: _categories
//                   .map((c) => DropdownMenuItem(value: c, child: Text(c)))
//                   .toList(),
//               onChanged: (val) =>
//                   setState(() => item['productCategory'] = val!),
//             ),
//             const SizedBox(height: 10),
//
//             TextFormField(
//               controller: item['nameController'] as TextEditingController,
//               decoration: const InputDecoration(
//                 labelText: 'Product Name *',
//                 hintText: 'e.g. Kakaba Wheat Seed',
//                 border: OutlineInputBorder(),
//                 isDense: true,
//               ),
//             ),
//             const SizedBox(height: 10),
//
//             Row(
//               children: [
//                 Expanded(
//                   flex: 2,
//                   child: TextFormField(
//                     controller: item['quantityController'] as TextEditingController,
//                     keyboardType: const TextInputType.numberWithOptions(decimal: true),
//                     decoration: const InputDecoration(
//                       labelText: 'Quantity *',
//                       border: OutlineInputBorder(),
//                       isDense: true,
//                     ),
//                   ),
//                 ),
//                 const SizedBox(width: 8),
//                 Expanded(
//                   child: DropdownButtonFormField<String>(
//                     value: item['unit'],
//                     decoration: const InputDecoration(
//                       labelText: 'Unit',
//                       border: OutlineInputBorder(),
//                       isDense: true,
//                     ),
//                     items: _units
//                         .map((u) =>
//                         DropdownMenuItem(value: u, child: Text(u)))
//                         .toList(),
//                     onChanged: (val) => setState(() => item['unit'] = val!),
//                   ),
//                 ),
//               ],
//             ),
//             const SizedBox(height: 10),
//
//             TextFormField(
//               controller: item['priceController'] as TextEditingController,
//               keyboardType: const TextInputType.numberWithOptions(decimal: true),
//               decoration: const InputDecoration(
//                 labelText: 'Estimated Price (ETB) *',
//                 border: OutlineInputBorder(),
//                 isDense: true,
//                 prefixText: 'ETB ',
//               ),
//               onChanged: (_) {
//                 // Instantly re-calculate bottom aggregate estimation summary layout container card UI metrics frame values upon typed values
//                 setState(() {});
//               },
//             ),
//             const SizedBox(height: 8),
//
//             Text(
//               'Redemption order: ${item['sequenceOrder']}',
//               style: TextStyle(
//                 fontSize: 12,
//                 color: Colors.grey.shade600,
//                 fontStyle: FontStyle.italic,
//               ),
//             ),
//           ],
//         ),
//       ),
//     );
//   }
// }

import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../services/farm_service.dart';
import '../../services/language_service.dart';

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

  static const Map<String, Map<String, String>> _strings = {
    'am': {
      'title':        'የግብዓት ፍላጎቶች ማስገቢያ',
      'infoMsg':      'ለዚህ ወቅት የሚያስፈልጉዎትን ሁሉ ያስገቡ። ባለሀብቶች ይሸፍናሉ፣ ቫውቸሮችም ይፈጠራሉ።',
      'inputItems':   'የግብዓት ንጥሎች',
      'addItem':      'ንጥል ጨምር',
      'noItems':      'ምንም ንጥሎች አልተጨመሩም',
      'noItemsHint':  'ዘሮች፣ ማዳበሪያዎች ወዘተ ለማከል "ንጥል ጨምር" ይጫኑ',
      'category':     'ምድብ *',
      'productName':  'የምርት ስም *',
      'productHint':  'ለምሳሌ፡ ካካባ ስንዴ ዘር',
      'quantity':     'መጠን *',
      'unit':         'ክፍል',
      'price':        'የተገመተ ዋጋ (ብር) *',
      'orderLabel':   'የቅደም ተከተል ቁጥር: {n}',
      'total':        'ጠቅላላ የተገመተ መጠን',
      'submit':       'ፍላጎቶች አስገባ',
      'successMsg':   'የግብዓት ፍላጎቶች በተሳካ ሁኔታ ቀርበዋል!',
      'itemN':        'ንጥል {n}',
      'validQty':     'ንጥል {n}፡ ትክክለኛ መጠን ያስገቡ',
      'validPrice':   'ንጥል {n}፡ ትክክለኛ ዋጋ ያስገቡ',
      'validName':    'ንጥል {n}፡ የምርት ስም ያስፈልጋል',
      'minOneItem':   'ቢያንስ አንድ ንጥል ያስፈልጋል',
    },
    'om': {
      'title':        'Fedhii Meeshaalee Galchi',
      'infoMsg':      'Meeshaalee yeroo kanaa barbaadamu hunda galchi. Mamuultonni ni maallaqa; waraqaan ragaas ni uumama.',
      'inputItems':   'Meeshaalee Galchuu',
      'addItem':      'Meeshaa Dabalii',
      'noItems':      'Meeshaan hin dabalamin',
      'noItemsHint':  'Kuduraa, xaa\'oo fi kkf dabaluuf "Meeshaa Dabalii" tuqi',
      'category':     'Gosa *',
      'productName':  'Maqaa Meeshaa *',
      'productHint':  'Fkn: Fuduraa Qamadii Kakaba',
      'quantity':     'Baay\'ina *',
      'unit':         'Safartuu',
      'price':        'Gatii Tilmaamame (ETB) *',
      'orderLabel':   'Tartiiba: {n}',
      'total':        'Gatii Tilmaamame Waliigalaa',
      'submit':       'Fedhii Galchi',
      'successMsg':   'Fedhiin meeshaalee milkaa\'inaan dhiyaate!',
      'itemN':        'Meeshaa {n}',
      'validQty':     'Meeshaa {n}: Baay\'ina sirrii galchi',
      'validPrice':   'Meeshaa {n}: Gatii sirrii galchi',
      'validName':    'Meeshaa {n}: Maqaan meeshaa barbaachisaa dha',
      'minOneItem':   'Xiqqaatti meeshaa tokko barbaachisa',
    },
    'en': {
      'title':        'Submit Input Needs',
      'infoMsg':      'List all agricultural inputs you need for this season. Investors will fund these and vouchers will be generated.',
      'inputItems':   'Input Items',
      'addItem':      'Add Item',
      'noItems':      'No items added yet',
      'noItemsHint':  'Tap "Add Item" to add seeds, fertilizers, etc.',
      'category':     'Category *',
      'productName':  'Product Name *',
      'productHint':  'e.g. Kakaba Wheat Seed',
      'quantity':     'Quantity *',
      'unit':         'Unit',
      'price':        'Estimated Price (ETB) *',
      'orderLabel':   'Redemption order: {n}',
      'total':        'Total Estimated Amount',
      'submit':       'Submit Input Needs',
      'successMsg':   'Input needs submitted successfully!',
      'itemN':        'Item {n}',
      'validQty':     'Item {n}: Enter a valid quantity',
      'validPrice':   'Item {n}: Enter a valid price',
      'validName':    'Item {n}: Product name is required',
      'minOneItem':   'Please add at least one input item',
    },
  };

  String _t(String key, {String n = ''}) {
    final code = context.read<LanguageService>().languageCode;
    return (_strings[code]?[key] ?? _strings['en']![key]!).replaceAll('{n}', n);
  }

  final List<String> _categories = ['SEED','FERTILIZER','PESTICIDE','TOOL','OTHER'];
  final List<String> _units = ['kg','litre','bag','piece'];

  void _addItem() {
    setState(() {
      _items.add({
        'productCategory': 'SEED',
        'nameController': TextEditingController(),
        'quantityController': TextEditingController(),
        'unit': 'kg',
        'priceController': TextEditingController(),
        'sequenceOrder': _items.length + 1,
      });
    });
  }

  void _removeItem(int index) {
    setState(() {
      (_items[index]['nameController'] as TextEditingController).dispose();
      (_items[index]['quantityController'] as TextEditingController).dispose();
      (_items[index]['priceController'] as TextEditingController).dispose();
      _items.removeAt(index);
      for (int i = 0; i < _items.length; i++) _items[i]['sequenceOrder'] = i + 1;
    });
  }

  bool _validateForm() {
    if (_items.isEmpty) { _showError(_t('minOneItem')); return false; }
    for (int i = 0; i < _items.length; i++) {
      final item = _items[i];
      final name  = (item['nameController'] as TextEditingController).text;
      final qty   = (item['quantityController'] as TextEditingController).text;
      final price = (item['priceController'] as TextEditingController).text;
      if (name.trim().isEmpty) { _showError(_t('validName', n: '${i+1}')); return false; }
      if (qty.trim().isEmpty || double.tryParse(qty) == null) { _showError(_t('validQty', n: '${i+1}')); return false; }
      if (price.trim().isEmpty || double.tryParse(price) == null) { _showError(_t('validPrice', n: '${i+1}')); return false; }
    }
    return true;
  }

  void _showError(String message) {
    ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text(message), backgroundColor: Colors.red));
  }

  Future<void> _submitInputNeeds() async {
    if (!_validateForm()) return;
    setState(() { _isLoading = true; _error = null; });
    final formattedItems = _items.map((item) => {
      'productCategory': item['productCategory'],
      'productName': (item['nameController'] as TextEditingController).text,
      'quantity': double.tryParse((item['quantityController'] as TextEditingController).text) ?? 0.0,
      'unit': item['unit'],
      'estimatedPriceEtb': double.tryParse((item['priceController'] as TextEditingController).text) ?? 0.0,
      'sequenceOrder': item['sequenceOrder'],
    }).toList();
    final result = await _farmService.submitInputNeeds(farmId: widget.farmId, items: formattedItems);
    if (mounted) {
      setState(() => _isLoading = false);
      if (result['success'] == true) {
        ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text(_t('successMsg')), backgroundColor: Colors.green));
        Navigator.pop(context, true);
      } else { setState(() => _error = result['message']); }
    }
  }

  double _calculateTotal() {
    double total = 0;
    for (final item in _items) {
      final priceStr = (item['priceController'] as TextEditingController?)?.text ?? '';
      total += double.tryParse(priceStr) ?? 0.0;
    }
    return total;
  }

  @override
  void dispose() {
    for (final item in _items) {
      (item['nameController'] as TextEditingController).dispose();
      (item['quantityController'] as TextEditingController).dispose();
      (item['priceController'] as TextEditingController).dispose();
    }
    super.dispose();
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
          const SizedBox(height: 20),
          Row(mainAxisAlignment: MainAxisAlignment.spaceBetween, children: [
            Text(_t('inputItems'), style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
            ElevatedButton.icon(onPressed: _addItem,
                icon: const Icon(Icons.add, size: 18), label: Text(_t('addItem')),
                style: ElevatedButton.styleFrom(backgroundColor: Colors.green, foregroundColor: Colors.white,
                    padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8))),
          ]),
          const SizedBox(height: 12),
          if (_items.isEmpty)
            Container(width: double.infinity, padding: const EdgeInsets.all(24),
                decoration: BoxDecoration(border: Border.all(color: Colors.grey.shade300),
                    borderRadius: BorderRadius.circular(8), color: Colors.grey.shade50),
                child: Column(children: [
                  Icon(Icons.add_box_outlined, size: 48, color: Colors.grey.shade400),
                  const SizedBox(height: 8),
                  Text(_t('noItems'), style: TextStyle(color: Colors.grey.shade500)),
                  const SizedBox(height: 4),
                  Text(_t('noItemsHint'), style: TextStyle(color: Colors.grey.shade400, fontSize: 12)),
                ])),
          ...List.generate(_items.length, (index) => _buildItemCard(index, _items[index])),
          if (_error != null) ...[
            const SizedBox(height: 16),
            Container(padding: const EdgeInsets.all(12),
                decoration: BoxDecoration(color: Colors.red.shade50, borderRadius: BorderRadius.circular(8), border: Border.all(color: Colors.red.shade200)),
                child: Row(children: [const Icon(Icons.error_outline, color: Colors.red), const SizedBox(width: 8),
                  Expanded(child: Text(_error!, style: const TextStyle(color: Colors.red)))])),
          ],
          const SizedBox(height: 24),
          if (_items.isNotEmpty) ...[
            Card(color: Colors.green.shade50, shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
                child: Padding(padding: const EdgeInsets.all(16),
                    child: Row(mainAxisAlignment: MainAxisAlignment.spaceBetween, children: [
                      Text(_t('total'), style: const TextStyle(fontWeight: FontWeight.bold)),
                      Text('ETB ${_calculateTotal().toStringAsFixed(2)}',
                          style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 18, color: Colors.green)),
                    ]))),
            const SizedBox(height: 16),
          ],
          SizedBox(width: double.infinity, child: ElevatedButton(
            onPressed: _isLoading ? null : _submitInputNeeds,
            style: ElevatedButton.styleFrom(backgroundColor: Colors.green, foregroundColor: Colors.white,
                padding: const EdgeInsets.symmetric(vertical: 16), shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8))),
            child: _isLoading
                ? const SizedBox(height: 20, width: 20, child: CircularProgressIndicator(color: Colors.white, strokeWidth: 2))
                : Text(_t('submit'), style: const TextStyle(fontSize: 16)),
          )),
          const SizedBox(height: 32),
        ]),
      ),
    );
  }

  Widget _buildItemCard(int index, Map<String, dynamic> item) {
    return Card(
      margin: const EdgeInsets.only(bottom: 12),
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
      child: Padding(padding: const EdgeInsets.all(16), child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
        Row(mainAxisAlignment: MainAxisAlignment.spaceBetween, children: [
          Text(_t('itemN', n: '${index + 1}'), style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
          IconButton(icon: const Icon(Icons.delete_outline, color: Colors.red), onPressed: () => _removeItem(index)),
        ]),
        const SizedBox(height: 8),
        DropdownButtonFormField<String>(
          value: item['productCategory'],
          decoration: InputDecoration(labelText: _t('category'), border: const OutlineInputBorder(), isDense: true),
          items: _categories.map((c) => DropdownMenuItem(value: c, child: Text(c))).toList(),
          onChanged: (val) => setState(() => item['productCategory'] = val!),
        ),
        const SizedBox(height: 10),
        TextFormField(controller: item['nameController'] as TextEditingController,
            decoration: InputDecoration(labelText: _t('productName'), hintText: _t('productHint'),
                border: const OutlineInputBorder(), isDense: true)),
        const SizedBox(height: 10),
        Row(children: [
          Expanded(flex: 2, child: TextFormField(controller: item['quantityController'] as TextEditingController,
              keyboardType: const TextInputType.numberWithOptions(decimal: true),
              decoration: InputDecoration(labelText: _t('quantity'), border: const OutlineInputBorder(), isDense: true))),
          const SizedBox(width: 8),
          Expanded(child: DropdownButtonFormField<String>(
            value: item['unit'],
            decoration: InputDecoration(labelText: _t('unit'), border: const OutlineInputBorder(), isDense: true),
            items: _units.map((u) => DropdownMenuItem(value: u, child: Text(u))).toList(),
            onChanged: (val) => setState(() => item['unit'] = val!),
          )),
        ]),
        const SizedBox(height: 10),
        TextFormField(controller: item['priceController'] as TextEditingController,
            keyboardType: const TextInputType.numberWithOptions(decimal: true),
            decoration: InputDecoration(labelText: _t('price'), border: const OutlineInputBorder(), isDense: true, prefixText: 'ETB '),
            onChanged: (_) => setState(() {})),
        const SizedBox(height: 8),
        Text(_t('orderLabel', n: '${item['sequenceOrder']}'),
            style: TextStyle(fontSize: 12, color: Colors.grey.shade600, fontStyle: FontStyle.italic)),
      ])),
    );
  }
}
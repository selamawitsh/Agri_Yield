import 'package:flutter/material.dart';
import '../models/product_model.dart';
import '../services/merchant_service.dart';

class ProductsScreen extends StatefulWidget {
  const ProductsScreen({super.key});

  @override
  State<ProductsScreen> createState() => _ProductsScreenState();
}

class _ProductsScreenState extends State<ProductsScreen> {
  final _merchantService = MerchantService();
  List<Product> _products = [];
  bool _loading = true;

  final List<String> _categories = ['SEED', 'FERTILIZER', 'PESTICIDE', 'TOOL', 'OTHER'];
  final List<String> _units = ['kg', 'litre', 'bag', 'piece'];

  @override
  void initState() {
    super.initState();
    _load();
  }

  Future<void> _load() async {
    setState(() => _loading = true);
    final products = await _merchantService.getInventory();
    if (mounted) setState(() { _products = products; _loading = false; });
  }

  Color _categoryColor(String cat) {
    switch (cat) {
      case 'SEED': return Colors.green;
      case 'FERTILIZER': return Colors.blue;
      case 'PESTICIDE': return Colors.red;
      case 'TOOL': return Colors.orange;
      default: return Colors.grey;
    }
  }

  IconData _categoryIcon(String cat) {
    switch (cat) {
      case 'SEED': return Icons.grass;
      case 'FERTILIZER': return Icons.science;
      case 'PESTICIDE': return Icons.bug_report;
      case 'TOOL': return Icons.handyman;
      default: return Icons.category;
    }
  }

  void _showProductDialog({Product? product}) {
    final nameCtrl = TextEditingController(text: product?.productName ?? '');
    final priceCtrl = TextEditingController(
        text: product?.currentPriceEtb.toString() ?? '');
    String selectedCategory = product?.productCategory ?? _categories[0];
    String selectedUnit = product?.unit ?? _units[0];
    bool isAvailable = product?.isAvailable ?? true;

    showDialog(
      context: context,
      builder: (ctx) => StatefulBuilder(
        builder: (ctx, setDialogState) => AlertDialog(
          title: Text(product == null ? 'Add Product' : 'Edit Product'),
          content: SingleChildScrollView(
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                TextField(
                  controller: nameCtrl,
                  decoration: const InputDecoration(
                    labelText: 'Product Name',
                    border: OutlineInputBorder(),
                  ),
                ),
                const SizedBox(height: 12),
                DropdownButtonFormField<String>(
                  value: selectedCategory,
                  decoration: const InputDecoration(
                    labelText: 'Category',
                    border: OutlineInputBorder(),
                  ),
                  items: _categories
                      .map((c) => DropdownMenuItem(value: c, child: Text(c)))
                      .toList(),
                  onChanged: (v) =>
                      setDialogState(() => selectedCategory = v!),
                ),
                const SizedBox(height: 12),
                DropdownButtonFormField<String>(
                  value: selectedUnit,
                  decoration: const InputDecoration(
                    labelText: 'Unit',
                    border: OutlineInputBorder(),
                  ),
                  items: _units
                      .map((u) => DropdownMenuItem(value: u, child: Text(u)))
                      .toList(),
                  onChanged: (v) => setDialogState(() => selectedUnit = v!),
                ),
                const SizedBox(height: 12),
                TextField(
                  controller: priceCtrl,
                  decoration: const InputDecoration(
                    labelText: 'Price (ETB)',
                    border: OutlineInputBorder(),
                    prefixText: 'ETB ',
                  ),
                  keyboardType: TextInputType.number,
                ),
                if (product != null) ...[
                  const SizedBox(height: 8),
                  SwitchListTile(
                    title: const Text('Available'),
                    value: isAvailable,
                    onChanged: (v) => setDialogState(() => isAvailable = v),
                    activeColor: Colors.orange,
                  ),
                ],
              ],
            ),
          ),
          actions: [
            TextButton(
              onPressed: () => Navigator.pop(ctx),
              child: const Text('Cancel'),
            ),
            ElevatedButton(
              onPressed: () async {
                Navigator.pop(ctx);
                if (product == null) {
                  await _merchantService.createProduct(
                    productName: nameCtrl.text,
                    productCategory: selectedCategory,
                    unit: selectedUnit,
                    currentPriceEtb: double.tryParse(priceCtrl.text) ?? 0,
                  );
                } else {
                  await _merchantService.updateProduct(product.id, {
                    'productName': nameCtrl.text,
                    'currentPriceEtb': double.tryParse(priceCtrl.text) ?? 0,
                    'isAvailable': isAvailable,
                  });
                }
                _load();
              },
              style: ElevatedButton.styleFrom(backgroundColor: Colors.orange),
              child: Text(product == null ? 'Add' : 'Update',
                  style: const TextStyle(color: Colors.white)),
            ),
          ],
        ),
      ),
    );
  }

  void _confirmDelete(Product product) {
    showDialog(
      context: context,
      builder: (ctx) => AlertDialog(
        title: const Text('Delete Product'),
        content: Text('Delete "${product.productName}"?'),
        actions: [
          TextButton(onPressed: () => Navigator.pop(ctx), child: const Text('Cancel')),
          ElevatedButton(
            onPressed: () async {
              Navigator.pop(ctx);
              await _merchantService.deleteProduct(product.id);
              _load();
            },
            style: ElevatedButton.styleFrom(backgroundColor: Colors.red),
            child: const Text('Delete', style: TextStyle(color: Colors.white)),
          ),
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Product Catalogue'),
        backgroundColor: Colors.orange,
        foregroundColor: Colors.white,
        actions: [
          IconButton(icon: const Icon(Icons.refresh), onPressed: _load),
        ],
      ),
      floatingActionButton: FloatingActionButton.extended(
        onPressed: () => _showProductDialog(),
        backgroundColor: Colors.orange,
        icon: const Icon(Icons.add, color: Colors.white),
        label: const Text('Add Product', style: TextStyle(color: Colors.white)),
      ),
      body: _loading
          ? const Center(child: CircularProgressIndicator(color: Colors.orange))
          : _products.isEmpty
              ? const Center(
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      Icon(Icons.inventory_2_outlined, size: 64, color: Colors.grey),
                      SizedBox(height: 16),
                      Text('No products yet',
                          style: TextStyle(color: Colors.grey, fontSize: 18)),
                      SizedBox(height: 8),
                      Text('Tap + to add your first product',
                          style: TextStyle(color: Colors.grey)),
                    ],
                  ),
                )
              : RefreshIndicator(
                  onRefresh: _load,
                  child: ListView.builder(
                    padding: const EdgeInsets.fromLTRB(16, 16, 16, 80),
                    itemCount: _products.length,
                    itemBuilder: (ctx, i) {
                      final p = _products[i];
                      final color = _categoryColor(p.productCategory);
                      return Card(
                        margin: const EdgeInsets.only(bottom: 12),
                        child: ListTile(
                          leading: CircleAvatar(
                            backgroundColor: color.withOpacity(0.15),
                            child: Icon(_categoryIcon(p.productCategory),
                                color: color),
                          ),
                          title: Text(p.productName,
                              style: const TextStyle(fontWeight: FontWeight.bold)),
                          subtitle: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              Text('${p.productCategory} • ${p.unit}',
                                  style: const TextStyle(fontSize: 12)),
                              Row(
                                children: [
                                  Text('ETB ${p.currentPriceEtb.toStringAsFixed(2)}',
                                      style: TextStyle(
                                          color: color, fontWeight: FontWeight.bold)),
                                  const SizedBox(width: 8),
                                  Container(
                                    padding: const EdgeInsets.symmetric(
                                        horizontal: 6, vertical: 2),
                                    decoration: BoxDecoration(
                                      color: p.isAvailable
                                          ? Colors.green.withOpacity(0.1)
                                          : Colors.red.withOpacity(0.1),
                                      borderRadius: BorderRadius.circular(4),
                                    ),
                                    child: Text(
                                      p.isAvailable ? 'Available' : 'Unavailable',
                                      style: TextStyle(
                                          fontSize: 11,
                                          color: p.isAvailable
                                              ? Colors.green
                                              : Colors.red),
                                    ),
                                  ),
                                ],
                              ),
                            ],
                          ),
                          trailing: PopupMenuButton(
                            itemBuilder: (_) => [
                              const PopupMenuItem(
                                  value: 'edit', child: Text('Edit')),
                              const PopupMenuItem(
                                  value: 'delete',
                                  child: Text('Delete',
                                      style: TextStyle(color: Colors.red))),
                            ],
                            onSelected: (v) {
                              if (v == 'edit') _showProductDialog(product: p);
                              if (v == 'delete') _confirmDelete(p);
                            },
                          ),
                        ),
                      );
                    },
                  ),
                ),
    );
  }
}

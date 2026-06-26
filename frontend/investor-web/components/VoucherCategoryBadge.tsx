import { ProductCategory } from '@/lib/types';

const CONFIG: Record<ProductCategory, { emoji: string; classes: string }> = {
  SEED:       { emoji: '', classes: 'bg-emerald-50 text-emerald-800 border-emerald-200' },
  FERTILIZER: { emoji: '', classes: 'bg-blue-50 text-blue-800 border-blue-200' },
  PESTICIDE:  { emoji: '', classes: 'bg-orange-50 text-orange-800 border-orange-200' },
  TOOL:       { emoji: '', classes: 'bg-gray-100 text-gray-700 border-gray-200' },
  OTHER:      { emoji: '', classes: 'bg-gray-100 text-gray-700 border-gray-200' },
};

export default function VoucherCategoryBadge({
  category,
}: {
  category: ProductCategory | string;
}) {
  const cfg = CONFIG[category as ProductCategory] ?? { emoji: '', classes: 'bg-gray-100 text-gray-700 border-gray-200' };
  return (
    <span
      className={`inline-flex items-center gap-1 border text-xs font-semibold rounded-full px-2.5 py-1 ${cfg.classes}`}
    >
      {cfg.emoji} {category}
    </span>
  );
}

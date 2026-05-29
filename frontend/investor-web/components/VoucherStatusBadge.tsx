import { VoucherStatus } from '@/lib/types';

const CONFIG: Record<VoucherStatus, { label: string; classes: string }> = {
  ACTIVE:    { label: 'Active',    classes: 'bg-green-100 text-green-800 border-green-200' },
  REDEEMED:  { label: 'Redeemed', classes: 'bg-gray-100 text-gray-600 border-gray-200' },
  GENERATED: { label: 'Locked',   classes: 'bg-blue-100 text-blue-800 border-blue-200' },
  EXPIRED:   { label: 'Expired',  classes: 'bg-red-100 text-red-700 border-red-200' },
  CANCELLED: { label: 'Cancelled',classes: 'bg-red-100 text-red-700 border-red-200' },
  REJECTED:  { label: 'Rejected', classes: 'bg-orange-100 text-orange-700 border-orange-200' },
};

export default function VoucherStatusBadge({
  status,
  size = 'sm',
}: {
  status: VoucherStatus;
  size?: 'xs' | 'sm';
}) {
  const cfg = CONFIG[status] ?? { label: status, classes: 'bg-gray-100 text-gray-600 border-gray-200' };
  return (
    <span
      className={`inline-flex items-center border font-medium rounded-full ${cfg.classes} ${
        size === 'xs' ? 'text-xs px-2 py-0.5' : 'text-xs px-2.5 py-1'
      }`}
    >
      {cfg.label}
    </span>
  );
}

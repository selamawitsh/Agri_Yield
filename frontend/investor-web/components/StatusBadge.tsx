interface StatusBadgeProps {
  status: string;
}

const statusColors: Record<string, string> = {
  OPEN: 'bg-blue-100 text-blue-800',
  PARTIALLY_FUNDED: 'bg-yellow-100 text-yellow-800',
  FULLY_FUNDED: 'bg-green-100 text-green-800',
  FUNDING_FAILED: 'bg-red-100 text-red-800',
  ACTIVE: 'bg-green-100 text-green-800',
  COMPLETED: 'bg-gray-100 text-gray-800',
  CANCELLED: 'bg-red-100 text-red-800',
  PENDING: 'bg-yellow-100 text-yellow-800',
  ESCROW_LOCKED: 'bg-blue-100 text-blue-800',
  VERIFIED: 'bg-green-100 text-green-800',
  FAILED: 'bg-red-100 text-red-800',
};

export default function StatusBadge({ status }: StatusBadgeProps) {
  const colorClass = statusColors[status] || 'bg-gray-100 text-gray-800';
  return (
      <span className={`px-2 py-1 text-xs font-medium rounded-full ${colorClass}`}>
      {status.replace(/_/g, ' ')}
    </span>
  );
}
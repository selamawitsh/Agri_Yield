interface Props { status: string | null | undefined; }

const MAP: Record<string, string> = {
  PENDING:          'bg-yellow-100 text-yellow-700',
  ACCEPTED:         'bg-green-100 text-green-700',
  REJECTED:         'bg-red-100 text-red-700',
  CONTRACT_SIGNED:  'bg-blue-100 text-blue-700',
  COMPLETED:        'bg-emerald-100 text-emerald-700',
  DEFAULTED:        'bg-red-200 text-red-800',
  EXPIRED:          'bg-gray-100 text-gray-500',
  SCHEDULED:        'bg-blue-100 text-blue-700',
  ARRIVED:          'bg-indigo-100 text-indigo-700',
  LOADED:           'bg-purple-100 text-purple-700',
  DELIVERED:        'bg-green-100 text-green-700',
  DRIVER_DEFAULTED: 'bg-red-100 text-red-700',
};

export default function StatusBadge({ status }: Props) {
  // FIX: status can be null/undefined when a FarmOpportunity hasn't been fully
  // enriched yet (e.g. cropCycleStatus is null until yield.predicted arrives).
  // Calling .replace() on null crashed the whole page. Render a neutral
  // placeholder instead of throwing.
  if (!status) {
    return (
      <span className="text-xs font-bold px-2.5 py-0.5 rounded-full bg-gray-100 text-gray-400">
        UNKNOWN
      </span>
    );
  }

  return (
    <span className={`text-xs font-bold px-2.5 py-0.5 rounded-full ${MAP[status] || 'bg-gray-100 text-gray-600'}`}>
      {status.replace(/_/g, ' ')}
    </span>
  );
}

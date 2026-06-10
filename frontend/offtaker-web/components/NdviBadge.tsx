interface Props { ndvi: number; healthStatus?: string; }

export default function NdviBadge({ ndvi, healthStatus }: Props) {
  const color = ndvi >= 0.6 ? 'bg-green-100 text-green-700'
    : ndvi >= 0.4 ? 'bg-lime-100 text-lime-700'
    : ndvi >= 0.2 ? 'bg-yellow-100 text-yellow-700'
    : 'bg-red-100 text-red-700';

  const label = healthStatus || (
    ndvi >= 0.6 ? 'Excellent' : ndvi >= 0.4 ? 'Good'
    : ndvi >= 0.2 ? 'Moderate' : 'Poor'
  );

  return (
    <span className={`inline-flex items-center gap-1 text-xs font-bold px-2.5 py-1 rounded-full ${color}`}>
      🛰 NDVI {ndvi.toFixed(2)} — {label}
    </span>
  );
}

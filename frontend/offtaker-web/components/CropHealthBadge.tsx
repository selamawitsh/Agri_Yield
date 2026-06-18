'use client';

interface Props {
  ndvi: number;
  compact?: boolean;
}

interface HealthTier {
  label: string;
  emoji: string;
  color: string;
  bgColor: string;
  borderColor: string;
  guidance: string;
}

function getTier(ndvi: number): HealthTier {
  if (ndvi >= 0.6) {
    return {
      label: 'Thriving',
      emoji: '🌿',
      color: 'text-green-700',
      bgColor: 'bg-green-50',
      borderColor: 'border-green-200',
      guidance: 'Strong, dense vegetation. Yield prediction is likely reliable — good farm to bid on with confidence.',
    };
  }
  if (ndvi >= 0.4) {
    return {
      label: 'Healthy',
      emoji: '🌱',
      color: 'text-lime-700',
      bgColor: 'bg-lime-50',
      borderColor: 'border-lime-200',
      guidance: 'Crop is growing well. No major concerns — proceed with a standard market-rate bid.',
    };
  }
  if (ndvi >= 0.2) {
    return {
      label: 'Developing',
      emoji: '🌾',
      color: 'text-yellow-700',
      bgColor: 'bg-yellow-50',
      borderColor: 'border-yellow-200',
      guidance: 'Moderate vegetation — this can be normal for early growth stages or crops nearing natural harvest dryness. Check the harvest window before assuming a problem.',
    };
  }
  if (ndvi >= 0.0) {
    return {
      label: 'Stressed',
      emoji: '⚠️',
      color: 'text-orange-700',
      bgColor: 'bg-orange-50',
      borderColor: 'border-orange-200',
      guidance: 'Vegetation stress detected. Consider bidding lower than market rate, or message the farmer to confirm field conditions before committing.',
    };
  }
  return {
    label: 'Critical',
    emoji: '🔴',
    color: 'text-red-700',
    bgColor: 'bg-red-50',
    borderColor: 'border-red-200',
    guidance: 'Very low or negative reading — often means bare soil, water, or a damaged crop. Verify with the farmer directly before placing any bid.',
  };
}

export default function CropHealthBadge({ ndvi, compact = false }: Props) {
  const tier = getTier(ndvi);

  if (compact) {
    return (
      <span className={`inline-flex items-center gap-1.5 text-xs font-bold px-2.5 py-1 rounded-full ${tier.bgColor} ${tier.color} border ${tier.borderColor}`}>
        {tier.emoji} {tier.label}
      </span>
    );
  }

  return (
    <div className={`rounded-2xl p-4 border ${tier.bgColor} ${tier.borderColor}`}>
      <div className="flex items-center justify-between mb-2">
        <div className="flex items-center gap-2">
          <span className="text-2xl">{tier.emoji}</span>
          <div>
            <p className={`font-bold text-lg ${tier.color}`}>{tier.label} Crop</p>
            <p className="text-xs text-gray-400">Satellite reading: {ndvi.toFixed(3)}</p>
          </div>
        </div>
      </div>
      <p className={`text-sm leading-relaxed ${tier.color}`}>{tier.guidance}</p>
    </div>
  );
}

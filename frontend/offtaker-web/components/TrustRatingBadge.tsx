'use client';

interface Props {
  agriScore: number;
  seasonsCompleted?: number;
}

interface TrustTier {
  label: string;
  stars: number;
  emoji: string;
  color: string;
  bgColor: string;
  borderColor: string;
  explanation: string;
}

function getTier(score: number, seasons?: number): TrustTier {
  // New farmers (default score 50, 0 seasons) get an honest "not yet rated" tier
  // rather than being lumped in with poor performers.
  if ((seasons ?? 0) === 0 && score <= 50) {
    return {
      label: 'New Farmer',
      stars: 0,
      emoji: '🌱',
      color: 'text-gray-600',
      bgColor: 'bg-gray-50',
      borderColor: 'border-gray-200',
      explanation: 'No completed seasons yet on the platform. There\'s no track record to judge — treat this as a higher-trust-required deal, perhaps with smaller quantity to start.',
    };
  }
  if (score >= 750) {
    return {
      label: 'Platinum',
      stars: 5,
      emoji: '🏆',
      color: 'text-emerald-700',
      bgColor: 'bg-emerald-50',
      borderColor: 'border-emerald-200',
      explanation: 'Outstanding track record across multiple seasons — reliable voucher usage, accurate yields, and consistent contract fulfillment. Low-risk trading partner.',
    };
  }
  if (score >= 600) {
    return {
      label: 'Gold',
      stars: 4,
      emoji: '🥇',
      color: 'text-amber-700',
      bgColor: 'bg-amber-50',
      borderColor: 'border-amber-200',
      explanation: 'Strong history of completing seasons and fulfilling contracts. A dependable trading partner with minor inconsistencies at most.',
    };
  }
  if (score >= 400) {
    return {
      label: 'Silver',
      stars: 3,
      emoji: '🥈',
      color: 'text-slate-700',
      bgColor: 'bg-slate-50',
      borderColor: 'border-slate-200',
      explanation: 'Decent track record with some seasons completed. Generally trustworthy, though it may be worth confirming delivery logistics directly.',
    };
  }
  if (score >= 200) {
    return {
      label: 'Bronze',
      stars: 2,
      emoji: '🥉',
      color: 'text-orange-700',
      bgColor: 'bg-orange-50',
      borderColor: 'border-orange-200',
      explanation: 'Limited or mixed track record. Some past issues with voucher use, yield accuracy, or contract fulfillment — proceed with extra diligence.',
    };
  }
  return {
    label: 'Caution',
    stars: 1,
    emoji: '🔻',
    color: 'text-red-700',
    bgColor: 'bg-red-50',
    borderColor: 'border-red-200',
    explanation: 'Low score from past seasons — repeated issues with fulfillment, repayment, or input use. Strongly consider direct verification before committing to a large bid.',
  };
}

export default function TrustRatingBadge({ agriScore, seasonsCompleted }: Props) {
  const tier = getTier(agriScore, seasonsCompleted);

  return (
    <div className={`rounded-2xl p-4 border ${tier.bgColor} ${tier.borderColor}`}>
      <div className="flex items-center justify-between mb-2">
        <div className="flex items-center gap-2">
          <span className="text-2xl">{tier.emoji}</span>
          <div>
            <p className={`font-bold text-lg ${tier.color}`}>{tier.label} Trust Rating</p>
            <p className="text-xs text-gray-400">
              Agri-Score: {agriScore} / 900
              {seasonsCompleted != null && ` · ${seasonsCompleted} season${seasonsCompleted !== 1 ? 's' : ''} completed`}
            </p>
          </div>
        </div>
        {tier.stars > 0 && (
          <div className="flex gap-0.5">
            {Array.from({ length: 5 }, (_, i) => (
              <span key={i} className={i < tier.stars ? tier.color : 'text-gray-200'}>★</span>
            ))}
          </div>
        )}
      </div>
      <p className={`text-sm leading-relaxed ${tier.color}`}>{tier.explanation}</p>
    </div>
  );
}

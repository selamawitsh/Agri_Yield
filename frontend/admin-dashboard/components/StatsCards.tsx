'use client';

interface StatsCardsProps {
  stats: {
    totalUsers: number;
    totalFarmers: number;
    totalInvestors: number;
    totalMerchants: number;
    totalOffTakers: number;
    pendingKyc: number;
    verifiedKyc: number;
    rejectedKyc: number;
    activeUsers: number;
    suspendedUsers: number;
  };
}

export default function StatsCards({ stats }: StatsCardsProps) {
  const metricCards = [
    { title: 'Global Registry', value: stats.totalUsers, label: 'Profiles Connected', styles: 'bg-gradient-to-br from-emerald-800 to-emerald-950 text-white shadow-md' },
    { title: 'Registered Farmers', value: stats.totalFarmers, label: 'Cultivation Nodes', styles: 'bg-emerald-700 text-white shadow-sm' },
    { title: 'Capital Stakeholders', value: stats.totalInvestors, label: 'Strategic Funding', styles: 'bg-stone-700 text-white shadow-sm' },
    { title: 'Merchant Pipeline', value: stats.totalMerchants, label: 'Logistics Clusters', styles: 'bg-amber-800 text-white shadow-sm' },
    { title: 'Off-Take Entities', value: stats.totalOffTakers, label: 'Contract Buyers', styles: 'bg-stone-500 text-white shadow-sm' }
  ];

  const validationTracks = [
    { title: 'Awaiting Verification', value: stats.pendingKyc, pillClass: 'bg-amber-100 text-amber-900 border-amber-200' },
    { title: 'Verified Safe Identities', value: stats.verifiedKyc, pillClass: 'bg-emerald-100 text-emerald-900 border-emerald-200' },
    { title: 'Active Yield Flows', value: stats.activeUsers, pillClass: 'bg-teal-100 text-teal-900 border-teal-200' },
    { title: 'Suspended Nodes', value: stats.suspendedUsers, pillClass: 'bg-rose-100 text-rose-900 border-rose-200' }
  ];

  return (
      <div className="space-y-6">
        {/* 5-Column High Aspect Ratio Hero Metric Row */}
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-5 gap-4">
          {metricCards.map((card, i) => (
              <div key={i} className={`rounded-2xl p-5 flex flex-col justify-between min-h-[140px] transition-transform hover:-translate-y-0.5 ${card.styles}`}>
                <div className="flex items-center justify-between">
                  <span className="text-[10px] font-bold tracking-widest uppercase opacity-80">{card.title}</span>
                  <svg className="w-4 h-4 opacity-60" fill="none" stroke="currentColor" strokeWidth="2.5" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" d="M12 3v17M3 12h18" />
                  </svg>
                </div>
                <div className="mt-4">
                  <p className="text-4xl font-black tracking-tight font-mono">{card.value}</p>
                  <p className="text-[11px] mt-1 font-medium opacity-75">{card.label}</p>
                </div>
              </div>
          ))}
        </div>

        {/* Pill Track Layout rows matching the organic green labels */}
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-3">
          {validationTracks.map((track, i) => (
              <div key={i} className={`border rounded-xl px-4 py-3 flex items-center justify-between font-bold text-xs shadow-xs ${track.pillClass}`}>
                <span>{track.title}</span>
                <span className="text-xs bg-white/80 backdrop-blur-xs text-slate-900 px-2 py-0.5 rounded-md font-mono shadow-2xs">
              {track.value}
            </span>
              </div>
          ))}
        </div>
      </div>
  );
}
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
  const cards = [
    { title: 'Total Users', value: stats.totalUsers, color: 'bg-blue-500', icon: '👥' },
    { title: 'Farmers', value: stats.totalFarmers, color: 'bg-green-500', icon: '🌾' },
    { title: 'Investors', value: stats.totalInvestors, color: 'bg-purple-500', icon: '💰' },
    { title: 'Merchants', value: stats.totalMerchants, color: 'bg-orange-500', icon: '🏪' },
    { title: 'Off-Takers', value: stats.totalOffTakers, color: 'bg-teal-500', icon: '🚚' },
    { title: 'Pending KYC', value: stats.pendingKyc, color: 'bg-yellow-500', icon: '⏳' },
    { title: 'Verified KYC', value: stats.verifiedKyc, color: 'bg-green-500', icon: '✅' },
    { title: 'Rejected KYC', value: stats.rejectedKyc, color: 'bg-red-500', icon: '❌' },
    { title: 'Active Users', value: stats.activeUsers, color: 'bg-emerald-500', icon: '🟢' },
    { title: 'Suspended', value: stats.suspendedUsers, color: 'bg-red-500', icon: '🔴' },
  ];

  return (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-5 gap-4 mb-8">
      {cards.map((card, index) => (
        <div key={index} className="bg-white rounded-lg shadow-md p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-gray-500 text-sm">{card.title}</p>
              <p className="text-2xl font-bold mt-1">{card.value}</p>
            </div>
            <div className={`${card.color} w-12 h-12 rounded-full flex items-center justify-center text-white text-xl`}>
              {card.icon}
            </div>
          </div>
        </div>
      ))}
    </div>
  );
}

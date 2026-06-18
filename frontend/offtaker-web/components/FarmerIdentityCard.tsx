'use client';

import type { FarmerIdentity } from '@/lib/types';

interface Props {
  farmer: FarmerIdentity | null;
}

export default function FarmerIdentityCard({ farmer }: Props) {
  if (!farmer) {
    return (
      <div className="bg-gray-50 border border-gray-100 rounded-2xl p-5 text-center text-gray-400 text-sm">
        <p className="text-2xl mb-2">👤</p>
        <p>Farmer identity unavailable — try refreshing.</p>
      </div>
    );
  }

  const kycColor = farmer.kycStatus === 'VERIFIED'
    ? 'bg-green-100 text-green-700'
    : farmer.kycStatus === 'PENDING'
    ? 'bg-yellow-100 text-yellow-700'
    : 'bg-red-100 text-red-700';

  const agriScoreColor = farmer.agriScore >= 700 ? 'text-green-600'
    : farmer.agriScore >= 500 ? 'text-lime-600'
    : farmer.agriScore >= 300 ? 'text-yellow-600' : 'text-red-600';

  return (
    <div className="bg-white border border-gray-100 rounded-2xl p-5">
      <div className="flex items-center gap-3 mb-4">
        <div className="w-12 h-12 bg-teal-100 rounded-full flex items-center justify-center text-xl">
          👨‍🌾
        </div>
        <div>
          <p className="font-bold text-gray-800">Farmer</p>
          <p className="text-xs text-gray-400 font-mono">{farmer.farmerId.slice(0, 13)}…</p>
        </div>
        <span className={`ml-auto text-xs font-bold px-2.5 py-1 rounded-full ${kycColor}`}>
          {farmer.kycStatus === 'VERIFIED' ? '✓ KYC Verified' : farmer.kycStatus}
        </span>
      </div>

      <div className="grid grid-cols-2 gap-3 text-sm">
        <div className="bg-gray-50 rounded-xl p-3">
          <p className="text-gray-400 text-xs uppercase tracking-wide font-medium">Phone</p>
          <p className="font-bold text-gray-800 mt-0.5">{farmer.phone}</p>
        </div>
        <div className="bg-gray-50 rounded-xl p-3">
          <p className="text-gray-400 text-xs uppercase tracking-wide font-medium">Fayda National ID</p>
          <p className="font-bold text-gray-800 mt-0.5 font-mono text-xs">{farmer.faydaId}</p>
        </div>
        <div className="bg-gray-50 rounded-xl p-3">
          <p className="text-gray-400 text-xs uppercase tracking-wide font-medium">Agri-Score</p>
          <p className={`font-bold mt-0.5 ${agriScoreColor}`}>{farmer.agriScore} / 900</p>
        </div>
        <div className="bg-gray-50 rounded-xl p-3">
          <p className="text-gray-400 text-xs uppercase tracking-wide font-medium">Seasons Completed</p>
          <p className="font-bold text-gray-800 mt-0.5">{farmer.totalSeasonsCompleted}</p>
        </div>
      </div>

      <p className="text-xs text-gray-400 mt-3 leading-relaxed">
        Agri-Score reflects the farmer's track record: voucher discipline, yield accuracy,
        contract fulfillment, and repayment history across past seasons. Higher scores indicate
        a more reliable trading partner.
      </p>
    </div>
  );
}

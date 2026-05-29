'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import toast from 'react-hot-toast';
import api from '@/lib/api';
import Navbar from '@/components/Navbar';
import VoucherStatusBadge from '@/components/VoucherStatusBadge';
import VoucherCategoryBadge from '@/components/VoucherCategoryBadge';
import { Voucher, VoucherStatus } from '@/lib/types';

interface PortfolioVoucherGroup {
  investmentId: string;
  farmId: string;
  farmName: string;
  cropType: string;
  region: string;
  seasonName: string;
  vouchers: Voucher[];
}

export default function PortfolioVouchersPage() {
  const router = useRouter();
  const [groups, setGroups] = useState<PortfolioVoucherGroup[]>([]);
  const [loading, setLoading] = useState(true);
  const [filterStatus, setFilterStatus] = useState<string>('ALL');

  // Aggregate stats
  const allVouchers = groups.flatMap((g) => g.vouchers);
  const totalValue = allVouchers.reduce((s, v) => s + v.amountEtb, 0);
  const redeemedValue = allVouchers
    .filter((v) => v.status === 'REDEEMED')
    .reduce((s, v) => s + v.amountEtb, 0);
  const activeCount = allVouchers.filter((v) => v.status === 'ACTIVE').length;
  const redeemedCount = allVouchers.filter((v) => v.status === 'REDEEMED').length;

  useEffect(() => {
    const token = localStorage.getItem('access_token');
    if (!token) { router.push('/login'); return; }
    fetchPortfolioVouchers();
  }, [router]);

  const fetchPortfolioVouchers = async () => {
    try {
      const res = await api.get('/portfolio/vouchers');
      if (res.data.success) setGroups(res.data.data ?? []);
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Failed to load vouchers');
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-green-600 mx-auto mb-4" />
          <p className="text-gray-500 text-sm">Loading voucher overview...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />
      <div className="container mx-auto px-6 py-8 max-w-5xl">

        <div className="mb-6">
          <h1 className="text-2xl font-bold text-gray-900">Voucher Overview</h1>
          <p className="text-gray-500 mt-1 text-sm">
            Track how your invested funds are being used across all farms
          </p>
        </div>

        {/* Summary metrics */}
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-8">
          {[
            {
              label: 'Total Deployed',
              value: `${totalValue.toLocaleString()} ETB`,
              sub: 'across all farms',
              color: 'text-gray-800',
              bg: 'bg-white',
            },
            {
              label: 'Redeemed Value',
              value: `${redeemedValue.toLocaleString()} ETB`,
              sub: `${totalValue > 0 ? Math.round((redeemedValue / totalValue) * 100) : 0}% of total`,
              color: 'text-green-600',
              bg: 'bg-white',
            },
            {
              label: 'Active Vouchers',
              value: activeCount,
              sub: 'awaiting scan',
              color: 'text-amber-600',
              bg: 'bg-white',
            },
            {
              label: 'Redeemed',
              value: redeemedCount,
              sub: 'inputs delivered',
              color: 'text-green-600',
              bg: 'bg-white',
            },
          ].map((m) => (
            <div key={m.label} className={`${m.bg} rounded-xl shadow p-5`}>
              <p className="text-xs text-gray-500 mb-1">{m.label}</p>
              <p className={`text-2xl font-bold ${m.color}`}>{m.value}</p>
              <p className="text-xs text-gray-400 mt-0.5">{m.sub}</p>
            </div>
          ))}
        </div>

        {/* Status filter */}
        <div className="flex gap-2 mb-5 flex-wrap">
          {['ALL', 'ACTIVE', 'REDEEMED', 'GENERATED', 'EXPIRED'].map((s) => (
            <button
              key={s}
              onClick={() => setFilterStatus(s)}
              className={`px-4 py-1.5 rounded-full text-sm font-medium transition ${
                filterStatus === s
                  ? 'bg-green-600 text-white'
                  : 'bg-white text-gray-600 border border-gray-200 hover:border-green-400'
              }`}
            >
              {s === 'ALL' ? 'All' : s === 'GENERATED' ? 'Locked' : s.charAt(0) + s.slice(1).toLowerCase()}
            </button>
          ))}
        </div>

        {/* Farm groups */}
        {groups.length === 0 ? (
          <div className="bg-white rounded-xl shadow p-12 text-center">
            <p className="text-5xl mb-4">🎟️</p>
            <p className="text-gray-500 text-lg">No vouchers yet</p>
            <p className="text-gray-400 text-sm mt-1">
              Vouchers are generated once your invested farms are fully funded
            </p>
            <Link
              href="/listings"
              className="mt-5 inline-block bg-green-600 text-white px-6 py-2.5 rounded-lg text-sm font-semibold hover:bg-green-700"
            >
              Browse Farms
            </Link>
          </div>
        ) : (
          <div className="space-y-6">
            {groups.map((group) => {
              const filtered =
                filterStatus === 'ALL'
                  ? group.vouchers
                  : group.vouchers.filter((v) => v.status === filterStatus);

              if (filtered.length === 0) return null;

              const redeemed = group.vouchers.filter((v) => v.status === 'REDEEMED').length;
              const pct = group.vouchers.length > 0
                ? Math.round((redeemed / group.vouchers.length) * 100)
                : 0;

              return (
                <div key={group.investmentId} className="bg-white rounded-xl shadow overflow-hidden">
                  {/* Farm header */}
                  <div className="px-5 py-4 border-b border-gray-100 flex items-center justify-between">
                    <div>
                      <h3 className="font-semibold text-gray-800">
                        {group.cropType} — {group.region}
                      </h3>
                      <p className="text-xs text-gray-500 mt-0.5">{group.seasonName}</p>
                    </div>
                    <div className="flex items-center gap-4">
                      <div className="text-right">
                        <p className="text-xs text-gray-400">Redeemed</p>
                        <p className="font-bold text-green-600">{pct}%</p>
                      </div>
                      <Link
                        href={`/portfolio/${group.investmentId}/vouchers`}
                        className="text-xs text-green-600 hover:underline font-medium"
                      >
                        Full view →
                      </Link>
                    </div>
                  </div>

                  {/* Progress bar */}
                  <div className="px-5 pt-3">
                    <div className="w-full bg-gray-100 rounded-full h-1.5">
                      <div
                        className="bg-green-500 h-1.5 rounded-full transition-all"
                        style={{ width: `${pct}%` }}
                      />
                    </div>
                  </div>

                  {/* Voucher rows */}
                  <div className="divide-y divide-gray-50">
                    {[...filtered]
                      .sort((a, b) => a.sequenceOrder - b.sequenceOrder)
                      .map((v) => (
                        <div key={v.id} className="px-5 py-3 flex items-center gap-3">
                          <span className="text-lg">
                            {{ SEED: '🌾', FERTILIZER: '🪣', PESTICIDE: '🛡️', TOOL: '🔧', OTHER: '📦' }[v.productCategory] ?? '📦'}
                          </span>
                          <div className="flex-1 min-w-0">
                            <p className="text-sm font-medium text-gray-800 truncate">
                              {v.productDescription}
                            </p>
                            <div className="flex items-center gap-2 mt-0.5">
                              <span className="text-xs text-gray-400">#{v.sequenceOrder}</span>
                              <VoucherCategoryBadge category={v.productCategory} />
                            </div>
                          </div>
                          <div className="flex items-center gap-3 flex-shrink-0">
                            <span className="text-sm font-bold font-mono text-gray-800">
                              {v.amountEtb.toLocaleString()} ETB
                            </span>
                            <VoucherStatusBadge status={v.status} size="xs" />
                          </div>
                        </div>
                      ))}
                  </div>
                </div>
              );
            })}
          </div>
        )}
      </div>
    </div>
  );
}

'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import toast from 'react-hot-toast';
import api from '@/lib/api';
import Navbar from '@/components/Navbar';
import StatusBadge from '@/components/StatusBadge';
import { Investment, PortfolioStats } from '@/lib/types';

type FilterType = 'ALL' | 'ACTIVE' | 'COMPLETED' | 'CANCELLED' | 'REFUNDED';
type SortType = 'date_desc' | 'date_asc' | 'amount_desc' | 'apr_desc';

export default function PortfolioPage() {
  const router = useRouter();
  const [investments, setInvestments] = useState<Investment[]>([]);
  const [stats, setStats] = useState<PortfolioStats | null>(null);
  const [loading, setLoading] = useState(true);
  const [cancellingId, setCancellingId] = useState<string | null>(null);
  const [filter, setFilter] = useState<FilterType>('ALL');
  const [sort, setSort] = useState<SortType>('date_desc');

  useEffect(() => {
    const token = localStorage.getItem('access_token');
    if (!token) { router.push('/login'); return; }
    fetchPortfolio();
  }, []);

  const fetchPortfolio = async () => {
    try {
      const res = await api.get('/portfolio');
      if (res.data.success) {
        const data: Investment[] = res.data.data || [];
        setInvestments(data);
        const totalInvested  = data.reduce((s, i) => s + i.amountEtb, 0);
        const totalReturned  = data.filter(i => i.status === 'COMPLETED')
                                   .reduce((s, i) => s + (i.payoutAmountEtb ?? 0), 0);
        const active         = data.filter(i => ['ACTIVE','ESCROW_LOCKED','PENDING'].includes(i.status)).length;
        const completed      = data.filter(i => i.status === 'COMPLETED').length;
        const cancelled      = data.filter(i => i.status === 'CANCELLED').length;
        const avgApr         = data.length > 0
          ? data.reduce((s, i) => s + i.expectedReturnPct, 0) / data.length : 0;
        setStats({ totalInvested, totalReturned, activeInvestments: active,
          completedInvestments: completed, cancelledInvestments: cancelled, averageApr: avgApr });
      }
    } catch (err: any) {
      toast.error(err.response?.data?.message || 'Failed to load portfolio');
    } finally { setLoading(false); }
  };

  const handleCancel = async (investmentId: string) => {
    if (!window.confirm('Cancel this investment? Funds will be returned.')) return;
    setCancellingId(investmentId);
    try {
      const res = await api.post(`/investments/${investmentId}/cancel`, { reason: 'Cancelled by investor' });
      if (res.data.success) { toast.success('Investment cancelled. Funds will be returned.'); fetchPortfolio(); }
    } catch (err: any) {
      toast.error(err.response?.data?.message || 'Cannot cancel this investment');
    } finally { setCancellingId(null); }
  };

  const filtered = investments.filter(i => {
    if (filter === 'ALL')       return true;
    if (filter === 'ACTIVE')    return ['ACTIVE','ESCROW_LOCKED','PENDING'].includes(i.status);
    if (filter === 'REFUNDED')  return i.status === 'REFUNDED';
    return i.status === filter;
  });

  const sorted = [...filtered].sort((a, b) => {
    if (sort === 'date_desc')   return new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime();
    if (sort === 'date_asc')    return new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime();
    if (sort === 'amount_desc') return b.amountEtb - a.amountEtb;
    if (sort === 'apr_desc')    return b.expectedReturnPct - a.expectedReturnPct;
    return 0;
  });

  if (loading) return (
    <div className="min-h-screen flex items-center justify-center">
      <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-green-600" />
    </div>
  );

  const TABS: { key: FilterType; label: string }[] = [
    { key: 'ALL',       label: 'All' },
    { key: 'ACTIVE',    label: 'Active' },
    { key: 'COMPLETED', label: 'Completed' },
    { key: 'REFUNDED',  label: 'Refunded' },
    { key: 'CANCELLED', label: 'Cancelled' },
  ];

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />
      <div className="container mx-auto px-6 py-8 max-w-5xl">

        <div className="mb-6">
          <h1 className="text-2xl font-bold text-gray-900">My Portfolio</h1>
          <p className="text-gray-500 mt-1">Track all your farm investments</p>
        </div>

        {/* Stats — SRS §6.3.2 */}
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-6">
          {[
            { label: 'Total Invested',  value: `${(stats?.totalInvested  || 0).toLocaleString()} ETB`, color: 'text-green-600',  bg: 'bg-green-50'  },
            { label: 'Total Returned',  value: `${(stats?.totalReturned  || 0).toLocaleString()} ETB`, color: 'text-blue-600',   bg: 'bg-blue-50'   },
            { label: 'Active',          value: stats?.activeInvestments    || 0,                        color: 'text-orange-600', bg: 'bg-orange-50' },
            { label: 'Avg APR',         value: `${(stats?.averageApr     || 0).toFixed(1)}%`,          color: 'text-purple-600', bg: 'bg-purple-50' },
          ].map(s => (
            <div key={s.label} className="bg-white rounded-2xl shadow-sm border border-gray-100 p-4">
              <p className="text-gray-400 text-xs font-medium uppercase tracking-wide">{s.label}</p>
              <p className={`text-2xl font-bold ${s.color} mt-1`}>{s.value}</p>
            </div>
          ))}
        </div>

        {/* Filter tabs + Sort */}
        <div className="flex flex-wrap gap-3 justify-between items-center mb-5">
          <div className="flex gap-1 bg-white rounded-xl shadow-sm border border-gray-100 p-1 flex-wrap">
            {TABS.map(tab => (
              <button key={tab.key} onClick={() => setFilter(tab.key)}
                className={`px-4 py-1.5 rounded-lg text-sm font-semibold transition ${
                  filter === tab.key ? 'bg-green-600 text-white' : 'text-gray-500 hover:bg-gray-100'
                }`}>
                {tab.label}
                <span className="ml-1.5 text-xs opacity-60">
                  ({tab.key === 'ALL' ? investments.length
                    : tab.key === 'ACTIVE' ? investments.filter(i => ['ACTIVE','ESCROW_LOCKED','PENDING'].includes(i.status)).length
                    : investments.filter(i => i.status === tab.key).length})
                </span>
              </button>
            ))}
          </div>
          <select value={sort} onChange={e => setSort(e.target.value as SortType)}
            className="bg-white border border-gray-200 text-sm rounded-xl px-3 py-2 text-gray-600 focus:outline-none focus:ring-2 focus:ring-green-500">
            <option value="date_desc">Newest first</option>
            <option value="date_asc">Oldest first</option>
            <option value="amount_desc">Highest amount</option>
            <option value="apr_desc">Highest APR</option>
          </select>
        </div>

        {sorted.length === 0 ? (
          <div className="bg-white rounded-2xl shadow-sm border border-gray-100 p-12 text-center">
            <p className="text-4xl mb-4">🌱</p>
            <p className="text-gray-500 text-lg font-medium">No investments found</p>
            <Link href="/listings" className="mt-4 inline-block bg-green-600 text-white px-5 py-2.5 rounded-xl font-semibold hover:bg-green-700 transition">
              Browse Farms
            </Link>
          </div>
        ) : (
          <div className="space-y-4">
            {sorted.map(inv => (
              <div key={inv.id} className="bg-white rounded-2xl shadow-sm border border-gray-100 p-5 hover:shadow-md transition">
                <div className="flex justify-between items-start">
                  <div className="flex-1">
                    <div className="flex items-center gap-2 flex-wrap mb-1">
                      <h3 className="font-bold text-gray-800">{inv.cropType} — {inv.region}</h3>
                      <StatusBadge status={inv.status} />
                    </div>
                    <p className="text-gray-400 text-sm">{inv.seasonName}</p>
                    {inv.notes && (
                      <p className="text-gray-400 text-xs mt-1 italic">"{inv.notes}"</p>
                    )}
                    {inv.cancelledReason && (
                      <p className="text-red-500 text-xs mt-1">Reason: {inv.cancelledReason}</p>
                    )}
                  </div>
                  <div className="text-right ml-4 shrink-0">
                    <p className="text-xl font-bold text-green-600">{inv.amountEtb.toLocaleString()} ETB</p>
                    <p className="text-sm text-gray-500 flex items-center justify-end gap-1 mt-0.5">
                      <span className="text-green-500">↑</span>
                      <span>{inv.expectedReturnPct}% APR</span>
                    </p>
                    {inv.payoutAmountEtb && inv.payoutAmountEtb > 0 && (
                      <p className="text-xs text-blue-600 font-semibold mt-1">
                        Paid out: {inv.payoutAmountEtb.toLocaleString()} ETB
                      </p>
                    )}
                  </div>
                </div>

                <div className="grid grid-cols-2 md:grid-cols-4 gap-3 mt-4 pt-4 border-t border-gray-100">
                  <div>
                    <p className="text-xs text-gray-400">Expected Return</p>
                    <p className="text-sm font-semibold text-green-600">
                      +{(inv.amountEtb * inv.expectedReturnPct / 100).toFixed(0)} ETB/yr
                    </p>
                  </div>
                  {inv.actualReturnPct != null && (
                    <div>
                      <p className="text-xs text-gray-400">Actual Return</p>
                      <p className="text-sm font-semibold text-blue-600">{inv.actualReturnPct}%</p>
                    </div>
                  )}
                  <div>
                    <p className="text-xs text-gray-400">Invested On</p>
                    <p className="text-sm font-semibold">{new Date(inv.createdAt).toLocaleDateString()}</p>
                  </div>
                  <div>
                    <p className="text-xs text-gray-400">Investment ID</p>
                    <p className="text-xs text-gray-400 font-mono">{inv.id.slice(0, 8)}…</p>
                  </div>
                </div>

                <div className="flex gap-4 mt-4">
                  <Link href={`/portfolio/${inv.id}`}
                    className="text-green-600 text-sm font-semibold hover:underline">
                    View Details →
                  </Link>
                  <Link href={`/portfolio/${inv.id}/vouchers`}
                    className="text-blue-600 text-sm font-semibold hover:underline">
                    Vouchers →
                  </Link>
                  {['PENDING', 'ESCROW_LOCKED'].includes(inv.status) && (
                    <button onClick={() => handleCancel(inv.id)} disabled={cancellingId === inv.id}
                      className="text-red-500 text-sm font-semibold hover:underline disabled:opacity-50 ml-auto">
                      {cancellingId === inv.id ? 'Cancelling…' : 'Cancel'}
                    </button>
                  )}
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}

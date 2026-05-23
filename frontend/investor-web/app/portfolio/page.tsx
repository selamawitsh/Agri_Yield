'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import toast from 'react-hot-toast';
import api from '@/lib/api';
import Navbar from '@/components/Navbar';
import StatusBadge from '@/components/StatusBadge';
import { Investment, PortfolioStats } from '@/lib/types';

export default function PortfolioPage() {
  const router = useRouter();
  const [investments, setInvestments] = useState<Investment[]>([]);
  const [stats, setStats] = useState<PortfolioStats | null>(null);
  const [loading, setLoading] = useState(true);
  const [cancellingId, setCancellingId] = useState<string | null>(null);
  const [filter, setFilter] = useState('ALL');

  useEffect(() => {
    const token = localStorage.getItem('access_token');
    if (!token) {
      router.push('/login');
      return;
    }
    fetchPortfolio();
  }, [router]);

  const fetchPortfolio = async () => {
    try {
      const response = await api.get('/portfolio');
      if (response.data.success) {
        const data: Investment[] = response.data.data;
        setInvestments(data);

        const totalInvested = data.reduce((s, i) => s + i.amountEtb, 0);
        const active = data.filter(i => ['ACTIVE', 'ESCROW_LOCKED'].includes(i.status)).length;
        const completed = data.filter(i => i.status === 'COMPLETED').length;
        const cancelled = data.filter(i => i.status === 'CANCELLED').length;
        const avgApr = data.length > 0
            ? data.reduce((s, i) => s + i.expectedReturnPct, 0) / data.length : 0;

        setStats({
          totalInvested,
          totalReturned: 0,
          activeInvestments: active,
          completedInvestments: completed,
          cancelledInvestments: cancelled,
          averageApr: avgApr
        });
      }
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Failed to load portfolio');
    } finally {
      setLoading(false);
    }
  };

  const handleCancel = async (investmentId: string) => {
    if (!window.confirm('Are you sure you want to cancel this investment? Your funds will be returned.')) return;

    setCancellingId(investmentId);
    try {
      const response = await api.post(`/investments/${investmentId}/cancel`, { reason: 'Cancelled by investor' });
      if (response.data.success) {
        toast.success('Investment cancelled. Funds will be returned.');
        fetchPortfolio();
      }
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Cannot cancel this investment');
    } finally {
      setCancellingId(null);
    }
  };

  const filtered = filter === 'ALL'
      ? investments
      : investments.filter(i => {
        if (filter === 'ACTIVE') return ['ACTIVE', 'ESCROW_LOCKED', 'PENDING'].includes(i.status);
        return i.status === filter;
      });

  if (loading) return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-green-600" />
      </div>
  );

  return (
      <div className="min-h-screen bg-gray-50">
        <Navbar />
        <div className="container mx-auto px-6 py-8">

          <div className="mb-6">
            <h1 className="text-2xl font-bold">My Portfolio</h1>
            <p className="text-gray-500 mt-1">Track all your farm investments</p>
          </div>

          {/* Stats */}
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-6">
            {[
              { label: 'Total Invested', value: `${(stats?.totalInvested || 0).toLocaleString()} ETB`, color: 'text-green-600' },
              { label: 'Active', value: stats?.activeInvestments || 0, color: 'text-blue-600' },
              { label: 'Completed', value: stats?.completedInvestments || 0, color: 'text-gray-600' },
              { label: 'Avg APR', value: `${(stats?.averageApr || 0).toFixed(1)}%`, color: 'text-purple-600' },
            ].map((s) => (
                <div key={s.label} className="bg-white rounded-xl shadow p-4">
                  <p className="text-gray-500 text-sm">{s.label}</p>
                  <p className={`text-xl font-bold ${s.color}`}>{s.value}</p>
                </div>
            ))}
          </div>

          {/* Filter tabs */}
          <div className="flex gap-2 mb-5 bg-white rounded-xl shadow p-2 w-fit">
            {['ALL', 'ACTIVE', 'COMPLETED', 'CANCELLED'].map((f) => (
                <button key={f} onClick={() => setFilter(f)}
                        className={`px-4 py-1.5 rounded-lg text-sm font-medium transition ${
                            filter === f ? 'bg-green-600 text-white' : 'text-gray-600 hover:bg-gray-100'
                        }`}>
                  {f}
                </button>
            ))}
          </div>

          {/* Investments List */}
          {filtered.length === 0 ? (
              <div className="bg-white rounded-xl shadow p-12 text-center">
                <svg className="w-16 h-16 mx-auto mb-4 text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10" />
                </svg>
                <p className="text-gray-500 text-lg">No investments found</p>
                <Link href="/listings" className="mt-4 flex items-center justify-center gap-2 mx-auto w-fit bg-green-600 text-white px-5 py-2 rounded-lg hover:bg-green-700">
                  Browse Farms
                  <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M14 5l7 7m0 0l-7 7m7-7H3" /></svg>
                </Link>
              </div>
          ) : (
              <div className="space-y-4">
                {filtered.map((inv) => (
                    <div key={inv.id} className="bg-white rounded-xl shadow p-5 hover:shadow-md transition">
                      <div className="flex justify-between items-start">
                        <div>
                          <div className="flex items-center gap-3 mb-1">
                            <h3 className="font-semibold text-gray-800">{inv.cropType} - {inv.region}</h3>
                            <StatusBadge status={inv.status} />
                          </div>
                          <p className="text-gray-500 text-sm">{inv.seasonName}</p>
                          {inv.notes && <p className="text-gray-400 text-xs mt-1 italic">&quot;{inv.notes}&quot;</p>}
                          {inv.cancelledReason && (
                              <p className="text-red-500 text-xs mt-1">Cancelled: {inv.cancelledReason}</p>
                          )}
                        </div>
                        <div className="text-right">
                          <p className="text-xl font-bold text-green-600">{inv.amountEtb.toLocaleString()} ETB</p>
                          <p className="text-sm text-gray-500">{inv.expectedReturnPct}% APR</p>
                        </div>
                      </div>

                      <div className="grid grid-cols-2 md:grid-cols-4 gap-3 mt-4 pt-4 border-t">
                        <div>
                          <p className="text-xs text-gray-500">Expected Return</p>
                          <p className="text-sm font-medium text-green-600">
                            +{(inv.amountEtb * inv.expectedReturnPct / 100).toFixed(2)} ETB/yr
                          </p>
                        </div>
                        {inv.actualReturnPct && (
                            <div>
                              <p className="text-xs text-gray-500">Actual Return</p>
                              <p className="text-sm font-medium">{inv.actualReturnPct}%</p>
                            </div>
                        )}
                        <div>
                          <p className="text-xs text-gray-500">Invested On</p>
                          <p className="text-sm font-medium">{new Date(inv.createdAt).toLocaleDateString()}</p>
                        </div>
                        <div>
                          <p className="text-xs text-gray-500">Investment ID</p>
                          <p className="text-xs text-gray-400 font-mono">{inv.id.slice(0, 8)}...</p>
                        </div>
                      </div>

                      <div className="flex gap-3 mt-4">
                        <Link href={`/portfolio/${inv.id}`}
                              className="flex items-center gap-1 text-green-600 text-sm hover:underline">
                          View Details
                          <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M9 5l7 7-7 7" /></svg>
                        </Link>
                        {['PENDING', 'ESCROW_LOCKED'].includes(inv.status) && (
                            <button
                                onClick={() => handleCancel(inv.id)}
                                disabled={cancellingId === inv.id}
                                className="text-red-500 text-sm hover:underline disabled:opacity-50">
                              {cancellingId === inv.id ? 'Cancelling...' : 'Cancel Investment'}
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
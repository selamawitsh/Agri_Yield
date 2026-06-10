'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import Navbar from '@/components/Navbar';
import { getMyBids } from '@/lib/api';
import type { Bid } from '@/lib/types';

export default function AnalyticsPage() {
  const router = useRouter();
  const [bids,    setBids]    = useState<Bid[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!localStorage.getItem('access_token')) { router.push('/login'); return; }
    getMyBids()
      .then(r => { if (r.data.success) setBids(r.data.data || []); })
      .catch(() => {})
      .finally(() => setLoading(false));
  }, []);

  if (loading) return (
    <div className="min-h-screen flex items-center justify-center">
      <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-teal-600" />
    </div>
  );

  const completed   = bids.filter(b => b.status === 'COMPLETED');
  const active      = bids.filter(b => ['PENDING','ACCEPTED','CONTRACT_SIGNED'].includes(b.status));
  const totalSpend  = completed.reduce((s, b) => s + b.totalValueEtb, 0);
  const totalQty    = completed.reduce((s, b) => s + b.quantityQuintals, 0);
  const avgPrice    = completed.length > 0
    ? completed.reduce((s, b) => s + b.pricePerQuintalEtb, 0) / completed.length : 0;
  const depositLocked = active.reduce((s, b) => s + b.bidDepositEtb, 0);

  // Group by status for distribution
  const statusGroups: Record<string, number> = {};
  bids.forEach(b => { statusGroups[b.status] = (statusGroups[b.status] || 0) + 1; });

  const barWidth = (count: number) => bids.length > 0 ? (count / bids.length) * 100 : 0;
  const statusColor: Record<string, string> = {
    PENDING: 'bg-yellow-400', ACCEPTED: 'bg-blue-400', CONTRACT_SIGNED: 'bg-indigo-400',
    COMPLETED: 'bg-green-500', DEFAULTED: 'bg-red-400', EXPIRED: 'bg-gray-300',
  };

  return (
    <div className="min-h-screen bg-gray-50 pb-20 md:pb-0">
      <Navbar />
      <div className="container mx-auto px-4 sm:px-6 py-6 max-w-5xl">

        <div className="mb-6">
          <h1 className="text-2xl font-bold text-gray-900">Procurement Analytics</h1>
          <p className="text-gray-500 mt-1 text-sm">Season performance summary — SRS §6.4</p>
        </div>

        {/* KPI cards — SRS §6.4 */}
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-6">
          {[
            { label: 'Total Spent',         value: `${totalSpend.toLocaleString()} ETB`,   color: 'text-teal-600',   icon: '💰' },
            { label: 'Quintals Procured',   value: `${totalQty.toFixed(1)} qt`,             color: 'text-green-600',  icon: '🌾' },
            { label: 'Avg Price / Quintal', value: `${avgPrice.toFixed(0)} ETB`,            color: 'text-blue-600',   icon: '📊' },
            { label: 'Deposit Locked',      value: `${depositLocked.toLocaleString()} ETB`, color: 'text-orange-600', icon: '🔒' },
          ].map(s => (
            <div key={s.label} className="bg-white rounded-2xl shadow-sm border border-gray-100 p-4">
              <div className="text-2xl mb-2">{s.icon}</div>
              <p className="text-gray-400 text-xs uppercase tracking-wide font-medium">{s.label}</p>
              <p className={`text-xl font-bold ${s.color} mt-1`}>{s.value}</p>
            </div>
          ))}
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-6">

          {/* Bid status distribution */}
          <div className="bg-white rounded-2xl shadow-sm border border-gray-100 p-6">
            <h3 className="font-bold text-gray-800 mb-4">Bid Status Distribution</h3>
            {bids.length === 0 ? (
              <p className="text-gray-400 text-sm text-center py-8">No bids yet</p>
            ) : (
              <div className="space-y-3">
                {Object.entries(statusGroups).map(([status, count]) => (
                  <div key={status}>
                    <div className="flex justify-between text-xs mb-1">
                      <span className="font-medium text-gray-700">{status.replace(/_/g, ' ')}</span>
                      <span className="text-gray-400">{count} bid{count !== 1 ? 's' : ''}</span>
                    </div>
                    <div className="w-full bg-gray-100 rounded-full h-2">
                      <div className={`h-2 rounded-full ${statusColor[status] || 'bg-gray-400'}`}
                        style={{ width: `${barWidth(count)}%` }} />
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>

          {/* Procurement summary table — SRS §6.4 */}
          <div className="bg-white rounded-2xl shadow-sm border border-gray-100 p-6">
            <h3 className="font-bold text-gray-800 mb-4">Completed Deals</h3>
            {completed.length === 0 ? (
              <p className="text-gray-400 text-sm text-center py-8">No completed deals yet</p>
            ) : (
              <div className="space-y-3">
                {completed.map(b => (
                  <div key={b.id} className="flex justify-between items-center border-b border-gray-50 pb-2">
                    <div>
                      <p className="text-sm font-semibold text-gray-800">{b.quantityQuintals} qt</p>
                      <p className="text-xs text-gray-400">{b.pricePerQuintalEtb.toLocaleString()} ETB/qt</p>
                    </div>
                    <div className="text-right">
                      <p className="text-sm font-bold text-teal-700">{b.totalValueEtb.toLocaleString()} ETB</p>
                      <p className="text-xs text-gray-400">{new Date(b.createdAt).toLocaleDateString()}</p>
                    </div>
                  </div>
                ))}
                <div className="pt-2 border-t border-gray-200 flex justify-between">
                  <span className="text-sm font-bold text-gray-700">Total</span>
                  <span className="text-sm font-bold text-teal-700">{totalSpend.toLocaleString()} ETB</span>
                </div>
              </div>
            )}
          </div>
        </div>

        {/* Savings vs broker model — SRS §6.4 */}
        <div className="bg-gradient-to-r from-teal-700 to-cyan-700 rounded-2xl p-6 text-white">
          <h3 className="font-bold text-lg mb-2">📈 Platform vs Broker Savings</h3>
          <p className="text-teal-100 text-sm mb-4">
            Traditional broker commission is typically 8-12% of transaction value.
            Agri-Yield charges a 5% platform fee.
          </p>
          <div className="grid grid-cols-3 gap-4 text-center">
            <div>
              <p className="text-teal-200 text-xs">Total Procured</p>
              <p className="text-xl font-bold">{totalSpend.toLocaleString()} ETB</p>
            </div>
            <div>
              <p className="text-teal-200 text-xs">Broker Cost (10% avg)</p>
              <p className="text-xl font-bold">{(totalSpend * 0.10).toLocaleString()} ETB</p>
            </div>
            <div>
              <p className="text-teal-200 text-xs">Your Savings</p>
              <p className="text-xl font-bold text-lime-300">
                {(totalSpend * 0.05).toLocaleString()} ETB
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

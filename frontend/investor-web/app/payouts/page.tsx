'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import toast from 'react-hot-toast';
import api from '@/lib/api';
import Navbar from '@/components/Navbar';
import { PayoutRecord } from '@/lib/types';

export default function PayoutsPage() {
  const router = useRouter();
  const [payouts,  setPayouts]  = useState<PayoutRecord[]>([]);
  const [loading,  setLoading]  = useState(true);
  const [dateFrom, setDateFrom] = useState('');
  const [dateTo,   setDateTo]   = useState('');

  useEffect(() => {
    const token = localStorage.getItem('access_token');
    if (!token) { router.push('/login'); return; }
    loadPayouts();
  }, []);

  const loadPayouts = async () => {
    try {
      const res = await api.get('/portfolio/payouts');
      if (res.data.success) setPayouts(res.data.data || []);
    } catch (err: any) {
      toast.error(err.response?.data?.message || 'Failed to load payouts');
    } finally { setLoading(false); }
  };

  const filtered = payouts.filter(p => {
    const d = new Date(p.paidAt);
    if (dateFrom && d < new Date(dateFrom)) return false;
    if (dateTo   && d > new Date(dateTo))   return false;
    return true;
  });

  const totalReceived = filtered.reduce((s, p) => s + p.totalEtb,   0);
  const totalReturns  = filtered.reduce((s, p) => s + p.returnEtb,  0);
  const totalPrincipal = filtered.reduce((s, p) => s + p.principalEtb, 0);
  const avgApr = filtered.length > 0
    ? filtered.reduce((s, p) => s + p.actualApr, 0) / filtered.length : 0;

  if (loading) return (
    <div className="min-h-screen flex items-center justify-center">
      <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-green-600" />
    </div>
  );

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />
      <div className="container mx-auto px-6 py-8 max-w-5xl">

        <div className="flex justify-between items-center mb-6">
          <div>
            <h1 className="text-2xl font-bold text-gray-900">Payout History</h1>
            <p className="text-gray-500 mt-1">All harvest settlement payments received</p>
          </div>
          <Link href="/statements"
            className="bg-green-700 text-white px-4 py-2 rounded-xl text-sm font-semibold hover:bg-green-600 transition">
            📄 Download Statement
          </Link>
        </div>

        {/* Date range filter */}
        <div className="bg-white rounded-2xl shadow-sm border border-gray-100 p-5 mb-6">
          <div className="flex flex-wrap gap-4 items-end">
            <div>
              <label className="block text-xs font-semibold text-gray-500 mb-1 uppercase">From</label>
              <input type="date" value={dateFrom} onChange={e => setDateFrom(e.target.value)}
                className="px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-green-500" />
            </div>
            <div>
              <label className="block text-xs font-semibold text-gray-500 mb-1 uppercase">To</label>
              <input type="date" value={dateTo} onChange={e => setDateTo(e.target.value)}
                className="px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-green-500" />
            </div>
            <button onClick={() => { setDateFrom(''); setDateTo(''); }}
              className="px-4 py-2.5 bg-gray-100 text-gray-600 rounded-xl text-sm font-semibold hover:bg-gray-200 transition">
              Clear
            </button>
          </div>
        </div>

        {/* Summary stats */}
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-6">
          {[
            { label: 'Total Received', value: `${totalReceived.toLocaleString()} ETB`,  color: 'text-green-600' },
            { label: 'Total Returns',  value: `+${totalReturns.toLocaleString()} ETB`,  color: 'text-blue-600'  },
            { label: 'Principal Back', value: `${totalPrincipal.toLocaleString()} ETB`, color: 'text-gray-700'  },
            { label: 'Avg APR',        value: `${avgApr.toFixed(1)}%`,                  color: 'text-purple-600'},
          ].map(s => (
            <div key={s.label} className="bg-white rounded-2xl shadow-sm border border-gray-100 p-4">
              <p className="text-gray-400 text-xs uppercase tracking-wide font-medium">{s.label}</p>
              <p className={`text-xl font-bold ${s.color} mt-1`}>{s.value}</p>
            </div>
          ))}
        </div>

        {/* Payout list */}
        {filtered.length === 0 ? (
          <div className="bg-white rounded-2xl shadow-sm border border-gray-100 p-16 text-center">
            <p className="text-4xl mb-4">💵</p>
            <p className="text-gray-500 text-lg font-medium">No payouts yet</p>
            <p className="text-gray-400 text-sm mt-2">Payouts are transferred when harvest is confirmed by the off-taker</p>
            <Link href="/listings" className="mt-4 inline-block bg-green-600 text-white px-5 py-2.5 rounded-xl font-semibold hover:bg-green-700 transition text-sm">
              Browse Farm Listings
            </Link>
          </div>
        ) : (
          <div className="space-y-4">
            {filtered.map(p => (
              <div key={p.id} className="bg-white rounded-2xl shadow-sm border border-gray-100 p-5 hover:shadow-md transition">
                <div className="flex justify-between items-start">
                  <div>
                    <p className="font-bold text-gray-800">
                      {p.payoutReason || 'Harvest Settlement'}
                    </p>
                    <p className="text-gray-400 text-sm mt-0.5">
                      {new Date(p.paidAt).toLocaleDateString('en-ET', { year: 'numeric', month: 'long', day: 'numeric' })}
                    </p>
                    <p className="text-gray-400 text-xs mt-0.5 font-mono">Farm: {p.farmId.slice(0, 8)}…</p>
                  </div>
                  <div className="text-right">
                    <p className="text-xl font-bold text-green-600">{p.totalEtb.toLocaleString()} ETB</p>
                    <p className="text-xs text-blue-600 font-semibold mt-0.5">{p.actualApr.toFixed(1)}% actual APR</p>
                  </div>
                </div>

                <div className="grid grid-cols-3 gap-3 mt-4 pt-4 border-t border-gray-100">
                  <div>
                    <p className="text-xs text-gray-400">Principal returned</p>
                    <p className="text-sm font-semibold text-gray-700">{p.principalEtb.toLocaleString()} ETB</p>
                  </div>
                  <div>
                    <p className="text-xs text-gray-400">Profit earned</p>
                    <p className="text-sm font-semibold text-green-600">+{p.returnEtb.toLocaleString()} ETB</p>
                  </div>
                  <div className="text-right">
                    <Link href={`/portfolio/${p.investmentId}`}
                      className="text-green-600 text-sm font-semibold hover:underline">
                      View investment →
                    </Link>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}

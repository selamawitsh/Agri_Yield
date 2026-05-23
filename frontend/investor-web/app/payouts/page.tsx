'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import toast from 'react-hot-toast';
import api from '@/lib/api';
import Navbar from '@/components/Navbar';
import { PayoutRecord } from '@/lib/types';

export default function PayoutsPage() {
  const router = useRouter();
  const [payouts, setPayouts] = useState<PayoutRecord[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const token = localStorage.getItem('access_token');
    if (!token) {
      router.push('/login');
      return;
    }
    fetchPayouts();
  }, [router]);

  const fetchPayouts = async () => {
    try {
      const response = await api.get('/portfolio/payouts');
      if (response.data.success) {
        setPayouts(response.data.data);
      }
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Failed to load payout history');
    } finally {
      setLoading(false);
    }
  };

  const totalReceived = payouts.reduce((s, p) => s + p.totalEtb, 0);
  const totalReturn = payouts.reduce((s, p) => s + p.returnEtb, 0);
  const totalPrincipal = payouts.reduce((s, p) => s + p.principalEtb, 0);

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
            <h1 className="text-2xl font-bold">Payout History</h1>
            <p className="text-gray-500 mt-1">Your complete earnings history</p>
          </div>

          {/* Summary Stats */}
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
            <div className="bg-white rounded-xl shadow p-5">
              <div className="text-2xl mb-1 text-green-600">
                <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
              </div>
              <p className="text-gray-500 text-sm">Total Received</p>
              <p className="text-xl font-bold text-green-600">{totalReceived.toLocaleString()} ETB</p>
            </div>
            <div className="bg-white rounded-xl shadow p-5">
              <div className="text-2xl mb-1 text-blue-600">
                <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M3 6l3 1m0 0l-3 9a5.002 5.002 0 006.001 0M6 7l3 9M6 7l6-2m6 2l3-1m-3 1l-3 9a5.002 5.002 0 006.001 0M18 7l3 9m-3-9l-6-2m0-2v2m0 16V5m0 16H9m3 0h3" /></svg>
              </div>
              <p className="text-gray-500 text-sm">Total Principal</p>
              <p className="text-xl font-bold text-blue-600">{totalPrincipal.toLocaleString()} ETB</p>
            </div>
            <div className="bg-white rounded-xl shadow p-5">
              <div className="text-2xl mb-1 text-purple-600">
                <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M13 7h8m0 0v8m0-8l-8 8-4-4-6 6" /></svg>
              </div>
              <p className="text-gray-500 text-sm">Total Earnings</p>
              <p className="text-xl font-bold text-purple-600">+{totalReturn.toLocaleString()} ETB</p>
            </div>
          </div>

          {payouts.length === 0 ? (
              <div className="bg-white rounded-xl shadow p-12 text-center">
                <svg className="w-16 h-16 mx-auto mb-4 text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M20 13V6a2 2 0 00-2-2H6a2 2 0 00-2 2v7m16 0v5a2 2 0 01-2 2H6a2 2 0 01-2-2v-5m16 0h-2.586a1 1 0 00-.707.293l-2.414 2.414a1 1 0 01-.707.293h-3.172a1 1 0 01-.707-.293l-2.414-2.414A1 1 0 006.586 13H4" />
                </svg>
                <p className="text-gray-500 text-lg">No payouts yet</p>
                <p className="text-gray-400 text-sm mt-1">
                  Payouts appear when your invested farms complete their harvest cycle
                </p>
              </div>
          ) : (
              <div className="bg-white rounded-xl shadow overflow-hidden">
                <table className="w-full text-sm">
                  <thead className="bg-gray-50 border-b">
                  <tr>
                    {['Date', 'Investment', 'Principal', 'Return', 'Total', 'APR', 'Reason'].map((h) => (
                        <th key={h} className="text-left px-4 py-3 text-gray-600 font-medium">{h}</th>
                    ))}
                  </tr>
                  </thead>
                  <tbody className="divide-y">
                  {payouts.map((payout) => (
                      <tr key={payout.id} className="hover:bg-gray-50">
                        <td className="px-4 py-3 text-gray-500">
                          {new Date(payout.paidAt).toLocaleDateString()}
                        </td>
                        <td className="px-4 py-3 font-mono text-xs text-gray-400">
                          {payout.investmentId.slice(0, 8)}...
                        </td>
                        <td className="px-4 py-3">{payout.principalEtb.toLocaleString()} ETB</td>
                        <td className="px-4 py-3 text-green-600">+{payout.returnEtb.toLocaleString()} ETB</td>
                        <td className="px-4 py-3 font-semibold">{payout.totalEtb.toLocaleString()} ETB</td>
                        <td className="px-4 py-3 text-purple-600">{payout.actualApr}%</td>
                        <td className="px-4 py-3 text-gray-500 text-xs">{payout.payoutReason || '-'}</td>
                      </tr>
                  ))}
                  </tbody>
                </table>
              </div>
          )}
        </div>
      </div>
  );
}
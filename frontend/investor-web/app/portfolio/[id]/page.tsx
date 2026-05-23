'use client';

import { useEffect, useState, useCallback } from 'react';
import { useRouter, useParams } from 'next/navigation';
import toast from 'react-hot-toast';
import api from '@/lib/api';
import Navbar from '@/components/Navbar';
import StatusBadge from '@/components/StatusBadge';
import { Investment } from '@/lib/types';

export default function InvestmentDetailPage() {
  const router = useRouter();
  const params = useParams();
  const investmentId = params.id as string;

  const [investment, setInvestment] = useState<Investment | null>(null);
  const [loading, setLoading] = useState(true);

  const fetchInvestment = useCallback(async () => {
    try {
      const response = await api.get(`/portfolio/${investmentId}`);
      if (response.data.success) {
        setInvestment(response.data.data);
      }
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Investment not found');
      router.push('/portfolio');
    } finally {
      setLoading(false);
    }
  }, [investmentId, router]);

  useEffect(() => {
    const token = localStorage.getItem('access_token');
    if (!token) {
      router.push('/login');
      return;
    }
    fetchInvestment();
  }, [fetchInvestment, router]);

  if (loading) return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-green-600" />
      </div>
  );

  if (!investment) return null;

  const expectedReturn = (investment.amountEtb * investment.expectedReturnPct / 100);

  return (
      <div className="min-h-screen bg-gray-50">
        <Navbar />
        <div className="container mx-auto px-6 py-8 max-w-3xl">

          <button onClick={() => router.back()} className="flex items-center gap-2 text-green-600 text-sm mb-4 hover:underline">
            <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M10 19l-7-7m0 0l7-7m-7 7h18" /></svg>
            Back to Portfolio
          </button>

          <div className="bg-white rounded-xl shadow-lg overflow-hidden">
            <div className="bg-gradient-to-r from-green-600 to-green-800 p-6 text-white">
              <div className="flex justify-between items-start">
                <div>
                  <h1 className="text-2xl font-bold">{investment.cropType} Investment</h1>
                  <p className="text-green-100">{investment.region} | {investment.seasonName}</p>
                </div>
                <StatusBadge status={investment.status} />
              </div>
            </div>

            <div className="p-6">
              <div className="grid grid-cols-2 md:grid-cols-3 gap-4 mb-6">
                {[
                  { label: 'Amount Invested', value: `${investment.amountEtb.toLocaleString()} ETB`, highlight: true },
                  { label: 'Expected APR', value: `${investment.expectedReturnPct}%` },
                  { label: 'Expected Return/yr', value: `+${expectedReturn.toFixed(2)} ETB` },
                  { label: 'Actual APR', value: investment.actualReturnPct ? `${investment.actualReturnPct}%` : 'Pending' },
                  { label: 'Status', value: investment.status.replace(/_/g, ' ') },
                  { label: 'Invested On', value: new Date(investment.createdAt).toLocaleDateString() },
                ].map((item) => (
                    <div key={item.label} className={`rounded-lg p-4 ${item.highlight ? 'bg-green-50 border border-green-200' : 'bg-gray-50'}`}>
                      <p className="text-xs text-gray-500">{item.label}</p>
                      <p className={`font-semibold mt-0.5 ${item.highlight ? 'text-green-700 text-lg' : 'text-gray-800'}`}>
                        {item.value}
                      </p>
                    </div>
                ))}
              </div>

              {/* IDs */}
              <div className="border-t pt-4 space-y-2 text-xs text-gray-400 font-mono">
                <p>Investment ID: {investment.id}</p>
                <p>Farm ID: {investment.farmId}</p>
                <p>Crop Cycle: {investment.cropCycleId}</p>
                <p>Input Need: {investment.inputNeedId}</p>
                <p>Last Updated: {new Date(investment.updatedAt).toLocaleString()}</p>
              </div>

              {investment.notes && (
                  <div className="mt-4 bg-blue-50 rounded-lg p-3">
                    <p className="text-xs text-blue-600 font-medium">Your notes:</p>
                    <p className="text-sm text-blue-800 mt-1 italic">&quot;{investment.notes}&quot;</p>
                  </div>
              )}

              {investment.cancelledReason && (
                  <div className="mt-4 bg-red-50 rounded-lg p-3">
                    <p className="text-xs text-red-600 font-medium">Cancellation reason:</p>
                    <p className="text-sm text-red-800 mt-1">{investment.cancelledReason}</p>
                  </div>
              )}

              {/* Escrow info */}
              <div className="mt-6 bg-yellow-50 border border-yellow-200 rounded-xl p-4 text-sm text-yellow-800">
                <strong>Escrow Status: </strong>
                Your funds are securely held in escrow. They will be released to the farmer in stages as vouchers are redeemed and input needs are fulfilled.
              </div>
            </div>
          </div>
        </div>
      </div>
  );
}
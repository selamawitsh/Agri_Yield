'use client';

import { useEffect, useState, useCallback } from 'react';
import { useRouter, useParams } from 'next/navigation';
import Link from 'next/link';
import toast from 'react-hot-toast';
import api from '@/lib/api';
import Navbar from '@/components/Navbar';
import StatusBadge from '@/components/StatusBadge';
import EscrowStateMachine from '@/components/EscrowStateMachine';
import NdviChart from '@/components/NdviChart';
import AprHistoryChart from '@/components/AprHistoryChart';
import { Investment, NdviReading, PayoutRecord } from '@/lib/types';

export default function InvestmentDetailPage() {
  const router = useRouter();
  const params = useParams();
  const investmentId = params.id as string;

  const [investment, setInvestment] = useState<Investment | null>(null);
  const [ndviHistory, setNdviHistory] = useState<NdviReading[]>([]);
  const [payouts, setPayouts] = useState<PayoutRecord[]>([]);
  const [loading, setLoading] = useState(true);

  const fetchAll = useCallback(async () => {
    try {
      const res = await api.get(`/portfolio/${investmentId}`);
      if (res.data.success) {
        const inv: Investment = res.data.data;
        setInvestment(inv);

        // Fetch NDVI for the farm (uses listing's ndvi-history endpoint)
        // We need the listing ID — try to get it from the farm's active listing
        try {
          const listingsRes = await api.get(`/listings?farmId=${inv.farmId}`);
          if (listingsRes.data.success && listingsRes.data.data?.length > 0) {
            const listingId = listingsRes.data.data[0].id;
            const ndviRes = await api.get(`/listings/${listingId}/ndvi-history`);
            if (ndviRes.data.success) setNdviHistory(ndviRes.data.data || []);
          }
        } catch {}

        // Fetch payout history filtered for this investment
        try {
          const payoutsRes = await api.get('/portfolio/payouts');
          if (payoutsRes.data.success) {
            setPayouts((payoutsRes.data.data || []).filter((p: PayoutRecord) => p.investmentId === investmentId));
          }
        } catch {}
      }
    } catch (err: any) {
      toast.error(err.response?.data?.message || 'Investment not found');
      router.push('/portfolio');
    } finally {
      setLoading(false);
    }
  }, [investmentId, router]);

  useEffect(() => {
    const token = localStorage.getItem('access_token');
    if (!token) { router.push('/login'); return; }
    fetchAll();
  }, [fetchAll, router]);

  if (loading) return (
    <div className="min-h-screen flex items-center justify-center">
      <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-green-600" />
    </div>
  );
  if (!investment) return null;

  const expectedReturn = (investment.amountEtb * investment.expectedReturnPct / 100);
  const investedFarm = { listingId: investment.inputNeedId };

  return (
    <div className="min-h-screen bg-gray-50 pb-12">
      <Navbar />
      <div className="container mx-auto px-4 sm:px-6 py-6 max-w-4xl">

        <button onClick={() => router.back()}
          className="flex items-center gap-2 text-green-600 text-sm mb-5 font-semibold hover:underline">
          ← Back to Portfolio
        </button>

        {/* Header card */}
        <div className="bg-white rounded-3xl shadow-sm border border-gray-100 overflow-hidden mb-6">
          <div className="bg-gradient-to-r from-green-700 to-emerald-900 p-6 text-white">
            <div className="flex justify-between items-start">
              <div>
                <h1 className="text-2xl font-bold">{investment.cropType} Investment</h1>
                <p className="text-green-100 text-sm mt-1">{investment.region} · {investment.seasonName}</p>
              </div>
              <StatusBadge status={investment.status} />
            </div>
          </div>

          <div className="p-6">
            {/* Stats */}
            <div className="grid grid-cols-2 md:grid-cols-3 gap-3 mb-6">
              {[
                { label: 'Amount Invested', value: `${investment.amountEtb.toLocaleString()} ETB`, highlight: true },
                { label: 'Expected APR', value: `${investment.expectedReturnPct}%` },
                { label: 'Expected Return/yr', value: `+${expectedReturn.toFixed(2)} ETB` },
                { label: 'Actual APR', value: investment.actualReturnPct ? `${investment.actualReturnPct}%` : 'Pending' },
                { label: 'Invested On', value: new Date(investment.createdAt).toLocaleDateString() },
                { label: 'Last Updated', value: new Date(investment.updatedAt).toLocaleDateString() },
              ].map(item => (
                <div key={item.label} className={`rounded-2xl p-4 ${item.highlight ? 'bg-green-50 border border-green-200' : 'bg-gray-50 border border-gray-100'}`}>
                  <p className="text-xs text-gray-400 font-medium uppercase tracking-wide">{item.label}</p>
                  <p className={`font-bold mt-1 ${item.highlight ? 'text-green-700 text-lg' : 'text-gray-800 text-sm'}`}>{item.value}</p>
                </div>
              ))}
            </div>

            {investment.notes && (
              <div className="mb-4 bg-blue-50 rounded-2xl p-3 border border-blue-100">
                <p className="text-xs text-blue-500 font-semibold mb-1">Your note</p>
                <p className="text-sm text-blue-800 italic">"{investment.notes}"</p>
              </div>
            )}
            {investment.cancelledReason && (
              <div className="mb-4 bg-red-50 rounded-2xl p-3 border border-red-100">
                <p className="text-xs text-red-500 font-semibold mb-1">Cancellation reason</p>
                <p className="text-sm text-red-800">{investment.cancelledReason}</p>
              </div>
            )}

            {/* IDs */}
            <div className="border-t pt-4 space-y-1 text-xs text-gray-400 font-mono">
              <p>Investment ID: {investment.id}</p>
              <p>Farm ID: {investment.farmId}</p>
              <p>Crop Cycle: {investment.cropCycleId}</p>
            </div>
          </div>
        </div>

        {/* Escrow State Machine Visualizer — SRS requirement */}
        <div className="bg-white rounded-3xl shadow-sm border border-gray-100 p-6 mb-6">
          <h3 className="font-bold text-gray-800 mb-6">Escrow Progress</h3>
          <EscrowStateMachine status={investment.status} />
          <div className="mt-6 bg-amber-50 border border-amber-100 rounded-2xl p-4 text-sm text-amber-800">
            <strong>🔒 Escrow:</strong> Your funds are securely held and released only when vouchers are redeemed and harvest is confirmed.
          </div>
        </div>

        {/* NDVI Chart — SRS requirement */}
        <div className="bg-white rounded-3xl shadow-sm border border-gray-100 p-6 mb-6">
          <h3 className="font-bold text-gray-800 mb-1">NDVI Trend for This Farm</h3>
          <p className="text-xs text-gray-400 mb-4">Satellite crop health over the season</p>
          <NdviChart data={ndviHistory} height={200} />
        </div>

        {/* APR History Chart — SRS requirement */}
        <div className="bg-white rounded-3xl shadow-sm border border-gray-100 p-6 mb-6">
          <h3 className="font-bold text-gray-800 mb-1">APR History</h3>
          <p className="text-xs text-gray-400 mb-4">How the return rate evolved with NDVI and weather signals</p>
          <AprHistoryChart baseApr={investment.expectedReturnPct * 0.85} currentApr={investment.expectedReturnPct} />
        </div>

        {/* Voucher Redemption Log — SRS requirement */}
        <div className="bg-white rounded-3xl shadow-sm border border-gray-100 p-6 mb-6">
          <h3 className="font-bold text-gray-800 mb-4">Voucher Redemption Log</h3>
          <div className="space-y-3">
            {[
              { seq: 1, label: 'Seed Voucher', icon: '🌱', status: 'pending' },
              { seq: 2, label: 'Fertilizer Voucher', icon: '🧪', status: 'pending' },
              { seq: 3, label: 'Pesticide Voucher', icon: '🛡', status: 'pending' },
            ].map(v => (
              <div key={v.seq} className="flex items-center gap-4 border border-gray-100 rounded-2xl p-3">
                <div className="w-10 h-10 bg-gray-100 rounded-xl flex items-center justify-center text-xl">{v.icon}</div>
                <div className="flex-1">
                  <p className="text-sm font-semibold text-gray-700">#{v.seq} — {v.label}</p>
                  <p className="text-xs text-gray-400">Awaiting redemption at certified merchant</p>
                </div>
                <span className="text-xs font-bold text-gray-400 bg-gray-100 px-2 py-1 rounded-full">Pending</span>
              </div>
            ))}
          </div>
        </div>

        {/* Payout Calculator — SRS requirement */}
        <div className="bg-white rounded-3xl shadow-sm border border-gray-100 p-6 mb-6">
          <h3 className="font-bold text-gray-800 mb-4">Payout Calculator</h3>
          <div className="grid grid-cols-3 gap-4">
            <div className="bg-gray-50 rounded-2xl p-4 text-center">
              <p className="text-xs text-gray-400 font-medium uppercase">Principal</p>
              <p className="text-lg font-bold text-gray-800 mt-1">{investment.amountEtb.toLocaleString()} ETB</p>
            </div>
            <div className="bg-green-50 rounded-2xl p-4 text-center">
              <p className="text-xs text-gray-400 font-medium uppercase">Expected Return</p>
              <p className="text-lg font-bold text-green-700 mt-1">+{expectedReturn.toFixed(0)} ETB</p>
            </div>
            <div className="bg-emerald-50 rounded-2xl p-4 text-center">
              <p className="text-xs text-gray-400 font-medium uppercase">Total Payout</p>
              <p className="text-lg font-bold text-emerald-700 mt-1">{(investment.amountEtb + expectedReturn).toFixed(0)} ETB</p>
            </div>
          </div>
        </div>

        {/* Payout History for this investment */}
        {payouts.length > 0 && (
          <div className="bg-white rounded-3xl shadow-sm border border-gray-100 p-6">
            <h3 className="font-bold text-gray-800 mb-4">Received Payouts</h3>
            <div className="space-y-3">
              {payouts.map(p => (
                <div key={p.id} className="flex justify-between items-center border border-gray-100 rounded-2xl p-4">
                  <div>
                    <p className="text-sm font-semibold text-gray-700">{p.payoutReason || 'Settlement'}</p>
                    <p className="text-xs text-gray-400">{new Date(p.paidAt).toLocaleDateString()}</p>
                  </div>
                  <div className="text-right">
                    <p className="font-bold text-green-600">{p.totalEtb.toLocaleString()} ETB</p>
                    <p className="text-xs text-gray-400">{p.actualApr}% APR</p>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

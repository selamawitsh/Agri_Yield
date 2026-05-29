'use client';

import { useEffect, useState, useCallback } from 'react';
import { useRouter, useParams } from 'next/navigation';
import toast from 'react-hot-toast';
import api from '@/lib/api';
import Navbar from '@/components/Navbar';
import VoucherStatusBadge from '@/components/VoucherStatusBadge';
import VoucherCategoryBadge from '@/components/VoucherCategoryBadge';
import VoucherSequenceBar from '@/components/VoucherSequenceBar';
import { Voucher, VoucherStatus, FarmVoucherTimeline } from '@/lib/types';

const STATUS_FILTERS: { label: string; value: string }[] = [
  { label: 'All', value: 'ALL' },
  { label: 'Active', value: 'ACTIVE' },
  { label: 'Redeemed', value: 'REDEEMED' },
  { label: 'Locked', value: 'GENERATED' },
  { label: 'Expired', value: 'EXPIRED' },
];

export default function InvestmentVouchersPage() {
  const router = useRouter();
  const params = useParams();
  const investmentId = params.id as string;

  const [timeline, setTimeline] = useState<FarmVoucherTimeline | null>(null);
  const [loading, setLoading] = useState(true);
  const [filter, setFilter] = useState('ALL');
  const [selectedVoucher, setSelectedVoucher] = useState<Voucher | null>(null);

  const fetchVouchers = useCallback(async () => {
    try {
      // Get investment to find the farmId first
      const invRes = await api.get(`/portfolio/${investmentId}`);
      if (!invRes.data.success) throw new Error('Investment not found');
      const farmId = invRes.data.data.farmId;

      // Then fetch all vouchers for that farm
      const vRes = await api.get(`/vouchers/farm/${farmId}`);
      if (vRes.data.success) {
        setTimeline(vRes.data.data);
      }
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Failed to load vouchers');
      router.push(`/portfolio/${investmentId}`);
    } finally {
      setLoading(false);
    }
  }, [investmentId, router]);

  useEffect(() => {
    const token = localStorage.getItem('access_token');
    if (!token) { router.push('/login'); return; }
    fetchVouchers();
  }, [fetchVouchers, router]);

  const filtered =
    filter === 'ALL'
      ? timeline?.vouchers ?? []
      : (timeline?.vouchers ?? []).filter((v) => v.status === filter);

  const sorted = [...filtered].sort((a, b) => a.sequenceOrder - b.sequenceOrder);

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-green-600 mx-auto mb-4" />
          <p className="text-gray-500 text-sm">Loading vouchers...</p>
        </div>
      </div>
    );
  }

  if (!timeline) return null;

  const { summary } = timeline;
  const redemptionPct =
    summary.totalVouchers > 0
      ? Math.round((summary.redeemedCount / summary.totalVouchers) * 100)
      : 0;

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />
      <div className="container mx-auto px-6 py-8 max-w-4xl">

        {/* Back */}
        <button
          onClick={() => router.back()}
          className="flex items-center gap-2 text-green-600 text-sm mb-5 hover:underline"
        >
          <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M10 19l-7-7m0 0l7-7m-7 7h18" />
          </svg>
          Back to Investment
        </button>

        {/* Header */}
        <div className="bg-gradient-to-r from-green-700 to-green-900 rounded-2xl p-6 mb-6 text-white">
          <div className="flex justify-between items-start mb-4">
            <div>
              <h1 className="text-2xl font-bold">{timeline.cropType} — Voucher Tracker</h1>
              <p className="text-green-200 text-sm mt-1">
                {timeline.region} · {timeline.seasonName}
              </p>
            </div>
            <div className="text-right">
              <p className="text-green-200 text-xs">Redemption Progress</p>
              <p className="text-3xl font-bold">{redemptionPct}%</p>
            </div>
          </div>

          {/* Progress bar */}
          <div className="w-full bg-white/20 rounded-full h-2 mb-4">
            <div
              className="bg-white h-2 rounded-full transition-all duration-500"
              style={{ width: `${redemptionPct}%` }}
            />
          </div>

          {/* Summary metrics */}
          <div className="grid grid-cols-4 gap-3">
            {[
              { label: 'Total', value: summary.totalVouchers, color: 'text-white' },
              { label: 'Active', value: summary.activeCount, color: 'text-amber-300' },
              { label: 'Redeemed', value: summary.redeemedCount, color: 'text-green-300' },
              { label: 'Locked', value: summary.generatedCount, color: 'text-blue-300' },
            ].map((m) => (
              <div key={m.label} className="bg-white/10 rounded-xl p-3 text-center">
                <p className={`text-xl font-bold ${m.color}`}>{m.value}</p>
                <p className="text-green-200 text-xs">{m.label}</p>
              </div>
            ))}
          </div>
        </div>

        {/* ETB Summary */}
        <div className="grid grid-cols-3 gap-4 mb-6">
          <div className="bg-white rounded-xl shadow p-4">
            <p className="text-xs text-gray-500 mb-1">Total Value</p>
            <p className="text-lg font-bold text-gray-800">
              {summary.totalValueEtb.toLocaleString()} ETB
            </p>
          </div>
          <div className="bg-white rounded-xl shadow p-4">
            <p className="text-xs text-gray-500 mb-1">Redeemed</p>
            <p className="text-lg font-bold text-green-600">
              {summary.redeemedValueEtb.toLocaleString()} ETB
            </p>
          </div>
          <div className="bg-white rounded-xl shadow p-4">
            <p className="text-xs text-gray-500 mb-1">Pending</p>
            <p className="text-lg font-bold text-amber-600">
              {summary.pendingValueEtb.toLocaleString()} ETB
            </p>
          </div>
        </div>

        {/* Sequence Bar */}
        <div className="bg-white rounded-xl shadow p-5 mb-6">
          <h3 className="text-sm font-semibold text-gray-700 mb-4">
            Redemption Sequence
          </h3>
          <VoucherSequenceBar vouchers={timeline.vouchers} />
          <p className="text-xs text-gray-400 mt-3">
            🔒 Locked vouchers unlock automatically when the preceding one is redeemed
          </p>
        </div>

        {/* Filter tabs */}
        <div className="flex gap-2 mb-4 overflow-x-auto">
          {STATUS_FILTERS.map((f) => (
            <button
              key={f.value}
              onClick={() => setFilter(f.value)}
              className={`px-4 py-1.5 rounded-full text-sm font-medium whitespace-nowrap transition ${
                filter === f.value
                  ? 'bg-green-600 text-white'
                  : 'bg-white text-gray-600 border border-gray-200 hover:border-green-400'
              }`}
            >
              {f.label}
              {f.value !== 'ALL' && (
                <span className="ml-1.5 opacity-70">
                  ({(timeline?.vouchers ?? []).filter((v) =>
                    v.status === f.value).length})
                </span>
              )}
            </button>
          ))}
        </div>

        {/* Voucher list */}
        {sorted.length === 0 ? (
          <div className="bg-white rounded-xl shadow p-12 text-center">
            <p className="text-4xl mb-3">🎟️</p>
            <p className="text-gray-500">No vouchers in this category</p>
          </div>
        ) : (
          <div className="space-y-3">
            {sorted.map((v) => (
              <div
                key={v.id}
                onClick={() => setSelectedVoucher(selectedVoucher?.id === v.id ? null : v)}
                className={`bg-white rounded-xl shadow cursor-pointer transition-all border-l-4 ${
                  v.status === 'REDEEMED'
                    ? 'border-green-500'
                    : v.status === 'ACTIVE'
                    ? 'border-amber-400'
                    : v.status === 'EXPIRED'
                    ? 'border-red-400'
                    : 'border-gray-200'
                } ${selectedVoucher?.id === v.id ? 'ring-2 ring-green-400' : ''}`}
              >
                {/* Card header */}
                <div className="p-4">
                  <div className="flex items-start justify-between gap-3">
                    <div className="flex items-start gap-3">
                      <div className="text-2xl mt-0.5">
                        {{ SEED: '🌾', FERTILIZER: '🪣', PESTICIDE: '🛡️', TOOL: '🔧', OTHER: '📦' }[v.productCategory] ?? '📦'}
                      </div>
                      <div>
                        <p className="font-semibold text-gray-800 text-sm">{v.productDescription}</p>
                        <div className="flex items-center gap-2 mt-1">
                          <VoucherCategoryBadge category={v.productCategory} />
                          <span className="text-xs text-gray-400">#{v.sequenceOrder}</span>
                        </div>
                      </div>
                    </div>
                    <div className="flex flex-col items-end gap-1.5">
                      <VoucherStatusBadge status={v.status} />
                      <span className="font-bold text-gray-900 text-sm font-mono">
                        {v.amountEtb.toLocaleString()} ETB
                      </span>
                    </div>
                  </div>

                  <div className="flex items-center justify-between mt-3 pt-3 border-t border-gray-100">
                    <span className="text-xs text-gray-400 font-mono">{v.alphanumericCode}</span>
                    <span className="text-xs text-gray-400">
                      {v.status === 'REDEEMED' && v.redeemedAt
                        ? `✓ Redeemed ${new Date(v.redeemedAt).toLocaleDateString()}`
                        : `Exp. ${new Date(v.validUntil).toLocaleDateString()}`}
                    </span>
                  </div>
                </div>

                {/* Expanded detail */}
                {selectedVoucher?.id === v.id && (
                  <div className="border-t border-gray-100 px-4 py-4 bg-gray-50 rounded-b-xl">
                    <h4 className="text-xs font-semibold text-gray-500 uppercase tracking-wide mb-3">
                      Voucher Detail
                    </h4>
                    <div className="grid grid-cols-2 gap-x-6 gap-y-2 text-sm">
                      <div>
                        <p className="text-xs text-gray-400">Voucher ID</p>
                        <p className="font-mono text-xs text-gray-700 break-all">{v.id}</p>
                      </div>
                      <div>
                        <p className="text-xs text-gray-400">Sequence</p>
                        <p className="font-medium">{v.sequenceOrder} of {timeline.vouchers.length}</p>
                      </div>
                      <div>
                        <p className="text-xs text-gray-400">Valid Until</p>
                        <p className="font-medium">{new Date(v.validUntil).toLocaleString()}</p>
                      </div>
                      {v.status === 'REDEEMED' && v.redeemedAt && (
                        <div>
                          <p className="text-xs text-gray-400">Redeemed At</p>
                          <p className="font-medium">{new Date(v.redeemedAt).toLocaleString()}</p>
                        </div>
                      )}
                      <div>
                        <p className="text-xs text-gray-400">Created</p>
                        <p className="font-medium">{new Date(v.createdAt).toLocaleDateString()}</p>
                      </div>
                    </div>

                    {v.status === 'REDEEMED' && (
                      <div className="mt-3 p-3 bg-green-50 border border-green-200 rounded-lg">
                        <p className="text-xs text-green-700 font-semibold">
                          ✅ Input delivered — escrow payment released to merchant
                        </p>
                      </div>
                    )}
                    {v.status === 'ACTIVE' && (
                      <div className="mt-3 p-3 bg-amber-50 border border-amber-200 rounded-lg">
                        <p className="text-xs text-amber-700 font-semibold">
                          🟡 Awaiting farmer to present this voucher at a certified merchant
                        </p>
                      </div>
                    )}
                    {v.status === 'GENERATED' && (
                      <div className="mt-3 p-3 bg-blue-50 border border-blue-200 rounded-lg">
                        <p className="text-xs text-blue-700 font-semibold">
                          🔒 Locked until voucher #{v.sequenceOrder - 1} is redeemed
                        </p>
                      </div>
                    )}
                    {v.status === 'EXPIRED' && (
                      <div className="mt-3 p-3 bg-red-50 border border-red-200 rounded-lg">
                        <p className="text-xs text-red-700 font-semibold">
                          ⚠️ Expired unused — funds returned to escrow for investor refund
                        </p>
                      </div>
                    )}
                  </div>
                )}
              </div>
            ))}
          </div>
        )}

        {/* Escrow note */}
        <div className="mt-6 bg-white border border-yellow-200 rounded-xl p-4 flex gap-3">
          <span className="text-xl">🔐</span>
          <div>
            <p className="text-sm font-semibold text-gray-800">How your money is protected</p>
            <p className="text-xs text-gray-500 mt-1">
              Each voucher release triggers a cryptographic 6-check validation. Funds are only
              transferred to the merchant after all checks pass — including GPS proximity,
              category match, and signature verification. Unredeemed expired vouchers are
              automatically refunded to investors.
            </p>
          </div>
        </div>

      </div>
    </div>
  );
}

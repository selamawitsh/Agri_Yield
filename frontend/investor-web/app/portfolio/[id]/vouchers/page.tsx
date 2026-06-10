'use client';

import { useEffect, useState, useCallback } from 'react';
import { useRouter, useParams } from 'next/navigation';
import toast from 'react-hot-toast';
import api from '@/lib/api';
import Navbar from '@/components/Navbar';
import VoucherStatusBadge from '@/components/VoucherStatusBadge';
import VoucherCategoryBadge from '@/components/VoucherCategoryBadge';
import VoucherSequenceBar from '@/components/VoucherSequenceBar';

// ── Real shape from VoucherResponse.java ─────────────────────────────────────
interface VoucherItem {
  id: string;
  voucherCode: string;
  investmentId: string;
  farmId: string;
  farmerId: string;
  merchantId: string | null;
  inputNeedId: string;
  cropCycleId: string;
  productName: string;
  productCategory: string;   // "SEED" | "FERTILIZER" | "PESTICIDE" | "TOOL" | "OTHER"
  amountEtb: number;
  status: string;            // "GENERATED" | "ACTIVE" | "REDEEMED" | "EXPIRED" | "CANCELLED"
  issuedAt: string | null;
  redeemedAt: string | null;
  expiresAt: string | null;
  createdAt: string;
}

const CATEGORY_ICON: Record<string, string> = {
  SEED: '🌾', FERTILIZER: '🪣', PESTICIDE: '🛡️', TOOL: '🔧', OTHER: '📦',
};

const STATUS_FILTERS = [
  { label: 'All', value: 'ALL' },
  { label: 'Active', value: 'ACTIVE' },
  { label: 'Redeemed', value: 'REDEEMED' },
  { label: 'Locked', value: 'GENERATED' },
  { label: 'Expired', value: 'EXPIRED' },
  { label: 'Cancelled', value: 'CANCELLED' },
];

export default function InvestmentVouchersPage() {
  const router = useRouter();
  const params = useParams();
  const investmentId = params.id as string;

  const [vouchers, setVouchers] = useState<VoucherItem[]>([]);
  const [farmId, setFarmId] = useState<string | null>(null);
  const [cropType, setCropType] = useState('');
  const [region, setRegion] = useState('');
  const [seasonName, setSeasonName] = useState('');
  const [loading, setLoading] = useState(true);
  const [filter, setFilter] = useState('ALL');
  const [expandedId, setExpandedId] = useState<string | null>(null);

  const fetchVouchers = useCallback(async () => {
    try {
      // Step 1 — get investment to find farmId
      const invRes = await api.get(`/portfolio/${investmentId}`);
      if (!invRes.data.success) throw new Error('Investment not found');
      const inv = invRes.data.data;
      const fId: string = inv.farmId;
      setFarmId(fId);
      setCropType(inv.cropType || '');
      setRegion(inv.region || '');
      setSeasonName(inv.seasonName || '');

      // Step 2 — GET /vouchers/farm/{farmId} → returns List<VoucherResponse>
      const vRes = await api.get(`/vouchers/farm/${fId}`);
      if (vRes.data.success) {
        setVouchers(vRes.data.data || []);
      }
    } catch (err: any) {
      toast.error(err.response?.data?.message || 'Failed to load vouchers');
      router.push(`/portfolio/${investmentId}`);
    } finally {
      setLoading(false);
    }
  }, [investmentId, router]);

  useEffect(() => {
    const token = localStorage.getItem('access_token');
    if (!token) { router.push('/login'); return; }
    fetchVouchers();
  }, [fetchVouchers]);

  // ── Derived summary from the flat list ───────────────────────────────────
  const total       = vouchers.length;
  const active      = vouchers.filter(v => v.status === 'ACTIVE').length;
  const redeemed    = vouchers.filter(v => v.status === 'REDEEMED').length;
  const generated   = vouchers.filter(v => v.status === 'GENERATED').length;
  const expired     = vouchers.filter(v => v.status === 'EXPIRED').length;
  const totalValue  = vouchers.reduce((s, v) => s + v.amountEtb, 0);
  const redeemedVal = vouchers.filter(v => v.status === 'REDEEMED').reduce((s, v) => s + v.amountEtb, 0);
  const pendingVal  = vouchers.filter(v => ['ACTIVE', 'GENERATED'].includes(v.status)).reduce((s, v) => s + v.amountEtb, 0);
  const redemptionPct = total > 0 ? Math.round((redeemed / total) * 100) : 0;

  const filtered = (filter === 'ALL' ? vouchers : vouchers.filter(v => v.status === filter))
    .sort((a, b) => {
      // sort by sequence embedded in productName if available, otherwise by createdAt
      return new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime();
    });

  // Build a VoucherSequenceBar-compatible shape
  const seqVouchers = vouchers.map((v, i) => ({
    id: v.id,
    sequenceOrder: i + 1,
    status: v.status as any,
    amountEtb: v.amountEtb,
    productCategory: v.productCategory as any,
    productDescription: v.productName,
    alphanumericCode: v.voucherCode,
    validUntil: v.expiresAt || '',
    redeemedAt: v.redeemedAt,
    redeemedMerchantId: v.merchantId,
    farmId: v.farmId,
    cropCycleId: v.cropCycleId,
    inputNeedItemId: v.inputNeedId,
    createdAt: v.createdAt,
  }));

  if (loading) return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50">
      <div className="text-center">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-green-600 mx-auto mb-4" />
        <p className="text-gray-500 text-sm">Loading vouchers...</p>
      </div>
    </div>
  );

  return (
    <div className="min-h-screen bg-gray-50 pb-12">
      <Navbar />
      <div className="container mx-auto px-4 sm:px-6 py-6 max-w-4xl">

        <button onClick={() => router.back()}
          className="flex items-center gap-2 text-green-600 text-sm mb-5 font-semibold hover:underline">
          ← Back to Investment
        </button>

        {/* Header */}
        <div className="bg-gradient-to-r from-green-700 to-emerald-900 rounded-2xl p-6 mb-6 text-white">
          <div className="flex justify-between items-start mb-4">
            <div>
              <h1 className="text-2xl font-bold">{cropType} — Voucher Tracker</h1>
              <p className="text-green-200 text-sm mt-1">{region} · {seasonName}</p>
            </div>
            <div className="text-right">
              <p className="text-green-200 text-xs">Redemption Progress</p>
              <p className="text-3xl font-bold">{redemptionPct}%</p>
            </div>
          </div>

          <div className="w-full bg-white/20 rounded-full h-2 mb-5">
            <div className="bg-white h-2 rounded-full transition-all duration-700"
              style={{ width: `${redemptionPct}%` }} />
          </div>

          <div className="grid grid-cols-4 gap-3">
            {[
              { label: 'Total',    value: total,    color: 'text-white' },
              { label: 'Active',   value: active,   color: 'text-amber-300' },
              { label: 'Redeemed',value: redeemed,  color: 'text-green-300' },
              { label: 'Locked',   value: generated, color: 'text-blue-300' },
            ].map(m => (
              <div key={m.label} className="bg-white/10 rounded-xl p-3 text-center">
                <p className={`text-xl font-bold ${m.color}`}>{m.value}</p>
                <p className="text-green-200 text-xs">{m.label}</p>
              </div>
            ))}
          </div>
        </div>

        {/* ETB Summary */}
        <div className="grid grid-cols-3 gap-4 mb-6">
          {[
            { label: 'Total Value',   value: totalValue,  color: 'text-gray-800' },
            { label: 'Redeemed',      value: redeemedVal, color: 'text-green-600' },
            { label: 'Pending',       value: pendingVal,  color: 'text-amber-600' },
          ].map(s => (
            <div key={s.label} className="bg-white rounded-2xl shadow-sm border border-gray-100 p-4">
              <p className="text-xs text-gray-400 font-medium mb-1">{s.label}</p>
              <p className={`text-lg font-bold ${s.color}`}>{s.value.toLocaleString()} ETB</p>
            </div>
          ))}
        </div>

        {/* Sequence bar */}
        {seqVouchers.length > 0 && (
          <div className="bg-white rounded-2xl shadow-sm border border-gray-100 p-5 mb-6">
            <h3 className="text-sm font-bold text-gray-700 mb-4">Redemption Sequence</h3>
            <VoucherSequenceBar vouchers={seqVouchers} />
            <p className="text-xs text-gray-400 mt-3">
              🔒 Locked vouchers unlock automatically when the preceding one is redeemed
            </p>
          </div>
        )}

        {/* Filter tabs */}
        <div className="flex gap-2 mb-4 overflow-x-auto">
          {STATUS_FILTERS.map(f => (
            <button key={f.value} onClick={() => setFilter(f.value)}
              className={`px-4 py-1.5 rounded-full text-sm font-semibold whitespace-nowrap transition ${
                filter === f.value
                  ? 'bg-green-600 text-white shadow-sm'
                  : 'bg-white text-gray-600 border border-gray-200 hover:border-green-400'
              }`}>
              {f.label}
              {f.value !== 'ALL' && (
                <span className="ml-1.5 opacity-60">
                  ({vouchers.filter(v => v.status === f.value).length})
                </span>
              )}
            </button>
          ))}
        </div>

        {/* Voucher list */}
        {filtered.length === 0 ? (
          <div className="bg-white rounded-2xl shadow-sm border border-gray-100 p-12 text-center">
            <p className="text-4xl mb-3">🎟️</p>
            <p className="text-gray-500 font-medium">No vouchers in this category</p>
          </div>
        ) : (
          <div className="space-y-3">
            {filtered.map((v, idx) => {
              const isExpanded = expandedId === v.id;
              const borderColor =
                v.status === 'REDEEMED'   ? 'border-l-green-500' :
                v.status === 'ACTIVE'     ? 'border-l-amber-400' :
                v.status === 'EXPIRED'    ? 'border-l-red-400'   :
                v.status === 'CANCELLED'  ? 'border-l-red-300'   :
                'border-l-gray-200';

              return (
                <div key={v.id}
                  onClick={() => setExpandedId(isExpanded ? null : v.id)}
                  className={`bg-white rounded-2xl shadow-sm border border-gray-100 border-l-4 ${borderColor} cursor-pointer transition-all ${isExpanded ? 'ring-2 ring-green-400' : 'hover:shadow-md'}`}>

                  <div className="p-4">
                    <div className="flex items-start justify-between gap-3">
                      <div className="flex items-start gap-3">
                        <div className="text-2xl mt-0.5 select-none">
                          {CATEGORY_ICON[v.productCategory] ?? '📦'}
                        </div>
                        <div>
                          <p className="font-semibold text-gray-800 text-sm">{v.productName}</p>
                          <div className="flex items-center gap-2 mt-1 flex-wrap">
                            <VoucherCategoryBadge category={v.productCategory as any} />
                            <span className="text-xs text-gray-400 font-mono">{v.voucherCode}</span>
                          </div>
                        </div>
                      </div>
                      <div className="flex flex-col items-end gap-1.5 shrink-0">
                        <VoucherStatusBadge status={v.status as any} />
                        <span className="font-bold text-gray-800 text-sm">
                          {v.amountEtb.toLocaleString()} ETB
                        </span>
                      </div>
                    </div>

                    <div className="flex items-center justify-between mt-3 pt-3 border-t border-gray-100 text-xs text-gray-400">
                      <span>
                        {v.status === 'REDEEMED' && v.redeemedAt
                          ? `✓ Redeemed ${new Date(v.redeemedAt).toLocaleDateString()}`
                          : v.expiresAt
                            ? `Expires ${new Date(v.expiresAt).toLocaleDateString()}`
                            : 'No expiry set'}
                      </span>
                      <span className="text-green-600 font-medium">{isExpanded ? '▲ less' : '▼ details'}</span>
                    </div>
                  </div>

                  {/* Expanded detail */}
                  {isExpanded && (
                    <div className="border-t border-gray-100 px-4 py-4 bg-gray-50 rounded-b-2xl">
                      <div className="grid grid-cols-2 gap-x-6 gap-y-3 text-sm mb-4">
                        <div>
                          <p className="text-xs text-gray-400 font-medium">Voucher ID</p>
                          <p className="font-mono text-xs text-gray-700 break-all mt-0.5">{v.id}</p>
                        </div>
                        <div>
                          <p className="text-xs text-gray-400 font-medium">Category</p>
                          <p className="font-semibold text-gray-800 mt-0.5">{v.productCategory}</p>
                        </div>
                        <div>
                          <p className="text-xs text-gray-400 font-medium">Issued At</p>
                          <p className="font-medium text-gray-700 mt-0.5">
                            {v.issuedAt ? new Date(v.issuedAt).toLocaleString() : '—'}
                          </p>
                        </div>
                        <div>
                          <p className="text-xs text-gray-400 font-medium">Expires At</p>
                          <p className="font-medium text-gray-700 mt-0.5">
                            {v.expiresAt ? new Date(v.expiresAt).toLocaleString() : '—'}
                          </p>
                        </div>
                        {v.redeemedAt && (
                          <div>
                            <p className="text-xs text-gray-400 font-medium">Redeemed At</p>
                            <p className="font-medium text-gray-700 mt-0.5">{new Date(v.redeemedAt).toLocaleString()}</p>
                          </div>
                        )}
                        {v.merchantId && (
                          <div>
                            <p className="text-xs text-gray-400 font-medium">Merchant ID</p>
                            <p className="font-mono text-xs text-gray-700 mt-0.5">{v.merchantId.slice(0, 8)}…</p>
                          </div>
                        )}
                      </div>

                      {/* Status-specific callout */}
                      {v.status === 'REDEEMED' && (
                        <div className="p-3 bg-green-50 border border-green-200 rounded-xl text-xs text-green-700 font-semibold">
                          ✅ Input delivered to farmer — escrow payment released to merchant
                        </div>
                      )}
                      {v.status === 'ACTIVE' && (
                        <div className="p-3 bg-amber-50 border border-amber-200 rounded-xl text-xs text-amber-700 font-semibold">
                          🟡 Awaiting farmer to present this voucher at a certified merchant
                        </div>
                      )}
                      {v.status === 'GENERATED' && (
                        <div className="p-3 bg-blue-50 border border-blue-200 rounded-xl text-xs text-blue-700 font-semibold">
                          🔒 Locked — unlocks after the preceding voucher is redeemed
                        </div>
                      )}
                      {v.status === 'EXPIRED' && (
                        <div className="p-3 bg-red-50 border border-red-200 rounded-xl text-xs text-red-700 font-semibold">
                          ⚠️ Expired without redemption — funds returned to escrow for investor refund
                        </div>
                      )}
                      {v.status === 'CANCELLED' && (
                        <div className="p-3 bg-gray-100 border border-gray-200 rounded-xl text-xs text-gray-600 font-semibold">
                          ✕ Cancelled by admin
                        </div>
                      )}
                    </div>
                  )}
                </div>
              );
            })}
          </div>
        )}

        {/* Escrow protection note */}
        <div className="mt-6 bg-white border border-amber-200 rounded-2xl p-4 flex gap-3">
          <span className="text-xl">🔐</span>
          <div>
            <p className="text-sm font-bold text-gray-800">How your money is protected</p>
            <p className="text-xs text-gray-500 mt-1">
              Each voucher release triggers a 6-check cryptographic validation: signature, duplicate detection,
              merchant category match, GPS proximity (&lt;50km), validity period, and sequential unlock order.
              Funds are only transferred to the merchant after all checks pass. Expired unredeemed vouchers
              are automatically refunded to investors.
            </p>
          </div>
        </div>

      </div>
    </div>
  );
}

'use client';

import { useEffect, useState, useCallback } from 'react';
import { useRouter, useParams } from 'next/navigation';
import toast from 'react-hot-toast';
import { motion, AnimatePresence } from 'framer-motion';
import {
  ChevronLeft,
  ChevronDown,
  ChevronUp,
  Lock,
  CheckCircle2,
  Clock,
  AlertCircle,
  XCircle,
  Sprout,
  Container,
  ShieldCheck,
  Wrench,
  Package,
  Ticket
} from 'lucide-react';
import api from '@/lib/api';
import Navbar from '@/components/Navbar';
import VoucherStatusBadge from '@/components/VoucherStatusBadge';
import VoucherCategoryBadge from '@/components/VoucherCategoryBadge';
import VoucherSequenceBar from '@/components/VoucherSequenceBar';

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

const STATUS_FILTERS = [
  { label: 'All', value: 'ALL' },
  { label: 'Active', value: 'ACTIVE' },
  { label: 'Redeemed', value: 'REDEEMED' },
  { label: 'Locked', value: 'GENERATED' },
  { label: 'Expired', value: 'EXPIRED' },
  { label: 'Cancelled', value: 'CANCELLED' },
];

const pageFadeIn = {
  hidden: { opacity: 0, y: 15 },
  show: { opacity: 1, y: 0, transition: { duration: 0.4, ease: 'easeOut', staggerChildren: 0.06 } }
};

const blockVariants = {
  hidden: { opacity: 0, y: 15 },
  show: { opacity: 1, y: 0, transition: { type: 'spring', stiffness: 100, damping: 15 } }
};

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
      const invRes = await api.get(`/portfolio/${investmentId}`);
      if (!invRes.data.success) throw new Error('Investment not found');
      const inv = invRes.data.data;
      const fId: string = inv.farmId;
      setFarmId(fId);
      setCropType(inv.cropType || '');
      setRegion(inv.region || '');
      setSeasonName(inv.seasonName || '');

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

  const total = vouchers.length;
  const active = vouchers.filter(v => v.status === 'ACTIVE').length;
  const redeemed = vouchers.filter(v => v.status === 'REDEEMED').length;
  const generated = vouchers.filter(v => v.status === 'GENERATED').length;
  const totalValue = vouchers.reduce((s, v) => s + v.amountEtb, 0);
  const redeemedVal = vouchers.filter(v => v.status === 'REDEEMED').reduce((s, v) => s + v.amountEtb, 0);
  const pendingVal = vouchers.filter(v => ['ACTIVE', 'GENERATED'].includes(v.status)).reduce((s, v) => s + v.amountEtb, 0);
  const redemptionPct = total > 0 ? Math.round((redeemed / total) * 100) : 0;

  const filtered = (filter === 'ALL' ? vouchers : vouchers.filter(v => v.status === filter))
      .sort((a, b) => new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime());

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

  const getCategoryIcon = (category: string) => {
    switch (category) {
      case 'SEED': return <Sprout className="w-5 h-5 text-emerald-700" />;
      case 'FERTILIZER': return <Container className="w-5 h-5 text-emerald-700" />;
      case 'PESTICIDE': return <ShieldCheck className="w-5 h-5 text-emerald-700" />;
      case 'TOOL': return <Wrench className="w-5 h-5 text-emerald-700" />;
      default: return <Package className="w-5 h-5 text-emerald-700" />;
    }
  };

  if (loading) return (
      <div className="min-h-screen flex flex-col items-center justify-center bg-gradient-to-br from-[#f8fafc] to-[#f1f5f9] space-y-4">
        <motion.div
            animate={{ rotate: 360 }}
            transition={{ repeat: Infinity, duration: 1, ease: "linear" }}
            className="rounded-full h-12 w-12 border-4 border-slate-200 border-t-emerald-600"
        />
        <motion.p
            initial={{ opacity: 0 }}
            animate={{ opacity: [0.4, 1, 0.4] }}
            transition={{ repeat: Infinity, duration: 1.5 }}
            className="text-slate-500 text-xs font-bold tracking-widest uppercase font-sans"
        >
          Loading voucher configuration...
        </motion.p>
      </div>
  );

  return (
      <div className="min-h-screen bg-gradient-to-br from-[#f8fafc] to-[#f1f5f9] text-slate-800 font-sans antialiased pb-16">
        <Navbar />

        <motion.div
            variants={pageFadeIn}
            initial="hidden"
            animate="show"
            className="container mx-auto px-4 sm:px-6 py-8 max-w-4xl space-y-6"
        >
          <motion.button
              whileHover={{ x: -2 }}
              onClick={() => router.back()}
              className="flex items-center gap-1.5 text-emerald-700 text-xs uppercase tracking-wider font-bold hover:text-emerald-800 transition-colors"
          >
            <ChevronLeft className="w-4 h-4" /> Back to Investment
          </motion.button>

          <motion.div variants={blockVariants} className="bg-white rounded-3xl shadow-xs border border-slate-200/60 overflow-hidden">
            <div className="bg-gradient-to-r from-slate-900 via-slate-800 to-slate-900 p-6 sm:p-8 text-white">
              <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
                <div className="space-y-1">
                  <h1 className="text-2xl sm:text-3xl font-black tracking-tight">{cropType} Voucher Tracker</h1>
                  <p className="text-slate-300 text-xs sm:text-sm font-medium">{region} Region &middot; {seasonName}</p>
                </div>
                <div className="text-left sm:text-right shrink-0">
                  <p className="text-slate-400 text-[10px] uppercase font-bold tracking-widest">Redemption Progress</p>
                  <p className="text-3xl font-black tracking-tight mt-0.5 text-emerald-400">{redemptionPct}%</p>
                </div>
              </div>

              <div className="w-full bg-slate-700/60 rounded-full h-1.5 mt-6 mb-2 overflow-hidden">
                <motion.div
                    initial={{ width: 0 }}
                    animate={{ width: `${redemptionPct}%` }}
                    transition={{ duration: 0.8, ease: "easeOut" }}
                    className="bg-emerald-500 h-1.5 rounded-full"
                />
              </div>
            </div>

            <div className="p-6 bg-slate-50/50 border-t border-slate-100">
              <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
                {[
                  { label: 'Total Issued', value: total, color: 'text-slate-800' },
                  { label: 'Active Status', value: active, color: 'text-amber-700' },
                  { label: 'Fully Redeemed', value: redeemed, color: 'text-emerald-800' },
                  { label: 'Remaining Locked', value: generated, color: 'text-blue-700' },
                ].map(m => (
                    <div key={m.label} className="bg-white border border-slate-200/60 rounded-2xl p-3 text-center shadow-2xs">
                      <p className={`text-xl font-black tracking-tight ${m.color}`}>{m.value}</p>
                      <p className="text-slate-400 text-[10px] font-bold uppercase tracking-wider mt-0.5">{m.label}</p>
                    </div>
                ))}
              </div>
            </div>
          </motion.div>

          <motion.div variants={blockVariants} className="grid grid-cols-3 gap-4">
            {[
              { label: 'Total Financial Value', value: totalValue, color: 'text-slate-800 bg-white' },
              { label: 'Settled Component Value', value: redeemedVal, color: 'text-emerald-800 bg-gradient-to-br from-emerald-50/60 to-teal-50/20 border-emerald-100/80 shadow-xs font-black' },
              { label: 'Pending Processing Value', value: pendingVal, color: 'text-amber-700 bg-white' },
            ].map(s => (
                <div key={s.label} className={`rounded-2xl border border-slate-200/60 p-4 ${s.color}`}>
                  <p className="text-[10px] text-slate-400 font-bold uppercase tracking-wider mb-1">{s.label}</p>
                  <p className="text-base font-black tracking-tight">{s.value.toLocaleString()} ETB</p>
                </div>
            ))}
          </motion.div>

          {seqVouchers.length > 0 && (
              <motion.div variants={blockVariants} className="bg-white rounded-3xl shadow-xs border border-slate-200/60 p-6">
                <h3 className="text-xs font-bold uppercase text-slate-400 tracking-wider mb-4">Operational Component Sequence</h3>
                <VoucherSequenceBar vouchers={seqVouchers} />
                <div className="mt-4 flex items-start gap-2 text-xs text-slate-400 font-medium">
                  <Lock className="w-3.5 h-3.5 text-slate-400 shrink-0 mt-0.5" />
                  <span>Downstream component vouchers unlock safely and automatically inside the cycle as soon as the farmer redeems the current step.</span>
                </div>
              </motion.div>
          )}

          <motion.div variants={blockVariants} className="flex gap-2 pb-1 overflow-x-auto no-scrollbar">
            {STATUS_FILTERS.map(f => {
              const count = f.value === 'ALL' ? vouchers.length : vouchers.filter(v => v.status === f.value).length;
              const isSelected = filter === f.value;
              return (
                  <button
                      key={f.value}
                      onClick={() => setFilter(f.value)}
                      className={`px-4 py-1.5 rounded-full text-xs font-bold tracking-wide uppercase whitespace-nowrap transition-all ${
                          isSelected
                              ? 'bg-slate-900 text-white shadow-xs'
                              : 'bg-white text-slate-500 border border-slate-200/70 hover:border-slate-300'
                      }`}
                  >
                    {f.label}
                    <span className="ml-1.5 text-[10px] text-slate-400">
                  ({count})
                </span>
                  </button>
              );
            })}
          </motion.div>

          <motion.div variants={blockVariants} className="space-y-3">
            <AnimatePresence mode="popLayout">
              {filtered.length === 0 ? (
                  <motion.div
                      initial={{ opacity: 0 }}
                      animate={{ opacity: 1 }}
                      exit={{ opacity: 0 }}
                      className="bg-white rounded-3xl shadow-xs border border-slate-200/60 p-12 text-center flex flex-col items-center justify-center space-y-3"
                  >
                    <div className="w-12 h-12 bg-slate-50 rounded-2xl flex items-center justify-center text-slate-400 border border-slate-100">
                      <Ticket className="w-5 h-5" />
                    </div>
                    <p className="text-slate-400 text-xs font-bold uppercase tracking-wider">No corresponding records matching this query view</p>
                  </motion.div>
              ) : (
                  filtered.map((v) => {
                    const isExpanded = expandedId === v.id;
                    const statusBorderColor =
                        v.status === 'REDEEMED'   ? 'border-l-emerald-500' :
                            v.status === 'ACTIVE'     ? 'border-l-amber-500' :
                                v.status === 'EXPIRED'    ? 'border-l-rose-500'   :
                                    v.status === 'CANCELLED'  ? 'border-l-slate-400'  :
                                        'border-l-slate-200';

                    return (
                        <motion.div
                            layout="position"
                            key={v.id}
                            onClick={() => setExpandedId(isExpanded ? null : v.id)}
                            className={`bg-white rounded-2xl shadow-2xs border border-slate-200/60 border-l-4 ${statusBorderColor} cursor-pointer transition-all ${
                                isExpanded ? 'ring-2 ring-emerald-600/20 border-slate-300' : 'hover:shadow-xs'
                            }`}
                        >
                          <div className="p-4 sm:p-5">
                            <div className="flex items-start justify-between gap-4">
                              <div className="flex items-start gap-3.5">
                                <div className="w-10 h-10 bg-slate-50 border border-slate-100 rounded-xl flex items-center justify-center shrink-0">
                                  {getCategoryIcon(v.productCategory)}
                                </div>
                                <div className="space-y-1">
                                  <p className="font-black text-slate-900 tracking-tight text-sm sm:text-base">{v.productName}</p>
                                  <div className="flex items-center gap-2 flex-wrap">
                                    <VoucherCategoryBadge category={v.productCategory as any} />
                                    <span className="text-[11px] text-slate-400 font-mono font-medium tracking-tight bg-slate-50 border border-slate-100 px-1.5 py-0.5 rounded-md">
                                {v.voucherCode}
                              </span>
                                  </div>
                                </div>
                              </div>
                              <div className="flex flex-col items-end gap-2 shrink-0 text-right">
                                <VoucherStatusBadge status={v.status as any} />
                                <span className="font-black text-slate-800 text-sm sm:text-base tracking-tight">
                            {v.amountEtb.toLocaleString()} ETB
                          </span>
                              </div>
                            </div>

                            <div className="flex items-center justify-between mt-4 pt-3 border-t border-slate-100 text-[11px] text-slate-400 font-medium">
                        <span className="font-mono">
                          {v.status === 'REDEEMED' && v.redeemedAt
                              ? `Settled: ${new Date(v.redeemedAt).toLocaleDateString()}`
                              : v.expiresAt
                                  ? `Deadline: ${new Date(v.expiresAt).toLocaleDateString()}`
                                  : 'No deadline constraints set'}
                        </span>
                              <span className="text-emerald-700 font-bold uppercase tracking-wider flex items-center gap-0.5">
                          {isExpanded ? 'Hide Ledger' : 'View Ledger'}
                                {isExpanded ? <ChevronUp className="w-3.5 h-3.5" /> : <ChevronDown className="w-3.5 h-3.5" />}
                        </span>
                            </div>
                          </div>

                          <AnimatePresence>
                            {isExpanded && (
                                <motion.div
                                    initial={{ opacity: 0, height: 0 }}
                                    animate={{ opacity: 1, height: 'auto' }}
                                    exit={{ opacity: 0, height: 0 }}
                                    className="border-t border-slate-100 bg-slate-50/50 rounded-b-2xl overflow-hidden"
                                >
                                  <div className="p-4 sm:p-5 grid grid-cols-1 sm:grid-cols-2 gap-x-6 gap-y-4 text-xs border-b border-slate-100">
                                    <div>
                                      <p className="text-[10px] text-slate-400 font-bold uppercase tracking-wider">Cryptographic Reference UUID</p>
                                      <p className="font-mono text-slate-600 break-all mt-1 bg-white p-2 rounded-lg border border-slate-200/50">{v.id}</p>
                                    </div>
                                    <div>
                                      <p className="text-[10px] text-slate-400 font-bold uppercase tracking-wider">Operational Target Division</p>
                                      <p className="font-semibold text-slate-700 mt-1 bg-white p-2 rounded-lg border border-slate-200/50">{v.productCategory}</p>
                                    </div>
                                    <div>
                                      <p className="text-[10px] text-slate-400 font-bold uppercase tracking-wider">Allocation Genesis Timestamp</p>
                                      <p className="font-medium text-slate-600 mt-1 bg-white p-2 rounded-lg border border-slate-200/50">
                                        {v.issuedAt ? new Date(v.issuedAt).toLocaleString() : 'Pending Queue Allocation'}
                                      </p>
                                    </div>
                                    <div>
                                      <p className="text-[10px] text-slate-400 font-bold uppercase tracking-wider">Cycle Void Expiration Window</p>
                                      <p className="font-medium text-slate-600 mt-1 bg-white p-2 rounded-lg border border-slate-200/50">
                                        {v.expiresAt ? new Date(v.expiresAt).toLocaleString() : 'Unrestricted Open Timeline'}
                                      </p>
                                    </div>
                                    {v.redeemedAt && (
                                        <div>
                                          <p className="text-[10px] text-slate-400 font-bold uppercase tracking-wider">Physical Validation Release</p>
                                          <p className="font-medium text-slate-600 mt-1 bg-white p-2 rounded-lg border border-slate-200/50">{new Date(v.redeemedAt).toLocaleString()}</p>
                                        </div>
                                    )}
                                    {v.merchantId && (
                                        <div>
                                          <p className="text-[10px] text-slate-400 font-bold uppercase tracking-wider">Authorized Merchant Network Profile</p>
                                          <p className="font-mono text-slate-600 mt-1 bg-white p-2 rounded-lg border border-slate-200/50">{v.merchantId}</p>
                                        </div>
                                    )}
                                  </div>

                                  <div className="p-4 bg-white">
                                    {v.status === 'REDEEMED' && (
                                        <div className="p-3 bg-emerald-50/60 border border-emerald-100/80 rounded-xl text-xs text-emerald-800 font-medium flex items-start gap-2">
                                          <CheckCircle2 className="w-4 h-4 text-emerald-600 shrink-0 mt-0.5" />
                                          <span>Physical inputs verified as delivered to the farm. Escrow processing completed and balance liquidity disbursed to the supplying merchant entity.</span>
                                        </div>
                                    )}
                                    {v.status === 'ACTIVE' && (
                                        <div className="p-3 bg-amber-50/60 border border-amber-100/80 rounded-xl text-xs text-amber-800 font-medium flex items-start gap-2">
                                          <Clock className="w-4 h-4 text-amber-600 shrink-0 mt-0.5" />
                                          <span>Awaiting deployment. Farmer is cleared to present this voucher package directly to certified network distribution points inside the operational range.</span>
                                        </div>
                                    )}
                                    {v.status === 'GENERATED' && (
                                        <div className="p-3 bg-blue-50/60 border border-blue-100/80 rounded-xl text-xs text-blue-800 font-medium flex items-start gap-2">
                                          <Lock className="w-4 h-4 text-blue-600 shrink-0 mt-0.5" />
                                          <span>Sequentially locked state. Balance funds are maintained inside the protected pool escrow framework pending verification completion of preceding cultivation stages.</span>
                                        </div>
                                    )}
                                    {v.status === 'EXPIRED' && (
                                        <div className="p-3 bg-rose-50/60 border border-rose-100/80 rounded-xl text-xs text-rose-800 font-medium flex items-start gap-2">
                                          <AlertCircle className="w-4 h-4 text-rose-600 shrink-0 mt-0.5" />
                                          <span>Voucher time constraints breached without redemption check. Allocation dropped, token voided, and capital returned to primary pool assets.</span>
                                        </div>
                                    )}
                                    {v.status === 'CANCELLED' && (
                                        <div className="p-3 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-600 font-medium flex items-start gap-2">
                                          <XCircle className="w-4 h-4 text-slate-500 shrink-0 mt-0.5" />
                                          <span>Operational sequence canceled by system administrative protocol override.</span>
                                        </div>
                                    )}
                                  </div>
                                </motion.div>
                            )}
                          </AnimatePresence>
                        </motion.div>
                    );
                  })
              )}
            </AnimatePresence>
          </motion.div>

          <motion.div variants={blockVariants} className="bg-white border border-slate-200/60 rounded-3xl p-5 flex items-start gap-4">
            <div className="w-10 h-10 bg-amber-50 border border-amber-100 rounded-xl flex items-center justify-center text-amber-700 shrink-0">
              <Lock className="w-5 h-5" />
            </div>
            <div className="space-y-1">
              <p className="text-sm font-black text-slate-900 tracking-tight">Automated Protection Framework</p>
              <p className="text-xs text-slate-400 font-medium leading-relaxed">
                Every specific item release executes a strict validation check: matching digital cryptographic signatures, duplicate deployment avoidance, strict merchant channel bounds check, structural sequence integrity protection, and GPS proximity checks. Principal funds transfer cleanly into supply chain vendor accounts only after validation passes completely. Voided or unredeemed balances revert safely to investor allocations.
              </p>
            </div>
          </motion.div>

        </motion.div>
      </div>
  );
}
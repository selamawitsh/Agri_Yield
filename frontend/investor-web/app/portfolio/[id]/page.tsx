'use client';

import { useEffect, useState, useCallback } from 'react';
import { useRouter, useParams } from 'next/navigation';
import Link from 'next/link';
import dynamic from 'next/dynamic';
import toast from 'react-hot-toast';
import { motion, AnimatePresence } from 'framer-motion';
import { ChevronLeft, ChevronRight, ArrowUpRight, Lock, Ticket, HelpCircle } from 'lucide-react';
import api from '@/lib/api';
import Navbar from '@/components/Navbar';
import StatusBadge from '@/components/StatusBadge';
import EscrowStateMachine from '@/components/EscrowStateMachine';
import NdviChart from '@/components/NdviChart';
import AprHistoryChart from '@/components/AprHistoryChart';
import { Investment, PayoutRecord } from '@/lib/types';
import SatelliteImageViewComponent from '@/components/SatelliteImageView';

const FarmMap = dynamic(() => import('@/components/FarmMap'), { ssr: false });

interface NdviPoint {
  ndviValue: number;
  cloudCoverage: number;
  healthStatus: string;
  sentinelSceneId: string;
  recordedDate: string;
}

interface YieldPred {
  predictedYieldMin: number;
  predictedYieldMax: number;
  predictedYieldMean: number;
  confidencePct: number;
  weeksToHarvest: number;
}

interface FarmMapData {
  centroidLat: number;
  centroidLng: number;
  areaHectares: number;
  latestNdvi?: { ndviValue: number; healthStatus: string; cloudCoverage: number };
}

const REGION_COORDS: Record<string, [number, number]> = {
  'Oromia': [8.5, 39.3], 'Amhara': [11.5, 38.0], 'SNNPR': [6.8, 37.5],
  'Tigray': [14.0, 38.5], 'Somali': [7.5, 44.0], 'Afar': [11.8, 41.5],
};

// Animation settings
const pageFadeIn = {
  hidden: { opacity: 0, y: 15 },
  show: { opacity: 1, y: 0, transition: { duration: 0.4, ease: 'easeOut', staggerChildren: 0.08 } }
};

const blockVariants = {
  hidden: { opacity: 0, y: 20 },
  show: { opacity: 1, y: 0, transition: { type: 'spring', stiffness: 90, damping: 14 } }
};

export default function InvestmentDetailPage() {
  const router = useRouter();
  const params = useParams();
  const investmentId = params.id as string;

  const [investment, setInvestment] = useState<Investment | null>(null);
  const [ndviHistory, setNdviHistory] = useState<NdviPoint[]>([]);
  const [yieldPred, setYieldPred] = useState<YieldPred | null>(null);
  const [farmMapData, setFarmMapData] = useState<FarmMapData | null>(null);
  const [payouts, setPayouts] = useState<PayoutRecord[]>([]);
  const [journey, setJourney] = useState<{eventType: string; occurredAt: string}[]>([]);
  const [loading, setLoading] = useState(true);

  const fetchAll = useCallback(async () => {
    try {
      const invRes = await api.get(`/portfolio/${investmentId}`);
      if (!invRes.data.success) throw new Error('Not found');
      const inv: Investment = invRes.data.data;
      setInvestment(inv);
      const fId = inv.farmId;

      await Promise.allSettled([
        api.get(`/geospatial/ndvi-history/${fId}?days=90`)
            .then(r => { if (r.data.success) setNdviHistory(r.data.data || []); }),
        api.get(`/geospatial/farms/${fId}/yield`)
            .then(r => { if (r.data.success && r.data.data) setYieldPred(r.data.data); }),
        api.get(`/geospatial/farm-map/${fId}`)
            .then(r => { if (r.data.success) setFarmMapData(r.data.data); }),
        api.get('/portfolio/payouts')
            .then(r => {
              if (r.data.success)
                setPayouts((r.data.data || []).filter((p: PayoutRecord) => p.investmentId === investmentId));
            }),
        api.get(`/portfolio/journey/${inv.farmId}`)
            .then(r => { if (r.data.success) setJourney(r.data.data || []); })
            .catch(() => {}),
      ]);
    } catch (err: any) {
      toast.error(err.response?.data?.message || 'Investment data not found');
      router.push('/portfolio');
    } finally {
      setLoading(false);
    }
  }, [investmentId, router]);

  useEffect(() => {
    const token = localStorage.getItem('access_token');
    if (!token) { router.push('/login'); return; }
    fetchAll();
  }, [fetchAll]);

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
          Loading farm insights...
        </motion.p>
      </div>
  );

  if (!investment) return null;

  const expectedReturn = investment.amountEtb * investment.expectedReturnPct / 100;
  const latestNdvi = ndviHistory.length > 0 ? ndviHistory[ndviHistory.length - 1] : null;
  const ndviChartData = ndviHistory.map(n => ({ date: n.recordedDate, ndviValue: n.ndviValue }));
  const [mapLat, mapLng] = farmMapData
      ? [farmMapData.centroidLat, farmMapData.centroidLng]
      : REGION_COORDS[investment.region] ?? [9.145, 40.489];
  const mapNdvi = farmMapData?.latestNdvi?.ndviValue ?? latestNdvi?.ndviValue;

  return (
      <div className="min-h-screen bg-gradient-to-br from-[#f8fafc] to-[#f1f5f9] text-slate-800 font-sans antialiased pb-16">
        <Navbar />

        <motion.div
            variants={pageFadeIn}
            initial="hidden"
            animate="show"
            className="container mx-auto px-4 sm:px-6 py-8 max-w-4xl space-y-6"
        >
          {/* Navigation Action */}
          <motion.button
              whileHover={{ x: -2 }}
              onClick={() => router.back()}
              className="flex items-center gap-1.5 text-emerald-700 text-xs uppercase tracking-wider font-bold hover:text-emerald-800 transition-colors"
          >
            <ChevronLeft className="w-4 h-4" /> Back to Portfolio
          </motion.button>

          {/* Hero Header Card */}
          <motion.div
              variants={blockVariants}
              className="bg-white rounded-3xl shadow-xs border border-slate-200/60 overflow-hidden"
          >
            <div className="bg-gradient-to-r from-slate-900 via-slate-800 to-slate-900 p-6 sm:p-8 text-white relative">
              <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
                <div className="space-y-1">
                  <h1 className="text-2xl sm:text-3xl font-black tracking-tight">{investment.cropType} Investment</h1>
                  <p className="text-slate-300 text-xs sm:text-sm font-medium">{investment.region} Region &middot; {investment.seasonName}</p>
                </div>
                <StatusBadge status={investment.status} />
              </div>
            </div>

            <div className="p-6 sm:p-8">
              <div className="grid grid-cols-2 sm:grid-cols-3 gap-4 mb-6">
                {[
                  { label: 'Amount Invested', value: `${investment.amountEtb.toLocaleString()} ETB`, highlight: true },
                  { label: 'Expected Return Yield', value: `${investment.expectedReturnPct}% APR` },
                  { label: 'Estimated Growth', value: `+${expectedReturn.toFixed(0)} ETB / yr` },
                  { label: 'Final Return Rate', value: investment.actualReturnPct ? `${investment.actualReturnPct}%` : 'Awaiting Harvest' },
                  { label: 'Invested On', value: new Date(investment.createdAt).toLocaleDateString() },
                  { label: 'Last Updated', value: new Date(investment.updatedAt).toLocaleDateString() },
                ].map((item, index) => (
                    <div
                        key={item.label}
                        className={`rounded-2xl p-4 transition-all ${
                            item.highlight
                                ? 'bg-gradient-to-br from-emerald-50/60 to-teal-50/20 border border-emerald-100/80 shadow-xs'
                                : 'bg-slate-50/80 border border-slate-100'
                        }`}
                    >
                      <p className="text-[10px] text-slate-400 font-bold uppercase tracking-widest">{item.label}</p>
                      <p className={`font-black tracking-tight mt-2 ${item.highlight ? 'text-emerald-800 text-xl' : 'text-slate-800 text-sm'}`}>{item.value}</p>
                    </div>
                ))}
              </div>

              <AnimatePresence>
                {investment.notes && (
                    <motion.div
                        initial={{ opacity: 0, height: 0 }}
                        animate={{ opacity: 1, height: 'auto' }}
                        className="mb-4 bg-blue-50/50 rounded-2xl p-4 border border-blue-100/60"
                    >
                      <p className="text-[10px] text-blue-500 font-bold uppercase tracking-wider mb-1">Portfolio Note</p>
                      <p className="text-sm text-blue-900 italic font-medium">"{investment.notes}"</p>
                    </motion.div>
                )}

                {investment.cancelledReason && (
                    <motion.div
                        initial={{ opacity: 0, height: 0 }}
                        animate={{ opacity: 1, height: 'auto' }}
                        className="mb-4 bg-rose-50/60 rounded-2xl p-4 border border-rose-100/60"
                    >
                      <p className="text-[10px] text-rose-500 font-bold uppercase tracking-wider mb-1">Cancellation Reason</p>
                      <p className="text-sm text-rose-900 font-semibold">{investment.cancelledReason}</p>
                    </motion.div>
                )}
              </AnimatePresence>

              <div className="border-t border-slate-100 pt-4 grid grid-cols-1 sm:grid-cols-3 gap-2 text-[11px] text-slate-400 font-mono tracking-tight">
                <p>Investment Identification: {investment.id}</p>
                <p>Registered Farm Field: {investment.farmId}</p>
                <p>Active Crop Sequence: {investment.cropCycleId}</p>
              </div>
            </div>
          </motion.div>

          {/* Escrow Tracker Node */}
          <motion.div variants={blockVariants} className="bg-white rounded-3xl shadow-xs border border-slate-200/60 p-6 sm:p-8">
            <div className="flex items-center gap-2 mb-6">
              <Lock className="w-4 h-4 text-slate-400" />
              <h3 className="font-black text-slate-900 tracking-tight text-base">Escrow Verification Flow</h3>
            </div>
            <EscrowStateMachine status={investment.status} />
            <div className="mt-6 bg-amber-50/60 border border-amber-100/70 rounded-2xl p-4 text-xs font-medium text-amber-800 leading-relaxed flex items-start gap-2">
              <HelpCircle className="w-4 h-4 text-amber-600 shrink-0 mt-0.5" />
              <span>Funds are secured safely within an isolated escrow engine. Capitally unlocked and dispatched to operational farms only following full confirmation across the verification checks.</span>
            </div>
          </motion.div>

          {/* Spatial Analytics Panel */}
          <motion.div variants={blockVariants} className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div className="bg-white rounded-3xl shadow-xs border border-slate-200/60 p-5 flex flex-col justify-between">
              <div className="flex justify-between items-center mb-4">
                <div>
                  <h3 className="font-black text-slate-900 tracking-tight text-sm">Satellite Matrix Tracking</h3>
                  <p className="text-[10px] text-slate-400 font-medium uppercase tracking-wider mt-0.5">Copernicus Sentinel Imagery</p>
                </div>
                <span className="text-[10px] font-bold tracking-widest uppercase bg-emerald-50 text-emerald-700 border border-emerald-100/80 px-2.5 py-1 rounded-lg">Operational Signal</span>
              </div>
              <div className="rounded-2xl overflow-hidden bg-slate-50 border border-slate-100">
                <SatelliteImageViewComponent farmId={investment.farmId} ndvi={latestNdvi?.ndviValue} healthStatus={latestNdvi?.healthStatus} cloudCoverage={latestNdvi?.cloudCoverage} recordedDate={latestNdvi?.recordedDate} />
              </div>
            </div>

            <div className="bg-white rounded-3xl shadow-xs border border-slate-200/60 p-5 flex flex-col justify-between">
              <div className="flex justify-between items-center mb-4">
                <div>
                  <h3 className="font-black text-slate-900 tracking-tight text-sm">Geospatial Boundary Map</h3>
                  <p className="text-[10px] text-slate-400 font-medium uppercase tracking-wider mt-0.5">
                    {farmMapData ? `${farmMapData.areaHectares.toFixed(1)} hectares footprint` : 'Regional Coordinates'}
                  </p>
                </div>
                <span className="text-[10px] font-bold tracking-widest uppercase bg-blue-50 text-blue-700 border border-blue-100/80 px-2.5 py-1 rounded-lg">Active Map</span>
              </div>
              <div className="rounded-2xl overflow-hidden border border-slate-100 bg-slate-50">
                <FarmMap
                    lat={mapLat}
                    lng={mapLng}
                    label={`${investment.cropType} Property`}
                    height={224}
                    ndvi={mapNdvi}
                    areaHectares={farmMapData?.areaHectares}
                />
              </div>
            </div>
          </motion.div>

          {/* NDVI Timeline Evaluation */}
          <motion.div variants={blockVariants} className="bg-white rounded-3xl shadow-xs border border-slate-200/60 p-6 sm:p-8">
            <h3 className="font-black text-slate-900 tracking-tight text-base mb-1">Vegetation Performance Evaluation</h3>
            <p className="text-xs text-slate-400 font-medium mb-6">
              Normalized Difference Vegetation Index tracking across 90 historical observation sequences &middot; {ndviChartData.length} records processed
            </p>
            <div className="p-2 bg-slate-50/50 rounded-2xl border border-slate-100">
              {ndviChartData.length > 0
                  ? <NdviChart data={ndviChartData} height={220} />
                  : <div className="h-36 flex items-center justify-center text-slate-400 text-xs font-bold tracking-wide">No tracking metrics logged yet</div>
              }
            </div>
          </motion.div>

          {/* Machine Learning Yield Analytics */}
          {yieldPred && (
              <motion.div variants={blockVariants} className="bg-white rounded-3xl shadow-xs border border-slate-200/60 p-6 sm:p-8">
                <div className="flex justify-between items-start mb-6">
                  <div>
                    <h3 className="font-black text-slate-900 tracking-tight text-base">Predictive Harvest Model</h3>
                    <p className="text-xs text-slate-400 font-medium mt-0.5">ML estimation pipeline &middot; {yieldPred.weeksToHarvest} remaining weeks until target harvest window</p>
                  </div>
                  <span className="bg-slate-900 text-white text-[10px] font-black tracking-widest uppercase px-3 py-1.5 rounded-xl shadow-xs">
                {yieldPred.confidencePct}% Accuracy Probability
              </span>
                </div>
                <div className="grid grid-cols-3 gap-3">
                  {[
                    { label: 'Minimum Harvest Threshold', value: `${yieldPred.predictedYieldMin.toFixed(1)} qt/ha`, color: 'text-slate-600 bg-slate-50' },
                    { label: 'Mean Distribution Target', value: `${yieldPred.predictedYieldMean.toFixed(1)} qt/ha`, color: 'text-emerald-800 bg-gradient-to-br from-emerald-50/40 to-teal-50/10 border-emerald-100/60 font-black' },
                    { label: 'Maximum Potential Curve', value: `${yieldPred.predictedYieldMax.toFixed(1)} qt/ha`, color: 'text-slate-600 bg-slate-50' },
                  ].map(y => (
                      <div key={y.label} className={`rounded-2xl p-4 text-center border border-slate-100 ${y.color}`}>
                        <p className="text-[10px] text-slate-400 font-bold uppercase tracking-wider mb-2">{y.label}</p>
                        <p className="text-base tracking-tight">{y.value}</p>
                      </div>
                  ))}
                </div>
              </motion.div>
          )}

          {/* Return Yield Multipliers */}
          <motion.div variants={blockVariants} className="bg-white rounded-3xl shadow-xs border border-slate-200/60 p-6 sm:p-8">
            <h3 className="font-black text-slate-900 tracking-tight text-base mb-1">Return Rate History</h3>
            <p className="text-xs text-slate-400 font-medium mb-6">Historical progression curves combined with physical sensor observations</p>
            <div className="p-2 bg-slate-50/50 rounded-2xl border border-slate-100">
              <AprHistoryChart
                  baseApr={investment.expectedReturnPct * 0.85}
                  currentApr={investment.expectedReturnPct}
              />
            </div>
          </motion.div>

          {/* Farm Journey Timeline */}
          {journey.length > 0 && (
            <motion.div variants={blockVariants} className="bg-white rounded-3xl shadow-xs border border-slate-200/60 p-6 sm:p-8">
              <h3 className="font-black text-slate-900 tracking-tight text-base mb-6">Farm Journey Timeline</h3>
              <div className="relative">
                <div className="absolute left-4 top-0 bottom-0 w-0.5 bg-slate-100" />
                <div className="space-y-6">
                  {journey.map((ev, i) => {
                    const STEPS: Record<string, {label: string; color: string; dot: string}> = {
                      'FULLY_FUNDED':       { label: 'Farm fully funded',          color: 'text-emerald-800 bg-emerald-50 border-emerald-100', dot: 'bg-emerald-500' },
                      'BID_ACCEPTED':       { label: 'Off-taker bid accepted',     color: 'text-blue-800 bg-blue-50 border-blue-100',         dot: 'bg-blue-500' },
                      'TRUCKS_DISPATCHED':  { label: 'Trucks dispatched to farm',  color: 'text-amber-800 bg-amber-50 border-amber-100',       dot: 'bg-amber-500' },
                      'HARVEST_CONFIRMED':  { label: 'Harvest received at factory',color: 'text-teal-800 bg-teal-50 border-teal-100',          dot: 'bg-teal-500' },
                      'SETTLEMENT_COMPLETED':{ label: 'Settlement paid to investors',color: 'text-purple-800 bg-purple-50 border-purple-100', dot: 'bg-purple-500' },
                      'DROUGHT_TRIGGERED':  { label: 'Drought insurance triggered', color: 'text-red-800 bg-red-50 border-red-100',            dot: 'bg-red-500' },
                    };
                    const step = STEPS[ev.eventType] || { label: ev.eventType, color: 'text-slate-600 bg-slate-50 border-slate-100', dot: 'bg-slate-400' };
                    return (
                      <div key={i} className="flex items-start gap-4 pl-0">
                        <div className={`w-8 h-8 rounded-full ${step.dot} flex items-center justify-center text-white text-xs font-black z-10 shrink-0`}>
                          {i + 1}
                        </div>
                        <div className={`flex-1 rounded-2xl border p-3 ${step.color}`}>
                          <p className="font-black text-sm tracking-tight">{step.label}</p>
                          <p className="text-xs opacity-70 mt-0.5 font-mono">
                            {new Date(ev.occurredAt).toLocaleString()}
                          </p>
                        </div>
                      </div>
                    );
                  })}
                </div>
              </div>
            </motion.div>
          )}

          {/* Payout Calculation Ledger */}
          <motion.div variants={blockVariants} className="bg-white rounded-3xl shadow-xs border border-slate-200/60 p-6 sm:p-8">
            <h3 className="font-black text-slate-900 tracking-tight text-base mb-4">Payout Ledger Matrix</h3>
            <div className="grid grid-cols-3 gap-3">
              {[
                { label: 'Invested Principal', value: `${investment.amountEtb.toLocaleString()} ETB`, bg: 'bg-slate-50' },
                { label: 'Calculated Yield Accrual', value: `+${expectedReturn.toFixed(0)} ETB`, bg: 'bg-emerald-50/50 text-emerald-800' },
                { label: 'Aggregate Settlement', value: `${(investment.amountEtb + expectedReturn).toFixed(0)} ETB`, bg: 'bg-slate-900 text-white border-transparent' },
              ].map(c => (
                  <div key={c.label} className={`${c.bg} rounded-2xl p-4 text-center border border-slate-100`}>
                    <p className={`text-[10px] font-bold uppercase tracking-wider mb-2 ${c.bg.includes('slate-900') ? 'text-slate-400' : 'text-slate-400'}`}>{c.label}</p>
                    <p className="text-base font-black tracking-tight">{c.value}</p>
                  </div>
              ))}
            </div>
          </motion.div>

          {/* Track Vouchers Node */}
          <motion.div variants={blockVariants}>
            <Link href={`/portfolio/${investmentId}/vouchers`}
                  className="flex items-center justify-between bg-white rounded-3xl shadow-xs border border-slate-200/60 p-5 hover:border-slate-300 hover:shadow-md transition-all duration-300 group"
            >
              <div className="flex items-center gap-4">
                <div className="w-12 h-12 bg-slate-50 border border-slate-100 rounded-2xl flex items-center justify-center text-slate-600 shadow-2xs">
                  <Ticket className="w-5 h-5" />
                </div>
                <div>
                  <p className="font-black text-slate-900 tracking-tight text-sm">Voucher Redemption Ledger</p>
                  <p className="text-xs text-slate-400 font-medium mt-0.5">Inspect physical component distributions and verification updates for this crop development</p>
                </div>
              </div>
              <ChevronRight className="w-5 h-5 text-slate-400 group-hover:text-slate-900 group-hover:translate-x-0.5 transition-all" />
            </Link>
          </motion.div>

          {/* Historical Account Settlements */}
          <AnimatePresence>
            {payouts.length > 0 && (
                <motion.div
                    variants={blockVariants}
                    className="bg-white rounded-3xl shadow-xs border border-slate-200/60 p-6 sm:p-8"
                >
                  <h3 className="font-black text-slate-900 tracking-tight text-base mb-4">Dispatched Historical Payouts</h3>
                  <div className="space-y-3">
                    {payouts.map(p => (
                        <div key={p.id} className="flex justify-between items-center border border-slate-100 rounded-2xl p-4 bg-slate-50/40 hover:bg-slate-50 transition-colors">
                          <div className="space-y-0.5">
                            <p className="text-sm font-bold text-slate-800 tracking-tight">{p.payoutReason || 'Standard Balance Settlement'}</p>
                            <p className="text-[11px] font-medium text-slate-400">{new Date(p.paidAt).toLocaleDateString()}</p>
                          </div>
                          <div className="text-right space-y-0.5">
                            <p className="font-black text-emerald-800 tracking-tight">{p.totalEtb.toLocaleString()} ETB</p>
                            <p className="text-[11px] font-bold text-slate-400 uppercase tracking-wider">{p.actualApr}% Final APR</p>
                          </div>
                        </div>
                    ))}
                  </div>
                </motion.div>
            )}
          </AnimatePresence>

        </motion.div>
      </div>
  );
}
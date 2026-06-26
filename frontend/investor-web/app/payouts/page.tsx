'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import toast from 'react-hot-toast';
import { motion, AnimatePresence } from 'framer-motion';
import {
  FileText,
  Calendar,
  X,
  Coins,
  TrendingUp,
  ShieldCheck,
  Percent,
  ArrowUpRight
} from 'lucide-react';
import api from '@/lib/api';
import Navbar from '@/components/Navbar';
import Sidebar from '@/components/DashboardSidebar';
import { PayoutRecord } from '@/lib/types';

const pageFadeIn = {
  hidden: { opacity: 0, y: 15 },
  show: { opacity: 1, y: 0, transition: { duration: 0.4, ease: 'easeOut', staggerChildren: 0.05 } }
};

const blockVariants = {
  hidden: { opacity: 0, y: 15 },
  show: { opacity: 1, y: 0, transition: { type: 'spring', stiffness: 100, damping: 15 } }
};

export default function PayoutsPage() {
  const router = useRouter();
  const [payouts, setPayouts] = useState<PayoutRecord[]>([]);
  const [loading, setLoading] = useState(true);
  const [dateFrom, setDateFrom] = useState('');
  const [dateTo, setDateTo] = useState('');


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
    } finally {
      setLoading(false);
    }
  };

  const filtered = payouts.filter(p => {
    const d = new Date(p.paidAt);
    if (dateFrom && d < new Date(dateFrom)) return false;
    if (dateTo && d > new Date(dateTo)) return false;
    return true;
  });

  const totalReceived = filtered.reduce((s, p) => s + p.totalEtb, 0);
  const totalReturns = filtered.reduce((s, p) => s + p.returnEtb, 0);
  const totalPrincipal = filtered.reduce((s, p) => s + p.principalEtb, 0);
  const avgApr = filtered.length > 0
      ? filtered.reduce((s, p) => s + p.actualApr, 0) / filtered.length : 0;

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
          Loading payout records...
        </motion.p>
      </div>
  );

  return (
      <div className="min-h-screen bg-gradient-to-br from-[#f8fafc] to-[#f1f5f9] text-slate-800 font-sans antialiased flex flex-col">
        {/* Primary Top Navigation bar */}
        <Navbar />

        <div className="flex flex-1 relative">
          {/* Core Sidebar Application Shell Panel */}
          <Sidebar isOpen={isSidebarOpen} setIsOpen={setIsSidebarOpen} />

          {/* Dynamic App Shell Content Frame */}
          <main className="flex-1 min-w-0 overflow-y-auto px-4 sm:px-8 py-8 lg:max-w-6xl xl:max-w-7xl mx-auto w-full">
            <motion.div
                variants={pageFadeIn}
                initial="hidden"
                animate="show"
                className="space-y-6"
            >
              {/* Top Operational Header */}
              <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4 mb-2">
                <div className="space-y-1">
                  <h1 className="text-2xl sm:text-3xl font-black tracking-tight text-slate-900">Payout History</h1>
                  <p className="text-slate-500 text-xs sm:text-sm font-medium">All harvest settlement payments received</p>
                </div>
                <Link
                    href="/statements"
                    className="flex items-center gap-2 bg-slate-900 text-white px-5 py-2.5 rounded-xl text-xs font-bold uppercase tracking-wide shadow-xs hover:bg-slate-800 transition-all shrink-0"
                >
                  <FileText className="w-4 h-4" /> Download Statement
                </Link>
              </div>

              {/* Temporal Filters Panel */}
              <motion.div variants={blockVariants} className="bg-white rounded-3xl shadow-xs border border-slate-200/60 p-5 sm:p-6">
                <div className="flex flex-wrap gap-4 items-end">
                  <div className="w-full sm:w-auto space-y-1.5">
                    <label className="block text-[10px] font-bold uppercase text-slate-400 tracking-wider">From Settlement Date</label>
                    <input
                        type="date"
                        value={dateFrom}
                        onChange={e => setDateFrom(e.target.value)}
                        className="w-full sm:w-48 px-4 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-xs font-medium focus:outline-none focus:ring-2 focus:ring-emerald-600/20 focus:border-emerald-600 transition"
                    />
                  </div>
                  <div className="w-full sm:w-auto space-y-1.5">
                    <label className="block text-[10px] font-bold uppercase text-slate-400 tracking-wider">To Settlement Date</label>
                    <input
                        type="date"
                        value={dateTo}
                        onChange={e => setDateTo(e.target.value)}
                        className="w-full sm:w-48 px-4 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-xs font-medium focus:outline-none focus:ring-2 focus:ring-emerald-600/20 focus:border-emerald-600 transition"
                    />
                  </div>
                  {(dateFrom || dateTo) && (
                      <motion.button
                          initial={{ scale: 0.95, opacity: 0 }}
                          animate={{ scale: 1, opacity: 1 }}
                          onClick={() => { setDateFrom(''); setDateTo(''); }}
                          className="flex items-center gap-1 px-4 py-2.5 bg-slate-100 text-slate-600 rounded-xl text-xs font-bold uppercase tracking-wider hover:bg-slate-200 transition"
                      >
                        <X className="w-3.5 h-3.5" /> Clear Filters
                      </motion.button>
                  )}
                </div>
              </motion.div>

              {/* Yield Analytics Grid */}
              <motion.div variants={blockVariants} className="grid grid-cols-2 md:grid-cols-4 gap-4">
                {[
                  {
                    label: 'Total Capital Liquidated',
                    value: `${totalReceived.toLocaleString()} ETB`,
                    color: 'text-slate-900 bg-white border-slate-200/60',
                    icon: <Coins className="w-4 h-4 text-slate-500" />
                  },
                  {
                    label: 'Net Profits Generated',
                    value: `+${totalReturns.toLocaleString()} ETB`,
                    color: 'text-emerald-800 bg-gradient-to-br from-emerald-50/60 to-teal-50/20 border-emerald-100/80 font-black',
                    icon: <TrendingUp className="w-4 h-4 text-emerald-600" />
                  },
                  {
                    label: 'Principal Recovered',
                    value: `${totalPrincipal.toLocaleString()} ETB`,
                    color: 'text-slate-700 bg-white border-slate-200/60',
                    icon: <ShieldCheck className="w-4 h-4 text-slate-500" />
                  },
                  {
                    label: 'Weighted Return (APR)',
                    value: `${avgApr.toFixed(1)}%`,
                    color: 'text-amber-700 bg-white border-slate-200/60',
                    icon: <Percent className="w-4 h-4 text-amber-600" />
                  },
                ].map(s => (
                    <div key={s.label} className={`rounded-2xl border p-4 shadow-2xs space-y-2 flex flex-col justify-between ${s.color}`}>
                      <div className="flex justify-between items-start gap-2">
                        <p className="text-[10px] text-slate-400 font-bold uppercase tracking-wider leading-tight">{s.label}</p>
                        <div className="shrink-0">{s.icon}</div>
                      </div>
                      <p className="text-lg sm:text-xl font-black tracking-tight">{s.value}</p>
                    </div>
                ))}
              </motion.div>

              {/* Master Ledger List */}
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
                          <Coins className="w-5 h-5" />
                        </div>
                        <div className="space-y-1">
                          <p className="text-slate-400 text-xs font-bold uppercase tracking-wider">No corresponding payout records found</p>
                          <p className="text-slate-400 text-xs max-w-sm mx-auto">Settlement balances drop automatically to accounts once downstream production yields are officially validated by contracted off-takers.</p>
                        </div>
                        <Link
                            href="/listings"
                            className="mt-2 inline-block bg-slate-900 text-white px-5 py-2.5 rounded-xl text-xs font-bold uppercase tracking-wider hover:bg-slate-800 transition shadow-2xs"
                        >
                          Browse Primary Listings
                        </Link>
                      </motion.div>
                  ) : (
                      filtered.map(p => (
                          <motion.div
                              layout="position"
                              key={p.id}
                              className="bg-white rounded-2xl shadow-2xs border border-slate-200/60 p-5 hover:shadow-xs transition-all border-l-4 border-l-emerald-600"
                          >
                            <div className="flex flex-col sm:flex-row justify-between items-start gap-4">
                              <div className="space-y-1">
                                <div className="flex items-center gap-2">
                                  <span className="w-2 h-2 rounded-full bg-emerald-500 animate-pulse" />
                                  <p className="font-black text-slate-900 tracking-tight text-sm sm:text-base">
                                    {p.payoutReason || 'Harvest Settlement Disbursal'}
                                  </p>
                                </div>
                                <div className="flex flex-wrap items-center gap-x-3 gap-y-1 text-xs text-slate-400 font-medium">
                            <span className="flex items-center gap-1 font-mono">
                              <Calendar className="w-3.5 h-3.5" />
                              {new Date(p.paidAt).toLocaleDateString('en-ET', { year: 'numeric', month: 'long', day: 'numeric' })}
                            </span>
                                  <span className="hidden sm:inline text-slate-300">&middot;</span>
                                  <span className="font-mono bg-slate-50 border border-slate-100 px-1.5 py-0.5 rounded text-[11px]">
                              Profile reference: {p.farmId.slice(0, 8)}…
                            </span>
                                </div>
                              </div>
                              <div className="text-left sm:text-right shrink-0 space-y-0.5">
                                <p className="text-xl font-black text-emerald-700 tracking-tight">{p.totalEtb.toLocaleString()} ETB</p>
                                <span className="inline-block text-[10px] bg-emerald-50 text-emerald-700 border border-emerald-100/70 px-2 py-0.5 rounded-full font-bold uppercase tracking-wider">
                            {p.actualApr.toFixed(1)}% Yield APR
                          </span>
                              </div>
                            </div>

                            <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 mt-4 pt-4 border-t border-slate-100/80 items-center">
                              <div className="space-y-0.5">
                                <p className="text-[10px] text-slate-400 font-bold uppercase tracking-wider">Asset Principal Return</p>
                                <p className="text-xs font-semibold text-slate-700">{p.principalEtb.toLocaleString()} ETB</p>
                              </div>
                              <div className="space-y-0.5">
                                <p className="text-[10px] text-slate-400 font-bold uppercase tracking-wider">Compounded Yield Net Profit</p>
                                <p className="text-xs font-black text-emerald-700">+{p.returnEtb.toLocaleString()} ETB</p>
                              </div>
                              <div className="text-left sm:text-right">
                                <Link
                                    href={`/portfolio/${p.investmentId}`}
                                    className="inline-flex items-center gap-1 text-emerald-700 text-xs font-bold uppercase tracking-wider hover:text-emerald-800 transition"
                                >
                                  Review Portfolio Node <ArrowUpRight className="w-3.5 h-3.5" />
                                </Link>
                              </div>
                            </div>
                          </motion.div>
                      ))
                  )}
                </AnimatePresence>
              </motion.div>
            </motion.div>
          </main>
        </div>
      </div>
  );
}
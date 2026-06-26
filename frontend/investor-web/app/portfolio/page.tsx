'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import toast from 'react-hot-toast';
import { motion, AnimatePresence } from 'framer-motion';
import api from '@/lib/api';
import DashboardSidebar from '@/components/DashboardSidebar';
import StatusBadge from '@/components/StatusBadge';
import {
  Briefcase,
  ArrowUpRight,
  RotateCcw,
  Activity,
  Percent,
  ChevronRight,
  Ticket,
  XCircle,
  Search,
  Sparkles,
  SlidersHorizontal
} from 'lucide-react';
import { Investment, PortfolioStats } from '@/lib/types';

type FilterType = 'ALL' | 'ACTIVE' | 'COMPLETED' | 'CANCELLED' | 'REFUNDED';
type SortType = 'date_desc' | 'date_asc' | 'amount_desc' | 'apr_desc';

const containerVariants = {
  hidden: { opacity: 0 },
  show: {
    opacity: 1,
    transition: { staggerChildren: 0.08 }
  }
};

const itemVariants = {
  hidden: { opacity: 0, y: 15 },
  show: { opacity: 1, y: 0, transition: { type: 'spring', stiffness: 100 } }
};

export default function PortfolioPage() {
  const router = useRouter();
  const [investments, setInvestments] = useState<Investment[]>([]);
  const [stats, setStats] = useState<PortfolioStats | null>(null);
  const [loading, setLoading] = useState(true);
  const [cancellingId, setCancellingId] = useState<string | null>(null);
  const [filter, setFilter] = useState<FilterType>('ALL');
  const [sort, setSort] = useState<SortType>('date_desc');

  useEffect(() => {
    const token = localStorage.getItem('access_token');
    if (!token) { router.push('/login'); return; }
    fetchPortfolio();
  }, []);

  const fetchPortfolio = async () => {
    try {
      const res = await api.get('/portfolio');
      if (res.data.success) {
        const data: Investment[] = res.data.data || [];
        setInvestments(data);
        const totalInvested  = data.reduce((s, i) => s + i.amountEtb, 0);
        const totalReturned  = data.filter(i => i.status === 'COMPLETED')
            .reduce((s, i) => s + (i.payoutAmountEtb ?? 0), 0);
        const active         = data.filter(i => ['ACTIVE','ESCROW_LOCKED','PENDING'].includes(i.status)).length;
        const completed      = data.filter(i => i.status === 'COMPLETED').length;
        const cancelled      = data.filter(i => i.status === 'CANCELLED').length;
        const avgApr         = data.length > 0
            ? data.reduce((s, i) => s + i.expectedReturnPct, 0) / data.length : 0;
        setStats({ totalInvested, totalReturned, activeInvestments: active,
          completedInvestments: completed, cancelledInvestments: cancelled, averageApr: avgApr });
      }
    } catch (err: any) {
      toast.error(err.response?.data?.message || "Oops! Couldn't load your portfolio.");
    } finally { setLoading(false); }
  };

  const handleCancel = async (investmentId: string) => {
    if (!window.confirm('Are you sure you want to back out of this investment? Your funds will be sent right back to you.')) return;
    setCancellingId(investmentId);
    try {
      const res = await api.post(`/investments/${investmentId}/cancel`, { reason: 'Cancelled by investor' });
      if (res.data.success) {
        toast.success("Done! Your investment was cancelled and funds are on their way back.");
        fetchPortfolio();
      }
    } catch (err: any) {
      toast.error(err.response?.data?.message || "We couldn't cancel this investment right now.");
    } finally { setCancellingId(null); }
  };

  const filtered = investments.filter(i => {
    if (filter === 'ALL')       return true;
    if (filter === 'ACTIVE')    return ['ACTIVE','ESCROW_LOCKED','PENDING'].includes(i.status);
    if (filter === 'REFUNDED')  return i.status === 'REFUNDED';
    return i.status === filter;
  });

  const sorted = [...filtered].sort((a, b) => {
    if (sort === 'date_desc')   return new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime();
    if (sort === 'date_asc')    return new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime();
    if (sort === 'amount_desc') return b.amountEtb - a.amountEtb;
    if (sort === 'apr_desc')    return b.expectedReturnPct - a.expectedReturnPct;
    return 0;
  });

  if (loading) return (
      <div className="h-screen flex flex-col items-center justify-center bg-gradient-to-br from-slate-50 to-emerald-50/30 space-y-4">
        <motion.div
            animate={{ rotate: 360 }}
            transition={{ repeat: Infinity, duration: 1, ease: "linear" }}
            className="rounded-full h-12 w-12 border-4 border-slate-200 border-t-emerald-600"
        />
        <motion.p
            initial={{ opacity: 0 }}
            animate={{ opacity: [0.4, 1, 0.4] }}
            transition={{ repeat: Infinity, duration: 1.5 }}
            className="text-slate-500 text-xs font-bold tracking-widest uppercase"
        >
          Gathering your farm stats...
        </motion.p>
      </div>
  );

  const TABS: { key: FilterType; label: string }[] = [
    { key: 'ALL',       label: 'All' },
    { key: 'ACTIVE',    label: 'Growing' },
    { key: 'COMPLETED', label: 'Harvested' },
    { key: 'REFUNDED',  label: 'Refunded' },
    { key: 'CANCELLED', label: 'Cancelled' },
  ];

  return (
      <div className="h-screen bg-gradient-to-br from-[#f8fafc] to-[#f1f5f9] text-slate-800 font-sans antialiased flex overflow-hidden">
        <DashboardSidebar activeTab="portfolio" />

        <div className="flex-1 flex flex-col min-w-0 overflow-hidden pt-14 lg:pt-0">
          <main className="flex-1 overflow-y-auto p-4 sm:p-6 lg:p-8 space-y-6">

            {/* Header Title Section */}
            <motion.div
                initial={{ opacity: 0, y: -10 }}
                animate={{ opacity: 1, y: 0 }}
                className="flex items-center justify-between border-b border-slate-200/60 pb-5"
            >
              <div className="flex items-center gap-4">
                <motion.div
                    whileHover={{ scale: 1.05 }}
                    className="p-3 bg-gradient-to-tr from-slate-900 to-slate-800 text-white rounded-2xl shadow-md"
                >
                  <Briefcase className="w-5 h-5" />
                </motion.div>
                <div>
                  <h1 className="text-2xl font-black tracking-tight text-slate-900 flex items-center gap-2">
                    My Portfolio <Sparkles className="w-4 h-4 text-amber-500 fill-amber-500 animate-pulse" />
                  </h1>
                  <p className="text-sm text-slate-500 font-medium mt-0.5">See how your investments are growing over time</p>
                </div>
              </div>
            </motion.div>

            {/* Metric Cards Grid */}
            <motion.div
                variants={containerVariants}
                initial="hidden"
                animate="show"
                className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4"
            >
              {[
                { label: 'Total Invested', value: `${(stats?.totalInvested || 0).toLocaleString()} ETB`, color: 'text-slate-900', icon: ArrowUpRight, iconClass: 'text-emerald-600 bg-emerald-50' },
                { label: 'Total Payouts Received', value: `${(stats?.totalReturned || 0).toLocaleString()} ETB`, color: 'text-emerald-800', icon: RotateCcw, iconClass: 'text-blue-600 bg-blue-50', customClass: 'bg-gradient-to-br from-emerald-50/40 to-teal-50/20 border-emerald-100' },
                { label: 'Active Projects', value: stats?.activeInvestments || 0, color: 'text-slate-800', icon: Activity, iconClass: 'text-amber-600 bg-amber-50' },
                { label: 'Average Return Rate', value: `${(stats?.averageApr || 0).toFixed(1)}% APR`, color: 'text-slate-900', icon: Percent, iconClass: 'text-indigo-600 bg-indigo-50' },
              ].map((s) => {
                const IconComponent = s.icon;
                return (
                    <motion.div
                        key={s.label}
                        variants={itemVariants}
                        whileHover={{ y: -4, transition: { duration: 0.2 } }}
                        className={`bg-white rounded-2xl border border-slate-200/60 p-5 relative overflow-hidden shadow-xs hover:shadow-md transition-shadow duration-300 ${s.customClass || ''}`}
                    >
                      <p className="text-slate-400 text-[10px] font-bold uppercase tracking-widest">{s.label}</p>
                      <p className={`text-2xl font-black ${s.color} mt-3 tracking-tight`}>{s.value}</p>
                      <div className={`absolute top-4 right-4 p-2 rounded-xl ${s.iconClass}`}>
                        <IconComponent className="w-4 h-4 opacity-90" />
                      </div>
                    </motion.div>
                );
              })}
            </motion.div>

            {/* Navigation Filters & Controls */}
            <motion.div
                initial={{ opacity: 0 }}
                animate={{ opacity: 1 }}
                transition={{ delay: 0.2 }}
                className="flex flex-col sm:flex-row gap-3 justify-between items-start sm:items-center pt-2"
            >
              <div className="flex gap-1.5 bg-white border border-slate-200/80 rounded-2xl shadow-xs p-1.5 flex-wrap">
                {TABS.map(tab => (
                    <button
                        key={tab.key}
                        onClick={() => setFilter(tab.key)}
                        className={`px-4 py-2 rounded-xl text-xs font-bold transition-all relative ${
                            filter === tab.key
                                ? 'bg-slate-900 text-white shadow-sm'
                                : 'text-slate-500 hover:text-slate-800 hover:bg-slate-100/70'
                        }`}
                    >
                      {tab.label}
                      <span className={`ml-2 text-[10px] px-1.5 py-0.5 rounded-md font-black ${filter === tab.key ? 'bg-white/20 text-white' : 'bg-slate-100 text-slate-500'}`}>
                    {tab.key === 'ALL' ? investments.length
                        : tab.key === 'ACTIVE' ? investments.filter(i => ['ACTIVE','ESCROW_LOCKED','PENDING'].includes(i.status)).length
                            : investments.filter(i => i.status === tab.key).length}
                  </span>
                    </button>
                ))}
              </div>

              <div className="flex items-center gap-2 w-full sm:w-auto">
                <SlidersHorizontal className="w-4 h-4 text-slate-400 hidden sm:block" />
                <select
                    value={sort}
                    onChange={e => setSort(e.target.value as SortType)}
                    className="bg-white border border-slate-200 text-xs font-bold rounded-xl px-3 py-2.5 text-slate-600 focus:outline-none focus:ring-2 focus:ring-slate-900 shadow-xs cursor-pointer tracking-wide w-full sm:w-auto"
                >
                  <option value="date_desc">Newest Investments First</option>
                  <option value="date_asc">Oldest Investments First</option>
                  <option value="amount_desc">Biggest Investments</option>
                  <option value="apr_desc">Highest Yield Rates</option>
                </select>
              </div>
            </motion.div>

            {/* Dynamic Content List */}
            <AnimatePresence mode="wait">
              {sorted.length === 0 ? (
                  <motion.div
                      initial={{ opacity: 0, scale: 0.95 }}
                      animate={{ opacity: 1, scale: 1 }}
                      exit={{ opacity: 0, scale: 0.95 }}
                      className="bg-white rounded-3xl shadow-xs border border-slate-200/60 p-12 text-center max-w-xl mx-auto mt-6"
                  >
                    <Search className="w-8 h-8 text-slate-300 mx-auto mb-4" />
                    <p className="text-slate-800 text-sm font-bold tracking-wide">Nothing matches this filter yet</p>
                    <p className="text-xs text-slate-400 mt-1.5 max-w-xs mx-auto">Looks like you don't have any investments under this category right now.</p>
                    <Link
                        href="/listings"
                        className="mt-6 inline-block bg-emerald-800 hover:bg-emerald-700 text-white px-6 py-3 rounded-xl text-xs font-bold tracking-wider hover:scale-[1.02] active:scale-[0.98] transition shadow-md shadow-emerald-800/10"
                    >
                      Explore Active Farms
                    </Link>
                  </motion.div>
              ) : (
                  <motion.div
                      variants={containerVariants}
                      initial="hidden"
                      animate="show"
                      className="space-y-4"
                  >
                    {sorted.map(inv => (
                        <motion.div
                            key={inv.id}
                            variants={itemVariants}
                            layout
                            whileHover={{ borderColor: 'rgb(203, 213, 225)' }}
                            className="bg-white rounded-2xl shadow-xs border border-slate-200/60 p-5 hover:shadow-md transition-shadow duration-300 space-y-4"
                        >
                          {/* Top row info */}
                          <div className="flex justify-between items-start gap-4">
                            <div className="space-y-1.5">
                              <div className="flex items-center gap-2 flex-wrap">
                                <h3 className="font-extrabold text-base text-slate-900 tracking-tight">{inv.cropType} &mdash; {inv.region}</h3>
                                <StatusBadge status={inv.status} />
                              </div>
                              <p className="text-slate-400 text-xs font-semibold bg-slate-50 inline-block px-2 py-0.5 rounded-md">{inv.seasonName}</p>

                              {inv.notes && (
                                  <p className="text-slate-500 text-xs italic mt-2 border-l-2 border-emerald-500 pl-3 bg-emerald-50/30 py-1 pr-2 rounded-r-md">
                                    "{inv.notes}"
                                  </p>
                              )}
                              {inv.cancelledReason && (
                                  <p className="text-rose-600 text-xs font-semibold mt-1 bg-rose-50 px-2 py-1 rounded-md inline-block">Reason for cancellation: {inv.cancelledReason}</p>
                              )}
                            </div>

                            <div className="text-right shrink-0">
                              <p className="text-lg font-black text-slate-900 tracking-tight">{inv.amountEtb.toLocaleString()} ETB</p>
                              <p className="text-xs text-emerald-700 font-bold flex items-center justify-end gap-1 mt-0.5">
                                <span>Rate: {inv.expectedReturnPct}% APR</span>
                              </p>
                              {inv.payoutAmountEtb && inv.payoutAmountEtb > 0 && (
                                  <p className="text-[10px] font-bold tracking-wide uppercase text-blue-700 bg-blue-50 border border-blue-100 px-2.5 py-0.5 rounded-lg mt-2 inline-block">
                                    Payout Cleared: {inv.payoutAmountEtb.toLocaleString()} ETB
                                  </p>
                              )}
                            </div>
                          </div>

                          {/* Breakdown Matrix */}
                          <div className="grid grid-cols-2 md:grid-cols-4 gap-4 pt-4 border-t border-slate-100">
                            <div>
                              <p className="text-slate-400 text-[10px] font-bold uppercase tracking-wider">Estimated Return</p>
                              <p className="text-xs font-black text-emerald-700 mt-0.5">
                                +{(inv.amountEtb * inv.expectedReturnPct / 100).toFixed(0)} ETB / year
                              </p>
                            </div>
                            {inv.actualReturnPct != null && (
                                <div>
                                  <p className="text-slate-400 text-[10px] font-bold uppercase tracking-wider">Final Return Rate</p>
                                  <p className="text-xs font-black text-blue-600 mt-0.5">{inv.actualReturnPct}%</p>
                                </div>
                            )}
                            <div>
                              <p className="text-slate-400 text-[10px] font-bold uppercase tracking-wider">Invested On</p>
                              <p className="text-xs font-bold text-slate-600 mt-0.5">{new Date(inv.createdAt).toLocaleDateString()}</p>
                            </div>
                            <div>
                              <p className="text-slate-400 text-[10px] font-bold uppercase tracking-wider">Reference ID</p>
                              <p className="text-[11px] font-mono text-slate-400 mt-0.5 tracking-tight">#{inv.id.slice(0, 8).toUpperCase()}</p>
                            </div>
                          </div>

                          {/* Bottom row actions */}
                          <div className="flex items-center justify-between pt-3.5 border-t border-slate-100 flex-wrap gap-2">
                            <div className="flex items-center gap-6">
                              <Link
                                  href={`/portfolio/${inv.id}`}
                                  className="text-slate-900 text-xs font-bold uppercase tracking-wider hover:text-emerald-700 transition-colors flex items-center gap-0.5 group"
                              >
                                View Details
                                <ChevronRight className="w-4 h-4 transition-transform group-hover:translate-x-0.5" />
                              </Link>
                              <Link
                                  href={`/portfolio/${inv.id}/vouchers`}
                                  className="text-slate-500 text-xs font-bold uppercase tracking-wider hover:text-slate-800 transition-colors flex items-center gap-1.5"
                              >
                                <Ticket className="w-3.5 h-3.5 text-slate-400" />
                                Receipts & Vouchers
                              </Link>
                            </div>

                            {['PENDING', 'ESCROW_LOCKED'].includes(inv.status) && (
                                <button
                                    onClick={() => handleCancel(inv.id)}
                                    disabled={cancellingId === inv.id}
                                    className="text-rose-500 hover:text-rose-700 text-xs font-bold uppercase tracking-wider transition-colors disabled:opacity-50 flex items-center gap-1"
                                >
                                  <XCircle className="w-3.5 h-3.5" />
                                  {cancellingId === inv.id ? 'Cancelling...' : 'Cancel Investment'}
                                </button>
                            )}
                          </div>

                        </motion.div>
                    ))}
                  </motion.div>
              )}
            </AnimatePresence>
          </main>
        </div>
      </div>
  );
}
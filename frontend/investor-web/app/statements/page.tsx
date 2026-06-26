'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import toast from 'react-hot-toast';
import { motion, AnimatePresence } from 'framer-motion';
import {
  FileText,
  Calendar,
  X,
  Coins,
  TrendingUp,
  ShieldCheck,
  Layers,
  Loader2,
  ArrowDownToLine
} from 'lucide-react';
import api from '@/lib/api';
import Navbar from '@/components/Navbar';
import Sidebar from '@/components/DashboardSidebar';
import { PayoutRecord, Investment } from '@/lib/types';

const pageFadeIn = {
  hidden: { opacity: 0, y: 15 },
  show: { opacity: 1, y: 0, transition: { duration: 0.4, ease: 'easeOut', staggerChildren: 0.05 } }
};

const blockVariants = {
  hidden: { opacity: 0, y: 15 },
  show: { opacity: 1, y: 0, transition: { type: 'spring', stiffness: 100, damping: 15 } }
};

export default function StatementsPage() {
  const router = useRouter();
  const [payouts, setPayouts] = useState<PayoutRecord[]>([]);
  const [investments, setInvestments] = useState<Investment[]>([]);
  const [loading, setLoading] = useState(true);
  const [generating, setGenerating] = useState(false);
  const [dateFrom, setDateFrom] = useState('');
  const [dateTo, setDateTo] = useState('');
  const [isSidebarOpen, setIsSidebarOpen] = useState(false);

  useEffect(() => {
    const token = localStorage.getItem('access_token');
    if (!token) { router.push('/login'); return; }
    loadData();
  }, []);

  const loadData = async () => {
    try {
      const [payoutsRes, portfolioRes] = await Promise.allSettled([
        api.get('/portfolio/payouts'),
        api.get('/portfolio'),
      ]);
      if (payoutsRes.status === 'fulfilled' && payoutsRes.value.data.success)
        setPayouts(payoutsRes.value.data.data || []);
      if (portfolioRes.status === 'fulfilled' && portfolioRes.value.data.success)
        setInvestments(portfolioRes.value.data.data || []);
    } catch {
      toast.error('Failed to load statement data');
    } finally {
      setLoading(false);
    }
  };

  const filteredPayouts = payouts.filter(p => {
    const d = new Date(p.paidAt);
    if (dateFrom && d < new Date(dateFrom)) return false;
    if (dateTo && d > new Date(dateTo)) return false;
    return true;
  });

  const filteredInvestments = investments.filter(i => {
    const d = new Date(i.createdAt);
    if (dateFrom && d < new Date(dateFrom)) return false;
    if (dateTo && d > new Date(dateTo)) return false;
    return true;
  });

  const totalReceived = filteredPayouts.reduce((s, p) => s + p.totalEtb, 0);
  const totalReturns = filteredPayouts.reduce((s, p) => s + p.returnEtb, 0);
  const totalInvested = filteredInvestments.reduce((s, i) => s + i.amountEtb, 0);

  const generatePdf = async () => {
    setGenerating(true);
    try {
      const { default: jsPDF } = await import('jspdf');
      const doc = new jsPDF();
      const now = new Date().toLocaleDateString();

      // Header Banner
      doc.setFillColor(15, 23, 42); // slate-900 matching theme
      doc.rect(0, 0, 210, 35, 'F');
      doc.setTextColor(255, 255, 255);
      doc.setFont('helvetica', 'bold');
      doc.setFontSize(20);
      doc.text('Agri-Yield Investment Statement', 15, 20);
      doc.setFont('helvetica', 'normal');
      doc.setFontSize(10);
      doc.text(`Generated: ${now}`, 15, 30);

      // Period Selection
      doc.setTextColor(100, 116, 139);
      doc.setFontSize(11);
      doc.text(`Period: ${dateFrom || 'All time'} — ${dateTo || 'Present'}`, 15, 48);

      // Summary Metric Layout
      doc.setFontSize(12);
      doc.setFont('helvetica', 'bold');
      doc.setTextColor(4, 120, 87); // emerald-700
      doc.text('Financial Summary Matrix', 15, 62);
      doc.setTextColor(51, 65, 85);
      doc.setFont('helvetica', 'normal');
      doc.setFontSize(10);
      doc.text(`Total Capital Invested: ${totalInvested.toLocaleString()} ETB`, 15, 72);
      doc.text(`Total Capital Received: ${totalReceived.toLocaleString()} ETB`, 15, 80);
      doc.text(`Total Net Profits Earned: ${totalReturns.toLocaleString()} ETB`, 15, 88);
      doc.text(`Active Allocations Count: ${filteredInvestments.length}`, 15, 96);
      doc.text(`Processed Settlements Count: ${filteredPayouts.length}`, 15, 104);

      // Investments Table Block
      if (filteredInvestments.length > 0) {
        doc.setFontSize(12);
        doc.setFont('helvetica', 'bold');
        doc.setTextColor(4, 120, 87);
        doc.text('Capital Allocation Ledger', 15, 118);
        doc.setTextColor(148, 163, 184);
        doc.setFontSize(9);
        doc.text('Date', 15, 128);
        doc.text('Crop / Region', 45, 128);
        doc.text('Amount (ETB)', 110, 128);
        doc.text('Expected APR', 150, 128);
        doc.text('Status', 178, 128);
        doc.setDrawColor(226, 232, 240);
        doc.line(15, 130, 195, 130);

        doc.setTextColor(51, 65, 85);
        doc.setFont('helvetica', 'normal');
        filteredInvestments.slice(0, 20).forEach((inv, i) => {
          const y = 136 + i * 8;
          if (y > 270) return;
          doc.text(new Date(inv.createdAt).toLocaleDateString(), 15, y);
          doc.text(`${inv.cropType} — ${inv.region}`.slice(0, 28), 45, y);
          doc.text(inv.amountEtb.toLocaleString(), 110, y);
          doc.text(`${inv.expectedReturnPct}%`, 150, y);
          doc.text(inv.status, 178, y);
        });
      }

      // Payouts Ledger Table Block
      if (filteredPayouts.length > 0) {
        doc.addPage();
        doc.setFillColor(15, 23, 42);
        doc.rect(0, 0, 210, 18, 'F');
        doc.setTextColor(255, 255, 255);
        doc.setFontSize(12);
        doc.setFont('helvetica', 'bold');
        doc.text('Harvest Settlement History Matrix', 15, 12);

        doc.setTextColor(148, 163, 184);
        doc.setFontSize(9);
        doc.text('Settlement Date', 15, 28);
        doc.text('Principal (ETB)', 50, 28);
        doc.text('Return (ETB)', 95, 28);
        doc.text('Total Disbursed', 135, 28);
        doc.text('Yield APR', 172, 28);
        doc.setDrawColor(226, 232, 240);
        doc.line(15, 30, 195, 30);

        doc.setTextColor(51, 65, 85);
        doc.setFont('helvetica', 'normal');
        filteredPayouts.forEach((p, i) => {
          const y = 36 + i * 8;
          if (y > 270) return;
          doc.text(new Date(p.paidAt).toLocaleDateString(), 15, y);
          doc.text(p.principalEtb.toLocaleString(), 50, y);
          doc.text(p.returnEtb.toLocaleString(), 95, y);
          doc.text(p.totalEtb.toLocaleString(), 135, y);
          doc.text(`${p.actualApr}%`, 172, y);
        });
      }

      // Universal Running Footer Wrap
      const pageCount = doc.getNumberOfPages();
      for (let i = 1; i <= pageCount; i++) {
        doc.setPage(i);
        doc.setFontSize(8);
        doc.setTextColor(148, 163, 184);
        doc.text('Agri-Yield — Ethiopia Agricultural Investment Platform — Confidential Financial Document', 15, 290);
        doc.text(`Page ${i} of ${pageCount}`, 180, 290);
      }

      doc.save(`agri-yield-statement-${now.replace(/\//g, '-')}.pdf`);
      toast.success('Statement downloaded!');
    } catch (err) {
      console.error(err);
      toast.error('Failed to generate PDF');
    } finally {
      setGenerating(false);
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
          Loading accounting matrix...
        </motion.p>
      </div>
  );

  return (
      <div className="min-h-screen bg-gradient-to-br from-[#f8fafc] to-[#f1f5f9] text-slate-800 font-sans antialiased flex flex-col">
        <Navbar onMenuClick={() => setIsSidebarOpen(!isSidebarOpen)} />

        <div className="flex flex-1 relative">
          <Sidebar isOpen={isSidebarOpen} setIsOpen={setIsSidebarOpen} />

          <main className="flex-1 min-w-0 overflow-y-auto px-4 sm:px-8 py-8 lg:max-w-6xl xl:max-w-7xl mx-auto w-full">
            <motion.div
                variants={pageFadeIn}
                initial="hidden"
                animate="show"
                className="space-y-6"
            >
              {/* Top Operational Header */}
              <div className="mb-2">
                <h1 className="text-2xl sm:text-3xl font-black tracking-tight text-slate-900">Investment Statements</h1>
                <p className="text-slate-500 text-xs sm:text-sm font-medium">Download authenticated PDF financial reports by date range</p>
              </div>

              {/* Date Range Selection Filter Panel */}
              <motion.div variants={blockVariants} className="bg-white rounded-3xl shadow-xs border border-slate-200/60 p-5 sm:p-6">
                <h3 className="text-xs font-bold uppercase text-slate-400 tracking-wider mb-4">Filter Statement Period</h3>
                <div className="flex flex-wrap gap-4 items-end">
                  <div className="w-full sm:w-auto space-y-1.5">
                    <label className="block text-[10px] font-bold uppercase text-slate-400 tracking-wider">From Genesis</label>
                    <input
                        type="date"
                        value={dateFrom}
                        onChange={e => setDateFrom(e.target.value)}
                        className="w-full sm:w-48 px-4 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-xs font-medium focus:outline-none focus:ring-2 focus:ring-emerald-600/20 focus:border-emerald-600 transition"
                    />
                  </div>
                  <div className="w-full sm:w-auto space-y-1.5">
                    <label className="block text-[10px] font-bold uppercase text-slate-400 tracking-wider">To Termination</label>
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
                  <button
                      onClick={generatePdf}
                      disabled={generating}
                      className="w-full sm:w-auto flex items-center justify-center gap-2 bg-slate-900 text-white px-6 py-2.5 rounded-xl text-xs font-bold uppercase tracking-wide hover:bg-slate-800 transition disabled:opacity-50 shadow-xs sm:ml-auto"
                  >
                    {generating ? (
                        <>
                          <Loader2 className="animate-spin h-3.5 w-3.5" />
                          <span>Compiling Document...</span>
                        </>
                    ) : (
                        <>
                          <ArrowDownToLine className="w-3.5 h-3.5" />
                          <span>Download PDF Statement</span>
                        </>
                    )}
                  </button>
                </div>
              </motion.div>

              {/* Comprehensive Metrics Grid */}
              <motion.div variants={blockVariants} className="grid grid-cols-2 md:grid-cols-4 gap-4">
                {[
                  { label: 'Total Invested Capital', value: `${totalInvested.toLocaleString()} ETB`, color: 'text-slate-900 bg-white border-slate-200/60', icon: <Coins className="w-4 h-4 text-slate-400" /> },
                  { label: 'Total Liquid Disbursals', value: `${totalReceived.toLocaleString()} ETB`, color: 'text-emerald-800 bg-gradient-to-br from-emerald-50/60 to-teal-50/20 border-emerald-100/80 font-black', icon: <TrendingUp className="w-4 h-4 text-emerald-600" /> },
                  { label: 'Compounded Revenue Yield', value: `+${totalReturns.toLocaleString()} ETB`, color: 'text-slate-700 bg-white border-slate-200/60', icon: <ShieldCheck className="w-4 h-4 text-slate-400" /> },
                  { label: 'Consolidated Nodes Ledger', value: filteredInvestments.length + filteredPayouts.length, color: 'text-amber-700 bg-white border-slate-200/60', icon: <Layers className="w-4 h-4 text-amber-500" /> },
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

              {/* Investments Primary Datatable */}
              <motion.div variants={blockVariants} className="bg-white rounded-3xl shadow-xs border border-slate-200/60 overflow-hidden">
                <div className="px-6 py-4 bg-slate-50/50 border-b border-slate-100">
                  <h3 className="text-xs font-bold uppercase text-slate-400 tracking-wider">Active Asset Allocations ({filteredInvestments.length})</h3>
                </div>
                <AnimatePresence mode="popLayout">
                  {filteredInvestments.length === 0 ? (
                      <div className="p-12 text-center text-xs font-bold uppercase tracking-wider text-slate-400">No active assets registered within specified parameters</div>
                  ) : (
                      <div className="overflow-x-auto">
                        <table className="w-full text-left text-xs border-collapse">
                          <thead className="bg-slate-50 border-b border-slate-200/60 text-[10px] font-bold uppercase text-slate-400 tracking-wider">
                          <tr>
                            {['Date', 'Crop / Region', 'Season', 'Amount (ETB)', 'Expected APR', 'Actual APR', 'Status'].map(h => (
                                <th key={h} className="px-6 py-3.5 font-bold">{h}</th>
                            ))}
                          </tr>
                          </thead>
                          <tbody className="divide-y divide-slate-100 font-medium text-slate-600">
                          {filteredInvestments.map(inv => (
                              <tr key={inv.id} className="hover:bg-slate-50/40 transition-colors">
                                <td className="px-6 py-4 font-mono text-slate-400">{new Date(inv.createdAt).toLocaleDateString()}</td>
                                <td className="px-6 py-4 font-bold text-slate-900">{inv.cropType} &middot; {inv.region}</td>
                                <td className="px-6 py-4 text-slate-400">{inv.seasonName}</td>
                                <td className="px-6 py-4 font-black text-slate-800">{inv.amountEtb.toLocaleString()}</td>
                                <td className="px-6 py-4 text-emerald-700 font-bold">{inv.expectedReturnPct}%</td>
                                <td className="px-6 py-4 font-mono text-slate-400">{inv.actualReturnPct ? `${inv.actualReturnPct}%` : '—'}</td>
                                <td className="px-6 py-4">
                              <span className={`text-[10px] font-bold uppercase tracking-wider px-2.5 py-1 rounded-full border ${
                                  inv.status === 'COMPLETED' ? 'bg-emerald-50 text-emerald-700 border-emerald-100/70' :
                                      inv.status === 'ACTIVE' ? 'bg-amber-50 text-amber-700 border-amber-100/70' :
                                          'bg-slate-50 text-slate-500 border-slate-200/70'
                              }`}>{inv.status}</span>
                                </td>
                              </tr>
                          ))}
                          </tbody>
                        </table>
                      </div>
                  )}
                </AnimatePresence>
              </motion.div>

              {/* Payouts Disbursal History Datatable */}
              <motion.div variants={blockVariants} className="bg-white rounded-3xl shadow-xs border border-slate-200/60 overflow-hidden">
                <div className="px-6 py-4 bg-slate-50/50 border-b border-slate-100">
                  <h3 className="text-xs font-bold uppercase text-slate-400 tracking-wider">Settlement Disbursal Matrix ({filteredPayouts.length})</h3>
                </div>
                <AnimatePresence mode="popLayout">
                  {filteredPayouts.length === 0 ? (
                      <div className="p-12 text-center text-xs font-bold uppercase tracking-wider text-slate-400">No payout liquidations processed within specified parameters</div>
                  ) : (
                      <div className="overflow-x-auto">
                        <table className="w-full text-left text-xs border-collapse">
                          <thead className="bg-slate-50 border-b border-slate-200/60 text-[10px] font-bold uppercase text-slate-400 tracking-wider">
                          <tr>
                            {['Disbursal Date', 'Farm Target Node', 'Principal (ETB)', 'Net Return (ETB)', 'Total (ETB)', 'Yield APR', 'Settlement Context'].map(h => (
                                <th key={h} className="px-6 py-3.5 font-bold">{h}</th>
                            ))}
                          </tr>
                          </thead>
                          <tbody className="divide-y divide-slate-100 font-medium text-slate-600">
                          {filteredPayouts.map(p => (
                              <tr key={p.id} className="hover:bg-slate-50/40 transition-colors">
                                <td className="px-6 py-4 font-mono text-slate-400">{new Date(p.paidAt).toLocaleDateString()}</td>
                                <td className="px-6 py-4 font-mono text-slate-400 bg-slate-50/30">Node: {p.farmId.slice(0, 8)}…</td>
                                <td className="px-6 py-4 font-semibold text-slate-700">{p.principalEtb.toLocaleString()}</td>
                                <td className="px-6 py-4 font-black text-emerald-700">+{p.returnEtb.toLocaleString()}</td>
                                <td className="px-6 py-4 font-black text-slate-900">{p.totalEtb.toLocaleString()}</td>
                                <td className="px-6 py-4 text-amber-700 font-bold">{p.actualApr}%</td>
                                <td className="px-6 py-4 text-slate-400 text-[11px] truncate max-w-xs">{p.payoutReason || 'Harvest Settlement Disbursal'}</td>
                              </tr>
                          ))}
                          </tbody>
                        </table>
                      </div>
                  )}
                </AnimatePresence>
              </motion.div>
            </motion.div>
          </main>
        </div>
      </div>
  );
}
'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import toast from 'react-hot-toast';
import api from '@/lib/api';
import Navbar from '@/components/Navbar';
import { PayoutRecord, Investment } from '@/lib/types';

export default function StatementsPage() {
  const router = useRouter();
  const [payouts, setPayouts] = useState<PayoutRecord[]>([]);
  const [investments, setInvestments] = useState<Investment[]>([]);
  const [loading, setLoading] = useState(true);
  const [generating, setGenerating] = useState(false);
  const [dateFrom, setDateFrom] = useState('');
  const [dateTo, setDateTo] = useState('');

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
    } catch { toast.error('Failed to load statement data'); }
    finally { setLoading(false); }
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

      // Header
      doc.setFillColor(21, 128, 61);
      doc.rect(0, 0, 210, 35, 'F');
      doc.setTextColor(255, 255, 255);
      doc.setFontSize(20);
      doc.text('Agri-Yield Investment Statement', 15, 20);
      doc.setFontSize(10);
      doc.text(`Generated: ${now}`, 15, 30);

      // Period
      doc.setTextColor(50, 50, 50);
      doc.setFontSize(12);
      doc.text(`Period: ${dateFrom || 'All time'} — ${dateTo || 'Present'}`, 15, 48);

      // Summary
      doc.setFontSize(11);
      doc.setTextColor(21, 128, 61);
      doc.text('Summary', 15, 62);
      doc.setTextColor(50, 50, 50);
      doc.setFontSize(10);
      doc.text(`Total Invested: ${totalInvested.toLocaleString()} ETB`, 15, 72);
      doc.text(`Total Received: ${totalReceived.toLocaleString()} ETB`, 15, 80);
      doc.text(`Total Returns: ${totalReturns.toLocaleString()} ETB`, 15, 88);
      doc.text(`Investments Count: ${filteredInvestments.length}`, 15, 96);
      doc.text(`Payouts Count: ${filteredPayouts.length}`, 15, 104);

      // Investments table
      if (filteredInvestments.length > 0) {
        doc.setFontSize(11);
        doc.setTextColor(21, 128, 61);
        doc.text('Investments', 15, 118);
        doc.setTextColor(100, 100, 100);
        doc.setFontSize(9);
        doc.text('Date', 15, 128); doc.text('Crop / Region', 45, 128);
        doc.text('Amount (ETB)', 110, 128); doc.text('APR %', 155, 128); doc.text('Status', 175, 128);
        doc.setDrawColor(200, 200, 200);
        doc.line(15, 130, 195, 130);

        doc.setTextColor(50, 50, 50);
        filteredInvestments.slice(0, 20).forEach((inv, i) => {
          const y = 136 + i * 8;
          if (y > 270) return;
          doc.text(new Date(inv.createdAt).toLocaleDateString(), 15, y);
          doc.text(`${inv.cropType} — ${inv.region}`.slice(0, 28), 45, y);
          doc.text(inv.amountEtb.toLocaleString(), 110, y);
          doc.text(`${inv.expectedReturnPct}%`, 155, y);
          doc.text(inv.status, 175, y);
        });
      }

      // Payouts on page 2
      if (filteredPayouts.length > 0) {
        doc.addPage();
        doc.setFillColor(21, 128, 61);
        doc.rect(0, 0, 210, 18, 'F');
        doc.setTextColor(255, 255, 255);
        doc.setFontSize(13);
        doc.text('Payout History', 15, 13);

        doc.setTextColor(100, 100, 100);
        doc.setFontSize(9);
        doc.text('Date', 15, 28); doc.text('Principal (ETB)', 50, 28);
        doc.text('Return (ETB)', 95, 28); doc.text('Total (ETB)', 135, 28);
        doc.text('APR %', 165, 28); doc.text('Reason', 180, 28);
        doc.line(15, 30, 195, 30);

        doc.setTextColor(50, 50, 50);
        filteredPayouts.forEach((p, i) => {
          const y = 36 + i * 8;
          if (y > 270) return;
          doc.text(new Date(p.paidAt).toLocaleDateString(), 15, y);
          doc.text(p.principalEtb.toLocaleString(), 50, y);
          doc.text(p.returnEtb.toLocaleString(), 95, y);
          doc.text(p.totalEtb.toLocaleString(), 135, y);
          doc.text(`${p.actualApr}%`, 165, y);
          doc.text((p.payoutReason || '—').slice(0, 12), 180, y);
        });
      }

      // Footer
      const pageCount = doc.getNumberOfPages();
      for (let i = 1; i <= pageCount; i++) {
        doc.setPage(i);
        doc.setFontSize(8);
        doc.setTextColor(150, 150, 150);
        doc.text('Agri-Yield — Ethiopia Agricultural Investment Platform — Confidential', 15, 290);
        doc.text(`Page ${i} of ${pageCount}`, 180, 290);
      }

      doc.save(`agri-yield-statement-${now.replace(/\//g, '-')}.pdf`);
      toast.success('Statement downloaded!');
    } catch (err) {
      console.error(err);
      toast.error('Failed to generate PDF');
    } finally { setGenerating(false); }
  };

  if (loading) return (
    <div className="min-h-screen flex items-center justify-center">
      <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-green-600" />
    </div>
  );

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />
      <div className="container mx-auto px-6 py-8 max-w-5xl">

        <div className="mb-6">
          <h1 className="text-2xl font-bold text-gray-900">Investment Statements</h1>
          <p className="text-gray-500 mt-1">Download PDF reports by date range</p>
        </div>

        {/* Date range filter */}
        <div className="bg-white rounded-2xl shadow-sm border border-gray-100 p-6 mb-6">
          <h3 className="font-bold text-gray-700 mb-4">Filter Period</h3>
          <div className="flex flex-wrap gap-4 items-end">
            <div>
              <label className="block text-xs font-semibold text-gray-500 mb-1 uppercase">From</label>
              <input type="date" value={dateFrom} onChange={e => setDateFrom(e.target.value)}
                className="px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-green-500" />
            </div>
            <div>
              <label className="block text-xs font-semibold text-gray-500 mb-1 uppercase">To</label>
              <input type="date" value={dateTo} onChange={e => setDateTo(e.target.value)}
                className="px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-green-500" />
            </div>
            <button onClick={() => { setDateFrom(''); setDateTo(''); }}
              className="px-4 py-2.5 bg-gray-100 text-gray-600 rounded-xl text-sm font-semibold hover:bg-gray-200 transition">
              Clear
            </button>
            <button onClick={generatePdf} disabled={generating}
              className="flex items-center gap-2 bg-green-700 text-white px-6 py-2.5 rounded-xl text-sm font-bold hover:bg-green-600 transition disabled:opacity-50 shadow-sm ml-auto">
              {generating ? (
                <><span className="animate-spin h-4 w-4 border-2 border-white border-t-transparent rounded-full" /> Generating...</>
              ) : (
                <>📄 Download PDF</>
              )}
            </button>
          </div>
        </div>

        {/* Summary stats */}
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-6">
          {[
            { label: 'Total Invested', value: `${totalInvested.toLocaleString()} ETB`, color: 'text-blue-600' },
            { label: 'Total Received', value: `${totalReceived.toLocaleString()} ETB`, color: 'text-green-600' },
            { label: 'Total Returns', value: `+${totalReturns.toLocaleString()} ETB`, color: 'text-purple-600' },
            { label: 'Transactions', value: filteredInvestments.length + filteredPayouts.length, color: 'text-gray-700' },
          ].map(s => (
            <div key={s.label} className="bg-white rounded-2xl shadow-sm border border-gray-100 p-4">
              <p className="text-gray-400 text-xs uppercase tracking-wide font-medium">{s.label}</p>
              <p className={`text-xl font-bold ${s.color} mt-1`}>{s.value}</p>
            </div>
          ))}
        </div>

        {/* Investments table — SRS: tax-relevant fields */}
        <div className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden mb-6">
          <div className="px-6 py-4 border-b border-gray-100">
            <h3 className="font-bold text-gray-800">Investments ({filteredInvestments.length})</h3>
          </div>
          {filteredInvestments.length === 0 ? (
            <div className="p-8 text-center text-gray-400">No investments in this period</div>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full text-sm">
                <thead className="bg-gray-50 border-b border-gray-100">
                  <tr>
                    {['Date', 'Crop / Region', 'Season', 'Amount (ETB)', 'Expected APR', 'Actual APR', 'Status'].map(h => (
                      <th key={h} className="text-left px-4 py-3 text-xs font-semibold text-gray-500 uppercase tracking-wide">{h}</th>
                    ))}
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-50">
                  {filteredInvestments.map(inv => (
                    <tr key={inv.id} className="hover:bg-gray-50/50">
                      <td className="px-4 py-3 text-gray-500">{new Date(inv.createdAt).toLocaleDateString()}</td>
                      <td className="px-4 py-3 font-medium text-gray-800">{inv.cropType} — {inv.region}</td>
                      <td className="px-4 py-3 text-gray-500">{inv.seasonName}</td>
                      <td className="px-4 py-3 font-bold text-blue-600">{inv.amountEtb.toLocaleString()}</td>
                      <td className="px-4 py-3 text-green-600 font-medium">{inv.expectedReturnPct}%</td>
                      <td className="px-4 py-3 text-gray-500">{inv.actualReturnPct ? `${inv.actualReturnPct}%` : '—'}</td>
                      <td className="px-4 py-3">
                        <span className={`text-xs font-semibold px-2 py-0.5 rounded-full ${
                          inv.status === 'COMPLETED' ? 'bg-green-100 text-green-700' :
                          inv.status === 'ACTIVE' ? 'bg-blue-100 text-blue-700' :
                          'bg-gray-100 text-gray-600'
                        }`}>{inv.status}</span>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>

        {/* Payouts table */}
        <div className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
          <div className="px-6 py-4 border-b border-gray-100">
            <h3 className="font-bold text-gray-800">Payouts Received ({filteredPayouts.length})</h3>
          </div>
          {filteredPayouts.length === 0 ? (
            <div className="p-8 text-center text-gray-400">No payouts in this period</div>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full text-sm">
                <thead className="bg-gray-50 border-b border-gray-100">
                  <tr>
                    {['Date', 'Farm', 'Principal (ETB)', 'Return (ETB)', 'Total (ETB)', 'Actual APR', 'Reason'].map(h => (
                      <th key={h} className="text-left px-4 py-3 text-xs font-semibold text-gray-500 uppercase tracking-wide">{h}</th>
                    ))}
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-50">
                  {filteredPayouts.map(p => (
                    <tr key={p.id} className="hover:bg-gray-50/50">
                      <td className="px-4 py-3 text-gray-500">{new Date(p.paidAt).toLocaleDateString()}</td>
                      <td className="px-4 py-3 font-mono text-xs text-gray-400">{p.farmId.slice(0, 8)}…</td>
                      <td className="px-4 py-3 text-blue-600 font-medium">{p.principalEtb.toLocaleString()}</td>
                      <td className="px-4 py-3 text-green-600 font-bold">+{p.returnEtb.toLocaleString()}</td>
                      <td className="px-4 py-3 font-bold text-gray-800">{p.totalEtb.toLocaleString()}</td>
                      <td className="px-4 py-3 text-purple-600 font-medium">{p.actualApr}%</td>
                      <td className="px-4 py-3 text-gray-500 text-xs">{p.payoutReason || '—'}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

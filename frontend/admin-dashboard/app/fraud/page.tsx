'use client';

import { useEffect, useState, useCallback } from 'react';
import { useRouter } from 'next/navigation';
import toast from 'react-hot-toast';
import api from '@/lib/api';
import { FraudAlert, FraudSeverity, FraudStats } from '@/lib/types';
import FraudSeverityBadge from '@/components/FraudSeverityBadge';
import FraudAlertTypeBadge from '@/components/FraudAlertTypeBadge';

const SEVERITY_FILTERS: { label: string; value: string }[] = [
  { label: 'All Alerts', value: 'ALL' },
  { label: 'Critical',   value: 'CRITICAL' },
  { label: 'High',       value: 'HIGH' },
  { label: 'Medium',     value: 'MEDIUM' },
  { label: 'Low',        value: 'LOW' },
];

export default function FraudAuditPage() {
  const router = useRouter();
  const [alerts, setAlerts]           = useState<FraudAlert[]>([]);
  const [stats, setStats]             = useState<FraudStats | null>(null);
  const [loading, setLoading]         = useState(true);
  const [severityFilter, setSeverity] = useState('ALL');
  const [unresolvedOnly, setUnresolved] = useState(false);
  const [page, setPage]               = useState(0);
  const [totalPages, setTotalPages]   = useState(1);
  const [resolveModal, setResolveModal] = useState<FraudAlert | null>(null);
  const [resolveNotes, setResolveNotes] = useState('');
  const [resolving, setResolving]     = useState(false);

  const PAGE_SIZE = 20;

  const fetchAlerts = useCallback(async () => {
    setLoading(true);
    try {
      const params: Record<string, string | number | boolean> = {
        page,
        size: PAGE_SIZE,
        unresolvedOnly,
      };
      if (severityFilter !== 'ALL') params.severity = severityFilter;

      const res = await api.get('/admin/fraud-audit', { params });
      if (res.data.success) {
        const data = res.data.data;
        if (data.content) {
          setAlerts(data.content);
          setTotalPages(data.totalPages ?? 1);
        } else {
          setAlerts(data);
          setTotalPages(1);
        }
      }
    } catch {
      toast.error('Failed to load fraud alerts');
    } finally {
      setLoading(false);
    }
  }, [severityFilter, unresolvedOnly, page]);

  const fetchStats = useCallback(async () => {
    try {
      const res = await api.get('/admin/fraud-audit', {
        params: { page: 0, size: 1 }
      });
      // Derive stats from available data if no dedicated endpoint
      if (res.data.success) {
        const all: FraudAlert[] = Array.isArray(res.data.data)
          ? res.data.data
          : res.data.data.content ?? [];
        setStats({
          totalAlerts:    all.length,
          unresolvedAlerts: all.filter(a => !a.resolved).length,
          criticalAlerts:   all.filter(a => a.severity === 'CRITICAL').length,
          highAlerts:       all.filter(a => a.severity === 'HIGH').length,
          resolvedToday:    all.filter(a =>
            a.resolved && a.resolvedAt &&
            new Date(a.resolvedAt).toDateString() === new Date().toDateString()
          ).length,
        });
      }
    } catch { /* silent */ }
  }, []);

  useEffect(() => {
    const token = localStorage.getItem('access_token');
    if (!token) { router.push('/login'); return; }
    fetchStats();
  }, [fetchStats, router]);

  useEffect(() => { fetchAlerts(); }, [fetchAlerts]);
  useEffect(() => { setPage(0); }, [severityFilter, unresolvedOnly]);

  async function handleResolve() {
    if (!resolveModal || !resolveNotes.trim()) {
      toast.error('Resolution notes are required');
      return;
    }
    setResolving(true);
    try {
      await api.patch(`/admin/fraud-audit/${resolveModal.id}/resolve`, {
        notes: resolveNotes,
      });
      toast.success('Alert resolved');
      setResolveModal(null);
      setResolveNotes('');
      fetchAlerts();
      fetchStats();
    } catch {
      toast.error('Failed to resolve alert');
    } finally {
      setResolving(false);
    }
  }

  const severityCount = (s: FraudSeverity) =>
    alerts.filter(a => a.severity === s).length;

  return (
    <div className="min-h-screen bg-[#F4F7F5] pb-12">
      {/* Header */}
      <header className="bg-white border-b border-slate-200 sticky top-0 z-40">
        <div className="max-w-7xl mx-auto px-4 h-16 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <button
              onClick={() => router.push('/dashboard')}
              className="text-xs font-bold text-slate-500 hover:text-slate-800 flex items-center gap-1.5 transition-colors"
            >
              <svg className="w-4 h-4" fill="none" stroke="currentColor" strokeWidth="2.5" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" d="M19 12H5M12 19l-7-7 7-7" />
              </svg>
              Dashboard
            </button>
            <span className="text-slate-300">|</span>
            <div>
              <h1 className="text-base font-black text-emerald-950 tracking-tight">
                Fraud Audit Log
              </h1>
              <p className="text-xs text-emerald-700/70 font-medium">FR-08 · Admin Only</p>
            </div>
          </div>
          <button
            onClick={() => { fetchAlerts(); fetchStats(); }}
            className="text-xs font-bold text-slate-600 bg-white border border-slate-200 px-4 py-2 rounded-xl hover:bg-slate-50 transition-all flex items-center gap-1.5"
          >
            <svg className="w-3.5 h-3.5" fill="none" stroke="currentColor" strokeWidth="2.5" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
            </svg>
            Refresh
          </button>
        </div>
      </header>

      <main className="max-w-7xl mx-auto px-4 py-6 space-y-6">

        {/* Stats row */}
        {stats && (
          <div className="grid grid-cols-2 md:grid-cols-5 gap-4">
            {[
              { label: 'Total Alerts',    value: stats.totalAlerts,     color: 'text-slate-800' },
              { label: 'Unresolved',      value: stats.unresolvedAlerts, color: 'text-red-600' },
              { label: 'Critical',        value: stats.criticalAlerts,   color: 'text-red-600' },
              { label: 'High',            value: stats.highAlerts,        color: 'text-orange-600' },
              { label: 'Resolved Today',  value: stats.resolvedToday,    color: 'text-emerald-700' },
            ].map((s) => (
              <div key={s.label} className="bg-white rounded-2xl border border-slate-200 shadow-sm p-4">
                <p className="text-[10px] font-bold uppercase tracking-widest text-slate-400 mb-1">
                  {s.label}
                </p>
                <p className={`text-2xl font-black ${s.color}`}>{s.value}</p>
              </div>
            ))}
          </div>
        )}

        {/* Filters */}
        <div className="bg-white border border-slate-200 rounded-2xl p-5 shadow-sm">
          <div className="flex flex-col sm:flex-row gap-4 items-start sm:items-center justify-between">
            <div className="flex flex-wrap gap-2">
              {SEVERITY_FILTERS.map((f) => (
                <button
                  key={f.value}
                  onClick={() => setSeverity(f.value)}
                  className={`px-4 py-2 text-xs font-bold rounded-xl border transition-all ${
                    severityFilter === f.value
                      ? 'bg-[#1B4332] text-white border-[#1B4332]'
                      : 'bg-white text-slate-600 border-slate-200 hover:border-emerald-400'
                  }`}
                >
                  {f.label}
                  {f.value !== 'ALL' && (
                    <span className="ml-1.5 opacity-70">
                      ({severityCount(f.value as FraudSeverity)})
                    </span>
                  )}
                </button>
              ))}
            </div>
            <label className="flex items-center gap-2 cursor-pointer select-none">
              <div
                onClick={() => setUnresolved(!unresolvedOnly)}
                className={`w-10 h-5 rounded-full transition-colors relative ${
                  unresolvedOnly ? 'bg-[#1B4332]' : 'bg-slate-200'
                }`}
              >
                <div className={`absolute top-0.5 w-4 h-4 bg-white rounded-full shadow transition-transform ${
                  unresolvedOnly ? 'translate-x-5' : 'translate-x-0.5'
                }`} />
              </div>
              <span className="text-xs font-bold text-slate-600">Unresolved only</span>
            </label>
          </div>
        </div>

        {/* Alert table */}
        <div className="bg-white border border-slate-200 rounded-2xl overflow-hidden shadow-sm">
          <div className="px-6 py-4 border-b border-slate-100 flex items-center justify-between">
            <div>
              <h2 className="text-sm font-bold text-slate-900">Fraud Alerts</h2>
              <p className="text-xs text-slate-400 mt-0.5">{alerts.length} results</p>
            </div>
            {loading && (
              <div className="w-4 h-4 border-2 border-emerald-600 border-t-transparent rounded-full animate-spin" />
            )}
          </div>

          {!loading && alerts.length === 0 ? (
            <div className="py-16 text-center">
                <p className="text-3xl mb-3"></p>
              <p className="text-sm font-bold text-slate-500">No fraud alerts found</p>
              <p className="text-xs text-slate-400 mt-1">
                {unresolvedOnly ? 'All alerts have been resolved' : 'No alerts match the current filter'}
              </p>
            </div>
          ) : (
            <div className="divide-y divide-slate-50">
              {alerts.map((alert) => (
                <div
                  key={alert.id}
                  className={`px-6 py-4 hover:bg-slate-50/60 transition-colors ${
                    alert.resolved ? 'opacity-60' : ''
                  }`}
                >
                  <div className="flex items-start justify-between gap-4">
                    <div className="flex-1 min-w-0">
                      <div className="flex items-center gap-3 flex-wrap mb-1.5">
                        <FraudSeverityBadge severity={alert.severity} />
                        <FraudAlertTypeBadge type={alert.alertType} />
                        {alert.resolved && (
                          <span className="inline-flex items-center gap-1 px-2 py-0.5 rounded-lg bg-emerald-50 text-emerald-700 text-[10px] font-bold border border-emerald-200">
                            Resolved
                          </span>
                        )}
                      </div>
                      <p className="text-sm font-semibold text-slate-800 truncate">
                        {alert.description}
                      </p>
                      <div className="flex items-center gap-4 mt-1.5 flex-wrap">
                        <span className="text-[11px] text-slate-400 font-mono">
                          {alert.entityType} · {alert.entityId?.slice(0, 8)}...
                        </span>
                        <span className="text-[11px] text-slate-400">
                          {new Date(alert.createdAt).toLocaleString()}
                        </span>
                      </div>
                    </div>
                    <div className="flex items-center gap-2 flex-shrink-0">
                      <button
                        onClick={() => router.push(`/fraud/${alert.id}`)}
                        className="px-3 py-1.5 text-[11px] font-bold border border-slate-200 rounded-xl hover:bg-slate-100 transition-colors text-slate-600"
                      >
                        Details
                      </button>
                      {!alert.resolved && (
                        <button
                          onClick={() => { setResolveModal(alert); setResolveNotes(''); }}
                          className="px-3 py-1.5 text-[11px] font-bold bg-emerald-700 text-white rounded-xl hover:bg-emerald-800 transition-colors"
                        >
                          Resolve
                        </button>
                      )}
                    </div>
                  </div>
                </div>
              ))}
            </div>
          )}

          {/* Pagination */}
          {totalPages > 1 && (
            <div className="px-6 py-4 border-t border-slate-100 flex items-center justify-between">
              <p className="text-xs text-slate-500">Page {page + 1} of {totalPages}</p>
              <div className="flex gap-2">
                <button
                  onClick={() => setPage(p => Math.max(0, p - 1))}
                  disabled={page === 0}
                  className="px-4 py-2 text-xs font-bold border border-slate-200 rounded-xl disabled:opacity-40 hover:bg-slate-50 transition-colors"
                >
                  ← Previous
                </button>
                <button
                  onClick={() => setPage(p => Math.min(totalPages - 1, p + 1))}
                  disabled={page === totalPages - 1}
                  className="px-4 py-2 text-xs font-bold border border-slate-200 rounded-xl disabled:opacity-40 hover:bg-slate-50 transition-colors"
                >
                  Next →
                </button>
              </div>
            </div>
          )}
        </div>
      </main>

      {/* Resolve Modal */}
      {resolveModal && (
        <div className="fixed inset-0 bg-slate-900/40 backdrop-blur-sm flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-2xl border border-slate-200 p-6 w-full max-w-md shadow-xl">
            <h3 className="font-black text-slate-900 mb-1">Resolve Fraud Alert</h3>
            <div className="flex items-center gap-2 mb-4">
              <FraudSeverityBadge severity={resolveModal.severity} />
              <FraudAlertTypeBadge type={resolveModal.alertType} />
            </div>
            <p className="text-sm text-slate-600 mb-4">{resolveModal.description}</p>
            <label className="block text-[10px] font-bold uppercase tracking-widest text-slate-400 mb-2">
              Resolution Notes *
            </label>
            <textarea
              value={resolveNotes}
              onChange={e => setResolveNotes(e.target.value)}
              rows={3}
              placeholder="Describe what action was taken..."
              className="w-full bg-slate-50 border border-slate-200 rounded-xl p-3 text-sm focus:outline-none focus:border-emerald-600 resize-none transition-colors"
            />
            <div className="flex gap-2 mt-4">
              <button
                onClick={() => { setResolveModal(null); setResolveNotes(''); }}
                className="flex-1 py-2.5 border border-slate-200 text-slate-600 rounded-xl text-xs font-bold hover:bg-slate-50 transition-colors"
              >
                Cancel
              </button>
              <button
                onClick={handleResolve}
                disabled={resolving || !resolveNotes.trim()}
                className="flex-1 py-2.5 bg-emerald-700 text-white rounded-xl text-xs font-bold hover:bg-emerald-800 disabled:opacity-50 transition-colors"
              >
                {resolving ? 'Resolving...' : 'Confirm Resolve'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

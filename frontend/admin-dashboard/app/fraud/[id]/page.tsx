'use client';

import { useEffect, useState, useCallback } from 'react';
import { useParams, useRouter } from 'next/navigation';
import toast from 'react-hot-toast';
import api from '@/lib/api';
import { FraudAlert, FraudRiskScore } from '@/lib/types';
import FraudSeverityBadge from '@/components/FraudSeverityBadge';
import FraudAlertTypeBadge from '@/components/FraudAlertTypeBadge';
import FraudRiskScoreBar from '@/components/FraudRiskScoreBar';

export default function FraudAlertDetailPage() {
  const { id } = useParams<{ id: string }>();
  const router  = useRouter();

  const [alert, setAlert]       = useState<FraudAlert | null>(null);
  const [score, setScore]       = useState<FraudRiskScore | null>(null);
  const [loading, setLoading]   = useState(true);
  const [resolveNotes, setNotes] = useState('');
  const [resolving, setResolving] = useState(false);
  const [showResolve, setShowResolve] = useState(false);

  const fetchAlert = useCallback(async () => {
    setLoading(true);
    try {
      const res = await api.get(`/admin/fraud-audit/${id}`);
      if (res.data.success) {
        const a: FraudAlert = res.data.data;
        setAlert(a);
        // Fetch risk score for this entity
        try {
          const scoreRes = await api.get(`/admin/fraud-score/${a.entityId}`, {
            params: { entityType: a.entityType },
          });
          if (scoreRes.data.success) setScore(scoreRes.data.data);
        } catch { /* score may not exist yet */ }
      }
    } catch {
      toast.error('Alert not found');
      router.push('/fraud');
    } finally {
      setLoading(false);
    }
  }, [id, router]);

  useEffect(() => {
    const token = localStorage.getItem('access_token');
    if (!token) { router.push('/login'); return; }
    fetchAlert();
  }, [fetchAlert, router]);

  async function handleResolve() {
    if (!alert || !resolveNotes.trim()) {
      toast.error('Notes are required');
      return;
    }
    setResolving(true);
    try {
      await api.patch(`/admin/fraud-audit/${alert.id}/resolve`, {
        notes: resolveNotes,
      });
      toast.success('Alert resolved');
      setShowResolve(false);
      fetchAlert();
    } catch {
      toast.error('Failed to resolve');
    } finally {
      setResolving(false);
    }
  }

  function parseEvidence(raw: string | null) {
    if (!raw) return null;
    try { return JSON.parse(raw); } catch { return null; }
  }

  if (loading) {
    return (
      <div className="min-h-screen bg-[#F4F7F5] flex items-center justify-center">
        <div className="w-8 h-8 border-2 border-emerald-600 border-t-transparent rounded-full animate-spin" />
      </div>
    );
  }

  if (!alert) return null;

  const evidence = parseEvidence(alert.evidence);

  return (
    <div className="min-h-screen bg-[#F4F7F5] pb-12">
      {/* Header */}
      <header className="bg-white border-b border-slate-200 sticky top-0 z-40">
        <div className="max-w-5xl mx-auto px-4 h-16 flex items-center gap-4">
          <button
            onClick={() => router.push('/fraud')}
            className="text-xs font-bold text-slate-500 hover:text-slate-800 flex items-center gap-1.5 transition-colors"
          >
            <svg className="w-4 h-4" fill="none" stroke="currentColor" strokeWidth="2.5" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" d="M19 12H5M12 19l-7-7 7-7" />
            </svg>
            Fraud Audit
          </button>
          <span className="text-slate-300">|</span>
          <span className="text-sm font-bold text-slate-700">Alert Detail</span>
        </div>
      </header>

      <main className="max-w-5xl mx-auto px-4 py-6 space-y-6">

        {/* Alert header card */}
        <div className="bg-white rounded-2xl border border-slate-200 shadow-sm p-6">
          <div className="flex flex-col sm:flex-row sm:items-start justify-between gap-4">
            <div>
              <div className="flex items-center gap-3 flex-wrap mb-3">
                <FraudSeverityBadge severity={alert.severity} />
                <FraudAlertTypeBadge type={alert.alertType} />
                {alert.resolved && (
                  <span className="inline-flex items-center gap-1 px-2.5 py-1 rounded-xl bg-emerald-50 text-emerald-700 text-[11px] font-bold border border-emerald-200">
                    Resolved
                  </span>
                )}
              </div>
              <p className="text-base font-bold text-slate-900">{alert.description}</p>
              <p className="text-xs text-slate-400 mt-1 font-mono">{alert.id}</p>
            </div>
            {!alert.resolved && (
              <button
                onClick={() => setShowResolve(true)}
                className="px-5 py-2.5 bg-emerald-700 text-white text-xs font-bold rounded-xl hover:bg-emerald-800 transition-colors flex-shrink-0"
              >
                Resolve Alert
              </button>
            )}
          </div>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">

          {/* Alert details */}
          <div className="bg-white rounded-2xl border border-slate-200 shadow-sm p-6 space-y-4">
            <h2 className="text-sm font-bold text-slate-900">Alert Information</h2>

            {[
              { label: 'Alert Type',   value: alert.alertType.replace(/_/g, ' ') },
              { label: 'Severity',     value: alert.severity },
              { label: 'Entity Type',  value: alert.entityType },
              { label: 'Entity ID',    value: alert.entityId ?? '—', mono: true },
              { label: 'Created At',   value: new Date(alert.createdAt).toLocaleString() },
            ].map((row) => (
              <div key={row.label}>
                <p className="text-[10px] font-bold uppercase tracking-widest text-slate-400 mb-1">
                  {row.label}
                </p>
                <p className={`text-sm font-semibold text-slate-800 break-all ${row.mono ? 'font-mono text-xs' : ''}`}>
                  {row.value}
                </p>
              </div>
            ))}

            {/* View entity button */}
            {alert.entityId && (
              <button
                onClick={() => router.push(`/users/${alert.entityId}`)}
                className="w-full mt-2 py-2.5 border border-slate-200 text-slate-700 text-xs font-bold rounded-xl hover:bg-slate-50 transition-colors"
              >
                View Entity Profile →
              </button>
            )}
          </div>

          {/* Evidence */}
          <div className="bg-white rounded-2xl border border-slate-200 shadow-sm p-6">
            <h2 className="text-sm font-bold text-slate-900 mb-4">Evidence</h2>
            {evidence ? (
              <div className="space-y-3">
                {Object.entries(evidence).map(([k, v]) => (
                  <div key={k}>
                    <p className="text-[10px] font-bold uppercase tracking-widest text-slate-400 mb-1">
                      {k.replace(/_/g, ' ')}
                    </p>
                    <p className="text-xs font-mono text-slate-700 bg-slate-50 rounded-xl px-3 py-2 break-all">
                      {String(v)}
                    </p>
                  </div>
                ))}
              </div>
            ) : (
              <div className="bg-slate-50 rounded-xl p-4">
                <p className="text-xs text-slate-400 font-mono break-all">
                  {alert.evidence ?? 'No evidence recorded'}
                </p>
              </div>
            )}
          </div>
        </div>

        {/* Resolution info */}
        {alert.resolved && (
          <div className="bg-emerald-50 border border-emerald-200 rounded-2xl p-6">
            <h2 className="text-sm font-bold text-emerald-900 mb-4">Resolution Record</h2>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              {[
                { label: 'Resolved By', value: alert.resolvedByAdminId ?? '—', mono: true },
                { label: 'Resolved At', value: alert.resolvedAt ? new Date(alert.resolvedAt).toLocaleString() : '—' },
              ].map((row) => (
                <div key={row.label}>
                  <p className="text-[10px] font-bold uppercase tracking-widest text-emerald-700/60 mb-1">
                    {row.label}
                  </p>
                  <p className={`text-sm font-semibold text-emerald-900 break-all ${row.mono ? 'font-mono text-xs' : ''}`}>
                    {row.value}
                  </p>
                </div>
              ))}
              <div className="col-span-full">
                <p className="text-[10px] font-bold uppercase tracking-widest text-emerald-700/60 mb-1">
                  Resolution Notes
                </p>
                <p className="text-sm font-semibold text-emerald-900">
                  {alert.resolutionNotes ?? '—'}
                </p>
              </div>
            </div>
          </div>
        )}

        {/* Risk Score */}
        {score && (
          <div className="bg-white rounded-2xl border border-slate-200 shadow-sm p-6">
            <h2 className="text-sm font-bold text-slate-900 mb-5">
              Entity Fraud Risk Score
            </h2>
            <FraudRiskScoreBar score={score} />
          </div>
        )}

      </main>

      {/* Resolve Modal */}
      {showResolve && (
        <div className="fixed inset-0 bg-slate-900/40 backdrop-blur-sm flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-2xl border border-slate-200 p-6 w-full max-w-md shadow-xl">
            <h3 className="font-black text-slate-900 mb-1">Resolve Alert</h3>
            <p className="text-xs text-slate-500 mb-4">{alert.description}</p>
            <label className="block text-[10px] font-bold uppercase tracking-widest text-slate-400 mb-2">
              Resolution Notes *
            </label>
            <textarea
              value={resolveNotes}
              onChange={e => setNotes(e.target.value)}
              rows={3}
              placeholder="Describe the action taken to resolve this alert..."
              className="w-full bg-slate-50 border border-slate-200 rounded-xl p-3 text-sm focus:outline-none focus:border-emerald-600 resize-none"
            />
            <div className="flex gap-2 mt-4">
              <button
                onClick={() => { setShowResolve(false); setNotes(''); }}
                className="flex-1 py-2.5 border border-slate-200 text-slate-600 rounded-xl text-xs font-bold hover:bg-slate-50"
              >
                Cancel
              </button>
              <button
                onClick={handleResolve}
                disabled={resolving || !resolveNotes.trim()}
                className="flex-1 py-2.5 bg-emerald-700 text-white rounded-xl text-xs font-bold hover:bg-emerald-800 disabled:opacity-50"
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

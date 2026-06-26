'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import api from '@/lib/api';
import { FraudAlert } from '@/lib/types';
import FraudSeverityBadge from './FraudSeverityBadge';

export default function FraudAlertWidget() {
  const router = useRouter();
  const [alerts, setAlerts] = useState<FraudAlert[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    api.get('/admin/fraud-audit', {
      params: { unresolvedOnly: true, size: 5, page: 0 },
    })
      .then(res => {
        if (res.data.success) {
          const data = res.data.data;
          setAlerts(Array.isArray(data) ? data : data.content ?? []);
        }
      })
      .catch(() => {})
      .finally(() => setLoading(false));
  }, []);

  const criticalCount = alerts.filter(a => a.severity === 'CRITICAL').length;
  const highCount     = alerts.filter(a => a.severity === 'HIGH').length;

  return (
    <div className="bg-white border border-slate-200 rounded-2xl overflow-hidden shadow-sm">
      <div className="px-6 py-4 border-b border-slate-100 flex items-center justify-between">
        <div>
          <h2 className="text-sm font-bold text-slate-900">Fraud Alerts</h2>
          <p className="text-xs text-slate-400 mt-0.5">
            {criticalCount > 0 && (
              <span className="text-red-600 font-bold">{criticalCount} critical · </span>
            )}
            {highCount > 0 && (
              <span className="text-orange-600 font-bold">{highCount} high · </span>
            )}
            {alerts.length} unresolved
          </p>
        </div>
        <button
          onClick={() => router.push('/fraud')}
          className="text-xs font-bold text-emerald-700 hover:underline"
        >
          View all →
        </button>
      </div>

      {loading ? (
        <div className="py-8 flex justify-center">
          <div className="w-5 h-5 border-2 border-emerald-600 border-t-transparent rounded-full animate-spin" />
        </div>
      ) : alerts.length === 0 ? (
        <div className="py-10 text-center">
          <p className="text-2xl mb-2"></p>
          <p className="text-xs text-slate-400 font-semibold">No unresolved alerts</p>
        </div>
      ) : (
        <div className="divide-y divide-slate-50">
          {alerts.slice(0, 5).map(alert => (
            <div
              key={alert.id}
              onClick={() => router.push(`/fraud/${alert.id}`)}
              className="px-6 py-3 hover:bg-slate-50 cursor-pointer transition-colors flex items-center gap-3"
            >
              <FraudSeverityBadge severity={alert.severity} showDot={false} />
              <div className="flex-1 min-w-0">
                <p className="text-xs font-semibold text-slate-800 truncate">
                  {alert.description}
                </p>
                <p className="text-[10px] text-slate-400 mt-0.5">
                  {alert.entityType} · {new Date(alert.createdAt).toLocaleDateString()}
                </p>
              </div>
              <svg className="w-4 h-4 text-slate-300 flex-shrink-0" fill="none" stroke="currentColor" strokeWidth="2.5" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" d="M9 5l7 7-7 7" />
              </svg>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

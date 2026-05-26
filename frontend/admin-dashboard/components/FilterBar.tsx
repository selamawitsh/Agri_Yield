'use client';

interface FilterBarProps {
  roleFilter: string;
  statusFilter: string;
  onRoleChange: (role: string) => void;
  onStatusChange: (status: string) => void;
  onRefresh: () => void;
}

const roles = ['ALL', 'FARMER', 'INVESTOR', 'MERCHANT', 'OFF_TAKER', 'ADMIN'];
const statuses = ['ALL', 'ACTIVE', 'SUSPENDED', 'PENDING_VERIFICATION'];

export default function FilterBar({ roleFilter, statusFilter, onRoleChange, onStatusChange, onRefresh }: FilterBarProps) {
  return (
    <div className="flex flex-wrap items-center gap-4">
      <div>
        <label className="block text-xs font-bold text-slate-600 mb-1">Role</label>
        <select value={roleFilter} onChange={e => onRoleChange(e.target.value)}
          className="bg-slate-50 border border-slate-200 text-xs font-bold rounded-xl px-3 py-2.5 focus:outline-none focus:border-emerald-600 focus:bg-white min-w-[140px]">
          {roles.map(r => <option key={r}>{r}</option>)}
        </select>
      </div>
      <div>
        <label className="block text-xs font-bold text-slate-600 mb-1">Account Status</label>
        <select value={statusFilter} onChange={e => onStatusChange(e.target.value)}
          className="bg-slate-50 border border-slate-200 text-xs font-bold rounded-xl px-3 py-2.5 focus:outline-none focus:border-emerald-600 focus:bg-white min-w-[180px]">
          {statuses.map(s => <option key={s} value={s}>{s.replace('_', ' ')}</option>)}
        </select>
      </div>
      <div className="mt-5">
        <button onClick={onRefresh}
          className="inline-flex items-center gap-2 bg-emerald-50 hover:bg-emerald-100 border border-emerald-200 text-emerald-900 text-xs font-bold px-4 py-2.5 rounded-xl transition-all">
          <svg className="w-3.5 h-3.5" fill="none" stroke="currentColor" strokeWidth="2.5" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
          </svg>
          Refresh
        </button>
      </div>
    </div>
  );
}

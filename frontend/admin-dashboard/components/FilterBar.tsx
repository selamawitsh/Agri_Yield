'use client';

interface FilterBarProps {
    roleFilter: string;
    statusFilter: string;
    onRoleChange: (role: string) => void;
    onStatusChange: (status: string) => void;
    onRefresh: () => void;
}

export default function FilterBar({
                                      roleFilter,
                                      statusFilter,
                                      onRoleChange,
                                      onStatusChange,
                                      onRefresh,
                                  }: FilterBarProps) {
    const roles = ['ALL', 'FARMER', 'INVESTOR', 'MERCHANT', 'OFF_TAKER', 'ADMIN'];
    const statuses = ['ALL', 'ACTIVE', 'SUSPENDED', 'PENDING_VERIFICATION'];

    return (
        <div className="bg-white border border-slate-200 rounded-2xl p-5 flex flex-col sm:flex-row sm:items-center justify-between gap-4 shadow-2xs">
            <div className="flex flex-wrap items-center gap-6">
                <div className="space-y-1">
                    <label className="block text-xs font-bold text-emerald-950 tracking-wide">Registry Focus Role</label>
                    <div className="relative">
                        <select
                            value={roleFilter}
                            onChange={(e) => onRoleChange(e.target.value)}
                            className="bg-slate-50 text-slate-900 border border-slate-200 text-xs font-bold rounded-xl pl-3 pr-10 py-2.5 focus:outline-none focus:border-emerald-700 focus:bg-white appearance-none cursor-pointer min-w-[160px] transition-all"
                        >
                            {roles.map((role) => (
                                <option key={role} value={role}>{role}</option>
                            ))}
                        </select>
                        <div className="absolute inset-y-0 right-0 flex items-center pr-3 pointer-events-none text-slate-400">
                            <svg className="w-4 h-4" fill="none" stroke="currentColor" strokeWidth="2.5" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" d="M19 9l-7 7-7-7" /></svg>
                        </div>
                    </div>
                </div>

                <div className="space-y-1">
                    <label className="block text-xs font-bold text-emerald-950 tracking-wide">Validation Status</label>
                    <div className="relative">
                        <select
                            value={statusFilter}
                            onChange={(e) => onStatusChange(e.target.value)}
                            className="bg-slate-50 text-slate-900 border border-slate-200 text-xs font-bold rounded-xl pl-3 pr-10 py-2.5 focus:outline-none focus:border-emerald-700 focus:bg-white appearance-none cursor-pointer min-w-[180px] transition-all"
                        >
                            {statuses.map((status) => (
                                <option key={status} value={status}>{status.replace('_', ' ')}</option>
                            ))}
                        </select>
                        <div className="absolute inset-y-0 right-0 flex items-center pr-3 pointer-events-none text-slate-400">
                            <svg className="w-4 h-4" fill="none" stroke="currentColor" strokeWidth="2.5" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" d="M19 9l-7 7-7-7" /></svg>
                        </div>
                    </div>
                </div>
            </div>

            <button
                onClick={onRefresh}
                className="inline-flex items-center justify-center gap-2 bg-emerald-50 hover:bg-emerald-100 border border-emerald-200 text-emerald-900 text-xs font-bold px-5 py-2.5 rounded-xl transition-all self-end sm:self-auto shadow-2xs"
            >
                <svg className="w-4 h-4" fill="none" stroke="currentColor" strokeWidth="2.5" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" d="M4 4v5h.582m15.356 2A8.001 8.001 0 1121.21 7.89M9 11l3-3 3 3m-3-3v12" />
                </svg>
                Re-Index Map Sector
            </button>
        </div>
    );
}
'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import api from '@/lib/api';
import { User, UserStats } from '@/lib/types';
import StatsCards from '@/components/StatsCards';
import UserTable from '@/components/UserTable';
import FilterBar from '@/components/FilterBar';

export default function DashboardPage() {
  const router = useRouter();
  const [users, setUsers] = useState<User[]>([]);
  const [stats, setStats] = useState<UserStats | null>(null);
  const [loading, setLoading] = useState(true);
  const [roleFilter, setRoleFilter] = useState('ALL');
  const [statusFilter, setStatusFilter] = useState('ALL');

  useEffect(() => {
    fetchStats();
    fetchUsers();
  }, [roleFilter, statusFilter]);

  const fetchStats = async () => {
    try {
      const response = await api.get('/admin/stats');
      if (response.data.success) setStats(response.data.data);
    } catch (e) { console.error(e); }
  };

  const fetchUsers = async () => {
    setLoading(true);
    try {
      const params: any = {};
      if (roleFilter !== 'ALL') params.role = roleFilter;
      if (statusFilter !== 'ALL') params.status = statusFilter;
      const response = await api.get('/admin/users', { params });
      if (response.data.success) setUsers(response.data.data.users);
    } catch (e) { console.error(e); }
    setLoading(false);
  };

  if (loading && users.length === 0) {
    return (
        <div className="min-h-screen flex items-center justify-center bg-slate-50 text-xs font-bold tracking-widest text-emerald-900/60 uppercase">
          Loading Vector Topology Map...
        </div>
    );
  }

  return (
      <div className="min-h-screen bg-[#F4F7F5] pb-12">
        <header className="bg-white border-b border-slate-200 sticky top-0 z-40">
          <div className="max-w-7xl mx-auto px-4 h-20 flex items-center justify-between">
            <div>
              <h1 className="text-xl font-black text-emerald-950 tracking-tight">Yogyakarta Sector</h1>
              <p className="text-xs font-bold text-emerald-700/80">Central Registry Console</p>
            </div>
            <button
                onClick={() => { localStorage.clear(); router.push('/login'); }}
                className="text-xs font-bold text-slate-600 bg-white border border-slate-200 px-4 py-2 rounded-xl hover:bg-slate-50 transition-all shadow-2xs"
            >
              Sign Out Terminal
            </button>
          </div>
        </header>

        <main className="max-w-7xl mx-auto px-4 py-8 space-y-6">
          {stats && <StatsCards stats={stats} />}

          <FilterBar
              roleFilter={roleFilter}
              statusFilter={statusFilter}
              onRoleChange={setRoleFilter}
              onStatusChange={setStatusFilter}
              onRefresh={fetchUsers}
          />

          <div className="bg-white border border-slate-200 rounded-3xl overflow-hidden shadow-xs">
            <div className="px-6 py-5 border-b border-slate-100 bg-white">
              <h2 className="text-sm font-bold text-slate-900">Ecosystem Identity Ledger</h2>
              <p className="text-xs text-slate-400 mt-0.5">Verified sector identities mapped to local plots</p>
            </div>
            <UserTable users={users} onUserUpdated={fetchUsers} />
          </div>
        </main>
      </div>
  );
}
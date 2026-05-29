'use client';
import { useEffect, useState, useCallback } from 'react';
import { useRouter } from 'next/navigation';
import toast from 'react-hot-toast';
import api from '@/lib/api';
import { User, UserStats, PagedResponse } from '@/lib/types';
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
  const [search, setSearch] = useState('');
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [totalElements, setTotalElements] = useState(0);
  const PAGE_SIZE = 20;

  const fetchStats = useCallback(async () => {
    try {
      const res = await api.get('/admin/stats');
      if (res.data.success) setStats(res.data.data);
    } catch { /* silent */ }
  }, []);

  const fetchUsers = useCallback(async () => {
    setLoading(true);
    try {
      const params: Record<string, string | number> = { page, size: PAGE_SIZE };
      if (roleFilter !== 'ALL') params.role = roleFilter;
      if (statusFilter !== 'ALL') params.status = statusFilter;
      if (search.trim()) params.search = search.trim();

      const res = await api.get('/admin/users', { params });
      if (res.data.success) {
        const paged: PagedResponse<User> = res.data.data;
        // Handle both paged and plain array responses
        if (paged.content) {
          setUsers(paged.content);
          setTotalPages(paged.totalPages);
          setTotalElements(paged.totalElements);
        } else {
          setUsers(res.data.data as User[]);
          setTotalPages(1);
          setTotalElements((res.data.data as User[]).length);
        }
      }
    } catch (e: unknown) {
      toast.error('Failed to load users');
      console.error(e);
    } finally {
      setLoading(false);
    }
  }, [roleFilter, statusFilter, search, page]);

  useEffect(() => { fetchStats(); }, [fetchStats]);
  useEffect(() => { fetchUsers(); }, [fetchUsers]);

  // Reset page when filters change
  useEffect(() => { setPage(0); }, [roleFilter, statusFilter, search]);

  function handleLogout() {
    localStorage.removeItem('access_token');
    localStorage.removeItem('refresh_token');
    router.push('/login');
  }

  return (
    <div className="min-h-screen bg-[#F4F7F5] pb-12">
      {/* Header */}
      <header className="bg-white border-b border-slate-200 sticky top-0 z-40">
        <div className="max-w-7xl mx-auto px-4 h-16 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <span className="text-2xl">🌾</span>
            <div>
              <h1 className="text-base font-black text-emerald-950 tracking-tight">Agri-Yield Admin</h1>
              <p className="text-xs text-emerald-700/70 font-medium">User Management</p>
            </div>
          </div>
          <a href="/fraud" className="text-xs font-bold text-slate-600 bg-white border border-slate-200 px-4 py-2 rounded-xl hover:bg-slate-50 transition-all mr-2">🚨 Fraud Alerts</a>
          <button onClick={handleLogout}
            className="text-xs font-bold text-slate-600 bg-white border border-slate-200 px-4 py-2 rounded-xl hover:bg-slate-50 transition-all">
            Sign Out
          </button>
        </div>
      </header>

      <main className="max-w-7xl mx-auto px-4 py-6 space-y-6">
        {/* Stats */}
        {stats && <StatsCards stats={stats} />}

        {/* Filters + Search */}
        <div className="bg-white border border-slate-200 rounded-2xl p-5 shadow-sm">
          <div className="flex flex-col lg:flex-row gap-4 items-start lg:items-center justify-between">
            <div className="flex flex-wrap gap-4 items-center">
              <FilterBar
                roleFilter={roleFilter}
                statusFilter={statusFilter}
                onRoleChange={setRoleFilter}
                onStatusChange={setStatusFilter}
                onRefresh={fetchUsers}
              />
            </div>
            {/* Search */}
            <div className="relative w-full lg:w-72">
              <input
                type="text"
                value={search}
                onChange={e => setSearch(e.target.value)}
                placeholder="Search by phone or Fayda ID..."
                className="w-full bg-slate-50 border border-slate-200 text-sm rounded-xl pl-10 pr-4 py-2.5 focus:outline-none focus:border-emerald-600 focus:bg-white transition-all"
              />
              <svg className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" d="M21 21l-4.35-4.35M17 11A6 6 0 1 1 5 11a6 6 0 0 1 12 0z" />
              </svg>
            </div>
          </div>
        </div>

        {/* User Table */}
        <div className="bg-white border border-slate-200 rounded-2xl overflow-hidden shadow-sm">
          <div className="px-6 py-4 border-b border-slate-100 flex items-center justify-between">
            <div>
              <h2 className="text-sm font-bold text-slate-900">All Users</h2>
              <p className="text-xs text-slate-400 mt-0.5">{totalElements} total users</p>
            </div>
            {loading && (
              <div className="w-4 h-4 border-2 border-emerald-600 border-t-transparent rounded-full animate-spin" />
            )}
          </div>

          <UserTable
            users={users}
            onUserUpdated={fetchUsers}
            onViewDetail={(id) => router.push(`/users/${id}`)}
          />

          {/* Pagination */}
          {totalPages > 1 && (
            <div className="px-6 py-4 border-t border-slate-100 flex items-center justify-between">
              <p className="text-xs text-slate-500">
                Page {page + 1} of {totalPages}
              </p>
              <div className="flex gap-2">
                <button
                  onClick={() => setPage(p => Math.max(0, p - 1))}
                  disabled={page === 0}
                  className="px-4 py-2 text-xs font-bold border border-slate-200 rounded-xl disabled:opacity-40 hover:bg-slate-50 transition-colors">
                  ← Previous
                </button>
                <button
                  onClick={() => setPage(p => Math.min(totalPages - 1, p + 1))}
                  disabled={page === totalPages - 1}
                  className="px-4 py-2 text-xs font-bold border border-slate-200 rounded-xl disabled:opacity-40 hover:bg-slate-50 transition-colors">
                  Next →
                </button>
              </div>
            </div>
          )}
        </div>
      </main>
    </div>
  );
}

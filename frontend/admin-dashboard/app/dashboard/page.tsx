'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import toast from 'react-hot-toast';
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
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  useEffect(() => {
    const token = localStorage.getItem('access_token');
    if (!token) {
      router.push('/login');
      return;
    }
    fetchStats();
    fetchUsers();
  }, [roleFilter, statusFilter, currentPage]);

  const fetchStats = async () => {
    try {
      const response = await api.get('/admin/stats');
      if (response.data.success) {
        setStats(response.data.data);
      }
    } catch (error) {
      console.error('Failed to fetch stats', error);
    }
  };

  const fetchUsers = async () => {
    setLoading(true);
    try {
      const params: any = { page: currentPage, size: 20 };
      if (roleFilter !== 'ALL') params.role = roleFilter;
      if (statusFilter !== 'ALL') params.status = statusFilter;

      const response = await api.get('/admin/users', { params });
      if (response.data.success) {
        setUsers(response.data.data.users);
        setTotalPages(response.data.data.totalPages);
      }
    } catch (error: any) {
      if (error.response?.status === 403) {
        toast.error('Admin access required');
        router.push('/login');
      } else {
        toast.error('Failed to fetch users');
      }
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = () => {
    localStorage.removeItem('access_token');
    localStorage.removeItem('refresh_token');
    router.push('/login');
  };

  if (loading && users.length === 0) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto mb-4"></div>
          <p>Loading...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-100">
      <nav className="bg-white shadow-md sticky top-0 z-10">
        <div className="container mx-auto px-6 py-4">
          <div className="flex justify-between items-center">
            <div>
              <h1 className="text-2xl font-bold">Agri-Yield Admin</h1>
              <p className="text-sm text-gray-500">User Management Dashboard</p>
            </div>
            <button onClick={handleLogout} className="bg-red-600 text-white px-4 py-2 rounded-lg hover:bg-red-700">
              Logout
            </button>
          </div>
        </div>
      </nav>

      <div className="container mx-auto px-6 py-8">
        {stats && <StatsCards stats={stats} />}
        
        <FilterBar
          roleFilter={roleFilter}
          statusFilter={statusFilter}
          onRoleChange={setRoleFilter}
          onStatusChange={setStatusFilter}
          onRefresh={fetchUsers}
        />

        <div className="bg-white rounded-lg shadow overflow-hidden">
          <UserTable users={users} onUserUpdated={fetchUsers} />
        </div>

        {totalPages > 1 && (
          <div className="flex justify-center gap-2 mt-6">
            <button
              onClick={() => setCurrentPage(Math.max(0, currentPage - 1))}
              disabled={currentPage === 0}
              className="px-4 py-2 border rounded-lg disabled:opacity-50"
            >
              Previous
            </button>
            <span className="px-4 py-2">Page {currentPage + 1} of {totalPages}</span>
            <button
              onClick={() => setCurrentPage(Math.min(totalPages - 1, currentPage + 1))}
              disabled={currentPage === totalPages - 1}
              className="px-4 py-2 border rounded-lg disabled:opacity-50"
            >
              Next
            </button>
          </div>
        )}
      </div>
    </div>
  );
}

'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import toast from 'react-hot-toast';
import api from '@/lib/api';
import { User } from '@/lib/types';
import BankAccountManager from '@/components/BankAccountManager';

export default function DashboardPage() {
  const router = useRouter();
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const token = localStorage.getItem('access_token');
    if (!token) {
      router.push('/login');
      return;
    }
    fetchProfile();
  }, []);

  const fetchProfile = async () => {
    try {
      const response = await api.get('/users/me');
      if (response.data.success) {
        setUser(response.data.data);
      }
    } catch (error: any) {
      if (error.response?.status === 401) {
        localStorage.removeItem('access_token');
        router.push('/login');
      } else {
        toast.error('Failed to load profile');
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

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-teal-600"></div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-100">
      <nav className="bg-white shadow-md">
        <div className="container mx-auto px-6 py-4">
          <div className="flex justify-between items-center">
            <h1 className="text-2xl font-bold text-teal-600">Agri-Yield Off-Taker</h1>
            <div className="flex gap-4">
              <Link href="/dashboard" className="text-teal-600 font-semibold">Dashboard</Link>
              <Link href="/profile" className="text-gray-600 hover:text-teal-600">Profile</Link>
              <button onClick={handleLogout} className="bg-red-600 text-white px-4 py-2 rounded-lg">Logout</button>
            </div>
          </div>
        </div>
      </nav>

      <div className="container mx-auto px-6 py-8">
        <div className="bg-gradient-to-r from-teal-600 to-cyan-600 rounded-lg shadow-lg p-6 mb-8 text-white">
          <h2 className="text-2xl font-bold">Welcome, Off-Taker!</h2>
          <p className="text-teal-100 mt-1">Purchase agricultural produce directly from farmers</p>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          <div className="bg-white rounded-lg shadow p-6">
            <h3 className="text-lg font-semibold mb-4">Company Information</h3>
            <div className="space-y-3">
              <div><p className="text-gray-500 text-sm">Phone</p><p className="font-medium">{user?.phone}</p></div>
              <div><p className="text-gray-500 text-sm">Email</p><p className="font-medium">{user?.email || 'Not set'}</p></div>
              <div><p className="text-gray-500 text-sm">KYC Status</p><span className={`px-2 py-1 text-xs rounded-full ${user?.kycStatus === 'VERIFIED' ? 'bg-green-100 text-green-800' : 'bg-yellow-100 text-yellow-800'}`}>{user?.kycStatus}</span></div>
            </div>
            <Link href="/profile" className="mt-4 inline-block text-teal-600 hover:text-teal-700 text-sm">Edit Profile →</Link>
          </div>

          <div className="bg-white rounded-lg shadow p-6">
            <BankAccountManager />
          </div>
        </div>

        <div className="mt-8 bg-white rounded-lg shadow p-6">
          <h3 className="text-lg font-semibold mb-4">Coming Soon</h3>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div className="border rounded-lg p-4 text-center"><div className="text-2xl mb-2">🌾</div><p className="font-medium">Browse Farms</p><p className="text-sm text-gray-500">Find available harvests</p></div>
            <div className="border rounded-lg p-4 text-center"><div className="text-2xl mb-2">💰</div><p className="font-medium">Place Bids</p><p className="text-sm text-gray-500">Bid on farm produce</p></div>
            <div className="border rounded-lg p-4 text-center"><div className="text-2xl mb-2">🚚</div><p className="font-medium">Logistics</p><p className="text-sm text-gray-500">Manage deliveries</p></div>
          </div>
        </div>
      </div>
    </div>
  );
}

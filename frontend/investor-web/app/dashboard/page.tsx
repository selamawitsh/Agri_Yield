'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import toast from 'react-hot-toast';
import api from '@/lib/api';
import Link from 'next/link';

interface UserProfile {
  id: string;
  phone: string;
  email: string;
  role: string;
  kycStatus: string;
  riskTolerance: string;
  investmentGoal: string;
  accountStatus: string;
  bankAccounts?: any[];
}

export default function DashboardPage() {
  const router = useRouter();
  const [profile, setProfile] = useState<UserProfile | null>(null);
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
        setProfile(response.data.data);
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
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-green-600"></div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-100">
      {/* Header */}
      <nav className="bg-white shadow-md">
        <div className="container mx-auto px-6 py-4">
          <div className="flex justify-between items-center">
            <h1 className="text-2xl font-bold text-green-600">Agri-Yield Investor</h1>
            <div className="flex gap-4">
              <Link href="/dashboard" className="text-green-600 font-semibold">Dashboard</Link>
              <Link href="/profile" className="text-gray-600 hover:text-green-600">Profile</Link>
              <button
                onClick={handleLogout}
                className="bg-red-600 text-white px-4 py-2 rounded-lg hover:bg-red-700"
              >
                Logout
              </button>
            </div>
          </div>
        </div>
      </nav>

      <div className="container mx-auto px-6 py-8">
        {/* Welcome Banner */}
        <div className="bg-gradient-to-r from-green-600 to-green-800 rounded-lg shadow-lg p-6 mb-8 text-white">
          <h2 className="text-2xl font-bold">Welcome back, Investor!</h2>
          <p className="text-green-100 mt-1">Your investment dashboard is ready</p>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          {/* Profile Card */}
          <div className="bg-white rounded-lg shadow p-6">
            <h3 className="text-lg font-semibold mb-4">Your Profile</h3>
            <div className="space-y-3">
              <div>
                <p className="text-gray-500 text-sm">Phone</p>
                <p className="font-medium">{profile?.phone}</p>
              </div>
              <div>
                <p className="text-gray-500 text-sm">Role</p>
                <p className="font-medium text-green-600">{profile?.role}</p>
              </div>
              <div>
                <p className="text-gray-500 text-sm">KYC Status</p>
                <span className={`px-2 py-1 text-xs rounded-full ${
                  profile?.kycStatus === 'VERIFIED' 
                    ? 'bg-green-100 text-green-800' 
                    : 'bg-yellow-100 text-yellow-800'
                }`}>
                  {profile?.kycStatus}
                </span>
              </div>
              <div>
                <p className="text-gray-500 text-sm">Account Status</p>
                <span className="px-2 py-1 text-xs rounded-full bg-green-100 text-green-800">
                  {profile?.accountStatus}
                </span>
              </div>
              <div>
                <p className="text-gray-500 text-sm">Risk Tolerance</p>
                <p className="font-medium">{profile?.riskTolerance || 'Not set'}</p>
              </div>
            </div>
            <Link href="/profile" className="mt-4 inline-block text-green-600 hover:text-green-700 text-sm">
              Edit Profile →
            </Link>
          </div>

          {/* Bank Account Card */}
          <div className="bg-white rounded-lg shadow p-6">
            <h3 className="text-lg font-semibold mb-4">Payment Methods</h3>
            {profile?.bankAccounts && profile.bankAccounts.length > 0 ? (
              <div>
                <p className="text-sm text-gray-600">
                  You have {profile.bankAccounts.length} bank account(s) linked.
                </p>
                <Link href="/profile" className="mt-4 inline-block text-green-600 hover:text-green-700 text-sm">
                  Manage Accounts →
                </Link>
              </div>
            ) : (
              <div>
                <p className="text-gray-500 text-sm mb-4">No bank accounts linked yet.</p>
                <Link href="/profile" className="text-green-600 hover:text-green-700 text-sm">
                  + Add Bank Account
                </Link>
              </div>
            )}
          </div>
        </div>

        {/* Coming Soon Section */}
        <div className="mt-8 bg-white rounded-lg shadow p-6">
          <h3 className="text-lg font-semibold mb-4">Coming Soon</h3>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div className="border rounded-lg p-4 text-center">
              <div className="text-2xl mb-2">🌾</div>
              <p className="font-medium">Browse Farms</p>
              <p className="text-sm text-gray-500">Invest in Ethiopian agriculture</p>
            </div>
            <div className="border rounded-lg p-4 text-center">
              <div className="text-2xl mb-2">💰</div>
              <p className="font-medium">Portfolio</p>
              <p className="text-sm text-gray-500">Track your investments</p>
            </div>
            <div className="border rounded-lg p-4 text-center">
              <div className="text-2xl mb-2">📊</div>
              <p className="font-medium">Returns</p>
              <p className="text-sm text-gray-500">View your earnings</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

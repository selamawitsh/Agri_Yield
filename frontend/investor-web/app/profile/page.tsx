'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import toast from 'react-hot-toast';
import api from '@/lib/api';
import Navbar from '@/components/Navbar';
import BankAccountManager from '@/components/BankAccountManager';
import { User } from '@/lib/types';

export default function ProfilePage() {
  const router = useRouter();
  const [profile, setProfile] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);
  const [editing, setEditing] = useState(false);
  const [formData, setFormData] = useState({
    email: '',
    preferredLanguage: 'EN',
    riskTolerance: '',
    investmentGoal: '',
  });

  useEffect(() => {
    const token = localStorage.getItem('access_token');
    if (!token) {
      router.push('/login');
      return;
    }
    fetchProfile();
  }, [router]);

  const fetchProfile = async () => {
    try {
      const response = await api.get('/users/me');
      if (response.data.success) {
        const userData = response.data.data;
        setProfile(userData);
        setFormData({
          email: userData.email || '',
          preferredLanguage: userData.preferredLanguage || 'EN',
          riskTolerance: userData.riskTolerance || '',
          investmentGoal: userData.investmentGoal || '',
        });
      }
    } catch (error) {
      toast.error('Failed to load profile');
    } finally {
      setLoading(false);
    }
  };

  const updateProfile = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const response = await api.patch('/users/me', formData);
      if (response.data.success) {
        setProfile(response.data.data);
        setEditing(false);
        toast.success('Profile updated!');
      }
    } catch (error) {
      toast.error('Failed to update profile');
    }
  };

  if (loading) return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-green-600" />
      </div>
  );

  return (
      <div className="min-h-screen bg-gray-50">
        <Navbar />
        <div className="container mx-auto px-6 py-8 max-w-4xl">
          <h1 className="text-2xl font-bold mb-6">Profile Settings</h1>

          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">

            {/* Profile Card */}
            <div className="bg-white rounded-xl shadow p-6">
              <div className="flex justify-between items-center mb-5">
                <h3 className="text-lg font-semibold">Account Info</h3>
                {!editing && (
                    <button onClick={() => setEditing(true)} className="text-green-600 text-sm hover:underline">
                      Edit
                    </button>
                )}
              </div>

              {!editing ? (
                  <div className="space-y-4">
                    {[
                      { label: 'Phone', value: profile?.phone },
                      { label: 'Email', value: profile?.email || 'Not set' },
                      { label: 'Role', value: profile?.role },
                      { label: 'KYC Status', value: profile?.kycStatus },
                      { label: 'Account Status', value: profile?.accountStatus },
                      { label: 'Risk Tolerance', value: profile?.riskTolerance || 'Not set' },
                      { label: 'Investment Goal', value: profile?.investmentGoal || 'Not set' },
                      { label: 'Language', value: profile?.preferredLanguage },
                    ].map((item) => (
                        <div key={item.label}>
                          <p className="text-xs text-gray-500">{item.label}</p>
                          <p className="font-medium text-sm mt-0.5">{item.value}</p>
                        </div>
                    ))}

                    {/* Investor Stats */}
                    {(profile?.agriScore !== undefined || profile?.totalInvestedEtb !== undefined) && (
                        <div className="border-t pt-4 mt-4">
                          <p className="text-xs font-semibold text-gray-500 mb-3 uppercase">Investor Stats</p>
                          <div className="grid grid-cols-2 gap-3">
                            <div className="bg-green-50 rounded-lg p-3">
                              <p className="text-xs text-gray-500">Agri-Score</p>
                              <p className="font-bold text-green-600">{profile.agriScore || 0}</p>
                            </div>
                            <div className="bg-blue-50 rounded-lg p-3">
                              <p className="text-xs text-gray-500">Total Invested</p>
                              <p className="font-bold text-blue-600">{(profile.totalInvestedEtb || 0).toLocaleString()} ETB</p>
                            </div>
                          </div>
                        </div>
                    )}
                  </div>
              ) : (
                  <form onSubmit={updateProfile} className="space-y-4">
                    <div>
                      <label className="block text-sm text-gray-600 mb-1">Email</label>
                      <input type="email" value={formData.email}
                             onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                             className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-green-500 text-sm" />
                    </div>
                    <div>
                      <label className="block text-sm text-gray-600 mb-1">Language</label>
                      <select value={formData.preferredLanguage}
                              onChange={(e) => setFormData({ ...formData, preferredLanguage: e.target.value })}
                              className="w-full px-3 py-2 border rounded-lg text-sm">
                        <option value="EN">English</option>
                        <option value="AM">Amharic</option>
                        <option value="OM">Oromiffa</option>
                      </select>
                    </div>
                    <div>
                      <label className="block text-sm text-gray-600 mb-1">Risk Tolerance</label>
                      <select value={formData.riskTolerance}
                              onChange={(e) => setFormData({ ...formData, riskTolerance: e.target.value })}
                              className="w-full px-3 py-2 border rounded-lg text-sm">
                        <option value="">Select...</option>
                        <option value="LOW">Low</option>
                        <option value="MODERATE">Moderate</option>
                        <option value="HIGH">High</option>
                      </select>
                    </div>
                    <div>
                      <label className="block text-sm text-gray-600 mb-1">Investment Goal</label>
                      <input type="text" value={formData.investmentGoal}
                             onChange={(e) => setFormData({ ...formData, investmentGoal: e.target.value })}
                             placeholder="e.g., Retirement, Wealth growth"
                             className="w-full px-3 py-2 border rounded-lg text-sm" />
                    </div>
                    <div className="flex gap-3 pt-2">
                      <button type="submit" className="bg-green-600 text-white px-4 py-2 rounded-lg text-sm hover:bg-green-700">
                        Save
                      </button>
                      <button type="button" onClick={() => setEditing(false)}
                              className="bg-gray-200 text-gray-700 px-4 py-2 rounded-lg text-sm hover:bg-gray-300">
                        Cancel
                      </button>
                    </div>
                  </form>
              )}
            </div>

            {/* Bank Accounts */}
            <div className="bg-white rounded-xl shadow p-6">
              <BankAccountManager userId={profile?.id || ''} onAccountUpdate={fetchProfile} />
            </div>
          </div>
        </div>
      </div>
  );
}
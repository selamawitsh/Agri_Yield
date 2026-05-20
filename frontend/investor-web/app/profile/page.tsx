'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import toast from 'react-hot-toast';
import api from '@/lib/api';
import BankAccountManager from '@/components/BankAccountManager';

interface UserProfile {
  id: string;
  phone: string;
  email: string;
  role: string;
  kycStatus: string;
  riskTolerance: string;
  investmentGoal: string;
  preferredLanguage: string;
}

export default function ProfilePage() {
  const router = useRouter();
  const [profile, setProfile] = useState<UserProfile | null>(null);
  const [loading, setLoading] = useState(true);
  const [editing, setEditing] = useState(false);
  const [formData, setFormData] = useState({
    email: '',
    preferredLanguage: 'en',
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
  }, []);

  const fetchProfile = async () => {
    try {
      const response = await api.get('/users/me');
      if (response.data.success) {
        const userData = response.data.data;
        setProfile(userData);
        setFormData({
          email: userData.email || '',
          preferredLanguage: userData.preferredLanguage || 'en',
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
        toast.success('Profile updated successfully');
      }
    } catch (error) {
      toast.error('Failed to update profile');
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
              <button
                onClick={() => router.push('/dashboard')}
                className="text-gray-600 hover:text-green-600"
              >
                Dashboard
              </button>
              <button
                onClick={() => router.push('/profile')}
                className="text-green-600 font-semibold"
              >
                Profile
              </button>
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
        <div className="max-w-4xl mx-auto">
          <h2 className="text-2xl font-bold mb-6">Profile Settings</h2>

          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            {/* Profile Information Card */}
            <div className="bg-white rounded-lg shadow p-6">
              <div className="flex justify-between items-center mb-4">
                <h3 className="text-lg font-semibold">Profile Information</h3>
                {!editing && (
                  <button onClick={() => setEditing(true)} className="text-green-600 hover:text-green-700">
                    Edit Profile
                  </button>
                )}
              </div>

              {!editing ? (
                <div className="space-y-3">
                  <div>
                    <p className="text-gray-500 text-sm">Phone</p>
                    <p className="font-medium">{profile?.phone}</p>
                  </div>
                  <div>
                    <p className="text-gray-500 text-sm">Email</p>
                    <p className="font-medium">{profile?.email || 'Not set'}</p>
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
                    <p className="text-gray-500 text-sm">Risk Tolerance</p>
                    <p className="font-medium">{profile?.riskTolerance || 'Not set'}</p>
                  </div>
                  <div>
                    <p className="text-gray-500 text-sm">Investment Goal</p>
                    <p className="font-medium">{profile?.investmentGoal || 'Not set'}</p>
                  </div>
                  <div>
                    <p className="text-gray-500 text-sm">Language</p>
                    <p className="font-medium">
                      {profile?.preferredLanguage === 'am' ? 'Amharic' : 
                       profile?.preferredLanguage === 'om' ? 'Oromiffa' : 'English'}
                    </p>
                  </div>
                </div>
              ) : (
                <form onSubmit={updateProfile} className="space-y-4">
                  <div>
                    <label className="block text-gray-700 text-sm mb-1">Email</label>
                    <input
                      type="email"
                      value={formData.email}
                      onChange={(e) => setFormData({...formData, email: e.target.value})}
                      className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-green-500"
                    />
                  </div>
                  <div>
                    <label className="block text-gray-700 text-sm mb-1">Preferred Language</label>
                    <select
                      value={formData.preferredLanguage}
                      onChange={(e) => setFormData({...formData, preferredLanguage: e.target.value})}
                      className="w-full px-3 py-2 border rounded-lg"
                    >
                      <option value="en">English</option>
                      <option value="am">Amharic</option>
                      <option value="om">Oromiffa</option>
                    </select>
                  </div>
                  <div>
                    <label className="block text-gray-700 text-sm mb-1">Risk Tolerance</label>
                    <select
                      value={formData.riskTolerance}
                      onChange={(e) => setFormData({...formData, riskTolerance: e.target.value})}
                      className="w-full px-3 py-2 border rounded-lg"
                    >
                      <option value="">Select...</option>
                      <option value="LOW">Low</option>
                      <option value="MODERATE">Moderate</option>
                      <option value="HIGH">High</option>
                    </select>
                  </div>
                  <div>
                    <label className="block text-gray-700 text-sm mb-1">Investment Goal</label>
                    <input
                      type="text"
                      value={formData.investmentGoal}
                      onChange={(e) => setFormData({...formData, investmentGoal: e.target.value})}
                      className="w-full px-3 py-2 border rounded-lg"
                      placeholder="e.g., Retirement, Wealth growth"
                    />
                  </div>
                  <div className="flex gap-3">
                    <button type="submit" className="bg-green-600 text-white px-4 py-2 rounded-lg hover:bg-green-700">
                      Save Changes
                    </button>
                    <button
                      type="button"
                      onClick={() => setEditing(false)}
                      className="bg-gray-500 text-white px-4 py-2 rounded-lg hover:bg-gray-600"
                    >
                      Cancel
                    </button>
                  </div>
                </form>
              )}
            </div>

            {/* Bank Account Card */}
            <div className="bg-white rounded-lg shadow p-6">
              <BankAccountManager userId={profile?.id || ''} onAccountUpdate={fetchProfile} />
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

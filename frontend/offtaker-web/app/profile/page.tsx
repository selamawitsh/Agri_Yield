'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import toast from 'react-hot-toast';
import api from '@/lib/api';
import { User } from '@/lib/types';
import BankAccountManager from '@/components/BankAccountManager';

export default function ProfilePage() {
  const router = useRouter();
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);
  const [editing, setEditing] = useState(false);
  const [formData, setFormData] = useState({ email: '', preferredLanguage: 'en' });

  useEffect(() => {
    const token = localStorage.getItem('access_token');
    if (!token) { router.push('/login'); return; }
    fetchProfile();
  }, []);

  const fetchProfile = async () => {
    try {
      const response = await api.get('/users/me');
      if (response.data.success) {
        setUser(response.data.data);
        setFormData({ email: response.data.data.email || '', preferredLanguage: response.data.data.preferredLanguage || 'en' });
      }
    } catch (error) { toast.error('Failed to load profile'); }
    finally { setLoading(false); }
  };

  const updateProfile = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const response = await api.patch('/users/me', formData);
      if (response.data.success) {
        setUser(response.data.data);
        setEditing(false);
        toast.success('Profile updated');
      }
    } catch (error) { toast.error('Failed to update profile'); }
  };

  const handleLogout = () => {
    localStorage.removeItem('access_token');
    localStorage.removeItem('refresh_token');
    router.push('/login');
  };

  if (loading) return <div className="min-h-screen flex items-center justify-center"><div className="animate-spin rounded-full h-12 w-12 border-b-2 border-teal-600"></div></div>;

  return (
    <div className="min-h-screen bg-gray-100">
      <nav className="bg-white shadow-md">
        <div className="container mx-auto px-6 py-4">
          <div className="flex justify-between items-center">
            <h1 className="text-2xl font-bold text-teal-600">Agri-Yield Off-Taker</h1>
            <div className="flex gap-4">
              <Link href="/dashboard" className="text-gray-600 hover:text-teal-600">Dashboard</Link>
              <Link href="/profile" className="text-teal-600 font-semibold">Profile</Link>
              <button onClick={handleLogout} className="bg-red-600 text-white px-4 py-2 rounded-lg">Logout</button>
            </div>
          </div>
        </div>
      </nav>

      <div className="container mx-auto px-6 py-8">
        <div className="max-w-4xl mx-auto">
          <h2 className="text-2xl font-bold mb-6">Profile Settings</h2>
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            <div className="bg-white rounded-lg shadow p-6">
              <div className="flex justify-between items-center mb-4">
                <h3 className="text-lg font-semibold">Profile Information</h3>
                {!editing && <button onClick={() => setEditing(true)} className="text-teal-600">Edit</button>}
              </div>
              {!editing ? (
                <div className="space-y-3">
                  <div><p className="text-gray-500 text-sm">Phone</p><p className="font-medium">{user?.phone}</p></div>
                  <div><p className="text-gray-500 text-sm">Email</p><p className="font-medium">{user?.email || 'Not set'}</p></div>
                  <div><p className="text-gray-500 text-sm">KYC Status</p><span className={`px-2 py-1 text-xs rounded-full ${user?.kycStatus === 'VERIFIED' ? 'bg-green-100 text-green-800' : 'bg-yellow-100 text-yellow-800'}`}>{user?.kycStatus}</span></div>
                  <div><p className="text-gray-500 text-sm">Language</p><p className="font-medium">{user?.preferredLanguage === 'am' ? 'Amharic' : 'English'}</p></div>
                </div>
              ) : (
                <form onSubmit={updateProfile} className="space-y-4">
                  <div><label className="block text-gray-700 text-sm mb-1">Email</label><input type="email" value={formData.email} onChange={(e) => setFormData({...formData, email: e.target.value})} className="w-full px-3 py-2 border rounded-lg" /></div>
                  <div><label className="block text-gray-700 text-sm mb-1">Language</label><select value={formData.preferredLanguage} onChange={(e) => setFormData({...formData, preferredLanguage: e.target.value})} className="w-full px-3 py-2 border rounded-lg"><option value="en">English</option><option value="am">Amharic</option></select></div>
                  <div className="flex gap-3"><button type="submit" className="bg-teal-600 text-white px-4 py-2 rounded-lg">Save</button><button type="button" onClick={() => setEditing(false)} className="bg-gray-500 text-white px-4 py-2 rounded-lg">Cancel</button></div>
                </form>
              )}
            </div>
            <div className="bg-white rounded-lg shadow p-6">
              <BankAccountManager />
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

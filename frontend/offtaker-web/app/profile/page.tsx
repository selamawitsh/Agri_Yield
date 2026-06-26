'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import type { UserProfile, BankAccount } from '@/lib/types';
import {
  getMyProfile, updateProfile, getBankAccounts,
  addBankAccount, verifyBankAccount, setDefaultBankAccount,
  deleteBankAccount, logout,
} from '@/lib/api';
import { clearTokens } from '@/lib/auth';

const LANGUAGE_OPTIONS = [
  { code: 'am', label: 'አማርኛ (Amharic)' },
  { code: 'om', label: 'Oromiffa' },
  { code: 'en', label: 'English' },
];

export default function ProfilePage() {
  const router = useRouter();
  const [profile,     setProfile]     = useState<UserProfile | null>(null);
  const [accounts,    setAccounts]    = useState<BankAccount[]>([]);
  const [loading,     setLoading]     = useState(true);
  const [editing,     setEditing]     = useState(false);
  const [saving,      setSaving]      = useState(false);
  const [showAddBank, setShowAddBank] = useState(false);
  const [message,     setMessage]     = useState('');
  const [email,       setEmail]       = useState('');
  const [language,    setLanguage]    = useState('en');
  const [bankType,    setBankType]    = useState('TELEBIRR');
  const [bankNumber,  setBankNumber]  = useState('');
  const [bankHolder,  setBankHolder]  = useState('');
  const [bankLoading, setBankLoading] = useState(false);

  useEffect(() => { loadAll(); }, []);

  async function loadAll() {
    setLoading(true);
    try {
      const [profileRes, bankRes] = await Promise.all([
        getMyProfile(),
        getBankAccounts(),
      ]);
      const p = profileRes.data.data;
      const b = bankRes.data.data || [];
      setProfile(p);
      setAccounts(b);
      setEmail(p.email || '');
      setLanguage(p.preferredLanguage || 'en');
    } catch {
      router.push('/login');
    } finally {
      setLoading(false);
    }
  }

  async function handleSave() {
    setSaving(true);
    try {
      await updateProfile({ email, preferredLanguage: language });
      setMessage('Profile updated');
      setEditing(false);
      loadAll();
    } catch (err: unknown) {
      setMessage(err instanceof Error ? err.message : 'Failed');
    } finally {
      setSaving(false);
    }
  }

  async function handleAddBank(e: React.FormEvent) {
    e.preventDefault();
    setBankLoading(true);
    try {
      await addBankAccount({
        accountType: bankType,
        accountNumber: bankNumber,
        accountHolderName: bankHolder,
      });
      setShowAddBank(false);
      setBankNumber('');
      setBankHolder('');
      setMessage('Account added. Verify with test deposit code.');
      loadAll();
    } catch (err: unknown) {
      setMessage(err instanceof Error ? err.message : 'Failed');
    } finally {
      setBankLoading(false);
    }
  }

  async function handleVerify(id: string) {
    const code = window.prompt('Enter verification code from test deposit:');
    if (!code) return;
    try {
      await verifyBankAccount(id, code);
      setMessage('Verified!');
      loadAll();
    } catch (err: unknown) {
      setMessage(err instanceof Error ? err.message : 'Failed');
    }
  }

  async function handleSetDefault(id: string) {
    try {
      await setDefaultBankAccount(id);
      loadAll();
    } catch (err: unknown) {
      setMessage(err instanceof Error ? err.message : 'Failed');
    }
  }

  async function handleDelete(id: string) {
    if (!confirm('Remove this account?')) return;
    try {
      await deleteBankAccount(id);
      loadAll();
    } catch (err: unknown) {
      setMessage(err instanceof Error ? err.message : 'Failed');
    }
  }

  async function handleLogout() {
    try { await logout(); } catch { /* ignore */ }
    clearTokens();
    router.push('/login');
  }

  if (loading) return (
    <div className="min-h-screen flex items-center justify-center">
      <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-teal-600" />
    </div>
  );

  return (
    <div className="min-h-screen bg-gray-50">
      <nav className="bg-teal-700 text-white px-6 py-4 flex justify-between items-center">
        <div className="flex items-center gap-3">
          <span className="font-bold text-lg">Agri-Yield Off-Taker</span>
        </div>
        <div className="flex gap-6 text-sm font-medium">
          <a href="/dashboard" className="hover:text-teal-200">Dashboard</a>
          <a href="/profile" className="text-teal-200">Profile</a>
          <button onClick={handleLogout} className="hover:text-teal-200">Sign Out</button>
        </div>
      </nav>

      <div className="max-w-3xl mx-auto px-4 py-8">
        <h1 className="text-2xl font-bold text-gray-900 mb-6">My Profile</h1>

        {message && (
          <div className="bg-teal-50 border border-teal-200 text-teal-700 px-4 py-3 rounded-lg mb-6 text-sm flex justify-between items-center">
            <span>{message}</span>
            <button onClick={() => setMessage('')} className="ml-4 font-bold">✕</button>
          </div>
        )}

        {/* Account Info */}
        <div className="bg-white rounded-2xl shadow-sm border border-gray-100 p-6 mb-6">
          <div className="flex justify-between items-start mb-6">
            <h2 className="text-lg font-semibold text-gray-900">Account Information</h2>
            <button
              onClick={() => setEditing(!editing)}
              className={`px-4 py-2 rounded-lg text-sm font-medium transition-colors ${
                editing
                  ? 'bg-gray-100 text-gray-600'
                  : 'bg-teal-700 text-white hover:bg-teal-800'
              }`}
            >
              {editing ? 'Cancel' : 'Edit'}
            </button>
          </div>

          <div className="grid grid-cols-2 gap-4 mb-4">
            <div>
              <p className="text-xs text-gray-400 uppercase tracking-wide mb-1">Phone</p>
              <p className="font-semibold">{profile?.phone}</p>
            </div>
            <div>
              <p className="text-xs text-gray-400 uppercase tracking-wide mb-1">Role</p>
              <span className="inline-flex px-2.5 py-0.5 rounded-full text-xs font-medium bg-teal-100 text-teal-800">
                {profile?.role}
              </span>
            </div>
            <div>
              <p className="text-xs text-gray-400 uppercase tracking-wide mb-1">KYC Status</p>
              <span className={`inline-flex px-2.5 py-0.5 rounded-full text-xs font-medium ${
                profile?.kycStatus === 'VERIFIED'
                  ? 'bg-green-100 text-green-800'
                  : 'bg-yellow-100 text-yellow-800'
              }`}>
                {profile?.kycStatus}
              </span>
            </div>
            <div>
              <p className="text-xs text-gray-400 uppercase tracking-wide mb-1">Fayda ID</p>
              <p className="font-mono text-sm">{profile?.faydaId}</p>
            </div>
          </div>

          {editing ? (
            <div className="space-y-4 border-t pt-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Email</label>
                <input
                  type="email"
                  value={email}
                  onChange={e => setEmail(e.target.value)}
                  className="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-teal-500 outline-none"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Language</label>
                <select
                  value={language}
                  onChange={e => setLanguage(e.target.value)}
                  className="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-teal-500 outline-none"
                >
                  {LANGUAGE_OPTIONS.map(l => (
                    <option key={l.code} value={l.code}>{l.label}</option>
                  ))}
                </select>
              </div>
              <button
                onClick={handleSave}
                disabled={saving}
                className="w-full bg-teal-700 text-white py-3 rounded-lg font-semibold hover:bg-teal-800 disabled:opacity-50"
              >
                {saving ? 'Saving...' : 'Save Changes'}
              </button>
            </div>
          ) : (
            <div className="grid grid-cols-2 gap-4 border-t pt-4">
              <div>
                <p className="text-xs text-gray-400 uppercase tracking-wide mb-1">Email</p>
                <p>{profile?.email || '—'}</p>
              </div>
              <div>
                <p className="text-xs text-gray-400 uppercase tracking-wide mb-1">Language</p>
                <p>{LANGUAGE_OPTIONS.find(l => l.code === profile?.preferredLanguage)?.label || profile?.preferredLanguage}</p>
              </div>
            </div>
          )}
        </div>

        {/* Bank Accounts */}
        <div className="bg-white rounded-2xl shadow-sm border border-gray-100 p-6">
          <div className="flex justify-between items-center mb-6">
            <div>
              <h2 className="text-lg font-semibold text-gray-900">Bank Accounts</h2>
              <p className="text-sm text-gray-500">For bid deposits and harvest payments</p>
            </div>
            {!showAddBank && (
              <button
                onClick={() => setShowAddBank(true)}
                className="px-4 py-2 bg-teal-700 text-white rounded-lg text-sm font-medium hover:bg-teal-800"
              >
                + Add Account
              </button>
            )}
          </div>

          {showAddBank && (
            <form onSubmit={handleAddBank} className="bg-gray-50 rounded-xl p-4 mb-6 space-y-3">
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-medium text-gray-600 mb-1">Type</label>
                  <select
                    value={bankType}
                    onChange={e => setBankType(e.target.value)}
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm outline-none"
                  >
                    <option value="TELEBIRR">Telebirr</option>
                    <option value="CBE">CBE Birr</option>
                  </select>
                </div>
                <div>
                  <label className="block text-xs font-medium text-gray-600 mb-1">Account Number</label>
                  <input
                    type="text"
                    value={bankNumber}
                    onChange={e => setBankNumber(e.target.value)}
                    required
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm outline-none"
                  />
                </div>
              </div>
              <div>
                <label className="block text-xs font-medium text-gray-600 mb-1">Account Holder</label>
                <input
                  type="text"
                  value={bankHolder}
                  onChange={e => setBankHolder(e.target.value)}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm outline-none"
                />
              </div>
              <div className="flex gap-2">
                <button
                  type="submit"
                  disabled={bankLoading}
                  className="flex-1 bg-teal-700 text-white py-2 rounded-lg text-sm font-semibold hover:bg-teal-800 disabled:opacity-50"
                >
                  {bankLoading ? 'Adding...' : 'Add Account'}
                </button>
                <button
                  type="button"
                  onClick={() => setShowAddBank(false)}
                  className="flex-1 border border-gray-300 text-gray-600 py-2 rounded-lg text-sm"
                >
                  Cancel
                </button>
              </div>
            </form>
          )}

          {accounts.length === 0 ? (
            <div className="text-center py-8 text-gray-400">
              <p className="font-medium">No bank accounts linked</p>
              <p className="text-sm mt-1">Add an account for bid deposits and payments</p>
            </div>
          ) : (
            <div className="space-y-3">
              {accounts.map(acc => (
                <div
                  key={acc.id}
                  className={`flex items-center justify-between p-4 rounded-xl border ${
                    acc.isDefault ? 'border-teal-200 bg-teal-50' : 'border-gray-200'
                  }`}
                >
                  <div className="flex items-center gap-3">
                    <div className={`w-10 h-10 rounded-xl flex items-center justify-center text-xs font-bold ${
                      acc.accountType === 'TELEBIRR' ? 'bg-teal-100 text-teal-700' : 'bg-yellow-100 text-yellow-700'
                    }`}>
                      {acc.accountType === 'TELEBIRR' ? 'TB' : 'CBE'}
                    </div>
                    <div>
                      <div className="flex items-center gap-2">
                        <p className="font-semibold text-sm">{acc.accountType}</p>
                        {acc.isDefault && (
                          <span className="bg-teal-100 text-teal-700 text-xs px-2 py-0.5 rounded-full font-medium">
                            Default
                          </span>
                        )}
                        {!acc.isVerified && (
                          <span className="bg-yellow-100 text-yellow-700 text-xs px-2 py-0.5 rounded-full font-medium">
                            Unverified
                          </span>
                        )}
                      </div>
                      <p className="text-gray-500 text-xs font-mono">
                        {'•'.repeat(Math.max(0, acc.accountNumber.length - 4))}
                        {acc.accountNumber.slice(-4)}
                      </p>
                    </div>
                  </div>
                  <div className="flex gap-2">
                    {!acc.isVerified && (
                      <button
                        onClick={() => handleVerify(acc.id)}
                        className="text-xs px-3 py-1.5 bg-teal-700 text-white rounded-lg hover:bg-teal-800"
                      >
                        Verify
                      </button>
                    )}
                    {!acc.isDefault && acc.isVerified && (
                      <button
                        onClick={() => handleSetDefault(acc.id)}
                        className="text-xs px-3 py-1.5 border border-teal-700 text-teal-700 rounded-lg hover:bg-teal-50"
                      >
                        Set Default
                      </button>
                    )}
                    <button
                      onClick={() => handleDelete(acc.id)}
                      className="text-xs px-3 py-1.5 border border-red-200 text-red-600 rounded-lg hover:bg-red-50"
                    >
                      Remove
                    </button>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

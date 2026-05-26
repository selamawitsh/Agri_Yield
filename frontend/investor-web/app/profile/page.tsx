'use client';
import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import {
  getMyProfile, updateProfile, getBankAccounts,
  addBankAccount, verifyBankAccount, setDefaultBankAccount,
  deleteBankAccount, logout,
  type UserProfile, type BankAccount,
} from '@/lib/api';
import { clearTokens } from '@/lib/auth';
import Navbar from '@/components/Navbar';

const LANGUAGE_OPTIONS = [
  { code: 'am', label: 'አማርኛ (Amharic)' },
  { code: 'om', label: 'Oromiffa' },
  { code: 'en', label: 'English' },
];
const RISK_OPTIONS = ['LOW', 'MODERATE', 'HIGH'];

export default function ProfilePage() {
  const router = useRouter();
  const [profile, setProfile] = useState<UserProfile | null>(null);
  const [accounts, setAccounts] = useState<BankAccount[]>([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [editing, setEditing] = useState(false);
  const [showAddBank, setShowAddBank] = useState(false);
  const [message, setMessage] = useState('');

  // Edit fields
  const [email, setEmail] = useState('');
  const [language, setLanguage] = useState('am');
  const [riskTolerance, setRiskTolerance] = useState('MODERATE');
  const [investmentGoal, setInvestmentGoal] = useState('');

  // Bank form
  const [bankType, setBankType] = useState('TELEBIRR');
  const [bankNumber, setBankNumber] = useState('');
  const [bankHolder, setBankHolder] = useState('');
  const [bankLoading, setBankLoading] = useState(false);

  useEffect(() => { loadAll(); }, []);

  async function loadAll() {
    setLoading(true);
    try {
      const [profileRes, bankRes] = await Promise.all([
        getMyProfile(), getBankAccounts(),
      ]);
      setProfile(profileRes.data);
      setAccounts(bankRes.data);
      setEmail(profileRes.data.email || '');
      setLanguage(profileRes.data.preferredLanguage || 'am');
      setRiskTolerance(profileRes.data.riskTolerance || 'MODERATE');
      setInvestmentGoal(profileRes.data.investmentGoal || '');
    } catch {
      router.push('/login');
    } finally {
      setLoading(false);
    }
  }

  async function handleSave() {
    setSaving(true);
    try {
      await updateProfile({ email, preferredLanguage: language, riskTolerance, investmentGoal });
      setMessage('Profile updated successfully');
      setEditing(false);
      loadAll();
    } catch (err: unknown) {
      setMessage(err instanceof Error ? err.message : 'Update failed');
    } finally {
      setSaving(false);
    }
  }

  async function handleAddBank(e: React.FormEvent) {
    e.preventDefault();
    setBankLoading(true);
    try {
      await addBankAccount({ accountType: bankType, accountNumber: bankNumber, accountHolderName: bankHolder });
      setShowAddBank(false);
      setBankNumber(''); setBankHolder('');
      setMessage('Bank account added. Check for a test deposit to verify.');
      loadAll();
    } catch (err: unknown) {
      setMessage(err instanceof Error ? err.message : 'Failed to add account');
    } finally {
      setBankLoading(false);
    }
  }

  async function handleVerify(accountId: string) {
    const code = window.prompt('Enter the verification code from your test deposit:');
    if (!code) return;
    try {
      await verifyBankAccount(accountId, code);
      setMessage('Account verified!');
      loadAll();
    } catch (err: unknown) {
      setMessage(err instanceof Error ? err.message : 'Verification failed');
    }
  }

  async function handleSetDefault(accountId: string) {
    try {
      await setDefaultBankAccount(accountId);
      setMessage('Default account updated');
      loadAll();
    } catch (err: unknown) {
      setMessage(err instanceof Error ? err.message : 'Failed');
    }
  }

  async function handleDelete(accountId: string) {
    if (!confirm('Remove this bank account?')) return;
    try {
      await deleteBankAccount(accountId);
      setMessage('Account removed');
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

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-green-600" />
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />
      <div className="max-w-3xl mx-auto px-4 py-8">
        <div className="flex justify-between items-center mb-6">
          <h1 className="text-2xl font-bold text-gray-900">My Profile</h1>
          <button onClick={handleLogout}
            className="text-sm text-red-600 hover:text-red-800 font-medium">
            Sign Out
          </button>
        </div>

        {message && (
          <div className="bg-green-50 border border-green-200 text-green-700 px-4 py-3 rounded-lg mb-6 text-sm flex justify-between">
            {message}
            <button onClick={() => setMessage('')} className="text-green-500 hover:text-green-700">✕</button>
          </div>
        )}

        {/* Profile Card */}
        <div className="bg-white rounded-2xl shadow-sm border border-gray-100 p-6 mb-6">
          <div className="flex justify-between items-start mb-6">
            <div>
              <h2 className="text-lg font-semibold text-gray-900">Account Information</h2>
              <p className="text-sm text-gray-500">Manage your personal details</p>
            </div>
            <button onClick={() => setEditing(!editing)}
              className={`px-4 py-2 rounded-lg text-sm font-medium transition-colors ${
                editing ? 'bg-gray-100 text-gray-600' : 'bg-green-600 text-white hover:bg-green-700'
              }`}>
              {editing ? 'Cancel' : 'Edit Profile'}
            </button>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <p className="text-xs text-gray-400 font-medium uppercase tracking-wide mb-1">Phone</p>
              <p className="font-semibold text-gray-900">{profile?.phone}</p>
            </div>
            <div>
              <p className="text-xs text-gray-400 font-medium uppercase tracking-wide mb-1">Role</p>
              <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800">
                {profile?.role}
              </span>
            </div>
            <div>
              <p className="text-xs text-gray-400 font-medium uppercase tracking-wide mb-1">KYC Status</p>
              <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
                profile?.kycStatus === 'VERIFIED'
                  ? 'bg-green-100 text-green-800'
                  : 'bg-yellow-100 text-yellow-800'
              }`}>
                {profile?.kycStatus}
              </span>
            </div>
            <div>
              <p className="text-xs text-gray-400 font-medium uppercase tracking-wide mb-1">Fayda ID</p>
              <p className="font-mono text-sm text-gray-700">{profile?.faydaId}</p>
            </div>
          </div>

          {editing ? (
            <div className="mt-6 space-y-4 border-t pt-6">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Email</label>
                <input type="email" value={email} onChange={e => setEmail(e.target.value)}
                  className="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 outline-none" />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Preferred Language</label>
                <select value={language} onChange={e => setLanguage(e.target.value)}
                  className="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 outline-none">
                  {LANGUAGE_OPTIONS.map(l => <option key={l.code} value={l.code}>{l.label}</option>)}
                </select>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">Risk Tolerance</label>
                <div className="flex gap-2">
                  {RISK_OPTIONS.map(r => (
                    <button key={r} type="button" onClick={() => setRiskTolerance(r)}
                      className={`flex-1 py-2 rounded-lg text-sm font-semibold border transition-colors ${
                        riskTolerance === r
                          ? 'bg-green-600 text-white border-green-600'
                          : 'bg-white text-gray-600 border-gray-300 hover:border-green-400'
                      }`}>
                      {r}
                    </button>
                  ))}
                </div>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Investment Goal</label>
                <input type="text" value={investmentGoal} onChange={e => setInvestmentGoal(e.target.value)}
                  className="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 outline-none" />
              </div>
              <button onClick={handleSave} disabled={saving}
                className="w-full bg-green-600 text-white py-3 rounded-lg font-semibold hover:bg-green-700 disabled:opacity-50 transition-colors">
                {saving ? 'Saving...' : 'Save Changes'}
              </button>
            </div>
          ) : (
            <div className="mt-4 grid grid-cols-2 gap-4 border-t pt-4">
              <div>
                <p className="text-xs text-gray-400 font-medium uppercase tracking-wide mb-1">Email</p>
                <p className="text-gray-900">{profile?.email || '—'}</p>
              </div>
              <div>
                <p className="text-xs text-gray-400 font-medium uppercase tracking-wide mb-1">Language</p>
                <p className="text-gray-900">{LANGUAGE_OPTIONS.find(l => l.code === profile?.preferredLanguage)?.label || profile?.preferredLanguage}</p>
              </div>
              <div>
                <p className="text-xs text-gray-400 font-medium uppercase tracking-wide mb-1">Risk Tolerance</p>
                <p className="text-gray-900">{profile?.riskTolerance || '—'}</p>
              </div>
              <div>
                <p className="text-xs text-gray-400 font-medium uppercase tracking-wide mb-1">Investment Goal</p>
                <p className="text-gray-900 text-sm">{profile?.investmentGoal || '—'}</p>
              </div>
            </div>
          )}
        </div>

        {/* Bank Accounts */}
        <div className="bg-white rounded-2xl shadow-sm border border-gray-100 p-6">
          <div className="flex justify-between items-center mb-6">
            <div>
              <h2 className="text-lg font-semibold text-gray-900">Bank Accounts</h2>
              <p className="text-sm text-gray-500">Linked payment accounts for investment payouts</p>
            </div>
            {!showAddBank && (
              <button onClick={() => setShowAddBank(true)}
                className="px-4 py-2 bg-green-600 text-white rounded-lg text-sm font-medium hover:bg-green-700 transition-colors">
                + Add Account
              </button>
            )}
          </div>

          {showAddBank && (
            <form onSubmit={handleAddBank} className="bg-gray-50 rounded-xl p-4 mb-6 space-y-3">
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-medium text-gray-600 mb-1">Account Type</label>
                  <select value={bankType} onChange={e => setBankType(e.target.value)}
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm outline-none focus:ring-2 focus:ring-green-500">
                    <option value="TELEBIRR">Telebirr</option>
                    <option value="CBE">CBE Birr</option>
                  </select>
                </div>
                <div>
                  <label className="block text-xs font-medium text-gray-600 mb-1">Account Number</label>
                  <input type="text" value={bankNumber} onChange={e => setBankNumber(e.target.value)} required
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm outline-none focus:ring-2 focus:ring-green-500" />
                </div>
              </div>
              <div>
                <label className="block text-xs font-medium text-gray-600 mb-1">Account Holder Name</label>
                <input type="text" value={bankHolder} onChange={e => setBankHolder(e.target.value)}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm outline-none focus:ring-2 focus:ring-green-500" />
              </div>
              <div className="flex gap-2">
                <button type="submit" disabled={bankLoading}
                  className="flex-1 bg-green-600 text-white py-2 rounded-lg text-sm font-semibold hover:bg-green-700 disabled:opacity-50">
                  {bankLoading ? 'Adding...' : 'Add Account'}
                </button>
                <button type="button" onClick={() => setShowAddBank(false)}
                  className="flex-1 border border-gray-300 text-gray-600 py-2 rounded-lg text-sm font-semibold hover:bg-gray-50">
                  Cancel
                </button>
              </div>
            </form>
          )}

          {accounts.length === 0 ? (
            <div className="text-center py-8 text-gray-400">
              <p className="text-4xl mb-2">🏦</p>
              <p className="font-medium">No bank accounts linked</p>
              <p className="text-sm">Add an account to receive investment payouts</p>
            </div>
          ) : (
            <div className="space-y-3">
              {accounts.map(acc => (
                <div key={acc.id} className={`flex items-center justify-between p-4 rounded-xl border ${
                  acc.isDefault ? 'border-green-200 bg-green-50' : 'border-gray-200'
                }`}>
                  <div className="flex items-center gap-3">
                    <div className={`w-10 h-10 rounded-xl flex items-center justify-center text-lg ${
                      acc.accountType === 'TELEBIRR' ? 'bg-blue-100' : 'bg-yellow-100'
                    }`}>
                      {acc.accountType === 'TELEBIRR' ? '📱' : '🏦'}
                    </div>
                    <div>
                      <div className="flex items-center gap-2">
                        <p className="font-semibold text-gray-900 text-sm">{acc.accountType}</p>
                        {acc.isDefault && (
                          <span className="bg-green-100 text-green-700 text-xs px-2 py-0.5 rounded-full font-medium">Default</span>
                        )}
                        {!acc.isVerified && (
                          <span className="bg-yellow-100 text-yellow-700 text-xs px-2 py-0.5 rounded-full font-medium">Unverified</span>
                        )}
                      </div>
                      <p className="text-gray-500 text-xs font-mono">
                        {'•'.repeat(acc.accountNumber.length - 4)}{acc.accountNumber.slice(-4)}
                      </p>
                    </div>
                  </div>
                  <div className="flex items-center gap-2">
                    {!acc.isVerified && (
                      <button onClick={() => handleVerify(acc.id)}
                        className="text-xs px-3 py-1.5 bg-blue-600 text-white rounded-lg hover:bg-blue-700">
                        Verify
                      </button>
                    )}
                    {!acc.isDefault && acc.isVerified && (
                      <button onClick={() => handleSetDefault(acc.id)}
                        className="text-xs px-3 py-1.5 border border-green-600 text-green-600 rounded-lg hover:bg-green-50">
                        Set Default
                      </button>
                    )}
                    <button onClick={() => handleDelete(acc.id)}
                      className="text-xs px-3 py-1.5 border border-red-200 text-red-600 rounded-lg hover:bg-red-50">
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

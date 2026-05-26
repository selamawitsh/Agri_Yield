
'use client';
import { useEffect, useState } from 'react';
import { useParams, useRouter } from 'next/navigation';
import toast from 'react-hot-toast';
import api from '@/lib/api';
import { User } from '@/lib/types';

export default function UserDetailPage() {
  const { id } = useParams<{ id: string }>();
  const router = useRouter();
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);
  const [suspendModal, setSuspendModal] = useState(false);
  const [kycModal, setKycModal] = useState(false);
  const [pendingKycStatus, setPendingKycStatus] = useState('');
  const [suspendReason, setSuspendReason] = useState('');
  const [acting, setActing] = useState(false);

  useEffect(() => { loadUser(); }, [id]);

  async function loadUser() {
    setLoading(true);
    try {
      const res = await api.get(`/admin/users/${id}`);
      setUser(res.data.data);
    } catch {
      toast.error('User not found');
      router.push('/dashboard');
    } finally { setLoading(false); }
  }

  // US-10: Suspend
  async function confirmSuspend() {
    if (!user) return;
    setActing(true);
    try {
      await api.patch(`/admin/users/${user.id}/status`, {
        status: 'SUSPENDED',
        reason: suspendReason || 'No reason provided',
      });
      toast.success('User suspended');
      setSuspendModal(false); setSuspendReason('');
      loadUser();
    } catch { toast.error('Failed to suspend'); }
    finally { setActing(false); }
  }

  // US-10: Activate
  async function handleActivate() {
    if (!user) return;
    setActing(true);
    try {
      await api.patch(`/admin/users/${user.id}/status`, { status: 'ACTIVE' });
      toast.success('User activated');
      loadUser();
    } catch { toast.error('Failed to activate'); }
    finally { setActing(false); }
  }

  // KYC update
  async function confirmKyc() {
    if (!user) return;
    setActing(true);
    try {
      await api.patch(`/admin/users/${user.id}/kyc`, { kycStatus: pendingKycStatus });
      toast.success(`KYC updated to ${pendingKycStatus}`);
      setKycModal(false);
      loadUser();
    } catch { toast.error('KYC update failed'); }
    finally { setActing(false); }
  }

  function roleBadgeColor(role: string) {
    const map: Record<string, string> = {
      FARMER: 'bg-green-100 text-green-800',
      INVESTOR: 'bg-blue-100 text-blue-800',
      MERCHANT: 'bg-orange-100 text-orange-800',
      OFF_TAKER: 'bg-purple-100 text-purple-800',
      ADMIN: 'bg-slate-200 text-slate-800',
    };
    return map[role] || 'bg-slate-100 text-slate-700';
  }

  function kycColor(status: string) {
    return status === 'VERIFIED' ? 'text-emerald-700 bg-emerald-50 border-emerald-200'
      : status === 'REJECTED' ? 'text-red-700 bg-red-50 border-red-200'
      : 'text-amber-700 bg-amber-50 border-amber-200';
  }

  function accountColor(status: string) {
    return status === 'ACTIVE' ? 'text-emerald-700 bg-emerald-50 border-emerald-200'
      : status === 'SUSPENDED' ? 'text-rose-700 bg-rose-50 border-rose-200'
      : 'text-yellow-700 bg-yellow-50 border-yellow-200';
  }

  if (loading) {
    return (
      <div className="min-h-screen bg-[#F4F7F5] flex items-center justify-center">
        <div className="w-8 h-8 border-2 border-emerald-600 border-t-transparent rounded-full animate-spin" />
      </div>
    );
  }

  if (!user) return null;

  return (
    <div className="min-h-screen bg-[#F4F7F5] pb-12">
      {/* Header */}
      <header className="bg-white border-b border-slate-200 sticky top-0 z-40">
        <div className="max-w-5xl mx-auto px-4 h-16 flex items-center gap-4">
          <button onClick={() => router.push('/dashboard')}
            className="text-xs font-bold text-slate-500 hover:text-slate-800 flex items-center gap-1.5 transition-colors">
            <svg className="w-4 h-4" fill="none" stroke="currentColor" strokeWidth="2.5" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" d="M19 12H5M12 19l-7-7 7-7" />
            </svg>
            Back to Dashboard
          </button>
          <span className="text-slate-300">|</span>
          <span className="text-sm font-bold text-slate-700">User Detail — US-09</span>
        </div>
      </header>

      <main className="max-w-5xl mx-auto px-4 py-6 space-y-6">
        {/* Identity Card */}
        <div className="bg-white rounded-2xl border border-slate-200 shadow-sm p-6">
          <div className="flex flex-col sm:flex-row sm:items-start justify-between gap-4">
            <div className="flex items-center gap-4">
              <div className="w-14 h-14 rounded-2xl bg-emerald-100 flex items-center justify-center text-2xl font-black text-emerald-800">
                {user.role === 'FARMER' ? '🌾' : user.role === 'INVESTOR' ? '💼' : user.role === 'MERCHANT' ? '🏪' : user.role === 'OFF_TAKER' ? '🚛' : '⚙️'}
              </div>
              <div>
                <div className="flex items-center gap-2 flex-wrap">
                  <h1 className="text-lg font-black text-slate-900">{user.phone}</h1>
                  <span className={`px-2.5 py-0.5 rounded-full text-[11px] font-bold ${roleBadgeColor(user.role)}`}>
                    {user.role}
                  </span>
                </div>
                <p className="text-xs text-slate-400 mt-0.5 font-mono">ID: {user.id}</p>
                {user.email && <p className="text-xs text-slate-500 mt-0.5">{user.email}</p>}
              </div>
            </div>

            {/* Action buttons */}
            <div className="flex flex-wrap gap-2">
              {user.kycStatus === 'PENDING' && (
                <>
                  <button onClick={() => { setPendingKycStatus('VERIFIED'); setKycModal(true); }}
                    className="px-4 py-2 bg-blue-600 text-white text-xs font-bold rounded-xl hover:bg-blue-700 transition-colors">
                    ✓ Verify KYC
                  </button>
                  <button onClick={() => { setPendingKycStatus('REJECTED'); setKycModal(true); }}
                    className="px-4 py-2 bg-red-500 text-white text-xs font-bold rounded-xl hover:bg-red-600 transition-colors">
                    ✕ Reject KYC
                  </button>
                </>
              )}
              {user.kycStatus === 'REJECTED' && (
                <button onClick={() => { setPendingKycStatus('VERIFIED'); setKycModal(true); }}
                  className="px-4 py-2 bg-blue-600 text-white text-xs font-bold rounded-xl hover:bg-blue-700 transition-colors">
                  Re-verify KYC
                </button>
              )}
              {user.kycStatus === 'VERIFIED' && (
                <button onClick={() => { setPendingKycStatus('REJECTED'); setKycModal(true); }}
                  className="px-4 py-2 border border-red-200 text-red-600 text-xs font-bold rounded-xl hover:bg-red-50 transition-colors">
                  Revoke KYC
                </button>
              )}
              {user.accountStatus === 'ACTIVE' && (
                <button onClick={() => setSuspendModal(true)}
                  className="px-4 py-2 bg-rose-600 text-white text-xs font-bold rounded-xl hover:bg-rose-700 transition-colors">
                  Suspend Account
                </button>
              )}
              {user.accountStatus === 'SUSPENDED' && (
                <button onClick={handleActivate} disabled={acting}
                  className="px-4 py-2 bg-emerald-700 text-white text-xs font-bold rounded-xl hover:bg-emerald-800 transition-colors disabled:opacity-50">
                  Reactivate Account
                </button>
              )}
            </div>
          </div>
        </div>

        {/* Status badges row */}
        <div className="grid grid-cols-2 sm:grid-cols-3 gap-4">
          <div className="bg-white rounded-2xl border border-slate-200 p-4">
            <p className="text-[10px] font-bold uppercase tracking-widest text-slate-400 mb-2">KYC Status</p>
            <span className={`inline-flex items-center px-3 py-1.5 rounded-xl border text-xs font-bold ${kycColor(user.kycStatus)}`}>
              {user.kycStatus === 'VERIFIED' ? '✓ ' : user.kycStatus === 'REJECTED' ? '✕ ' : '⏳ '}
              {user.kycStatus}
            </span>
          </div>
          <div className="bg-white rounded-2xl border border-slate-200 p-4">
            <p className="text-[10px] font-bold uppercase tracking-widest text-slate-400 mb-2">Account Status</p>
            <span className={`inline-flex items-center px-3 py-1.5 rounded-xl border text-xs font-bold ${accountColor(user.accountStatus)}`}>
              {user.accountStatus.replace('_', ' ')}
            </span>
          </div>
          <div className="bg-white rounded-2xl border border-slate-200 p-4">
            <p className="text-[10px] font-bold uppercase tracking-widest text-slate-400 mb-2">Language</p>
            <p className="text-sm font-bold text-slate-700">
              {user.preferredLanguage === 'am' ? '🇪🇹 Amharic' : user.preferredLanguage === 'om' ? '🇪🇹 Oromiffa' : '🌐 English'}
            </p>
          </div>
        </div>

        {/* Details grid */}
        <div className="bg-white rounded-2xl border border-slate-200 shadow-sm p-6">
          <h2 className="text-sm font-bold text-slate-900 mb-5">Account Details</h2>
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-x-8 gap-y-5">
            <InfoRow label="Fayda National ID" value={user.faydaId} mono />
            <InfoRow label="Phone Number" value={user.phone} mono />
            <InfoRow label="Email" value={user.email || '—'} />
            <InfoRow label="Role" value={user.role} />
            <InfoRow label="Joined" value={user.createdAt ? new Date(user.createdAt).toLocaleString() : '—'} />
            {user.faydaVerifiedAt && (
              <InfoRow label="Fayda Verified At" value={new Date(user.faydaVerifiedAt).toLocaleString()} />
            )}
            {user.riskTolerance && (
              <InfoRow label="Risk Tolerance" value={user.riskTolerance} />
            )}
            {user.investmentGoal && (
              <InfoRow label="Investment Goal" value={user.investmentGoal} />
            )}
            {user.agriScore !== undefined && user.agriScore !== null && (
              <InfoRow label="Agri-Score" value={`${user.agriScore} / 900`} />
            )}
          </div>
        </div>

        {/* Audit info */}
        <div className="bg-white rounded-2xl border border-slate-200 shadow-sm p-6">
          <h2 className="text-sm font-bold text-slate-900 mb-4">Audit Information</h2>
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-5">
            <InfoRow label="User ID" value={user.id} mono />
            <InfoRow label="Account Status" value={user.accountStatus} />
            <InfoRow label="KYC Status" value={user.kycStatus} />
            <InfoRow label="Preferred Language" value={user.preferredLanguage} />
          </div>
        </div>
      </main>

      {/* Suspend Modal */}
      {suspendModal && (
        <div className="fixed inset-0 bg-slate-900/40 backdrop-blur-sm flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-2xl border border-slate-200 p-6 w-full max-w-sm shadow-xl">
            <h3 className="font-bold text-slate-900 mb-1">Suspend Account</h3>
            <p className="text-xs text-slate-500 mb-4">{user.phone}</p>
            <textarea value={suspendReason} onChange={e => setSuspendReason(e.target.value)}
              rows={3} placeholder="Reason for suspension..."
              className="w-full bg-slate-50 border border-slate-200 rounded-xl p-3 text-xs focus:outline-none focus:border-rose-400 resize-none" />
            <div className="flex gap-2 mt-4">
              <button onClick={() => setSuspendModal(false)}
                className="flex-1 py-2.5 border border-slate-200 text-slate-600 rounded-xl text-xs font-bold hover:bg-slate-50">
                Cancel
              </button>
              <button onClick={confirmSuspend} disabled={acting}
                className="flex-1 py-2.5 bg-rose-600 text-white rounded-xl text-xs font-bold hover:bg-rose-700 disabled:opacity-50">
                {acting ? 'Suspending...' : 'Confirm Suspend'}
              </button>
            </div>
          </div>
        </div>
      )}

      {/* KYC Modal */}
      {kycModal && (
        <div className="fixed inset-0 bg-slate-900/40 backdrop-blur-sm flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-2xl border border-slate-200 p-6 w-full max-w-sm shadow-xl">
            <h3 className="font-bold text-slate-900 mb-1">Update KYC Status</h3>
            <p className="text-xs text-slate-500 mb-4">
              Set KYC for <span className="font-bold text-slate-700">{user.phone}</span> to{' '}
              <span className={`font-bold ${pendingKycStatus === 'VERIFIED' ? 'text-emerald-700' : 'text-red-600'}`}>
                {pendingKycStatus}
              </span>?
            </p>
            <div className="flex gap-2 mt-4">
              <button onClick={() => setKycModal(false)}
                className="flex-1 py-2.5 border border-slate-200 text-slate-600 rounded-xl text-xs font-bold hover:bg-slate-50">
                Cancel
              </button>
              <button onClick={confirmKyc} disabled={acting}
                className={`flex-1 py-2.5 text-white rounded-xl text-xs font-bold disabled:opacity-50 ${
                  pendingKycStatus === 'VERIFIED' ? 'bg-emerald-700 hover:bg-emerald-800' : 'bg-red-600 hover:bg-red-700'
                }`}>
                {acting ? 'Updating...' : 'Confirm'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

function InfoRow({ label, value, mono }: { label: string; value: string; mono?: boolean }) {
  return (
    <div>
      <p className="text-[10px] font-bold uppercase tracking-widest text-slate-400 mb-1">{label}</p>
      <p className={`text-sm font-semibold text-slate-800 break-all ${mono ? 'font-mono text-xs' : ''}`}>
        {value}
      </p>
    </div>
  );
}

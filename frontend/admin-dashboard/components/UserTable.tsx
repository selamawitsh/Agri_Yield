'use client';
import { useState } from 'react';
import { User } from '@/lib/types';
import toast from 'react-hot-toast';
import api from '@/lib/api';

interface UserTableProps {
  users: User[];
  onUserUpdated: () => void;
  onViewDetail: (id: string) => void;
}

export default function UserTable({ users, onUserUpdated, onViewDetail }: UserTableProps) {
  const [updatingId, setUpdatingId] = useState<string | null>(null);
  const [suspendModal, setSuspendModal] = useState(false);
  const [kycModal, setKycModal] = useState(false);
  const [selectedUser, setSelectedUser] = useState<User | null>(null);
  const [suspendReason, setSuspendReason] = useState('');
  const [pendingKycStatus, setPendingKycStatus] = useState('');

  // US-10: Suspend
  function openSuspend(user: User) {
    setSelectedUser(user); setSuspendModal(true);
  }
  async function confirmSuspend() {
    if (!selectedUser) return;
    setUpdatingId(selectedUser.id);
    try {
      await api.patch(`/admin/users/${selectedUser.id}/status`, {
        status: 'SUSPENDED',
        reason: suspendReason || 'No reason provided',
      });
      toast.success(`${selectedUser.phone} suspended`);
      onUserUpdated();
      setSuspendModal(false); setSuspendReason('');
    } catch { toast.error('Suspend failed'); }
    finally { setUpdatingId(null); }
  }

  // US-10: Reactivate
  async function handleActivate(user: User) {
    setUpdatingId(user.id);
    try {
      await api.patch(`/admin/users/${user.id}/status`, { status: 'ACTIVE' });
      toast.success(`${user.phone} reactivated`);
      onUserUpdated();
    } catch { toast.error('Activation failed'); }
    finally { setUpdatingId(null); }
  }

  // KYC: Verify or Reject
  function openKyc(user: User, status: string) {
    setSelectedUser(user); setPendingKycStatus(status); setKycModal(true);
  }
  async function confirmKyc() {
    if (!selectedUser) return;
    setUpdatingId(selectedUser.id);
    try {
      await api.patch(`/admin/users/${selectedUser.id}/kyc`, { kycStatus: pendingKycStatus });
      toast.success(`KYC set to ${pendingKycStatus}`);
      onUserUpdated();
      setKycModal(false);
    } catch { toast.error('KYC update failed'); }
    finally { setUpdatingId(null); }
  }

  function roleBadge(role: string) {
    const colors: Record<string, string> = {
      FARMER: 'bg-green-100 text-green-800',
      INVESTOR: 'bg-blue-100 text-blue-800',
      MERCHANT: 'bg-orange-100 text-orange-800',
      OFF_TAKER: 'bg-purple-100 text-purple-800',
      ADMIN: 'bg-slate-200 text-slate-800',
    };
    return colors[role] || 'bg-slate-100 text-slate-700';
  }

  function kycBadge(status: string) {
    const colors: Record<string, string> = {
      VERIFIED: 'bg-emerald-100 text-emerald-800',
      PENDING: 'bg-amber-100 text-amber-800',
      REJECTED: 'bg-red-100 text-red-700',
    };
    return colors[status] || 'bg-slate-100 text-slate-700';
  }

  function statusBadge(status: string) {
    const colors: Record<string, string> = {
      ACTIVE: 'bg-emerald-100 text-emerald-800',
      SUSPENDED: 'bg-rose-100 text-rose-700',
      PENDING_VERIFICATION: 'bg-yellow-100 text-yellow-800',
    };
    return colors[status] || 'bg-slate-100 text-slate-700';
  }

  return (
    <>
      <div className="overflow-x-auto">
        <table className="w-full text-left">
          <thead>
            <tr className="border-b border-slate-100 bg-slate-50/50 text-slate-400 text-[11px] font-bold uppercase tracking-wider">
              <th className="px-6 py-3">Phone</th>
              <th className="px-6 py-3">Fayda ID</th>
              <th className="px-6 py-3">Role</th>
              <th className="px-6 py-3">KYC</th>
              <th className="px-6 py-3">Status</th>
              <th className="px-6 py-3">Joined</th>
              <th className="px-6 py-3 text-right">Actions</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-50 text-xs text-slate-700">
            {users.length === 0 ? (
              <tr>
                <td colSpan={7} className="px-6 py-16 text-center text-slate-400 font-medium">
                  No users found matching the current filters
                </td>
              </tr>
            ) : (
              users.map(user => (
                <tr key={user.id} className="hover:bg-slate-50/60 transition-colors">
                  <td className="px-6 py-3.5 font-bold font-mono text-slate-900">{user.phone}</td>
                  <td className="px-6 py-3.5 font-mono text-slate-500 text-[11px]">
                    {user.faydaId ? user.faydaId.slice(0, 12) + '...' : '—'}
                  </td>
                  <td className="px-6 py-3.5">
                    <span className={`px-2 py-0.5 rounded-full text-[10px] font-bold ${roleBadge(user.role)}`}>
                      {user.role}
                    </span>
                  </td>
                  <td className="px-6 py-3.5">
                    <span className={`px-2 py-0.5 rounded-md text-[10px] font-bold ${kycBadge(user.kycStatus)}`}>
                      {user.kycStatus}
                    </span>
                  </td>
                  <td className="px-6 py-3.5">
                    <span className={`px-2 py-0.5 rounded-md text-[10px] font-bold ${statusBadge(user.accountStatus)}`}>
                      {user.accountStatus.replace('_', ' ')}
                    </span>
                  </td>
                  <td className="px-6 py-3.5 text-slate-400 text-[11px]">
                    {user.createdAt ? new Date(user.createdAt).toLocaleDateString() : '—'}
                  </td>
                  <td className="px-6 py-3.5">
                    <div className="flex items-center justify-end gap-3">
                      {/* View detail — US-09 */}
                      <button
                        onClick={() => onViewDetail(user.id)}
                        className="text-emerald-700 hover:text-emerald-900 font-bold text-xs">
                        View
                      </button>
                      <span className="w-px h-3 bg-slate-200" />
                      {/* KYC actions */}
                      {user.kycStatus === 'PENDING' && (
                        <>
                          <button
                            onClick={() => openKyc(user, 'VERIFIED')}
                            disabled={updatingId === user.id}
                            className="text-blue-600 hover:text-blue-800 font-bold text-xs">
                            Verify
                          </button>
                          <button
                            onClick={() => openKyc(user, 'REJECTED')}
                            disabled={updatingId === user.id}
                            className="text-red-500 hover:text-red-700 font-bold text-xs">
                            Reject
                          </button>
                          <span className="w-px h-3 bg-slate-200" />
                        </>
                      )}
                      {user.kycStatus === 'REJECTED' && (
                        <>
                          <button
                            onClick={() => openKyc(user, 'VERIFIED')}
                            disabled={updatingId === user.id}
                            className="text-blue-600 hover:text-blue-800 font-bold text-xs">
                            Re-verify
                          </button>
                          <span className="w-px h-3 bg-slate-200" />
                        </>
                      )}
                      {/* US-10: Suspend / Activate */}
                      {user.accountStatus === 'ACTIVE' ? (
                        <button
                          onClick={() => openSuspend(user)}
                          disabled={updatingId === user.id}
                          className="text-rose-600 hover:text-rose-800 font-bold text-xs">
                          Suspend
                        </button>
                      ) : user.accountStatus === 'SUSPENDED' ? (
                        <button
                          onClick={() => handleActivate(user)}
                          disabled={updatingId === user.id}
                          className="text-emerald-700 hover:text-emerald-900 font-bold text-xs">
                          Activate
                        </button>
                      ) : null}
                    </div>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {/* Suspend Modal */}
      {suspendModal && selectedUser && (
        <div className="fixed inset-0 bg-slate-900/40 backdrop-blur-sm flex items-center justify-center z-50 p-4">
          <div className="bg-white border border-slate-200 rounded-2xl p-6 w-full max-w-sm shadow-xl">
            <h3 className="font-bold text-slate-900 mb-1">Suspend Account</h3>
            <p className="text-xs text-slate-500 mb-4">{selectedUser.phone} • {selectedUser.role}</p>
            <textarea
              value={suspendReason}
              onChange={e => setSuspendReason(e.target.value)}
              rows={3}
              placeholder="Reason for suspension (shown in audit log)..."
              className="w-full bg-slate-50 border border-slate-200 rounded-xl p-3 text-xs focus:outline-none focus:border-emerald-700 resize-none"
            />
            <div className="flex gap-2 mt-4">
              <button onClick={() => setSuspendModal(false)}
                className="flex-1 px-4 py-2.5 border border-slate-200 text-slate-600 rounded-xl text-xs font-bold hover:bg-slate-50">
                Cancel
              </button>
              <button onClick={confirmSuspend}
                className="flex-1 px-4 py-2.5 bg-rose-600 text-white rounded-xl text-xs font-bold hover:bg-rose-700">
                Confirm Suspend
              </button>
            </div>
          </div>
        </div>
      )}

      {/* KYC Modal */}
      {kycModal && selectedUser && (
        <div className="fixed inset-0 bg-slate-900/40 backdrop-blur-sm flex items-center justify-center z-50 p-4">
          <div className="bg-white border border-slate-200 rounded-2xl p-6 w-full max-w-sm shadow-xl">
            <h3 className="font-bold text-slate-900 mb-1">Update KYC Status</h3>
            <p className="text-xs text-slate-500 mb-4">
              Set KYC for <span className="font-bold text-slate-700">{selectedUser.phone}</span> to{' '}
              <span className={`font-bold ${pendingKycStatus === 'VERIFIED' ? 'text-emerald-700' : 'text-red-600'}`}>
                {pendingKycStatus}
              </span>?
            </p>
            <div className="flex gap-2 mt-4">
              <button onClick={() => setKycModal(false)}
                className="flex-1 px-4 py-2.5 border border-slate-200 text-slate-600 rounded-xl text-xs font-bold hover:bg-slate-50">
                Cancel
              </button>
              <button onClick={confirmKyc}
                className={`flex-1 px-4 py-2.5 text-white rounded-xl text-xs font-bold ${
                  pendingKycStatus === 'VERIFIED' ? 'bg-emerald-700 hover:bg-emerald-800' : 'bg-red-600 hover:bg-red-700'
                }`}>
                Confirm
              </button>
            </div>
          </div>
        </div>
      )}
    </>
  );
}

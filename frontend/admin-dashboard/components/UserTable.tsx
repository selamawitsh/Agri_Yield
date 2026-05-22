'use client';

import { User } from '@/lib/types';
import { useState } from 'react';
import toast from 'react-hot-toast';
import api from '@/lib/api';

interface UserTableProps {
  users: User[];
  onUserUpdated: () => void;
}

export default function UserTable({ users, onUserUpdated }: UserTableProps) {
  const [updatingUserId, setUpdatingUserId] = useState<string | null>(null);
  const [showReasonModal, setShowReasonModal] = useState(false);
  const [showKycModal, setShowKycModal] = useState(false);
  const [selectedUser, setSelectedUser] = useState<User | null>(null);
  const [suspendReason, setSuspendReason] = useState('');
  const [kycStatus, setKycStatus] = useState('');

  const handleSuspend = async (user: User) => {
    setSelectedUser(user);
    setShowReasonModal(true);
  };

  const confirmSuspend = async () => {
    if (!selectedUser) return;
    setUpdatingUserId(selectedUser.id);
    try {
      await api.patch(`/admin/users/${selectedUser.id}/status`, {
        status: 'SUSPENDED',
        reason: suspendReason || 'No reason provided'
      });
      toast.success('User isolated from active pool');
      onUserUpdated();
      setShowReasonModal(false);
      setSuspendReason('');
    } catch (error) {
      toast.error('Operation failed');
    } finally {
      setUpdatingUserId(null);
    }
  };

  const handleActivate = async (user: User) => {
    setUpdatingUserId(user.id);
    try {
      await api.patch(`/admin/users/${user.id}/status`, { status: 'ACTIVE' });
      toast.success('User status restored');
      onUserUpdated();
    } catch (error) {
      toast.error('Activation error');
    } finally {
      setUpdatingUserId(null);
    }
  };

  const handleKycUpdate = async (user: User, status: string) => {
    setSelectedUser(user);
    setKycStatus(status);
    setShowKycModal(true);
  };

  const confirmKycUpdate = async () => {
    if (!selectedUser) return;
    setUpdatingUserId(selectedUser.id);
    try {
      await api.patch(`/admin/users/${selectedUser.id}/kyc`, { kycStatus });
      toast.success(`Identity status: ${kycStatus}`);
      onUserUpdated();
      setShowKycModal(false);
    } catch (error) {
      toast.error('Ledger write failed');
    } finally {
      setUpdatingUserId(null);
    }
  };

  return (
      <>
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse">
            <thead>
            <tr className="border-b border-slate-200 text-slate-400 font-bold text-[11px] uppercase tracking-wider bg-slate-50/70">
              <th className="px-6 py-4">Phone Identity</th>
              <th className="px-6 py-4">National ID</th>
              <th className="px-6 py-4">Assigned Role</th>
              <th className="px-6 py-4">KYC State</th>
              <th className="px-6 py-4">Account Status</th>
              <th className="px-6 py-4 text-right">Actions Panel</th>
            </tr>
            </thead>
            <tbody className="divide-y divide-slate-100 text-xs font-semibold text-slate-700 bg-white">
            {users.length === 0 ? (
                <tr>
                  <td colSpan={6} className="px-6 py-12 text-center text-slate-400">
                    No records matching the filter targets.
                  </td>
                </tr>
            ) : (
                users.map((user) => (
                    <tr key={user.id} className="hover:bg-slate-50/60 transition-colors">
                      <td className="px-6 py-4 text-slate-900 font-bold font-mono tracking-tight">{user.phone}</td>
                      <td className="px-6 py-4 font-mono text-slate-500">{user.faydaId || '—'}</td>
                      <td className="px-6 py-4">
                    <span className="px-2.5 py-1 rounded-full border text-[10px] font-extrabold tracking-wide bg-stone-100 text-stone-800 border-stone-200">
                      {user.role}
                    </span>
                      </td>
                      <td className="px-6 py-4">
                    <span className={`inline-flex items-center gap-1.5 px-2.5 py-1 rounded-md text-[11px] font-bold ${
                        user.kycStatus === 'VERIFIED' ? 'text-emerald-800 bg-emerald-50' : 'text-amber-800 bg-amber-50'
                    }`}>
                      {user.kycStatus}
                    </span>
                      </td>
                      <td className="px-6 py-4">
                    <span className={`text-[10px] uppercase px-2 py-0.5 rounded-md font-black tracking-wider ${
                        user.accountStatus === 'ACTIVE' ? 'bg-emerald-100 text-emerald-800' : 'bg-rose-100 text-rose-800'
                    }`}>
                      {user.accountStatus}
                    </span>
                      </td>
                      <td className="px-6 py-4 text-right">
                        <div className="inline-flex items-center gap-3">
                          {user.kycStatus !== 'VERIFIED' && (
                              <button
                                  onClick={() => handleKycUpdate(user, 'VERIFIED')}
                                  disabled={updatingUserId === user.id}
                                  className="text-emerald-700 hover:text-emerald-900 font-bold text-xs"
                              >
                                Verify
                              </button>
                          )}
                          <span className="w-px h-3 bg-slate-200" />
                          {user.accountStatus === 'ACTIVE' ? (
                              <button
                                  onClick={() => handleSuspend(user)}
                                  disabled={updatingUserId === user.id}
                                  className="text-rose-600 hover:text-rose-800 font-bold text-xs"
                              >
                                Suspend
                              </button>
                          ) : (
                              <button
                                  onClick={() => handleActivate(user)}
                                  disabled={updatingUserId === user.id}
                                  className="text-emerald-700 hover:text-emerald-900 font-bold text-xs"
                              >
                                Activate
                              </button>
                          )}
                        </div>
                      </td>
                    </tr>
                ))
            )}
            </tbody>
          </table>
        </div>

        {/* Overlays */}
        {showReasonModal && selectedUser && (
            <div className="fixed inset-0 bg-slate-900/40 backdrop-blur-xs flex items-center justify-center z-50 p-4">
              <div className="bg-white border border-slate-200 rounded-2xl p-6 w-full max-w-sm shadow-xl">
                <h3 className="text-sm font-bold text-slate-900">Suspend Account Access</h3>
                <div className="mt-4">
              <textarea
                  value={suspendReason}
                  onChange={(e) => setSuspendReason(e.target.value)}
                  className="w-full bg-slate-50 text-slate-900 border border-slate-200 rounded-xl p-3 text-xs focus:outline-none focus:border-emerald-700 focus:bg-white"
                  rows={3}
                  placeholder="Log suspension reasoning..."
              />
                </div>
                <div className="flex justify-end gap-2 mt-5 text-xs font-bold">
                  <button onClick={() => setShowReasonModal(false)} className="px-4 py-2 border border-slate-200 text-slate-500 rounded-xl">Cancel</button>
                  <button onClick={confirmSuspend} className="px-4 py-2 bg-rose-600 text-white rounded-xl">Apply Hold</button>
                </div>
              </div>
            </div>
        )}

        {showKycModal && selectedUser && (
            <div className="fixed inset-0 bg-slate-900/40 backdrop-blur-xs flex items-center justify-center z-50 p-4">
              <div className="bg-white border border-slate-200 rounded-2xl p-6 w-full max-w-sm shadow-xl">
                <h3 className="text-sm font-bold text-slate-900">Confirm Verification Shift</h3>
                <p className="text-slate-500 text-xs mt-2">Commit validation flags for {selectedUser.phone} to {kycStatus}?</p>
                <div className="flex justify-end gap-2 mt-6 text-xs font-bold">
                  <button onClick={() => setShowKycModal(false)} className="px-4 py-2 border border-slate-200 text-slate-500 rounded-xl">Cancel</button>
                  <button onClick={confirmKycUpdate} className="px-4 py-2 bg-emerald-800 text-white rounded-xl">Confirm</button>
                </div>
              </div>
            </div>
        )}
      </>
  );
}
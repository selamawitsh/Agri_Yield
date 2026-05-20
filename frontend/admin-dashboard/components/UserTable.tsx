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
      toast.success(`User ${selectedUser.phone} suspended successfully`);
      onUserUpdated();
      setShowReasonModal(false);
      setSuspendReason('');
    } catch (error) {
      toast.error('Failed to suspend user');
    } finally {
      setUpdatingUserId(null);
    }
  };

  const handleActivate = async (user: User) => {
    setUpdatingUserId(user.id);
    try {
      await api.patch(`/admin/users/${user.id}/status`, {
        status: 'ACTIVE'
      });
      toast.success(`User ${user.phone} activated successfully`);
      onUserUpdated();
    } catch (error) {
      toast.error('Failed to activate user');
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
      await api.patch(`/admin/users/${selectedUser.id}/kyc`, {
        kycStatus: kycStatus
      });
      toast.success(`KYC for ${selectedUser.phone} updated to ${kycStatus}`);
      onUserUpdated();
      setShowKycModal(false);
    } catch (error) {
      toast.error('Failed to update KYC status');
    } finally {
      setUpdatingUserId(null);
    }
  };

  const getRoleBadgeColor = (role: string) => {
    const colors: Record<string, string> = {
      FARMER: 'bg-green-100 text-green-800',
      INVESTOR: 'bg-purple-100 text-purple-800',
      MERCHANT: 'bg-orange-100 text-orange-800',
      OFF_TAKER: 'bg-teal-100 text-teal-800',
      ADMIN: 'bg-red-100 text-red-800',
    };
    return colors[role] || 'bg-gray-100 text-gray-800';
  };

  const getStatusBadgeColor = (status: string) => {
    const colors: Record<string, string> = {
      ACTIVE: 'bg-green-100 text-green-800',
      SUSPENDED: 'bg-red-100 text-red-800',
      PENDING_VERIFICATION: 'bg-yellow-100 text-yellow-800',
    };
    return colors[status] || 'bg-gray-100 text-gray-800';
  };

  const getKycBadgeColor = (status: string) => {
    const colors: Record<string, string> = {
      VERIFIED: 'bg-green-100 text-green-800',
      PENDING: 'bg-yellow-100 text-yellow-800',
      REJECTED: 'bg-red-100 text-red-800',
    };
    return colors[status] || 'bg-gray-100 text-gray-800';
  };

  return (
    <>
      <div className="overflow-x-auto">
        <table className="min-w-full divide-y divide-gray-200">
          <thead className="bg-gray-50">
            <tr>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Phone</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Fayda ID</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Role</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">KYC Status</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Account Status</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">KYC Actions</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Account Actions</th>
            </tr>
          </thead>
          <tbody className="bg-white divide-y divide-gray-200">
            {users.map((user) => (
              <tr key={user.id} className="hover:bg-gray-50">
                <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{user.phone}</td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{user.faydaId}</td>
                <td className="px-6 py-4 whitespace-nowrap">
                  <span className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${getRoleBadgeColor(user.role)}`}>
                    {user.role}
                  </span>
                </td>
                <td className="px-6 py-4 whitespace-nowrap">
                  <span className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${getKycBadgeColor(user.kycStatus)}`}>
                    {user.kycStatus}
                  </span>
                </td>
                <td className="px-6 py-4 whitespace-nowrap">
                  <span className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${getStatusBadgeColor(user.accountStatus)}`}>
                    {user.accountStatus}
                  </span>
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                  <div className="flex gap-2">
                    {user.kycStatus !== 'VERIFIED' && (
                      <button
                        onClick={() => handleKycUpdate(user, 'VERIFIED')}
                        disabled={updatingUserId === user.id}
                        className="bg-green-600 text-white px-2 py-1 rounded text-xs hover:bg-green-700"
                      >
                        Verify
                      </button>
                    )}
                    {user.kycStatus === 'PENDING' && (
                      <button
                        onClick={() => handleKycUpdate(user, 'REJECTED')}
                        disabled={updatingUserId === user.id}
                        className="bg-red-600 text-white px-2 py-1 rounded text-xs hover:bg-red-700"
                      >
                        Reject
                      </button>
                    )}
                  </div>
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                  {user.accountStatus === 'ACTIVE' ? (
                    <button
                      onClick={() => handleSuspend(user)}
                      disabled={updatingUserId === user.id}
                      className="text-red-600 hover:text-red-900"
                    >
                      Suspend
                    </button>
                  ) : user.accountStatus === 'SUSPENDED' ? (
                    <button
                      onClick={() => handleActivate(user)}
                      disabled={updatingUserId === user.id}
                      className="text-green-600 hover:text-green-900"
                    >
                      Activate
                    </button>
                  ) : (
                    <span className="text-gray-400">Pending</span>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* Suspend Reason Modal */}
      {showReasonModal && selectedUser && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-6 w-96">
            <h2 className="text-xl font-bold mb-4">Suspend User</h2>
            <p className="text-gray-600 mb-4">Suspend {selectedUser.phone}?</p>
            <div className="mb-4">
              <label className="block text-gray-700 mb-2">Reason</label>
              <textarea
                value={suspendReason}
                onChange={(e) => setSuspendReason(e.target.value)}
                className="w-full px-3 py-2 border rounded-lg"
                rows={3}
                placeholder="Enter reason..."
              />
            </div>
            <div className="flex justify-end gap-3">
              <button onClick={() => setShowReasonModal(false)} className="px-4 py-2 border rounded-lg">Cancel</button>
              <button onClick={confirmSuspend} className="px-4 py-2 bg-red-600 text-white rounded-lg">Suspend</button>
            </div>
          </div>
        </div>
      )}

      {/* KYC Update Modal */}
      {showKycModal && selectedUser && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-6 w-96">
            <h2 className="text-xl font-bold mb-4">Update KYC Status</h2>
            <p className="text-gray-600 mb-4">
              Change KYC status for {selectedUser.phone} to <strong>{kycStatus}</strong>?
            </p>
            <div className="flex justify-end gap-3">
              <button onClick={() => setShowKycModal(false)} className="px-4 py-2 border rounded-lg">Cancel</button>
              <button 
                onClick={confirmKycUpdate} 
                className={`px-4 py-2 rounded-lg text-white ${
                  kycStatus === 'VERIFIED' ? 'bg-green-600' : 'bg-red-600'
                }`}
              >
                Confirm {kycStatus}
              </button>
            </div>
          </div>
        </div>
      )}
    </>
  );
}

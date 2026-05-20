'use client';

import { useState, useEffect } from 'react';
import toast from 'react-hot-toast';
import api from '@/lib/api';

interface BankAccount {
  id: string;
  accountType: string;
  accountNumber: string;
  accountHolderName: string;
  verified: boolean;
  verifiedAt: string | null;
  default: boolean;
}

interface BankAccountManagerProps {
  userId: string;
  onAccountUpdate?: () => void;
}

export default function BankAccountManager({ userId, onAccountUpdate }: BankAccountManagerProps) {
  const [accounts, setAccounts] = useState<BankAccount[]>([]);
  const [loading, setLoading] = useState(true);
  const [showAddForm, setShowAddForm] = useState(false);
  const [showVerifyForm, setShowVerifyForm] = useState(false);
  const [selectedAccountId, setSelectedAccountId] = useState<string | null>(null);
  const [verificationCode, setVerificationCode] = useState('');
  
  const [newAccount, setNewAccount] = useState({
    account_type: 'TELEBIRR',
    account_number: '',
    account_holder_name: '',
  });

  useEffect(() => {
    fetchAccounts();
  }, []);

  const fetchAccounts = async () => {
    try {
      const response = await api.get('/users/me/bank');
      if (response.data.success) {
        setAccounts(response.data.data);
      }
    } catch (error) {
      console.error('Failed to fetch bank accounts', error);
    } finally {
      setLoading(false);
    }
  };

  const addBankAccount = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const response = await api.post('/users/me/bank', newAccount);
      if (response.data.success) {
        toast.success(response.data.message);
        setShowAddForm(false);
        setNewAccount({ account_type: 'TELEBIRR', account_number: '', account_holder_name: '' });
        fetchAccounts();
        if (onAccountUpdate) onAccountUpdate();
      }
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Failed to add bank account');
    }
  };

  const verifyAccount = async () => {
    if (!selectedAccountId) return;
    
    try {
      const response = await api.post('/users/me/bank/verify', {
        account_id: selectedAccountId,
        verification_code: verificationCode,
      });
      if (response.data.success) {
        toast.success('Bank account verified successfully!');
        setShowVerifyForm(false);
        setVerificationCode('');
        setSelectedAccountId(null);
        fetchAccounts();
        if (onAccountUpdate) onAccountUpdate();
      }
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Verification failed');
    }
  };

  const setDefaultAccount = async (accountId: string) => {
    try {
      const response = await api.post('/users/me/bank/default', {
        account_id: accountId,
      });
      if (response.data.success) {
        toast.success('Default account updated');
        fetchAccounts();
        if (onAccountUpdate) onAccountUpdate();
      }
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Failed to set default account');
    }
  };

  const deleteAccount = async (accountId: string) => {
    if (!confirm('Are you sure you want to delete this bank account?')) return;
    
    try {
      await api.delete(`/users/me/bank/${accountId}`);
      toast.success('Bank account deleted');
      fetchAccounts();
      if (onAccountUpdate) onAccountUpdate();
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Failed to delete account');
    }
  };

  const maskAccountNumber = (number: string) => {
    if (!number) return '****';
    return '****' + number.slice(-4);
  };

  if (loading) {
    return <div className="text-center py-4">Loading bank accounts...</div>;
  }

  return (
    <div className="space-y-4">
      <div className="flex justify-between items-center">
        <h3 className="text-lg font-semibold">Bank Accounts</h3>
        <button
          onClick={() => setShowAddForm(!showAddForm)}
          className="text-green-600 hover:text-green-700 text-sm"
        >
          + Add Account
        </button>
      </div>

      {/* Add Account Form */}
      {showAddForm && (
        <form onSubmit={addBankAccount} className="bg-gray-50 p-4 rounded-lg space-y-3">
          <div>
            <label className="block text-sm text-gray-700 mb-1">Account Type</label>
            <select
              value={newAccount.account_type}
              onChange={(e) => setNewAccount({...newAccount, account_type: e.target.value})}
              className="w-full px-3 py-2 border rounded-lg"
              required
            >
              <option value="TELEBIRR">Telebirr</option>
              <option value="CBE">CBE (Commercial Bank of Ethiopia)</option>
            </select>
          </div>
          <div>
            <label className="block text-sm text-gray-700 mb-1">Account Number</label>
            <input
              type="text"
              value={newAccount.account_number}
              onChange={(e) => setNewAccount({...newAccount, account_number: e.target.value})}
              placeholder={newAccount.account_type === 'TELEBIRR' ? '10-digit phone number' : '10-16 digit account number'}
              className="w-full px-3 py-2 border rounded-lg"
              required
            />
          </div>
          <div>
            <label className="block text-sm text-gray-700 mb-1">Account Holder Name</label>
            <input
              type="text"
              value={newAccount.account_holder_name}
              onChange={(e) => setNewAccount({...newAccount, account_holder_name: e.target.value})}
              placeholder="Full name as on account"
              className="w-full px-3 py-2 border rounded-lg"
            />
          </div>
          <div className="flex gap-2">
            <button type="submit" className="bg-green-600 text-white px-4 py-2 rounded-lg hover:bg-green-700">
              Add Account
            </button>
            <button
              type="button"
              onClick={() => setShowAddForm(false)}
              className="bg-gray-500 text-white px-4 py-2 rounded-lg hover:bg-gray-600"
            >
              Cancel
            </button>
          </div>
          <p className="text-xs text-gray-500 mt-2">
            A 1 ETB test deposit will be sent to verify your account.
          </p>
        </form>
      )}

      {/* Accounts List */}
      {accounts.length === 0 ? (
        <div className="text-center py-8 bg-gray-50 rounded-lg">
          <p className="text-gray-500">No bank accounts linked</p>
          <p className="text-sm text-gray-400 mt-1">Add an account to receive payouts</p>
        </div>
      ) : (
        <div className="space-y-3">
          {accounts.map((account) => (
            <div
              key={account.id}
              className={`border rounded-lg p-4 ${account.default ? 'border-green-500 bg-green-50' : 'border-gray-200'}`}
            >
              <div className="flex justify-between items-start">
                <div>
                  <div className="flex items-center gap-2">
                    <span className="font-semibold">{account.accountType}</span>
                    {account.default && (
                      <span className="bg-green-600 text-white text-xs px-2 py-0.5 rounded">Default</span>
                    )}
                    {account.verified ? (
                      <span className="bg-green-100 text-green-800 text-xs px-2 py-0.5 rounded">✓ Verified</span>
                    ) : (
                      <span className="bg-yellow-100 text-yellow-800 text-xs px-2 py-0.5 rounded">⚠ Pending Verification</span>
                    )}
                  </div>
                  <p className="text-gray-700 mt-1">{maskAccountNumber(account.accountNumber)}</p>
                  <p className="text-sm text-gray-500">{account.accountHolderName}</p>
                  {account.verifiedAt && (
                    <p className="text-xs text-gray-400 mt-1">
                      Verified: {new Date(account.verifiedAt).toLocaleDateString()}
                    </p>
                  )}
                </div>
                <div className="flex gap-2">
                  {!account.verified && (
                    <button
                      onClick={() => {
                        setSelectedAccountId(account.id);
                        setShowVerifyForm(true);
                      }}
                      className="text-blue-600 hover:text-blue-700 text-sm"
                    >
                      Verify
                    </button>
                  )}
                  {!account.default && account.verified && (
                    <button
                      onClick={() => setDefaultAccount(account.id)}
                      className="text-green-600 hover:text-green-700 text-sm"
                    >
                      Set Default
                    </button>
                  )}
                  {!account.default && (
                    <button
                      onClick={() => deleteAccount(account.id)}
                      className="text-red-600 hover:text-red-700 text-sm"
                    >
                      Delete
                    </button>
                  )}
                </div>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Verify Modal */}
      {showVerifyForm && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-6 w-96">
            <h2 className="text-xl font-bold mb-4">Verify Bank Account</h2>
            <p className="text-gray-600 mb-4">
              A 1 ETB test deposit was sent to your account. Enter the verification code.
            </p>
            <p className="text-sm text-gray-500 mb-4">
              Demo code: <strong>ETB1</strong>
            </p>
            <div className="mb-4">
              <label className="block text-gray-700 mb-2">Verification Code</label>
              <input
                type="text"
                value={verificationCode}
                onChange={(e) => setVerificationCode(e.target.value)}
                placeholder="Enter code (e.g., ETB1)"
                className="w-full px-3 py-2 border rounded-lg"
              />
            </div>
            <div className="flex justify-end gap-3">
              <button
                onClick={() => {
                  setShowVerifyForm(false);
                  setVerificationCode('');
                  setSelectedAccountId(null);
                }}
                className="px-4 py-2 border rounded-lg hover:bg-gray-50"
              >
                Cancel
              </button>
              <button
                onClick={verifyAccount}
                className="px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700"
              >
                Verify
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

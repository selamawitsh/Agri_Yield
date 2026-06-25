'use client';

import { useState, useEffect } from 'react';
import toast from 'react-hot-toast';
import api from '@/lib/api';
import { BankAccount } from '@/lib/types';

export default function BankAccountManager() {
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
        toast.success('Bank account verified!');
        setShowVerifyForm(false);
        setVerificationCode('');
        setSelectedAccountId(null);
        fetchAccounts();
      }
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Verification failed');
    }
  };

  const setDefaultAccount = async (accountId: string) => {
    try {
      const response = await api.post('/users/me/bank/default', { account_id: accountId });
      if (response.data.success) {
        toast.success('Default account updated');
        fetchAccounts();
      }
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Failed to set default');
    }
  };

  const deleteAccount = async (accountId: string) => {
    if (!confirm('Are you sure you want to delete this bank account?')) return;
    try {
      await api.delete(`/users/me/bank/${accountId}`);
      toast.success('Bank account deleted');
      fetchAccounts();
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Failed to delete');
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
        <h3 className="text-lg font-semibold">Payment Accounts</h3>
        <button onClick={() => setShowAddForm(!showAddForm)} className="text-teal-600 hover:text-teal-700 text-sm">
          + Add Account
        </button>
      </div>

      {showAddForm && (
        <form onSubmit={addBankAccount} className="bg-gray-50 p-4 rounded-lg space-y-3">
          <select
            value={newAccount.account_type}
            onChange={(e) => setNewAccount({...newAccount, account_type: e.target.value})}
            className="w-full px-3 py-2 border rounded-lg"
          >
            <option value="TELEBIRR">Telebirr</option>
            <option value="CBE">CBE Bank</option>
          </select>
          <input
            type="text"
            placeholder="Account Number"
            value={newAccount.account_number}
            onChange={(e) => setNewAccount({...newAccount, account_number: e.target.value})}
            className="w-full px-3 py-2 border rounded-lg"
            required
          />
          <input
            type="text"
            placeholder="Account Holder Name"
            value={newAccount.account_holder_name}
            onChange={(e) => setNewAccount({...newAccount, account_holder_name: e.target.value})}
            className="w-full px-3 py-2 border rounded-lg"
          />
          <div className="flex gap-2">
            <button type="submit" className="bg-teal-600 text-white px-4 py-2 rounded-lg">Add</button>
            <button type="button" onClick={() => setShowAddForm(false)} className="bg-gray-500 text-white px-4 py-2 rounded-lg">Cancel</button>
          </div>
          <p className="text-xs text-gray-500">A 1 ETB test deposit will be sent. Use code 'ETB1' to verify.</p>
        </form>
      )}

      {accounts.length === 0 ? (
        <div className="text-center py-8 bg-gray-50 rounded-lg">
          <p className="text-gray-500">No payment accounts linked</p>
        </div>
      ) : (
        accounts.map((account) => (
          <div key={account.id} className={`border rounded-lg p-4 ${account.default ? 'border-teal-500 bg-teal-50' : 'border-gray-200'}`}>
            <div className="flex justify-between items-start">
              <div>
                <div className="flex items-center gap-2">
                  <span className="font-semibold">{account.accountType}</span>
                  {account.default && <span className="bg-teal-600 text-white text-xs px-2 py-0.5 rounded">Default</span>}
                  {account.verified ? (
                    <span className="bg-green-100 text-green-800 text-xs px-2 py-0.5 rounded">Verified</span>
                  ) : (
                    <span className="bg-yellow-100 text-yellow-800 text-xs px-2 py-0.5 rounded">Pending</span>
                  )}
                </div>
                <p className="text-gray-700 mt-1">{maskAccountNumber(account.accountNumber)}</p>
                <p className="text-sm text-gray-500">{account.accountHolderName}</p>
              </div>
              <div className="flex gap-2">
                {!account.verified && (
                  <button onClick={() => { setSelectedAccountId(account.id); setShowVerifyForm(true); }} className="text-blue-600 text-sm">
                    Verify
                  </button>
                )}
                {!account.default && account.verified && (
                  <button onClick={() => setDefaultAccount(account.id)} className="text-teal-600 text-sm">
                    Set Default
                  </button>
                )}
                {!account.default && (
                  <button onClick={() => deleteAccount(account.id)} className="text-red-600 text-sm">
                    Delete
                  </button>
                )}
              </div>
            </div>
          </div>
        ))
      )}

      {showVerifyForm && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-6 w-96">
            <h2 className="text-xl font-bold mb-4">Verify Bank Account</h2>
            <p className="text-gray-600 mb-4">Enter verification code (Demo: ETB1)</p>
            <input
              type="text"
              value={verificationCode}
              onChange={(e) => setVerificationCode(e.target.value)}
              placeholder="Enter code"
              className="w-full px-3 py-2 border rounded-lg mb-4"
            />
            <div className="flex justify-end gap-3">
              <button onClick={() => setShowVerifyForm(false)} className="px-4 py-2 border rounded-lg">Cancel</button>
              <button onClick={verifyAccount} className="px-4 py-2 bg-teal-600 text-white rounded-lg">Verify</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

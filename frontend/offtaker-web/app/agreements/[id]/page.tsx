'use client';

import { useEffect, useState } from 'react';
import { useRouter, useParams } from 'next/navigation';
import toast from 'react-hot-toast';
import Navbar from '@/components/Navbar';
import Icon from '@/components/Icons';
import { getAgreement, signAgreement, getBidById } from '@/lib/api';
import type { Agreement, Bid } from '@/lib/types';

export default function AgreementPage() {
  const router = useRouter();
  const params = useParams();
  const agreementId = params.id as string;

  const [agreement, setAgreement] = useState<Agreement | null>(null);
  const [bid,       setBid]       = useState<Bid | null>(null);
  const [loading,   setLoading]   = useState(true);
  const [signing,   setSigning]   = useState(false);
  const [confirmed, setConfirmed] = useState(false);
  const [notFound,  setNotFound]  = useState(false);

  useEffect(() => {
    if (!localStorage.getItem('access_token')) { router.push('/login'); return; }
    load();
  }, [agreementId]);

  const load = async () => {
    setLoading(true);
    try {
      const agreementRes = await getAgreement(agreementId);
      if (agreementRes.data.success) {
        const ag = agreementRes.data.data;
        setAgreement(ag);
        try {
          const bidRes = await getBidById(ag.bidId);
          if (bidRes.data.success) setBid(bidRes.data.data);
        } catch {
          // Bid context is optional
        }
      }
    } catch (err: any) {
      if (err.response?.status === 404) {
        setNotFound(true);
      } else {
        toast.error('Failed to load agreement');
      }
    } finally {
      setLoading(false);
    }
  };

  const handleSign = async () => {
    if (!agreement) return;
    if (!confirmed) {
      toast.error('Please confirm you have reviewed the contract');
      return;
    }
    setSigning(true);
    try {
      const res = await signAgreement(agreement.id);
      if (res.data.success) {
        toast.success(res.data.data.fullyExecuted
          ? 'Contract fully executed!'
          : 'Your signature recorded. Waiting for the other party.');
        setAgreement(res.data.data);
      }
    } catch (err: any) {
      toast.error(err.response?.data?.message || 'Signing failed');
    } finally {
      setSigning(false);
    }
  };

  if (loading) return (
    <div className="min-h-screen flex items-center justify-center">
      <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-teal-600" />
    </div>
  );

  if (notFound) return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />
      <div className="container mx-auto px-4 py-12 max-w-3xl text-center">
        <p className="text-3xl mb-3"></p>
        <p className="text-gray-700 font-semibold text-lg">Agreement not found</p>
        <p className="text-gray-400 text-sm mt-2">
          This agreement may not exist yet. The farmer must accept the bid first.
        </p>
        <button onClick={() => router.push('/bids')}
          className="mt-5 bg-teal-700 text-white px-5 py-2 rounded-xl text-sm font-semibold hover:bg-teal-600">
          ← Back to Bids
        </button>
      </div>
    </div>
  );

  return (
    <div className="min-h-screen bg-gray-50 pb-20 md:pb-0">
      <Navbar />
      <div className="container mx-auto px-4 sm:px-6 py-6 max-w-3xl">

        <button onClick={() => router.push('/bids')}
          className="text-teal-700 text-sm font-semibold mb-6 hover:underline flex items-center gap-2">
          ← Back to Bids
        </button>

        <h1 className="text-2xl font-bold text-gray-900 mb-6">Purchase Agreement</h1>

        {bid && (
          <div className="bg-white rounded-2xl shadow-sm border border-gray-100 p-5 mb-5">
            <h3 className="font-bold text-gray-800 mb-3">Bid Details</h3>
            <div className="grid grid-cols-2 md:grid-cols-3 gap-4 text-sm">
              <div><p className="text-gray-400 text-xs">Quantity</p>
                <p className="font-bold">{bid.quantityQuintals} quintals</p></div>
              <div><p className="text-gray-400 text-xs">Price</p>
                <p className="font-bold">{bid.pricePerQuintalEtb.toLocaleString()} ETB/qt</p></div>
              <div><p className="text-gray-400 text-xs">Total Value</p>
                <p className="font-bold text-teal-700">{bid.totalValueEtb.toLocaleString()} ETB</p></div>
              <div><p className="text-gray-400 text-xs">Deposit (10%)</p>
                <p className="font-bold">{bid.bidDepositEtb.toLocaleString()} ETB</p></div>
              <div><p className="text-gray-400 text-xs">Farm ID</p>
                <p className="font-mono text-xs text-gray-600">{bid.farmId}</p></div>
              <div><p className="text-gray-400 text-xs">Bid Status</p>
                <p className="font-bold">{bid.status}</p></div>
            </div>
          </div>
        )}

        {agreement && (
          <div className="bg-white rounded-2xl shadow-sm border border-gray-100 p-5 mb-5">
            <h3 className="font-bold text-gray-800 mb-4">Contract Status</h3>

            <div className="space-y-3 mb-5">
                <div className={`flex items-center gap-3 p-3 rounded-xl border ${
                agreement.farmerSignedAt
                  ? 'border-green-200 bg-green-50'
                  : 'border-gray-100 bg-gray-50'
              }`}>
                <span className="text-xl">{agreement.farmerSignedAt ? <Icon name="check" className="h-5 w-5 text-green-700"/> : <Icon name="clock" className="h-5 w-5 text-gray-400"/>}</span>
                <div>
                  <p className="text-sm font-semibold text-gray-800">Farmer Signature</p>
                  <p className="text-xs text-gray-400">
                    {agreement.farmerSignedAt
                      ? `Signed ${new Date(agreement.farmerSignedAt).toLocaleString()}`
                      : 'Awaiting farmer signature'}
                  </p>
                </div>
              </div>
                <div className={`flex items-center gap-3 p-3 rounded-xl border ${
                agreement.offtakerSignedAt
                  ? 'border-green-200 bg-green-50'
                  : 'border-gray-100 bg-gray-50'
              }`}>
                <span className="text-xl">{agreement.offtakerSignedAt ? <Icon name="check" className="h-5 w-5 text-green-700"/> : <Icon name="clock" className="h-5 w-5 text-gray-400"/>}</span>
                <div>
                  <p className="text-sm font-semibold text-gray-800">Your Signature</p>
                  <p className="text-xs text-gray-400">
                    {agreement.offtakerSignedAt
                      ? `Signed ${new Date(agreement.offtakerSignedAt).toLocaleString()}`
                      : 'Your signature pending'}
                  </p>
                </div>
              </div>
            </div>

            {agreement.fullyExecuted ? (
              <div className="bg-green-50 border border-green-200 rounded-xl p-4 text-center">
                <p className="text-2xl mb-1"><Icon name="check" className="h-10 w-10 text-green-700"/></p>
                <p className="font-bold text-green-800">Contract Fully Executed</p>
                <p className="text-sm text-green-600 mt-1">
                  Both parties have signed. You can now schedule harvest collection.
                </p>
                <button onClick={() => router.push(`/logistics?agreementId=${agreement.id}`)}
                  className="mt-3 bg-green-700 text-white px-5 py-2 rounded-xl text-sm font-semibold hover:bg-green-800 transition">
                  Schedule Dispatch →
                </button>
              </div>
            ) : !agreement.offtakerSignedAt ? (
              <div className="space-y-4">
                    {agreement.contractPdfUrl && (
                  <div className="border border-gray-200 rounded-xl p-4 bg-gray-50">
                    <p className="text-sm font-semibold text-gray-700 mb-2">Purchase Agreement PDF</p>
                    <a href={agreement.contractPdfUrl} target="_blank" rel="noopener noreferrer"
                      className="text-teal-600 text-sm font-medium hover:underline">
                      Open PDF Contract ↗
                    </a>
                  </div>
                )}
                <label className="flex items-start gap-3 p-4 bg-amber-50 border border-amber-100 rounded-xl cursor-pointer">
                  <input type="checkbox" checked={confirmed} onChange={e => setConfirmed(e.target.checked)}
                    className="mt-1 w-4 h-4 accent-teal-600" />
                  <span className="text-sm text-amber-800">
                    I confirm that I have reviewed the purchase agreement, understand my obligations,
                    and agree to the terms including the 10% bid deposit and delivery schedule.
                  </span>
                </label>
                <button onClick={handleSign} disabled={signing || !confirmed}
                  className="w-full bg-teal-700 text-white py-3 rounded-xl font-bold hover:bg-teal-600 disabled:opacity-50 transition">
                  {signing ? 'Signing with Fayda ID…' : 'Sign Agreement'}
                </button>
                <p className="text-xs text-gray-400 text-center">
                  Your Fayda National ID is used as the digital signature — linked to your account on file
                </p>
              </div>
            ) : (
              <div className="bg-blue-50 border border-blue-100 rounded-xl p-4 text-center">
                <p className="font-bold text-blue-800">You have signed</p>
                <p className="text-sm text-blue-600 mt-1">
                  Waiting for the farmer to sign the agreement.
                </p>
              </div>
            )}
          </div>
        )}

        {agreement?.contractHash && (
          <div className="bg-gray-50 border border-gray-100 rounded-xl p-4 text-xs">
            <p className="text-gray-400 font-semibold mb-1">Contract integrity hash (SHA-256)</p>
            <p className="font-mono text-gray-600 break-all">{agreement.contractHash}</p>
          </div>
        )}
      </div>
    </div>
  );
}

'use client';

import { useEffect, useState } from 'react';
import { useRouter, useSearchParams } from 'next/navigation';
import toast from 'react-hot-toast';
import Navbar from '@/components/Navbar';
import StatusBadge from '@/components/StatusBadge';
import { scheduleDispatch, getDispatchesForAgreement, confirmDelivery, getMyBids } from '@/lib/api';
import type { Bid, Dispatch } from '@/lib/types';

export default function LogisticsPage() {
  const router          = useRouter();
  const searchParams    = useSearchParams();
  const preAgreementId  = searchParams.get('agreementId');

  const [bids,         setBids]         = useState<Bid[]>([]);
  const [dispatches,   setDispatches]   = useState<Record<string, Dispatch[]>>({});
  const [loading,      setLoading]      = useState(true);
  const [showForm,     setShowForm]     = useState(false);
  const [selectedAgreementId, setSelectedAgreementId] = useState('');
  const [submitting,   setSubmitting]   = useState(false);
  const [confirming,   setConfirming]   = useState<string | null>(null);

  const [driverFaydaId, setDriverFaydaId] = useState('');
  const [truckCount,    setTruckCount]    = useState('1');
  const [pickupDate,    setPickupDate]    = useState('');

  const [confirmDispatch, setConfirmDispatch] = useState<string | null>(null);
  const [actualQty,       setActualQty]       = useState('');
  const [qualityGrade,    setQualityGrade]    = useState('A');

  useEffect(() => {
    if (!localStorage.getItem('access_token')) { router.push('/login'); return; }
    load();
  }, []);

  useEffect(() => {
    if (preAgreementId) {
      setSelectedAgreementId(preAgreementId);
      setShowForm(true);
    }
  }, [preAgreementId]);

  const load = async () => {
    try {
      const res = await getMyBids();
      if (res.data.success) {
        // FIX: only bids that have reached CONTRACT_SIGNED/COMPLETED AND have
        // a populated agreementId are relevant here — dispatches are keyed by
        // agreement, not by bid.
        const signedBids = (res.data.data || []).filter(b =>
          ['CONTRACT_SIGNED', 'COMPLETED'].includes(b.status) && b.agreementId);
        setBids(signedBids);

        // FIX: was calling getDispatchesForAgreement(bid.id) — wrong UUID.
        // Now correctly uses bid.agreementId.
        for (const bid of signedBids) {
          if (!bid.agreementId) continue;
          try {
            const dr = await getDispatchesForAgreement(bid.agreementId);
            if (dr.data.success) {
              setDispatches(prev => ({ ...prev, [bid.agreementId as string]: dr.data.data || [] }));
            }
          } catch {
            // No dispatches yet for this agreement — fine
          }
        }
      }
    } catch {
      toast.error('Failed to load logistics');
    } finally {
      setLoading(false);
    }
  };

  const handleSchedule = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedAgreementId) { toast.error('Select a contract'); return; }
    setSubmitting(true);
    try {
      const res = await scheduleDispatch({
        agreementId:         selectedAgreementId,
        driverFaydaId,
        truckCount:          parseInt(truckCount),
        scheduledPickupDate: pickupDate,
      });
      if (res.data.success) {
        toast.success('Dispatch scheduled! Driver penalty deposit locked.');
        setShowForm(false);
        setDriverFaydaId(''); setTruckCount('1'); setPickupDate('');
        load();
      }
    } catch (err: any) {
      toast.error(err.response?.data?.message || 'Failed to schedule');
    } finally {
      setSubmitting(false);
    }
  };

  // FIX: takes agreementId directly now (was taking bid.id before)
  const handleConfirmDelivery = async (agreementId: string) => {
    const qty = parseFloat(actualQty);
    if (isNaN(qty) || qty <= 0) { toast.error('Enter actual quantity'); return; }
    setConfirming(agreementId);
    try {
      const res = await confirmDelivery(agreementId, {
        actualQuantityQuintals: qty,
        qualityGrade,
      });
      if (res.data.success) {
        toast.success('Delivery confirmed! Settlement process initiated.');
        setConfirmDispatch(null);
        setActualQty('');
        load();
      }
    } catch (err: any) {
      toast.error(err.response?.data?.message || 'Failed to confirm delivery');
    } finally {
      setConfirming(null);
    }
  };

  const dispatchStatusIcon: Record<string, string> = {
    SCHEDULED: '📅', ARRIVED: '🚛', LOADED: '📦', DELIVERED: '✅', DRIVER_DEFAULTED: '❌',
  };

  if (loading) return (
    <div className="min-h-screen flex items-center justify-center">
      <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-teal-600" />
    </div>
  );

  return (
    <div className="min-h-screen bg-gray-50 pb-20 md:pb-0">
      <Navbar />
      <div className="container mx-auto px-4 sm:px-6 py-6 max-w-5xl">

        <div className="flex justify-between items-center mb-6">
          <div>
            <h1 className="text-2xl font-bold text-gray-900">Logistics</h1>
            <p className="text-gray-500 mt-1 text-sm">Schedule and track harvest collection dispatches</p>
          </div>
          <button onClick={() => setShowForm(true)}
            className="bg-teal-700 text-white px-4 py-2 rounded-xl text-sm font-semibold hover:bg-teal-600 transition">
            + Schedule Dispatch
          </button>
        </div>

        {showForm && (
          <form onSubmit={handleSchedule}
            className="bg-white rounded-2xl shadow-sm border border-gray-100 p-6 mb-6">
            <h3 className="font-bold text-gray-800 mb-4">Schedule Truck Dispatch</h3>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-4">
              <div>
                <label className="block text-xs font-semibold text-gray-600 mb-1">Contract (Signed Bid)</label>
                {/* FIX: value/options now use bid.agreementId, not bid.id */}
                <select value={selectedAgreementId} onChange={e => setSelectedAgreementId(e.target.value)} required
                  className="w-full px-3 py-2.5 border border-gray-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-teal-500">
                  <option value="">Select contract…</option>
                  {bids.filter(b => b.status === 'CONTRACT_SIGNED' && b.agreementId).map(b => (
                    <option key={b.agreementId!} value={b.agreementId!}>
                      {b.quantityQuintals} qt — {b.totalValueEtb.toLocaleString()} ETB ({b.agreementId!.slice(0, 8)}…)
                    </option>
                  ))}
                </select>
              </div>
              <div>
                <label className="block text-xs font-semibold text-gray-600 mb-1">Driver Fayda ID</label>
                <input type="text" value={driverFaydaId} onChange={e => setDriverFaydaId(e.target.value)} required
                  placeholder="Driver's national ID"
                  className="w-full px-3 py-2.5 border border-gray-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-teal-500" />
              </div>
              <div>
                <label className="block text-xs font-semibold text-gray-600 mb-1">Number of Trucks</label>
                <input type="number" min="1" max="20" value={truckCount} onChange={e => setTruckCount(e.target.value)} required
                  className="w-full px-3 py-2.5 border border-gray-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-teal-500" />
              </div>
              <div>
                <label className="block text-xs font-semibold text-gray-600 mb-1">Scheduled Pickup Date</label>
                <input type="date" value={pickupDate} onChange={e => setPickupDate(e.target.value)} required
                  min={new Date().toISOString().split('T')[0]}
                  className="w-full px-3 py-2.5 border border-gray-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-teal-500" />
              </div>
            </div>
            <div className="bg-amber-50 border border-amber-100 rounded-xl p-3 mb-4 text-xs text-amber-700">
              ⚠️ A 500 ETB driver penalty deposit will be locked in escrow. This is forfeited if the driver defaults.
            </div>
            <div className="flex gap-3">
              <button type="button" onClick={() => setShowForm(false)}
                className="flex-1 bg-gray-100 text-gray-700 py-2.5 rounded-xl text-sm font-semibold hover:bg-gray-200 transition">
                Cancel
              </button>
              <button type="submit" disabled={submitting}
                className="flex-1 bg-teal-700 text-white py-2.5 rounded-xl text-sm font-semibold hover:bg-teal-600 disabled:opacity-50 transition">
                {submitting ? 'Scheduling…' : 'Confirm Dispatch'}
              </button>
            </div>
          </form>
        )}

        {bids.length === 0 ? (
          <div className="bg-white rounded-2xl shadow-sm border border-gray-100 p-12 text-center">
            <p className="text-4xl mb-3">🚚</p>
            <p className="text-gray-500 font-medium">No signed contracts yet</p>
            <p className="text-gray-400 text-sm mt-1">Dispatches are available after a purchase agreement is signed by both parties</p>
          </div>
        ) : (
          <div className="space-y-5">
            {bids.map(bid => {
              const agreementId = bid.agreementId as string;
              const bidDispatches = dispatches[agreementId] || [];
              return (
                <div key={bid.id} className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
                  <div className="bg-gray-50 border-b border-gray-100 px-5 py-3 flex justify-between items-center">
                    <div>
                      <p className="font-bold text-gray-800 text-sm">
                        {bid.quantityQuintals} qt — {bid.totalValueEtb.toLocaleString()} ETB
                      </p>
                      <p className="text-xs text-gray-400 font-mono">Agreement: {agreementId.slice(0, 16)}…</p>
                    </div>
                    <StatusBadge status={bid.status} />
                  </div>
                  <div className="p-5">
                    {bidDispatches.length === 0 ? (
                      <p className="text-gray-400 text-sm text-center py-4">No dispatches scheduled yet</p>
                    ) : (
                      <div className="space-y-4">
                        {bidDispatches.map(d => (
                          <div key={d.id} className="border border-gray-100 rounded-xl p-4">
                            <div className="flex justify-between items-start mb-3">
                              <div>
                                <p className="font-semibold text-gray-800 text-sm">
                                  {dispatchStatusIcon[d.status]} {d.truckCount} truck{d.truckCount > 1 ? 's' : ''}
                                </p>
                                <p className="text-xs text-gray-400 mt-0.5">
                                  Driver Fayda: {d.driverFaydaId}
                                </p>
                              </div>
                              <StatusBadge status={d.status} />
                            </div>
                            <div className="grid grid-cols-2 gap-3 text-xs">
                              <div>
                                <p className="text-gray-400">Scheduled Pickup</p>
                                <p className="font-semibold">{new Date(d.scheduledPickupDate).toLocaleDateString()}</p>
                              </div>
                              {d.actualPickupDate && (
                                <div>
                                  <p className="text-gray-400">Actual Pickup</p>
                                  <p className="font-semibold text-green-600">{new Date(d.actualPickupDate).toLocaleDateString()}</p>
                                </div>
                              )}
                              <div>
                                <p className="text-gray-400">Driver Deposit</p>
                                <p className="font-semibold">{d.driverPenaltyEscrowEtb.toLocaleString()} ETB</p>
                              </div>
                            </div>

                            {d.status === 'LOADED' && confirmDispatch !== agreementId && (
                              <button onClick={() => setConfirmDispatch(agreementId)}
                                className="mt-3 w-full bg-green-700 text-white py-2 rounded-xl text-sm font-semibold hover:bg-green-600 transition">
                                Confirm Receipt at Factory
                              </button>
                            )}
                            {confirmDispatch === agreementId && (
                              <div className="mt-3 bg-green-50 border border-green-100 rounded-xl p-4 space-y-3">
                                <p className="text-sm font-bold text-green-800">Confirm Harvest Delivery</p>
                                <div className="grid grid-cols-2 gap-3">
                                  <div>
                                    <label className="block text-xs font-semibold text-gray-600 mb-1">Actual Quantity (quintals)</label>
                                    <input type="number" step="0.1" min="0.1" value={actualQty}
                                      onChange={e => setActualQty(e.target.value)}
                                      className="w-full px-3 py-2 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-green-500" />
                                  </div>
                                  <div>
                                    <label className="block text-xs font-semibold text-gray-600 mb-1">Quality Grade</label>
                                    <select value={qualityGrade} onChange={e => setQualityGrade(e.target.value)}
                                      className="w-full px-3 py-2 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-green-500">
                                      {['A', 'B', 'C', 'PREMIUM'].map(g => <option key={g}>{g}</option>)}
                                    </select>
                                  </div>
                                </div>
                                <div className="flex gap-2">
                                  <button onClick={() => setConfirmDispatch(null)}
                                    className="flex-1 bg-white border border-gray-200 text-gray-600 py-2 rounded-lg text-sm font-semibold">
                                    Cancel
                                  </button>
                                  <button onClick={() => handleConfirmDelivery(agreementId)}
                                    disabled={confirming === agreementId}
                                    className="flex-1 bg-green-700 text-white py-2 rounded-lg text-sm font-semibold disabled:opacity-50">
                                    {confirming === agreementId ? 'Confirming…' : 'Confirm & Trigger Settlement'}
                                  </button>
                                </div>
                              </div>
                            )}
                          </div>
                        ))}
                      </div>
                    )}
                  </div>
                </div>
              );
            })}
          </div>
        )}
      </div>
    </div>
  );
}

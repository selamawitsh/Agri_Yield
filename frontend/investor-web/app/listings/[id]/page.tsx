'use client';

import { useEffect, useState } from 'react';
import { useRouter, useParams } from 'next/navigation';
import toast from 'react-hot-toast';
import api from '@/lib/api';
import Navbar from '@/components/Navbar';
import StatusBadge from '@/components/StatusBadge';
import FundingProgress from '@/components/FundingProgress';
import { FarmListing } from '@/lib/types';

export default function ListingDetailPage() {
  const router = useRouter();
  const params = useParams();
  const listingId = params.id as string;

  const [listing, setListing] = useState<FarmListing | null>(null);
  const [loading, setLoading] = useState(true);
  const [investing, setInvesting] = useState(false);
  const [showInvestModal, setShowInvestModal] = useState(false);
  const [investAmount, setInvestAmount] = useState('');
  const [investNotes, setInvestNotes] = useState('');

  useEffect(() => {
    const token = localStorage.getItem('access_token');
    if (!token) { router.push('/login'); return; }
    fetchListing();
  }, [listingId]);

  const fetchListing = async () => {
    try {
      const response = await api.get(`/listings/${listingId}`);
      if (response.data.success) setListing(response.data.data);
    } catch (error) {
      toast.error('Listing not found');
      router.push('/listings');
    } finally {
      setLoading(false);
    }
  };

  const handleInvest = async (e: React.FormEvent) => {
    e.preventDefault();
    const amount = parseFloat(investAmount);
    if (isNaN(amount) || amount < 500) {
      toast.error('Minimum investment is 500 ETB');
      return;
    }
    if (!listing) return;
    const remaining = listing.totalAmountEtb - listing.fundedAmountEtb;
    if (amount > remaining) {
      toast.error(`Maximum you can invest is ${remaining.toLocaleString()} ETB`);
      return;
    }

    setInvesting(true);
    try {
      const response = await api.post(`/listings/${listingId}/invest`, {
        amountEtb: amount,
        notes: investNotes,
      });
      if (response.data.success) {
        toast.success('Investment placed successfully! Funds are in escrow.');
        setShowInvestModal(false);
        fetchListing();
      }
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Investment failed');
    } finally {
      setInvesting(false);
    }
  };

  if (loading) return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-emerald-900" />
      </div>
  );

  if (!listing) return null;

  const remaining = listing.totalAmountEtb - listing.fundedAmountEtb;
  const canInvest = listing.status === 'OPEN' || listing.status === 'PARTIALLY_FUNDED';

  return (
      <div className="min-h-screen bg-gray-50 pb-12">
        <Navbar />
        <div className="container mx-auto px-4 sm:px-6 py-6 max-w-4xl">

          {/* Back */}
          <button onClick={() => router.back()} className="flex items-center gap-2 text-emerald-950 font-semibold mb-6 hover:opacity-70 transition bg-white px-4 py-2 rounded-full shadow-sm w-fit">
            <span className="text-xl">←</span> Back to Map/Fields
          </button>

          {/* Hero Card */}
          <div className="bg-white rounded-[2.5rem] shadow-sm overflow-hidden mb-6 border border-gray-100">
            {/* Top Hero Image Area */}
            <div className="relative h-48 bg-emerald-950">
              <div className="absolute inset-0 bg-[url('https://images.unsplash.com/photo-1592982537447-6f296fb00fd8?auto=format&fit=crop&q=80')] bg-cover bg-center opacity-50 mix-blend-overlay"></div>
              <div className="absolute bottom-6 left-6 right-6 flex justify-between items-end">
                <div className="bg-white/90 backdrop-blur-sm p-4 rounded-3xl">
                  <h1 className="text-2xl font-bold text-emerald-950">{listing.cropType} Field Premium Plot</h1>
                  <p className="text-gray-600 text-sm font-medium mt-1">📍 {listing.region} • {listing.kebeleCode}</p>
                </div>
                <div className="bg-lime-200 text-emerald-950 p-4 rounded-3xl text-center shadow-lg">
                  <p className="text-2xl font-black">{listing.currentApr}%</p>
                  <p className="text-xs font-bold uppercase tracking-wide">APR Return</p>
                </div>
              </div>
            </div>

            <div className="p-8">
              <div className="flex justify-between items-center bg-gray-50 p-4 rounded-full mb-8 border border-gray-100">
                <StatusBadge status={listing.status} />
                {listing.fundingDeadline && (
                    <span className="text-sm font-semibold text-emerald-900 flex items-center gap-2">
                  <span>⏱</span> {new Date(listing.fundingDeadline).toLocaleDateString()} Deadline
                </span>
                )}
              </div>

              <div className="bg-emerald-50 rounded-3xl p-6 mb-8 border border-emerald-100">
                <FundingProgress
                    funded={listing.fundedAmountEtb}
                    total={listing.totalAmountEtb}
                    pct={listing.fundingPct}
                />
              </div>

              {/* Stats Grid */}
              <h3 className="font-bold text-emerald-950 text-lg mb-4 ml-2">Field Analytics</h3>
              <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-8">
                {[
                  { label: 'Total Needed', value: `${listing.totalAmountEtb.toLocaleString()} ETB` },
                  { label: 'Funded', value: `${listing.fundedAmountEtb.toLocaleString()} ETB` },
                  { label: 'Remaining', value: `${remaining.toLocaleString()} ETB` },
                  { label: 'Agri-Score', value: `${listing.agriScore} / 900` },
                  { label: 'Base APR', value: `${listing.baseApr}%` },
                  { label: 'Current APR', value: `${listing.currentApr}%` },
                  { label: 'Crop', value: listing.cropType },
                  { label: 'Season', value: listing.seasonName },
                ].map((item) => (
                    <div key={item.label} className="bg-white border border-gray-100 rounded-3xl p-4 shadow-sm">
                      <p className="text-gray-400 text-xs font-medium uppercase tracking-wider">{item.label}</p>
                      <p className="font-bold text-emerald-950 text-sm mt-1">{item.value}</p>
                    </div>
                ))}
              </div>

              {/* APR Breakdown */}
              <div className="p-5 bg-lime-50 border border-lime-100 rounded-3xl mb-8 flex items-start gap-4">
                <div className="text-2xl mt-1">✨</div>
                <div>
                  <h3 className="font-bold text-emerald-950 mb-1">APR Breakdown</h3>
                  <p className="text-emerald-900 text-sm font-medium">
                    {listing.baseApr}% base + NDVI bonus + weather bonus = {listing.currentApr}% current APR
                  </p>
                  <p className="text-emerald-700/70 text-xs mt-2 font-medium">
                    Returns are dynamically updated based on live satellite NDVI readings and weather data.
                  </p>
                </div>
              </div>

              {/* Invest Button */}
              {canInvest ? (
                  <button
                      onClick={() => setShowInvestModal(true)}
                      className="w-full bg-emerald-950 text-white py-4 rounded-full text-lg font-bold shadow-lg hover:bg-emerald-900 hover:-translate-y-0.5 transition duration-200">
                    Proceed to Invest
                  </button>
              ) : (
                  <div className="w-full bg-gray-100 text-gray-500 py-4 rounded-full text-center font-bold">
                    {listing.status === 'FULLY_FUNDED' ? '🔒 Fully Funded' : `🔒 ${listing.status}`}
                  </div>
              )}
            </div>
          </div>

          {/* Invest Modal */}
          {showInvestModal && (
              <div className="fixed inset-0 bg-emerald-950/40 backdrop-blur-sm flex items-end md:items-center justify-center z-50 p-4 pb-0 md:pb-4">
                <div className="bg-white rounded-t-[2.5rem] md:rounded-[2.5rem] p-8 w-full max-w-md shadow-2xl transform transition-all translate-y-0">
                  <div className="w-12 h-1.5 bg-gray-200 rounded-full mx-auto mb-6 md:hidden"></div>

                  <h2 className="text-2xl font-bold text-emerald-950 mb-1">Fund this field</h2>
                  <p className="text-gray-500 text-sm font-medium mb-8">
                    {listing.region} • <span className="text-lime-700 font-bold">{listing.currentApr}% APR</span> • Max {remaining.toLocaleString()} ETB
                  </p>

                  <form onSubmit={handleInvest}>
                    <div className="mb-6">
                      <label className="block text-emerald-950 text-sm font-bold mb-2 ml-2">
                        Investment Amount (ETB)
                      </label>
                      <input
                          type="number"
                          min="500"
                          max={remaining}
                          step="100"
                          required
                          value={investAmount}
                          onChange={(e) => setInvestAmount(e.target.value)}
                          className="w-full px-5 py-4 bg-gray-50 border border-gray-200 rounded-full focus:outline-none focus:ring-2 focus:ring-emerald-500 focus:bg-white transition text-lg font-bold text-emerald-950 placeholder-gray-400"
                          placeholder="Min. 500"
                      />
                      {investAmount && parseFloat(investAmount) >= 500 && (
                          <p className="text-lime-700 text-xs font-bold mt-3 ml-2 flex items-center gap-1">
                            <span>✦</span> Expected return: {(parseFloat(investAmount) * listing.currentApr / 100).toFixed(2)} ETB/yr
                          </p>
                      )}
                    </div>

                    <div className="mb-8">
                      <label className="block text-emerald-950 text-sm font-bold mb-2 ml-2">
                        Notes (optional)
                      </label>
                      <textarea
                          value={investNotes}
                          onChange={(e) => setInvestNotes(e.target.value)}
                          className="w-full px-5 py-4 bg-gray-50 border border-gray-200 rounded-3xl focus:outline-none focus:ring-2 focus:ring-emerald-500 focus:bg-white transition text-sm font-medium text-emerald-950 resize-none"
                          rows={2}
                          placeholder="Add a memo..."
                      />
                    </div>

                    <div className="flex gap-3 mt-4">
                      <button type="button" onClick={() => setShowInvestModal(false)}
                              className="flex-1 bg-gray-100 text-emerald-950 py-4 rounded-full font-bold hover:bg-gray-200 transition">
                        Cancel
                      </button>
                      <button type="submit" disabled={investing}
                              className="flex-1 bg-emerald-950 text-white py-4 rounded-full font-bold hover:bg-emerald-900 transition shadow-lg disabled:opacity-50">
                        {investing ? 'Processing...' : 'Confirm'}
                      </button>
                    </div>
                  </form>
                </div>
              </div>
          )}
        </div>
      </div>
  );
}
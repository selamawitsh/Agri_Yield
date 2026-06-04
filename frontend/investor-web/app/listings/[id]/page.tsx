'use client';

import { useEffect, useState } from 'react';
import { useRouter, useParams } from 'next/navigation';
import dynamic from 'next/dynamic';
import toast from 'react-hot-toast';
import api from '@/lib/api';
import Navbar from '@/components/Navbar';
import StatusBadge from '@/components/StatusBadge';
import FundingProgress from '@/components/FundingProgress';
import NdviChart from '@/components/NdviChart';
import YieldPredictionBar from '@/components/YieldPredictionBar';
import { FarmListing, NdviReading, YieldPrediction } from '@/lib/types';

const FarmMap = dynamic(() => import('@/components/FarmMap'), { ssr: false });

export default function ListingDetailPage() {
  const router = useRouter();
  const params = useParams();
  const listingId = params.id as string;

  const [listing, setListing] = useState<FarmListing | null>(null);
  const [ndviHistory, setNdviHistory] = useState<NdviReading[]>([]);
  const [yieldPrediction, setYieldPrediction] = useState<YieldPrediction | null>(null);
  const [loading, setLoading] = useState(true);
  const [investing, setInvesting] = useState(false);
  const [showInvestModal, setShowInvestModal] = useState(false);
  const [investAmount, setInvestAmount] = useState('');
  const [investNotes, setInvestNotes] = useState('');

  useEffect(() => {
    const token = localStorage.getItem('access_token');
    if (!token) { router.push('/login'); return; }
    fetchAll();
  }, [listingId]);

  const fetchAll = async () => {
    try {
      const [listingRes, ndviRes] = await Promise.allSettled([
        api.get(`/listings/${listingId}`),
        api.get(`/listings/${listingId}/ndvi-history`),
      ]);
      if (listingRes.status === 'fulfilled' && listingRes.value.data.success)
        setListing(listingRes.value.data.data);
      else { toast.error('Listing not found'); router.push('/listings'); return; }

      if (ndviRes.status === 'fulfilled' && ndviRes.value.data.success)
        setNdviHistory(ndviRes.value.data.data || []);
    } catch {
      toast.error('Failed to load listing');
      router.push('/listings');
    } finally {
      setLoading(false);
    }
  };

  const handleInvest = async (e: React.FormEvent) => {
    e.preventDefault();
    const amount = parseFloat(investAmount);
    if (isNaN(amount) || amount < 500) { toast.error('Minimum investment is 500 ETB'); return; }
    if (!listing) return;
    const remaining = listing.totalAmountEtb - listing.fundedAmountEtb;
    if (amount > remaining) { toast.error(`Max you can invest is ${remaining.toLocaleString()} ETB`); return; }

    setInvesting(true);
    try {
      const res = await api.post(`/listings/${listingId}/invest`, { amountEtb: amount, notes: investNotes });
      if (res.data.success) {
        toast.success('Investment placed! Funds are in escrow.');
        setShowInvestModal(false);
        fetchAll();
      }
    } catch (err: any) {
      toast.error(err.response?.data?.message || 'Investment failed');
    } finally { setInvesting(false); }
  };

  if (loading) return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50">
      <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-green-600" />
    </div>
  );
  if (!listing) return null;

  const remaining = listing.totalAmountEtb - listing.fundedAmountEtb;
  const canInvest = listing.status === 'OPEN' || listing.status === 'PARTIALLY_FUNDED';
  const latestNdvi = ndviHistory.length > 0 ? ndviHistory[ndviHistory.length - 1].ndviValue : null;
  const ndviHealth = latestNdvi == null ? 'N/A' : latestNdvi >= 0.6 ? 'Excellent' : latestNdvi >= 0.4 ? 'Good' : latestNdvi >= 0.2 ? 'Moderate' : 'Poor';
  const ndviColor = latestNdvi == null ? 'text-gray-400' : latestNdvi >= 0.6 ? 'text-green-600' : latestNdvi >= 0.4 ? 'text-lime-600' : latestNdvi >= 0.2 ? 'text-yellow-600' : 'text-red-600';

  // Approximate GPS from region for map (real data would come from farm-service)
  const regionCoords: Record<string, [number, number]> = {
    'Oromia': [8.5, 39.3], 'Amhara': [11.5, 38.0], 'SNNPR': [6.8, 37.5],
    'Tigray': [14.0, 38.5], 'Somali': [7.5, 44.0], 'Afar': [11.8, 41.5],
  };
  const [mapLat, mapLng] = regionCoords[listing.region] || [9.145, 40.489];

  return (
    <div className="min-h-screen bg-gray-50 pb-12">
      <Navbar />
      <div className="container mx-auto px-4 sm:px-6 py-6 max-w-5xl">

        <button onClick={() => router.back()}
          className="flex items-center gap-2 text-green-700 font-semibold mb-6 hover:opacity-70 transition bg-white px-4 py-2 rounded-full shadow-sm w-fit text-sm">
          ← Back to Listings
        </button>

        {/* Hero */}
        <div className="bg-white rounded-3xl shadow-sm border border-gray-100 overflow-hidden mb-6">
          <div className="relative h-44 bg-emerald-950">
            <div className="absolute inset-0 bg-[url('https://images.unsplash.com/photo-1592982537447-6f296fb00fd8?auto=format&fit=crop&q=80')] bg-cover bg-center opacity-40" />
            <div className="absolute bottom-5 left-6 right-6 flex justify-between items-end gap-4">
              <div className="bg-white/90 backdrop-blur-sm p-4 rounded-2xl">
                <h1 className="text-xl font-bold text-emerald-950">{listing.cropType} — {listing.region}</h1>
                <p className="text-gray-500 text-sm">📍 {listing.kebeleCode} · {listing.seasonName}</p>
              </div>
              <div className="bg-lime-300 text-emerald-950 p-3 rounded-2xl text-center shadow">
                <p className="text-2xl font-black">{listing.currentApr}%</p>
                <p className="text-xs font-bold uppercase">APR</p>
              </div>
            </div>
          </div>

          <div className="p-6">
            <div className="flex flex-wrap gap-2 items-center mb-5">
              <StatusBadge status={listing.status} />
              {listing.fundingDeadline && (
                <span className="text-xs font-semibold text-gray-500 bg-gray-100 px-3 py-1 rounded-full">
                  ⏱ Deadline: {new Date(listing.fundingDeadline).toLocaleDateString()}
                </span>
              )}
              {/* Parametric insurance status (SRS) */}
              <span className="text-xs font-semibold text-blue-600 bg-blue-50 px-3 py-1 rounded-full">
                🛡 Parametric Insurance: Active
              </span>
            </div>

            <div className="bg-emerald-50 rounded-2xl p-4 mb-6 border border-emerald-100">
              <FundingProgress funded={listing.fundedAmountEtb} total={listing.totalAmountEtb} pct={listing.fundingPct} />
            </div>

            {/* Stats grid */}
            <div className="grid grid-cols-2 md:grid-cols-4 gap-3 mb-6">
              {[
                { label: 'Total Needed', value: `${listing.totalAmountEtb?.toLocaleString()} ETB` },
                { label: 'Funded', value: `${listing.fundedAmountEtb?.toLocaleString()} ETB` },
                { label: 'Remaining', value: `${remaining.toLocaleString()} ETB` },
                { label: 'Agri-Score', value: `${listing.agriScore} / 900` },
                { label: 'Base APR', value: `${listing.baseApr}%` },
                { label: 'Current APR', value: `${listing.currentApr}%` },
                { label: 'NDVI Health', value: ndviHealth, valueClass: ndviColor },
                { label: 'Latest NDVI', value: latestNdvi != null ? latestNdvi.toFixed(3) : 'N/A' },
              ].map(item => (
                <div key={item.label} className="bg-gray-50 border border-gray-100 rounded-2xl p-3">
                  <p className="text-gray-400 text-xs uppercase tracking-wide font-medium">{item.label}</p>
                  <p className={`font-bold text-sm mt-1 ${(item as any).valueClass || 'text-gray-800'}`}>{item.value}</p>
                </div>
              ))}
            </div>
          </div>
        </div>

        {/* NDVI Time-Series Chart — SRS requirement */}
        <div className="bg-white rounded-3xl shadow-sm border border-gray-100 p-6 mb-6">
          <h3 className="font-bold text-gray-800 mb-1">NDVI Trend</h3>
          <p className="text-xs text-gray-400 mb-4">Satellite vegetation index over time — higher is healthier</p>
          <NdviChart data={ndviHistory} height={220} />
        </div>

        {/* Yield Prediction — SRS requirement */}
        {yieldPrediction && (
          <div className="mb-6">
            <YieldPredictionBar
              min={yieldPrediction.predictedYieldMin}
              mean={yieldPrediction.predictedYieldMean}
              max={yieldPrediction.predictedYieldMax}
              confidencePct={yieldPrediction.confidencePct}
              cropType={listing.cropType}
            />
          </div>
        )}

        {/* Farm Map — SRS: Leaflet.js */}
        <div className="bg-white rounded-3xl shadow-sm border border-gray-100 p-6 mb-6">
          <h3 className="font-bold text-gray-800 mb-4">Farm Location</h3>
          <FarmMap lat={mapLat} lng={mapLng} label={`${listing.cropType} — ${listing.region}`} height={260} />
          <p className="text-xs text-gray-400 mt-2 text-center">📍 Approximate farm location — {listing.region}, {listing.kebeleCode}</p>
        </div>

        {/* APR Breakdown */}
        <div className="bg-lime-50 border border-lime-100 rounded-3xl p-5 mb-6 flex items-start gap-4">
          <div className="text-2xl mt-1">✨</div>
          <div>
            <h3 className="font-bold text-emerald-950 mb-1">APR Breakdown</h3>
            <p className="text-emerald-900 text-sm font-medium">
              {listing.baseApr}% base + NDVI bonus + weather bonus = {listing.currentApr}% current APR
            </p>
            <p className="text-emerald-700/60 text-xs mt-1">Dynamically updated from satellite NDVI and weather signals</p>
          </div>
        </div>

        {/* Off-taker bid status placeholder (SRS) */}
        <div className="bg-white rounded-3xl shadow-sm border border-gray-100 p-6 mb-6">
          <h3 className="font-bold text-gray-800 mb-3">Off-Taker Bid Status</h3>
          <div className="bg-gray-50 rounded-2xl p-4 text-center text-gray-400 text-sm">
            <p>🏭 Off-taker bids will appear here once the farm is fully funded and growing.</p>
          </div>
        </div>

        {/* Voucher Redemption Timeline placeholder (SRS) */}
        <div className="bg-white rounded-3xl shadow-sm border border-gray-100 p-6 mb-6">
          <h3 className="font-bold text-gray-800 mb-3">Voucher Redemption Timeline</h3>
          <div className="bg-gray-50 rounded-2xl p-4 text-center text-gray-400 text-sm">
            <p>🏷 Voucher redemption events will appear here as farmers redeem input vouchers.</p>
          </div>
        </div>

        {/* Invest CTA */}
        {canInvest ? (
          <button onClick={() => setShowInvestModal(true)}
            className="w-full bg-emerald-950 text-white py-4 rounded-full text-lg font-bold shadow-lg hover:bg-emerald-900 hover:-translate-y-0.5 transition">
            Invest Now — {listing.currentApr}% APR
          </button>
        ) : (
          <div className="w-full bg-gray-100 text-gray-500 py-4 rounded-full text-center font-bold">
            {listing.status === 'FULLY_FUNDED' ? '🔒 Fully Funded' : `🔒 ${listing.status}`}
          </div>
        )}
      </div>

      {/* Invest Modal */}
      {showInvestModal && (
        <div className="fixed inset-0 bg-black/40 backdrop-blur-sm flex items-end md:items-center justify-center z-50 p-4">
          <div className="bg-white rounded-t-3xl md:rounded-3xl p-8 w-full max-w-md shadow-2xl">
            <div className="w-10 h-1 bg-gray-200 rounded-full mx-auto mb-6 md:hidden" />
            <h2 className="text-2xl font-bold text-emerald-950 mb-1">Invest in this Farm</h2>
            <p className="text-gray-400 text-sm mb-6">
              {listing.region} · <span className="text-green-700 font-bold">{listing.currentApr}% APR</span> · Max {remaining.toLocaleString()} ETB
            </p>
            <form onSubmit={handleInvest}>
              <div className="mb-4">
                <label className="block text-sm font-bold text-gray-700 mb-2">Amount (ETB) — min 500</label>
                <input type="number" min="500" max={remaining} step="100" required
                  value={investAmount} onChange={e => setInvestAmount(e.target.value)}
                  className="w-full px-5 py-4 bg-gray-50 border border-gray-200 rounded-full text-lg font-bold focus:outline-none focus:ring-2 focus:ring-green-500"
                  placeholder="500" />
                {investAmount && parseFloat(investAmount) >= 500 && (
                  <p className="text-green-700 text-xs font-bold mt-2 ml-2">
                    ✦ Expected return: {(parseFloat(investAmount) * listing.currentApr / 100).toFixed(2)} ETB/yr
                  </p>
                )}
              </div>
              <div className="mb-6">
                <label className="block text-sm font-bold text-gray-700 mb-2">Notes (optional)</label>
                <textarea value={investNotes} onChange={e => setInvestNotes(e.target.value)}
                  className="w-full px-5 py-3 bg-gray-50 border border-gray-200 rounded-2xl text-sm resize-none focus:outline-none focus:ring-2 focus:ring-green-500"
                  rows={2} placeholder="Add a memo..." />
              </div>
              <div className="flex gap-3">
                <button type="button" onClick={() => setShowInvestModal(false)}
                  className="flex-1 bg-gray-100 text-gray-700 py-4 rounded-full font-bold hover:bg-gray-200 transition">Cancel</button>
                <button type="submit" disabled={investing}
                  className="flex-1 bg-emerald-950 text-white py-4 rounded-full font-bold hover:bg-emerald-900 transition disabled:opacity-50">
                  {investing ? 'Processing...' : 'Confirm'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import toast from 'react-hot-toast';
import Navbar from '@/components/Navbar';
import NdviBadge from '@/components/NdviBadge';
import { getFarmDetail, placeBid } from '@/lib/api';
import type { FarmMarketplace } from '@/lib/types';

// SRS §6.4: Off-Taker finds farms by farmId — no list endpoint exists on
// offtaker-service. In production this would call a search/analytics service.
// For now we provide a farmId lookup form matching the SRS table UI spec.

const EXAMPLE_FARMS = [
  'Enter a Farm ID to look up',
];

const CROP_ICONS: Record<string, string> = {
  WHEAT: '🌾', TEFF: '🌿', BARLEY: '🟤', MAIZE: '🌽',
  SORGHUM: '🌱', COFFEE: '☕', BEANS: '🫘', MILLET: '🌾',
};

export default function FarmsPage() {
  const router = useRouter();
  const [farmIdInput, setFarmIdInput] = useState('');
  const [farm,        setFarm]        = useState<FarmMarketplace | null>(null);
  const [loading,     setLoading]     = useState(false);
  const [showBidForm, setShowBidForm] = useState(false);
  const [bidQty,      setBidQty]      = useState('');
  const [bidPrice,    setBidPrice]    = useState('');
  const [bidDays,     setBidDays]     = useState('7');
  const [placing,     setPlacing]     = useState(false);

  useEffect(() => {
    if (!localStorage.getItem('access_token')) { router.push('/login'); }
  }, []);

  const handleSearch = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!farmIdInput.trim()) return;
    setLoading(true);
    setFarm(null);
    setShowBidForm(false);
    try {
      const res = await getFarmDetail(farmIdInput.trim());
      if (res.data.success) setFarm(res.data.data);
    } catch (err: any) {
      toast.error(err.response?.data?.message || 'Farm not found');
    } finally { setLoading(false); }
  };

  const handlePlaceBid = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!farm) return;
    const qty   = parseFloat(bidQty);
    const price = parseFloat(bidPrice);
    const days  = parseInt(bidDays);
    if (isNaN(qty) || qty <= 0)   { toast.error('Enter valid quantity'); return; }
    if (isNaN(price) || price <= 0) { toast.error('Enter valid price'); return; }
    setPlacing(true);
    try {
      const res = await placeBid({ farmId: farm.farmId, quantityQuintals: qty, pricePerQuintalEtb: price, expiresInDays: days });
      if (res.data.success) {
        toast.success('Bid placed! 10% deposit locked in escrow.');
        setShowBidForm(false);
        router.push('/bids');
      }
    } catch (err: any) {
      toast.error(err.response?.data?.message || 'Failed to place bid');
    } finally { setPlacing(false); }
  };

  const ndviColor = (v: number) =>
    v >= 0.6 ? 'text-green-600' : v >= 0.4 ? 'text-lime-600' : v >= 0.2 ? 'text-yellow-600' : 'text-red-600';

  return (
    <div className="min-h-screen bg-gray-50 pb-20 md:pb-0">
      <Navbar />
      <div className="container mx-auto px-4 sm:px-6 py-6 max-w-4xl">

        <div className="mb-6">
          <h1 className="text-2xl font-bold text-gray-900">Farm Discovery</h1>
          <p className="text-gray-500 mt-1 text-sm">Look up farms by ID to view NDVI, yield prediction, and place purchase bids</p>
        </div>

        {/* Search */}
        <form onSubmit={handleSearch} className="bg-white rounded-2xl shadow-sm border border-gray-100 p-6 mb-6">
          <label className="block text-sm font-semibold text-gray-700 mb-2">Farm ID</label>
          <div className="flex gap-3">
            <input
              type="text"
              value={farmIdInput}
              onChange={e => setFarmIdInput(e.target.value)}
              placeholder="e.g. 550e8400-e29b-41d4-a716-446655440000"
              className="flex-1 px-4 py-3 border border-gray-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-teal-500 font-mono"
            />
            <button type="submit" disabled={loading}
              className="bg-teal-700 text-white px-6 py-3 rounded-xl text-sm font-semibold hover:bg-teal-600 disabled:opacity-50 transition">
              {loading ? '…' : 'Look up'}
            </button>
          </div>
          <p className="text-xs text-gray-400 mt-2">
            Get farm IDs from the investment platform listings or your procurement manager.
          </p>
        </form>

        {/* Farm detail card */}
        {farm && (
          <div className="space-y-5">

            {/* Hero */}
            <div className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
              <div className="bg-gradient-to-r from-teal-800 to-emerald-800 p-6 text-white">
                <div className="flex justify-between items-start">
                  <div>
                    <h2 className="text-2xl font-bold">
                      {CROP_ICONS[farm.cropType] || '🌾'} {farm.cropType}
                    </h2>
                    <p className="text-teal-100 text-sm mt-1">📍 {farm.region} · {farm.kebeleCode}</p>
                  </div>
                  <div className="bg-white/20 rounded-xl p-3 text-center">
                    <p className="text-2xl font-black">{farm.agriScore}</p>
                    <p className="text-xs font-bold text-teal-100">/ 900</p>
                    <p className="text-xs text-teal-200">Agri-Score</p>
                  </div>
                </div>
              </div>

              <div className="p-6">
                <div className="flex flex-wrap gap-2 mb-5">
                  <NdviBadge ndvi={farm.currentNdvi} healthStatus={farm.ndviHealthStatus} />
                  {farm.harvestReady && (
                    <span className="bg-green-100 text-green-700 text-xs font-bold px-3 py-1 rounded-full">
                      🟢 Harvest Ready
                    </span>
                  )}
                  <span className="bg-gray-100 text-gray-600 text-xs font-medium px-3 py-1 rounded-full">
                    {farm.cropCycleStatus}
                  </span>
                </div>

                {/* Stats grid — SRS §6.4 */}
                <div className="grid grid-cols-2 md:grid-cols-4 gap-3 mb-6">
                  {[
                    { label: 'Farm Area',          value: `${farm.areaHectares.toFixed(2)} ha` },
                    { label: 'NDVI',               value: farm.currentNdvi.toFixed(3),         valueClass: ndviColor(farm.currentNdvi) },
                    { label: 'Predicted Yield',    value: `${farm.predictedYieldMeanQuintals.toFixed(1)} qt` },
                    { label: 'Yield Confidence',   value: `${farm.yieldConfidencePct}%` },
                    { label: 'Harvest Window',
                      value: farm.estimatedHarvestFrom
                        ? `${new Date(farm.estimatedHarvestFrom).toLocaleDateString()} – ${new Date(farm.estimatedHarvestTo!).toLocaleDateString()}`
                        : 'Not yet predicted' },
                    { label: 'GPS',                value: `${farm.gpsCentroidLat.toFixed(4)}, ${farm.gpsCentroidLng.toFixed(4)}` },
                    { label: 'Farmer ID',          value: farm.farmerId.slice(0, 12) + '…' },
                    { label: 'Crop Cycle',         value: farm.cropCycleId.slice(0, 12) + '…' },
                  ].map(item => (
                    <div key={item.label} className="bg-gray-50 border border-gray-100 rounded-xl p-3">
                      <p className="text-gray-400 text-xs uppercase tracking-wide font-medium">{item.label}</p>
                      <p className={`font-bold text-sm mt-1 ${(item as any).valueClass || 'text-gray-800'}`}>{item.value}</p>
                    </div>
                  ))}
                </div>

                {/* Place Bid CTA */}
                {!showBidForm ? (
                  <button onClick={() => setShowBidForm(true)}
                    className="w-full bg-teal-700 text-white py-3 rounded-xl font-bold hover:bg-teal-600 transition">
                    Place Purchase Bid
                  </button>
                ) : (
                  <form onSubmit={handlePlaceBid} className="bg-teal-50 border border-teal-100 rounded-2xl p-5 space-y-4">
                    <h3 className="font-bold text-teal-900 text-base">Place Bid</h3>
                    <div className="grid grid-cols-1 md:grid-cols-3 gap-3">
                      <div>
                        <label className="block text-xs font-semibold text-gray-600 mb-1">Quantity (quintals)</label>
                        <input type="number" step="0.1" min="1" required value={bidQty}
                          onChange={e => setBidQty(e.target.value)}
                          placeholder={`Max ~${farm.predictedYieldMeanQuintals.toFixed(0)} qt`}
                          className="w-full px-3 py-2.5 border border-gray-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-teal-500" />
                      </div>
                      <div>
                        <label className="block text-xs font-semibold text-gray-600 mb-1">Price / quintal (ETB)</label>
                        <input type="number" step="1" min="1" required value={bidPrice}
                          onChange={e => setBidPrice(e.target.value)}
                          placeholder="e.g. 2500"
                          className="w-full px-3 py-2.5 border border-gray-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-teal-500" />
                      </div>
                      <div>
                        <label className="block text-xs font-semibold text-gray-600 mb-1">Bid valid (days)</label>
                        <select value={bidDays} onChange={e => setBidDays(e.target.value)}
                          className="w-full px-3 py-2.5 border border-gray-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-teal-500">
                          {[3,5,7,14,30].map(d => <option key={d} value={d}>{d} days</option>)}
                        </select>
                      </div>
                    </div>
                    {bidQty && bidPrice && (
                      <div className="bg-white rounded-xl p-3 text-sm border border-teal-100">
                        <p className="text-gray-600">Total value: <strong className="text-teal-700">{(parseFloat(bidQty) * parseFloat(bidPrice)).toLocaleString()} ETB</strong></p>
                        <p className="text-gray-400 text-xs mt-0.5">10% deposit ({((parseFloat(bidQty) * parseFloat(bidPrice)) * 0.1).toLocaleString()} ETB) locked in escrow until bid expires or is rejected</p>
                      </div>
                    )}
                    <div className="flex gap-3">
                      <button type="button" onClick={() => setShowBidForm(false)}
                        className="flex-1 bg-white border border-gray-200 text-gray-600 py-2.5 rounded-xl text-sm font-semibold hover:bg-gray-50 transition">
                        Cancel
                      </button>
                      <button type="submit" disabled={placing}
                        className="flex-1 bg-teal-700 text-white py-2.5 rounded-xl text-sm font-semibold hover:bg-teal-600 disabled:opacity-50 transition">
                        {placing ? 'Placing…' : 'Confirm Bid'}
                      </button>
                    </div>
                  </form>
                )}
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

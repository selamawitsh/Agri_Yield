'use client';

import { useState, useEffect, useCallback } from 'react';
import { useRouter, useSearchParams } from 'next/navigation';
import Link from 'next/link';
import toast from 'react-hot-toast';
import Navbar from '@/components/Navbar';
import NdviBadge from '@/components/NdviBadge';
import StatusBadge from '@/components/StatusBadge';
import { browseFarms, placeBid } from '@/lib/api';
import type { FarmMarketplace } from '@/lib/types';

const CROP_TYPES = ['WHEAT', 'TEFF', 'BARLEY', 'MAIZE', 'SORGHUM', 'COFFEE', 'BEANS', 'MILLET'];

const NDVI_COLOR = (v: number) =>
  v >= 0.6 ? 'text-green-700' : v >= 0.4 ? 'text-lime-700' : v >= 0.2 ? 'text-yellow-700' : 'text-red-600';

export default function FarmsPage() {
  const router       = useRouter();
  const searchParams = useSearchParams();

  const [farms,       setFarms]       = useState<FarmMarketplace[]>([]);
  const [loading,     setLoading]     = useState(true);
  const [cropType,    setCropType]    = useState('');
  const [region,      setRegion]      = useState('');
  const [harvestOnly, setHarvestOnly] = useState(false);
  const [selected,    setSelected]    = useState<FarmMarketplace | null>(null);
  const [showBid,     setShowBid]     = useState(false);
  const [bidQty,      setBidQty]      = useState('');
  const [bidPrice,    setBidPrice]    = useState('');
  const [bidDays,     setBidDays]     = useState('7');
  const [placing,     setPlacing]     = useState(false);

  useEffect(() => {
    if (!localStorage.getItem('access_token')) { router.push('/login'); return; }
    load();
  }, []);

  // If redirected from bids page with a lookup param, pre-select that farm
  useEffect(() => {
    const lookup = searchParams.get('lookup');
    if (lookup && farms.length > 0) {
      const match = farms.find(f => f.farmId === lookup);
      if (match) setSelected(match);
    }
  }, [searchParams, farms]);

  const load = useCallback(async () => {
    setLoading(true);
    try {
      const params: Record<string, string | boolean> = {};
      if (cropType)    params.cropType    = cropType;
      if (region)      params.region      = region;
      if (harvestOnly) params.harvestReady = true;
      const res = await browseFarms(params);
      if (res.data.success) setFarms(res.data.data || []);
    } catch (err: any) {
      toast.error(err.response?.data?.message || 'Failed to load farms');
    } finally {
      setLoading(false);
    }
  }, [cropType, region, harvestOnly]);

  const handleFilter = (e: React.FormEvent) => {
    e.preventDefault();
    load();
  };

  const handlePlaceBid = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selected) return;
    const qty   = parseFloat(bidQty);
    const price = parseFloat(bidPrice);
    const days  = parseInt(bidDays);
    if (isNaN(qty)   || qty <= 0)   { toast.error('Enter a valid quantity');  return; }
    if (isNaN(price) || price <= 0) { toast.error('Enter a valid price');     return; }
    setPlacing(true);
    try {
      const res = await placeBid({
        farmId:             selected.farmId,
        quantityQuintals:   qty,
        pricePerQuintalEtb: price,
        expiresInDays:      days,
      });
      if (res.data.success) {
        toast.success('Bid placed. 10% deposit locked in escrow.');
        setShowBid(false);
        setBidQty('');
        setBidPrice('');
        router.push('/bids');
      }
    } catch (err: any) {
      toast.error(err.response?.data?.message || 'Failed to place bid');
    } finally {
      setPlacing(false);
    }
  };

  const totalValue = bidQty && bidPrice
    ? parseFloat(bidQty) * parseFloat(bidPrice) : 0;

  return (
    <div className="min-h-screen bg-gray-50 pb-20 md:pb-0">
      <Navbar />

      <div className="container mx-auto px-4 sm:px-6 py-6 max-w-7xl">

        <div className="flex justify-between items-start mb-6">
          <div>
            <h1 className="text-2xl font-bold text-gray-900">Farm Marketplace</h1>
            <p className="text-gray-500 text-sm mt-1">
              Browse available farms. Farms appear here when harvest is predicted by satellite monitoring.
            </p>
          </div>
          <button onClick={load}
            className="text-sm text-teal-700 font-semibold border border-teal-200 px-4 py-2 rounded-xl hover:bg-teal-50 transition">
            Refresh
          </button>
        </div>

        {/* Filter bar */}
        <form onSubmit={handleFilter}
          className="bg-white rounded-2xl shadow-sm border border-gray-100 p-4 mb-6 flex flex-wrap gap-3 items-end">
          <div className="flex-1 min-w-[140px]">
            <label className="block text-xs font-semibold text-gray-600 mb-1">Crop Type</label>
            <select value={cropType} onChange={e => setCropType(e.target.value)}
              className="w-full px-3 py-2.5 border border-gray-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-teal-500">
              <option value="">All crops</option>
              {CROP_TYPES.map(c => <option key={c} value={c}>{c}</option>)}
            </select>
          </div>
          <div className="flex-1 min-w-[140px]">
            <label className="block text-xs font-semibold text-gray-600 mb-1">Region</label>
            <input type="text" value={region} onChange={e => setRegion(e.target.value)}
              placeholder="e.g. Oromia"
              className="w-full px-3 py-2.5 border border-gray-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-teal-500" />
          </div>
          <div className="flex items-center gap-2 pb-0.5">
            <input type="checkbox" id="harvestOnly" checked={harvestOnly}
              onChange={e => setHarvestOnly(e.target.checked)}
              className="w-4 h-4 accent-teal-600" />
            <label htmlFor="harvestOnly" className="text-sm font-semibold text-gray-600 cursor-pointer">
              Harvest ready only
            </label>
          </div>
          <button type="submit"
            className="bg-teal-700 text-white px-6 py-2.5 rounded-xl text-sm font-semibold hover:bg-teal-600 transition">
            Apply Filters
          </button>
        </form>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">

          {/* Farm list */}
          <div className="lg:col-span-1 space-y-3">
            {loading ? (
              <div className="flex items-center justify-center py-20">
                <div className="animate-spin rounded-full h-10 w-10 border-b-2 border-teal-600" />
              </div>
            ) : farms.length === 0 ? (
              <div className="bg-white rounded-2xl shadow-sm border border-gray-100 p-10 text-center">
                <p className="text-gray-400 font-medium text-sm">No farms available yet.</p>
                <p className="text-gray-300 text-xs mt-1">
                  Farms appear once geospatial monitoring predicts harvest readiness.
                </p>
              </div>
            ) : farms.map(farm => (
              <button key={farm.farmId}
                onClick={() => { setSelected(farm); setShowBid(false); }}
                className={`w-full text-left bg-white rounded-2xl shadow-sm border transition p-4
                  ${selected?.farmId === farm.farmId
                    ? 'border-teal-500 ring-2 ring-teal-200'
                    : 'border-gray-100 hover:border-teal-200'}`}>
                <div className="flex justify-between items-start mb-2">
                  <div>
                    <p className="font-bold text-gray-800 text-sm">{farm.cropType}</p>
                    <p className="text-xs text-gray-400 mt-0.5">{farm.region} · {farm.kebeleCode}</p>
                  </div>
                  <div className="text-right">
                    <p className="text-xs font-bold text-teal-700">{farm.agriScore} / 900</p>
                    <p className="text-xs text-gray-400">Agri-Score</p>
                  </div>
                </div>
                <div className="flex flex-wrap gap-1.5 mt-2">
                  <NdviBadge ndvi={farm.currentNdvi} healthStatus={farm.ndviHealthStatus} />
                  {farm.harvestReady && (
                    <span className="bg-green-100 text-green-700 text-xs font-bold px-2 py-0.5 rounded-full">
                      Harvest Ready
                    </span>
                  )}
                  {farm.existingBidsCount > 0 && (
                    <span className="bg-orange-100 text-orange-700 text-xs font-medium px-2 py-0.5 rounded-full">
                      {farm.existingBidsCount} bid{farm.existingBidsCount > 1 ? 's' : ''}
                    </span>
                  )}
                </div>
                <p className="text-xs text-gray-500 mt-2">
                  Predicted yield: <strong>{farm.predictedYieldMeanQuintals.toFixed(1)} qt</strong>
                  &nbsp;({farm.yieldConfidencePct}% confidence)
                </p>
              </button>
            ))}
          </div>

          {/* Detail panel */}
          <div className="lg:col-span-2">
            {!selected ? (
              <div className="bg-white rounded-2xl shadow-sm border border-gray-100 p-16 text-center h-full flex flex-col items-center justify-center">
                <p className="text-gray-300 text-5xl mb-4">&#9651;</p>
                <p className="text-gray-400 font-medium">Select a farm to view details and place a bid</p>
              </div>
            ) : (
              <div className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">

                {/* Header */}
                <div className="bg-teal-800 p-6 text-white">
                  <div className="flex justify-between items-start">
                    <div>
                      <h2 className="text-xl font-bold">{selected.cropType}</h2>
                      <p className="text-teal-200 text-sm mt-0.5">
                        {selected.region} &middot; {selected.kebeleCode}
                      </p>
                      <p className="text-teal-300 text-xs mt-1 font-mono">
                        {selected.farmId}
                      </p>
                    </div>
                    <div className="bg-white/20 rounded-xl p-3 text-center min-w-[64px]">
                      <p className="text-2xl font-black">{selected.agriScore}</p>
                      <p className="text-xs text-teal-200">/ 900</p>
                      <p className="text-xs text-teal-300">Agri-Score</p>
                    </div>
                  </div>
                  <div className="flex flex-wrap gap-2 mt-3">
                    <NdviBadge ndvi={selected.currentNdvi} healthStatus={selected.ndviHealthStatus} />
                    {selected.harvestReady && (
                      <span className="bg-green-400/30 text-green-100 text-xs font-bold px-3 py-1 rounded-full border border-green-400/40">
                        Harvest Ready
                      </span>
                    )}
                    <StatusBadge status={selected.cropCycleStatus} />
                  </div>
                </div>

                {/* Stats grid */}
                <div className="p-6">
                  <div className="grid grid-cols-2 md:grid-cols-3 gap-3 mb-6">
                    {[
                      { label: 'Farm Area',         value: `${selected.areaHectares.toFixed(2)} ha` },
                      { label: 'Current NDVI',      value: selected.currentNdvi.toFixed(3),           cls: NDVI_COLOR(selected.currentNdvi) },
                      { label: 'Predicted Yield',   value: `${selected.predictedYieldMeanQuintals.toFixed(1)} quintals` },
                      { label: 'Yield Confidence',  value: `${selected.yieldConfidencePct}%` },
                      { label: 'Existing Bids',     value: selected.existingBidsCount.toString() },
                      { label: 'GPS',               value: `${selected.gpsCentroidLat.toFixed(4)}, ${selected.gpsCentroidLng.toFixed(4)}` },
                      ...(selected.estimatedHarvestFrom ? [{
                        label: 'Harvest Window',
                        value: `${new Date(selected.estimatedHarvestFrom).toLocaleDateString()} - ${new Date(selected.estimatedHarvestTo!).toLocaleDateString()}`,
                      }] : []),
                      { label: 'Crop Cycle',        value: selected.cropCycleStatus },
                    ].map(item => (
                      <div key={item.label} className="bg-gray-50 border border-gray-100 rounded-xl p-3">
                        <p className="text-gray-400 text-xs uppercase tracking-wide font-medium">{item.label}</p>
                        <p className={`font-bold text-sm mt-1 ${(item as any).cls || 'text-gray-800'}`}>
                          {item.value}
                        </p>
                      </div>
                    ))}
                  </div>

                  {/* Bid form */}
                  {!showBid ? (
                    <button onClick={() => setShowBid(true)}
                      className="w-full bg-teal-700 text-white py-3 rounded-xl font-bold hover:bg-teal-600 transition text-sm">
                      Place Purchase Bid
                    </button>
                  ) : (
                    <form onSubmit={handlePlaceBid}
                      className="bg-gray-50 border border-gray-200 rounded-2xl p-5 space-y-4">
                      <div className="flex justify-between items-center">
                        <h3 className="font-bold text-gray-800">Place Bid on {selected.cropType}</h3>
                        <button type="button" onClick={() => setShowBid(false)}
                          className="text-gray-400 hover:text-gray-600 text-sm">
                          Cancel
                        </button>
                      </div>

                      <div className="grid grid-cols-1 md:grid-cols-3 gap-3">
                        <div>
                          <label className="block text-xs font-semibold text-gray-600 mb-1">
                            Quantity (quintals)
                          </label>
                          <input type="number" step="0.1" min="1" required
                            value={bidQty} onChange={e => setBidQty(e.target.value)}
                            placeholder={`Up to ${selected.predictedYieldMeanQuintals.toFixed(0)} qt`}
                            className="w-full px-3 py-2.5 border border-gray-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-teal-500" />
                        </div>
                        <div>
                          <label className="block text-xs font-semibold text-gray-600 mb-1">
                            Price per quintal (ETB)
                          </label>
                          <input type="number" step="1" min="1" required
                            value={bidPrice} onChange={e => setBidPrice(e.target.value)}
                            placeholder="e.g. 2500"
                            className="w-full px-3 py-2.5 border border-gray-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-teal-500" />
                        </div>
                        <div>
                          <label className="block text-xs font-semibold text-gray-600 mb-1">
                            Bid valid for
                          </label>
                          <select value={bidDays} onChange={e => setBidDays(e.target.value)}
                            className="w-full px-3 py-2.5 border border-gray-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-teal-500">
                            {[3, 5, 7, 14, 30].map(d => (
                              <option key={d} value={d}>{d} days</option>
                            ))}
                          </select>
                        </div>
                      </div>

                      {totalValue > 0 && (
                        <div className="bg-white border border-gray-200 rounded-xl p-4 text-sm space-y-1">
                          <div className="flex justify-between">
                            <span className="text-gray-500">Total bid value</span>
                            <span className="font-bold text-teal-700">{totalValue.toLocaleString()} ETB</span>
                          </div>
                          <div className="flex justify-between">
                            <span className="text-gray-500">10% deposit (locked in escrow)</span>
                            <span className="font-semibold text-orange-600">{(totalValue * 0.1).toLocaleString()} ETB</span>
                          </div>
                          <p className="text-xs text-gray-400 pt-1 border-t border-gray-100">
                            Deposit is refunded if the farmer rejects the bid or it expires.
                          </p>
                        </div>
                      )}

                      <button type="submit" disabled={placing}
                        className="w-full bg-teal-700 text-white py-3 rounded-xl font-bold hover:bg-teal-600 disabled:opacity-50 transition text-sm">
                        {placing ? 'Placing bid...' : 'Confirm Bid'}
                      </button>
                    </form>
                  )}
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}

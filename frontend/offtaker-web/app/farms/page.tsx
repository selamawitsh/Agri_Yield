'use client';

import { useState, useEffect, useCallback } from 'react';
import { useRouter, useSearchParams } from 'next/navigation';
import dynamic from 'next/dynamic';
import toast from 'react-hot-toast';
import Navbar from '@/components/Navbar';
import StatusBadge from '@/components/StatusBadge';
import NdviHistoryChart from '@/components/NdviHistoryChart';
import FarmerIdentityCard from '@/components/FarmerIdentityCard';
import CropHealthBadge from '@/components/CropHealthBadge';
import TrustRatingBadge from '@/components/TrustRatingBadge';
import { browseFarms, placeBid, getFarmFullDetail } from '@/lib/api';
import type { FarmMarketplace, FarmBrowseParams, FarmFullDetail } from '@/lib/types';

const FarmMap = dynamic(() => import('@/components/FarmMap'), { ssr: false });

const CROP_TYPES = ['WHEAT', 'TEFF', 'BARLEY', 'MAIZE', 'SORGHUM', 'COFFEE', 'BEANS', 'MILLET'];

const NDVI_COLOR = (v: number) =>
  v >= 0.6 ? 'text-green-700' : v >= 0.4 ? 'text-lime-700' : v >= 0.2 ? 'text-yellow-700' : 'text-red-600';

export default function FarmsPage() {
  const router       = useRouter();
  const searchParams = useSearchParams();

  // Core Data States
  const [farms,            setFarms]            = useState<FarmMarketplace[]>([]);
  const [loading,          setLoading]          = useState(true);
  const [selected,         setSelected]         = useState<FarmMarketplace | null>(null);
  const [detail,           setDetail]           = useState<FarmFullDetail | null>(null);
  const [detailLoading,    setDetailLoading]    = useState(false);
  
  // Bid Form States
  const [showBid,          setShowBid]          = useState(false);
  const [bidQty,           setBidQty]           = useState('');
  const [bidPrice,         setBidPrice]         = useState('');
  const [bidDays,          setBidDays]          = useState('7');
  const [placing,          setPlacing]          = useState(false);

  // Filter Input States
  const [cropType,         setCropType]         = useState('');
  const [region,           setRegion]           = useState('');
  const [harvestOnly,      setHarvestOnly]      = useState(false);
  const [minNdvi,          setMinNdvi]          = useState('');
  const [harvestDateFrom,  setHarvestDateFrom]  = useState('');
  const [harvestDateTo,    setHarvestDateTo]    = useState('');
  const [minYieldQuintals, setMinYieldQuintals] = useState('');

  // Active Query Parameters
  const [activeFilters, setActiveFilters] = useState<FarmBrowseParams>({});

  const load = useCallback(async (filters: FarmBrowseParams = activeFilters) => {
    setLoading(true);
    try {
      const res = await browseFarms(filters);
      if (res.data.success) {
        const fetchedFarms = res.data.data || [];
        setFarms(fetchedFarms);

        const lookup = searchParams.get('lookup');
        if (lookup) {
          const match = fetchedFarms.find(f => f.farmId === lookup);
          if (match) {
            handleSelectFarm(match);
          }
        }
      }
    } catch (err: any) {
      toast.error(err.response?.data?.message || 'Failed to load farms');
    } finally {
      setLoading(false);
    }
  }, [activeFilters, searchParams]);

  useEffect(() => {
    if (!localStorage.getItem('access_token')) { 
      router.push('/login'); 
      return; 
    }
    load(activeFilters);
  }, [activeFilters, load, router]);

  const handleFilter = (e: React.FormEvent) => {
    e.preventDefault();
    
    const params: FarmBrowseParams = {};
    if (cropType)          params.cropType         = cropType;
    if (region)            params.region           = region.trim();
    if (harvestOnly)       params.harvestReady      = true;
    if (minNdvi)           params.minNdvi           = parseFloat(minNdvi);
    if (harvestDateFrom)   params.harvestDateFrom   = harvestDateFrom;
    if (harvestDateTo)     params.harvestDateTo     = harvestDateTo;
    if (minYieldQuintals)  params.minYieldQuintals  = parseFloat(minYieldQuintals);

    setActiveFilters(params);
  };

  const handleSelectFarm = async (farm: FarmMarketplace) => {
    setSelected(farm);
    setShowBid(false);
    setDetail(null);
    setDetailLoading(true);
    try {
      const res = await getFarmFullDetail(farm.farmId);
      if (res.data.success) setDetail(res.data.data);
    } catch (err: any) {
      toast.error('Could not load full farm details');
    } finally {
      setDetailLoading(false);
    }
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
        toast.success('Bid placed! 10% deposit locked in escrow.');
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

  const totalValue = bidQty && bidPrice ? parseFloat(bidQty) * parseFloat(bidPrice) : 0;

  const regionCoords: Record<string, [number, number]> = {
    'Oromia': [8.5, 39.3], 'Amhara': [11.5, 38.0], 'SNNPR': [6.8, 37.5],
    'South Ethiopia': [6.8, 37.5], 'South West Ethiopia': [7.0, 35.5],
    'Tigray': [14.0, 38.5], 'Somali': [7.5, 44.0], 'Afar': [11.8, 41.5],
  };

  const getMapCoords = (farm: FarmMarketplace): [number, number] => {
    if (farm.gpsCentroidLat && farm.gpsCentroidLng &&
        (farm.gpsCentroidLat !== 0 || farm.gpsCentroidLng !== 0)) {
      return [farm.gpsCentroidLat, farm.gpsCentroidLng];
    }
    return regionCoords[farm.region] || [9.145, 40.489];
  };

  const bidStatusColor: Record<string, string> = {
    PENDING: 'bg-yellow-100 text-yellow-700', ACCEPTED: 'bg-green-100 text-green-700',
    REJECTED: 'bg-red-100 text-red-700', CONTRACT_SIGNED: 'bg-blue-100 text-blue-700',
    COMPLETED: 'bg-emerald-100 text-emerald-700', DEFAULTED: 'bg-red-200 text-red-800',
    EXPIRED: 'bg-gray-100 text-gray-500',
  };

  return (
    <div className="min-h-screen bg-gray-50 pb-20 md:pb-0">
      <Navbar />

      <div className="container mx-auto px-4 sm:px-6 py-6 max-w-7xl">

        <div className="flex justify-between items-start mb-6">
          <div>
            <h1 className="text-2xl font-bold text-gray-900">Farm Marketplace</h1>
            <p className="text-gray-500 text-sm mt-1">
              Farms appear here when satellite monitoring predicts harvest readiness.
            </p>
          </div>
          <button onClick={() => load(activeFilters)}
            className="text-sm text-teal-700 font-semibold border border-teal-200 px-4 py-2 rounded-xl hover:bg-teal-50 transition">
            Refresh
          </button>
        </div>

        <form onSubmit={handleFilter}
          className="bg-white rounded-2xl shadow-sm border border-gray-100 p-4 mb-6">
          <div className="flex flex-wrap gap-3 items-end">

            <div className="flex-1 min-w-[130px]">
              <label className="block text-xs font-semibold text-gray-600 mb-1">Crop Type</label>
              <select value={cropType} onChange={e => setCropType(e.target.value)}
                className="w-full px-3 py-2.5 border border-gray-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-teal-500">
                <option value="">All crops</option>
                {CROP_TYPES.map(c => <option key={c} value={c}>{c}</option>)}
              </select>
            </div>

            <div className="flex-1 min-w-[130px]">
              <label className="block text-xs font-semibold text-gray-600 mb-1">Region</label>
              <input type="text" value={region} onChange={e => setRegion(e.target.value)}
                placeholder="e.g. Oromia"
                className="w-full px-3 py-2.5 border border-gray-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-teal-500" />
            </div>

            <div className="flex-1 min-w-[120px]">
              <label className="block text-xs font-semibold text-gray-600 mb-1">Min NDVI</label>
              <input type="number" step="0.01" min="0" max="1" value={minNdvi}
                onChange={e => setMinNdvi(e.target.value)}
                placeholder="0.0 – 1.0"
                className="w-full px-3 py-2.5 border border-gray-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-teal-500" />
            </div>

            <div className="flex-1 min-w-[120px]">
              <label className="block text-xs font-semibold text-gray-600 mb-1">Min Yield (qt)</label>
              <input type="number" step="1" min="0" value={minYieldQuintals}
                onChange={e => setMinYieldQuintals(e.target.value)}
                placeholder="e.g. 50"
                className="w-full px-3 py-2.5 border border-gray-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-teal-500" />
            </div>

            <div className="flex-1 min-w-[140px]">
              <label className="block text-xs font-semibold text-gray-600 mb-1">Harvest From</label>
              <input type="date" value={harvestDateFrom} onChange={e => setHarvestDateFrom(e.target.value)}
                className="w-full px-3 py-2.5 border border-gray-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-teal-500" />
            </div>

            <div className="flex-1 min-w-[140px]">
              <label className="block text-xs font-semibold text-gray-600 mb-1">Harvest To</label>
              <input type="date" value={harvestDateTo} onChange={e => setHarvestDateTo(e.target.value)}
                className="w-full px-3 py-2.5 border border-gray-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-teal-500" />
            </div>

            <div className="flex items-center gap-2 pb-0.5">
              <input type="checkbox" id="harvestOnly" checked={harvestOnly}
                onChange={e => setHarvestOnly(e.target.checked)}
                className="w-4 h-4 accent-teal-600" />
              <label htmlFor="harvestOnly" className="text-sm font-semibold text-gray-600 cursor-pointer whitespace-nowrap">
                Harvest ready only
              </label>
            </div>

            <button type="submit"
              className="bg-teal-700 text-white px-6 py-2.5 rounded-xl text-sm font-semibold hover:bg-teal-600 transition">
              Apply Filters
            </button>
          </div>
        </form>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">

          <div className="lg:col-span-1 space-y-3">
            {loading ? (
              <div className="flex items-center justify-center py-20">
                <div className="animate-spin rounded-full h-10 w-10 border-b-2 border-teal-600" />
              </div>
            ) : farms.length === 0 ? (
              <div className="bg-white rounded-2xl shadow-sm border border-gray-100 p-10 text-center">
                <p className="text-gray-400 font-medium text-sm">No farms match your filters.</p>
                <p className="text-gray-300 text-xs mt-1">
                  Farms appear once geospatial monitoring predicts harvest readiness.
                </p>
              </div>
            ) : farms.map(farm => (
              <button key={farm.farmId}
                onClick={() => handleSelectFarm(farm)}
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
                  <CropHealthBadge ndvi={farm.currentNdvi} compact />
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

          <div className="lg:col-span-2">
            {!selected ? (
              <div className="bg-white rounded-2xl shadow-sm border border-gray-100 p-16 text-center h-full flex flex-col items-center justify-center">
                <svg className="w-16 h-16 text-teal-600 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="1.5" d="M12 3v1m0 16v1m9-9h-1M4 12H3m15.364-6.364l-.707.707M6.343 17.657l-.707.707m12.728 0l-.707-.707M6.343 6.343l-.707-.707M14 12a2 2 0 11-4 0 2 2 0 014 0z" />
                </svg>
                <p className="text-gray-400 font-medium">Select a farm to view details and place a bid</p>
              </div>
            ) : (
              <div className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">

                <div className="bg-teal-800 p-6 text-white">
                  <div className="flex justify-between items-start">
                    <div>
                      <h2 className="text-xl font-bold">{selected.cropType}</h2>
                      <p className="text-teal-200 text-sm mt-0.5">
                        {selected.region} &middot; {selected.kebeleCode}
                      </p>
                      <p className="text-teal-300 text-xs mt-1 font-mono">{selected.farmId}</p>
                    </div>
                    <div className="bg-white/20 rounded-xl p-3 text-center min-w-[64px]">
                      <p className="text-2xl font-black">{selected.agriScore}</p>
                      <p className="text-xs text-teal-200">/ 900</p>
                      <p className="text-xs text-teal-300">Agri-Score</p>
                    </div>
                  </div>
                  <div className="flex flex-wrap gap-2 mt-3">
                    <CropHealthBadge ndvi={selected.currentNdvi} compact />
                    {selected.harvestReady && (
                      <span className="bg-green-400/30 text-green-100 text-xs font-bold px-3 py-1 rounded-full border border-green-400/40">
                        Harvest Ready
                      </span>
                    )}
                    <StatusBadge status={selected.cropCycleStatus} />
                  </div>
                </div>

                <div className="p-6 pb-0">
                  <h3 className="font-bold text-gray-800 mb-3 text-sm">Farm Location — Satellite View</h3>
                  <FarmMap
                    lat={getMapCoords(selected)[0]}
                    lng={getMapCoords(selected)[1]}
                    label={`${selected.cropType} — ${selected.region}`}
                    height={260}
                    ndvi={selected.currentNdvi}
                    areaHectares={selected.areaHectares}
                  />
                  <p className="text-xs text-gray-400 mt-2 text-center">
                    <span className="inline-block mr-1">📍</span> {selected.gpsCentroidLat && selected.gpsCentroidLat !== 0
                      ? 'Exact farm coordinates from satellite registration'
                      : `Approximate region center — ${selected.region}`}
                  </p>
                </div>

                <div className="p-6">
                  <div className="grid grid-cols-2 md:grid-cols-3 gap-3 mb-6">
                    {[
                      { label: 'Farm Area',        value: `${selected.areaHectares.toFixed(2)} ha` },
                      { label: 'Current NDVI',     value: selected.currentNdvi.toFixed(3), cls: NDVI_COLOR(selected.currentNdvi) },
                      { label: 'Predicted Yield',  value: `${selected.predictedYieldMeanQuintals.toFixed(1)} quintals` },
                      { label: 'Yield Confidence', value: `${selected.yieldConfidencePct}%` },
                      { label: 'Existing Bids',    value: selected.existingBidsCount.toString() },
                      { label: 'GPS',              value: `${selected.gpsCentroidLat.toFixed(4)}, ${selected.gpsCentroidLng.toFixed(4)}` },
                      ...(selected.estimatedHarvestFrom ? [{
                        label: 'Harvest Window',
                        value: `${new Date(selected.estimatedHarvestFrom).toLocaleDateString()} – ${new Date(selected.estimatedHarvestTo!).toLocaleDateString()}`,
                      }] : []),
                      { label: 'Crop Cycle', value: selected.cropCycleStatus },
                    ].map(item => (
                      <div key={item.label} className="bg-gray-50 border border-gray-100 rounded-xl p-3">
                        <p className="text-gray-400 text-xs uppercase tracking-wide font-medium">{item.label}</p>
                        <p className={`font-bold text-sm mt-1 ${(item as any).cls || 'text-gray-800'}`}>
                          {item.value}
                        </p>
                      </div>
                    ))}
                  </div>

                  <div className="mb-6">
                    <h3 className="font-bold text-gray-800 mb-3">Crop Health — What It Means for Your Bid</h3>
                    <CropHealthBadge ndvi={selected.currentNdvi} />
                  </div>

                  <div className="mb-6">
                    <h3 className="font-bold text-gray-800 mb-3">NDVI History (90 Days)</h3>
                    {detailLoading ? (
                      <div className="flex items-center justify-center py-10">
                        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-teal-600" />
                      </div>
                    ) : (
                      <NdviHistoryChart data={detail?.ndviHistory || []} />
                    )}
                  </div>

                  <div className="mb-6">
                    <h3 className="font-bold text-gray-800 mb-3">Farmer Trust Rating</h3>
                    {detailLoading ? (
                      <div className="flex items-center justify-center py-10">
                        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-teal-600" />
                      </div>
                    ) : (
                      <TrustRatingBadge
                        agriScore={detail?.farmer?.agriScore ?? selected.agriScore}
                        seasonsCompleted={detail?.farmer?.totalSeasonsCompleted}
                      />
                    )}
                  </div>

                  <div className="mb-6">
                    <h3 className="font-bold text-gray-800 mb-3">Farmer Contact Information</h3>
                    {detailLoading ? (
                      <div className="flex items-center justify-center py-10">
                        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-teal-600" />
                      </div>
                    ) : (
                      <FarmerIdentityCard farmer={detail?.farmer || null} />
                    )}
                  </div>

                  <div className="mb-6">
                    <h3 className="font-bold text-gray-800 mb-3">Existing Bids on This Farm</h3>
                    {detailLoading ? (
                      <div className="flex items-center justify-center py-10">
                        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-teal-600" />
                      </div>
                    ) : !detail?.bids || detail.bids.length === 0 ? (
                      <div className="bg-gray-50 rounded-2xl p-5 text-center text-gray-400 text-sm flex flex-col items-center justify-center">
                        <svg className="w-8 h-8 text-gray-400 mb-2" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                        </svg>
                        <p>No other bids have been placed on this farm yet.</p>
                      </div>
                    ) : (
                      <div className="space-y-2">
                        {detail.bids.map(bid => (
                          <div key={bid.id} className="flex items-center justify-between border border-gray-100 rounded-xl p-3">
                            <div>
                              <p className="text-sm font-semibold text-gray-800">
                                {bid.quantityQuintals} qt @ {bid.pricePerQuintalEtb.toLocaleString()} ETB/qt
                              </p>
                              <p className="text-xs text-gray-400 mt-0.5">
                                Total: {bid.totalValueEtb.toLocaleString()} ETB · {new Date(bid.createdAt).toLocaleDateString()}
                              </p>
                            </div>
                            <span className={`text-xs font-bold px-2.5 py-0.5 rounded-full ${bidStatusColor[bid.status] || 'bg-gray-100 text-gray-600'}`}>
                              {bid.status.replace(/_/g, ' ')}
                            </span>
                          </div>
                        ))}
                      </div>
                    )}
                  </div>

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
                          className="text-gray-400 hover:text-gray-600 text-sm">Cancel</button>
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
                          <label className="block text-xs font-semibold text-gray-600 mb-1">Bid valid for</label>
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
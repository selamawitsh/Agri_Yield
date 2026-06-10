'use client';

import { useEffect, useState, useCallback } from 'react';
import { useRouter, useParams } from 'next/navigation';
import Link from 'next/link';
import dynamic from 'next/dynamic';
import toast from 'react-hot-toast';
import api from '@/lib/api';
import Navbar from '@/components/Navbar';
import StatusBadge from '@/components/StatusBadge';
import EscrowStateMachine from '@/components/EscrowStateMachine';
import NdviChart from '@/components/NdviChart';
import AprHistoryChart from '@/components/AprHistoryChart';
import { Investment, PayoutRecord } from '@/lib/types';

const FarmMap = dynamic(() => import('@/components/FarmMap'), { ssr: false });

interface NdviPoint {
  farmId: string;
  ndviValue: number;
  cloudCoverage: number;
  healthStatus: string;
  sentinelSceneId: string;
  recordedDate: string;
}

interface YieldPred {
  predictedYieldMin: number;
  predictedYieldMax: number;
  predictedYieldMean: number;
  confidencePct: number;
  weeksToHarvest: number;
}

interface FarmMapData {
  centroidLat: number;
  centroidLng: number;
  areaHectares: number;
  latestNdvi?: { ndviValue: number; healthStatus: string; cloudCoverage: number; recordedDate: string };
}

// Region fallback coords if farm boundary not in MongoDB yet
const REGION_COORDS: Record<string, [number, number]> = {
  'Oromia': [8.5, 39.3], 'Amhara': [11.5, 38.0], 'SNNPR': [6.8, 37.5],
  'Tigray': [14.0, 38.5], 'Somali': [7.5, 44.0], 'Afar': [11.8, 41.5],
};

export default function InvestmentDetailPage() {
  const router = useRouter();
  const params = useParams();
  const investmentId = params.id as string;

  const [investment, setInvestment]   = useState<Investment | null>(null);
  const [ndviHistory, setNdviHistory] = useState<NdviPoint[]>([]);
  const [yieldPred, setYieldPred]     = useState<YieldPred | null>(null);
  const [farmMapData, setFarmMapData] = useState<FarmMapData | null>(null);
  const [payouts, setPayouts]         = useState<PayoutRecord[]>([]);
  const [loading, setLoading]         = useState(true);

  const fetchAll = useCallback(async () => {
    try {
      // 1. Investment
      const invRes = await api.get(`/portfolio/${investmentId}`);
      if (!invRes.data.success) throw new Error('Not found');
      const inv: Investment = invRes.data.data;
      setInvestment(inv);
      const fId = inv.farmId;

      // 2. NDVI history — geospatial service
      try {
        const ndviRes = await api.get(`/geospatial/ndvi-history/${fId}?days=90`);
        if (ndviRes.data.success) setNdviHistory(ndviRes.data.data || []);
      } catch {}

      // 3. Yield prediction
      try {
        const yRes = await api.get(`/geospatial/farms/${fId}/yield`);
        if (yRes.data.success && yRes.data.data) setYieldPred(yRes.data.data);
      } catch {}

      // 4. Farm map — real GPS centroid from geospatial service
      try {
        const mapRes = await api.get(`/geospatial/farm-map/${fId}`);
        if (mapRes.data.success) setFarmMapData(mapRes.data.data);
      } catch {}

      // 5. Payouts
      try {
        const pRes = await api.get('/portfolio/payouts');
        if (pRes.data.success) {
          setPayouts((pRes.data.data || []).filter((p: PayoutRecord) => p.investmentId === investmentId));
        }
      } catch {}

    } catch (err: any) {
      toast.error(err.response?.data?.message || 'Investment not found');
      router.push('/portfolio');
    } finally {
      setLoading(false);
    }
  }, [investmentId, router]);

  useEffect(() => {
    const token = localStorage.getItem('access_token');
    if (!token) { router.push('/login'); return; }
    fetchAll();
  }, [fetchAll]);

  if (loading) return (
    <div className="min-h-screen flex items-center justify-center">
      <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-green-600" />
    </div>
  );
  if (!investment) return null;

  const expectedReturn = investment.amountEtb * investment.expectedReturnPct / 100;
  const latestNdvi     = ndviHistory.length > 0 ? ndviHistory[ndviHistory.length - 1] : null;
  const ndviChartData  = ndviHistory.map(n => ({ date: n.recordedDate, ndviValue: n.ndviValue }));

  // Map coordinates: use real GPS from geospatial service, fallback to region center
  const [mapLat, mapLng] = farmMapData
    ? [farmMapData.centroidLat, farmMapData.centroidLng]
    : REGION_COORDS[investment.region] ?? [9.145, 40.489];

  const mapNdvi = farmMapData?.latestNdvi?.ndviValue ?? latestNdvi?.ndviValue ?? undefined;

  return (
    <div className="min-h-screen bg-gray-50 pb-12">
      <Navbar />
      <div className="container mx-auto px-4 sm:px-6 py-6 max-w-4xl">

        <button onClick={() => router.back()}
          className="flex items-center gap-2 text-green-600 text-sm mb-5 font-semibold hover:underline">
          ← Back to Portfolio
        </button>

        {/* Header */}
        <div className="bg-white rounded-3xl shadow-sm border border-gray-100 overflow-hidden mb-6">
          <div className="bg-gradient-to-r from-green-700 to-emerald-900 p-6 text-white">
            <div className="flex justify-between items-start">
              <div>
                <h1 className="text-2xl font-bold">{investment.cropType} Investment</h1>
                <p className="text-green-100 text-sm mt-1">{investment.region} · {investment.seasonName}</p>
              </div>
              <StatusBadge status={investment.status} />
            </div>
          </div>

          <div className="p-6">
            <div className="grid grid-cols-2 md:grid-cols-3 gap-3 mb-5">
              {[
                { label: 'Amount Invested',    value: `${investment.amountEtb.toLocaleString()} ETB`, highlight: true },
                { label: 'Expected APR',       value: `${investment.expectedReturnPct}%` },
                { label: 'Expected Return/yr', value: `+${expectedReturn.toFixed(0)} ETB` },
                { label: 'Actual APR',         value: investment.actualReturnPct ? `${investment.actualReturnPct}%` : 'Pending' },
                { label: 'Invested On',        value: new Date(investment.createdAt).toLocaleDateString() },
                { label: 'Last Updated',       value: new Date(investment.updatedAt).toLocaleDateString() },
              ].map(item => (
                <div key={item.label} className={`rounded-2xl p-4 ${item.highlight ? 'bg-green-50 border border-green-200' : 'bg-gray-50 border border-gray-100'}`}>
                  <p className="text-xs text-gray-400 font-medium uppercase tracking-wide">{item.label}</p>
                  <p className={`font-bold mt-1 ${item.highlight ? 'text-green-700 text-lg' : 'text-gray-800 text-sm'}`}>{item.value}</p>
                </div>
              ))}
            </div>

            {investment.notes && (
              <div className="mb-3 bg-blue-50 rounded-2xl p-3 border border-blue-100">
                <p className="text-xs text-blue-500 font-semibold mb-1">Your note</p>
                <p className="text-sm text-blue-800 italic">"{investment.notes}"</p>
              </div>
            )}
            {investment.cancelledReason && (
              <div className="mb-3 bg-red-50 rounded-2xl p-3 border border-red-100">
                <p className="text-xs text-red-500 font-semibold mb-1">Cancellation reason</p>
                <p className="text-sm text-red-800">{investment.cancelledReason}</p>
              </div>
            )}

            <div className="border-t pt-4 space-y-1 text-xs text-gray-400 font-mono">
              <p>Investment ID: {investment.id}</p>
              <p>Farm ID: {investment.farmId}</p>
              <p>Crop Cycle: {investment.cropCycleId}</p>
            </div>
          </div>
        </div>

        {/* Escrow State Machine */}
        <div className="bg-white rounded-3xl shadow-sm border border-gray-100 p-6 mb-6">
          <h3 className="font-bold text-gray-800 mb-6">Escrow Progress</h3>
          <EscrowStateMachine status={investment.status} />
          <div className="mt-5 bg-amber-50 border border-amber-100 rounded-2xl p-4 text-sm text-amber-800">
            <strong>🔒 Escrow:</strong> Funds secured. Released only when vouchers pass 6-check validation and harvest is confirmed.
          </div>
        </div>

        {/* Farm Location — Real satellite basemap via Leaflet + ESRI */}
        <div className="bg-white rounded-3xl shadow-sm border border-gray-100 p-6 mb-6">
          <div className="flex justify-between items-start mb-4">
            <div>
              <h3 className="font-bold text-gray-800">Farm Location</h3>
              <p className="text-xs text-gray-400 mt-0.5">
                Satellite imagery · ESRI World Imagery
                {farmMapData ? ' · Real GPS coordinates' : ' · Approximate region coordinates'}
              </p>
            </div>
            {farmMapData && (
              <span className="text-xs text-gray-400 bg-gray-100 px-3 py-1 rounded-full">
                {farmMapData.areaHectares.toFixed(1)} ha ·{' '}
                {farmMapData.centroidLat.toFixed(4)}°N,{' '}
                {farmMapData.centroidLng.toFixed(4)}°E
              </span>
            )}
          </div>

          <FarmMap
            lat={mapLat}
            lng={mapLng}
            label={`${investment.cropType} — ${investment.region}`}
            height={320}
            ndvi={mapNdvi}
            areaHectares={farmMapData?.areaHectares}
          />

          {/* NDVI overlay info below map */}
          {latestNdvi && (
            <div className="flex flex-wrap gap-2 mt-3">
              <span className={`text-xs font-bold px-3 py-1.5 rounded-full ${
                latestNdvi.healthStatus === 'EXCELLENT' ? 'bg-green-100 text-green-700' :
                latestNdvi.healthStatus === 'GOOD'      ? 'bg-lime-100 text-lime-700' :
                latestNdvi.healthStatus === 'MODERATE'  ? 'bg-yellow-100 text-yellow-700' :
                'bg-red-100 text-red-700'
              }`}>
                🌿 NDVI {latestNdvi.ndviValue.toFixed(3)} — {latestNdvi.healthStatus}
              </span>
              <span className="text-xs text-gray-500 bg-gray-100 px-3 py-1.5 rounded-full">
                ☁️ Cloud cover: {latestNdvi.cloudCoverage.toFixed(1)}%
              </span>
              <span className="text-xs text-gray-500 bg-gray-100 px-3 py-1.5 rounded-full">
                📅 {new Date(latestNdvi.recordedDate).toLocaleDateString()}
              </span>
              {latestNdvi.sentinelSceneId && (
                <span className="text-xs text-gray-400 bg-gray-50 px-3 py-1.5 rounded-full font-mono">
                  🛰 {latestNdvi.sentinelSceneId}
                </span>
              )}
            </div>
          )}
        </div>

        {/* NDVI Chart */}
        <div className="bg-white rounded-3xl shadow-sm border border-gray-100 p-6 mb-6">
          <h3 className="font-bold text-gray-800 mb-1">NDVI Trend</h3>
          <p className="text-xs text-gray-400 mb-4">Satellite vegetation index — last 90 days</p>
          {ndviChartData.length > 0 ? (
            <NdviChart data={ndviChartData} height={200} />
          ) : (
            <div className="h-32 flex items-center justify-center bg-gray-50 rounded-2xl text-gray-400 text-sm">
              No NDVI readings yet — scheduler runs every 5 days
            </div>
          )}
        </div>

        {/* Yield Prediction */}
        {yieldPred && (
          <div className="bg-white rounded-3xl shadow-sm border border-gray-100 p-6 mb-6">
            <div className="flex justify-between items-start mb-4">
              <div>
                <h3 className="font-bold text-gray-800">Yield Prediction</h3>
                <p className="text-xs text-gray-400 mt-0.5">XGBoost ML model · {yieldPred.weeksToHarvest} weeks to harvest</p>
              </div>
              <span className="bg-green-100 text-green-700 text-xs font-bold px-3 py-1 rounded-full">
                {yieldPred.confidencePct}% confidence
              </span>
            </div>
            <div className="grid grid-cols-3 gap-3">
              {[
                { label: 'Min Yield',  value: `${yieldPred.predictedYieldMin.toFixed(1)} qt/ha`, color: 'text-gray-700' },
                { label: 'Mean Yield', value: `${yieldPred.predictedYieldMean.toFixed(1)} qt/ha`, color: 'text-green-700' },
                { label: 'Max Yield',  value: `${yieldPred.predictedYieldMax.toFixed(1)} qt/ha`, color: 'text-gray-700' },
              ].map(y => (
                <div key={y.label} className="bg-gray-50 rounded-2xl p-4 text-center border border-gray-100">
                  <p className="text-xs text-gray-400 font-medium mb-1">{y.label}</p>
                  <p className={`font-bold text-base ${y.color}`}>{y.value}</p>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* APR History */}
        <div className="bg-white rounded-3xl shadow-sm border border-gray-100 p-6 mb-6">
          <h3 className="font-bold text-gray-800 mb-1">APR History</h3>
          <p className="text-xs text-gray-400 mb-4">Return rate evolution with NDVI and weather signals</p>
          <AprHistoryChart
            baseApr={investment.expectedReturnPct * 0.85}
            currentApr={investment.expectedReturnPct}
          />
        </div>

        {/* Payout Calculator */}
        <div className="bg-white rounded-3xl shadow-sm border border-gray-100 p-6 mb-6">
          <h3 className="font-bold text-gray-800 mb-4">Payout Calculator</h3>
          <div className="grid grid-cols-3 gap-4">
            {[
              { label: 'Principal',       value: `${investment.amountEtb.toLocaleString()} ETB`,              color: 'text-gray-800',    bg: 'bg-gray-50' },
              { label: 'Expected Return', value: `+${expectedReturn.toFixed(0)} ETB`,                         color: 'text-green-700',   bg: 'bg-green-50' },
              { label: 'Total Payout',    value: `${(investment.amountEtb + expectedReturn).toFixed(0)} ETB`, color: 'text-emerald-700', bg: 'bg-emerald-50' },
            ].map(c => (
              <div key={c.label} className={`${c.bg} rounded-2xl p-4 text-center border border-gray-100`}>
                <p className="text-xs text-gray-400 font-medium mb-1">{c.label}</p>
                <p className={`font-bold text-base ${c.color}`}>{c.value}</p>
              </div>
            ))}
          </div>
        </div>

        {/* Voucher Tracker link */}
        <Link href={`/portfolio/${investmentId}/vouchers`}
          className="flex items-center justify-between bg-white rounded-3xl shadow-sm border border-gray-100 p-5 mb-6 hover:border-green-400 hover:shadow-md transition group">
          <div className="flex items-center gap-4">
            <div className="w-12 h-12 bg-green-100 rounded-2xl flex items-center justify-center text-2xl">🎟️</div>
            <div>
              <p className="font-bold text-gray-800">Voucher Tracker</p>
              <p className="text-xs text-gray-400 mt-0.5">View input voucher redemption progress for this farm</p>
            </div>
          </div>
          <span className="text-green-600 font-bold group-hover:translate-x-1 transition-transform">→</span>
        </Link>

        {/* Received Payouts */}
        {payouts.length > 0 && (
          <div className="bg-white rounded-3xl shadow-sm border border-gray-100 p-6">
            <h3 className="font-bold text-gray-800 mb-4">Received Payouts</h3>
            <div className="space-y-3">
              {payouts.map(p => (
                <div key={p.id} className="flex justify-between items-center border border-gray-100 rounded-2xl p-4">
                  <div>
                    <p className="text-sm font-semibold text-gray-700">{p.payoutReason || 'Settlement'}</p>
                    <p className="text-xs text-gray-400">{new Date(p.paidAt).toLocaleDateString()}</p>
                  </div>
                  <div className="text-right">
                    <p className="font-bold text-green-600">{p.totalEtb.toLocaleString()} ETB</p>
                    <p className="text-xs text-gray-400">{p.actualApr}% APR</p>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}

      </div>
    </div>
  );
}

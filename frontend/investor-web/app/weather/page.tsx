'use client';
import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import Navbar from '@/components/Navbar';
import WeatherPanel from '@/components/WeatherPanel';
import api from '@/lib/api';

interface Portfolio {
  farmId: string;
  farmName?: string;
  cropType?: string;
  region?: string;
}

export default function WeatherPage() {
  const router = useRouter();
  const [portfolioFarms, setPortfolioFarms] = useState<Portfolio[]>([]);
  const [selectedFarmId, setSelectedFarmId] = useState<string>('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const token = localStorage.getItem('access_token');
    if (!token) { router.push('/login'); return; }
    loadPortfolio();
  }, []);

  async function loadPortfolio() {
    try {
      const res = await api.get('/portfolio');
      if (res.data.success) {
        const investments = res.data.data || [];
        const farms: Portfolio[] = investments.map((inv: any) => ({
          farmId: inv.farmId,
          farmName: inv.farmName,
          cropType: inv.cropType,
          region: inv.region,
        }));
        // deduplicate by farmId
        const unique = farms.filter((f, i, arr) =>
          arr.findIndex(x => x.farmId === f.farmId) === i);
        setPortfolioFarms(unique);
        if (unique.length > 0) setSelectedFarmId(unique[0].farmId);
      }
    } catch { /* silent */ }
    finally { setLoading(false); }
  }

  const selectedFarm = portfolioFarms.find(f => f.farmId === selectedFarmId);

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />
      <div className="max-w-4xl mx-auto px-4 py-8">
        <div className="flex items-center justify-between mb-6">
          <div>
            <h1 className="text-2xl font-bold text-gray-900">Weather & Climate</h1>
            <p className="text-sm text-gray-500 mt-0.5">
              Monitor weather conditions across your invested farms
            </p>
          </div>
        </div>

        {loading ? (
          <div className="flex items-center justify-center py-20">
            <div className="animate-spin rounded-full h-10 w-10 border-b-2 border-green-600" />
          </div>
        ) : portfolioFarms.length === 0 ? (
          <div className="text-center py-20">
            <p className="text-4xl mb-3">🌤️</p>
            <p className="text-gray-500 font-medium">No farms in your portfolio yet</p>
            <p className="text-sm text-gray-400 mt-1">Invest in a farm to monitor its weather</p>
          </div>
        ) : (
          <div className="space-y-6">
            {/* Farm selector */}
            {portfolioFarms.length > 1 && (
              <div className="bg-white rounded-2xl border border-slate-100 p-4">
                <label className="block text-xs font-bold text-slate-500 uppercase tracking-wide mb-2">
                  Select Farm
                </label>
                <div className="flex flex-wrap gap-2">
                  {portfolioFarms.map(f => (
                    <button key={f.farmId}
                      onClick={() => setSelectedFarmId(f.farmId)}
                      className={`px-4 py-2 rounded-xl text-sm font-semibold transition-colors ${
                        selectedFarmId === f.farmId
                          ? 'bg-green-600 text-white'
                          : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
                      }`}>
                      {f.farmName || f.cropType || f.farmId.slice(0, 8)}
                    </button>
                  ))}
                </div>
              </div>
            )}

            {/* Farm info */}
            {selectedFarm && (
              <div className="bg-white rounded-2xl border border-slate-100 p-4 flex items-center gap-4">
                <div className="w-10 h-10 rounded-xl bg-green-50 flex items-center justify-center text-xl">🌾</div>
                <div>
                  <p className="font-bold text-slate-900">
                    {selectedFarm.farmName || selectedFarm.cropType || 'Farm'}
                  </p>
                  <p className="text-xs text-slate-400">{selectedFarm.region} • Farm ID: {selectedFarmId.slice(0, 8)}...</p>
                </div>
              </div>
            )}

            {/* Weather panel — full detail */}
            {selectedFarmId && <WeatherPanel farmId={selectedFarmId} compact={false} />}
          </div>
        )}
      </div>
    </div>
  );
}

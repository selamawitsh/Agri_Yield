'use client';

import { useEffect, useState } from 'react';
import { useRouter, useSearchParams } from 'next/navigation';
import {
  getWeatherCurrent,
  getWeatherRisk,
  getDroughtStatus,
  getWeatherAlerts,
} from '@/lib/api';
import Navbar from '@/components/Navbar';
import {
  Thermometer,
  Droplets,
  AlertTriangle,
  ShieldCheck,
  Sun,
  CloudRain,
  Wind,
  CloudSnow,
} from 'lucide-react';

export default function WeatherPage() {
  const router       = useRouter();
  const searchParams = useSearchParams();
  const farmId       = searchParams.get('farmId') || '';

  const [current, setCurrent] = useState<any>(null);
  const [risk,    setRisk]    = useState<any>(null);
  const [drought, setDrought] = useState<any>(null);
  const [alerts,  setAlerts]  = useState<any[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const token = localStorage.getItem('access_token');
    if (!token) { router.push('/login'); return; }
    if (!farmId) { router.push('/dashboard'); return; }
    loadWeather();
  }, [farmId]);

  async function loadWeather() {
    setLoading(true);
    const results = await Promise.allSettled([
      getWeatherCurrent(farmId),
      getWeatherRisk(farmId),
      getDroughtStatus(farmId),
      getWeatherAlerts(farmId),
    ]);
    // Each result.value is an axios response: { data: { success, data: actualData } }
    if (results[0].status === 'fulfilled') setCurrent(results[0].value.data.data);
    if (results[1].status === 'fulfilled') setRisk(results[1].value.data.data);
    if (results[2].status === 'fulfilled') setDrought(results[2].value.data.data);
    if (results[3].status === 'fulfilled') setAlerts(results[3].value.data.data || []);
    setLoading(false);
  }

  const riskColor: Record<string, string> = {
    LOW:      'text-green-700 bg-green-50 border-green-200',
    MEDIUM:   'text-amber-700 bg-amber-50 border-amber-200',
    HIGH:     'text-orange-700 bg-orange-50 border-orange-200',
    CRITICAL: 'text-red-700 bg-red-50 border-red-200',
  };

  function AlertIcon({ type }: { type: string }) {
    const cls = 'h-5 w-5 shrink-0';
    if (type === 'FROST_WARNING')    return <CloudSnow className={`${cls} text-blue-500`} />;
    if (type === 'HEAVY_RAIN')       return <CloudRain className={`${cls} text-blue-600`} />;
    if (type === 'DROUGHT_WARNING')  return <Sun className={`${cls} text-amber-500`} />;
    if (type === 'DROUGHT_TRIGGER')  return <AlertTriangle className={`${cls} text-red-500`} />;
    if (type === 'HEATWAVE')         return <Wind className={`${cls} text-orange-500`} />;
    return <AlertTriangle className={`${cls} text-gray-400`} />;
  }

  return (
    <div className="min-h-screen bg-gray-50 pb-20 md:pb-0">
      <Navbar />
      <div className="container mx-auto px-4 sm:px-6 py-6 max-w-3xl">

        <div className="flex items-center gap-3 mb-6">
          <button
            onClick={() => router.back()}
            className="text-teal-700 text-sm font-semibold hover:underline"
          >
            ← Back
          </button>
          <h1 className="text-xl font-bold text-gray-900">
            Farm Weather
            <span className="ml-2 text-sm font-mono text-gray-400">
              {farmId.slice(0, 8)}…
            </span>
          </h1>
        </div>

        {loading ? (
          <div className="flex items-center justify-center py-20">
            <div className="animate-spin rounded-full h-10 w-10 border-b-2 border-teal-600" />
          </div>
        ) : (
          <div className="space-y-6">

            {/* Current conditions */}
            <div className="bg-white rounded-2xl border border-gray-100 shadow-sm p-6">
              <h2 className="text-base font-bold text-gray-900 mb-4">Current Conditions</h2>
              <div className="grid grid-cols-2 sm:grid-cols-4 gap-4">
                <div className="bg-gray-50 rounded-xl p-3">
                  <div className="flex items-center gap-1.5 mb-1">
                    <Thermometer className="h-3.5 w-3.5 text-orange-500" />
                    <p className="text-xs text-gray-400 uppercase tracking-wide">Temperature</p>
                  </div>
                  <p className="text-xl font-black text-gray-800">
                    {current ? `${current.temperatureC?.toFixed(1)}°C` : '—'}
                  </p>
                </div>
                <div className="bg-gray-50 rounded-xl p-3">
                  <div className="flex items-center gap-1.5 mb-1">
                    <Droplets className="h-3.5 w-3.5 text-blue-500" />
                    <p className="text-xs text-gray-400 uppercase tracking-wide">Rainfall</p>
                  </div>
                  <p className="text-xl font-black text-blue-600">
                    {current ? `${current.rainfallMm?.toFixed(1)}mm` : '—'}
                  </p>
                </div>
                <div className="bg-gray-50 rounded-xl p-3">
                  <div className="flex items-center gap-1.5 mb-1">
                    <Sun className="h-3.5 w-3.5 text-amber-500" />
                    <p className="text-xs text-gray-400 uppercase tracking-wide">Dry Day</p>
                  </div>
                  <p className="text-xl font-black text-orange-600">
                    {current ? (current.isDryDay ? 'Yes' : 'No') : '—'}
                  </p>
                </div>
                <div className="bg-gray-50 rounded-xl p-3">
                  <div className="flex items-center gap-1.5 mb-1">
                    <ShieldCheck className="h-3.5 w-3.5 text-teal-500" />
                    <p className="text-xs text-gray-400 uppercase tracking-wide">Risk Level</p>
                  </div>
                  {risk ? (
                    <span className={`text-xs font-bold px-2.5 py-1 rounded-lg border ${riskColor[risk.riskLevel] || 'text-gray-600 bg-gray-50 border-gray-200'}`}>
                      {risk.riskLevel}
                    </span>
                  ) : (
                    <p className="text-xl font-black text-gray-300">—</p>
                  )}
                </div>
              </div>
            </div>

            {/* Drought status */}
            {drought && (
              <div className={`rounded-2xl border p-5 ${
                drought.isTriggered
                  ? 'bg-red-50 border-red-200'
                  : 'bg-green-50 border-green-200'
              }`}>
                <div className="flex items-center gap-3 mb-3">
                  {drought.isTriggered
                    ? <AlertTriangle className="h-6 w-6 text-red-600 shrink-0" />
                    : <ShieldCheck className="h-6 w-6 text-green-600 shrink-0" />
                  }
                  <div>
                    <p className={`font-bold ${drought.isTriggered ? 'text-red-700' : 'text-green-700'}`}>
                      {drought.isTriggered ? 'Drought Triggered' : 'Drought Status: Normal'}
                    </p>
                    <p className="text-xs text-gray-500">
                      {drought.consecutiveDryDays} / {drought.droughtThresholdDays} consecutive dry days
                    </p>
                  </div>
                </div>
                <div className="bg-white/60 rounded-full h-2 overflow-hidden">
                  <div
                    className={`h-2 rounded-full transition-all ${drought.isTriggered ? 'bg-red-500' : 'bg-orange-400'}`}
                    style={{
                      width: `${Math.min(100, (drought.consecutiveDryDays / drought.droughtThresholdDays) * 100)}%`
                    }}
                  />
                </div>
                {drought.isTriggered && (
                  <p className="text-sm text-red-600 mt-3 font-medium">
                    Parametric insurance triggered. Investors will receive partial refund. Consider adjusting logistics schedule.
                  </p>
                )}
              </div>
            )}

            {/* Weather alerts */}
            {alerts.length > 0 ? (
              <div className="bg-white rounded-2xl border border-gray-100 shadow-sm p-6">
                <h2 className="text-base font-bold text-gray-900 mb-4">
                  Weather Alerts ({alerts.length})
                </h2>
                <div className="space-y-3">
                  {alerts.map((alert, i) => (
                    <div key={alert.id || i} className="flex items-start gap-3 p-3 bg-gray-50 rounded-xl border border-gray-100">
                      <AlertIcon type={alert.alertType} />
                      <div>
                        <p className="text-sm font-semibold text-gray-700">
                          {alert.alertType.replace(/_/g, ' ')}
                        </p>
                        <p className="text-xs text-gray-500 mt-0.5">{alert.messageEn}</p>
                        {alert.createdAt && (
                          <p className="text-xs text-gray-400 mt-1 font-mono">
                            {new Date(alert.createdAt).toLocaleDateString()}
                          </p>
                        )}
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            ) : (
              <div className="bg-white rounded-2xl border border-gray-100 shadow-sm p-10 text-center">
                <ShieldCheck className="h-10 w-10 text-green-400 mx-auto mb-3" />
                <p className="text-gray-500 font-medium">No active weather alerts</p>
                <p className="text-gray-400 text-sm mt-1">Weather conditions are normal for this farm</p>
              </div>
            )}

          </div>
        )}
      </div>
    </div>
  );
}

'use client';
import { useEffect, useState } from 'react';
import { useRouter, useSearchParams } from 'next/navigation';
import { getWeatherCurrent, getWeatherRisk, getDroughtStatus, getWeatherAlerts } from '@/lib/api';

export default function WeatherPage() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const farmId = searchParams.get('farmId') || '';

  const [current, setCurrent] = useState<any>(null);
  const [risk, setRisk] = useState<any>(null);
  const [drought, setDrought] = useState<any>(null);
  const [alerts, setAlerts] = useState<any[]>([]);
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
    if (results[0].status === 'fulfilled') setCurrent(results[0].value.data);
    if (results[1].status === 'fulfilled') setRisk(results[1].value.data);
    if (results[2].status === 'fulfilled') setDrought(results[2].value.data);
    if (results[3].status === 'fulfilled') setAlerts(results[3].value.data);
    setLoading(false);
  }

  const riskColor: Record<string, string> = {
    LOW: 'text-green-700 bg-green-50 border-green-200',
    MEDIUM: 'text-amber-700 bg-amber-50 border-amber-200',
    HIGH: 'text-orange-700 bg-orange-50 border-orange-200',
    CRITICAL: 'text-red-700 bg-red-50 border-red-200',
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <nav className="bg-blue-700 text-white px-6 py-4 flex items-center gap-4">
        <button onClick={() => router.back()} className="text-blue-200 hover:text-white text-sm">← Back</button>
        <span className="font-bold">Farm Weather — {farmId.slice(0, 8)}...</span>
      </nav>

      <div className="max-w-3xl mx-auto px-4 py-8">
        {loading ? (
          <div className="flex items-center justify-center py-20">
            <div className="animate-spin rounded-full h-10 w-10 border-b-2 border-blue-700" />
          </div>
        ) : (
          <div className="space-y-6">
            {/* Current conditions */}
            <div className="bg-white rounded-2xl border border-slate-100 p-6">
              <h2 className="text-base font-bold text-slate-900 mb-4">Current Conditions</h2>
              <div className="grid grid-cols-2 sm:grid-cols-4 gap-4">
                <div>
                  <p className="text-xs text-slate-400 uppercase tracking-wide">Temperature</p>
                  <p className="text-2xl font-black text-slate-800">
                    {current ? `${current.temperatureC?.toFixed(1)}°C` : '—'}
                  </p>
                </div>
                <div>
                  <p className="text-xs text-slate-400 uppercase tracking-wide">Rainfall</p>
                  <p className="text-2xl font-black text-blue-600">
                    {current ? `${current.rainfallMm?.toFixed(1)}mm` : '—'}
                  </p>
                </div>
                <div>
                  <p className="text-xs text-slate-400 uppercase tracking-wide">Dry Day</p>
                  <p className="text-2xl font-black text-orange-600">
                    {current ? (current.isDryDay ? 'Yes' : 'No') : '—'}
                  </p>
                </div>
                <div>
                  <p className="text-xs text-slate-400 uppercase tracking-wide mb-1">Risk Level</p>
                  {risk ? (
                    <span className={`text-sm font-bold px-3 py-1 rounded-lg border ${riskColor[risk.riskLevel]}`}>
                      {risk.riskLevel}
                    </span>
                  ) : <p className="text-2xl font-black text-slate-300">—</p>}
                </div>
              </div>
            </div>

            {/* Drought */}
            {drought && (
              <div className={`rounded-2xl border p-5 ${drought.isTriggered ? 'bg-red-50 border-red-200' : 'bg-green-50 border-green-200'}`}>
                <div className="flex items-center gap-3 mb-3">
                  <span className="text-2xl">{drought.isTriggered ? '🚨' : '✅'}</span>
                  <div>
                    <p className={`font-bold ${drought.isTriggered ? 'text-red-700' : 'text-green-700'}`}>
                      {drought.isTriggered ? 'Drought Triggered' : 'Drought: Normal'}
                    </p>
                    <p className="text-xs text-slate-500">{drought.consecutiveDryDays} / {drought.droughtThresholdDays} dry days</p>
                  </div>
                </div>
                <div className="bg-white/50 rounded-full h-2">
                  <div className="h-2 rounded-full bg-orange-500 transition-all"
                    style={{ width: `${Math.min(100, (drought.consecutiveDryDays / drought.droughtThresholdDays) * 100)}%` }} />
                </div>
                {drought.isTriggered && (
                  <p className="text-sm text-red-600 mt-3 font-medium">
                    ⚠️ Harvest may be affected. Consider adjusting logistics schedule.
                  </p>
                )}
              </div>
            )}

            {/* Alerts */}
            {alerts.length > 0 && (
              <div className="bg-white rounded-2xl border border-slate-100 p-6">
                <h2 className="text-base font-bold text-slate-900 mb-4">
                  Weather Alerts ({alerts.length})
                </h2>
                <div className="space-y-3">
                  {alerts.map(alert => (
                    <div key={alert.id} className="flex items-start gap-3 p-3 bg-slate-50 rounded-xl">
                      <span className="text-lg">
                        {alert.alertType === 'FROST_WARNING' ? '❄️'
                          : alert.alertType === 'HEAVY_RAIN' ? '🌧️'
                          : alert.alertType.includes('DROUGHT') ? '☀️'
                          : '⚠️'}
                      </span>
                      <div>
                        <p className="text-sm font-semibold text-slate-700">
                          {alert.alertType.replace(/_/g, ' ')}
                        </p>
                        <p className="text-xs text-slate-500">{alert.messageEn}</p>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
}

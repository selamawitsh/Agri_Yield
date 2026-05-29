'use client';
import { useEffect, useState } from 'react';
import { getWeatherCurrent, getDroughtStatus, getWeatherRisk, getWeatherAlerts, getWeatherForecast,
  type WeatherReading, type DroughtStatus, type WeatherRisk, type WeatherAlert } from '@/lib/api';

interface WeatherPanelProps {
  farmId: string;
  compact?: boolean;
}

export default function WeatherPanel({ farmId, compact = false }: WeatherPanelProps) {
  const [current, setCurrent] = useState<WeatherReading | null>(null);
  const [drought, setDrought] = useState<DroughtStatus | null>(null);
  const [risk, setRisk] = useState<WeatherRisk | null>(null);
  const [alerts, setAlerts] = useState<WeatherAlert[]>([]);
  const [forecast, setForecast] = useState<WeatherReading[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!farmId) return;
    loadWeather();
  }, [farmId]);

  async function loadWeather() {
    setLoading(true);
    try {
      const [curRes, droughtRes, riskRes, alertRes, forecastRes] = await Promise.allSettled([
        getWeatherCurrent(farmId),
        getDroughtStatus(farmId),
        getWeatherRisk(farmId),
        getWeatherAlerts(farmId),
        getWeatherForecast(farmId),
      ]);

      if (curRes.status === 'fulfilled') setCurrent(curRes.value.data);
      if (droughtRes.status === 'fulfilled') setDrought(droughtRes.value.data);
      if (riskRes.status === 'fulfilled') setRisk(riskRes.value.data);
      if (alertRes.status === 'fulfilled') setAlerts(alertRes.value.data);
      if (forecastRes.status === 'fulfilled') setForecast(forecastRes.value.data);
    } catch { /* silent */ }
    finally { setLoading(false); }
  }

  if (loading) return (
    <div className="bg-white rounded-2xl border border-slate-100 p-6">
      <div className="animate-pulse space-y-3">
        <div className="h-4 bg-slate-200 rounded w-1/3" />
        <div className="h-8 bg-slate-200 rounded" />
      </div>
    </div>
  );

  const riskColor = {
    LOW: 'text-green-700 bg-green-50 border-green-200',
    MEDIUM: 'text-amber-700 bg-amber-50 border-amber-200',
    HIGH: 'text-orange-700 bg-orange-50 border-orange-200',
    CRITICAL: 'text-red-700 bg-red-50 border-red-200',
  };

  const severityColor: Record<string, string> = {
    LOW: 'bg-green-100 text-green-700',
    MEDIUM: 'bg-amber-100 text-amber-700',
    HIGH: 'bg-orange-100 text-orange-700',
    CRITICAL: 'bg-red-100 text-red-700',
  };

  const alertIcon: Record<string, string> = {
    FROST_WARNING: '❄️',
    HEAVY_RAIN: '🌧️',
    DROUGHT_WARNING: '☀️',
    DROUGHT_TRIGGER: '🚨',
    HEATWAVE: '🌡️',
  };

  if (compact) {
    return (
      <div className="flex items-center gap-3 flex-wrap">
        {current && (
          <span className="text-sm font-semibold text-slate-700">
            🌡️ {current.temperatureC.toFixed(1)}°C
          </span>
        )}
        {current && (
          <span className="text-sm font-semibold text-blue-600">
            💧 {current.rainfallMm.toFixed(1)}mm
          </span>
        )}
        {risk && (
          <span className={`text-xs font-bold px-2 py-0.5 rounded-full border ${riskColor[risk.riskLevel]}`}>
            {risk.riskLevel} RISK
          </span>
        )}
        {drought?.isTriggered && (
          <span className="text-xs font-bold px-2 py-0.5 rounded-full bg-red-100 text-red-700 border border-red-200">
            🚨 DROUGHT
          </span>
        )}
        {alerts.length > 0 && (
          <span className="text-xs text-orange-600 font-bold">{alerts.length} alert{alerts.length > 1 ? 's' : ''}</span>
        )}
      </div>
    );
  }

  return (
    <div className="space-y-4">
      {/* Current weather + risk */}
      <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
        <div className="bg-white rounded-xl border border-slate-100 p-4">
          <p className="text-xs text-slate-400 font-medium uppercase tracking-wide mb-1">Temperature</p>
          <p className="text-2xl font-black text-slate-800">
            {current ? `${current.temperatureC.toFixed(1)}°C` : '—'}
          </p>
        </div>
        <div className="bg-white rounded-xl border border-slate-100 p-4">
          <p className="text-xs text-slate-400 font-medium uppercase tracking-wide mb-1">Rainfall</p>
          <p className="text-2xl font-black text-blue-600">
            {current ? `${current.rainfallMm.toFixed(1)}mm` : '—'}
          </p>
        </div>
        <div className="bg-white rounded-xl border border-slate-100 p-4">
          <p className="text-xs text-slate-400 font-medium uppercase tracking-wide mb-1">Dry Days</p>
          <p className="text-2xl font-black text-orange-600">
            {drought ? drought.consecutiveDryDays : '—'}
          </p>
        </div>
        <div className="bg-white rounded-xl border border-slate-100 p-4">
          <p className="text-xs text-slate-400 font-medium uppercase tracking-wide mb-1">Risk Level</p>
          {risk ? (
            <span className={`inline-block text-sm font-bold px-2 py-1 rounded-lg border ${riskColor[risk.riskLevel]}`}>
              {risk.riskLevel}
            </span>
          ) : <p className="text-2xl font-black text-slate-400">—</p>}
        </div>
      </div>

      {/* Drought status */}
      {drought && (
        <div className={`rounded-xl border p-4 ${drought.isTriggered
          ? 'bg-red-50 border-red-200'
          : drought.consecutiveDryDays > 15
          ? 'bg-amber-50 border-amber-200'
          : 'bg-green-50 border-green-200'}`}>
          <div className="flex items-center gap-3">
            <span className="text-2xl">
              {drought.isTriggered ? '🚨' : drought.consecutiveDryDays > 15 ? '⚠️' : '✅'}
            </span>
            <div>
              <p className={`font-bold text-sm ${drought.isTriggered ? 'text-red-700' : drought.consecutiveDryDays > 15 ? 'text-amber-700' : 'text-green-700'}`}>
                {drought.isTriggered ? 'Drought Triggered — Parametric Insurance Active'
                  : drought.consecutiveDryDays > 15 ? `Drought Warning — ${drought.consecutiveDryDays} dry days`
                  : 'Normal Conditions'}
              </p>
              <div className="mt-1 bg-white/50 rounded-full h-2 w-48">
                <div className="h-2 rounded-full bg-current opacity-60 transition-all"
                  style={{ width: `${Math.min(100, (drought.consecutiveDryDays / drought.droughtThresholdDays) * 100)}%` }} />
              </div>
              <p className="text-xs mt-0.5 opacity-70">
                {drought.consecutiveDryDays} / {drought.droughtThresholdDays} days threshold
              </p>
            </div>
          </div>
        </div>
      )}

      {/* Alerts */}
      {alerts.length > 0 && (
        <div className="space-y-2">
          <p className="text-xs font-bold text-slate-500 uppercase tracking-wide">Active Alerts</p>
          {alerts.slice(0, 3).map(alert => (
            <div key={alert.id} className="bg-white rounded-xl border border-slate-100 p-3 flex items-start gap-3">
              <span className="text-lg">{alertIcon[alert.alertType] || '⚠️'}</span>
              <div>
                <div className="flex items-center gap-2 mb-1">
                  <span className={`text-[10px] font-bold px-1.5 py-0.5 rounded ${severityColor[alert.severity] || 'bg-slate-100 text-slate-700'}`}>
                    {alert.severity}
                  </span>
                  <span className="text-xs font-semibold text-slate-600">
                    {alert.alertType.replace(/_/g, ' ')}
                  </span>
                </div>
                <p className="text-xs text-slate-600">{alert.messageEn}</p>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* 7-day forecast */}
      {forecast.length > 0 && (
        <div>
          <p className="text-xs font-bold text-slate-500 uppercase tracking-wide mb-2">7-Day Forecast</p>
          <div className="grid grid-cols-7 gap-1">
            {forecast.slice(0, 7).map((f, i) => (
              <div key={i} className="bg-white rounded-xl border border-slate-100 p-2 text-center">
                <p className="text-[10px] text-slate-400 font-medium">Day {f.forecastHorizonDays ?? i + 1}</p>
                <p className="text-sm font-bold text-slate-700 mt-1">{f.temperatureC.toFixed(0)}°</p>
                <p className="text-[10px] text-blue-500">{f.rainfallMm.toFixed(0)}mm</p>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}

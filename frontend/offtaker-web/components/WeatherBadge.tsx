'use client';
import { useEffect, useState } from 'react';
import { getWeatherRisk, getDroughtStatus, getWeatherAlerts } from '@/lib/api';

interface WeatherBadgeProps {
  farmId: string;
}

export default function WeatherBadge({ farmId }: WeatherBadgeProps) {
  const [riskLevel, setRiskLevel] = useState<string | null>(null);
  const [droughtTriggered, setDroughtTriggered] = useState(false);
  const [alertCount, setAlertCount] = useState(0);

  useEffect(() => {
    if (!farmId) return;
    Promise.allSettled([
      getWeatherRisk(farmId),
      getDroughtStatus(farmId),
      getWeatherAlerts(farmId),
    ]).then(([riskRes, droughtRes, alertRes]) => {
      if (riskRes.status === 'fulfilled') setRiskLevel(riskRes.value.data.riskLevel);
      if (droughtRes.status === 'fulfilled') setDroughtTriggered(droughtRes.value.data.isTriggered);
      if (alertRes.status === 'fulfilled') setAlertCount(alertRes.value.data.length);
    });
  }, [farmId]);

  if (!riskLevel) return null;

  const colors: Record<string, string> = {
    LOW: 'bg-green-100 text-green-700',
    MEDIUM: 'bg-amber-100 text-amber-700',
    HIGH: 'bg-orange-100 text-orange-700',
    CRITICAL: 'bg-red-100 text-red-700',
  };

  return (
    <div className="flex items-center gap-1.5 flex-wrap">
      <span className={`text-[10px] font-bold px-2 py-0.5 rounded-full ${colors[riskLevel] || 'bg-slate-100 text-slate-600'}`}>
        🌤️ {riskLevel} RISK
      </span>
      {droughtTriggered && (
        <span className="text-[10px] font-bold px-2 py-0.5 rounded-full bg-red-100 text-red-700">
          🚨 DROUGHT
        </span>
      )}
      {alertCount > 0 && (
        <span className="text-[10px] font-bold px-2 py-0.5 rounded-full bg-orange-100 text-orange-700">
          ⚠️ {alertCount} alert{alertCount > 1 ? 's' : ''}
        </span>
      )}
    </div>
  );
}

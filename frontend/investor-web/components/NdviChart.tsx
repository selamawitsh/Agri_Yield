'use client';

import {
  LineChart, Line, XAxis, YAxis, CartesianGrid,
  Tooltip, ResponsiveContainer, ReferenceLine, Area, AreaChart
} from 'recharts';

interface NdviPoint { date: string; ndviValue: number; }

interface Props {
  data: NdviPoint[];
  height?: number;
  showGrid?: boolean;
  compact?: boolean;
}

const healthColor = (v: number) => {
  if (v >= 0.6) return '#16a34a';
  if (v >= 0.4) return '#65a30d';
  if (v >= 0.2) return '#ca8a04';
  return '#dc2626';
};

export default function NdviChart({ data, height = 200, showGrid = true, compact = false }: Props) {
  if (!data || data.length === 0) {
    return (
      <div className="flex items-center justify-center bg-gray-50 rounded-xl text-gray-400 text-sm" style={{ height }}>
        No NDVI data available
      </div>
    );
  }

  const latest = data[data.length - 1]?.ndviValue ?? 0;
  const color = healthColor(latest);

  const formatted = data.map(d => ({
    ...d,
    label: new Date(d.date).toLocaleDateString('en-ET', { month: 'short', day: 'numeric' }),
  }));

  return (
    <ResponsiveContainer width="100%" height={height}>
      <AreaChart data={formatted} margin={{ top: 5, right: 10, left: compact ? -20 : 0, bottom: 0 }}>
        <defs>
          <linearGradient id="ndviGrad" x1="0" y1="0" x2="0" y2="1">
            <stop offset="5%" stopColor={color} stopOpacity={0.25} />
            <stop offset="95%" stopColor={color} stopOpacity={0.02} />
          </linearGradient>
        </defs>
        {showGrid && <CartesianGrid strokeDasharray="3 3" stroke="#f0f0f0" />}
        <XAxis dataKey="label" tick={{ fontSize: 11, fill: '#9ca3af' }} tickLine={false} axisLine={false} interval="preserveStartEnd" />
        <YAxis domain={[0, 1]} tick={{ fontSize: 11, fill: '#9ca3af' }} tickLine={false} axisLine={false} tickFormatter={v => v.toFixed(1)} />
        <Tooltip
          contentStyle={{ background: '#fff', border: '1px solid #e5e7eb', borderRadius: 12, fontSize: 12 }}
          formatter={(v: number) => [v.toFixed(3), 'NDVI']}
          labelStyle={{ color: '#374151', fontWeight: 600 }}
        />
        <ReferenceLine y={0.6} stroke="#16a34a" strokeDasharray="4 4" strokeOpacity={0.4} label={{ value: 'Good', fill: '#16a34a', fontSize: 10 }} />
        <ReferenceLine y={0.2} stroke="#dc2626" strokeDasharray="4 4" strokeOpacity={0.4} label={{ value: 'Poor', fill: '#dc2626', fontSize: 10 }} />
        <Area type="monotone" dataKey="ndviValue" stroke={color} strokeWidth={2.5} fill="url(#ndviGrad)" dot={false} activeDot={{ r: 5, fill: color }} />
      </AreaChart>
    </ResponsiveContainer>
  );
}

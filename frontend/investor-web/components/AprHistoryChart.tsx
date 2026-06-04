'use client';

import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, ReferenceLine } from 'recharts';

interface AprPoint { date: string; apr: number; }
interface Props { baseApr: number; currentApr: number; }

// Generate synthetic APR history from base → current for display
function generateAprHistory(base: number, current: number): AprPoint[] {
  const points: AprPoint[] = [];
  const now = new Date();
  for (let i = 8; i >= 0; i--) {
    const d = new Date(now);
    d.setDate(d.getDate() - i * 7);
    const progress = (8 - i) / 8;
    const noise = (Math.random() - 0.5) * 0.4;
    const apr = +(base + (current - base) * progress + noise).toFixed(2);
    points.push({ date: d.toLocaleDateString('en-ET', { month: 'short', day: 'numeric' }), apr });
  }
  return points;
}

export default function AprHistoryChart({ baseApr, currentApr }: Props) {
  const data = generateAprHistory(baseApr, currentApr);
  return (
    <ResponsiveContainer width="100%" height={160}>
      <LineChart data={data} margin={{ top: 5, right: 10, left: -20, bottom: 0 }}>
        <CartesianGrid strokeDasharray="3 3" stroke="#f0f0f0" />
        <XAxis dataKey="date" tick={{ fontSize: 10, fill: '#9ca3af' }} tickLine={false} axisLine={false} interval={2} />
        <YAxis tick={{ fontSize: 10, fill: '#9ca3af' }} tickLine={false} axisLine={false} tickFormatter={v => `${v}%`} />
        <Tooltip formatter={(v: number) => [`${v}%`, 'APR']} contentStyle={{ borderRadius: 8, fontSize: 12 }} />
        <ReferenceLine y={baseApr} stroke="#9ca3af" strokeDasharray="4 4" label={{ value: 'Base', fill: '#9ca3af', fontSize: 10 }} />
        <Line type="monotone" dataKey="apr" stroke="#16a34a" strokeWidth={2} dot={false} activeDot={{ r: 4 }} />
      </LineChart>
    </ResponsiveContainer>
  );
}

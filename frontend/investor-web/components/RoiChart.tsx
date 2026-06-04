'use client';

import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Legend } from 'recharts';

interface Props { averageApr: number; }

export default function RoiChart({ averageApr }: Props) {
  const inflation = 22; // Ethiopia avg inflation reference
  const data = Array.from({ length: 5 }, (_, i) => {
    const year = new Date().getFullYear() - 4 + i;
    return {
      year: String(year),
      'Agri-Yield APR': +(averageApr + (Math.random() - 0.5) * 2).toFixed(1),
      'Inflation': +(inflation + (Math.random() - 0.5) * 3).toFixed(1),
    };
  });

  return (
    <ResponsiveContainer width="100%" height={180}>
      <LineChart data={data} margin={{ top: 5, right: 10, left: -20, bottom: 0 }}>
        <CartesianGrid strokeDasharray="3 3" stroke="#f0f0f0" />
        <XAxis dataKey="year" tick={{ fontSize: 11, fill: '#9ca3af' }} tickLine={false} axisLine={false} />
        <YAxis tick={{ fontSize: 11, fill: '#9ca3af' }} tickLine={false} axisLine={false} tickFormatter={v => `${v}%`} />
        <Tooltip formatter={(v: number) => [`${v}%`]} contentStyle={{ borderRadius: 10, fontSize: 12 }} />
        <Legend wrapperStyle={{ fontSize: 12 }} />
        <Line type="monotone" dataKey="Agri-Yield APR" stroke="#16a34a" strokeWidth={2.5} dot={{ r: 4 }} />
        <Line type="monotone" dataKey="Inflation" stroke="#f97316" strokeWidth={2} strokeDasharray="5 5" dot={false} />
      </LineChart>
    </ResponsiveContainer>
  );
}

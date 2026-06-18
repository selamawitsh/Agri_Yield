'use client';

import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, ReferenceLine } from 'recharts';
import type { NdviHistoryPoint } from '@/lib/types';

interface Props {
  data: NdviHistoryPoint[];
  height?: number;
}

const NDVI_BANDS = [
  { from: 0.6, label: 'Excellent', color: '#16a34a' },
  { from: 0.4, label: 'Good',      color: '#65a30d' },
  { from: 0.2, label: 'Moderate',  color: '#ca8a04' },
  { from: -1,  label: 'Poor',      color: '#dc2626' },
];

function bandFor(value: number) {
  return NDVI_BANDS.find(b => value >= b.from) || NDVI_BANDS[NDVI_BANDS.length - 1];
}

export default function NdviHistoryChart({ data, height = 240 }: Props) {
  if (!data || data.length === 0) {
    return (
      <div className="bg-gray-50 rounded-2xl p-8 text-center text-gray-400 text-sm">
        <p className="text-2xl mb-2">📡</p>
        <p>No NDVI history yet — satellite monitoring runs every 5 days.</p>
      </div>
    );
  }

  // Sort chronologically and dedupe same-day readings by averaging
  const sorted = [...data].sort((a, b) => a.date.localeCompare(b.date));
  const grouped = new Map<string, { sum: number; count: number }>();
  sorted.forEach(p => {
    const existing = grouped.get(p.date) || { sum: 0, count: 0 };
    grouped.set(p.date, { sum: existing.sum + p.ndviValue, count: existing.count + 1 });
  });
  const chartData = Array.from(grouped.entries()).map(([date, { sum, count }]) => ({
    date: new Date(date).toLocaleDateString('en-US', { month: 'short', day: 'numeric' }),
    ndvi: Number((sum / count).toFixed(3)),
  }));

  const latest = chartData[chartData.length - 1];
  const latestBand = bandFor(latest.ndvi);

  return (
    <div>
      <div className="flex items-center justify-between mb-3">
        <div>
          <p className="text-xs text-gray-400 uppercase tracking-wide font-semibold">Latest reading</p>
          <p className="text-2xl font-black" style={{ color: latestBand.color }}>
            {latest.ndvi.toFixed(3)}
            <span className="text-sm font-semibold ml-2">{latestBand.label}</span>
          </p>
        </div>
        <div className="text-right text-xs text-gray-400">
          <p>{chartData.length} readings</p>
          <p>over {Math.ceil((new Date(sorted[sorted.length - 1].date).getTime() -
                               new Date(sorted[0].date).getTime()) / 86400000)} days</p>
        </div>
      </div>

      <ResponsiveContainer width="100%" height={height}>
        <LineChart data={chartData} margin={{ top: 5, right: 10, left: -20, bottom: 0 }}>
          <CartesianGrid strokeDasharray="3 3" stroke="#f1f5f9" />
          <XAxis dataKey="date" tick={{ fontSize: 11, fill: '#94a3b8' }} />
          <YAxis domain={[-0.2, 1]} tick={{ fontSize: 11, fill: '#94a3b8' }} />
          <ReferenceLine y={0.6} stroke="#16a34a" strokeDasharray="2 2" strokeOpacity={0.4} />
          <ReferenceLine y={0.4} stroke="#65a30d" strokeDasharray="2 2" strokeOpacity={0.4} />
          <ReferenceLine y={0.2} stroke="#ca8a04" strokeDasharray="2 2" strokeOpacity={0.4} />
          <Tooltip
            contentStyle={{ borderRadius: 12, border: '1px solid #e2e8f0', fontSize: 12 }}
            formatter={(value: number) => [value.toFixed(3), 'NDVI']}
          />
          <Line type="monotone" dataKey="ndvi" stroke="#0f766e" strokeWidth={2.5}
            dot={{ r: 3, fill: '#0f766e' }} activeDot={{ r: 5 }} />
        </LineChart>
      </ResponsiveContainer>

      {/* Plain-language explanation — addressing "clear NDVI explanation" request */}
      <div className="mt-4 bg-teal-50 border border-teal-100 rounded-xl p-4 text-sm text-teal-900">
        <p className="font-semibold mb-1">📡 What is NDVI?</p>
        <p className="text-teal-800 leading-relaxed">
          NDVI (Normalized Difference Vegetation Index) measures crop health from satellite imagery,
          scored from -1.0 to 1.0. Healthy, dense vegetation reflects more near-infrared light, producing
          a higher score. <strong>Above 0.6 is excellent</strong> crop health, <strong>0.4–0.6 is good</strong>,
          <strong> 0.2–0.4 is moderate</strong> (often early growth or stress), and <strong>below 0.2 is poor</strong>
          (bare soil, severe stress, or pre/post-harvest field).
        </p>
      </div>
    </div>
  );
}

'use client';

interface Props {
  min: number;
  mean: number;
  max: number;
  confidencePct: number;
  cropType?: string;
}

export default function YieldPredictionBar({ min, mean, max, confidencePct, cropType }: Props) {
  const range = max - min || 1;
  const meanPos = ((mean - min) / range) * 100;

  return (
    <div className="bg-gray-50 rounded-2xl p-5 border border-gray-100">
      <div className="flex justify-between items-start mb-4">
        <div>
          <p className="text-xs font-bold text-gray-400 uppercase tracking-wide">Yield Prediction</p>
          {cropType && <p className="text-sm font-semibold text-gray-700 mt-0.5">{cropType}</p>}
        </div>
        <div className="bg-green-100 text-green-700 px-3 py-1 rounded-full text-xs font-bold">
          {confidencePct}% confidence
        </div>
      </div>

      {/* Range bar */}
      <div className="relative mb-4">
        <div className="w-full h-3 bg-gray-200 rounded-full overflow-hidden">
          <div className="h-full bg-gradient-to-r from-yellow-400 via-green-500 to-green-600 rounded-full"
            style={{ marginLeft: '0%', width: '100%' }} />
        </div>
        {/* Mean marker */}
        <div className="absolute top-0 h-3 w-1 bg-green-800 rounded-full shadow"
          style={{ left: `${meanPos}%`, transform: 'translateX(-50%)' }} />
        <div className="absolute -top-6 text-xs font-bold text-green-800"
          style={{ left: `${meanPos}%`, transform: 'translateX(-50%)' }}>
          {mean.toFixed(1)}
        </div>
      </div>

      <div className="flex justify-between text-xs text-gray-500 font-medium">
        <span>Min: <strong className="text-gray-700">{min.toFixed(1)} qt/ha</strong></span>
        <span>Mean: <strong className="text-green-700">{mean.toFixed(1)} qt/ha</strong></span>
        <span>Max: <strong className="text-gray-700">{max.toFixed(1)} qt/ha</strong></span>
      </div>
    </div>
  );
}

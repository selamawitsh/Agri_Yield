import { FraudRiskScore } from '@/lib/types';

const COMPONENTS = [
  { key: 'gpsAnomalyScore',        label: 'GPS Anomaly',       color: 'bg-orange-400' },
  { key: 'duplicateVoucherScore',   label: 'Duplicate Voucher', color: 'bg-red-500' },
  { key: 'exifMismatchScore',       label: 'EXIF Mismatch',     color: 'bg-amber-400' },
  { key: 'suspiciousActivityScore', label: 'Suspicious Activity', color: 'bg-purple-400' },
] as const;

export default function FraudRiskScoreBar({ score }: { score: FraudRiskScore }) {
  const maxScore = 100;
  const pct = Math.min((score.totalScore / maxScore) * 100, 100);

  const barColor =
    score.totalScore >= 90 ? 'bg-red-600' :
    score.totalScore >= 70 ? 'bg-orange-500' :
    score.totalScore >= 40 ? 'bg-amber-500' :
    'bg-emerald-500';

  return (
    <div className="space-y-4">
      {/* Total score */}
      <div>
        <div className="flex items-center justify-between mb-2">
          <span className="text-[10px] font-bold uppercase tracking-widest text-slate-400">
            Total Risk Score
          </span>
          <span className={`text-2xl font-black ${
            score.totalScore >= 90 ? 'text-red-600' :
            score.totalScore >= 70 ? 'text-orange-600' :
            score.totalScore >= 40 ? 'text-amber-600' :
            'text-emerald-700'
          }`}>
            {score.totalScore}<span className="text-sm font-medium text-slate-400">/100</span>
          </span>
        </div>
        <div className="w-full bg-slate-100 rounded-full h-2.5">
          <div
            className={`${barColor} h-2.5 rounded-full transition-all duration-500`}
            style={{ width: `${pct}%` }}
          />
        </div>
      </div>

      {/* Component breakdown */}
      <div className="space-y-2.5">
        {COMPONENTS.map((c) => {
          const val = score[c.key];
          const compPct = Math.min((val / 40) * 100, 100);
          return (
            <div key={c.key}>
              <div className="flex justify-between mb-1">
                <span className="text-xs text-slate-500">{c.label}</span>
                <span className="text-xs font-bold text-slate-700">{val}</span>
              </div>
              <div className="w-full bg-slate-100 rounded-full h-1.5">
                <div
                  className={`${c.color} h-1.5 rounded-full`}
                  style={{ width: `${compPct}%` }}
                />
              </div>
            </div>
          );
        })}
      </div>

      <p className="text-[10px] text-slate-400">
        Formula: GPS + Duplicate + EXIF + Suspicious · Last calculated:{' '}
        {score.calculatedAt
          ? new Date(score.calculatedAt).toLocaleString()
          : 'Never'}
      </p>
    </div>
  );
}

import { FraudSeverity } from '@/lib/types';

const CONFIG: Record<FraudSeverity, { label: string; classes: string; dot: string }> = {
  LOW:      { label: 'Low',      classes: 'bg-slate-100 text-slate-600 border-slate-200',       dot: 'bg-slate-400' },
  MEDIUM:   { label: 'Medium',   classes: 'bg-amber-50 text-amber-700 border-amber-200',         dot: 'bg-amber-500' },
  HIGH:     { label: 'High',     classes: 'bg-orange-50 text-orange-700 border-orange-200',      dot: 'bg-orange-500' },
  CRITICAL: { label: 'Critical', classes: 'bg-red-50 text-red-700 border-red-300',               dot: 'bg-red-600' },
};

export default function FraudSeverityBadge({
  severity,
  showDot = true,
}: {
  severity: FraudSeverity;
  showDot?: boolean;
}) {
  const cfg = CONFIG[severity] ?? CONFIG.LOW;
  return (
    <span className={`inline-flex items-center gap-1.5 px-2.5 py-1 rounded-xl border text-[11px] font-bold ${cfg.classes}`}>
      {showDot && <span className={`w-1.5 h-1.5 rounded-full ${cfg.dot}`} />}
      {cfg.label}
    </span>
  );
}

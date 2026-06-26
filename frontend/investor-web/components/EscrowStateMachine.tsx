'use client';

import { Wallet, Lock, Sprout, Wheat, CheckCircle2, XCircle } from 'lucide-react';

interface Props { status: string; }

const STEPS = [
  { key: 'PENDING',         label: 'Investment Placed',    desc: 'Your investment is being processed',         Icon: Wallet },
  { key: 'ESCROW_LOCKED',   label: 'Funds Secured',        desc: 'Funds locked in escrow account',             Icon: Lock },
  { key: 'ACTIVE',          label: 'Farm Growing',         desc: 'Farm is funded and actively growing',        Icon: Sprout },
  { key: 'COMPLETED',       label: 'Harvest Complete',     desc: 'Harvest confirmed by off-taker',             Icon: Wheat },
  { key: 'SETTLED',         label: 'Payout Transferred',   desc: 'Principal + return sent to your account',    Icon: CheckCircle2 },
];

const STATUS_ORDER: Record<string, number> = {
  'PENDING':       0,
  'ESCROW_LOCKED': 1,
  'ACTIVE':        2,
  'COMPLETED':     3,
  'SETTLED':       4,
};

const FAILED_STATUSES = ['CANCELLED', 'FAILED', 'FUNDING_FAILED', 'REFUNDED'];

export default function EscrowStateMachine({ status }: Props) {
  const failed    = FAILED_STATUSES.includes(status);
  const activeIdx = failed ? -1 : (STATUS_ORDER[status] ?? 0);
  const pct       = failed ? 0 : (activeIdx / (STEPS.length - 1)) * 88;

  return (
    <div className="relative">
      {/* Background track */}
      <div className="absolute top-6 left-6 right-6 h-0.5 bg-slate-100 z-0" />
      {/* Progress fill */}
      <div
        className="absolute top-6 left-6 h-0.5 bg-emerald-500 z-0 transition-all duration-700"
        style={{ width: `${pct}%` }}
      />

      <div className="relative z-10 flex justify-between">
        {STEPS.map((step, i) => {
          const done   = !failed && i < activeIdx;
          const active = !failed && i === activeIdx;
          const StepIcon = step.Icon;
          return (
            <div key={step.key} className="flex flex-col items-center gap-2 w-1/5">
              <div className={`w-12 h-12 rounded-full flex items-center justify-center shadow-sm transition-all duration-300 ${
                done   ? 'bg-emerald-500 text-white shadow-emerald-200' :
                active ? 'bg-white border-2 border-emerald-500 text-emerald-600 shadow-emerald-100 shadow-md scale-110' :
                failed ? 'bg-red-100 text-red-400' :
                         'bg-slate-100 text-slate-400'
              }`}>
                {done
                  ? <CheckCircle2 className="w-5 h-5 text-white" />
                  : <StepIcon className="w-5 h-5" />
                }
              </div>
              <div className="text-center px-1">
                <p className={`text-[11px] font-bold leading-tight ${
                  active ? 'text-emerald-700' :
                  done   ? 'text-slate-700'   :
                  failed ? 'text-red-400'     :
                           'text-slate-400'
                }`}>
                  {step.label}
                </p>
                {active && (
                  <p className="text-[10px] text-slate-400 mt-0.5 hidden md:block leading-snug">
                    {step.desc}
                  </p>
                )}
              </div>
            </div>
          );
        })}
      </div>

      {failed && (
        <div className="mt-6 bg-red-50 border border-red-200 rounded-xl p-3 flex items-center gap-2">
          <XCircle className="w-4 h-4 text-red-500 shrink-0" />
          <p className="text-red-600 font-semibold text-sm">
            Investment {status.toLowerCase().replace(/_/g, ' ')} — funds will be refunded
          </p>
        </div>
      )}

      {!failed && (
        <div className="mt-6 flex justify-between text-[10px] text-slate-400 font-medium px-1">
          <span>Started</span>
          <span className="text-emerald-600 font-bold">
            Step {activeIdx + 1} of {STEPS.length}
          </span>
          <span>Complete</span>
        </div>
      )}
    </div>
  );
}

'use client';

interface Props { status: string; }

const STEPS = [
  { key: 'PENDING',       label: 'Investment Placed',    icon: '💳', desc: 'Your investment is being processed' },
  { key: 'ESCROW_LOCKED', label: 'Escrow Locked',        icon: '🔒', desc: 'Funds secured in escrow account' },
  { key: 'ACTIVE',        label: 'Farm Growing',         icon: '🌱', desc: 'Farm is funded and growing' },
  { key: 'COMPLETED',     label: 'Harvest Complete',     icon: '🌾', desc: 'Harvest confirmed by off-taker' },
  { key: 'SETTLED',       label: 'Payout Transferred',   icon: '💰', desc: 'Principal + return sent to your wallet' },
];

const FAILED = ['CANCELLED', 'FAILED', 'FUNDING_FAILED'];

export default function EscrowStateMachine({ status }: Props) {
  const failed = FAILED.includes(status);
  const currentIdx = failed ? -1 : STEPS.findIndex(s => s.key === status);
  const activeIdx = currentIdx === -1 ? (status === 'COMPLETED' ? 3 : 0) : currentIdx;

  return (
    <div className="relative">
      {/* Track line */}
      <div className="absolute top-6 left-6 right-6 h-0.5 bg-gray-200 z-0" />
      <div
        className="absolute top-6 left-6 h-0.5 bg-green-500 z-0 transition-all duration-700"
        style={{ width: failed ? '0%' : `${(activeIdx / (STEPS.length - 1)) * 88}%` }}
      />

      <div className="relative z-10 flex justify-between">
        {STEPS.map((step, i) => {
          const done = i < activeIdx || status === 'COMPLETED';
          const active = i === activeIdx && !failed;
          return (
            <div key={step.key} className="flex flex-col items-center gap-2 w-1/5">
              <div className={`w-12 h-12 rounded-full flex items-center justify-center text-xl shadow-sm transition-all ${
                done ? 'bg-green-500 text-white shadow-green-200' :
                active ? 'bg-white border-2 border-green-500 shadow-green-100 shadow-md' :
                failed ? 'bg-red-100 text-red-400' :
                'bg-gray-100 text-gray-400'
              }`}>
                {done ? '✓' : step.icon}
              </div>
              <div className="text-center">
                <p className={`text-xs font-bold ${active ? 'text-green-700' : done ? 'text-gray-700' : 'text-gray-400'}`}>
                  {step.label}
                </p>
                {active && <p className="text-xs text-gray-400 mt-0.5 hidden md:block">{step.desc}</p>}
              </div>
            </div>
          );
        })}
      </div>

      {failed && (
        <div className="mt-6 bg-red-50 border border-red-200 rounded-xl p-3 text-center">
          <p className="text-red-600 font-semibold text-sm">Investment {status.toLowerCase().replace('_', ' ')} — Funds will be refunded</p>
        </div>
      )}
    </div>
  );
}

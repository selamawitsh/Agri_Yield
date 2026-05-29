'use client';
import { Voucher } from '@/lib/types';
import VoucherCategoryBadge from './VoucherCategoryBadge';

export default function VoucherSequenceBar({ vouchers }: { vouchers: Voucher[] }) {
  const sorted = [...vouchers].sort((a, b) => a.sequenceOrder - b.sequenceOrder);

  return (
    <div className="flex items-center gap-0 overflow-x-auto pb-1">
      {sorted.map((v, i) => {
        const isDone = v.status === 'REDEEMED';
        const isActive = v.status === 'ACTIVE';
        const isLocked = v.status === 'GENERATED';

        return (
          <div key={v.id} className="flex items-center">
            <div className="flex flex-col items-center gap-1 min-w-[80px]">
              <div
                className={`w-8 h-8 rounded-full flex items-center justify-center text-sm font-bold border-2 transition-all ${
                  isDone
                    ? 'bg-green-600 border-green-600 text-white'
                    : isActive
                    ? 'bg-amber-500 border-amber-500 text-white'
                    : 'bg-white border-gray-300 text-gray-400'
                }`}
              >
                {isDone ? '✓' : isLocked ? '🔒' : v.sequenceOrder}
              </div>
              <span className="text-[10px] text-gray-500 text-center leading-tight">
                {v.productCategory}
              </span>
              <span className="text-[10px] font-semibold text-gray-700">
                {v.amountEtb.toLocaleString()} ETB
              </span>
            </div>
            {i < sorted.length - 1 && (
              <div
                className={`h-0.5 w-8 mx-0.5 rounded ${
                  isDone ? 'bg-green-500' : 'bg-gray-200'
                }`}
              />
            )}
          </div>
        );
      })}
    </div>
  );
}

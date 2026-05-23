interface FundingProgressProps {
    funded: number;
    total: number;
    pct: number;
}

export default function FundingProgress({ funded, total, pct }: FundingProgressProps) {
    return (
        <div>
            <div className="flex justify-between text-sm text-gray-600 mb-1">
                <span>{pct.toFixed(1)}% funded</span>
                <span>{funded.toLocaleString()} / {total.toLocaleString()} ETB</span>
            </div>
            <div className="w-full bg-gray-200 rounded-full h-2">
                <div
                    className="bg-green-500 h-2 rounded-full transition-all"
                    style={{ width: `${Math.min(pct, 100)}%` }}
                />
            </div>
        </div>
    );
}
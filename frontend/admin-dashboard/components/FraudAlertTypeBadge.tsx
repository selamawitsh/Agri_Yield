import { FraudAlertType } from '@/lib/types';

const LABELS: Record<FraudAlertType, { label: string; icon: string }> = {
  DUPLICATE_VOUCHER_REDEMPTION: { label: 'Duplicate Scan',     icon: '' },
  INVALID_QR_SIGNATURE:         { label: 'Invalid Signature',  icon: '' },
  GPS_MISMATCH:                 { label: 'GPS Mismatch',       icon: '' },
  EXIF_METADATA_MISMATCH:       { label: 'EXIF Mismatch',      icon: '' },
  SUSPICIOUS_GPS_MOVEMENT:      { label: 'Suspicious GPS',     icon: '' },
  MERCHANT_INELIGIBLE:          { label: 'Merchant Ineligible',icon: '' },
  HIGH_FRAUD_SCORE:             { label: 'High Risk Score',    icon: '' },
  SUSPICIOUS_ACCOUNT:           { label: 'Suspicious Account', icon: '' },
};

export default function FraudAlertTypeBadge({ type }: { type: FraudAlertType }) {
  const cfg = LABELS[type] ?? { label: type, icon: '' };
  return (
    <span className="inline-flex items-center gap-1.5 text-xs font-semibold text-slate-700">
      <span>{cfg.icon}</span>
      {cfg.label}
    </span>
  );
}

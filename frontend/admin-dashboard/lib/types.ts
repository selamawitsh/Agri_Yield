export interface User {
  id: string;
  phone: string;
  email: string | null;
  faydaId: string;
  role: 'FARMER' | 'INVESTOR' | 'MERCHANT' | 'OFF_TAKER' | 'ADMIN';
  kycStatus: 'PENDING' | 'VERIFIED' | 'REJECTED';
  accountStatus: 'ACTIVE' | 'SUSPENDED' | 'PENDING_VERIFICATION';
  preferredLanguage: string;
  riskTolerance?: string;
  investmentGoal?: string;
  agriScore?: number;
  createdAt: string;
  updatedAt?: string;
  faydaVerifiedAt?: string;
}

export interface UserStats {
  totalUsers: number;
  totalFarmers: number;
  totalInvestors: number;
  totalMerchants: number;
  totalOffTakers: number;
  pendingKyc: number;
  verifiedKyc: number;
  rejectedKyc: number;
  activeUsers: number;
  suspendedUsers: number;
}

export interface PagedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
  first: boolean;
  last: boolean;
}

export interface ApiResponse<T> {
  success: boolean;
  message?: string;
  data: T;
}

// ── Fraud Types (FR-07, FR-08) ────────────────────────────────────────────────

export type FraudSeverity = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
export type FraudAlertType =
  | 'DUPLICATE_VOUCHER_REDEMPTION'
  | 'INVALID_QR_SIGNATURE'
  | 'GPS_MISMATCH'
  | 'EXIF_METADATA_MISMATCH'
  | 'SUSPICIOUS_GPS_MOVEMENT'
  | 'MERCHANT_INELIGIBLE'
  | 'HIGH_FRAUD_SCORE'
  | 'SUSPICIOUS_ACCOUNT';

export type FraudEntityType = 'FARMER' | 'MERCHANT' | 'INVESTOR';

export interface FraudAlert {
  id: string;
  alertType: FraudAlertType;
  entityType: FraudEntityType;
  entityId: string;
  severity: FraudSeverity;
  description: string;
  evidence: string | null;
  resolved: boolean;
  resolvedByAdminId: string | null;
  resolutionNotes: string | null;
  resolvedAt: string | null;
  createdAt: string;
}

export interface FraudRiskScore {
  entityId: string;
  entityType: FraudEntityType;
  gpsAnomalyScore: number;
  duplicateVoucherScore: number;
  exifMismatchScore: number;
  suspiciousActivityScore: number;
  totalScore: number;
  severity: FraudSeverity;
  calculatedAt: string;
}

export interface FraudStats {
  totalAlerts: number;
  unresolvedAlerts: number;
  criticalAlerts: number;
  highAlerts: number;
  resolvedToday: number;
}

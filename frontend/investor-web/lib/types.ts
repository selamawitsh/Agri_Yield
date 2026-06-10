// ── User / Auth ───────────────────────────────────────────────────────────────

export interface UserProfile {
  id: string;
  phone: string;
  email?: string;
  faydaId: string;
  role: string;
  kycStatus: string;
  accountStatus: string;
  preferredLanguage: string;
  riskTolerance?: string;
  investmentGoal?: string;
  agriScore?: number;
  createdAt: string;
  bankAccounts: BankAccount[];
  defaultBankAccount?: BankAccount;
}

export interface BankAccount {
  id: string;
  accountType: string;
  accountNumber: string;
  accountHolderName?: string;
  isVerified: boolean;
  isDefault: boolean;
  verifiedAt?: string;
  createdAt: string;
}

export interface AuthTokens {
  accessToken: string;
  refreshToken: string;
  expiresIn: number;
}

// alias used by dashboard
export type User = UserProfile;

// ── Voucher types (SRS §3.5) ──────────────────────────────────────────────────

export type VoucherStatus =
  | 'GENERATED' | 'ACTIVE' | 'REDEEMED'
  | 'EXPIRED'   | 'CANCELLED' | 'REJECTED';

export type ProductCategory =
  | 'SEED' | 'FERTILIZER' | 'PESTICIDE' | 'TOOL' | 'OTHER';

export interface Voucher {
  id: string;
  farmId: string;
  cropCycleId: string;
  inputNeedItemId: string;
  amountEtb: number;
  productCategory: ProductCategory;
  productDescription: string;
  sequenceOrder: number;
  status: VoucherStatus;
  alphanumericCode: string;
  validUntil: string;
  redeemedAt: string | null;
  redeemedMerchantId: string | null;
  createdAt: string;
}

export interface VoucherRedemptionRecord {
  id: string;
  voucherId: string;
  merchantId: string;
  merchantGpsLat: number;
  merchantGpsLng: number;
  productsDispensed: DispensedProduct[];
  paymentReference: string;
  scanTimestamp: string;
}

export interface DispensedProduct {
  productName: string;
  quantity: string;
  unit: string;
  lotNumber: string;
}

export interface VoucherSummary {
  totalVouchers: number;
  activeCount: number;
  redeemedCount: number;
  generatedCount: number;
  expiredCount: number;
  totalValueEtb: number;
  redeemedValueEtb: number;
  pendingValueEtb: number;
}

export interface FarmVoucherTimeline {
  farmId: string;
  farmName: string;
  cropType: string;
  region: string;
  seasonName: string;
  vouchers: Voucher[];
  summary: VoucherSummary;
}

// ── Investment Service types (SRS §4.1) ───────────────────────────────────────

export type ListingStatus =
  | 'OPEN' | 'PARTIALLY_FUNDED' | 'FULLY_FUNDED'
  | 'FUNDING_FAILED' | 'ACTIVE' | 'COMPLETED' | 'CANCELLED';

export type InvestmentStatus =
  | 'PENDING' | 'ESCROW_LOCKED' | 'ACTIVE'
  | 'COMPLETED' | 'REFUNDED' | 'CANCELLED' | 'FAILED';

export interface FarmListing {
  id: string;
  farmId: string;
  farmerId: string;
  inputNeedId: string;
  cropCycleId: string;
  cropType: string;
  region: string;
  kebeleCode: string;
  seasonName: string;
  totalAmountEtb: number;
  fundedAmountEtb: number;
  fundingPct: number;
  currentApr: number;
  baseApr: number;
  agriScore: number;
  status: ListingStatus;
  // SRS §4.1.1: listing_expires_at (renamed from funding_deadline)
  listingExpiresAt: string | null;
  /** @deprecated use listingExpiresAt — kept for backward compat during migration */
  fundingDeadline?: string | null;
  fullyFundedAt: string | null;
  // SRS: satellite_verified flag from farm-service
  satelliteVerified?: boolean;
  createdAt: string;
}

export interface Investment {
  id: string;
  investorId: string;
  farmId: string;
  farmerId: string;
  inputNeedId: string;
  cropCycleId: string;
  amountEtb: number;
  // SRS §4.1.1: this investor's share of total listing (e.g. 0.41667)
  investmentPct: number | null;
  status: InvestmentStatus;
  cropType: string;
  region: string;
  seasonName: string;
  expectedReturnPct: number;
  actualReturnPct: number | null;
  notes: string | null;
  cancelledReason: string | null;
  // SRS §4.1.1: payout tracking
  lockedAt: string | null;
  payoutAmountEtb: number | null;
  payoutAt: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface PayoutRecord {
  id: string;
  investmentId: string;
  investorId: string;
  farmId: string;
  listingId: string;
  principalEtb: number;
  returnEtb: number;
  totalEtb: number;
  actualApr: number;
  payoutReason: string | null;
  paidAt: string;
}

export interface PortfolioStats {
  totalInvested: number;
  totalReturned: number;
  activeInvestments: number;
  completedInvestments: number;
  cancelledInvestments: number;
  averageApr: number;
}

// ── Geospatial / Weather types (SRS §3.6, §4.4) ───────────────────────────────

export interface NdviReading {
  date: string;
  ndviValue: number;
  cloudCoverage?: number;
}

export interface YieldPrediction {
  farmId: string;
  cropType: string;
  predictedYieldMin: number;
  predictedYieldMax: number;
  predictedYieldMean: number;
  confidencePct: number;
  predictedAt: string;
  modelVersion: string;
}

export interface WeatherReading {
  id?: string;
  farmId?: string;
  temperatureC: number;
  rainfallMm: number;
  humidityPct?: number;
  isDryDay: boolean;
  forecastType: string;
  forecastHorizonDays?: number;
  recordedDate: string;
}

export interface DroughtStatus {
  farmId: string;
  consecutiveDryDays: number;
  droughtThresholdDays: number;
  isTriggered: boolean;
  triggeredAt?: string;
  lastChecked: string;
}

export interface WeatherRisk {
  farmId: string;
  riskScore: number;
  riskLevel: 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
}

export interface WeatherAlert {
  id: string;
  farmId: string;
  alertType: string;
  severity: string;
  messageEn: string;
  messageAm?: string;
  messageOm?: string;
  forecastValue?: number;
  forecastDate?: string;
  createdAt: string;
}

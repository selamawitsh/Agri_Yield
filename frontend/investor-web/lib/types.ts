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

// ── Voucher Types (VS-02, VS-03, VS-10) ──────────────────────────────────────

export type VoucherStatus =
  | 'GENERATED'
  | 'ACTIVE'
  | 'REDEEMED'
  | 'EXPIRED'
  | 'CANCELLED'
  | 'REJECTED';

export type ProductCategory =
  | 'SEED'
  | 'FERTILIZER'
  | 'PESTICIDE'
  | 'TOOL'
  | 'OTHER';

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

// ── Investment Service Types ──────────────────────────────────────────────────

export type ListingStatus =
  | 'OPEN'
  | 'PARTIALLY_FUNDED'
  | 'FULLY_FUNDED'
  | 'FUNDING_FAILED'
  | 'ACTIVE'
  | 'COMPLETED'
  | 'CANCELLED';

export type InvestmentStatus =
  | 'PENDING'
  | 'ESCROW_LOCKED'
  | 'ACTIVE'
  | 'COMPLETED'
  | 'CANCELLED'
  | 'FAILED';

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
  fundingDeadline: string | null;
  fullyFundedAt: string | null;
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
  status: InvestmentStatus;
  cropType: string;
  region: string;
  seasonName: string;
  expectedReturnPct: number;
  actualReturnPct: number | null;
  notes: string | null;
  cancelledReason: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface PayoutRecord {
  id: string;
  investmentId: string;
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

// Alias — dashboard imports 'User', pages use 'UserProfile'
export type User = UserProfile;

// ── Investment Service Types ──────────────────────────────────────────────────

export type ListingStatus =
  | 'OPEN' | 'PARTIALLY_FUNDED' | 'FULLY_FUNDED'
  | 'FUNDING_FAILED' | 'ACTIVE' | 'COMPLETED' | 'CANCELLED';

export type InvestmentStatus =
  | 'PENDING' | 'ESCROW_LOCKED' | 'ACTIVE'
  | 'COMPLETED' | 'CANCELLED' | 'FAILED';

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
  fundingDeadline: string | null;
  fullyFundedAt: string | null;
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
  status: InvestmentStatus;
  cropType: string;
  region: string;
  seasonName: string;
  expectedReturnPct: number;
  actualReturnPct: number | null;
  notes: string | null;
  cancelledReason: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface PayoutRecord {
  id: string;
  investmentId: string;
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

export interface NdviReading {
  date: string;
  ndviValue: number;
  cloudCoverage?: number;
}

export interface YieldPrediction {
  predictedYieldMin: number;
  predictedYieldMax: number;
  predictedYieldMean: number;
  confidencePct: number;
  predictedAt: string;
}

export type User = UserProfile;

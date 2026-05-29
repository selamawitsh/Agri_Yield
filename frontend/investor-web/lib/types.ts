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

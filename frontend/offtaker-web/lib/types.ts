// ── Auth / User ───────────────────────────────────────────────────────────────

export interface UserProfile {
  id: string;
  phone: string;
  email?: string;
  faydaId: string;
  role: string;
  kycStatus: string;
  accountStatus: string;
  preferredLanguage: string;
  createdAt: string;
  bankAccounts: BankAccount[];
  defaultBankAccount?: BankAccount;
}
export type User = UserProfile;

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

// ── Farm Marketplace (GET /api/v1/offtaker/farms/{farmId}) ───────────────────

export interface FarmMarketplace {
  farmId: string;
  farmerId: string;
  cropType: string;
  areaHectares: number;
  region: string;
  kebeleCode: string;
  gpsCentroidLat: number;
  gpsCentroidLng: number;
  agriScore: number;
  cropCycleId: string;
  cropCycleStatus: string;
  currentNdvi: number;
  ndviHealthStatus: string;
  predictedYieldMeanQuintals: number;
  yieldConfidencePct: number;
  harvestReady: boolean;
  estimatedHarvestFrom: string | null;
  estimatedHarvestTo: string | null;
}

// ── Bids ─────────────────────────────────────────────────────────────────────

export type BidStatus =
  | 'PENDING' | 'ACCEPTED' | 'REJECTED'
  | 'CONTRACT_SIGNED' | 'COMPLETED' | 'DEFAULTED' | 'EXPIRED';

export interface Bid {
  id: string;
  offtakerId: string;
  farmId: string;
  quantityQuintals: number;
  pricePerQuintalEtb: number;
  totalValueEtb: number;
  bidDepositEtb: number;
  status: BidStatus;
  expiresAt: string;
  acceptedAt: string | null;
  createdAt: string;
}

export interface PlaceBidPayload {
  farmId: string;
  quantityQuintals: number;
  pricePerQuintalEtb: number;
  expiresInDays: number;
}

// ── Agreements ────────────────────────────────────────────────────────────────

export interface Agreement {
  id: string;
  bidId: string;
  contractHash: string;
  contractPdfUrl: string | null;
  farmerSignedAt: string | null;
  offtakerSignedAt: string | null;
  fullyExecuted: boolean;
  createdAt: string;
}

// ── Dispatches ────────────────────────────────────────────────────────────────

export type DispatchStatus =
  | 'SCHEDULED' | 'ARRIVED' | 'LOADED' | 'DELIVERED' | 'DRIVER_DEFAULTED';

export interface Dispatch {
  id: string;
  agreementId: string;
  driverFaydaId: string;
  truckCount: number;
  scheduledPickupDate: string;
  actualPickupDate: string | null;
  driverPenaltyEscrowEtb: number;
  status: DispatchStatus;
  createdAt: string;
}

export interface ScheduleDispatchPayload {
  agreementId: string;
  driverFaydaId: string;
  truckCount: number;
  scheduledPickupDate: string;
}

export interface ConfirmDeliveryPayload {
  actualQuantityQuintals: number;
  qualityGrade: string;
}

// ── Weather ───────────────────────────────────────────────────────────────────

export interface WeatherAlert {
  id: string;
  alertType: string;
  severity: string;
  messageEn: string;
  createdAt: string;
}

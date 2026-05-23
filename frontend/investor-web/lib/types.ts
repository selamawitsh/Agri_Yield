export interface User {
  id: string;
  phone: string;
  email: string | null;
  faydaId: string;
  role: string;
  kycStatus: string;
  accountStatus: string;
  preferredLanguage: string;
  riskTolerance?: string;
  investmentGoal?: string;
  agriScore?: number;
  totalInvestedEtb?: number;
  totalReturnedEtb?: number;
  totalSeasonsCompleted?: number;
}

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
  status: string;
  fundingDeadline: string;
  fullyFundedAt?: string;
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
  status: string;
  cropType: string;
  region: string;
  seasonName: string;
  expectedReturnPct: number;
  actualReturnPct?: number;
  notes?: string;
  cancelledReason?: string;
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
  payoutReason: string;
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
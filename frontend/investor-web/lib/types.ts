export interface User {
  id: string;
  phone: string;
  email: string | null;
  faydaId: string;
  role: string;
  kycStatus: string;
  accountStatus: string;
  preferredLanguage: string;
}

export interface FarmListing {
  id: string;
  farmId: string;
  farmName: string;
  cropType: string;
  region: string;
  totalAmount: number;
  amountRaised: number;
  baseApr: number;
  currentApr: number;
  status: string;
  ndviScore?: number;
  yieldPrediction?: number;
  farmerAgriScore: number;
}

export interface Investment {
  id: string;
  listingId: string;
  farmName: string;
  cropType: string;
  amount: number;
  investmentPct: number;
  status: string;
  currentApr: number;
  expectedReturn: number;
  createdAt: string;
}

export interface PortfolioSummary {
  totalInvested: number;
  totalReturned: number;
  activeInvestments: number;
  completedInvestments: number;
  averageApr: number;
}

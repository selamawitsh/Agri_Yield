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

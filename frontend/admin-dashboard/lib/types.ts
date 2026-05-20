export interface User {
  id: string;
  phone: string;
  email: string | null;
  faydaId: string;
  role: 'FARMER' | 'INVESTOR' | 'MERCHANT' | 'OFF_TAKER' | 'ADMIN';
  kycStatus: 'PENDING' | 'VERIFIED' | 'REJECTED';
  accountStatus: 'ACTIVE' | 'SUSPENDED' | 'PENDING_VERIFICATION';
  preferredLanguage: string;
  createdAt: string;
  updatedAt: string;
  lastLoginAt: string | null;
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

export interface ApiResponse<T> {
  success: boolean;
  message?: string;
  data: T;
  timestamp: number;
}

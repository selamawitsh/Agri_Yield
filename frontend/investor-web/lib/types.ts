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

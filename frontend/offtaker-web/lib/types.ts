export interface User {
  id: string;
  phone: string;
  email: string | null;
  faydaId: string;
  role: string;
  kycStatus: string;
  accountStatus: string;
  preferredLanguage: string;
  createdAt: string;
}

export interface BankAccount {
  id: string;
  accountType: string;
  accountNumber: string;
  accountHolderName: string;
  verified: boolean;
  default: boolean;
  verifiedAt: string | null;
}

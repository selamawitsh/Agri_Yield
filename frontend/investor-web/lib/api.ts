const BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api/v1';

function getToken(): string | null {
  if (typeof window === 'undefined') return null;
  return localStorage.getItem('access_token');
}

async function request<T>(
  method: string,
  endpoint: string,
  body?: unknown
): Promise<T> {
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
  };
  const token = getToken();
  if (token) headers['Authorization'] = `Bearer ${token}`;

  const res = await fetch(`${BASE_URL}${endpoint}`, {
    method,
    headers,
    body: body ? JSON.stringify(body) : undefined,
  });

  const data = await res.json();
  if (!res.ok || data.success === false) {
    throw new Error(data.message || 'Request failed');
  }
  return data;
}

// ── Auth ──────────────────────────────────────────────────────────────────────

export async function register(payload: {
  phone: string;
  faydaId: string;
  password: string;
  fullName: string;
  role: string;
}) {
  return request<{ success: boolean; message: string; data: string }>(
    'POST', '/auth/register', payload
  );
}

export async function verifyOtp(payload: {
  phone: string;
  otpCode: string;
  purpose: string;
}) {
  return request<{ success: boolean; message: string }>(
    'POST', '/auth/otp/verify', payload
  );
}

export async function login(payload: { phone: string; password: string }) {
  return request<{
    success: boolean;
    data: { accessToken: string; refreshToken: string; expiresIn: number };
  }>('POST', '/auth/login', payload);
}

export async function refreshToken(refreshToken: string) {
  return request<{ success: boolean; data: { accessToken: string } }>(
    'POST', '/auth/refresh', { refresh_token: refreshToken }
  );
}

export async function logout() {
  return request('POST', '/auth/logout', {});
}

// ── User Profile ──────────────────────────────────────────────────────────────

export async function getMyProfile() {
  return request<{ success: boolean; data: UserProfile }>(
    'GET', '/users/me'
  );
}

export async function updateProfile(payload: {
  email?: string;
  preferredLanguage?: string;
  riskTolerance?: string;
  investmentGoal?: string;
}) {
  return request<{ success: boolean; data: UserProfile }>(
    'PATCH', '/users/me', payload
  );
}

// ── Bank Accounts ─────────────────────────────────────────────────────────────

export async function getBankAccounts() {
  return request<{ success: boolean; data: BankAccount[] }>(
    'GET', '/users/me/bank'
  );
}

export async function addBankAccount(payload: {
  accountType: string;
  accountNumber: string;
  accountHolderName?: string;
}) {
  return request<{ success: boolean; data: BankAccount }>(
    'POST', '/users/me/bank', payload
  );
}

export async function verifyBankAccount(accountId: string, verificationCode: string) {
  return request<{ success: boolean; data: BankAccount }>(
    'POST', '/users/me/bank/verify', { account_id: accountId, verification_code: verificationCode }
  );
}

export async function setDefaultBankAccount(accountId: string) {
  return request<{ success: boolean; data: BankAccount }>(
    'POST', '/users/me/bank/default', { account_id: accountId }
  );
}

export async function deleteBankAccount(accountId: string) {
  return request<{ success: boolean }>(
    'DELETE', `/users/me/bank/${accountId}`
  );
}

// ── Types ─────────────────────────────────────────────────────────────────────

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

// US-04: Refresh token — called automatically by fetch interceptor
export async function refreshAccessToken(refreshTokenValue: string) {
  const res = await fetch(`${BASE_URL}/auth/refresh`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ refresh_token: refreshTokenValue }),
  });
  const data = await res.json();
  if (!res.ok || data.success === false) throw new Error('Token refresh failed');
  return data as { success: boolean; data: { accessToken: string } };
}

// Default axios instance for pages that use `import api from '@/lib/api'`
import axios from 'axios';

const _axiosInstance = axios.create({
  baseURL: BASE_URL,
  headers: { 'Content-Type': 'application/json' },
});

_axiosInstance.interceptors.request.use((config) => {
  if (typeof window !== 'undefined') {
    const token = localStorage.getItem('access_token');
    if (token) config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

_axiosInstance.interceptors.response.use(
  (response) => response,
  async (error) => {
    const original = error.config;
    if (error.response?.status === 401 && !original._retry) {
      original._retry = true;
      try {
        const refresh = localStorage.getItem('refresh_token');
        const res = await axios.post(`${BASE_URL}/auth/refresh`, { refresh_token: refresh });
        const newToken = res.data.data.accessToken;
        localStorage.setItem('access_token', newToken);
        original.headers.Authorization = `Bearer ${newToken}`;
        return _axiosInstance(original);
      } catch {
        localStorage.removeItem('access_token');
        localStorage.removeItem('refresh_token');
        window.location.href = '/login';
      }
    }
    return Promise.reject(error);
  }
);

export default _axiosInstance;

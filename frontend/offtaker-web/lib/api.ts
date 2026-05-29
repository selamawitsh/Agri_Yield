const BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api/v1';

function getToken(): string | null {
  if (typeof window === 'undefined') return null;
  return localStorage.getItem('access_token');
}

async function request<T>(method: string, endpoint: string, body?: unknown): Promise<T> {
  const headers: Record<string, string> = { 'Content-Type': 'application/json' };
  const token = getToken();
  if (token) headers['Authorization'] = `Bearer ${token}`;
  const res = await fetch(`${BASE_URL}${endpoint}`, {
    method, headers, body: body ? JSON.stringify(body) : undefined,
  });
  const data = await res.json();
  if (!res.ok || data.success === false) throw new Error(data.message || 'Request failed');
  return data;
}

export async function register(payload: { phone: string; faydaId: string; password: string; fullName: string; role: string }) {
  return request<{ success: boolean; message: string; data: string }>('POST', '/auth/register', payload);
}
export async function verifyOtp(payload: { phone: string; otpCode: string; purpose: string }) {
  return request<{ success: boolean }>('POST', '/auth/otp/verify', payload);
}
export async function login(payload: { phone: string; password: string }) {
  return request<{ success: boolean; data: { accessToken: string; refreshToken: string; expiresIn: number } }>('POST', '/auth/login', payload);
}
export async function logout() {
  return request('POST', '/auth/logout', {});
}
export async function getMyProfile() {
  return request<{ success: boolean; data: UserProfile }>('GET', '/users/me');
}
export async function updateProfile(payload: { email?: string; preferredLanguage?: string }) {
  return request<{ success: boolean; data: UserProfile }>('PATCH', '/users/me', payload);
}
export async function getBankAccounts() {
  return request<{ success: boolean; data: BankAccount[] }>('GET', '/users/me/bank');
}
export async function addBankAccount(payload: { accountType: string; accountNumber: string; accountHolderName?: string }) {
  return request<{ success: boolean; data: BankAccount }>('POST', '/users/me/bank', payload);
}
export async function verifyBankAccount(accountId: string, verificationCode: string) {
  return request<{ success: boolean; data: BankAccount }>('POST', '/users/me/bank/verify', { account_id: accountId, verification_code: verificationCode });
}
export async function setDefaultBankAccount(accountId: string) {
  return request<{ success: boolean; data: BankAccount }>('POST', '/users/me/bank/default', { account_id: accountId });
}
export async function deleteBankAccount(accountId: string) {
  return request<{ success: boolean }>('DELETE', `/users/me/bank/${accountId}`);
}

export interface UserProfile {
  id: string; phone: string; email?: string; faydaId: string;
  role: string; kycStatus: string; accountStatus: string;
  preferredLanguage: string; createdAt: string;
  bankAccounts: BankAccount[]; defaultBankAccount?: BankAccount;
}
export interface BankAccount {
  id: string; accountType: string; accountNumber: string;
  accountHolderName?: string; isVerified: boolean;
  isDefault: boolean; verifiedAt?: string; createdAt: string;
}

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

// ── Weather ───────────────────────────────────────────────────────────────────

export async function getWeatherRisk(farmId: string) {
  return request<{ success: boolean; data: { farmId: string; riskScore: number; riskLevel: string } }>(
    'GET', `/weather/risk/${farmId}`);
}

export async function getDroughtStatus(farmId: string) {
  return request<{ success: boolean; data: {
    farmId: string; consecutiveDryDays: number;
    droughtThresholdDays: number; isTriggered: boolean;
  } }>('GET', `/weather/drought/${farmId}`);
}

export async function getWeatherAlerts(farmId: string) {
  return request<{ success: boolean; data: Array<{
    id: string; alertType: string; severity: string;
    messageEn: string; createdAt: string;
  }> }>('GET', `/weather/alerts/${farmId}`);
}

export async function getWeatherCurrent(farmId: string) {
  return request<{ success: boolean; data: {
    temperatureC: number; rainfallMm: number;
    isDryDay: boolean; recordedDate: string;
  } }>('GET', `/weather/current/${farmId}`);
}

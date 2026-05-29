import axios from 'axios';

const BASE_URL =
    process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api/v1';

// ================= API INSTANCE =================

const api = axios.create({
  baseURL: BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// ================= TOKEN HELPERS =================

function getToken(): string | null {
  if (typeof window === 'undefined') return null;
  return localStorage.getItem('access_token');
}

// ================= AXIOS INTERCEPTORS =================

api.interceptors.request.use((config) => {
  const token = getToken();

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});

api.interceptors.response.use(
    (response) => response,

    async (error) => {
      const originalRequest = error.config;

      // Access token expired
      if (
          error.response?.status === 401 &&
          !originalRequest._retry
      ) {
        originalRequest._retry = true;

        try {
          const refreshToken = localStorage.getItem('refresh_token');

          if (!refreshToken) {
            throw new Error('No refresh token found');
          }

          const response = await axios.post(
              `${BASE_URL}/auth/refresh`,
              {
                refresh_token: refreshToken,
              },
              {
                headers: {
                  'Content-Type': 'application/json',
                },
              }
          );

          const newAccessToken =
              response.data.data.accessToken;

          // Save new token
          localStorage.setItem(
              'access_token',
              newAccessToken
          );

          // Retry original request
          originalRequest.headers.Authorization =
              `Bearer ${newAccessToken}`;

          return api(originalRequest);
        } catch (refreshError) {
          // Logout user if refresh fails
          localStorage.removeItem('access_token');
          localStorage.removeItem('refresh_token');

          if (typeof window !== 'undefined') {
            window.location.href = '/login';
          }

          return Promise.reject(refreshError);
        }
      }

      return Promise.reject(error);
    }
);

// ================= GENERIC REQUEST FUNCTION =================

async function request<T>(
    method: string,
    endpoint: string,
    body?: unknown
): Promise<T> {
  const response = await api.request<T>({
    method,
    url: endpoint,
    data: body,
  });

  return response.data;
}

// ================= AUTH =================

export async function register(payload: {
  phone: string;
  faydaId: string;
  password: string;
  fullName: string;
  role: string;
}) {
  return request<{
    success: boolean;
    message: string;
    data: string;
  }>('POST', '/auth/register', payload);
}

export async function verifyOtp(payload: {
  phone: string;
  otpCode: string;
  purpose: string;
}) {
  return request<{
    success: boolean;
    message: string;
  }>('POST', '/auth/otp/verify', payload);
}

export async function login(payload: {
  phone: string;
  password: string;
}) {
  return request<{
    success: boolean;
    data: {
      accessToken: string;
      refreshToken: string;
      expiresIn: number;
    };
  }>('POST', '/auth/login', payload);
}

export async function refreshAccessToken(
    refreshTokenValue: string
) {
  return request<{
    success: boolean;
    data: {
      accessToken: string;
    };
  }>('POST', '/auth/refresh', {
    refresh_token: refreshTokenValue,
  });
}

export async function logout() {
  return request<{
    success: boolean;
    message?: string;
  }>('POST', '/auth/logout', {});
}

// ================= USER PROFILE =================

export async function getMyProfile() {
  return request<{
    success: boolean;
    data: UserProfile;
  }>('GET', '/users/me');
}

export async function updateProfile(payload: {
  email?: string;
  preferredLanguage?: string;
  riskTolerance?: string;
  investmentGoal?: string;
}) {
  return request<{
    success: boolean;
    data: UserProfile;
  }>('PATCH', '/users/me', payload);
}

// ================= BANK ACCOUNTS =================

export async function getBankAccounts() {
  return request<{
    success: boolean;
    data: BankAccount[];
  }>('GET', '/users/me/bank');
}

export async function addBankAccount(payload: {
  accountType: string;
  accountNumber: string;
  accountHolderName?: string;
}) {
  return request<{
    success: boolean;
    data: BankAccount;
  }>('POST', '/users/me/bank', payload);
}

export async function verifyBankAccount(
    accountId: string,
    verificationCode: string
) {
  return request<{
    success: boolean;
    data: BankAccount;
  }>('POST', '/users/me/bank/verify', {
    account_id: accountId,
    verification_code: verificationCode,
  });
}

export async function setDefaultBankAccount(
    accountId: string
) {
  return request<{
    success: boolean;
    data: BankAccount;
  }>('POST', '/users/me/bank/default', {
    account_id: accountId,
  });
}

export async function deleteBankAccount(
    accountId: string
) {
  return request<{
    success: boolean;
  }>('DELETE', `/users/me/bank/${accountId}`);
}

// ================= VOUCHERS =================

// VS-02: Get all vouchers for a specific farm
export const getFarmVouchers = (
    farmId: string
) => api.get(`/vouchers/farm/${farmId}`);

// VS-03: Get single voucher detail
export const getVoucherDetail = (
    voucherId: string
) => api.get(`/vouchers/${voucherId}`);

// Portfolio voucher overview
export const getPortfolioVouchers = () =>
    api.get('/portfolio/vouchers');

// ================= TYPES =================

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

// ================= EXPORT DEFAULT API =================

export default api;
// ── Weather Service ───────────────────────────────────────────────────────────

export async function getWeatherCurrent(farmId: string) {
  return request<{ success: boolean; data: WeatherReading }>('GET', `/weather/current/${farmId}`);
}

export async function getWeatherForecast(farmId: string, days = 7) {
  return request<{ success: boolean; data: WeatherReading[] }>('GET', `/weather/forecast/${farmId}?days=${days}`);
}

export async function getWeatherRisk(farmId: string) {
  return request<{ success: boolean; data: WeatherRisk }>('GET', `/weather/risk/${farmId}`);
}

export async function getDroughtStatus(farmId: string) {
  return request<{ success: boolean; data: DroughtStatus }>('GET', `/weather/drought/${farmId}`);
}

export async function getWeatherAlerts(farmId: string) {
  return request<{ success: boolean; data: WeatherAlert[] }>('GET', `/weather/alerts/${farmId}`);
}

export interface WeatherReading {
  id?: string;
  farmId?: string;
  temperatureC: number;
  rainfallMm: number;
  humidityPct?: number;
  isDryDay: boolean;
  forecastType: string;
  forecastHorizonDays?: number;
  recordedDate: string;
}

export interface DroughtStatus {
  farmId: string;
  consecutiveDryDays: number;
  droughtThresholdDays: number;
  isTriggered: boolean;
  triggeredAt?: string;
  lastChecked: string;
}

export interface WeatherRisk {
  farmId: string;
  riskScore: number;
  riskLevel: 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
}

export interface WeatherAlert {
  id: string;
  farmId: string;
  alertType: string;
  severity: string;
  messageEn: string;
  messageAm?: string;
  forecastValue?: number;
  forecastDate?: string;
  createdAt: string;
}

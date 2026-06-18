import axios from 'axios';
import type {
  FarmMarketplace, FarmBrowseParams,
  Bid, PlaceBidPayload,
  Agreement,
  Dispatch, ScheduleDispatchPayload, ConfirmDeliveryPayload,
  UserProfile, BankAccount,
} from './types';

const BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api/v1';

const api = axios.create({
  baseURL: BASE_URL,
  headers: { 'Content-Type': 'application/json' },
});

function getToken(): string | null {
  if (typeof window === 'undefined') return null;
  return localStorage.getItem('access_token');
}

api.interceptors.request.use(cfg => {
  const token = getToken();
  if (token) cfg.headers.Authorization = `Bearer ${token}`;
  return cfg;
});

api.interceptors.response.use(
  r => r,
  async error => {
    const orig = error.config;
    if (error.response?.status === 401 && !orig._retry) {
      orig._retry = true;
      try {
        const refresh = localStorage.getItem('refresh_token');
        if (!refresh) throw new Error('no refresh token');
        const res = await axios.post(`${BASE_URL}/auth/refresh`, { refresh_token: refresh });
        const token = res.data.data.accessToken;
        localStorage.setItem('access_token', token);
        orig.headers.Authorization = `Bearer ${token}`;
        return api(orig);
      } catch {
        localStorage.removeItem('access_token');
        localStorage.removeItem('refresh_token');
        if (typeof window !== 'undefined') window.location.href = '/login';
      }
    }
    return Promise.reject(error);
  }
);

export default api;

// ── Auth ──────────────────────────────────────────────────────────────────────

export const login = (p: { phone: string; password: string }) =>
  api.post<{ success: boolean; data: { accessToken: string; refreshToken: string; expiresIn: number } }>(
    '/auth/login', p);

export const register = (p: { phone: string; faydaId: string; password: string; fullName: string; role: string }) =>
  api.post<{ success: boolean; message: string; data: string }>('/auth/register', p);

export const verifyOtp = (p: { phone: string; otpCode: string; purpose: string }) =>
  api.post<{ success: boolean }>('/auth/otp/verify', p);

export const logout = () => api.post('/auth/logout', {});

export const refreshAccessToken = (refreshToken: string) =>
  api.post<{ success: boolean; data: { accessToken: string } }>(
    '/auth/refresh', { refresh_token: refreshToken });

// ── User ──────────────────────────────────────────────────────────────────────

export const getMyProfile = () =>
  api.get<{ success: boolean; data: UserProfile }>('/users/me');

export const updateProfile = (p: { email?: string; preferredLanguage?: string }) =>
  api.patch<{ success: boolean; data: UserProfile }>('/users/me', p);

export const getBankAccounts = () =>
  api.get<{ success: boolean; data: BankAccount[] }>('/users/me/bank');

export const addBankAccount = (p: { accountType: string; accountNumber: string; accountHolderName?: string }) =>
  api.post<{ success: boolean; data: BankAccount }>('/users/me/bank', p);

export const verifyBankAccount = (accountId: string, verificationCode: string) =>
  api.post<{ success: boolean; data: BankAccount }>(
    '/users/me/bank/verify', { account_id: accountId, verification_code: verificationCode });

export const setDefaultBankAccount = (accountId: string) =>
  api.post('/users/me/bank/default', { account_id: accountId });

export const deleteBankAccount = (accountId: string) =>
  api.delete(`/users/me/bank/${accountId}`);

// ── Farm Marketplace ──────────────────────────────────────────────────────────

export const browseFarms = (params?: FarmBrowseParams) =>
  api.get<{ success: boolean; data: FarmMarketplace[] }>('/offtaker/farms', { params });

export const getFarmDetail = (farmId: string) =>
  api.get<{ success: boolean; data: FarmMarketplace }>(`/offtaker/farms/${farmId}`);

// ── Bids ──────────────────────────────────────────────────────────────────────

export const placeBid = (payload: PlaceBidPayload) =>
  api.post<{ success: boolean; data: Bid }>('/offtaker/bids', payload);

export const getMyBids = () =>
  api.get<{ success: boolean; data: Bid[] }>('/offtaker/bids');

export const getBidById = (bidId: string) =>
  api.get<{ success: boolean; data: Bid }>(`/offtaker/bids/${bidId}`);

export const getBidsForFarm = (farmId: string) =>
  api.get<{ success: boolean; data: Bid[] }>(`/offtaker/bids/farm/${farmId}`);

// ── Agreements ────────────────────────────────────────────────────────────────

export const getAgreement = (agreementId: string) =>
  api.get<{ success: boolean; data: Agreement }>(`/agreements/${agreementId}`);

export const signAgreement = (agreementId: string) =>
  api.post<{ success: boolean; data: Agreement }>(`/agreements/${agreementId}/sign`);

// ── Dispatches ────────────────────────────────────────────────────────────────

export const scheduleDispatch = (payload: ScheduleDispatchPayload) =>
  api.post<{ success: boolean; data: Dispatch }>('/offtaker/dispatches', payload);

export const getDispatchesForAgreement = (agreementId: string) =>
  api.get<{ success: boolean; data: Dispatch[] }>(`/offtaker/dispatches/${agreementId}`);

export const confirmDelivery = (agreementId: string, payload: ConfirmDeliveryPayload) =>
  api.post<{ success: boolean; data: Dispatch }>(
    `/offtaker/deliveries/${agreementId}/confirm`, payload);

// ── Weather ───────────────────────────────────────────────────────────────────

export const getWeatherAlerts = (farmId: string) =>
  api.get<{ success: boolean; data: any[] }>(`/weather/alerts/${farmId}`);

export const getWeatherCurrent = (farmId: string) =>
  api.get<{ success: boolean; data: any }>(`/weather/current/${farmId}`);

// ── Full Farm Detail (UC-OFF-02) ──────────────────────────────────────────────

export const getFarmFullDetail = (farmId: string) =>
  api.get<{ success: boolean; data: import('./types').FarmFullDetail }>(
    `/offtaker/farms/${farmId}/full-detail`);

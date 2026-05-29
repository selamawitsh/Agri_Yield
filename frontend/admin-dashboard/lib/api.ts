import axios from 'axios';

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api/v1';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: { 'Content-Type': 'application/json' },
});

api.interceptors.request.use((config) => {
  if (typeof window !== 'undefined') {
    const token = localStorage.getItem('access_token');
    if (token) config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const original = error.config;
    if (error.response?.status === 401 && !original._retry) {
      original._retry = true;
      try {
        const refreshToken = localStorage.getItem('refresh_token');
        const res = await axios.post(`${API_BASE_URL}/auth/refresh`, {
          refresh_token: refreshToken,
        });
        const newToken = res.data.data.accessToken;
        localStorage.setItem('access_token', newToken);
        original.headers.Authorization = `Bearer ${newToken}`;
        return api(original);
      } catch {
        localStorage.removeItem('access_token');
        localStorage.removeItem('refresh_token');
        window.location.href = '/login';
      }
    }
    return Promise.reject(error);
  }
);

export default api;

// ── Fraud API (FR-08) ─────────────────────────────────────────────────────────

export const getFraudAlerts = (params: {
  severity?: string;
  unresolvedOnly?: boolean;
  page?: number;
  size?: number;
}) => api.get('/admin/fraud-audit', { params });

export const getFraudAlert = (alertId: string) =>
  api.get(`/admin/fraud-audit/${alertId}`);

export const resolveAlert = (alertId: string, notes: string) =>
  api.patch(`/admin/fraud-audit/${alertId}/resolve`, { notes });

export const getAlertsByEntity = (entityId: string, entityType: string) =>
  api.get(`/admin/fraud-audit/entity/${entityId}`, { params: { entityType } });

export const getFraudScore = (entityId: string, entityType: string) =>
  api.get(`/admin/fraud-score/${entityId}`, { params: { entityType } });

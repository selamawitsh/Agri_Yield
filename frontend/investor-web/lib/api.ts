import axios from 'axios';

const API_BASE_URL = 'http://localhost:8081/api/v1';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add token to EVERY request
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('access_token');
  if (token && token.includes('.') && token.split('.').length === 3) {
    config.headers.Authorization = `Bearer ${token}`;
    console.log('✅ Request with valid JWT token');
  } else {
    console.warn('⚠️ No valid JWT token found');
  }
  return config;
});

// Handle token refresh on 401
api.interceptors.response.use(
  (response) => {
    console.log('✅ Response received:', response.config.url, response.status);
    return response;
  },
  async (error) => {
    console.error('❌ Response error:', error.config?.url, error.response?.status);
    const originalRequest = error.config;
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;
      try {
        const refreshToken = localStorage.getItem('refresh_token');
        const response = await axios.post(`${API_BASE_URL}/auth/refresh`, {}, {
          headers: { 'X-Refresh-Token': refreshToken }
        });
        const { access_token } = response.data.data;
        localStorage.setItem('access_token', access_token);
        originalRequest.headers.Authorization = `Bearer ${access_token}`;
        console.log('✅ Token refreshed successfully');
        return api(originalRequest);
      } catch (refreshError) {
        console.error('❌ Refresh failed, redirecting to login');
        localStorage.removeItem('access_token');
        localStorage.removeItem('refresh_token');
        window.location.href = '/login';
        return Promise.reject(refreshError);
      }
    }
    return Promise.reject(error);
  }
);

export default api;

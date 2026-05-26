import { refreshAccessToken } from './api';

export function saveTokens(accessToken: string, refreshToken: string) {
  localStorage.setItem('access_token', accessToken);
  localStorage.setItem('refresh_token', refreshToken);
}

export function getAccessToken(): string | null {
  return localStorage.getItem('access_token');
}

export function getRefreshToken(): string | null {
  return localStorage.getItem('refresh_token');
}

export function clearTokens() {
  localStorage.removeItem('access_token');
  localStorage.removeItem('refresh_token');
}

export function isLoggedIn(): boolean {
  return !!getAccessToken();
}

// US-04: Call this when a 401 is received — tries to get a new access token
export async function tryRefreshToken(): Promise<boolean> {
  try {
    const refresh = getRefreshToken();
    if (!refresh) return false;
    const res = await refreshAccessToken(refresh);
    localStorage.setItem('access_token', res.data.accessToken);
    return true;
  } catch {
    clearTokens();
    return false;
  }
}

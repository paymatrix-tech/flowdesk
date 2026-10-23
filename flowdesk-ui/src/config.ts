const configured = import.meta.env.VITE_API_BASE_URL;
export const API_BASE =
  configured !== undefined && configured !== '' ? configured : '';

export const apiV1 = `${API_BASE}/api/v1`;
export const searchApi = `${API_BASE}/api/search/payments/by-keyword`;
export const analyticsApi = `${API_BASE}/api/analytics`;

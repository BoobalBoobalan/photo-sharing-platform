import axios from 'axios';

// Use VITE_API_BASE_URL if set, otherwise default to live Render backend production endpoint
const baseURL = import.meta.env.VITE_API_BASE_URL || 'https://photo-sharing-platform-u4t3.onrender.com/api';

const API = axios.create({
  baseURL: baseURL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Attach Authorization header if JWT token exists in localStorage
API.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Global Error Interceptor
API.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      if (!window.location.pathname.startsWith('/gallery/')) {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
      }
    }
    return Promise.reject(error);
  }
);

export default API;

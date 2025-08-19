// src/lib/api.ts

import toast from "react-hot-toast";

export async function fetchWithAuth(url: string, options: RequestInit = {}): Promise<Response> {
  const token = localStorage.getItem('jwt');

  const headers = new Headers(options.headers || {});
  if (token) {
    headers.append('Authorization', `Bearer ${token}`);
  }

  const response = await fetch(url, {
    ...options,
    headers,
  });


  if (response.status === 401) {  // Unauthorized - token likely expired
    toast.error('Session expired, please log in again.');
    localStorage.removeItem('jwt');
    window.dispatchEvent(new Event("logout"));
  }

  return response;
}

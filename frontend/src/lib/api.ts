// src/lib/api.ts

import toast from "react-hot-toast";

export async function fetchWithAuth(url: string, options: RequestInit = {}): Promise<Response> {
  const token = localStorage.getItem('jwt');
  // toast.error(token)
  const headers = new Headers(options.headers || {});
  if (token) {
    headers.append('Authorization', `Bearer ${token}`);
  }

  // toast.error(url)
  const response = await fetch(url, {
    ...options,
    headers,
  });
  // toast.error(url, response.status)


  if (response.status === 401) {  // Unauthorized - token likely expired
    toast.error('Session expired, please log in again.');
    localStorage.removeItem('jwt');
    window.dispatchEvent(new Event("logout"));
  }

  return response;
}

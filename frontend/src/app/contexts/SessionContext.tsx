
'use client';

import { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import toast from "react-hot-toast";

interface Session {
  isLoggedIn: boolean;
  token: string | null;
}

export interface SessionContextType {
  session: Session;
  login: (token: string) => void;
  logout: () => void;
}

const SessionContext = createContext<SessionContextType | undefined>(undefined);

export function SessionProvider({ children }: { children: ReactNode }) {
  const [session, setSession] = useState<Session>({
    isLoggedIn: false,
    token: null,
  });

  useEffect(() => {
    const token = localStorage.getItem('jwt');
    if (token) {
      setSession({ isLoggedIn: true, token });
    }

    const handleLogoutEvent = () => logout();
    window.addEventListener("logout", handleLogoutEvent);

    return () => {
      window.removeEventListener("logout", handleLogoutEvent);
    };
  }, []);

  const login = (token: string) => {
    localStorage.setItem('jwt', token);
    setSession({ isLoggedIn: true, token });
  };

  const logout = () => {
    localStorage.removeItem('jwt');
    setSession({ isLoggedIn: false, token: null });
    window.location.href = "/";
    // window.location.reload();
    toast.success('Log out successful!');
  };

  return (
    <SessionContext.Provider value={{ session, login, logout }}>
      {children}
    </SessionContext.Provider>
  );
}

export function useSession() {
  const context = useContext(SessionContext);
  if (context === undefined) {
    throw new Error('useSession must be used within a SessionProvider');
  }
  return context;
}

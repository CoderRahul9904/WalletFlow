import {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useState,
  type ReactNode,
} from 'react';

/**
 * DEVELOPMENT-ONLY session state.
 *
 * The WalletFlow backend does NOT implement authentication. There are no
 * login / token / session endpoints. To make the local UI usable for testing
 * against the seeded demo data, we simply remember a `userId`, `walletId`,
 * and `userName` in sessionStorage.
 *
 * This is explicitly NOT a secure login. Do not use this pattern in production.
 */

interface SessionState {
  userId: number | null;
  walletId: number | null;
  userName: string | null;
  email: string | null;
}

interface SessionContextValue extends SessionState {
  isReady: boolean;
  setSession: (next: Partial<SessionState>) => void;
  clearSession: () => void;
}

const STORAGE_KEY = 'walletflow.dev-session';

const SessionContext = createContext<SessionContextValue | undefined>(undefined);

function readStored(): SessionState {
  try {
    const raw = sessionStorage.getItem(STORAGE_KEY);
    if (!raw) return { userId: null, walletId: null, userName: null, email: null };
    const parsed = JSON.parse(raw) as Partial<SessionState>;
    return {
      userId: parsed.userId ?? null,
      walletId: parsed.walletId ?? null,
      userName: parsed.userName ?? null,
      email: parsed.email ?? null,
    };
  } catch {
    return { userId: null, walletId: null, userName: null, email: null };
  }
}

export function SessionProvider({ children }: { children: ReactNode }) {
  const [session, setSessionState] = useState<SessionState>(() => readStored());

  useEffect(() => {
    try {
      sessionStorage.setItem(STORAGE_KEY, JSON.stringify(session));
    } catch {
      /* ignore quota / private mode errors */
    }
  }, [session]);

  const setSession = useCallback((next: Partial<SessionState>) => {
    setSessionState((prev) => ({ ...prev, ...next }));
  }, []);

  const clearSession = useCallback(() => {
    setSessionState({ userId: null, walletId: null, userName: null, email: null });
    sessionStorage.removeItem(STORAGE_KEY);
  }, []);

  const value = useMemo<SessionContextValue>(
    () => ({
      ...session,
      isReady: session.walletId != null,
      setSession,
      clearSession,
    }),
    [session, setSession, clearSession],
  );

  return <SessionContext.Provider value={value}>{children}</SessionContext.Provider>;
}

export function useSession(): SessionContextValue {
  const ctx = useContext(SessionContext);
  if (!ctx) throw new Error('useSession must be used within SessionProvider');
  return ctx;
}

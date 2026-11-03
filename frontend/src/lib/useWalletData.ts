import { useCallback, useEffect, useState } from 'react';
import { getBalance } from '../api/walletService';
import { getTransactionHistory } from '../api/transactionService';
import { ApiError } from '../api/client';
import type { UserBalance, UserTransactionHistoryResponse } from '../api/types';
import { useSession } from '../context/SessionContext';

interface WalletData {
  balance: UserBalance | null;
  history: UserTransactionHistoryResponse[];
  loading: boolean;
  error: string | null;
  refresh: () => Promise<void>;
}

/**
 * Loads both balance and transaction history for the currently selected
 * wallet. Used by Dashboard, Balance and History pages so refreshing after
 * a mutation is one call.
 */
export function useWalletData(): WalletData {
  const { walletId } = useSession();
  const [balance, setBalance] = useState<UserBalance | null>(null);
  const [history, setHistory] = useState<UserTransactionHistoryResponse[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const refresh = useCallback(async () => {
    if (walletId == null) {
      setBalance(null);
      setHistory([]);
      return;
    }
    setLoading(true);
    setError(null);
    try {
      const [b, h] = await Promise.all([getBalance(walletId), getTransactionHistory(walletId)]);
      setBalance(b);
      setHistory(h);
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'Failed to load wallet data.');
    } finally {
      setLoading(false);
    }
  }, [walletId]);

  useEffect(() => {
    void refresh();
  }, [refresh]);

  return { balance, history, loading, error, refresh };
}

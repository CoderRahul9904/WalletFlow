import { RefreshCw, Wallet as WalletIcon } from 'lucide-react';
import { formatMoney } from '../lib/format';
import { useWalletData } from '../lib/useWalletData';
import { useSession } from '../context/SessionContext';
import StateBoundary from '../components/StateBoundary';

export default function Balance() {
  const { walletId } = useSession();
  const { balance, loading, error, refresh } = useWalletData();

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-semibold text-slate-900">Wallet balance</h1>
        <p className="mt-1 text-sm text-slate-500">
          Live view returned by GET /api/v1/wallets/{walletId}/balance
        </p>
      </div>

      <StateBoundary loading={loading} error={error} onRetry={() => void refresh()}>
        <div className="card p-8 bg-gradient-to-br from-brand-600 to-brand-800 text-white">
          <div className="flex items-center gap-2 text-white/80 text-sm">
            <WalletIcon size={16} /> Available balance
          </div>
          <div className="mt-3 text-4xl font-semibold tabular-nums">
            {formatMoney(balance?.balance ?? 0, balance?.currency ?? 'INR')}
          </div>
          <div className="mt-2 text-sm text-white/80">
            Wallet #{balance?.id ?? walletId} · {balance?.currency ?? 'INR'}
          </div>
        </div>

        <div className="mt-4 flex justify-end">
          <button type="button" className="btn-secondary" onClick={() => void refresh()}>
            <RefreshCw size={16} /> Refresh
          </button>
        </div>
      </StateBoundary>
    </div>
  );
}

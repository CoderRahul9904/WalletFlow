import { useMemo, useState } from 'react';
import { RefreshCw } from 'lucide-react';
import { useWalletData } from '../lib/useWalletData';
import { useSession } from '../context/SessionContext';
import StateBoundary from '../components/StateBoundary';
import TransactionList from '../components/TransactionList';
import type { TransactionType } from '../api/types';

type Filter = 'ALL' | TransactionType;

const FILTERS: { key: Filter; label: string }[] = [
  { key: 'ALL', label: 'All' },
  { key: 'DEPOSIT', label: 'Deposits' },
  { key: 'WITHDRAWAL', label: 'Withdrawals' },
  { key: 'TRANSFER', label: 'Transfers' },
];

/**
 * Client-side filtering / sorting only — the backend exposes no list,
 * filter, sort or by-reference transaction endpoints, so we operate purely
 * on the array returned by POST /api/v1/transactions/history/{walletId}.
 * There is no transaction-detail endpoint to link to from here.
 */
export default function History() {
  const { walletId } = useSession();
  const { history, loading, error, refresh } = useWalletData();
  const [filter, setFilter] = useState<Filter>('ALL');
  const [sortDesc, setSortDesc] = useState(true);

  const filtered = useMemo(() => {
    const rows = filter === 'ALL' ? history : history.filter((t) => t.transactionType === filter);
    return [...rows].sort((a, b) => {
      const ta = new Date(a.transactionTime).getTime();
      const tb = new Date(b.transactionTime).getTime();
      return sortDesc ? tb - ta : ta - tb;
    });
  }, [history, filter, sortDesc]);

  return (
    <div className="space-y-6">
      <div className="flex items-start justify-between gap-4 flex-wrap">
        <div>
          <h1 className="text-2xl font-semibold text-slate-900">Transaction history</h1>
          <p className="mt-1 text-sm text-slate-500">
            Wallet #{walletId} · {history.length} total record{history.length === 1 ? '' : 's'}
          </p>
        </div>
        <div className="flex items-center gap-2">
          <button
            type="button"
            className="btn-secondary"
            onClick={() => setSortDesc((s) => !s)}
            title="Toggle sort direction by transaction time"
          >
            {sortDesc ? 'Newest first' : 'Oldest first'}
          </button>
          <button type="button" className="btn-primary" onClick={() => void refresh()}>
            <RefreshCw size={16} /> Refresh
          </button>
        </div>
      </div>

      <div className="flex gap-2 flex-wrap">
        {FILTERS.map((f) => (
          <button
            key={f.key}
            type="button"
            onClick={() => setFilter(f.key)}
            className={[
              'px-3 py-1.5 rounded-full text-xs font-medium border transition',
              filter === f.key
                ? 'bg-brand-600 text-white border-brand-600'
                : 'bg-white text-slate-600 border-slate-200 hover:border-brand-300',
            ].join(' ')}
          >
            {f.label}
          </button>
        ))}
      </div>

      <StateBoundary
        loading={loading}
        error={error}
        isEmpty={!loading && !error && filtered.length === 0}
        emptyMessage="No transactions match the current filter."
        onRetry={() => void refresh()}
      >
        <div className="card p-6">
          <TransactionList transactions={filtered} />
        </div>
      </StateBoundary>

      <p className="text-xs text-slate-500">
        Note: the backend does not expose a transaction-by-reference endpoint,
        so transaction details are shown here directly from the history array.
      </p>
    </div>
  );
}

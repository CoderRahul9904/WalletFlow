import { Link } from 'react-router-dom';
import {
  ArrowDownLeft,
  ArrowUpRight,
  CreditCard,
  RefreshCw,
  Wallet as WalletIcon,
} from 'lucide-react';
import { formatMoney } from '../lib/format';
import { useWalletData } from '../lib/useWalletData';
import { useSession } from '../context/SessionContext';
import StatCard from '../components/StatCard';
import StateBoundary from '../components/StateBoundary';
import TransactionList from '../components/TransactionList';

export default function Dashboard() {
  const { walletId, userName } = useSession();
  const { balance, history, loading, error, refresh } = useWalletData();

  const total = history.length;
  const incoming = history.filter((t) => t.walletMoneyFlowType === 'INCOMING').length;
  const outgoing = total - incoming;

  return (
    <div className="space-y-6">
      <div className="flex items-start justify-between gap-4">
        <div>
          <h1 className="text-2xl font-semibold text-slate-900">
            Hello, {userName ?? 'there'}
          </h1>
          <p className="mt-1 text-sm text-slate-500">
            Wallet #{walletId ?? '—'} · {balance?.currency ?? 'INR'}
          </p>
        </div>
        <button type="button" className="btn-secondary" onClick={() => void refresh()}>
          <RefreshCw size={16} /> Refresh
        </button>
      </div>

      <StateBoundary loading={loading} error={error} onRetry={() => void refresh()}>
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
          <StatCard
            label="Available balance"
            value={formatMoney(balance?.balance ?? 0, balance?.currency ?? 'INR')}
            icon={WalletIcon}
            tone="brand"
          />
          <StatCard label="Transactions" value={total} icon={CreditCard} tone="slate" />
          <StatCard label="Incoming" value={incoming} icon={ArrowDownLeft} tone="emerald" />
          <StatCard label="Outgoing" value={outgoing} icon={ArrowUpRight} tone="rose" />
        </div>

        <div className="grid md:grid-cols-3 gap-3 mt-2">
          <Link to="/deposit" className="card p-4 hover:border-brand-300 transition group">
            <div className="flex items-center gap-3">
              <div className="h-9 w-9 rounded-lg bg-emerald-50 text-emerald-700 grid place-items-center">
                <ArrowDownLeft size={16} />
              </div>
              <div>
                <div className="text-sm font-semibold text-slate-900">Deposit money</div>
                <div className="text-xs text-slate-500 group-hover:text-brand-600">
                  Add funds to your wallet
                </div>
              </div>
            </div>
          </Link>
          <Link to="/withdraw" className="card p-4 hover:border-brand-300 transition group">
            <div className="flex items-center gap-3">
              <div className="h-9 w-9 rounded-lg bg-red-50 text-red-700 grid place-items-center">
                <ArrowUpRight size={16} />
              </div>
              <div>
                <div className="text-sm font-semibold text-slate-900">Withdraw money</div>
                <div className="text-xs text-slate-500 group-hover:text-brand-600">
                  Move funds out of your wallet
                </div>
              </div>
            </div>
          </Link>
          <Link to="/transfer" className="card p-4 hover:border-brand-300 transition group">
            <div className="flex items-center gap-3">
              <div className="h-9 w-9 rounded-lg bg-brand-50 text-brand-700 grid place-items-center">
                <RefreshCw size={16} />
              </div>
              <div>
                <div className="text-sm font-semibold text-slate-900">Transfer money</div>
                <div className="text-xs text-slate-500 group-hover:text-brand-600">
                  Send to another wallet ID
                </div>
              </div>
            </div>
          </Link>
        </div>

        <div className="card p-6 mt-6">
          <div className="flex items-center justify-between mb-2">
            <h2 className="text-base font-semibold text-slate-900">Recent activity</h2>
            <Link to="/history" className="text-sm text-brand-700 hover:underline">
              View all
            </Link>
          </div>
          <TransactionList transactions={history} limit={6} />
          {history.length === 0 && (
            <p className="mt-2 text-xs text-slate-500 text-center">
              This wallet has no transactions yet. Deposit some money to get started.
            </p>
          )}
        </div>
      </StateBoundary>
    </div>
  );
}

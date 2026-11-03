import { ArrowUpRight, RefreshCw, Landmark } from 'lucide-react';
import type { UserTransactionHistoryResponse } from '../api/types';
import { formatDateTime, formatMoney } from '../lib/format';

interface Props {
  transactions: UserTransactionHistoryResponse[];
  limit?: number;
}

/** Visual metadata derived from a backend transaction row. */
function visualise(tx: UserTransactionHistoryResponse) {
  const incoming = tx.walletMoneyFlowType === 'INCOMING';
  const Icon =
    tx.transactionType === 'TRANSFER'
      ? RefreshCw
      : tx.transactionType === 'DEPOSIT'
        ? Landmark
        : ArrowUpRight;
  return {
    Icon,
    incoming,
    amountClass: incoming ? 'text-incoming' : 'text-outgoing',
    sign: incoming ? '+' : '−',
  };
}

export default function TransactionList({ transactions, limit }: Props) {
  const rows = limit ? transactions.slice(0, limit) : transactions;
  return (
    <ul className="divide-y divide-slate-100">
      {rows.map((tx, idx) => {
        const v = visualise(tx);
        const Icon = v.Icon;
        return (
          <li key={`${tx.transactionTime}-${idx}`} className="py-3 flex items-center gap-3">
            <div
              className={[
                'h-9 w-9 rounded-full grid place-items-center shrink-0',
                v.incoming ? 'bg-emerald-50 text-incoming' : 'bg-red-50 text-outgoing',
              ].join(' ')}
            >
              <Icon size={16} />
            </div>
            <div className="min-w-0 flex-1">
              <div className="text-sm font-medium text-slate-900 truncate">
                {tx.counterPartyName}
              </div>
              <div className="text-xs text-slate-500 truncate">
                {tx.transactionType} · {tx.transactionStatus} ·{' '}
                {v.incoming ? <span>INCOMING</span> : <span>OUTGOING</span>}
              </div>
              <div className="text-[11px] text-slate-400">
                {formatDateTime(tx.transactionTime)}
              </div>
            </div>
            <div className={['text-sm font-semibold tabular-nums', v.amountClass].join(' ')}>
              {v.sign}
              {formatMoney(tx.amount)}
            </div>
          </li>
        );
      })}
      {rows.length === 0 && (
        <li className="py-6 text-center text-sm text-slate-500">No transactions yet.</li>
      )}
    </ul>
  );
}

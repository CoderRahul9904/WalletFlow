import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { ArrowUpRight, Loader2 } from 'lucide-react';
import { withdraw } from '../api/walletService';
import { ApiError } from '../api/client';
import { formatMoney } from '../lib/format';
import { useSession } from '../context/SessionContext';
import { useWalletData } from '../lib/useWalletData';
import FeedbackBanner from '../components/FeedbackBanner';

export default function Withdraw() {
  const { walletId } = useSession();
  const { balance, refresh } = useWalletData();
  const [busy, setBusy] = useState(false);
  const [result, setResult] = useState<{ kind: 'success' | 'error'; text: string } | null>(null);

  const available = Number(balance?.balance ?? 0);

  // NOTE: we intentionally do NOT cap the amount at the current balance here.
  // The backend's InsufficientBalanceException is the authoritative source of
  // that error (see WalletService#withDrawAmount). Blocking it client-side
  // would prevent the canonical server message from ever reaching the UI.
  const schema = z.object({
    amount: z.coerce
      .number({ invalid_type_error: 'Amount is required' })
      .positive('Amount must be greater than zero'),
  });
  type Form = z.infer<typeof schema>;

  const form = useForm<Form>({ resolver: zodResolver(schema), defaultValues: { amount: 100 } });

  async function onSubmit(values: Form) {
    if (walletId == null) return;
    setBusy(true);
    setResult(null);
    try {
      const res = await withdraw(walletId, values.amount);
      setResult({ kind: 'success', text: res.message });
      await refresh();
      form.reset({ amount: 100 });
    } catch (err) {
      const e = err as ApiError;
      setResult({ kind: 'error', text: e?.message ?? 'Withdrawal failed.' });
    } finally {
      setBusy(false);
    }
  }

  return (
    <div className="space-y-6 max-w-xl">
      <div>
        <h1 className="text-2xl font-semibold text-slate-900">Withdraw money</h1>
        <p className="mt-1 text-sm text-slate-500">
          POST /api/v1/wallets/{'{id}'}/withdraw. The backend rejects withdrawals
          larger than the current balance and any non-positive amounts.
        </p>
      </div>

      <div className="card p-6">
        <div className="text-sm text-slate-500">Available balance</div>
        <div className="mt-1 text-3xl font-semibold text-slate-900 tabular-nums">
          {formatMoney(available, balance?.currency ?? 'INR')}
        </div>
      </div>

      {result && <FeedbackBanner kind={result.kind} message={result.text} />}

      <form onSubmit={form.handleSubmit(onSubmit)} className="card p-6 space-y-4">
        <div>
          <label className="label" htmlFor="amount">Withdrawal amount (INR)</label>
          <input
            id="amount"
            type="number"
            step="0.01"
            className="input"
            {...form.register('amount')}
            disabled={busy}
          />
          {form.formState.errors.amount && (
            <p className="text-xs text-red-600 mt-1">{form.formState.errors.amount.message}</p>
          )}
        </div>
        <button type="submit" className="btn-danger w-full" disabled={busy || walletId == null}>
          {busy ? <Loader2 className="animate-spin" size={16} /> : <ArrowUpRight size={16} />}
          Withdraw
        </button>
      </form>
    </div>
  );
}

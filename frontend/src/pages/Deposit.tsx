import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { ArrowDownLeft, Loader2 } from 'lucide-react';
import { deposit } from '../api/walletService';
import { ApiError } from '../api/client';
import { formatMoney } from '../lib/format';
import { useSession } from '../context/SessionContext';
import { useWalletData } from '../lib/useWalletData';
import FeedbackBanner from '../components/FeedbackBanner';

const schema = z.object({
  amount: z.coerce
    .number({ invalid_type_error: 'Amount is required' })
    .positive('Amount must be greater than zero')
    .max(10_000_000, 'Amount is unrealistically large'),
});
type Form = z.infer<typeof schema>;

export default function Deposit() {
  const { walletId } = useSession();
  const { balance, refresh } = useWalletData();
  const [busy, setBusy] = useState(false);
  const [result, setResult] = useState<{ kind: 'success' | 'error'; text: string } | null>(null);

  const form = useForm<Form>({
    resolver: zodResolver(schema),
    defaultValues: { amount: 500 },
  });

  async function onSubmit(values: Form) {
    if (walletId == null) return;
    setBusy(true);
    setResult(null);
    try {
      const res = await deposit(walletId, values.amount);
      setResult({ kind: 'success', text: res.message });
      await refresh();
      form.reset({ amount: 500 });
    } catch (err) {
      const e = err as ApiError;
      setResult({ kind: 'error', text: e?.message ?? 'Deposit failed.' });
    } finally {
      setBusy(false);
    }
  }

  return (
    <div className="space-y-6 max-w-xl">
      <div>
        <h1 className="text-2xl font-semibold text-slate-900">Deposit money</h1>
        <p className="mt-1 text-sm text-slate-500">
          Adds funds to wallet #{walletId} via POST /api/v1/wallets/{'{id}'}/deposit.
          The backend expects a raw JSON number as the body — the Axios layer
          sends it in exactly that format.
        </p>
      </div>

      <div className="card p-6">
        <div className="text-sm text-slate-500">Current balance</div>
        <div className="mt-1 text-3xl font-semibold text-slate-900 tabular-nums">
          {formatMoney(balance?.balance ?? 0, balance?.currency ?? 'INR')}
        </div>
      </div>

      {result && <FeedbackBanner kind={result.kind} message={result.text} />}

      <form onSubmit={form.handleSubmit(onSubmit)} className="card p-6 space-y-4">
        <div>
          <label className="label" htmlFor="amount">Deposit amount (INR)</label>
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
        <div className="flex flex-wrap gap-2">
          {[100, 500, 1000, 5000].map((v) => (
            <button
              key={v}
              type="button"
              className="btn-secondary"
              onClick={() => form.setValue('amount', v, { shouldValidate: true })}
              disabled={busy}
            >
              {formatMoney(v)}
            </button>
          ))}
        </div>
        <button type="submit" className="btn-success w-full" disabled={busy || walletId == null}>
          {busy ? <Loader2 className="animate-spin" size={16} /> : <ArrowDownLeft size={16} />}
          Deposit
        </button>
      </form>
    </div>
  );
}

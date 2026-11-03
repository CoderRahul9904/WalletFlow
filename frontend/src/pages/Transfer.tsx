import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { Loader2, RefreshCw } from 'lucide-react';
import { transferMoney } from '../api/transactionService';
import { ApiError } from '../api/client';
import { formatMoney } from '../lib/format';
import { useSession } from '../context/SessionContext';
import { useWalletData } from '../lib/useWalletData';
import ConfirmDialog from '../components/ConfirmDialog';
import FeedbackBanner from '../components/FeedbackBanner';

const schema = z.object({
  receiverWalletId: z.coerce
    .number({ invalid_type_error: 'Receiver wallet ID is required' })
    .int('Receiver wallet ID must be an integer')
    .positive('Receiver wallet ID must be a positive integer'),
  amount: z.coerce
    .number({ invalid_type_error: 'Amount is required' })
    .positive('Amount must be greater than zero'),
});
type Form = z.infer<typeof schema>;

export default function Transfer() {
  const { walletId } = useSession();
  const { balance, refresh } = useWalletData();
  const [busy, setBusy] = useState(false);
  const [confirmOpen, setConfirmOpen] = useState(false);
  const [pending, setPending] = useState<Form | null>(null);
  const [result, setResult] = useState<{ kind: 'success' | 'error'; text: string } | null>(null);

  const form = useForm<Form>({
    resolver: zodResolver(schema),
    defaultValues: { receiverWalletId: undefined, amount: 100 },
  });

  function handleSubmit(values: Form) {
    if (walletId == null) return;
    if (values.receiverWalletId === walletId) {
      setResult({ kind: 'error', text: 'Self-transfer is not allowed. Enter a different wallet ID.' });
      return;
    }
    setPending(values);
    setConfirmOpen(true);
  }

  async function executeTransfer() {
    if (!pending || walletId == null) return;
    setBusy(true);
    setResult(null);
    try {
      const res = await transferMoney(walletId, {
        receiverWalletId: pending.receiverWalletId,
        amount: pending.amount,
      });
      setResult({ kind: 'success', text: res.message });
      await refresh();
      form.reset({ receiverWalletId: undefined, amount: 100 });
      setConfirmOpen(false);
    } catch (err) {
      const e = err as ApiError;
      setResult({ kind: 'error', text: e?.message ?? 'Transfer failed.' });
      setConfirmOpen(false);
    } finally {
      setBusy(false);
    }
  }

  return (
    <div className="space-y-6 max-w-xl">
      <div>
        <h1 className="text-2xl font-semibold text-slate-900">Transfer money</h1>
        <p className="mt-1 text-sm text-slate-500">
          POST /api/v1/transactions/transfer/{'{senderWalletId}'}. Body matches
          the backend DTO <code>UserTransferMoneyRequest</code>:
          <code>{'{ receiverWalletId, amount }'}</code>.
        </p>
      </div>

      <div className="card p-6">
        <div className="text-sm text-slate-500">Available balance (wallet #{walletId})</div>
        <div className="mt-1 text-3xl font-semibold text-slate-900 tabular-nums">
          {formatMoney(balance?.balance ?? 0, balance?.currency ?? 'INR')}
        </div>
      </div>

      {result && <FeedbackBanner kind={result.kind} message={result.text} />}

      <form onSubmit={form.handleSubmit(handleSubmit)} className="card p-6 space-y-4">
        <div>
          <label className="label" htmlFor="receiverWalletId">Receiver wallet ID</label>
          <input
            id="receiverWalletId"
            type="number"
            className="input"
            placeholder="e.g. 102"
            {...form.register('receiverWalletId')}
            disabled={busy}
          />
          {form.formState.errors.receiverWalletId && (
            <p className="text-xs text-red-600 mt-1">
              {form.formState.errors.receiverWalletId.message}
            </p>
          )}
        </div>
        <div>
          <label className="label" htmlFor="amount">Amount (INR)</label>
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
        <button
          type="submit"
          className="btn-primary w-full"
          disabled={busy || walletId == null}
        >
          {busy ? <Loader2 className="animate-spin" size={16} /> : <RefreshCw size={16} />}
          Review &amp; confirm transfer
        </button>
      </form>

      <ConfirmDialog
        open={confirmOpen}
        title="Confirm money transfer"
        busy={busy}
        confirmLabel={busy ? 'Processing…' : 'Send transfer'}
        description={
          pending && (
            <div className="space-y-2">
              <p>
                You are about to send{' '}
                <strong>{formatMoney(pending.amount)}</strong> from your wallet
                #{walletId} to wallet#{' '}
                <strong>{pending.receiverWalletId}</strong>.
              </p>
              <p className="text-xs text-slate-500">
                This operation is irreversible. Both wallets' balances and the
                transaction record will be updated atomically on the server.
              </p>
            </div>
          )
        }
        onCancel={() => !busy && setConfirmOpen(false)}
        onConfirm={() => void executeTransfer()}
      />
    </div>
  );
}

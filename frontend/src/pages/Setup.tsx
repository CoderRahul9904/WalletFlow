import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { Link, useNavigate } from 'react-router-dom';
import { Building2, Loader2, UserPlus, Wallet as WalletIcon } from 'lucide-react';
import { createUser } from '../api/userService';
import { createWallet } from '../api/walletService';
import type { ApiError } from '../api/client';
import { useSession } from '../context/SessionContext';
import FeedbackBanner from '../components/FeedbackBanner';

const signupSchema = z.object({
  name: z.string().min(2, 'Name is required'),
  email: z.string().email('Enter a valid email'),
  password: z.string().min(4, 'Password must be at least 4 characters'),
  phone: z.string().min(7, 'Enter a valid phone number'),
});
type SignupForm = z.infer<typeof signupSchema>;

const attachSchema = z.object({
  walletId: z.coerce.number().int().positive('Wallet ID must be a positive integer'),
  userName: z.string().min(1, 'Display name is required').optional().or(z.literal('')),
});
type AttachForm = z.infer<typeof attachSchema>;

/**
 * Setup page.
 *
 * The WalletFlow backend does NOT implement authentication — there is no
 * login, token, or session endpoint. This page is a DEVELOPMENT-ONLY
 * selector that lets a user either register a new account (which triggers
 * wallet creation) or attach to an existing wallet by its numeric ID for
 * browsing the seeded demo data.
 *
 * Do not expose this to untrusted users. Never enter real credentials.
 */
export default function Setup() {
  const navigate = useNavigate();
  const { setSession, walletId, userName } = useSession();

  const signup = useForm<SignupForm>({ resolver: zodResolver(signupSchema) });
  const attach = useForm<AttachForm>({
    resolver: zodResolver(attachSchema),
    defaultValues: { walletId: 101, userName: '' },
  });

  const [busy, setBusy] = useState<null | 'signup' | 'attach'>(null);
  const [banner, setBanner] = useState<{ kind: 'success' | 'error'; text: string } | null>(null);

  async function onSignup(values: SignupForm) {
    setBusy('signup');
    setBanner(null);
    try {
      const created = await createUser(values);
      const w = await createWallet(created.id);
      setSession({
        userId: created.id,
        walletId: w.walletId,
        userName: w.userName,
        email: w.email,
      });
      setBanner({
        kind: 'success',
        text: `Welcome ${w.userName}! Wallet #${w.walletId} is ready.`,
      });
      setTimeout(() => navigate('/'), 600);
    } catch (err) {
      const e = err as ApiError;
      setBanner({ kind: 'error', text: e?.message ?? 'Registration failed.' });
    } finally {
      setBusy(null);
    }
  }

  function onAttach(values: AttachForm) {
    setBusy('attach');
    setBanner(null);
    setSession({
      walletId: values.walletId,
      userId: null,
      userName: values.userName?.trim() || `Wallet #${values.walletId}`,
      email: null,
    });
    setBanner({ kind: 'success', text: `Attached to wallet #${values.walletId}.` });
    setTimeout(() => navigate('/'), 350);
    setBusy(null);
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-semibold text-slate-900">Setup</h1>
        <p className="mt-1 text-sm text-slate-500">
          Register a new user (and open their wallet), or attach to an existing
          wallet by its numeric ID. This is a development-only selector — the
          backend has no login flow.
        </p>
      </div>

      {walletId != null && (
        <FeedbackBanner kind="info">
          Currently active: <strong>{userName ?? 'unknown'}</strong> · wallet #{walletId}
          <span className="ml-2">
            <Link to="/" className="underline">Go to dashboard</Link>
          </span>
        </FeedbackBanner>
      )}

      {banner && <FeedbackBanner kind={banner.kind} message={banner.text} />}

      <div className="grid md:grid-cols-2 gap-4">
        <section className="card p-6">
          <div className="flex items-center gap-2 text-slate-900">
            <UserPlus size={18} />
            <h2 className="text-lg font-semibold">Register a new user</h2>
          </div>
          <p className="mt-1 text-xs text-slate-500">
            The demo maps <code>password</code> directly into
            <code> passwordHash</code>. Do not use real credentials.
          </p>
          <form onSubmit={signup.handleSubmit(onSignup)} className="mt-4 space-y-3">
            <Field label="Name" error={signup.formState.errors.name?.message}>
              <input className="input" {...signup.register('name')} placeholder="Asha Verma" />
            </Field>
            <Field label="Email" error={signup.formState.errors.email?.message}>
              <input className="input" {...signup.register('email')} placeholder="asha@example.com" />
            </Field>
            <Field label="Password (demo)" error={signup.formState.errors.password?.message}>
              <input
                className="input"
                type="password"
                {...signup.register('password')}
                placeholder="not real credentials"
              />
            </Field>
            <Field label="Phone" error={signup.formState.errors.phone?.message}>
              <input className="input" {...signup.register('phone')} placeholder="9000000123" />
            </Field>
            <button type="submit" className="btn-primary w-full" disabled={busy !== null}>
              {busy === 'signup' && <Loader2 className="animate-spin" size={16} />}
              Create user + wallet
            </button>
          </form>
        </section>

        <section className="card p-6">
          <div className="flex items-center gap-2 text-slate-900">
            <Building2 size={18} />
            <h2 className="text-lg font-semibold">Attach existing wallet</h2>
          </div>
          <p className="mt-1 text-xs text-slate-500">
            <code>data.sql</code> seeds wallets 101 (Rahul), 102 (Amit), 103
            (Priya). Enter any existing wallet ID to browse its balance and
            history.
          </p>
          <form onSubmit={attach.handleSubmit(onAttach)} className="mt-4 space-y-3">
            <Field label="Wallet ID" error={attach.formState.errors.walletId?.message}>
              <input
                className="input"
                type="number"
                {...attach.register('walletId')}
                placeholder="101"
              />
            </Field>
            <Field
              label="Display name (optional)"
              error={attach.formState.errors.userName?.message}
            >
              <input className="input" {...attach.register('userName')} placeholder="Rahul" />
            </Field>
            <button type="submit" className="btn-primary w-full" disabled={busy !== null}>
              {busy === 'attach' ? <Loader2 className="animate-spin" size={16} /> : <WalletIcon size={16} />}
              Attach wallet
            </button>
          </form>
          <p className="mt-3 text-[11px] text-slate-400">
            This flow does not authenticate you. Anyone who knows a wallet ID
            can browse that wallet in this demo. Never expose this app beyond
            localhost.
          </p>
        </section>
      </div>
    </div>
  );
}

function Field({
  label,
  error,
  children,
}: {
  label: string;
  error?: string;
  children: React.ReactNode;
}) {
  return (
    <div>
      <label className="label">{label}</label>
      {children}
      {error && <p className="text-xs text-red-600 mt-1">{error}</p>}
    </div>
  );
}

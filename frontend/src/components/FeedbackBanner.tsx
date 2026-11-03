import type { ReactNode } from 'react';

interface Props {
  kind?: 'success' | 'error' | 'info';
  message?: string | null;
  children?: ReactNode;
}

/** Small inline banner used after actions (deposit, withdraw, transfer, etc.). */
export default function FeedbackBanner({ kind = 'info', message, children }: Props) {
  if (!message && !children) return null;
  const palette =
    kind === 'success'
      ? 'bg-emerald-50 text-emerald-800 border-emerald-200'
      : kind === 'error'
        ? 'bg-red-50 text-red-800 border-red-200'
        : 'bg-brand-50 text-brand-800 border-brand-200';
  return (
    <div
      role="status"
      aria-live="polite"
      className={`rounded-lg border px-4 py-3 text-sm ${palette}`}
    >
      {message}
      {children}
    </div>
  );
}

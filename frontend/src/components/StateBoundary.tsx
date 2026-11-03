import { AlertTriangle, Inbox, Loader2, RefreshCw } from 'lucide-react';
import type { ReactNode } from 'react';

interface Props {
  loading?: boolean;
  error?: string | null;
  isEmpty?: boolean;
  emptyMessage?: string;
  onRetry?: () => void;
  children: ReactNode;
}

/**
 * Reusable wrapper that renders loading, error-with-retry, empty, or content.
 * Keeps every page's async-state handling consistent.
 */
export default function StateBoundary({
  loading,
  error,
  isEmpty,
  emptyMessage = 'Nothing to show yet.',
  onRetry,
  children,
}: Props) {
  if (loading) {
    return (
      <div className="card p-10 grid place-items-center text-slate-500">
        <Loader2 className="animate-spin" size={28} />
        <p className="mt-3 text-sm">Loading…</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="card p-8 text-center">
        <div className="mx-auto h-12 w-12 rounded-full bg-red-50 text-red-600 grid place-items-center">
          <AlertTriangle size={22} />
        </div>
        <h3 className="mt-3 text-base font-semibold text-slate-900">Something went wrong</h3>
        <p className="mt-1 text-sm text-slate-600">{error}</p>
        {onRetry && (
          <button type="button" onClick={onRetry} className="btn-primary mt-4">
            <RefreshCw size={16} /> Retry
          </button>
        )}
      </div>
    );
  }

  if (isEmpty) {
    return (
      <div className="card p-10 text-center text-slate-500">
        <div className="mx-auto h-12 w-12 rounded-full bg-slate-100 text-slate-400 grid place-items-center">
          <Inbox size={22} />
        </div>
        <p className="mt-3 text-sm">{emptyMessage}</p>
      </div>
    );
  }

  return <>{children}</>;
}

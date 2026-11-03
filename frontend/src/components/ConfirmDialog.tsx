import { Loader2 } from 'lucide-react';
import type { ReactNode } from 'react';

interface Props {
  open: boolean;
  title: string;
  description?: ReactNode;
  confirmLabel?: string;
  cancelLabel?: string;
  busy?: boolean;
  onConfirm: () => void;
  onCancel: () => void;
}

/**
 * Simple modal used for money-transfer confirmation. Prevents accidental
 * submissions by forcing an explicit user action before the API call fires.
 */
export default function ConfirmDialog({
  open,
  title,
  description,
  confirmLabel = 'Confirm',
  cancelLabel = 'Cancel',
  busy,
  onConfirm,
  onCancel,
}: Props) {
  if (!open) return null;
  return (
    <div
      role="dialog"
      aria-modal="true"
      aria-labelledby="confirm-title"
      className="fixed inset-0 z-50 grid place-items-center bg-slate-900/40 p-4"
      onClick={() => !busy && onCancel()}
    >
      <div
        className="card w-full max-w-md p-6"
        onClick={(e) => e.stopPropagation()}
      >
        <h2 id="confirm-title" className="text-lg font-semibold text-slate-900">
          {title}
        </h2>
        {description && <div className="mt-3 text-sm text-slate-600">{description}</div>}
        <div className="mt-6 flex justify-end gap-2">
          <button type="button" className="btn-secondary" onClick={onCancel} disabled={busy}>
            {cancelLabel}
          </button>
          <button type="button" className="btn-primary" onClick={onConfirm} disabled={busy}>
            {busy && <Loader2 className="animate-spin" size={16} />}
            {confirmLabel}
          </button>
        </div>
      </div>
    </div>
  );
}

import type { LucideIcon } from 'lucide-react';
import type { ReactNode } from 'react';

interface Props {
  label: string;
  value: ReactNode;
  icon: LucideIcon;
  tone?: 'brand' | 'emerald' | 'rose' | 'slate';
  hint?: string;
}

const TONE: Record<string, string> = {
  brand: 'bg-brand-50 text-brand-700',
  emerald: 'bg-emerald-50 text-emerald-700',
  rose: 'bg-red-50 text-red-700',
  slate: 'bg-slate-100 text-slate-700',
};

export default function StatCard({ label, value, icon: Icon, tone = 'brand', hint }: Props) {
  return (
    <div className="card p-5 flex items-start gap-4">
      <div className={`h-10 w-10 rounded-xl grid place-items-center ${TONE[tone]}`}>
        <Icon size={18} />
      </div>
      <div className="min-w-0">
        <div className="text-xs uppercase tracking-wide text-slate-500">{label}</div>
        <div className="mt-1 text-lg font-semibold text-slate-900 truncate">{value}</div>
        {hint && <div className="text-xs text-slate-500 mt-0.5">{hint}</div>}
      </div>
    </div>
  );
}

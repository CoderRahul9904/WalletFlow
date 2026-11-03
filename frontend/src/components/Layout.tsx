import { NavLink, Outlet, useNavigate } from 'react-router-dom';
import {
  ArrowDownLeft,
  ArrowUpRight,
  CreditCard,
  History as HistoryIcon,
  LogOut,
  RefreshCw,
  ScanFace,
  Wallet as WalletIcon,
  LayoutDashboard,
} from 'lucide-react';
import { useSession } from '../context/SessionContext';

const NAV_ITEMS = [
  { to: '/', label: 'Dashboard', icon: LayoutDashboard, end: true },
  { to: '/balance', label: 'Balance', icon: WalletIcon },
  { to: '/deposit', label: 'Deposit', icon: ArrowDownLeft },
  { to: '/withdraw', label: 'Withdraw', icon: ArrowUpRight },
  { to: '/transfer', label: 'Transfer', icon: RefreshCw },
  { to: '/history', label: 'History', icon: HistoryIcon },
  { to: '/setup', label: 'Setup / Switch', icon: ScanFace },
];

export default function Layout() {
  const { walletId, userName, isReady, clearSession } = useSession();
  const navigate = useNavigate();

  return (
    <div className="min-h-screen flex">
      <aside className="hidden md:flex md:w-64 md:flex-col bg-white border-r border-slate-200 sticky top-0 h-screen">
        <div className="px-5 py-6 border-b border-slate-100">
          <div className="flex items-center gap-2">
            <div className="h-9 w-9 rounded-xl bg-brand-600 text-white grid place-items-center">
              <CreditCard size={18} />
            </div>
            <div>
              <div className="font-semibold text-slate-900 leading-tight">WalletFlow</div>
              <div className="text-xs text-slate-500">Digital wallet demo</div>
            </div>
          </div>
        </div>
        <nav className="flex-1 px-3 py-4 space-y-1">
          {NAV_ITEMS.map((item) => {
            const Icon = item.icon;
            return (
              <NavLink
                key={item.to}
                to={item.to}
                end={item.end}
                className={({ isActive }) =>
                  [
                    'flex items-center gap-3 px-3 py-2 rounded-lg text-sm font-medium transition',
                    isActive
                      ? 'bg-brand-50 text-brand-700'
                      : 'text-slate-600 hover:bg-slate-50 hover:text-slate-900',
                  ].join(' ')
                }
              >
                <Icon size={16} />
                <span>{item.label}</span>
              </NavLink>
            );
          })}
        </nav>
        <div className="p-4 border-t border-slate-100 text-xs text-slate-500">
          <div className="font-medium text-slate-700 truncate">{userName ?? 'No user'}</div>
          <div>Wallet #{walletId ?? '—'}</div>
          {isReady && (
            <button
              type="button"
              onClick={() => {
                clearSession();
                navigate('/setup');
              }}
              className="mt-2 inline-flex items-center gap-1 text-brand-700 hover:underline"
            >
              <LogOut size={14} /> Switch wallet
            </button>
          )}
        </div>
      </aside>

      <div className="flex-1 flex flex-col min-w-0">
        <header className="md:hidden sticky top-0 z-10 bg-white border-b border-slate-200 px-4 py-3 flex items-center justify-between">
          <div className="flex items-center gap-2">
            <div className="h-8 w-8 rounded-lg bg-brand-600 text-white grid place-items-center">
              <CreditCard size={16} />
            </div>
            <span className="font-semibold text-slate-900">WalletFlow</span>
          </div>
          <span className="text-xs text-slate-500">Wallet #{walletId ?? '—'}</span>
        </header>

        <main className="flex-1 p-4 md:p-8 max-w-6xl w-full mx-auto">
          <Outlet />
        </main>

        <nav className="md:hidden sticky bottom-0 z-10 bg-white border-t border-slate-200 grid grid-cols-6 text-[10px]">
          {NAV_ITEMS.slice(0, 6).map((item) => {
            const Icon = item.icon;
            return (
              <NavLink
                key={item.to}
                to={item.to}
                end={item.end}
                className={({ isActive }) =>
                  [
                    'flex flex-col items-center justify-center gap-0.5 py-2',
                    isActive ? 'text-brand-700' : 'text-slate-500',
                  ].join(' ')
                }
              >
                <Icon size={16} />
                <span>{item.label}</span>
              </NavLink>
            );
          })}
        </nav>
      </div>
    </div>
  );
}

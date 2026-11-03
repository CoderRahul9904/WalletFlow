import { Navigate, Route, Routes } from 'react-router-dom';
import Layout from './components/Layout';
import { useSession } from './context/SessionContext';
import Balance from './pages/Balance';
import Dashboard from './pages/Dashboard';
import Deposit from './pages/Deposit';
import History from './pages/History';
import Setup from './pages/Setup';
import Transfer from './pages/Transfer';
import Withdraw from './pages/Withdraw';

/** Redirects to /setup when no wallet has been chosen yet. */
function RequireWallet({ children }: { children: React.ReactNode }) {
  const { isReady } = useSession();
  if (!isReady) return <Navigate to="/setup" replace />;
  return <>{children}</>;
}

export default function App() {
  return (
    <Routes>
      <Route element={<Layout />}>
        <Route path="/setup" element={<Setup />} />
        <Route
          path="/"
          element={
            <RequireWallet>
              <Dashboard />
            </RequireWallet>
          }
        />
        <Route
          path="/balance"
          element={
            <RequireWallet>
              <Balance />
            </RequireWallet>
          }
        />
        <Route
          path="/deposit"
          element={
            <RequireWallet>
              <Deposit />
            </RequireWallet>
          }
        />
        <Route
          path="/withdraw"
          element={
            <RequireWallet>
              <Withdraw />
            </RequireWallet>
          }
        />
        <Route
          path="/transfer"
          element={
            <RequireWallet>
              <Transfer />
            </RequireWallet>
          }
        />
        <Route
          path="/history"
          element={
            <RequireWallet>
              <History />
            </RequireWallet>
          }
        />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Route>
    </Routes>
  );
}

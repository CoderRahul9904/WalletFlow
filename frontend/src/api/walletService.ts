import { apiClient, normaliseError } from './client';
import type {
  UserBalance,
  UserDepositResponse,
  UserWallet,
  UserWithDrawResponse,
} from './types';

/**
 * The backend expects `@RequestBody BigDecimal` for deposit/withdraw, so we
 * send a **raw JSON number** as the request body (NOT an object wrapper).
 */

/** POST /api/v1/wallets/users/{userId} */
export async function createWallet(userId: number): Promise<UserWallet> {
  try {
    const { data } = await apiClient.post<UserWallet>(`/api/v1/wallets/users/${userId}`);
    return data;
  } catch (err) {
    throw normaliseError(err);
  }
}

/** GET /api/v1/wallets/{walletId}/balance */
export async function getBalance(walletId: number): Promise<UserBalance> {
  try {
    const { data } = await apiClient.get<UserBalance>(`/api/v1/wallets/${walletId}/balance`);
    return data;
  } catch (err) {
    throw normaliseError(err);
  }
}

/** POST /api/v1/wallets/{walletId}/deposit  body = raw BigDecimal number */
export async function deposit(walletId: number, amount: number): Promise<UserDepositResponse> {
  try {
    const { data } = await apiClient.post<UserDepositResponse>(
      `/api/v1/wallets/${walletId}/deposit`,
      amount,
    );
    return data;
  } catch (err) {
    throw normaliseError(err);
  }
}

/** POST /api/v1/wallets/{walletId}/withdraw  body = raw BigDecimal number */
export async function withdraw(walletId: number, amount: number): Promise<UserWithDrawResponse> {
  try {
    const { data } = await apiClient.post<UserWithDrawResponse>(
      `/api/v1/wallets/${walletId}/withdraw`,
      amount,
    );
    return data;
  } catch (err) {
    throw normaliseError(err);
  }
}

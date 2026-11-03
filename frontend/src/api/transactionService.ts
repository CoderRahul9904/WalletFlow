import { apiClient, normaliseError } from './client';
import type {
  UserTransferMoneyRequest,
  UserTransferMoneyResponse,
  UserTransactionHistoryResponse,
} from './types';

/** POST /api/v1/transactions/transfer/{senderWalletId} */
export async function transferMoney(
  senderWalletId: number,
  payload: UserTransferMoneyRequest,
): Promise<UserTransferMoneyResponse> {
  try {
    const { data } = await apiClient.post<UserTransferMoneyResponse>(
      `/api/v1/transactions/transfer/${senderWalletId}`,
      payload,
    );
    return data;
  } catch (err) {
    throw normaliseError(err);
  }
}

/** POST /api/v1/transactions/history/{walletId}  (yes — POST, per existing controller mapping) */
export async function getTransactionHistory(
  walletId: number,
): Promise<UserTransactionHistoryResponse[]> {
  try {
    const { data } = await apiClient.post<UserTransactionHistoryResponse[]>(
      `/api/v1/transactions/history/${walletId}`,
    );
    return data;
  } catch (err) {
    throw normaliseError(err);
  }
}

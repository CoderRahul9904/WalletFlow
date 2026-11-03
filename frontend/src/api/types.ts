/**
 * TypeScript interfaces mirroring the exact backend request/response DTOs
 * discovered in Phase 1 (see docs/API_INVENTORY.md). Field names match the
 * Java DTOs so no manual mapping is required.
 */

// ---------- Enums (mirror Java enums, serialized as strings) ----------
export type TransactionType = 'DEPOSIT' | 'TRANSFER' | 'WITHDRAWAL';
export type TransactionStatus = 'SUCCESS' | 'FAILED' | 'PENDING';
export type WalletMoneyFlowType = 'INCOMING' | 'OUTGOING';

// ---------- User ----------
export interface CreateUserRequest {
  name: string;
  email: string;
  password: string;
  phone: string;
}

export interface SignUpResponse {
  id: number;
  name: string;
  email: string;
}

export interface RequestUser {
  id: number;
  name: string;
  email: string;
  phone: string;
}

// ---------- Wallet ----------
export interface UserWallet {
  userName: string;
  email: string;
  walletId: number;
  balance: number;
  currency: string;
}

export interface UserBalance {
  id: number;
  balance: number;
  currency: string;
}

export interface UserDepositResponse {
  name: string;
  totalAmount: number;
  depositAmount: number;
  message: string;
}

export interface UserWithDrawResponse {
  name: string;
  totalAmount: number;
  withDrawAmount: number;
  message: string;
}

// ---------- Transactions ----------
export interface UserTransferMoneyRequest {
  receiverWalletId: number;
  amount: number;
}

export interface UserTransferMoneyResponse {
  senderName: string;
  receiverName: string;
  transferAmount: number;
  message: string;
}

export interface UserTransactionHistoryResponse {
  ownerName: string;
  counterPartyName: string;
  amount: number;
  transactionType: TransactionType;
  transactionStatus: TransactionStatus;
  walletMoneyFlowType: WalletMoneyFlowType;
  transactionTime: string; // ISO LocalDateTime
}

// ---------- Consistent error body returned by GlobalExceptionHandler ----------
export interface ApiErrorResponse {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
}

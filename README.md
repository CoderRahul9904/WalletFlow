# WalletFlow — Backend API Inventory

> Derived strictly from reading the source code (controllers, services, repositories,
> entities, DTOs, mappers, enums, `application.properties`). No endpoints were guessed.

## Runtime / Configuration facts

| Item | Value | Source |
|------|-------|--------|
| Server port | **8081** | `application.properties` `server.port=8081` |
| Context path | none (root) | no `server.servlet.context-path` |
| Database | PostgreSQL `jdbc:postgresql://localhost:5432/walletflow` | `application.properties` |
| DDL strategy | `create` (`spring.jpa.hibernate.ddl-auto=create`) | `application.properties` |
| Seed data | `data.sql` executed on startup (`spring.sql.init.mode=always`) | `application.properties`, `data.sql` |
| Currency default | `INR` | `Wallet.currency` |
| Authentication | **None implemented** (no Spring Security dependency) | `pom.xml` |
| CORS | **Not configured** (must be added for local frontend) | no config class present |
| Global error handling | **Not present** (raw `RuntimeException`/`IllegalArgumentException` → HTTP 500) | no `@ControllerAdvice` |

## Domain model relationships

- `User (1) ── (1) Wallet` — `Wallet.user` is `@OneToOne(LAZY)`, `user_id` unique & not-null.
- `Wallet (1) ── (N) WalletTransaction` — via `sender_wallet_id` (nullable) and `receiver_wallet_id` (nullable), both `@ManyToOne(LAZY)`.
  - `DEPOSIT`  → `receiverWallet` set, `senderWallet` null.
  - `WITHDRAWAL` → `senderWallet` set, `receiverWallet` null.
  - `TRANSFER` → both set.
- Enums: `TransactionType { DEPOSIT, TRANSFER, WITHDRAWAL }`, `TransactionStatus { SUCCESS, FAILED, PENDING }`, `WalletMoneyFlowType { INCOMING, OUTGOING }`.

Seed IDs available in `data.sql`: users `1,2,3`; wallets `101,102,103`; transactions `1..7`.

---

## A. User APIs — `UserController` (`/api/v1/users`)

### A1. Create user
- **POST** `/api/v1/users`
- Request body: `CreateUserRequest`
  ```json
  { "name": "string", "email": "string", "password": "string", "phone": "string" }
  ```
- Response: `SignUpResponse` — `{ "name": "string" }`, status 200 (intended).
- Validation (existing): none at DTO/controller level. `email` unique & not-null; `passwordHash` not-null.
- **Confirmed defect:** `CreateUserRequest.password` is NOT mapped to `User.passwordHash` (name mismatch in MapStruct). `passwordHash` remains null → NOT NULL violation → HTTP 500 on every create. (See Bug B-1 in test report.)

### A2. Get user by id
- **GET** `/api/v1/users/{id}`
- Path var: `id` (Long)
- Response: `RequestUser` — `{ "name": "string", "email": "string", "phone": "string" }` (note: no `id`, no password), 200.
- Error: unknown id → `RuntimeException` → currently 500 (should be 404).

### A3. Get user by email
- **GET** `/api/v1/users?email={email}`
- Query param: `email` (required String)
- Response: `RequestUser` (same shape), 200.
- Error: unknown email → `RuntimeException` → currently 500 (should be 404). Missing param → 400.

---

## B. Wallet APIs — `WalletController` (`/api/v1/wallets`)

### B1. Create wallet for a user
- **POST** `/api/v1/wallets/users/{id}`
- Path var: `id` = user id. No body.
- Response: `UserWallet` — `{ "userName", "email", "walletId", "balance", "currency" }`, 200.
- Business rules (existing): user must exist (else `RuntimeException`); duplicate wallet per user blocked via `existsByUserId` (`RuntimeException`). New wallet balance defaults to `0`, currency `INR`.
- Errors currently surface as 500.

### B2. Get balance
- **GET** `/api/v1/wallets/{id}/balance`
- Path var: `id` = wallet id.
- Response: `UserBalance` — `{ "id", "balance", "currency" }`, 200.
- Error: invalid wallet id → `RuntimeException` → currently 500 (should be 404).

### B3. Deposit
- **POST** `/api/v1/wallets/{id}/deposit`
- Path var: `id` = wallet id.
- Request body: **a raw JSON number** deserialized to `BigDecimal` (e.g. `500` or `500.00`), `Content-Type: application/json`. NOT an object.
- Response: `UserDepositResponse` — `{ "name", "totalAmount", "depositAmount", "message" }`, 200.
- Validation (existing): rejects null / amount ≤ 0 via `IllegalArgumentException` ("Deposit amount must be greater than zero"). Wallet must exist. Creates a `DEPOSIT` transaction (`receiverWallet` set), updates balance.

### B4. Withdraw
- **POST** `/api/v1/wallets/{id}/withdraw`
- Path var: `id` = wallet id.
- Request body: raw JSON number → `BigDecimal`.
- Response: `UserWithDrawResponse` — `{ "name", "totalAmount", "withDrawAmount", "message" }`, 200.
- Validation (existing): rejects amount > balance (`IllegalArgumentException`). Wallet must exist. Creates `WITHDRAWAL` transaction (`senderWallet` set).
- **Confirmed defects:** no positive/null check → negative withdrawal INCREASES balance; null → NPE (500). (Bug B-3.)

---

## C. Money Transfer APIs — `WalletTransactionController` (`/api/v1/transactions`)

### C1. Transfer money
- **POST** `/api/v1/transactions/transfer/{id}`
- Path var: `id` = **sender** wallet id.
- Request body: `UserTransferMoneyRequest` — `{ "receiverWalletId": number, "amount": number }`.
- Response: `UserTransferMoneyResponse` — `{ "senderName", "receiverName", "transferAmount", "message" }`, 200.
- Flow (existing): load sender → check amount ≤ sender balance → decrement sender → load receiver → increment receiver → save `TRANSFER` transaction (both wallets set). Method is `@Transactional`, so a failure after decrement rolls back.
- Errors: sender/receiver not found → `RuntimeException` (500); amount > balance → `IllegalArgumentException`.
- **Confirmed defects:** no positive/null amount check (negative reverses/inflates balances); self-transfer (senderId == receiverId) not blocked (net-zero, spurious record). (Bug B-4, B-5.)

### C2. Transaction history
- **POST** `/api/v1/transactions/history/{id}`  (note: uses POST, not GET)
- Path var: `id` = owner wallet id. No body.
- Response: `List<UserTransactionHistoryResponse>`:
  ```json
  [{ "ownerName", "counterPartyName", "amount",
     "transactionType", "transactionStatus",
     "walletMoneyFlowType", "transactionTime" }]
  ```
  - `DEPOSIT` → counterParty "Wallet Deposit", flow INCOMING.
  - `WITHDRAWAL` → counterParty "Cash Withdrawal", flow OUTGOING.
  - `TRANSFER` → counterParty = other party's user name; flow OUTGOING if owner is sender else INCOMING.
- Wallet must exist (else 500). Null-safe: DEPOSIT/WITHDRAWAL branches do not touch the null side.
- **Note:** response contains NO transaction `id` or `reference`. There is **no get-transaction-by-reference endpoint**.

---

## D. Endpoints that do NOT exist (must not be invented in the UI)

- No transaction detail / by-reference endpoint.
- No list-all-transactions / pagination / filtering endpoint.
- No login / authentication / token endpoint.
- No update/delete user or wallet endpoints.


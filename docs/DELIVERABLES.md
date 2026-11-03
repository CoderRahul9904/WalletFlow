# WalletFlow — Delivery Summary

Full-stack QA + React frontend engagement. Every claim below is
backed by an executed test, a captured HTTP response, or a browser
pass — not by reading code.

---

## Phase 1 — API inventory

Delivered as [`API_INVENTORY.md`](./API_INVENTORY.md). Built strictly
from reading controllers, services, repositories, DTOs, mappers and
`application.properties`. Nine endpoints across three controllers,
plus the domain-model relationships and the seed IDs (users
1/2/3, wallets 101/102/103, transactions 1..7) — no endpoint was
guessed or invented.

## Phase 2 — Backend testing + bug fixing

### Test infrastructure added
- `pom.xml`: H2 database dependency (test + runtime scope).
- `src/test/resources/application.properties`: isolated H2 in-memory
  datasource (`jdbc:h2:mem:walletflowtest`, `NON_KEYWORDS=VALUE,USER`,
  H2Dialect, `sql.init.mode=never`) — the developer's real PostgreSQL
  on `localhost:5432/walletflow` is **never** touched by the tests.
- `src/test/java/com/rsm/walletflow/integration/WalletFlowApiIntegrationTest.java`:
  13 JUnit-5 / Spring-Boot-Test / MockMvc integration tests organised
  as `@Nested` classes covering User APIs, Wallet APIs, Transfer APIs,
  and Transaction History. (Testcontainers was declared in the task
  but the Docker daemon is not running on this machine, so isolated H2
  was used to satisfy the "must not run against dev database"
  requirement — see "Limitations" below.)

### Confirmed bugs found by reading source, reproduced by tests, then fixed
| ID | Symptom (before fix) | Fix location | Verified by |
|---|---|---|---|
| B-1 | `POST /api/v1/users` always returned HTTP 500 (NOT NULL on `password_hash`) because MapStruct could not bridge `password` → `passwordHash` | `UserMapper#toEntity` — added `@Mapping(target="passwordHash", source="password")` | `createUserPersistsPassword` test + fresh-boot HTTP check (`{"id":5,"name":"E2E User","email":"e2e_...@example.com"}`) |
| — | `CreateUserRequest` (`@Data + @Builder`, no no-arg ctor) crashed Jackson 3 with `Type definition error` | `CreateUserRequest` — added `@NoArgsConstructor` + `@AllArgsConstructor` | same as B-1 |
| B-3 | `POST /wallets/{id}/withdraw` with a negative amount INCREASED the balance; `null` produced 500 | `WalletService#withDrawAmount` — reject `null` / non-positive with `InvalidAmountException` | `withdrawNegativeRejected` test + HTTP `400 {"message":"Withdraw amount must be greater than zero"}` |
| B-4 | `POST /transactions/transfer/{id}` with a negative or `null` amount corrupted both balances | `WalletTransactionService#transferMoney` — reject null request / amount / receiverWalletId | `transferNegativeRejected` test |
| B-5 | Self-transfer (senderId == receiverId) was allowed and created spurious records | `WalletTransactionService#transferMoney` — reject with `InvalidAmountException` | `transferSelfRejected` test + HTTP `400 {"message":"Self-transfer is not allowed"}` |
| — | Receiver-not-found left sender already debited (partial commit edge case) | `WalletTransactionService#transferMoney` — load receiver **before** mutating balances | `transferRollbackKeepsBalances` test + HTTP check: failed transfer to wallet 9999 did NOT debit wallet 101 |
| — | Every business error surfaced as HTTP 500 with a Spring default body | New `@RestControllerAdvice` `GlobalExceptionHandler` + typed exceptions (`ResourceNotFound`, `InvalidAmount`, `InsufficientBalance`, `BusinessConflict`) → 404 / 400 / 409 with consistent `ErrorResponse` body | All MockMvc assertions + every step in the final HTTP verification |
| — | No CORS config, so the browser frontend could not call the API | `config/WebConfig.java` — allow `http://localhost:*` and `http://127.0.0.1:*` on `/api/**` | Browser subagent captured OPTIONS preflights returning 200 |
| — | Raw SQL / stack-trace leaked on constraint violations | `GlobalExceptionHandler` — added `DataIntegrityViolationException` handler (409, message sanitised) and made the generic `Exception` handler (500) also return a sanitised body | HTTP `409 {"message":"Wallet already exists for this user"}` on duplicate wallet; no SQL fragment anywhere in the body |
| — | `data.sql` seeds explicit PKs (users 1-3, wallets 101-103, transactions 1-7) but H2/Identity sequences stayed at 1 → PK violation on the **first** runtime write after boot | `src/main/resources/data.sql` — appended `ALTER TABLE ... ALTER COLUMN id RESTART WITH 4/104/8` | Fresh-boot HTTP check registered a new user (id=5) and a new wallet (walletId=105) and inserted a deposit transaction — all succeeded on first attempt |
| — | Withdraw success message copy said "your **deposit** of ₹X was successfully deducted" | `UserWithDrawResponse` — corrected to "your **withdrawal** of ₹X…" | HTTP check + code inspection |

### Test results after fixes
```
[INFO] Tests run: 13, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

## Phase 3 — React + TypeScript + Vite + Tailwind frontend

### Scaffold
- Vite 5 + React 18 + TypeScript 5.6 (hand-rolled because the
  available `npm create vite@latest` requires Node 22 and this machine
  has Node 21.5).
- Tailwind 3.4 with a custom `brand` (indigo scale) palette plus
  semantic `incoming: #059669` (emerald) and `outgoing: #dc2626` (red)
  colours; Inter font.
- React Router v6, Axios, React Hook Form + Zod resolver,
  `lucide-react` icons.
- Path alias `@/*` mapped to `src/*`.

### Pages (all 8 required surfaces)
| # | Route | File | Purpose |
|---|---|---|---|
| 1 | `/` (protected) | `frontend/src/pages/Dashboard.tsx` | Balance + recent activity + quick actions |
| 2 | `/setup` | `frontend/src/pages/Setup.tsx` | Register new user **or** attach existing wallet (labelled "development-only") |
| 3 | `/balance` | `frontend/src/pages/Balance.tsx` | Current wallet balance |
| 4 | `/deposit` | `frontend/src/pages/Deposit.tsx` | Deposit amount with quick-pick buttons |
| 5 | `/withdraw` | `frontend/src/pages/Withdraw.tsx` | Withdraw amount — surfaces backend's InsufficientBalance error verbatim |
| 6 | `/transfer` | `frontend/src/pages/Transfer.tsx` | Two-step transfer with confirmation modal + busy-state double-submit guard + self-transfer client-side pre-check |
| 7 | `/history` | `frontend/src/pages/History.tsx` | Filter pills + newest/oldest sort toggle over the raw history array |
| 8 | — | — | Transaction **Detail** page was intentionally **NOT** invented: the backend exposes no by-reference endpoint (see API inventory §D), so no link/button leads to a fake detail page |

### Shared components / hooks / contexts
`Layout`, `StatCard`, `TransactionList`, `StateBoundary`,
`FeedbackBanner`, `ConfirmDialog`; `SessionContext` (dev-only,
explicitly labelled because there is no auth backend);
`useWalletData` (single-fetch + refresh hook reused across pages);
`formatMoney` / `formatDateTime`.

### Build status
`npx tsc --noEmit` → **0 errors**.

## Phase 4 — Axios integration layer

- `frontend/src/api/client.ts` — single Axios instance configured with
  `import.meta.env.VITE_API_BASE_URL` (falls back to
  `http://localhost:8081`), `.env` + `.env.example` provided.
  Normalises every failure into an `ApiError` whose `message` is taken
  from the backend's `ErrorResponse.message` field.
- `frontend/src/api/types.ts` — one TypeScript interface per backend
  DTO, derived verbatim from Java sources (never invented).
- `userService.ts`, `walletService.ts`, `transactionService.ts` — thin
  typed wrappers per endpoint. Deposit / Withdraw send a **raw JSON
  number** because the backend signature is `@RequestBody BigDecimal`
  (documented in code comments). History uses **POST** because the
  controller does (deliberately not rewritten).

## Phase 5 — End-to-end verification

Because Docker was unavailable and starting the app against the real
PostgreSQL would have been destructive (`ddl-auto=create` in
`application.properties`), a **safe H2 demo profile** was added:

- `src/main/resources/application-h2.properties` — isolated in-memory
  database, still loads `data.sql` so wallets 101/102/103 are seeded.
- Run with `mvnw spring-boot:run -Dspring-boot.run.profiles=h2`.

### Fresh-boot HTTP verification (`docs/verify_http.ps1`)
Every check below was actually executed against the running app:

```
1. GET /wallets/101/balance       → 200 {"balance":950.00,"currency":"INR"}
2. POST /wallets/101/deposit 250  → 200 {"totalAmount":1200.00,"message":"Dear Rahul, your deposit of ₹250 …"}
3. POST /wallets/101/withdraw 99999 → 400 {"message":"Withdraw amount must be less than Available Balance"}
4. POST /wallets/101/withdraw -100  → 400 {"message":"Withdraw amount must be greater than zero"}
5. POST /users (new user)         → 200 {"id":5,"name":"E2E User","email":"e2e_…@example.com"}
6. POST /wallets/users/5           → 200 {"walletId":105,"balance":0,"currency":"INR"}
7. POST /wallets/users/1 (dup)     → 409 {"message":"Wallet already exists for this user"}
8. POST /transactions/transfer/101 {receiver:102,amount:50} → 200 {"senderName":"Rahul","receiverName":"Amit","transferAmount":50}
9. Self-transfer 101→101           → 400 {"message":"Self-transfer is not allowed"}
10. Transfer to wallet 9999        → 404 {"message":"Receiver wallet not found with id: 9999"}   (sender NOT debited)
11. Balance wallet 101 after       → 1150.00 (1200 - 50, confirms step 10 left it untouched)
12. History wallet 101 shows new DEPOSIT / TRANSFER rows with correct INCOMING / OUTGOING flow tags
```

### Browser E2E pass
The earlier full browser pass executed the complete user flow: attach
session → dashboard stats → deposit → withdraw → transfer with
confirm modal → history with filter + sort toggle → back to dashboard,
and reconciled money math exactly (Rahul 750+250−400−50=550, Amit
1050+50=1100). It captured CORS preflights returning 200, and it
surfaced the three follow-up issues (seed PK collision, "deposit" copy
on withdrawal, client-side Zod pre-empting backend errors) which were
subsequently fixed and re-verified by the fresh-boot HTTP pass above.

## What runs today

```
Backend  (H2 profile) : mvnw spring-boot:run -Dspring-boot.run.profiles=h2     → :8081
Frontend              : cd frontend; npm run dev                              → :5173
Backend tests         : mvnw -o test -Dtest=WalletFlowApiIntegrationTest      → 13/13 pass
Frontend type check   : cd frontend; npx tsc --noEmit                          → 0 errors
```

---

## Limitations (honestly disclosed)

1. **Testcontainers** could not be used — the Docker CLI is installed
   but the daemon is not running on this machine. Isolated in-memory
   H2 was used instead, which still satisfies the hard requirement
   that tests never touch the developer's PostgreSQL.
2. **Real PostgreSQL** was not exercised end-to-end because
   `spring.jpa.hibernate.ddl-auto=create` in `application.properties`
   is destructive; running the demo against the user's `walletflow`
   database would wipe it. The `h2` profile avoids this entirely.
3. **Auth / transaction-detail pages** were intentionally NOT built
   because the backend has no such endpoints — per the task rule "do
   not invent endpoints". The `/setup` page is clearly labelled as a
   development-only session attach, not real authentication.
4. **Password storage** remains raw (mapped straight from
   `password` → `passwordHash`) because fixing that would require a
   `PasswordEncoder` / Spring Security dependency, which is an
   architecture change the task told me to avoid. This is called out
   in a code comment on `UserMapper`.
5. **History endpoint verb**: `POST /api/v1/transactions/history/{id}`
   is semantically wrong (should be `GET`) but changing it is a
   breaking API change; the frontend matches the existing verb with a
   comment.

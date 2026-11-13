---
documentId: WF-POL-006
documentName: Transaction Limits Policy
version: "1.0"
status: DRAFT
category: TRANSACTION_LIMITS
sourceType: INTERNAL_POLICY
effectiveDate: PENDING_APPROVAL
owner: WalletFlow Product & Engineering Team
jurisdiction: India
---

# Transaction Limits Policy

## 1. Purpose
This policy establishes the fixed WalletFlow transaction thresholds for the demo wallet product. These limits are internal product rules that support safe behavior, predictable user experience, and controlled operational risk in the educational fintech simulation.

## 2. Scope and Applicability
This policy applies to all wallet users and all backend validation checks for:
- deposits
- withdrawals
- transfers
- historical transaction counting
- limit enforcement on wallet endpoints

These limits are fictional internal constraints for WalletFlow and should not be interpreted as licensed financial service terms or regulated payment rules.

## 3. Definitions
- Daily limit period: The time window from 00:00 IST to 23:59:59.999 IST.
- Successful transaction: A transaction that was accepted and committed by the backend.
- Failed transaction: A request rejected by validation or processing logic before successful completion.
- Aggregation: The sum of all successful transaction amounts for a wallet during the current day.

## 4. Fixed Product Limits
WalletFlow applies the following fixed limits to every user wallet:

1. Maximum wallet balance: ₹50,000
2. Maximum single transaction value: ₹10,000
3. Daily deposit limit: ₹20,000
4. Daily withdrawal limit: ₹10,000
5. Daily transfer limit: ₹20,000
6. Maximum 20 successful transactions per day, combined across all transaction types

## 5. Policy Statements
### 5.1 Balance limit
1. A wallet balance must never exceed ₹50,000 under normal operation.
2. A requested deposit or transfer that would exceed the maximum wallet balance must be rejected before balance mutation.

### 5.2 Single transaction cap
1. No single deposit, withdrawal, or transfer event may exceed ₹10,000.
2. If a request exceeds this value, the request fails and the transaction is rejected.

### 5.3 Daily limit aggregation
1. Daily deposit, withdrawal, and transfer limits are calculated across the current day in IST.
2. Daily totals are reset at 00:00 IST.
3. The system must enforce daily totals using backend logic, not just browser validation.
4. Transfers and withdrawals are counted against the relevant operational category and wallet-specific totals.

### 5.4 Daily totals and transaction count
1. WalletFlow allows a maximum of 20 successful transactions per wallet per day, combined across deposits, withdrawals, and transfers.
2. Failed transactions do not count toward the successful transaction count limit.
3. The daily count must be evaluated using successfully committed transactions only.

### 5.5 Midnight reset
1. At 00:00 IST, all daily counters reset to zero for the new day.
2. Daily totals must be recalculated from the start of the new period.

### 5.6 User-facing messages
1. User-facing errors must clearly state the relevant reason for rejection.
2. Rejection messages should communicate whether the request exceeds the maximum wallet balance, the single-transaction limit, or a daily limit.
3. Message wording must be consistent with the backend validation logic.

## 6. Roles and Responsibilities
### Product team
- Reviews and approves limit changes.
- Ensures user-facing messages align with actual backend enforcement.

### Engineering team
- Implements limit checks in backend services.
- Enforces aggregation and reset logic at 00:00 IST.
- Ensures failed transactions do not increment successful counts.

### Support team
- Uses limit rules to explain rejected requests and user issues.

## 7. Operational Procedures
1. WalletFlow validates each request before balance mutation.
2. If the amount exceeds a single-transaction limit, the request is rejected.
3. If the wallet or day would exceed a daily or balance cap, the request is rejected.
4. The same rules are used for deposit, withdrawal, and transfer endpoints.
5. Support teams may review transaction history to validate whether a request was rejected due to a limit.

## 8. Security and Audit Considerations
1. Daily limit checks must be backend-enforced.
2. Limit calculations must consider real transaction state and transaction timestamps.
3. Validation must prevent bypass by client-side manipulation.
4. Audit logs should preserve the transaction status, amount, timestamp, and reason for failure or success.

## 9. Exceptions and Escalation
Any change to the fixed limit values requires approval by the WalletFlow Product & Engineering Team. Exceptions for testing or demo scenarios must be explicitly documented and time-bound. Any repeated bypass or suspicious limit manipulation must be escalated immediately.

## 10. Related Policies
- WF-POL-001 Wallet Operations Policy
- WF-POL-002 Transaction Management Policy
- WF-POL-005 Refunds and Disputes Policy
- WF-POL-009 Finny AI Policy

## 11. Revision History
| Version | Date | Status | Summary |
| --- | --- | --- | --- |
| 1.0 | Pending approval | DRAFT | Initial WalletFlow transaction limit policy for the educational wallet simulation. |

## 12. Mandatory Note
These limits are fictional internal rules for WalletFlow educational use only. They do not confer legal status or regulatory relief. Any real-money product requires separate legal, compliance, and product review.

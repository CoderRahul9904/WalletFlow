---
documentId: WF-POL-007
documentName: Privacy Policy
version: "1.0"
status: DRAFT
category: PRIVACY
sourceType: INTERNAL_POLICY
effectiveDate: PENDING_APPROVAL
owner: WalletFlow Product & Engineering Team
jurisdiction: India
---

# Privacy Policy

## 1. Purpose
This policy defines the internal privacy expectations for WalletFlow. It is designed to ensure that the platform collects only necessary information, restricts access to user and transaction data, and handles user data responsibly in the educational simulation environment.

## 2. Scope and Applicability
This policy applies to:
- user account data and demo profiles
- wallet records and transaction history
- logs, metadata, and operational records
- AI interactions involving wallet and policy information
- support tools and internal review workflows

## 3. Definitions
- Personal information: Any information that could identify or be linked to a user.
- Financial data: Wallet balances, transaction history, amount values, references, and account associations.
- Data minimisation: Collecting and keeping only the data necessary for the defined product function.
- AI data usage: The processing of user prompts or wallet-related information through an AI service for operational, explanatory, or support assistance.

## 4. Policy Statements
### 4.1 Data minimisation
1. WalletFlow must collect only the data needed to operate the wallet simulation and support user experience.
2. Real identity documents, real bank data, and regulated customer onboarding fields are not part of the current demo design.
3. Product teams must review data collection demands against necessity and product purpose.

### 4.2 User data access
1. Only authorised personnel may access wallet or transaction data.
2. Support teams must access the minimum necessary information to resolve a complaint or conduct an investigation.
3. User data must not be shared broadly across teams without a need-to-know rationale.

### 4.3 Password handling and secrets
1. Passwords and authentication secrets must be stored and transmitted using secure controls.
2. Plaintext credentials must never be written to logs, code, or support notes.
3. Secrets must be stored in secure configuration or secret-management mechanisms.

### 4.4 Logging and retention
1. Application logs should preserve only the minimal information required for traceability and support.
2. Logs should avoid exposing secrets, raw credentials, or unrestricted personal information.
3. Data retention must follow the product’s operational needs and internal review requirements.

### 4.5 Third-party AI provider usage
1. WalletFlow may use an external AI provider for informational assistance, but the AI system must remain read-only with respect to transactions and account modification.
2. AI prompts should minimise unnecessary user data and avoid transmitting sensitive personal details unless directly required for a support workflow.
3. The product must not assume that third-party AI providers are regulated financial intermediaries or that their processing is compliant with a specific legal framework without review.

### 4.6 Privacy incidents
1. Any suspected data leak, unauthorised access, or accidental exposure of user data must be escalated promptly.
2. The incident must be reviewed, recorded, and actioned through the support and engineering process.
3. The privacy review should identify root cause, scope, and remediation steps.

## 5. Roles and Responsibilities
### Product team
- Reviews data collection and support workflows.
- Ensures product features remain aligned with the demo-only design.

### Engineering team
- Implements data-minimisation checks and secure storage practices.
- Restricts backend access and logs sensitive information appropriately.

### Support team
- Handles complaints with the minimum necessary data and avoids unnecessary disclosure.

## 6. Operational Procedures
1. Users are onboarded with demo-only profile data.
2. Support teams investigate only the necessary wallet or transaction data.
3. AI workflows should avoid unnecessary inclusion of sensitive or personal information.
4. Any suspected privacy issue triggers escalation and documentation.

## 7. Security and Audit Considerations
1. Access control must be maintained for all user, transaction, and support records.
2. Secret and environment management must avoid accidental exposure in source control.
3. Audit logs should support review without containing unnecessary sensitive data.

## 8. Exceptions and Escalation
Any exception involving broader personal data collection, retained account data, or use of third-party AI for sensitive operational tasks requires approval by the WalletFlow Product & Engineering Team and appropriate risk assessment before use.

## 9. Related Policies
- WF-POL-003 Customer Verification Policy
- WF-POL-004 Security Policy
- WF-POL-008 Customer Grievance Policy
- WF-POL-009 Finny AI Policy

## 10. Revision History
| Version | Date | Status | Summary |
| --- | --- | --- | --- |
| 1.0 | Pending approval | DRAFT | Initial WalletFlow privacy policy for the educational wallet application. |

## 11. Mandatory Note
This document reflects internal product and privacy expectations for the WalletFlow demo environment. It does not represent a legal determination of compliance with any Indian privacy law or other regulatory regime. Actual applicability depends on the real business model, data processing activities, and legal review.

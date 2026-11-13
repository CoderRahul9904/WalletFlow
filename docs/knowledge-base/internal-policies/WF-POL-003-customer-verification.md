---
documentId: WF-POL-003
documentName: Customer Verification Policy
version: "1.0"
status: DRAFT
category: CUSTOMER_VERIFICATION
sourceType: INTERNAL_POLICY
effectiveDate: PENDING_APPROVAL
owner: WalletFlow Product & Engineering Team
jurisdiction: India
---

# Customer Verification Policy

## 1. Purpose
This policy defines the fictional customer verification model for WalletFlow. The application is an educational fintech simulation and does not implement regulated customer identification or KYC workflows. The purpose of this policy is to clarify the demo identity model and to prevent the accidental collection or use of real personal identity data in the WalletFlow environment.

## 2. Scope and Applicability
This policy applies to:
- user registration in WalletFlow
- identity data fields used in demo flows
- support processes involving customer records
- engineering and product handling of user profile information

This policy applies to the demo system only. It is not a general exemption from customer due diligence in any real financial service.

## 3. Definitions
- Demo identity: A non-realistic or fictional identity used for product demonstration, education, and testing.
- Real identity documents: Government-issued identities, Aadhaar, PAN, bank account credentials, or other genuine personal identification materials.
- KYC: Customer identification and verification processes used in regulated financial services.

## 4. Policy Statements
### 4.1 No-KYC demo model
1. WalletFlow currently operates under a no-KYC demo model.
2. Users may be created with fictional names, demo identifiers, and non-sensitive test data.
3. No real Aadhaar, PAN, bank account numbers, or government identity documents are required or accepted.
4. The application must not request or store real identity documents from users.

### 4.2 Restrictions on data collection
1. Product teams must not introduce fields that request personal identity numbers, bank account details, or government-issued documents unless a separate, documented legal review is completed.
2. Data used for demo accounts must be fictional and clearly not tied to real persons.
3. If a user attempts to provide real identity data, the system must reject or sanitise the entry according to policy and internal review.

### 4.3 Demo information and support
1. Support and development teams may use demo names and sample identifiers in operational or educational contexts.
2. Any user data used in screenshots, recordings, or training material must be de-identified where appropriate.
3. The product must not imply that a user has been verified under any actual KYC law or regulatory regime.

### 4.4 Limitations
1. This policy is not a statement that WalletFlow is exempt from real regulatory obligations in a different commercial context.
2. If WalletFlow evolves into a real-money or regulated financial product, an independent legal and compliance assessment is required before collecting identity information or enabling actual financial operations.
3. The no-KYC model is a conscious educational design decision for the current simulator and must not be copied into a real financial product without review.

## 5. Roles and Responsibilities
### Product team
- Determines the user profile model for demo scenarios.
- Ensures fields and flows remain non-regulated and fictional.

### Engineering team
- Prevents storage of real identity documents.
- Applies validation to reject unsupported identity input.

### Support team
- Escalates if a user attempts to provide real identity information or asks for regulated verification requirements.

## 6. Operational Procedures
1. New user creation shall use fictional or demo-only identity values.
2. The system must not request real identity verification elements.
3. Support requests must be handled with demo user data only.
4. If an exception is needed for a testing scenario, the exception must be documented, time-bound, and internally approved.

## 7. Security and Audit Considerations
1. No real identity documents should be stored in application databases, backups, logs, or support tooling.
2. Database schema and forms must be reviewed to ensure no real-document fields are introduced without approval.
3. Security reviews must include checks for accidental collection of identity metadata.

## 8. Exceptions and Escalation
Any request to introduce real identity verification or actual customer onboarding must be escalated to the WalletFlow Product & Engineering Team and subject to legal and regulatory review before implementation.

## 9. Related Policies
- WF-POL-004 Security Policy
- WF-POL-007 Privacy Policy
- WF-POL-008 Customer Grievance Policy
- WF-FRAMEWORK-001 Business and Governance Overview

## 10. Revision History
| Version | Date | Status | Summary |
| --- | --- | --- | --- |
| 1.0 | Pending approval | DRAFT | Initial policy establishing the no-KYC demo model for WalletFlow. |

## 11. Mandatory Note
This policy reflects the current educational design choice for WalletFlow only. It does not imply that any real product is exempt from legal or regulatory obligations. Actual applicability depends on the actual business model, product activities, jurisdiction, and required regulatory authorisation.

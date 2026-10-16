-- ==========================================
-- USERS
-- ==========================================
INSERT INTO users
(id, name, email, password_hash, phone, created_at)
VALUES
    (1, 'Rahul', 'rahul@example.com', 'DEMO_HASH_RAHUL',
     '9000000001', CURRENT_TIMESTAMP),
    (2, 'Amit', 'amit@example.com', 'DEMO_HASH_AMIT',
     '9000000002', CURRENT_TIMESTAMP),
    (3, 'Priya', 'priya@example.com', 'DEMO_HASH_PRIYA',
     '9000000003', CURRENT_TIMESTAMP);


-- ==========================================
-- WALLETS
-- Balances reflect all transactions below
-- ==========================================
INSERT INTO wallets
(id, user_id, balance, currency, created_at)
VALUES
    (101, 1, 750.00, 'INR', CURRENT_TIMESTAMP),
    (102, 2, 1050.00, 'INR', CURRENT_TIMESTAMP),
    (103, 3, 800.00, 'INR', CURRENT_TIMESTAMP);


-- ==========================================
-- WALLET TRANSACTIONS
-- ==========================================

-- Initial deposits
INSERT INTO wallet_transactions
(id, sender_wallet_id, receiver_wallet_id,
 amount, type, status, reference, created_at)
VALUES
    (1, NULL, 101, 1000.00, 'DEPOSIT', 'SUCCESS',
     'WF-DEP-001', CURRENT_TIMESTAMP),

    (2, NULL, 102, 1000.00, 'DEPOSIT', 'SUCCESS',
     'WF-DEP-002', CURRENT_TIMESTAMP),

    (3, NULL, 103, 1000.00, 'DEPOSIT', 'SUCCESS',
     'WF-DEP-003', CURRENT_TIMESTAMP);


-- Rahul sends ₹100 to Amit
INSERT INTO wallet_transactions
(id, sender_wallet_id, receiver_wallet_id,
 amount, type, status, reference, created_at)
VALUES
    (4, 101, 102, 100.00, 'TRANSFER', 'SUCCESS',
     'WF-TRF-001', CURRENT_TIMESTAMP);


-- Rahul sends ₹200 to Priya
INSERT INTO wallet_transactions
(id, sender_wallet_id, receiver_wallet_id,
 amount, type, status, reference, created_at)
VALUES
    (5, 101, 103, 200.00, 'TRANSFER', 'SUCCESS',
     'WF-TRF-002', CURRENT_TIMESTAMP);


-- Amit sends ₹50 to Rahul
INSERT INTO wallet_transactions
(id, sender_wallet_id, receiver_wallet_id,
 amount, type, status, reference, created_at)
VALUES
    (6, 102, 101, 50.00, 'TRANSFER', 'SUCCESS',
     'WF-TRF-003', CURRENT_TIMESTAMP);


-- Priya sends ₹100 to Amit
INSERT INTO wallet_transactions
(id, sender_wallet_id, receiver_wallet_id,
 amount, type, status, reference, created_at)
VALUES
    (7, 103, 102, 100.00, 'TRANSFER', 'SUCCESS',
     'WF-TRF-004', CURRENT_TIMESTAMP);
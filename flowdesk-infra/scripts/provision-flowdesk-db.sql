-- Run on your existing Postgres (port 5432), e.g.:
--   docker exec -i electricity-ebilling-postgres psql -U ebilling -d postgres -v ON_ERROR_STOP=1 -f - < provision-flowdesk-db.sql

SELECT 'CREATE DATABASE flowdesk'
WHERE NOT EXISTS (SELECT 1 FROM pg_database WHERE datname = 'flowdesk')\gexec

DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'debezium_user') THEN
    CREATE ROLE debezium_user WITH LOGIN REPLICATION PASSWORD 'dbz_root_pass';
  ELSE
    ALTER ROLE debezium_user WITH LOGIN REPLICATION PASSWORD 'dbz_root_pass';
  END IF;
END
$$;

GRANT ALL PRIVILEGES ON DATABASE flowdesk TO debezium_user;

\connect flowdesk

CREATE SCHEMA IF NOT EXISTS payments AUTHORIZATION debezium_user;
GRANT ALL ON SCHEMA payments TO debezium_user;
ALTER ROLE debezium_user IN DATABASE flowdesk SET search_path TO payments, public;
ALTER DEFAULT PRIVILEGES IN SCHEMA payments GRANT ALL ON TABLES TO debezium_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA payments GRANT ALL ON SEQUENCES TO debezium_user;

CREATE TABLE IF NOT EXISTS payments.payment (
    payment_id            BIGSERIAL PRIMARY KEY,
    transaction_id        VARCHAR(255) NOT NULL UNIQUE,
    payment_status        VARCHAR(50),
    payment_type          VARCHAR(50),
    payment_io_type       VARCHAR(35),
    delivery_method       VARCHAR(50),
    entry_type            VARCHAR(6),
    channel               VARCHAR(50),
    business_unit         VARCHAR(255),
    department            VARCHAR(100),
    counter_party         VARCHAR(255),
    sender_name           VARCHAR(255),
    sender_amount         NUMERIC(18, 8),
    sender_currency       VARCHAR(3),
    receiver_name         VARCHAR(255),
    receiver_amount       NUMERIC(18, 8),
    receiver_currency     VARCHAR(3),
    payment_date          DATE,
    delivery_date         DATE,
    creation_date_time    TIMESTAMP,
    memo_post_status      VARCHAR(50),
    sender_account_number VARCHAR(35),
    receiver_account_number VARCHAR(35),
    external_ref          VARCHAR(255),
    payment_note          VARCHAR(250),
    operation_notes       VARCHAR(1000),
    return_status         VARCHAR(50),
    fee_amount            NUMERIC(18, 8),
    fee_currency          VARCHAR(3),
    book                  VARCHAR(255),
    cost_center           VARCHAR(100),
    customer_type         VARCHAR(45)
);

DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_publication WHERE pubname = 'flowdesk_publication') THEN
    CREATE PUBLICATION flowdesk_publication FOR TABLE payments.payment;
  END IF;
END
$$;

GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA payments TO debezium_user;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA payments TO debezium_user;

CREATE INDEX IF NOT EXISTS idx_payment_payment_date ON payments.payment (payment_date DESC);
CREATE INDEX IF NOT EXISTS idx_payment_payment_status ON payments.payment (payment_status);
CREATE INDEX IF NOT EXISTS idx_payment_sender_amount ON payments.payment (sender_amount);
CREATE INDEX IF NOT EXISTS idx_payment_creation_date_time ON payments.payment (creation_date_time DESC);

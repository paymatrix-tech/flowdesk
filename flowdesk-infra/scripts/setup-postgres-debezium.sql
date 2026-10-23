-- Optional: re-apply grants for debezium_user on an existing flowdesk DB.
-- Full create (DB, schema, table): use provision-flowdesk-db.sh instead.

DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'debezium_user') THEN
    CREATE ROLE debezium_user WITH LOGIN REPLICATION PASSWORD 'dbz_root_pass';
  ELSE
    ALTER ROLE debezium_user WITH LOGIN REPLICATION PASSWORD 'dbz_root_pass';
  END IF;
END
$$;

GRANT CONNECT ON DATABASE flowdesk TO debezium_user;
GRANT USAGE ON SCHEMA payments TO debezium_user;
GRANT SELECT ON ALL TABLES IN SCHEMA payments TO debezium_user;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA payments TO debezium_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA payments GRANT SELECT ON TABLES TO debezium_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA payments GRANT USAGE, SELECT ON SEQUENCES TO debezium_user;

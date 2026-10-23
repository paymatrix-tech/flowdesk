#!/usr/bin/env sh
set -euo pipefail

# Provisions database flowdesk + schema payments on existing Postgres (default: electricity-ebilling-postgres on 5432).
PG_CONTAINER="${PG_CONTAINER:-electricity-ebilling-postgres}"
PG_SUPERUSER="${PG_SUPERUSER:-ebilling}"

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

docker exec -i "$PG_CONTAINER" psql -U "$PG_SUPERUSER" -d postgres -v ON_ERROR_STOP=1 \
  < "$SCRIPT_DIR/provision-flowdesk-db.sql"

echo "Done. Flowdesk: database flowdesk, schema payments @ localhost:5432 (debezium_user / dbz_root_pass)"

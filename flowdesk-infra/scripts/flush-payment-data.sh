#!/usr/bin/env sh
# Wipes Flowdesk payment data in Postgres + Elasticsearch (local dev; API must be running).
set -euo pipefail

API_URL="${API_URL:-http://localhost:8080}"
SEED="${SEED:-0}"

echo "Flushing Postgres + Elasticsearch (seed=${SEED}; 0 = no demo rows)..."
if command -v python3 >/dev/null; then
    curl -sf -X POST "${API_URL}/api/v1/demo/payments/flush?seed=${SEED}" | python3 -m json.tool
else
    curl -sf -X POST "${API_URL}/api/v1/demo/payments/flush?seed=${SEED}"
    echo ""
fi
echo "Done."

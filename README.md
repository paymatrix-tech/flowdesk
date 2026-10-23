# Flowdesk

Payment operations console: blotter, Elasticsearch search, analytics cards, CSV export, and live SSE updates.

## Modules

| Path | Stack |
|------|--------|
| `flowdesk-api` | Spring Boot 4.1, Java 25 |
| `flowdesk-ui` | React 19, Vite |
| `flowdesk-infra` | Kafka, Debezium Connect, Elasticsearch |

## Quick start

```bash
npm run install:all
npm run up:docker          # Kafka, Debezium, Elasticsearch
./flowdesk-infra/scripts/provision-flowdesk-db.sh   # once: Postgres DB + schema
npm run dev                # API :8080 + UI :5173
```

- UI: http://localhost:5173  
- API: http://localhost:8080  
- Swagger: http://localhost:8080/docs/api  

`flowdesk-ui/.env` is committed with dev defaults (Vite proxy to `:8080`). Change `VITE_API_BASE_URL` only if the API is on another host.

## PostgreSQL

Shared Postgres on **localhost:5432**:

| Item | Value |
|------|--------|
| Database | `flowdesk` |
| Schema / table | `payments.payment` |
| App user | `debezium_user` / `dbz_root_pass` |

```bash
# Optional overrides
PG_CONTAINER=electricity-ebilling-postgres PG_SUPERUSER=ebilling \
  ./flowdesk-infra/scripts/provision-flowdesk-db.sh
```

Optional re-grants only: `flowdesk-infra/scripts/setup-postgres-debezium.sql`

**CDC:** Postgres needs `wal_level=logical` for Debezium. After provisioning:

```bash
./flowdesk-infra/scripts/manage_connector.sh create
```

Connector config: `flowdesk-infra/connectors/flowdesk-payment-connector.json`  
Kafka topic: `flowdesk-cdc.payments.payment`

## npm scripts

| Script | Description |
|--------|-------------|
| `npm run up:docker` | Start infra containers |
| `npm run dev` | API + UI (no Docker) |
| `npm run up` | Docker, then API + UI |
| `npm run db:provision` | Provision `flowdesk` database |
| `npm run data:flush` | Clear PG + ES via demo API (`SEED=50 npm run data:flush` to re-seed) |
| `npm run connector:create` | Register Debezium connector |

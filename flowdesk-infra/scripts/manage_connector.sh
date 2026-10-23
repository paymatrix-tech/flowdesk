#!/usr/bin/env sh
# Register or manage the Flowdesk Debezium connector (run from host; Debezium on localhost:8083).
set -euo pipefail

DEBEZIUM_URL="${DEBEZIUM_URL:-http://localhost:8083}"
CONNECTOR_NAME="${CONNECTOR_NAME:-flowdesk-payment-connector}"
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
CONFIG_PATH="${CONFIG_PATH:-$SCRIPT_DIR/../connectors/$CONNECTOR_NAME.json}"
MAX_RETRIES=30
SLEEP_TIME=5

wait_for_api() {
    echo "Waiting for Debezium Connect at $DEBEZIUM_URL ..."
    retries=0
    while [ "$retries" -lt "$MAX_RETRIES" ]; do
        http_code=$(curl -s -o /dev/null -w "%{http_code}" "$DEBEZIUM_URL/")
        if [ "$http_code" = "200" ]; then
            echo "Debezium API is ready."
            return 0
        fi
        retries=$((retries + 1))
        echo "Not ready (HTTP $http_code). Retry in ${SLEEP_TIME}s [$retries/$MAX_RETRIES]"
        sleep "$SLEEP_TIME"
    done
    echo "Debezium API did not become ready."
    exit 1
}

create_connector() {
    if [ ! -f "$CONFIG_PATH" ]; then
        echo "Config not found: $CONFIG_PATH"
        exit 1
    fi
    wait_for_api
    echo "Creating connector from $CONFIG_PATH ..."
    response=$(curl -s -w "\n%{http_code}" \
        -X POST "$DEBEZIUM_URL/connectors" \
        -H "Content-Type: application/json" \
        --data @"$CONFIG_PATH")
    http_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | sed -e '$d')

    case "$http_code" in
        201) echo "Connector created." ;;
        409) echo "Connector already exists." ;;
        400)
            echo "Bad request (HTTP 400): $body"
            exit 1
            ;;
        *)
            echo "Failed (HTTP $http_code): $body"
            exit 1
            ;;
    esac
}

status_connector() {
    wait_for_api
    curl -s "$DEBEZIUM_URL/connectors/$CONNECTOR_NAME/status" | (command -v jq >/dev/null && jq . || cat)
}

delete_connector() {
    wait_for_api
    echo "Deleting connector $CONNECTOR_NAME ..."
    http_code=$(curl -s -o /dev/null -w "%{http_code}" \
        -X DELETE "$DEBEZIUM_URL/connectors/$CONNECTOR_NAME")
    if [ "$http_code" = "204" ]; then
        echo "Connector deleted."
    else
        echo "Delete failed (HTTP $http_code)"
        exit 1
    fi
}

list_connectors() {
    wait_for_api
    curl -s "$DEBEZIUM_URL/connectors" | (command -v jq >/dev/null && jq . || cat)
}

case "${1:-}" in
    create) create_connector ;;
    status) status_connector ;;
    delete) delete_connector ;;
    list) list_connectors ;;
    *)
        echo "Usage: $0 {create|status|delete|list}"
        exit 1
        ;;
esac

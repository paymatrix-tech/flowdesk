package com.flowdesk.api.common;

public class CommonConstants {

    private CommonConstants() {
    }

    /** Elasticsearch index name (not Postgres). */
    public static final String PAYMENT_INDEX = "payments";

    public static final String POSTGRES_DATABASE = "flowdesk";
    public static final String POSTGRES_SCHEMA = "payments";
    public static final String PAYMENT_TABLE = POSTGRES_SCHEMA + ".payment";

    /** Kafka topic from Debezium ({topic.prefix}.{schema}.{table}). */
    public static final String PAYMENT_CDC_TOPIC =
            "flowdesk-cdc." + POSTGRES_SCHEMA + ".payment";
    public static final String PAYMENT_CDC_GROUP = "flowdesk-api";
    public static final double ONE_MILLION = 1_000_000.00;
}

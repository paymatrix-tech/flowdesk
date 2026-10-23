package com.flowdesk.api.util;

import java.io.IOException;
import java.sql.Date;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

/**
 * Parses dates from Debezium (ISO string or epoch days), demo payloads (ISO string),
 * and JDBC row maps converted to JSON (epoch milliseconds or {@link java.sql.Date}).
 */
public class JsonLocalDateDeserializer extends JsonDeserializer<LocalDate> {

    public static final Logger LOGGER = LoggerFactory.getLogger(JsonLocalDateDeserializer.class);

    /** Above this magnitude, numeric values are treated as epoch milliseconds (not days). */
    private static final long EPOCH_DAY_MAX_MAGNITUDE = 10_000_000L;

    @Override
    public LocalDate deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonToken token = parser.currentToken();
        if (token == JsonToken.VALUE_STRING) {
            return parseString(parser.getText());
        }
        if (token == JsonToken.VALUE_NUMBER_INT || token == JsonToken.VALUE_NUMBER_FLOAT) {
            return fromEpochNumber(parser.getLongValue());
        }
        if (token == JsonToken.VALUE_EMBEDDED_OBJECT) {
            Object embedded = parser.getEmbeddedObject();
            if (embedded instanceof LocalDate localDate) {
                return localDate;
            }
            if (embedded instanceof Date sqlDate) {
                return sqlDate.toLocalDate();
            }
            if (embedded instanceof java.util.Date utilDate) {
                return Instant.ofEpochMilli(utilDate.getTime()).atZone(ZoneOffset.UTC).toLocalDate();
            }
        }
        context.reportWrongTokenException(
                this, JsonToken.VALUE_STRING, "Expected date string, number, or embedded date.");
        return null;
    }

    private static LocalDate parseString(String raw) {
        String string = raw == null ? "" : raw.trim();
        if (string.isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(string, DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        } catch (DateTimeException ignored) {
            try {
                return LocalDate.parse(string, DateTimeFormatter.ISO_LOCAL_DATE);
            } catch (DateTimeException ex) {
                LOGGER.warn("Unparseable date string: {}", string);
                return null;
            }
        }
    }

    static LocalDate fromEpochNumber(long value) {
        if (Math.abs(value) > EPOCH_DAY_MAX_MAGNITUDE) {
            return Instant.ofEpochMilli(value).atZone(ZoneOffset.UTC).toLocalDate();
        }
        return LocalDate.ofEpochDay(value);
    }
}

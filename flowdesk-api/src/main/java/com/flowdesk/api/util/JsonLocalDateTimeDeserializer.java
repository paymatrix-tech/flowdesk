package com.flowdesk.api.util;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class JsonLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

    @Override
    public LocalDateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonToken token = parser.currentToken();
        if (token == JsonToken.START_ARRAY) {
            return parseArray(parser, context);
        }
        if (token == JsonToken.VALUE_NUMBER_INT || token == JsonToken.VALUE_NUMBER_FLOAT) {
            long value = parser.getLongValue();
            if (Math.abs(value) > 10_000_000L) {
                return LocalDateTime.ofInstant(Instant.ofEpochMilli(value), ZoneOffset.UTC);
            }
            return JsonLocalDateDeserializer.fromEpochNumber(value).atStartOfDay();
        }
        if (token == JsonToken.VALUE_STRING) {
            String string = parser.getText().trim();
            if (string.isEmpty()) {
                return null;
            }
            try {
                if (string.contains("T")) {
                    return LocalDateTime.parse(string, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
                }
                return LocalDateTime.parse(string, DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss"));
            } catch (DateTimeException e) {
                throw new IOException(e);
            }
        }
        if (token == JsonToken.VALUE_EMBEDDED_OBJECT) {
            Object embedded = parser.getEmbeddedObject();
            if (embedded instanceof LocalDateTime localDateTime) {
                return localDateTime;
            }
            if (embedded instanceof Timestamp ts) {
                return ts.toLocalDateTime();
            }
            if (embedded instanceof java.util.Date utilDate) {
                return LocalDateTime.ofInstant(utilDate.toInstant(), ZoneOffset.UTC);
            }
        }
        context.reportWrongTokenException(this, JsonToken.VALUE_STRING, "Expected string.");
        return null;
    }

    private LocalDateTime parseArray(JsonParser jp, DeserializationContext ctxt) throws IOException {
        int year = getNextValue(jp);
        int month = getNextValue(jp);
        int day = getNextValue(jp);
        int h = getNextValue(jp);
        int m = getNextValue(jp);
        int s = getNextValue(jp);
        if (jp.nextToken() != JsonToken.END_ARRAY) {
            ctxt.reportWrongTokenException(this, JsonToken.END_ARRAY, "after LocalDate ints");
        }
        return LocalDateTime.of(year, month, day, h, m, s);
    }

    private int getNextValue(JsonParser jp) throws IOException {
        jp.nextToken();
        return Integer.parseInt(jp.getText());
    }
}

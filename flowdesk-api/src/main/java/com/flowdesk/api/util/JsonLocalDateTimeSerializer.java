package com.flowdesk.api.util;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class JsonLocalDateTimeSerializer extends JsonSerializer<LocalDateTime>{

    public static DateTimeFormatter MMDDYYYYHHMMSS = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");

    @Override
    public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers)
            throws IOException, JsonProcessingException {
        value = value.atZone(ZoneId.systemDefault()).toLocalDateTime();
        gen.writeString(MMDDYYYYHHMMSS.format(value));
    }



}

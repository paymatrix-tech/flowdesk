package com.flowdesk.api.config;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.flowdesk.api.util.JsonLocalDateDeserializer;
import com.flowdesk.api.util.JsonLocalDateTimeDeserializer;

@Configuration
public class CdcJacksonConfig {

    @Bean("cdcObjectMapper")
    ObjectMapper cdcObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        SimpleModule cdcDates = new SimpleModule("flowdesk-cdc-dates");
        cdcDates.addDeserializer(LocalDate.class, new JsonLocalDateDeserializer());
        cdcDates.addDeserializer(LocalDateTime.class, new JsonLocalDateTimeDeserializer());
        mapper.registerModule(cdcDates);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        return mapper;
    }
}

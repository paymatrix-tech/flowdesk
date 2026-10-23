package com.flowdesk.api.model.vo;

import java.time.LocalDateTime;

import com.flowdesk.api.util.JsonLocalDateTimeDeserializer;
import com.flowdesk.api.util.JsonLocalDateTimeSerializer;
import com.flowdesk.api.util.ValidationType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PaymentValidationMessage {

    private Long id;

    private Long paymentId;

    private Payment payment;

    private String validationCode;

    private String subCode;

    private String displayMessage;

    private ValidationType validationType;

    private String status;

    private LocalDateTime updatedDateTime;
}

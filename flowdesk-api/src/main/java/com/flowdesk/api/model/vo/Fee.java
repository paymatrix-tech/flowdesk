package com.flowdesk.api.model.vo;

/**
 *
 */

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.flowdesk.api.util.FeePostingStatus;
import com.flowdesk.api.util.JsonLocalDateTimeDeserializer;
import com.flowdesk.api.util.JsonLocalDateTimeSerializer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author Ganesh Manohar - Swapstech
 *
 */

@Data
@NoArgsConstructor
public class Fee implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private UUID id;

    private String feeName;

    private String feeTier;

    private String application;

    private String product;

    private String feeCurrency;

    private BigDecimal feeAmount;

    private FeePostingStatus feeStatus;

    private String feeTransactionId;

    private Long paymentId;

    private Payment payment;

    private String createdBy;

    private LocalDateTime creationDateTime;

}

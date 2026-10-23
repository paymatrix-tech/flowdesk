package com.flowdesk.api.model.vo;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.flowdesk.api.util.*;

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
@SuppressWarnings("serial")
public class PaymentReturnRecord implements Serializable{

    private Long id;

    private Long achReturnId;

    private Long paymentId;

    private Payment payment;

    private String returnCode;

    private String returnDesc;

    private String dishonoredReturnCode;

    private String dishonoredReturnDesc;
    private String contestedDishonoredReturnCode;

    private String contestedDishonoredReturnDesc;

    private String status;

    private String initiatedBy;

    private String comments;

    private LocalDate deceasedDateOfDeath;

    private String fieldCode;

    private LocalDate memoPostDate;

    private MemoPostStatus memoPostStatus;

    private AccountingStatus customerAccountingStatus;

    private LocalDate customerAccountingDate;

    private AccountingStatus glAccountingStatus;

    private LocalDate glAccountingDate;

    private String fedSettlementId;

    private String dishonoredReturnFedSettlementId;

    private String contestedDishonoredReturnFedSettlementId;

    private String transmissionStatus;

    private Boolean isReturnForReversal;

    private String createdBy;

    @JsonDeserialize(using = JsonLocalDateTimeDeserializer.class)
    @JsonSerialize(using = JsonLocalDateTimeSerializer.class)
    private LocalDateTime createdDateTime;

    private String lastUpdatedBy;

    @JsonDeserialize(using = JsonLocalDateTimeDeserializer.class)
    @JsonSerialize(using = JsonLocalDateTimeSerializer.class)
    private LocalDateTime lastUpdatedDateTime;
}

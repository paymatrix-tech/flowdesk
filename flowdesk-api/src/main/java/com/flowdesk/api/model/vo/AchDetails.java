package com.flowdesk.api.model.vo;

/**
 *
 */

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

import com.flowdesk.api.util.IoType;
import com.flowdesk.api.util.JsonLocalDateTimeDeserializer;
import com.flowdesk.api.util.JsonLocalDateTimeSerializer;
import com.flowdesk.api.util.SecCode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AchDetails implements Serializable {

    private static final long serialVersionUID = 1L;

    private UUID id;

    private SecCode secCode;

    private String traceNumber;

    private Integer batchNumber;

    private IoType achType;

    private Boolean recurring;

    private String achReturnReferenceNumber;

    private String achReturnCode;

    private String achReturnDesc;

    private String comments;

    private Boolean ackRequested;

    private UUID achTransactionId;

    private UUID fileId;

    private String fileName;

    private String addendaTypeCode; //2 string

    private Integer addendaSequenceNumber;//4 number

    private Long entryDetailSequenceNumber; //7 number

    private UUID batchId;

    @JsonIgnore
    private Long paymentId;

    private Payment payment;

    private String createdBy;

    @JsonDeserialize(using = JsonLocalDateTimeDeserializer.class)
    @JsonSerialize(using = JsonLocalDateTimeSerializer.class)
    private LocalDateTime creationDateTime;
    private String foreignTraceNumber;

    private String receiverIdentificationNumber;

    private String foreignCorrespondentBankName;

    private String foreignCorrespondentBankIdentification;

    private String foreignCorrespondentBankBranchCode;

    private String additionalInfo;

    private String companyIdentification;

    private String originationDfiIdentification;

    //new fields added for IAT Send

    private String transactionTypeCode;

    private String debtorCurrency;

}

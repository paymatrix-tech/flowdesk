package com.flowdesk.api.model.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.flowdesk.api.util.*;
import lombok.Data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Payment implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long paymentId;

    private String wireType;

    private String externalRef;

    private String networkRef;

    private String otherBankRef;

    private String sourceRef;

    private String forexContract;

    private String paymentStatus;

    private String returnStatus;

    private String entryType;

    private Boolean template=false;

    private String counterParty;

    private String businessUnit;

    private String senderAccountType;

    private String senderAccountSubType;

    private String senderAccountName;

    private String senderAccountNumber;

    private Boolean forexFlag=false;

    private Boolean isAutoCover=true;

    private Integer autoCoverDelay;

    private Boolean thirdPartyFlag=false;

    private String thirdPartyFI;

    private String book;

    private String department;

    private String costCenter;

    private String customerType;

    private BigDecimal senderAmount;

    private BigDecimal marketRate;

    private String senderCurrency;

    private String paymentType;

    private String submissionCutOffTime;

    private String releaseCutOffTime;

    private LocalDateTime releaseCutOffDateTime;

    private BigDecimal receiverAmount;

    private String receiverCurrency;

    private LocalDate deliveryDate;

    private LocalDate settlementDate;

    private String deliveryMethod;

    private LocalDate paymentDate;

    private BigDecimal conversionRate;

    private String deliveryAgent;

    private String deliveryAgentType;

    private String deliveryAgentAccount;

    private String receiverAgent;

    private String receiverAgentType;

    private String receiverAgentAccount;
    /** Sender Details **/
    private String senderName;

    private String senderType;

    private String senderAddress1;

    private String senderAddress2;

    private String senderCity;

    private String senderState;

    private String senderZip;

    private String senderCountry;

    private String senderCountryCode;

    private String senderIdType;

    private String senderId;

    private String senderId2;

    private String senderIdType2;

    /** Sender Bank Details **/
    private String senderBankName;

    private String senderBankAddress1;

    private String senderBankAddress2;

    private String senderBankCity;

    private String senderBankState;

    private String senderBankZip;

    private String senderBankCountry;

    private String senderBankCountryCode;

    private String senderBankIdType;

    private String senderBankId;

    /** Sender Intermediary Details **/
    private Boolean isIntermediarySender=false;

    private String intermediarySenderName;

    private String intermediarySenderAddress1;

    private String intermediarySenderAddress2;

    private String intermediarySenderCity;

    private String intermediarySenderState;

    private String intermediarySenderZip;

    private String intermediarySenderCountry;

    private String intermediarySenderId;

    private String intermediarySenderIdType;

    /** Ultimate Sender Details **/
    private Boolean isUltimateSender=false;

    private String ultimateSenderName;

    private String ultimateSenderAccountNumber;

    private String ultimateSenderTaxId;

    private String ultimateSenderAddress1;

    private String ultimateSenderAddress2;

    private String ultimateSenderCity;

    private String ultimateSenderState;

    private String ultimateSenderZip;

    private String ultimateSenderCountry;

    private String ultimateSenderCountryCode;

    /** Receiver Details **/
    private String receiverName;

    private String receiverType;

    private String receiverAccountType;

    private String receiverAccountSubType;

    private String receiverAccountName;

    private String receiverAccountNumber;

    private String receiverAddress1;

    private String receiverAddress2;

    private String receiverCity;

    private String receiverState;

    private String receiverZip;

    private String receiverPhoneNumber;

    private String receiverCountry;

    private String receiverCountryCode;

    private String receiverIdType;

    private String receiverId;

    private String receiverId2;

    private String receiverIdType2;

    /** Receiver Bank Details **/
    private String receiverBankName;

    private String receiverBankAddress1;

    private String receiverBankAddress2;

    private String receiverBankCity;

    private String receiverBankState;

    private String receiverBankZip;

    private String receiverBankCountry;

    private String receiverBankCountryCode;

    private String receiverBankIdType;

    private String receiverBankId;

    /** Receiver Intermediary Details **/
    private Boolean isIntermediaryReceiver=false;

    private String intermediaryReceiverName;

    private String intermediaryReceiverAddress1;

    private String intermediaryReceiverAddress2;

    private String intermediaryReceiverCity;

    private String intermediaryReceiverState;

    private String intermediaryReceiverZip;

    private String intermediaryReceiverCountry;

    private String intermediaryReceiverId;

    private String intermediaryReceiverIdType;

    private String receiverTemplateName;

    /** Ultimate Receiver Details **/
    private Boolean isUltimateReceiver=false;

    private String ultimateReceiverName;

    private String ultimateReceiverAccountNumber;

    private String ultimateReceiverTaxId;

    private String ultimateReceiverAddress1;

    private String ultimateReceiverAddress2;

    private String ultimateReceiverCity;

    private String ultimateReceiverState;

    private String ultimateReceiverZip;

    private String ultimateReceiverCountry;

    private String ultimateReceiverCountryCode;

    /** Notes fields **/
    private String paymentNote;

    private String paymentNote2;

    private String paymentRemittanceInfo;

    private String regulatoryNote;

    private String senderToReceiverNote;

    private String operationNote;

    private String memoOverrideNote;

    private String detailsOfCharge;

    /** Logical Fields **/
    private String processInstanceId;

    private Long pricingId;

    private Boolean isInvertedRate=false;

    private String source;

    /** Others **/
    private BigDecimal feeAmount;

    private String feeCurrency;

    private String feePaidBy;

    private String relatedId;

    private String paymentIoType;

    private String channel;

    private Boolean isNetted=false;

    private List<Long> linkedNetIds;

    private String senderEmail;

    private String senderPhoneNumber;

    private String senderRoutingNumber;

    private LocalDate senderDateOfBirth;

    private String senderCityOfBirth;

    private String senderCountryOfBirth;

    private String senderDiscretionaryData;

    private String receiverRoutingNumber;

    private LocalDate receiverDateOfBirth;

    private String receiverCityOfBirth;

    private String receiverCountryOfBirth;

    private String receiverEmail;

    private String receiverDiscretionaryData;

    private String paymentAction;

    private String fourEyeAction;

    private String counterPartyId;

    private String businessUnitId;

    private String approvedBy;

    private LocalDateTime approvedTime;

    private String errorMessage;

    private List<Fee> fees = new ArrayList<>();

    private List<AdditionalAttribute> additionalAttributes = new ArrayList<>();

    private AchDetails achDetails;

    private PaymentLock paymentLock;

    private FedWireISODetails fedWireISODetails;

    private BookTransferDetails bookTransferDetails;

    private RtpDetails rtpDetails;

    private FedNowDetails fedNowDetails;

    private SwiftDetails swiftDetails;

    private PaymentReversalRecord paymentReversalRecord;

    private List<PaymentReturnRecord> paymentReturnRecordList = new ArrayList<>();

    private RecurringPayment recurringPayment;

    private String achOriginationId;

    private String ofacReferenceNo;

    private Long bulkFileNumber;

    private String transferType;

    private String senderAccountTokenId;

    private String receiverAccountTokenId;

    private String receiverCounterPartyId;

    private String receiverCounterParty;

    private LocalDate memoPostDate;

    private MemoPostStatus memoPostStatus;

    private String memoPostFailureReason;

    private Integer memoPostRetryCount;

    private String memoPostType;

    private Integer tranCode;

    private AccountingStatus customerAccountingStatus;

    private LocalDate customerAccountingDate;

    private AccountingStatus glAccountingStatus;

    private LocalDate glAccountingDate;

    private String fedSettlementId;

    private String chargeType;

    private Boolean coverPayment=false;

    private Boolean isOffset=false;

    private int isEDI = 0;

    private Long offsetPaymentId;

    private String individualName;

    private String fboAccountName;

    private String fboAccountNumber;

    private String fboAccountType;

    private String fboAccountSubType;

    private String validationMessage;

    public List<PaymentValidationMessage> paymentValidationMessages = new ArrayList<>();

    private String createdBy;

    private String createdByType;

    @JsonDeserialize(using = JsonLocalDateTimeDeserializer.class)
    @JsonSerialize(using = JsonLocalDateTimeSerializer.class)
    private LocalDateTime creationDateTime;

    private String lastUpdatedBy;

    private LocalDateTime lastUpdatedTime;

    private String requestMethod;

    private String fraudReferenceNo;

    private String fraudReason;

    private String fraudScore;

    private Boolean isSubmissionCutOffExceeded = false;

    private String categoryId;

    private String categoryName;

    private String approvalId;

    private Boolean approvalPending=false;

    private String bookTransferMemopostFailure;

    private String fileType;

    private Boolean isSweep = Boolean.FALSE;

    private String speed;

    private Boolean accountAnalysis;

    public Payment() {
        super();
    }

    private String intermediarySenderCountryCode;

    private String intermediaryReceiverCountryCode;

    private String memoPostReferenceId;

    private String customerId;

    private String returnOriginatingSource;

    private String reversalOriginatingSource;

    private String cancellationOriginationSource;

    private String cancellationReason;

    private String transactionId;
}

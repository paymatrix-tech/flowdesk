    package com.flowdesk.api.model.es;

    import lombok.Data;
    import org.springframework.data.annotation.Id;
    import org.springframework.data.elasticsearch.annotations.*;

    import java.math.BigDecimal;
    import java.util.List;

    import static com.flowdesk.api.common.CommonConstants.PAYMENT_INDEX;


    @Data
    @Document(indexName = PAYMENT_INDEX)  // ES index name; auto-creates if not exists
    @Setting(settingPath = "/es-settings/payments-settings.json")
    @Mapping(mappingPath = "/es-settings/payments-mappings.json")
    public class PaymentDocument {

        @Id
        private String paymentId;

        @Field(type = FieldType.Double)
        private BigDecimal senderAmount;

        @Field(type = FieldType.Double)
        private BigDecimal receiverAmount;

        @Field(type = FieldType.Text)
        private String senderName;

        @Field(type = FieldType.Text)
        private String receiverName;

        @Field(type = FieldType.Keyword)
        private String paymentStatus;

        @Field(type = FieldType.Keyword)
        private String memopostStatus;

        @Field(type = FieldType.Keyword)
        private String deliveryMethod;

        @Field(type = FieldType.Keyword)
        private String paymentType;

        @Field(type = FieldType.Text)
        private String wireType;

        @Field(type = FieldType.Text)
        private String externalRef;

        @Field(type = FieldType.Text)
        private String networkRef;

        @Field(type = FieldType.Text)
        private String otherBankRef;

        @Field(type = FieldType.Text)
        private String sourceRef;

        @Field(type = FieldType.Keyword)
        private String entryType;

        @Field(type = FieldType.Text)
        private String counterParty;

        @Field(type = FieldType.Text)
        private String businessUnit;

        @Field(type = FieldType.Text)
        private String senderAccountNumber;

        @Field(type = FieldType.Text)
        private String receiverAccountNumber;

        @Field(type = FieldType.Text)
        private String senderAccountName;

        @Field(type = FieldType.Text)
        private String receiverAccountName;

        @Field(type = FieldType.Text)
        private String book;

        @Field(type = FieldType.Text)
        private String costCenter;

        @Field(type = FieldType.Keyword)
        private String customerType;

        @Field(type = FieldType.Keyword)
        private String senderCurrency;

        @Field(type = FieldType.Keyword)
        private String receiverCurrency;

        @Field(type = FieldType.Text)
        private String senderCity;

        @Field(type = FieldType.Text)
        private String senderState;

        @Field(type = FieldType.Text)
        private String senderCountry;

        @Field(type = FieldType.Text)
        private String receiverCity;

        @Field(type = FieldType.Text)
        private String receiverState;

        @Field(type = FieldType.Text)
        private String receiverCountry;

        @Field(type = FieldType.Text)
        private String senderBankName;

        @Field(type = FieldType.Text)
        private String senderBankCity;

        @Field(type = FieldType.Text)
        private String senderBankState;

        @Field(type = FieldType.Text)
        private String senderBankCountry;

        @Field(type = FieldType.Text)
        private String receiverBankName;

        @Field(type = FieldType.Text)
        private String receiverBankCity;

        @Field(type = FieldType.Text)
        private String receiverBankState;

        @Field(type = FieldType.Text)
        private String receiverBankCountry;

        @Field(type = FieldType.Boolean)
        private String isIntermediarySender;

        @Field(type = FieldType.Boolean)
        private String isIntermediaryReceiver;

        @Field(type = FieldType.Boolean)
        private String isUltimateSender;

        @Field(type = FieldType.Boolean)
        private String isUltimateReceiver;

        @Field(type = FieldType.Keyword)
        private String channel;

        @Field(type = FieldType.Long)
        private String senderRoutingNumber;

        @Field(type = FieldType.Long)
        private String receiverRoutingNumber;

        @Field(type = FieldType.Long)
        private String bulkFileNumber;

        @Field(type = FieldType.Text)
        private String ofacReferenceNo;

        @Field(type = FieldType.Text)
        private String validationMessage;

        @Field(type = FieldType.Text)
        private String requestMethod;

        @Field(type = FieldType.Text)
        private String fileType;

        @Field(type = FieldType.Text)
        private String searchableText;

        @CompletionField(maxInputLength = 50, preserveSeparators = true, preservePositionIncrements = true)
        private List<String> suggest;
    }
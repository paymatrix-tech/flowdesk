package com.flowdesk.api.mapper;

import com.flowdesk.api.model.es.PaymentDocument;
import com.flowdesk.api.model.vo.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    PaymentMapper INSTANCE = Mappers.getMapper(PaymentMapper.class);

    @Mapping(source = "paymentId", target = "paymentId")
    @Mapping(source = "senderAmount", target = "senderAmount")
    @Mapping(source = "receiverAmount", target = "receiverAmount")
    @Mapping(source = "senderName", target = "senderName")
    @Mapping(source = "receiverName", target = "receiverName")
    @Mapping(source = "paymentStatus", target = "paymentStatus")
    @Mapping(source = "memoPostStatus", target = "memopostStatus") // handle VO -> ES mismatch
    @Mapping(source = "paymentDate", target = "paymentDate")
    default PaymentDocument toDocument(Payment source) {
        if (source == null) {
            return null;
        }

        PaymentDocument doc = new PaymentDocument();
        if (source.getPaymentId() != null) {
            doc.setPaymentId(String.valueOf(source.getPaymentId()));
        }
        doc.setSenderAmount(source.getSenderAmount());
        doc.setReceiverAmount(source.getReceiverAmount());
        doc.setSenderName(source.getSenderName());
        doc.setReceiverName(source.getReceiverName());
        doc.setPaymentStatus(source.getPaymentStatus());
        if (source.getMemoPostStatus() != null) {
            doc.setMemopostStatus(source.getMemoPostStatus().name());
        }
        doc.setDeliveryMethod(source.getDeliveryMethod());
        doc.setPaymentType(source.getPaymentType());
        doc.setWireType(source.getWireType());
        doc.setExternalRef(source.getExternalRef());
        doc.setNetworkRef(source.getNetworkRef());
        doc.setOtherBankRef(source.getOtherBankRef());
        doc.setSourceRef(source.getSourceRef());
        doc.setEntryType(source.getEntryType());
        doc.setCounterParty(source.getCounterParty());
        doc.setBusinessUnit(source.getBusinessUnit());
        doc.setSenderAccountName(source.getSenderAccountName());
        doc.setReceiverAccountName(source.getReceiverAccountName());
        doc.setBook(source.getBook());
        doc.setCostCenter(source.getCostCenter());
        doc.setCustomerType(source.getCustomerType());
        doc.setSenderCurrency(source.getSenderCurrency());
        doc.setReceiverCurrency(source.getReceiverCurrency());
        doc.setChannel(source.getChannel());
        doc.setOfacReferenceNo(source.getOfacReferenceNo());
        doc.setValidationMessage(source.getValidationMessage());
        doc.setRequestMethod(source.getRequestMethod());
        doc.setFileType(source.getFileType());

        buildSearchableText(source, doc);
        // suggest is a context-enabled completion field in ES; omit unless we send proper contexts
        return doc;
    }

    private void buildSearchableText(Payment source, PaymentDocument doc) {
        String searchable = Stream.of(
                        source.getSenderName(),
                        source.getReceiverName(),
                        source.getPaymentStatus(),
                        source.getMemoPostStatus() != null ? source.getMemoPostStatus().name() : null,
                        source.getSenderAmount(),
                        source.getReceiverAmount(),
                        source.getPaymentDate(),
                        source.getPaymentId(),
                        source.getTransactionId(),
                        source.getDeliveryMethod(),
                        source.getPaymentType(),
                        source.getWireType(),
                        source.getExternalRef(),
                        source.getNetworkRef(),
                        source.getOtherBankRef(),
                        source.getSourceRef(),
                        source.getEntryType(),
                        source.getCounterParty(),
                        source.getBusinessUnit(),
                        source.getSenderAccountName(),
                        source.getReceiverAccountName(),
                        source.getBook(),
                        source.getCostCenter(),
                        source.getCustomerType(),
                        source.getSenderCurrency(),
                        source.getReceiverCurrency(),
                        source.getSenderCity(),
                        source.getSenderState(),
                        source.getSenderCountry(),
                        source.getReceiverCity(),
                        source.getReceiverState(),
                        source.getReceiverCountry(),
                        source.getSenderBankName(),
                        source.getSenderBankCity(),
                        source.getSenderBankState(),
                        source.getSenderBankCountry(),
                        source.getReceiverBankName(),
                        source.getReceiverBankCity(),
                        source.getReceiverBankState(),
                        source.getReceiverBankCountry(),
                        source.getOfacReferenceNo(),
                        source.getValidationMessage(),
                        source.getRequestMethod(),
                        source.getFileType(),
                        // Booleans → add readable keywords if true
                        Boolean.TRUE.equals(source.getIsIntermediarySender()) ? "intermediarySender" : null,
                        Boolean.TRUE.equals(source.getIsIntermediaryReceiver()) ? "intermediaryReceiver" : null,
                        Boolean.TRUE.equals(source.getIsUltimateSender()) ? "ultimateSender" : null,
                        Boolean.TRUE.equals(source.getIsUltimateReceiver()) ? "ultimateReceiver" : null
                )
                .filter(Objects::nonNull)                          // remove nulls
                .map(String::valueOf)                            // convert everything to String
                .filter(s -> !s.isBlank())                       // remove empty strings
                .collect(Collectors.joining(" "));               // join into one string

        doc.setSearchableText(searchable);
    }
}

package com.flowdesk.api.model.vo;

import java.io.Serializable;
import java.util.UUID;

import com.flowdesk.api.util.IoType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FedWireISODetails implements Serializable{

    private UUID id;

    private String messageId;

    private String omad;

    private String finId;

    private String messageType;

    private String correlationId;

    private IoType direction;

    private String localInstrument;

    private Payment payment;

    private String fedwireISOMessage;
}

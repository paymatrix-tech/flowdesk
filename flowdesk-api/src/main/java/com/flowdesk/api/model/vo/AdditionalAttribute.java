package com.flowdesk.api.model.vo;

import java.io.Serializable;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import com.fasterxml.jackson.annotation.JsonIgnore;

@SuppressWarnings("serial")
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AdditionalAttribute  implements Serializable{

    private UUID id;

    private Long paymentId;

    private Payment payment;

    private String level;

    private String attributeName;

    private String value;

    public AdditionalAttribute(AdditionalAttribute additionalAttribute) {
        this.level = additionalAttribute.getLevel();
        this.attributeName = additionalAttribute.getAttributeName();
        this.value = additionalAttribute.getValue();
    }
}

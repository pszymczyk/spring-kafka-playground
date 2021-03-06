package com.pszymczyk.events;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ItemAdded.class, name = ItemAdded.TYPE),
        @JsonSubTypes.Type(value = ItemAdded.class, name = ItemRemoved.TYPE)
})
public interface OrderEvent {
    String getOrderId();
    String getItem();
    String getType();
}

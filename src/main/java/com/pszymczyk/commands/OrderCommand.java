package com.pszymczyk.commands;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        visible = true,
        defaultImpl = UnknownCommand.class)
@JsonSubTypes({
        @JsonSubTypes.Type(value = AddItem.class, name = AddItem.TYPE),
        @JsonSubTypes.Type(value = RemoveItem.class, name = RemoveItem.TYPE)
})
public interface OrderCommand {
    String getType();
    String getOrderId();
}

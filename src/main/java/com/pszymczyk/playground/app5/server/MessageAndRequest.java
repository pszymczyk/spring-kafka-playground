package com.pszymczyk.playground.app5.server;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        visible = true,
        defaultImpl = Void.class)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Request.class, name = "request"),
        @JsonSubTypes.Type(value = Message.class, name = "message")
})
public interface MessageAndRequest {
    String getType();
}

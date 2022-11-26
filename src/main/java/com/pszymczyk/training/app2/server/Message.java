package com.pszymczyk.training.app2.server;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Message(@JsonProperty("sender") String sender, @JsonProperty("receiver") String receiver, @JsonProperty("value") String value, @JsonProperty("type") String type) implements MessageAndRequest {

    public Message(String sender, String receiver, String value) {
        this(sender, receiver, value, "message");
    }

    @Override
    public String getType() {
        return type;
    }
}

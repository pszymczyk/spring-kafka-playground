package com.pszymczyk.training.app2.server;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Request(@JsonProperty("id") String id, @JsonProperty("value") String value, @JsonProperty("type") String type) implements MessageAndRequest {
    public Request(String id, String value) {
        this(id, value, "request");
    }

    @Override
    public String getType() {
        return type;
    }
}

package com.pszymczyk.training.app2.server;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Message(@JsonProperty("sender") String sender, @JsonProperty("receiver") String receiver,
                      @JsonProperty("value") String value) {
}

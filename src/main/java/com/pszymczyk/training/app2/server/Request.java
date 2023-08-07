package com.pszymczyk.training.app2.server;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Request(@JsonProperty("id") String id, @JsonProperty("rqst") String rqst){
}

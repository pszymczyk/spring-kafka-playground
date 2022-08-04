package com.pszymczyk.playground.app5.server;

class Unknown implements MessageAndRequest {
    @Override
    public String getType() {
        return "unknown";
    }
}

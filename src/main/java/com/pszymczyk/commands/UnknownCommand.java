package com.pszymczyk.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UnknownCommand implements OrderCommand {

    private final String orderId;
    private final String item;
    private final String type;

    @JsonCreator
    public UnknownCommand(
            @JsonProperty("orderId") String orderId,
            @JsonProperty("item") String item,
            @JsonProperty("type") String type) {
        this.orderId = orderId;
        this.item = item;
        this.type = type;
    }

    public String getItem() {
        return item;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "OrderCommand{" +
                "orderId='" + orderId + '\'' +
                ", item='" + item + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}

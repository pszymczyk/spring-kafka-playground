package com.pszymczyk.application.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RemoveItem implements OrderCommand {

    public static final String TYPE = "RemoveItem";

    private final String orderId;
    private final String item;

    @JsonCreator
    public RemoveItem(
        @JsonProperty("orderId") String orderId,
        @JsonProperty("item") String item) {
        this.orderId = orderId;
        this.item = item;
    }

    public String getItem() {
        return item;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getType() {
        return TYPE;
    }

    @Override
    public String toString() {
        return "OrderCommand{" +
                "orderId='" + orderId + '\'' +
                ", item='" + item + '\'' +
                ", type='" + TYPE + '\'' +
                '}';
    }
}

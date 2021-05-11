package com.pszymczyk.events;

public class ItemRemoved implements OrderEvent {

    static final String TYPE = "ItemRemoved";

    private final String orderId;
    private final String item;

    public ItemRemoved(
        String orderId,
        String item) {
        this.orderId = orderId;
        this.item = item;
    }

    public String getItem() {
        return item;
    }

    @Override
    public String getOrderId() {
        return orderId;
    }

    @Override
    public String getType() {
        return TYPE;
    }
}

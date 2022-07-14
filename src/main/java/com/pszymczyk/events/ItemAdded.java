package com.pszymczyk.events;

public class ItemAdded implements OrderEvent {

    public static final String TYPE = "ItemAdded";

    private final String orderId;
    private final String item;

    public ItemAdded(String orderId, String item) {
        this.orderId = orderId;
        this.item = item;
    }

    @Override
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

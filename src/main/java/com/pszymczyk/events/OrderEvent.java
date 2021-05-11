package com.pszymczyk.events;

public interface OrderEvent {
    String getOrderId();
    String getItem();
    String getType();
}

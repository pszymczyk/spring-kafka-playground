package com.pszymczyk.events;

interface OrderEvent {
    String getOrderId();
    String getItem();
    String getType();
}

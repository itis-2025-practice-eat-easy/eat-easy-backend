package com.technokratos.eateasy.orderimpl.util;

public enum Status {
    CREATED("Заказ создан", 1),
    PAID("Заказ оплачен", 2),
    CANCELLED("Заказ отменён", 3),
    DELIVERED("Заказ доставлен",4);


    private final String reason;
    private final int layer;

    Status(String reason, int level) {
        this.reason = reason;
        this.layer = level;
    }

    public String getReason() {
        return reason;
    }

    public int getLevel() {
        return layer;
    }
}


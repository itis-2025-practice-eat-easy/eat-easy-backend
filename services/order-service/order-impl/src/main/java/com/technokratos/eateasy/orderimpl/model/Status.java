package com.technokratos.eateasy.orderimpl.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Status {
    CREATED("Заказ создан", 1),
    PAID("Заказ оплачен", 2),
    CANCELLED("Заказ отменён", 3),
    DELIVERED("Заказ доставлен",4);
    private final String reason;
    private final int layer;
}

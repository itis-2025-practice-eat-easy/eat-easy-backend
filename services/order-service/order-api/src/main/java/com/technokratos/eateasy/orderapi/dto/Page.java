package com.technokratos.eateasy.orderapi.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Page<T> {
    private int totalOrders;
    private int currentPage;
    private int ordersInPage;
    private List<T> orders;
}

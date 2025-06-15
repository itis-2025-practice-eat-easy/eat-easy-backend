package com.technokratos.eateasy.orderimpl.service;

import com.technokratos.eateasy.orderapi.OrderRequestDto;
import com.technokratos.eateasy.orderapi.OrderResponseDto;
import com.technokratos.eateasy.orderapi.Page;
import com.technokratos.eateasy.orderapi.StatusResponseDto;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    OrderResponseDto getById(UUID id);

    OrderResponseDto create(OrderRequestDto requestDto);

    List<StatusResponseDto> getListOfAllStatus(UUID orderId);

    Page<OrderResponseDto> getPagableUserOrders(UUID id, int page, int pageSize, Boolean actual);
}

package com.technokratos.eateasy.orderimpl.service;

import com.technokratos.eateasy.orderapi.dto.OrderRequestDto;
import com.technokratos.eateasy.orderapi.dto.OrderResponseDto;
import com.technokratos.eateasy.orderapi.dto.Page;
import com.technokratos.eateasy.orderapi.dto.OrderLogResponseDto;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    OrderResponseDto getById(UUID id);
    OrderResponseDto create(OrderRequestDto requestDto);
    List<OrderLogResponseDto> getListOfAllStatus(UUID orderId);
    Page<OrderResponseDto> getPageableUserOrders(UUID id, int page, int pageSize, Boolean actual);
}

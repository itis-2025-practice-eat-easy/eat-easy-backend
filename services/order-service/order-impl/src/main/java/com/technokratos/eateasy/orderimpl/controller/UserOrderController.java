package com.technokratos.eateasy.orderimpl.controller;

import com.technokratos.eateasy.orderapi.dto.OrderResponseDto;
import com.technokratos.eateasy.orderapi.dto.Page;
import com.technokratos.eateasy.orderapi.api.UserOrderApi;
import com.technokratos.eateasy.orderimpl.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserOrderController implements UserOrderApi {
    private final OrderService service;
    @Override
    public Page<OrderResponseDto> getPagableUserOrders(UUID id, int page, int pageSize, boolean actual) {
        return service.getPageableUserOrders(id, page, pageSize, actual);
    }
}

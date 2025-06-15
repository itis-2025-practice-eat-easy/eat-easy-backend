package com.technokratos.eateasy.orderimpl.controller;

import com.technokratos.eateasy.orderapi.OrderResponseDto;
import com.technokratos.eateasy.orderapi.Page;
import com.technokratos.eateasy.orderapi.UserOrderApi;
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
    public Page<OrderResponseDto> getPagableUserOrders(UUID id, int page, int pageSize, Boolean actual) {
        return service.getPagableUserOrders(id,page,pageSize,actual);
    }
}

package com.technokratos.eateasy.orderimpl.controller;

import com.technokratos.eateasy.orderapi.OrderApi;
import com.technokratos.eateasy.orderapi.OrderRequestDto;
import com.technokratos.eateasy.orderapi.OrderResponseDto;
import com.technokratos.eateasy.orderapi.StatusResponseDto;
import com.technokratos.eateasy.orderapi.Page;
import com.technokratos.eateasy.orderimpl.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class OrderController implements OrderApi {
    private final OrderService service;
    @Override
    public OrderResponseDto getById(UUID id) {
        return service.getById(id);
    }
    @Override
    public OrderResponseDto create(OrderRequestDto requestDto) {
        return service.create(requestDto);
    }
    @Override
    public List<StatusResponseDto> getListOfAllStatus(UUID orderId) {
        return service.getListOfAllStatus(orderId);
    }




    //TODO: метод смены статуса (аргументы - id заказа, новый статус)




}

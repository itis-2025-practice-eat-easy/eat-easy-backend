package com.technokratos.eateasy.orderapi;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.UUID;

@RequestMapping("/api/v1/orders")
public interface OrderApi {

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    OrderResponseDto getById(@PathVariable UUID id);

    @PostMapping()
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    void create(@RequestBody OrderRequestDto requestDto);

    List<StatusResponseDto> getListOfAllStatus(UUID orderId);

    Page<OrderResponseDto> getPagableUserOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(name = "page_size", defaultValue = "10") int pageSize,
            @RequestParam(required = false) Boolean actual
    );

}

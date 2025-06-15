package com.technokratos.eateasy.orderapi;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.UUID;

@RequestMapping("/api/v1/orders")
public interface OrderApi {

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    OrderResponseDto getById(@PathVariable @NotNull UUID id);



    @PostMapping()
    @ResponseStatus(HttpStatus.ACCEPTED)
    @ResponseBody
    OrderResponseDto create(@RequestBody @Valid OrderRequestDto requestDto);

    List<StatusResponseDto> getListOfAllStatus(UUID orderId);




}

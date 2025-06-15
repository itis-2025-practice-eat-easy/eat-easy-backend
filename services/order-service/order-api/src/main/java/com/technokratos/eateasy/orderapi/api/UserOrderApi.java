package com.technokratos.eateasy.orderapi.api;

import com.technokratos.eateasy.orderapi.dto.OrderResponseDto;
import com.technokratos.eateasy.orderapi.dto.Page;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("/api/v1/users")
public interface UserOrderApi {
    @GetMapping("/{id}/orders")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    Page<OrderResponseDto> getPagableUserOrders(
            @PathVariable @NotNull UUID id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(name = "page_size", defaultValue = "10") int pageSize,
            @RequestParam(required = false) @NotNull boolean actual);
}

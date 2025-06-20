package com.technokratos.eateasy.orderimpl.service;

import com.technokratos.eateasy.orderapi.dto.CartDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "cart-service", path = "/api/v1/carts")
public interface CartClientService {
    @GetMapping("/{id}")
    CartDto getByUserId(@PathVariable UUID id);
}

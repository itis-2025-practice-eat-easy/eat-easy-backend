package com.technokratos.api;

import com.technokratos.dto.CartRequest;
import com.technokratos.dto.CartResponse;
import com.technokratos.dto.ProductRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping("/api/v1")
public interface CartApi {
    @PostMapping("/carts")
    @ResponseStatus(HttpStatus.OK)
    CartResponse create(@RequestBody CartRequest req);

    @GetMapping("/user/{id}/carts")
    @ResponseStatus(HttpStatus.OK)
    List<CartResponse> getAll(@PathVariable("id") UUID id);

    @PutMapping("/carts/{id}/products")
    @ResponseStatus(HttpStatus.OK)
    void addToCart(@RequestBody ProductRequest request, @PathVariable("id") UUID userId);

    @GetMapping("/carts/{id}")
    @ResponseStatus(HttpStatus.OK)
    CartResponse getById(@PathVariable("id") UUID userId);

}

package com.technokratos.api;

import com.technokratos.dto.CartResponse;
import com.technokratos.dto.ProductRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Cart API", description = "Операции с корзиной пользователя")
@RequestMapping("/api/v1")
public interface CartApi {

    @Operation(
            summary = "Получить все корзины пользователя",
            description = "Возвращает список всех корзин, связанных с указанным пользователем",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Корзины успешно получены"),
                    @ApiResponse(responseCode = "404", description = "Пользователь не найден", content = @Content)
            }
    )
    @GetMapping("/users/{id}/carts")
    @ResponseStatus(HttpStatus.OK)
    List<CartResponse> getAll(
            @Parameter(description = "UUID пользователя", required = true)
            @PathVariable("id") UUID userId
    );

    @Operation(
            summary = "Добавить продукт в корзину",
            description = "Добавляет указанный продукт и его количество в корзину по ID корзины",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Продукт успешно добавлен"),
                    @ApiResponse(responseCode = "400", description = "Ошибка валидации запроса", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Корзина или продукт не найдены", content = @Content)
            }
    )
    @PutMapping("/carts/{id}/products")
    @ResponseStatus(HttpStatus.OK)
    void addToCart(
            @RequestBody(
                    required = true,
                    description = "Запрос на добавление продукта в корзину",
                    content = @Content(schema = @Schema(implementation = ProductRequest.class))
            )
            @Valid @org.springframework.web.bind.annotation.RequestBody ProductRequest request,

            @Parameter(description = "UUID корзины", required = true)
            @PathVariable("id") UUID cartId
    );

    @Operation(
            summary = "Получить корзину по ID",
            description = "Возвращает корзину по её UUID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Корзина успешно получена"),
                    @ApiResponse(responseCode = "404", description = "Корзина не найдена", content = @Content)
            }
    )
    @GetMapping("/carts/{id}")
    @ResponseStatus(HttpStatus.OK)
    CartResponse getById(
            @Parameter(description = "UUID корзины", required = true)
            @PathVariable("id") UUID userId
    );
}

package com.technokratos.eateasy.orderapi.api;

import com.technokratos.eateasy.orderapi.dto.OrderLogResponseDto;
import com.technokratos.eateasy.orderapi.dto.OrderRequestDto;
import com.technokratos.eateasy.orderapi.dto.OrderResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Order Management", description = "API for managing orders")
@RequestMapping("/api/v1/orders")
public interface OrderApi {

    @Operation(
            summary = "Get order by ID",
            description = "Returns order information by its unique identifier"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Order found",
                    content = @Content(schema = @Schema(implementation = OrderResponseDto.class))
            ),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    OrderResponseDto getById(
            @Parameter(
                    description = "Order UUID",
                    required = true,
                    example = "123e4567-e89b-12d3-a456-426614174000",
                    in = ParameterIn.PATH
            )
            @PathVariable @NotNull UUID id
    );

    @Operation(
            summary = "Create a new order",
            description = "Creates a new order based on the provided data"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "202",
                    description = "Order accepted",
                    content = @Content(schema = @Schema(implementation = OrderResponseDto.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid data for order creation")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    @ResponseBody
    OrderResponseDto create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "New order data", required = true,
                    content = @Content(schema = @Schema(implementation = OrderRequestDto.class))
            )
            @Valid @RequestBody OrderRequestDto requestDto
    );

    @Operation(
            summary = "Get order status history",
            description = "Returns a list of all status change logs for the order by its ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Order status history retrieved",
                    content = @Content(schema = @Schema(implementation = OrderLogResponseDto.class))
            ),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @GetMapping("/{id}/info")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    List<OrderLogResponseDto> getListOfAllStatus(
            @Parameter(
                    description = "Order UUID",
                    required = true,
                    example = "123e4567-e89b-12d3-a456-426614174000",
                    in = ParameterIn.PATH
            )
            @PathVariable @NotNull UUID id
    );
}


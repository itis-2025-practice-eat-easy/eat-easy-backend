package com.technokratos.eateasy.orderapi.api;

import com.technokratos.eateasy.orderapi.dto.OrderResponseDto;
import com.technokratos.eateasy.orderapi.dto.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "User Order History", description = "API for retrieving a specific user's orders with pagination")
@RequestMapping("/api/v1/users")
public interface UserOrderApi {

    @Operation(
            summary = "Get user orders",
            description = "Returns a list of a specific user's orders with pagination and filtering by current status"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved user orders",
                    content = @Content(schema = @Schema(implementation = Page.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{id}/orders")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    Page<OrderResponseDto> getPagableUserOrders(
            @Parameter(
                    description = "User UUID",
                    required = true,
                    example = "123e4567-e89b-12d3-a456-426614174000",
                    in = ParameterIn.PATH
            )
            @PathVariable @NotNull UUID id,

            @Parameter(
                    description = "Page number (starting from 0)",
                    example = "0",
                    in = ParameterIn.QUERY
            )
            @RequestParam(defaultValue = "0") int page,

            @Parameter(
                    description = "Page size (number of items per page)",
                    name = "page_size",
                    example = "10",
                    in = ParameterIn.QUERY
            )
            @RequestParam(name = "page_size", defaultValue = "10") int pageSize,

            @Parameter(
                    description = "Filter by order activity: true — only active orders, false — all orders",
                    required = false,
                    example = "true",
                    in = ParameterIn.QUERY
            )
            @RequestParam(required = false) @NotNull boolean actual
    );
}


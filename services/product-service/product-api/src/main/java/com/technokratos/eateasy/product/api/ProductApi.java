package com.technokratos.eateasy.product.api;

import com.technokratos.eateasy.product.dto.product.ProductRequest;
import com.technokratos.eateasy.product.dto.product.ProductResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


import java.util.UUID;

@Tag(name = "Products", description = "Product management (food and grocery items)")
@RequestMapping("/api/v1/products")
public interface ProductApi {

    @Operation(summary = "Get a product by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    ProductResponse getById(
            @Parameter(description = "Product UUID", example = "c7e2f6b4-98b8-4f98-89b2-8295e8d25b5a", required = true)
            @PathVariable("id") UUID productId);

    @Operation(summary = "Create a new product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid product data"),
            @ApiResponse(responseCode = "409", description = "Product already exists")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    ProductResponse create(
            @RequestBody(
                    description = "Product to create",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ProductRequest.class),
                            examples = @ExampleObject(value = """
                                {
                                  "title": "Organic Banana",
                                  "description": "Fresh organic bananas from Ecuador.",
                                  "photoUrl": "http://example.com/images/banana.jpg",
                                  "price": 1.99,
                                  "category": "Fruits",
                                  "quantity": 150
                                }
                            """))
            )
            ProductRequest product);

    @Operation(summary = "Update the quantity of a product (add or subtract the provided value)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product quantity updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid quantity data"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @PatchMapping("/{id}/count")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    void updateQuantity(
            @Parameter(description = "Product UUID", example = "c7e2f6b4-98b8-4f98-89b2-8295e8d25b5a", required = true)
            @PathVariable("id") UUID productId,

            @RequestBody(
                    description = "Quantity change (positive to increase, negative to decrease)",
                    required = true,
                    content = @Content(schema = @Schema(type = "integer", example = "5"))
            )
            Integer quantity);

    @Operation(summary = "Update a product's details (partial update)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid product data"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    void update(
            @Parameter(description = "Product UUID", example = "c7e2f6b4-98b8-4f98-89b2-8295e8d25b5a", required = true)
            @PathVariable("id") UUID productId,

            @RequestBody(
                    description = "Fields to update in the product",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ProductRequest.class),
                            examples = @ExampleObject(value = """
                                {
                                  "price": 2.49,
                                  "quantity": 120
                                }
                            """))
            )
            ProductRequest product);

    @Operation(summary = "Delete a product by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    void delete(
            @Parameter(description = "Product UUID", example = "c7e2f6b4-98b8-4f98-89b2-8295e8d25b5a", required = true)
            @PathVariable("id") UUID productId);
}

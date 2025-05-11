package com.technokratos.eateasy.product.api;

import com.technokratos.eateasy.product.dto.category.CategoryRequest;
import com.technokratos.eateasy.product.dto.category.CategoryResponse;
import com.technokratos.eateasy.product.dto.product.ProductResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Categories", description = "Category management (creating and retrieving product categories)")
@RequestMapping("api/v1/categories")
public interface CategoryApi {

    @Operation(summary = "Get all categories")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categories retrieved successfully",
                    content = @Content(schema = @Schema(implementation = CategoryResponse.class))),
            @ApiResponse(responseCode = "404", description = "No categories found")
    })
    @GetMapping
    ResponseEntity<List<CategoryResponse>> getAll();

    @Operation(summary = "Create a new category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Category created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid category data")
    })
    @PostMapping
    ResponseEntity<CategoryResponse> create(

            @RequestBody(
                    description = "Category to create",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CategoryRequest.class),
                            examples = @ExampleObject(value = """
                                {
                                  "title": "Fruits"
                                }
                            """))
            )
            CategoryRequest category);

    @Operation(summary = "Get all products in a category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @GetMapping("/{id}/products")
    ResponseEntity<List<ProductResponse>> getProductsByCategory(
            @Parameter(description = "Category ID", example = "1", required = true)
            @PathVariable Long id,

            @Parameter(description = "Field by which products should be ordered", example = "price")
            @RequestParam(required = false) String order_by,

            @Parameter(description = "Page number", example = "1")
            @RequestParam(required = false) String page,

            @Parameter(description = "Number of products per page", example = "10")
            @RequestParam(required = false) String page_size,

            @Parameter(description = "Max price for product filter", example = "100.0")
            @RequestParam(required = false) String max_price,

            @Parameter(description = "Min price for product filter", example = "1.0")
            @RequestParam(required = false) String min_price
    );
}

package com.technokratos.eateasy.product.api;

import com.technokratos.eateasy.product.dto.category.CategoryRequest;
import com.technokratos.eateasy.product.dto.category.CategoryResponse;
import com.technokratos.eateasy.product.dto.product.ProductResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(
    name = "Categories",
    description = "Category management (creating and retrieving product categories)")
@RequestMapping("api/v1/categories")
public interface CategoryApi {

  @Operation(summary = "Get all categories")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Categories retrieved successfully",
            content = @Content(schema = @Schema(implementation = CategoryResponse.class))),
      })
  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  List<CategoryResponse> getAll();

  @Operation(summary = "Create a new category")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "201", description = "Category created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid category data")
      })
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  CategoryResponse create(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "Category to create",
              required = true,
              content =
                  @Content(
                      schema = @Schema(implementation = CategoryRequest.class),
                      examples =
                          @ExampleObject(
                              value =
                                  """
                                {
                                  "title": "Fruits"
                                }
                            """)))
          @org.springframework.web.bind.annotation.RequestBody
          @Valid
          CategoryRequest category);

  @Operation(summary = "Get all products in a category")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Products retrieved successfully",
            content = @Content(schema = @Schema(implementation = ProductResponse.class))),
        @ApiResponse(responseCode = "404", description = "Category not found")
      })
  @GetMapping("/{id}/products")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  List<ProductResponse> getProductsByCategory(
      @Parameter(
              description = "Category ID (UUID)",
              example = "f0272540-93bd-4a9d-8ca7-c55a4136585a",
              required = true)
          @PathVariable("id")
          @NotNull
          UUID id,
      @Parameter(
              description = "Sorting criteria: 'popularity', 'price', or 'new'",
              example = "popularity")
          @RequestParam(name = "order_by", required = false, defaultValue = "popularity")
          @Pattern(regexp = "popularity|price|new", message = "order_by must be one of: popularity, price, new")
          String orderBy,
      @Parameter(description = "Page number (0-based)", example = "0")
          @RequestParam(name = "page", required = false, defaultValue = "0")
          @Min(0)
          @Max(1000)
          Integer page,
      @Parameter(description = "Number of products per page (1–100)", example = "20")
          @RequestParam(name = "page_size", required = false, defaultValue = "20")
          @Min(1)
          @Max(1000)
          Integer pageSize,
      @Parameter(description = "Maximum product price for filtering", example = "99.99")
          @RequestParam(name = "max_price", required = false)
          @DecimalMin("0.0")
          @DecimalMax("10000.0")
          BigDecimal maxPrice,
      @Parameter(description = "Minimum product price for filtering", example = "10.00")
          @RequestParam(name = "min_price", required = false)
          @DecimalMin("0.0")
          @DecimalMax("10000.0")
          BigDecimal minPrice);
}

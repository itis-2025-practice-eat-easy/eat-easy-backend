package com.technokratos.eateasy.product.mapper;

import com.technokratos.eateasy.product.dto.product.ProductRequest;
import com.technokratos.eateasy.product.dto.product.ProductResponse;
import com.technokratos.eateasy.product.dto.product.ProductUpdateRequest;
import com.technokratos.eateasy.product.entity.Product;
import java.sql.Timestamp;
import java.time.Instant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

  @Mapping(target = "createdAt", expression = "java(getCurrentTimestamp())")
  @Mapping(target = "popularity", constant = "0")
  Product toEntity(ProductRequest productRequest);

  @Mapping(target = "createdAt", expression = "java(getCurrentTimestamp())")
  @Mapping(target = "popularity", constant = "0")
  Product toEntity(ProductUpdateRequest productRequest);

  @Mapping(target = "categories", expression = "java(new java.util.ArrayList<>())")
  ProductResponse toResponse(Product product);

  default Timestamp getCurrentTimestamp() {
    return Timestamp.from(Instant.now());
  }
}

package com.technokratos.eateasy.product.mapper;

import com.technokratos.eateasy.product.dto.category.CategoryRequest;
import com.technokratos.eateasy.product.dto.category.CategoryResponse;
import com.technokratos.eateasy.product.entity.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

  Category toEntity(CategoryRequest categoryRequest);

  CategoryResponse toResponse(Category category);
}

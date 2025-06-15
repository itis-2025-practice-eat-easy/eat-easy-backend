package com.technokratos.eateasy.cart.mapper;

import com.technokratos.dto.CartRequest;
import com.technokratos.dto.CartResponse;
import com.technokratos.eateasy.cart.entity.Cart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {
    @Mapping(target = "id", ignore = true)
    Cart toEntity(CartRequest cartRequest);
    CartResponse toResponse(Cart cart);
}

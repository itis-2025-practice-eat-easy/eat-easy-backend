package com.technokratos.eateasy.cart.mapper;

import com.technokratos.dto.CartResponse;
import com.technokratos.eateasy.cart.entity.Cart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {
    CartResponse toResponse(Cart cart);
}

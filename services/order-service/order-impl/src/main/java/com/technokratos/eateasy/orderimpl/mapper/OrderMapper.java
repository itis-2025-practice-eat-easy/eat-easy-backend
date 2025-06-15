package com.technokratos.eateasy.orderimpl.mapper;

import com.technokratos.eateasy.orderapi.OrderRequestDto;
import com.technokratos.eateasy.orderapi.OrderResponseDto;
import com.technokratos.eateasy.orderimpl.model.OrderEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderResponseDto toDto(OrderEntity orderEntity);
    OrderEntity toEntity(OrderRequestDto orderDto);
}

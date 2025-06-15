package com.technokratos.eateasy.orderimpl.mapper;

import com.technokratos.eateasy.orderapi.dto.OrderRequestDto;
import com.technokratos.eateasy.orderapi.dto.OrderResponseDto;
import com.technokratos.eateasy.orderimpl.model.OrderEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderResponseDto toDto(OrderEntity orderEntity);
    OrderEntity toEntity(OrderRequestDto orderDto);
}

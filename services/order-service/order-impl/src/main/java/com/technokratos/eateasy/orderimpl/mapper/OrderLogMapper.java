package com.technokratos.eateasy.orderimpl.mapper;

import com.technokratos.eateasy.orderapi.dto.OrderLogResponseDto;
import com.technokratos.eateasy.orderimpl.model.OrderLogEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderLogMapper {
    OrderLogResponseDto toDto(OrderLogEntity orderEntity);
}

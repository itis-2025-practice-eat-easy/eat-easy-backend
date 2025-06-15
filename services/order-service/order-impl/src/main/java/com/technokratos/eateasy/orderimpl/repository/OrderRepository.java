package com.technokratos.eateasy.orderimpl.repository;

import com.technokratos.eateasy.orderapi.StatusResponseDto;
import com.technokratos.eateasy.orderimpl.model.OrderEntity;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {
    Optional<OrderEntity> findById(UUID id);

    List<StatusResponseDto> getListOfAllStatus(UUID orderId);

    void save(OrderEntity order);

    List<OrderEntity> findAllByUser(UUID userId, Pageable pageable);

    List<OrderEntity> findAllActualByUser(UUID userId, Pageable pageable);
}

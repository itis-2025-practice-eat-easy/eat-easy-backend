package com.technokratos.eateasy.orderimpl.repository;

import com.technokratos.eateasy.orderimpl.model.OrderEntity;
import com.technokratos.eateasy.orderimpl.model.OrderLogEntity;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {
    Optional<OrderEntity> findById(UUID id);

    List<OrderLogEntity> getListOfAllStatus(UUID orderId);

    void save(OrderEntity order);

    int countByUser(UUID userId);

    List<OrderEntity> findAllByUser(UUID userId, Pageable pageable);

    List<OrderEntity> findAllActualByUser(UUID userId, Pageable pageable);

    boolean isOrderWithThisCartIdExist(UUID cartId);
}

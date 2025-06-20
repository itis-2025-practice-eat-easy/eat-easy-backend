package com.technokratos.eateasy.orderimpl.service.impl;

import com.technokratos.eateasy.common.exception.ConflictServiceException;
import com.technokratos.eateasy.common.exception.NotFoundServiceException;
import com.technokratos.eateasy.orderapi.dto.OrderRequestDto;
import com.technokratos.eateasy.orderapi.dto.OrderResponseDto;
import com.technokratos.eateasy.orderapi.dto.Page;
import com.technokratos.eateasy.orderapi.dto.OrderLogResponseDto;
import com.technokratos.eateasy.orderimpl.mapper.OrderLogMapper;
import com.technokratos.eateasy.orderimpl.mapper.OrderMapper;
import com.technokratos.eateasy.orderimpl.model.OrderEntity;
import com.technokratos.eateasy.orderimpl.repository.OrderRepository;
import com.technokratos.eateasy.orderimpl.service.OrderService;
import com.technokratos.eateasy.orderimpl.service.CartClientService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final OrderRepository repository;
    private final OrderMapper orderMapper;
    private final OrderLogMapper orderLogMapper;
    private final CartClientService cartClientService;
    @Override
    public OrderResponseDto getById(UUID id) {
        return repository.findById(id)
                .map(orderMapper::toDto)
                .orElseThrow(() -> new NotFoundServiceException(String.format("Order not found with id: %s!", id)));
    }
    @Override
    public OrderResponseDto create(OrderRequestDto requestDto) {
        OrderEntity order = orderMapper.toEntity(requestDto);
        UUID orderId = UUID.randomUUID();
        order.setId(orderId);
        UUID cartId = cartClientService.getByUserId(requestDto.userId()).id();
        order.setCartId(cartId);
        if(repository.isOrderWithThisCartIdExist(cartId)){
            throw new ConflictServiceException(
                    "Order already exist!",
                    String.format("Order with cart %s already exist", cartId)
            );
        }
        repository.save(order);
        return getById(orderId);
    }
    @Override
    public List<OrderLogResponseDto> getListOfAllStatus(UUID orderId) {
        if (getById(orderId) == null){
            throw new NotFoundServiceException(String.format("Order not found with id: %s!", orderId));
        }
        return repository.getListOfAllStatus(orderId)
                .stream()
                .map(orderLogMapper::toDto)
                .collect(Collectors.toList());
    }
    @Override
    public Page<OrderResponseDto> getPageableUserOrders(UUID userId, int page, int pageSize, Boolean actual) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        List<OrderEntity> listOfOrders = (actual)
                ? repository.findAllActualByUser(userId, pageable)
                : repository.findAllByUser(userId, pageable);
        List<OrderResponseDto> ordersDto = listOfOrders
                .stream()
                .map(orderMapper::toDto)
                .collect(Collectors.toList());
        int countOfOrders = repository.countByUser(userId);
        return Page.<OrderResponseDto>builder()
                .totalOrders(countOfOrders)
                .currentPage(page)
                .ordersInPage(pageSize)
                .orders(ordersDto)
                .build();
    }
}

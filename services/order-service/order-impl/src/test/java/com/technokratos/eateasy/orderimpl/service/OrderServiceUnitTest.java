package com.technokratos.eateasy.orderimpl.service;

import com.technokratos.eateasy.common.exception.ConflictServiceException;
import com.technokratos.eateasy.common.exception.NotFoundServiceException;
import com.technokratos.eateasy.orderapi.dto.OrderLogResponseDto;
import com.technokratos.eateasy.orderapi.dto.OrderRequestDto;
import com.technokratos.eateasy.orderapi.dto.OrderResponseDto;
import com.technokratos.eateasy.orderimpl.mapper.OrderLogMapper;
import com.technokratos.eateasy.orderimpl.mapper.OrderMapper;
import com.technokratos.eateasy.orderimpl.model.OrderEntity;
import com.technokratos.eateasy.orderimpl.model.OrderLogEntity;
import com.technokratos.eateasy.orderimpl.model.Status;
import com.technokratos.eateasy.orderimpl.repository.OrderRepository;
import com.technokratos.eateasy.orderimpl.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceUnitTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderMapper orderMapper;
    @Mock
    private OrderLogMapper orderLogMapper;
    @InjectMocks
    private OrderServiceImpl orderService;
    private UUID orderId;
    private UUID cartId;
    private UUID userId;
    private OrderEntity orderEntity;
    private OrderRequestDto orderRequestDto;
    private OrderResponseDto orderResponseDto;
    private OrderLogEntity orderLogEntity;
    private OrderLogResponseDto orderLogResponseDto;
    @BeforeEach
    void setup() {
        orderId = UUID.randomUUID();
        cartId = UUID.randomUUID();
        userId = UUID.randomUUID();
        orderEntity = OrderEntity.builder()
                .id(orderId)
                .cartId(cartId)
                .userId(userId)
                .deliveryAddress("Test Address")
                .build();
        orderRequestDto = new OrderRequestDto(userId, "Test Address");
        orderResponseDto = new OrderResponseDto(cartId, userId, "Test Address");
        orderLogEntity = new OrderLogEntity();
        orderLogEntity.setOrderId(orderId);
        orderLogEntity.setStatus(Status.valueOf("CREATED"));
        orderLogResponseDto = new OrderLogResponseDto(orderId, "CREATED", null);
    }

    @Test
    void getById_shouldReturnOrder() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(orderEntity));
        when(orderMapper.toDto(orderEntity)).thenReturn(orderResponseDto);
        OrderResponseDto result = orderService.getById(orderId);
        assertNotNull(result);
        assertEquals(orderResponseDto, result);
    }

    @Test
    void getById_shouldThrowNotFound() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());
        assertThrows(NotFoundServiceException.class, () -> orderService.getById(orderId));
    }

    @Test
    void create_shouldCreateOrderSuccessfully() {
        when(orderMapper.toEntity(orderRequestDto)).thenReturn(orderEntity);
        when(orderRepository.isOrderWithThisCartIdExist(any())).thenReturn(false);
        doNothing().when(orderRepository).save(any());
        when(orderRepository.findById(any())).thenReturn(Optional.of(orderEntity));
        when(orderMapper.toDto(orderEntity)).thenReturn(orderResponseDto);
        OrderResponseDto result = orderService.create(orderRequestDto);
        assertNotNull(result);
        verify(orderRepository).save(any());
    }

    @Test
    void create_shouldThrowConflictWhenCartExists() {
        when(orderMapper.toEntity(orderRequestDto)).thenReturn(orderEntity);
        when(orderRepository.isOrderWithThisCartIdExist(any())).thenReturn(true);
        assertThrows(ConflictServiceException.class, () -> orderService.create(orderRequestDto));
    }

    @Test
    void getListOfAllStatus_shouldReturnStatuses() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(orderEntity));
        when(orderMapper.toDto(orderEntity)).thenReturn(orderResponseDto);
        when(orderRepository.getListOfAllStatus(orderId)).thenReturn(List.of(orderLogEntity));
        when(orderLogMapper.toDto(orderLogEntity)).thenReturn(orderLogResponseDto);
        List<OrderLogResponseDto> result = orderService.getListOfAllStatus(orderId);
        assertEquals(1, result.size());
        assertEquals(orderLogResponseDto, result.get(0));
    }

    @Test
    void getListOfAllStatus_shouldThrowIfOrderNotFound() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());
        assertThrows(NotFoundServiceException.class, () -> orderService.getListOfAllStatus(orderId));
    }
}

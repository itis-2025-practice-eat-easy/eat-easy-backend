package com.technokratos.eateasy.orderimpl.service.impl;

import com.technokratos.eateasy.common.exception.NotFoundServiceException;
import com.technokratos.eateasy.orderapi.OrderRequestDto;
import com.technokratos.eateasy.orderapi.OrderResponseDto;
import com.technokratos.eateasy.orderapi.Page;
import com.technokratos.eateasy.orderapi.StatusResponseDto;
import com.technokratos.eateasy.orderimpl.mapper.OrderMapper;
import com.technokratos.eateasy.orderimpl.model.OrderEntity;
import com.technokratos.eateasy.orderimpl.repository.OrderRepository;
import com.technokratos.eateasy.orderimpl.service.OrderService;
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
    private final OrderMapper mapper;
    @Override
    public OrderResponseDto getById(UUID id) {
        return repository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new NotFoundServiceException(String.format("Order not found with id: %s!", id)));
    }

    @Override
    public void create(OrderRequestDto requestDto) {
        OrderEntity order = mapper.toEntity(requestDto);
        order.setId(UUID.randomUUID());

        //TODO: через фейгин вытащить id корзины (сервиса пока что нет)

        order.setCartId(UUID.fromString("892346c8-aaf9-4458-b92c-1a0c95d03702"));


        repository.save(order);
        log.info("Order created");
    }


    @Override
    public List<StatusResponseDto> getListOfAllStatus(UUID orderId) {
        return repository.getListOfAllStatus(orderId);
    }

    @Override
    public Page<OrderResponseDto> getPagableUserOrders(int page, int pageSize, Boolean actual) {

        //TODO: нужно получить id пользователя и добавить в запрос

        Pageable pageable = PageRequest.of(page, pageSize);
        List<OrderEntity> listOfOrders = (actual)
                ? repository.findAllActual(pageable)
                : repository.findAll(pageable);

        List<OrderResponseDto> ordersDto = listOfOrders
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());

        return Page.<OrderResponseDto>builder()
                .totalOrders(ordersDto.size())
                .currentPage(page)
                .ordersInPage(pageSize)
                .orders(ordersDto)
                .build();
    }
}

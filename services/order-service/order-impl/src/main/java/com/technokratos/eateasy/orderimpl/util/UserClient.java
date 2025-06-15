package com.technokratos.eateasy.orderimpl.util;

import org.springframework.cloud.openfeign.FeignClient;


@FeignClient(name = "cart-service", //имя сервиса в Consul
    path = "/api/v1/carts") //базовый путь как в API
public interface UserClient {

}
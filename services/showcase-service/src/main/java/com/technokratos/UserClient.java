package com.technokratos;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "user-service", //имя сервиса в Consul
    path = "/api/v1/users") //базовый путь как в API
public interface UserClient {
    @PostMapping
    UserDto createUser(@RequestBody UserDto userDto);

    @GetMapping
    List<UserDto> getAllUsers();

    // для примера сохраним пользователя и получим список всех пользователей
}

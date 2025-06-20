package com.technokratos.eateasy.authenticationservice.client;

import com.technokratos.eateasy.userapi.dto.UserWithHashPasswordResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "${custom.service.user-service.name}")
public interface UserClient {

    @GetMapping(value = "/api/v1/users", params = "email")
    UserWithHashPasswordResponseDto getUserByEmail(@RequestParam("email")  String email);
}

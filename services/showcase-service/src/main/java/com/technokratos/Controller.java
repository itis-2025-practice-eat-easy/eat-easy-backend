package com.technokratos;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/showcase")
public class Controller {

    private final Service service;

    @PostMapping
    public UserDto showcase() {
        return service.createUser(UserDto.builder()
                .username("superUser123")
                .email("superuser@example.com")
                .password("StrongP@ssw0rd!")
                .firstName("Super")
                .lastName("User")
                .role("ADMIN")
                .build());
    }

    @GetMapping
    public List<UserDto> showcase2() {
        return service.getAllUsers();
    }
}

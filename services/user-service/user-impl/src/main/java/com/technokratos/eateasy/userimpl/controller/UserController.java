package com.technokratos.eateasy.userimpl.controller;

import com.technokratos.eateasy.userapi.api.UserApi;
import com.technokratos.eateasy.userapi.dto.UserRequestDto;
import com.technokratos.eateasy.userapi.dto.UserResponseDto;
import com.technokratos.eateasy.userapi.dto.UserWithHashPasswordResponseDto;
import com.technokratos.eateasy.userimpl.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController implements UserApi {
    private final UserService service;
    @Override
    public List<UserResponseDto> getAllUsers() {
        log.info("Received request to get all users");
        return service.getAll();
    }
    @Override
    public UserResponseDto getUserById(UUID id) {
        log.info("Received request to get user by id: {}", id);
        return service.getById(id);
    }
    @Override
    public UserResponseDto createUser(UserRequestDto userDto) {
        log.info("Received request to create user: {}", userDto);
        return service.create(userDto);
    }
    @Override
    public UserWithHashPasswordResponseDto getUserByEmail(String email) {
        log.info("Received request to get user by email: {}", email);
        return service.getUserByEmail(email);
    }
    @Override
    public UserResponseDto updateUser(UUID id, UserRequestDto userDto) {
        log.info("Received request to update user with id: {}, data: {}", id, userDto);
        return service.update(id, userDto);
    }
    @Override
    public void deleteUser(UUID id) {
        log.info("Received request to delete user with id: {}", id);
        service.delete(id);
    }
}

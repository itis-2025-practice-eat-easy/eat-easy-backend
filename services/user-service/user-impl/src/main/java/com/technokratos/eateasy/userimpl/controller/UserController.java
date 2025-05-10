package com.technokratos.eateasy.userimpl.controller;

import com.technokratos.eateasy.userapi.api.UserApi;
import com.technokratos.eateasy.userapi.dto.UserRequestDto;
import com.technokratos.eateasy.userapi.dto.UserResponseDto;
import com.technokratos.eateasy.userimpl.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController implements UserApi {

    private final UserService userService;

    @Override
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        log.info("Received request to get all users");
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @Override
    public ResponseEntity<UserResponseDto> getUserById(UUID id) {
        log.info("Received request to get user by id: {}", id);
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @Override
    public ResponseEntity<UserResponseDto> createUser(UserRequestDto userDto) {
        log.info("Received request to create user: {}", userDto);
        return new ResponseEntity<>(userService.createUser(userDto), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<UserResponseDto> getUserByEmail(String email) {
        log.info("Received request to get user by email: {}", email);
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    @Override
    public ResponseEntity<UserResponseDto> updateUser(UUID id, UserRequestDto userDto) {
        log.info("Received request to update user with id: {}, data: {}", id, userDto);
        return ResponseEntity.ok(userService.updateUser(id, userDto));
    }

    @Override
    public ResponseEntity<Void> deleteUser(UUID id) {
        log.info("Received request to delete user with id: {}", id);
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
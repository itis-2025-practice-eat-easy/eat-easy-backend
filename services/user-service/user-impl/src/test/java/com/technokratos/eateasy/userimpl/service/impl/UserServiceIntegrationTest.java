package com.technokratos.eateasy.userimpl.service.impl;

import com.technokratos.eateasy.userapi.dto.UserRequestDto;
import com.technokratos.eateasy.userapi.dto.UserResponseDto;
import com.technokratos.eateasy.userimpl.exception.UserAlreadyExistsException;
import com.technokratos.eateasy.userimpl.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestPropertySource("classpath:application-test.yaml")
public class UserServiceIntegrationTest {

    @Autowired
    private UserServiceImpl userService;

    private UserRequestDto userRequestDto;

    final String USERNAME = "rbrmnv";
    final String PASSWORD = "password123";
    final String EMAIL = "robert@mail.ru";
    final String FIRST_NAME = "Robert";
    final String LAST_NAME = "Romanov";

    @BeforeEach
    void setUp() {
        userRequestDto = UserRequestDto.builder()
                .username(USERNAME)
                .email(EMAIL)
                .password(PASSWORD)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .build();
    }

    @Test
    void testCreateUser() {
        UserResponseDto userResponseDto = userService.createUser(userRequestDto);

        assertNotNull(userResponseDto);
        assertNotNull(userResponseDto.getId());
        assertEquals(USERNAME, userResponseDto.getUsername());
        assertEquals(EMAIL, userResponseDto.getEmail());
    }

    @Test
    void testCreateUser_UsernameAlreadyExists() {
        userService.createUser(userRequestDto);

        UserRequestDto duplicateUser = new UserRequestDto();
        duplicateUser.setUsername(USERNAME);
        duplicateUser.setEmail(EMAIL + "1");
        duplicateUser.setPassword(PASSWORD);

        assertThrows(UserAlreadyExistsException.class, () -> {
            userService.createUser(duplicateUser);
        });
    }

    @Test
    void testGetUserById() {
        UserResponseDto createdUser = userService.createUser(userRequestDto);

        UserResponseDto foundUser = userService.getUserById(createdUser.getId());

        assertNotNull(foundUser);
        assertEquals(createdUser.getId(), foundUser.getId());
    }

    @Test
    void testGetUserByEmail() {
        userService.createUser(userRequestDto);

        UserResponseDto foundUser = userService.getUserByEmail(EMAIL);

        assertNotNull(foundUser);
        assertEquals(EMAIL, foundUser.getEmail());
    }

    @Test
    void testUpdateUser() {
        UserResponseDto createdUser = userService.createUser(userRequestDto);

        userRequestDto.setUsername(USERNAME + "NEW");
        userRequestDto.setEmail(EMAIL + "NEW");

        UserResponseDto updatedUser = userService.updateUser(createdUser.getId(), userRequestDto);

        assertNotNull(updatedUser);
        assertEquals(USERNAME + "NEW", updatedUser.getUsername());
        assertEquals(EMAIL + "NEW", updatedUser.getEmail());
    }

    @Test
    void testDeleteUser() {
        UserResponseDto createdUser = userService.createUser(userRequestDto);

        userService.deleteUser(createdUser.getId());

        assertThrows(UserNotFoundException.class, () -> {
            userService.getUserById(createdUser.getId());
        });
    }

    @Test
    void testDeleteUser_UserNotFound() {
        assertThrows(UserNotFoundException.class, () -> {
            userService.deleteUser(UUID.randomUUID());
        });
    }
}

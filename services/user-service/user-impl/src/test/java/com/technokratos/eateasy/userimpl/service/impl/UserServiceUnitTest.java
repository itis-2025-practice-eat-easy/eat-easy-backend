package com.technokratos.eateasy.userimpl.service.impl;

import com.technokratos.eateasy.userapi.dto.UserRequestDto;
import com.technokratos.eateasy.userapi.dto.UserResponseDto;
import com.technokratos.eateasy.userimpl.exception.UserAlreadyExistsException;
import com.technokratos.eateasy.userimpl.exception.UserNotFoundException;
import com.technokratos.eateasy.userimpl.mapper.UserMapper;
import com.technokratos.eateasy.userimpl.model.UserEntity;
import com.technokratos.eateasy.userimpl.repository.UserRepository;
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
class UserServiceUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private UserEntity userEntity;
    private UserRequestDto userRequestDto;
    private UserResponseDto userResponseDto;

    @BeforeEach
    void setUp() {

        final String USERNAME = "rbrmnv";
        final String PASSWORD = "password123";
        final String EMAIL = "robert@mail.ru";
        final String FIRST_NAME = "Robert";
        final String LAST_NAME = "Romanov";

        UUID userId = UUID.randomUUID();

        userEntity = UserEntity.builder()
                .id(UUID.randomUUID())
                .username(USERNAME)
                .password(PASSWORD)
                .email(EMAIL)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .build();

        userRequestDto = UserRequestDto.builder()
                .username(USERNAME)
                .email(EMAIL)
                .password(PASSWORD)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .build();

        userResponseDto = UserResponseDto.builder()
                .id(userId)
                .username(USERNAME)
                .email(EMAIL)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .build();
    }


    @Test
    void getAllUsersTest() {
        when(userRepository.findAll()).thenReturn(List.of(userEntity));
        when(userMapper.toDto(userEntity)).thenReturn(userResponseDto);

        List<UserResponseDto> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(userResponseDto, result.get(0));
    }

    @Test
    void getUserByIdTest() {
        when(userRepository.findById(userEntity.getId())).thenReturn(Optional.of(userEntity));
        when(userMapper.toDto(userEntity)).thenReturn(userResponseDto);

        UserResponseDto result = userService.getUserById(userEntity.getId());

        assertNotNull(result);
        assertEquals(userResponseDto, result);
    }

    @Test
    void getUserByIdExceptionTest() {
        UUID nonExistentId = UUID.randomUUID();
        when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(nonExistentId));
    }

    @Test
    void createUserTest() {
        when(userRepository.existsByUsername(userRequestDto.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(userRequestDto.getEmail())).thenReturn(false);
        when(userMapper.toEntity(userRequestDto)).thenReturn(userEntity);
        when(userRepository.save(userEntity)).thenReturn(userEntity);
        when(userMapper.toDto(userEntity)).thenReturn(userResponseDto);

        UserResponseDto result = userService.createUser(userRequestDto);

        assertNotNull(result);
        assertEquals(userResponseDto, result);
    }

    @Test
    void createUserUsernameExceptionTest() {
        when(userRepository.existsByUsername(userRequestDto.getUsername())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(userRequestDto));
    }

    @Test
    void createUserEmailExceptionTest() {
        when(userRepository.existsByUsername(userRequestDto.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(userRequestDto.getEmail())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(userRequestDto));
    }


    @Test
    void updateUserUsernameExceptionTest() {
        UUID nonExistentId = UUID.randomUUID();
        when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateUser(nonExistentId, userRequestDto));
    }

    @Test
    void updateUserEmailExceptionTest() {
        when(userRepository.findById(userEntity.getId())).thenReturn(Optional.of(userEntity));

        userRequestDto.setUsername("not_rbrmnv");
        when(userRepository.existsByUsername("not_rbrmnv")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class,
                () -> userService.updateUser(userEntity.getId(), userRequestDto));
    }


    @Test
    void deleteUserTest() {
        when(userRepository.existsById(userEntity.getId())).thenReturn(true);

        userService.deleteUser(userEntity.getId());

        verify(userRepository, times(1)).deleteById(userEntity.getId());
    }

    @Test
    void deleteUserExceptionTest() {
        UUID nonExistentId = UUID.randomUUID();
        when(userRepository.existsById(nonExistentId)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(nonExistentId));
    }
}

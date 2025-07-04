package com.technokratos.eateasy.userimpl.service.impl;

import com.technokratos.eateasy.common.exception.ConflictServiceException;
import com.technokratos.eateasy.common.exception.NotFoundServiceException;
import com.technokratos.eateasy.userapi.dto.UserRequestCreateDto;
import com.technokratos.eateasy.userapi.dto.UserResponseDto;
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
    private UserRequestCreateDto userRequestCreateDto;
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

        userRequestCreateDto = UserRequestCreateDto.builder()
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

        List<UserResponseDto> result = userService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(userResponseDto, result.get(0));
    }

    @Test
    void getUserByIdTest() {
        when(userRepository.findById(userEntity.getId())).thenReturn(Optional.of(userEntity));
        when(userMapper.toDto(userEntity)).thenReturn(userResponseDto);

        UserResponseDto result = userService.getById(userEntity.getId());

        assertNotNull(result);
        assertEquals(userResponseDto, result);
    }

    @Test
    void getUserByIdExceptionTest() {
        UUID nonExistentId = UUID.randomUUID();
        when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(NotFoundServiceException.class, () -> userService.getById(nonExistentId));
    }

    @Test
    void createUserTest() {
        when(userRepository.existsByUsername(userRequestCreateDto.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(userRequestCreateDto.getEmail())).thenReturn(false);
        when(userMapper.toEntity(userRequestCreateDto)).thenReturn(userEntity);
        when(userRepository.save(userEntity)).thenReturn(userEntity);
        when(userMapper.toDto(userEntity)).thenReturn(userResponseDto);

        UserResponseDto result = userService.create(userRequestCreateDto);

        assertNotNull(result);
        assertEquals(userResponseDto, result);
    }

    @Test
    void createUserUsernameExceptionTest() {
        when(userRepository.existsByUsername(userRequestCreateDto.getUsername())).thenReturn(true);

        assertThrows(ConflictServiceException.class, () -> userService.create(userRequestCreateDto));
    }

    @Test
    void createUserEmailExceptionTest() {
        when(userRepository.existsByUsername(userRequestCreateDto.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(userRequestCreateDto.getEmail())).thenReturn(true);

        assertThrows(ConflictServiceException.class, () -> userService.create(userRequestCreateDto));
    }


    @Test
    void updateUserUsernameExceptionTest() {
        UUID nonExistentId = UUID.randomUUID();
        when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(NotFoundServiceException.class, () -> userService.update(nonExistentId, userRequestCreateDto));
    }

    @Test
    void updateUserEmailExceptionTest() {
        when(userRepository.findById(userEntity.getId())).thenReturn(Optional.of(userEntity));

        userRequestCreateDto.setUsername("not_rbrmnv");
        when(userRepository.existsByUsername("not_rbrmnv")).thenReturn(true);

        assertThrows(ConflictServiceException.class,
                () -> userService.update(userEntity.getId(), userRequestCreateDto));
    }

    @Test
    void deleteUserExceptionTest() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundServiceException.class, () -> userService.delete(id));
        verify(userRepository, never()).deleteById(any());
    }
}

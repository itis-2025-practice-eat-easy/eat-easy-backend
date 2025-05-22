package com.technokratos.eateasy.userimpl.service;

import com.technokratos.eateasy.userapi.dto.UserRequestDto;
import com.technokratos.eateasy.userapi.dto.UserResponseDto;
import com.technokratos.eateasy.userimpl.model.UserEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {

    List<UserResponseDto> getAll();

    UserResponseDto getById(UUID id);

    UserResponseDto create(UserRequestDto userDto);

    UserResponseDto update(UUID id, UserRequestDto userDto);

    void delete(UUID id);

    UserResponseDto getByEmail(String email);

    Optional<UserEntity> getEntityByEmail(String email);
}

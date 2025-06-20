package com.technokratos.eateasy.userimpl.service;

import com.technokratos.eateasy.userapi.dto.UserRequestCreateDto;
import com.technokratos.eateasy.userapi.dto.UserRequestUpdateDto;
import com.technokratos.eateasy.userapi.dto.UserResponseDto;
import com.technokratos.eateasy.userapi.dto.UserWithHashPasswordResponseDto;
import com.technokratos.eateasy.userimpl.model.UserEntity;

import java.util.List;
import java.util.UUID;

public interface UserService {
    List<UserResponseDto> getAll();
    UserResponseDto getById(UUID id);
    UserResponseDto create(UserRequestCreateDto userDto);
    UserResponseDto update(UUID id, UserRequestUpdateDto userDto);
    void delete(UUID id);
    UserEntity getEntityByEmail(String email);
    UserWithHashPasswordResponseDto getUserByEmail(String email);
}

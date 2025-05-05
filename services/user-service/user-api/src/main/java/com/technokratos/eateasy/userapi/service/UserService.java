package com.technokratos.eateasy.userapi.service;

import com.technokratos.eateasy.userapi.dto.UserRequestDto;
import com.technokratos.eateasy.userapi.dto.UserResponseDto;

import java.util.List;
import java.util.UUID;

public interface UserService {

    List<UserResponseDto> getAllUsers();

    UserResponseDto  getUserById(UUID id);

    UserResponseDto  createUser(UserRequestDto userDto);

    UserResponseDto updateUser(UUID id, UserRequestDto userDto);

    void deleteUser(UUID id);

    UserResponseDto  getUserByEmail(String email);
}

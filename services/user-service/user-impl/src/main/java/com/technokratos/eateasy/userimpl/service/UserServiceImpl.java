package com.technokratos.eateasy.userimpl.service;


import com.technokratos.eateasy.userapi.dto.UserRequestDto;
import com.technokratos.eateasy.userapi.dto.UserResponseDto;
import com.technokratos.eateasy.userapi.exception.UserAlreadyExistsException;
import com.technokratos.eateasy.userapi.exception.UserNotFoundException;
import com.technokratos.eateasy.userapi.service.UserService;
import com.technokratos.eateasy.userdb.mapper.UserMapper;
import com.technokratos.eateasy.userdb.model.UserEntity;
import com.technokratos.eateasy.userdb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    @Override
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDto getUserById(UUID id) {
        return userRepository.findById(id)
                .map(userMapper::toDto)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }

    @Override
    public UserResponseDto getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::toDto)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
    }

    @Transactional
    @Override
    public UserResponseDto createUser(UserRequestDto userDto) {
        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists");
        }
        
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists");
        }

        UserEntity user = userMapper.toEntity(userDto);
        UserEntity savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    @Transactional
    @Override
    public UserResponseDto updateUser(UUID id, UserRequestDto userDto) {
        UserEntity existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        if (!existingUser.getUsername().equals(userDto.getUsername()) && 
            userRepository.existsByUsername(userDto.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists");
        }

        if (!existingUser.getEmail().equals(userDto.getEmail()) && 
            userRepository.existsByEmail(userDto.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists");
        }

        userMapper.updateEntity(existingUser, userDto);
        UserEntity updatedUser = userRepository.save(existingUser);
        return userMapper.toDto(updatedUser);
    }

    @Transactional
    @Override
    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }
}
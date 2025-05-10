package com.technokratos.eateasy.userimpl.service.impl;


import com.technokratos.eateasy.userapi.dto.UserRequestDto;
import com.technokratos.eateasy.userapi.dto.UserResponseDto;
import com.technokratos.eateasy.userimpl.exception.UserAlreadyExistsException;
import com.technokratos.eateasy.userimpl.exception.UserNotFoundException;
import com.technokratos.eateasy.userimpl.mapper.UserMapper;
import com.technokratos.eateasy.userimpl.model.UserEntity;
import com.technokratos.eateasy.userimpl.repository.UserRepository;
import com.technokratos.eateasy.userimpl.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
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
            log.warn("Username already exists: {}", userDto.getUsername());
            throw new UserAlreadyExistsException("Username already exists");
        }
        
        if (userRepository.existsByEmail(userDto.getEmail())) {
            log.warn("Email already exists: {}", userDto.getEmail());
            throw new UserAlreadyExistsException("Email already exists");
        }

        UserEntity user = userMapper.toEntity(userDto);
        UserEntity savedUser = userRepository.save(user);
        log.info("User created with ID: {}", savedUser.getId());
        return userMapper.toDto(savedUser);
    }

    @Transactional
    @Override
    public UserResponseDto updateUser(UUID id, UserRequestDto userDto) {
        UserEntity existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        if (!existingUser.getUsername().equals(userDto.getUsername()) && 
            userRepository.existsByUsername(userDto.getUsername())) {
            log.warn("Username already exists: {}", userDto.getUsername());
            throw new UserAlreadyExistsException("Username already exists");
        }

        if (!existingUser.getEmail().equals(userDto.getEmail()) && 
            userRepository.existsByEmail(userDto.getEmail())) {
            log.warn("Email already exists: {}", userDto.getEmail());
            throw new UserAlreadyExistsException("Email already exists");
        }

        userMapper.updateEntity(existingUser, userDto);
        UserEntity updatedUser = userRepository.save(existingUser);
        log.info("User updated with ID: {}", existingUser.getId());
        return userMapper.toDto(updatedUser);
    }

    @Transactional
    @Override
    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            log.warn("User not found with id: " + id);
            throw new UserNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
        log.info("User deleted with ID: {}", id);
    }
}
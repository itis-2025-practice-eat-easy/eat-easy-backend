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
    public List<UserResponseDto> getAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDto getById(UUID id) {
        return userRepository.findById(id)
                .map(userMapper::toDto)
                .orElseThrow(() -> new UserNotFoundException(String.format("User not found with id: %s!", id)));
    }

    @Override
    public UserResponseDto getByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::toDto)
                .orElseThrow(() -> new UserNotFoundException(String.format("User not found with email: %s!", email)));
    }

    @Transactional
    @Override
    public UserResponseDto create(UserRequestDto userDto) {
        if (userRepository.existsByUsername(userDto.getUsername())) {
            log.debug("Username already exists: {}", userDto.getUsername());
            throw new UserAlreadyExistsException("Username already exists");
        }
        
        if (userRepository.existsByEmail(userDto.getEmail())) {
            log.debug("Email already exists: {}", userDto.getEmail());
            throw new UserAlreadyExistsException("Email already exists");
        }

        UserEntity user = userMapper.toEntity(userDto);
        UserEntity savedUser = userRepository.save(user);
        log.info("User created with ID: {}", savedUser.getId());
        return userMapper.toDto(savedUser);
    }

    @Transactional
    @Override
    public UserResponseDto update(UUID id, UserRequestDto userDto) {
        UserEntity existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(String.format("User not found with id: %s!", id)));

        if (!existingUser.getUsername().equals(userDto.getUsername()) && 
            userRepository.existsByUsername(userDto.getUsername())) {
            log.debug("Username already exists: {}", userDto.getUsername());
            throw new UserAlreadyExistsException("Username already exists");
        }

        if (!existingUser.getEmail().equals(userDto.getEmail()) && 
            userRepository.existsByEmail(userDto.getEmail())) {
            log.debug("Email already exists: {}", userDto.getEmail());
            throw new UserAlreadyExistsException("Email already exists");
        }

        userMapper.updateEntity(existingUser, userDto);
        UserEntity updatedUser = userRepository.save(existingUser);
        log.info("User updated with ID: {}", existingUser.getId());
        return userMapper.toDto(updatedUser);
    }

    @Transactional
    @Override
    public void delete(UUID id) {
        getById(id);
        userRepository.deleteById(id);
        log.info(String.format("User deleted with id: %s!", id));
    }
}
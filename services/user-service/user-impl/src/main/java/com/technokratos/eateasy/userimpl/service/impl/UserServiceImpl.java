package com.technokratos.eateasy.userimpl.service.impl;

import com.technokratos.eateasy.common.exception.BadRequestServiceException;
import com.technokratos.eateasy.common.exception.ConflictServiceException;
import com.technokratos.eateasy.common.exception.NotFoundServiceException;
import com.technokratos.eateasy.userapi.dto.UserRequestDto;
import com.technokratos.eateasy.userapi.dto.UserResponseDto;
import com.technokratos.eateasy.userapi.dto.UserWithHashPasswordResponseDto;
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
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final UserMapper mapper;
    @Override
    public List<UserResponseDto> getAll() {
        return repository.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }
    @Override
    public UserResponseDto getById(UUID id) {
        return repository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new NotFoundServiceException(String.format("User not found with id: %s!", id)));
    }
    @Override
    public UserEntity getEntityByEmail(String email) {
        return repository.findByEmail(email)
                .orElseThrow(() -> new NotFoundServiceException(
                        String.format("User not found with email: %s!", email))
                );
    }
    @Override
    public UserWithHashPasswordResponseDto getUserByEmail(String email) {
        UserEntity entity = getEntityByEmail(email);
        return new UserWithHashPasswordResponseDto(mapper.toDto(entity), entity.getPassword());
    }

    @Transactional
    @Override
    public UserResponseDto create(UserRequestDto userDto) {
        if (repository.existsByUsername(userDto.getUsername())) {
            log.debug("Username already exists: {}", userDto.getUsername());
            throw new ConflictServiceException("Username already exists");
        }
        if (repository.existsByEmail(userDto.getEmail())) {
            log.debug("Email already exists: {}", userDto.getEmail());
            throw new ConflictServiceException("Email already exists");
        }
        UserEntity user = mapper.toEntity(userDto);
        UserEntity savedUser = repository.save(user);
        log.info("User created with ID: {}", savedUser.getId());
        return mapper.toDto(savedUser);
    }

    @Transactional
    @Override
    public UserResponseDto update(UUID id, UserRequestDto userDto) {
        UserEntity existingUser = repository.findById(id)
                .orElseThrow(() -> new NotFoundServiceException(String.format("User not found with id: %s!", id)));
        if (!existingUser.getUsername().equals(userDto.getUsername()) && 
            repository.existsByUsername(userDto.getUsername())) {
            log.debug("Username already exists: {}", userDto.getUsername());
            throw new ConflictServiceException("Username already exists");
        }
        if (!existingUser.getEmail().equals(userDto.getEmail()) && 
            repository.existsByEmail(userDto.getEmail())) {
            log.debug("Email already exists: {}", userDto.getEmail());
            throw new ConflictServiceException("Email already exists");
        }
        mapper.updateEntity(existingUser, userDto);
        UserEntity updatedUser = repository.save(existingUser);
        log.info("User updated with ID: {}", existingUser.getId());
        return mapper.toDto(updatedUser);
    }
    @Transactional
    @Override
    public void delete(UUID id) {
        getById(id);
        repository.deleteById(id);
        log.info(String.format("User deleted with id: %s!", id));
    }
    private boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_.!#$%&’*+/=?`{|}~^-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$"
    );
}
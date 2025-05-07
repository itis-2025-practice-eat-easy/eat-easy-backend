package com.technokratos.eateasy.userimpl.mapper;


import com.technokratos.eateasy.userapi.dto.UserRequestDto;
import com.technokratos.eateasy.userapi.dto.UserResponseDto;
import com.technokratos.eateasy.userimpl.model.UserEntity;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final PasswordEncoder passwordEncoder;


    public UserResponseDto toDto(UserEntity entity) {
        return entity == null ? null :
                UserResponseDto.builder()
                        .id(entity.getId())
                        .username(entity.getUsername())
                        .email(entity.getEmail())
                        .firstName(entity.getFirstName())
                        .lastName(entity.getLastName())
                        .build();
    }

    public UserEntity toEntity(UserRequestDto dto) {
        return dto == null ? null :
                UserEntity.builder()
                        .username(dto.getUsername())
                        .email(dto.getEmail())
                        .password(passwordEncoder.encode(dto.getPassword()))
                        .firstName(dto.getFirstName())
                        .lastName(dto.getLastName())
                        .build();
    }

    public void updateEntity(UserEntity entity, UserRequestDto dto) {
        if (dto == null || entity == null) {
            return;
        }

        if (dto.getUsername() != null) {
            entity.setUsername(dto.getUsername());
        }
        if (dto.getEmail() != null) {
            entity.setEmail(dto.getEmail());
        }
        if (dto.getFirstName() != null) {
            entity.setFirstName(dto.getFirstName());
        }
        if (dto.getLastName() != null) {
            entity.setLastName(dto.getLastName());
        }
    }
}
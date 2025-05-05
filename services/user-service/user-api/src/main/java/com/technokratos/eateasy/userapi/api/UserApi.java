package com.technokratos.eateasy.userapi.api;

import com.technokratos.eateasy.userapi.dto.UserRequestDto;
import com.technokratos.eateasy.userapi.dto.UserResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "User Management", description = "API для управления пользователями")
public interface UserApi {

    @Operation(summary = "Получить всех пользователей", description = "Возвращает список всех зарегистрированных пользователей")
    @ApiResponse(responseCode = "200", description = "Успешный запрос", content = @Content(schema = @Schema(implementation = UserResponseDto.class)))
    @GetMapping
    ResponseEntity<List<UserResponseDto>> getAllUsers();

    @Operation(summary = "Получить пользователя по ID", description = "Возвращает пользователя по его уникальному идентификатору")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь найден", content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @GetMapping("/{id}")
    ResponseEntity<UserResponseDto> getUserById(
            @Parameter(description = "UUID пользователя", required = true, example = "123e4567-e89b-12d3-a456-426614174000", in = ParameterIn.PATH)
            @PathVariable UUID id);

    @Operation(summary = "Создать нового пользователя", description = "Регистрирует нового пользователя в системе")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Пользователь успешно создан", content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Невалидные данные пользователя")
    })
    @PostMapping
    ResponseEntity<UserResponseDto> createUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные нового пользователя", required = true,
                    content = @Content(schema = @Schema(implementation = UserRequestDto.class)))
            @Valid @RequestBody UserRequestDto userDto);

    @Operation(summary = "Получить пользователя по email", description = "Возвращает пользователя по его email адресу")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь найден", content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @GetMapping(params = "email")
    ResponseEntity<UserResponseDto> getUserByEmail(
            @Parameter(description = "Email пользователя", required = true, example = "user@example.com", in = ParameterIn.QUERY)
            @RequestParam @NotBlank @Email String email);

    @Operation(summary = "Обновить данные пользователя", description = "Обновляет информацию о существующем пользователе")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Данные пользователя обновлены", content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @PutMapping("/{id}")
    ResponseEntity<UserResponseDto> updateUser(
            @Parameter(description = "UUID пользователя", required = true, example = "123e4567-e89b-12d3-a456-426614174000", in = ParameterIn.PATH)
            @PathVariable UUID id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Обновленные данные пользователя", required = true,
                    content = @Content(schema = @Schema(implementation = UserRequestDto.class)))
            @Valid @RequestBody UserRequestDto userDto);

    @Operation(summary = "Удалить пользователя", description = "Удаляет пользователя из системы по его ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Пользователь успешно удален"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteUser(
            @Parameter(description = "UUID пользователя", required = true, example = "123e4567-e89b-12d3-a456-426614174000", in = ParameterIn.PATH)
            @PathVariable UUID id);
}
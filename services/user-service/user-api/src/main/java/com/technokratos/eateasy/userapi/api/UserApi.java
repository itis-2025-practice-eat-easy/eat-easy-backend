package com.technokratos.eateasy.userapi.api;

import com.technokratos.eateasy.userapi.dto.UserRequestCreateDto;
import com.technokratos.eateasy.userapi.dto.UserRequestUpdateDto;
import com.technokratos.eateasy.userapi.dto.UserResponseDto;
import com.technokratos.eateasy.userapi.dto.UserWithHashPasswordResponseDto;
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
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "User Management", description = "API for managing users")
@RequestMapping("/api/v1/users")
public interface UserApi {

    @Operation(summary = "Get all users", description = "Returns a list of all registered users")
    @ApiResponse(responseCode = "200", description = "Successful request", content = @Content(schema = @Schema(implementation = UserResponseDto.class)))
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    List<UserResponseDto> getAllUsers();

    @Operation(summary = "Get user by ID", description = "Returns a user by their unique identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found", content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    UserResponseDto getUserById(
            @Parameter(description = "User UUID", required = true, example = "123e4567-e89b-12d3-a456-426614174000", in = ParameterIn.PATH)
            @PathVariable UUID id);

    @Operation(summary = "Create new user", description = "Registers a new user in the system")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User successfully created", content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid user data")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    UserResponseDto createUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "New user data", required = true,
                    content = @Content(schema = @Schema(implementation = UserRequestCreateDto.class)))
            @Valid @RequestBody UserRequestCreateDto userDto);

    @Operation(summary = "Get user by email", description = "Returns a user by their email address")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found", content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping(params = "email")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    UserWithHashPasswordResponseDto getUserByEmail(
            @Parameter(description = "User email", required = true, example = "user@example.com", in = ParameterIn.QUERY)
            @RequestParam @NotBlank @Email String email);

    @Operation(summary = "Update user data", description = "Updates the information of an existing user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User data updated", content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    UserResponseDto updateUser(
            @Parameter(description = "User UUID", required = true, example = "123e4567-e89b-12d3-a456-426614174000", in = ParameterIn.PATH)
            @PathVariable UUID id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated user data", required = true,
                    content = @Content(schema = @Schema(implementation = UserRequestUpdateDto.class)))
            @Valid @RequestBody UserRequestUpdateDto userDto);

    @Operation(summary = "Delete user", description = "Deletes a user from the system by their ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User successfully deleted"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    void deleteUser(
            @Parameter(description = "User UUID", required = true, example = "123e4567-e89b-12d3-a456-426614174000", in = ParameterIn.PATH)
            @PathVariable UUID id);
}

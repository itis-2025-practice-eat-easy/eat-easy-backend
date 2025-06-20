package com.technokratos.eateasy.userapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.technokratos.eateasy.userapi.roleenum.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Data Transfer Object containing user response data")
public class UserResponseDto {
    @Schema(
            description = "Unique identifier of the user",
            example = "123e4567-e89b-12d3-a456-426614174000",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private UUID id;
    @Schema(
            description = "Unique username of the user",
            example = "john_doe",
            requiredMode = Schema.RequiredMode.REQUIRED,
            minLength = 3,
            maxLength = 50
    )
    private String username;
    @Schema(
            description = "Email address of the user",
            example = "john.doe@example.com",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String email;
    @Schema(
            description = "First name of the user",
            example = "John",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String firstName;
    @Schema(
            description = "Last name of the user",
            example = "Doe",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String lastName;
    @Schema(
            description = "Role of the user",
            example = "User",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private UserRole role;

}
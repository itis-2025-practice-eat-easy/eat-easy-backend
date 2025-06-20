package com.technokratos.eateasy.userapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Data Transfer Object containing user response data and hash password")
public class UserWithHashPasswordResponseDto {
    @Schema(
            description = "Data Transfer Object containing user response data",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private UserResponseDto userResponseDto;
    @Schema(
            description = "Unique hash password of the user",
            example = "$2a$10$zaOMN7q8hg7C0oV3uAzqd.SXFa.ulQvUa6eiTgFHhSvwkTzFK/wu.",
            requiredMode = Schema.RequiredMode.REQUIRED,
            minLength = 60
    )
    private String hashPassword;
}

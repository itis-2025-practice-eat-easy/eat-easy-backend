package com.technokratos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.lang.Nullable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto{
    private String username;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String  role;
}

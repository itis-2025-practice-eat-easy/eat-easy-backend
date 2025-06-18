package com.technokratos.eateasy.authenticationservice.userdetails;

import com.technokratos.eateasy.jwtauthenticationstarter.security.userdetails.IdentifiableUserDetails;
import com.technokratos.eateasy.userapi.dto.UserWithHashPasswordResponseDto;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.UUID;


public class UserResponseUserDetails implements IdentifiableUserDetails<UUID> {

    private final UUID id;
    private final String username;
    private final String password;
    private final String role;

    public UserResponseUserDetails(UserWithHashPasswordResponseDto userResponse) {
        this.id = userResponse.getUserResponseDto().getId();
        this.username = userResponse.getUserResponseDto().getEmail();
        this.password = userResponse.getHashPassword();
        this.role = "ROLE_%s".formatted(userResponse.getUserResponseDto().getRole().name());
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }
}

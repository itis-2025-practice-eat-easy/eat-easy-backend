package com.technokratos.eateasy.authenticationservice.service;

import com.technokratos.eateasy.authenticationservice.client.UserClient;
import com.technokratos.eateasy.authenticationservice.userdetails.UserResponseUserDetails;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceUserDetailService implements UserDetailsService {

    private final UserClient client;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.trace("Loading user by username: {}", username);
        try {
            var user = client.getUserByEmail(username);
            log.debug("User found by email: {}", username);
            return new UserResponseUserDetails(user);
        } catch (FeignException.NotFound | FeignException.BadRequest e) {
            log.debug("User with email '{}' not found", username, e);
            throw new UsernameNotFoundException("User with email '%s' not found".formatted(username));
        } catch (FeignException e) {
            log.error("Error occurred while fetching user by email: {}", username, e);
            throw new InternalAuthenticationServiceException("Error occurred while fetching user by email: %s".formatted(username), e);
        }
    }
}

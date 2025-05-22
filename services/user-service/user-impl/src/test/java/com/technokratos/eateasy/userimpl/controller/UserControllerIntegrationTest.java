package com.technokratos.eateasy.userimpl.controller;

import com.technokratos.eateasy.userapi.dto.UserRequestDto;
import com.technokratos.eateasy.userapi.dto.UserResponseDto;
import com.technokratos.eateasy.userapi.roleenum.UserRole;
import com.technokratos.eateasy.userimpl.config.TestRestTemplateConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = TestRestTemplateConfig.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = "test")
@Slf4j
public class UserControllerIntegrationTest {

    @Autowired
    TestRestTemplate testRestTemplate;

    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

    private UserRequestDto userRequestDto;
    private UUID createdUserId;

    final String USERNAME = "rbrmnv";
    final String PASSWORD = "password123";
    final String EMAIL = "robert@mail.ru";
    final String FIRST_NAME = "Robert";
    final String LAST_NAME = "Romanov";
    final UserRole ROLE = UserRole.USER;

    @BeforeEach
    void clearDatabase() {
        jdbcTemplate.getJdbcTemplate().execute("DELETE FROM users");
    }

    @Test
    void createTest() {
        userRequestDto = UserRequestDto.builder()
                .username(USERNAME)
                .email(EMAIL)
                .password(PASSWORD)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .role(ROLE)
                .build();


        ResponseEntity<UserResponseDto> response = testRestTemplate.exchange(
                "/users",
                HttpMethod.POST,
                new HttpEntity<>(userRequestDto),
                UserResponseDto.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        UUID userId = response.getBody().getId();
        assertNotNull(userId);

        jdbcTemplate.query(
                "select * from users where id = :id",
                new MapSqlParameterSource("id", userId),
                rs -> {
                    assertEquals(userRequestDto.getUsername(), rs.getString("username"));
                    assertEquals(userRequestDto.getEmail(), rs.getString("email"));
                    assertEquals(userRequestDto.getFirstName(), rs.getString("first_name"));
                    assertEquals(userRequestDto.getLastName(), rs.getString("last_name"));
                    assertEquals(userRequestDto.getRole().toString(), rs.getString("role"));
                    assertFalse(rs.next());
                }
        );
    }

    @Test
    void getAllUsersTest() {
        ResponseEntity<List<UserResponseDto>> response = testRestTemplate.exchange(
                "/users",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void getUserByIdTest() {
        userRequestDto = UserRequestDto.builder()
                .username(USERNAME)
                .email(EMAIL)
                .password(PASSWORD)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .role(ROLE)
                .build();

        ResponseEntity<UserResponseDto> createResponse = testRestTemplate.exchange(
                "/users",
                HttpMethod.POST,
                new HttpEntity<>(userRequestDto),
                UserResponseDto.class
        );
        createdUserId = createResponse.getBody().getId();

        ResponseEntity<UserResponseDto> response = testRestTemplate.exchange(
                "/users/" + createdUserId,
                HttpMethod.GET,
                null,
                UserResponseDto.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        UserResponseDto user = response.getBody();
        assertNotNull(user);
        assertEquals(createdUserId, user.getId());
        assertEquals(USERNAME, user.getUsername());
        assertEquals(EMAIL, user.getEmail());
        assertEquals(FIRST_NAME, user.getFirstName());
        assertEquals(LAST_NAME, user.getLastName());
        assertEquals(ROLE, user.getRole());
    }

    @Test
    void getUserByIdNotFoundTest() {
        UUID nonExistentId = UUID.randomUUID();
        ResponseEntity<String> response = testRestTemplate.exchange(
                "/users/" + nonExistentId,
                HttpMethod.GET,
                null,
                String.class
        );
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getUserByEmailTest() {
        userRequestDto = UserRequestDto.builder()
                .username(USERNAME)
                .email(EMAIL)
                .password(PASSWORD)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .role(ROLE)
                .build();

        ResponseEntity<UserResponseDto> createResponse = testRestTemplate.exchange(
                "/users",
                HttpMethod.POST,
                new HttpEntity<>(userRequestDto),
                UserResponseDto.class
        );

        createdUserId = createResponse.getBody().getId();

        ResponseEntity<UserResponseDto> response = testRestTemplate.exchange(
                "/users?email=" + EMAIL,
                HttpMethod.GET,
                null,
                UserResponseDto.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        UserResponseDto user = response.getBody();
        assertNotNull(user);
        assertEquals(createdUserId, user.getId());
        assertEquals(EMAIL, user.getEmail());
    }

    @Test
    void getUserByEmailNotFoundTest() {
        String nonExistentEmail = "nonexistent@mail.ru";
        ResponseEntity<String> response = testRestTemplate.exchange(
                "/users?email=" + nonExistentEmail,
                HttpMethod.GET,
                null,
                String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void updateUserTest() {
        userRequestDto = UserRequestDto.builder()
                .username(USERNAME)
                .email(EMAIL)
                .password(PASSWORD)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .role(ROLE)
                .build();

        ResponseEntity<UserResponseDto> createResponse = testRestTemplate.exchange(
                "/users",
                HttpMethod.POST,
                new HttpEntity<>(userRequestDto),
                UserResponseDto.class
        );
        createdUserId = createResponse.getBody().getId();

        UserRequestDto updatedUser = UserRequestDto.builder()
                .username("updateduser")
                .email("updated@mail.ru")
                .password("updatedpass123")
                .firstName("Updated")
                .lastName("User")
                .role(UserRole.ADMIN)
                .build();

        ResponseEntity<UserResponseDto> response = testRestTemplate.exchange(
                "/users/" + createdUserId,
                HttpMethod.PUT,
                new HttpEntity<>(updatedUser),
                UserResponseDto.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        UserResponseDto user = response.getBody();
        assertNotNull(user);
        assertEquals(createdUserId, user.getId());
        assertEquals(updatedUser.getUsername(), user.getUsername());
        assertEquals(updatedUser.getEmail(), user.getEmail());
        assertEquals(updatedUser.getFirstName(), user.getFirstName());
        assertEquals(updatedUser.getLastName(), user.getLastName());

        jdbcTemplate.query(
                "select * from users where id = :id",
                new MapSqlParameterSource("id", createdUserId),
                rs -> {
                    assertEquals(updatedUser.getUsername(), rs.getString("username"));
                    assertEquals(updatedUser.getEmail(), rs.getString("email"));
                    assertEquals(updatedUser.getFirstName(), rs.getString("first_name"));
                    assertEquals(updatedUser.getLastName(), rs.getString("last_name"));
                    assertEquals(updatedUser.getRole().toString(), rs.getString("role"));
                    assertFalse(rs.next());
                }
        );
    }

    @Test
    void updateUserNotFoundTest() {
        userRequestDto = UserRequestDto.builder()
                .username(USERNAME)
                .email(EMAIL)
                .password(PASSWORD)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .role(ROLE)
                .build();

        UUID nonExistentId = UUID.randomUUID();
        ResponseEntity<String> response = testRestTemplate.exchange(
                "/users/" + nonExistentId,
                HttpMethod.PUT,
                new HttpEntity<>(userRequestDto),
                String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void deleteUserTest() {
        userRequestDto = UserRequestDto.builder()
                .username(USERNAME)
                .email(EMAIL)
                .password(PASSWORD)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .role(ROLE)
                .build();

        ResponseEntity<UserResponseDto> createResponse = testRestTemplate.exchange(
                "/users",
                HttpMethod.POST,
                new HttpEntity<>(userRequestDto),
                UserResponseDto.class
        );


        UUID userIdToDelete = createResponse.getBody().getId();

        ResponseEntity<Void> deleteResponse = testRestTemplate.exchange(
                "/users/" + userIdToDelete,
                HttpMethod.DELETE,
                null,
                Void.class
        );

        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());

        Integer count = jdbcTemplate.queryForObject(
                "select count(*) from users where id = :id",
                new MapSqlParameterSource("id", userIdToDelete),
                Integer.class
        );
        assertEquals(0, count);
    }

    @Test
    void deleteUserNotFoundTest() {
        UUID nonExistentId = UUID.randomUUID();
        ResponseEntity<String> response = testRestTemplate.exchange(
                "/users/" + nonExistentId,
                HttpMethod.DELETE,
                null,
                String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
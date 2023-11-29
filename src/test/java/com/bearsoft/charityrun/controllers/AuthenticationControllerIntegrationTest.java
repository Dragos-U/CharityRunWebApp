package com.bearsoft.charityrun.controllers;

import com.bearsoft.charityrun.models.domain.dtos.AppUserDTO;
import com.bearsoft.charityrun.models.domain.dtos.AuthenticationRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@Transactional
public class AuthenticationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String REGISTRATION_URL = "/api/v1/authentication/registration";
    private static final String LOGIN_URL = "/api/v1/authentication/login";
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";
    private static final String EMAIL = "john.doe@gmail.com";
    private static final String PASSWORD = "1234";

    @Nested
    @DisplayName("Integration Tests for /registration endpoint")
    class RegistrationTests {

        @Test
        @DisplayName("User is registered - Success")
        void testRegisterAppUserSuccessfully() throws Exception {
            AppUserDTO appUserDTO = AppUserDTO.builder()
                    .firstName(FIRST_NAME)
                    .lastName(LAST_NAME)
                    .email(EMAIL)
                    .password(PASSWORD)
                    .address(null)
                    .build();
            String registerJsonRequest = objectMapper.writeValueAsString(appUserDTO);

            mockMvc.perform(post(REGISTRATION_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(registerJsonRequest))
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("Registering duplicate throws Conflict Exception")
        @Sql(statements =
                "INSERT INTO app_user (first_name, last_name, email, password)" +
                        " VALUES ('John', 'Doe', 'john.doe@gmail.com', '1234')",
                executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
        @Sql(statements = "DELETE FROM app_user WHERE email='john.doe@gmail.com'",
                executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
        void testRegisterDuplicateAppUser() throws Exception {
            AppUserDTO newAppUserDTO = AppUserDTO.builder()
                    .firstName(FIRST_NAME)
                    .lastName(LAST_NAME)
                    .email(EMAIL)
                    .password(PASSWORD)
                    .address(null)
                    .build();
            String newJsonRequest = objectMapper.writeValueAsString(newAppUserDTO);

            mockMvc.perform(post(REGISTRATION_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(newJsonRequest))
                    .andExpect(status().isConflict());
        }
    }

    @Nested
    @DisplayName("Integration Tests for /login endpoint")
    class LoginTests {

        @Autowired
        private JdbcTemplate jdbcTemplate;

        @Autowired
        private PasswordEncoder passwordEncoder;

        @BeforeEach
        void setUp() {
            String encodedPassword = passwordEncoder.encode(PASSWORD);
            String sql = "INSERT INTO app_user (first_name, last_name, email, password) VALUES (?, ?, ?, ?)";
            jdbcTemplate.update(sql, FIRST_NAME, LAST_NAME, EMAIL, encodedPassword);
        }

        @AfterEach
        void cleanUp() {
            String sql = "DELETE FROM app_user WHERE email = ?";
            jdbcTemplate.update(sql, EMAIL);
        }

        @Test
        @DisplayName("AppUser Login - Success")
        void testLoginAppUserSuccessfully() throws Exception {
            AuthenticationRequestDTO requestDTO = AuthenticationRequestDTO.builder()
                    .email(EMAIL)
                    .password(PASSWORD)
                    .build();
            String loginJsonRequest = objectMapper.writeValueAsString(requestDTO);
            mockMvc.perform(post(LOGIN_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(loginJsonRequest))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.access_token").isNotEmpty())
                    .andExpect(jsonPath("$.refresh_token").isNotEmpty());
        }

        @Test
        @DisplayName("AppUser not found throws Bad Request Exception")
        void testLoginAppUserNotFound() throws Exception {
            AuthenticationRequestDTO requestDTO = AuthenticationRequestDTO.builder()
                    .email("nonexistent@email")
                    .password(PASSWORD)
                    .build();
            String loginJsonRequest = objectMapper.writeValueAsString(requestDTO);

            mockMvc.perform(post(LOGIN_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(loginJsonRequest))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("AppUser invalid password throws forbidden Exception")
        void testLoginAppUserWrongPassword() throws Exception {
            AuthenticationRequestDTO requestDTO = AuthenticationRequestDTO.builder()
                    .email(EMAIL)
                    .password("wrongPassword")
                    .build();
            String loginJsonRequest = objectMapper.writeValueAsString(requestDTO);

            mockMvc.perform(post(LOGIN_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(loginJsonRequest))
                    .andExpect(status().isBadRequest());
        }
    }
}
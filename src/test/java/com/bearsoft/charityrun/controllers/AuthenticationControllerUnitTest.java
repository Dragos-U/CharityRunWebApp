package com.bearsoft.charityrun.controllers;

import com.bearsoft.charityrun.exceptions.appuser.AppUserAlreadyExistsException;
import com.bearsoft.charityrun.exceptions.appuser.AppUserNotFoundException;
import com.bearsoft.charityrun.exceptions.appuser.PasswordDoesNotMatchException;
import com.bearsoft.charityrun.models.domain.dtos.AppUserDTO;
import com.bearsoft.charityrun.models.domain.dtos.AuthenticationRequestDTO;
import com.bearsoft.charityrun.models.domain.dtos.AuthenticationResponseDTO;
import com.bearsoft.charityrun.models.domain.entities.Address;
import com.bearsoft.charityrun.services.security.AuthenticationService;
import com.bearsoft.charityrun.services.security.JwtFilterService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthenticationController.class)
@AutoConfigureMockMvc(addFilters = false)
@Disabled
class AuthenticationControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtFilterService jwtFilterService;

    @MockBean
    private AuthenticationService authenticationService;

    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";
    private static final String EMAIL = "john.doe@gmail.com";
    private static final String PASSWORD = "1234";
    private static final Address ADDRESS = null;

    @Nested
    @DisplayName("Tests for /registration endpoint")
    class RegistrationTests {

        private AppUserDTO testUser;
        private String testUserJson;

        @BeforeEach
        void setup() throws Exception {
            testUser = AppUserDTO.builder()
                    .firstName(FIRST_NAME)
                    .lastName(LAST_NAME)
                    .email(EMAIL)
                    .password(PASSWORD)
                    .address(ADDRESS)
                    .build();
            testUserJson = objectMapper.writeValueAsString(testUser);

            AuthenticationResponseDTO mockResponse = new AuthenticationResponseDTO("dummy-token", "dummy-refresh-token");
            given(authenticationService.registerAppUser(any(AppUserDTO.class))).willReturn(mockResponse);
        }

        @Test
        @DisplayName("User is registered successfully")
        void testRegisterAppUserSuccessfully() throws Exception {

            mockMvc.perform(post("/api/v1/authentication/registration")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(testUserJson))
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("AppUserAlreadyExistException thrown")
        void testRegisterAppUserUsernameExistsException() throws Exception {
            given(authenticationService.registerAppUser(any(AppUserDTO.class)))
                    .willThrow(new AppUserAlreadyExistsException("Username already exists."));

            mockMvc.perform(post("/api/v1/authentication/registration")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(testUserJson))
                    .andExpect(status().isConflict());
        }
    }

    @Nested
    @DisplayName("Tests for /login endpoint")
    class LoginTests {

        private final String accessToken = "access-token";
        private final String refreshToken = "refresh-token";

        @BeforeEach
        void setupLoginTests() {
            AuthenticationResponseDTO mockResponse = new AuthenticationResponseDTO("access-token", "refresh-token");
            given(authenticationService.loginAppUser(any(AuthenticationRequestDTO.class))).willReturn(mockResponse);
        }

        @Test
        @DisplayName("Login successfully")
        void testLoginAppUserSuccessfully() throws Exception {
            AuthenticationRequestDTO requestDTO = new AuthenticationRequestDTO(EMAIL, PASSWORD);
            AuthenticationResponseDTO responseDTO = new AuthenticationResponseDTO(accessToken, refreshToken);
            String loginJsonRequest = objectMapper.writeValueAsString(requestDTO);

            given(authenticationService.loginAppUser(any(AuthenticationRequestDTO.class))).willReturn(responseDTO);

            mockMvc.perform(post("/api/v1/authentication/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(loginJsonRequest))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.access_token").value(accessToken))
                    .andExpect(jsonPath("$.refresh_token").value(refreshToken));
        }

        @Test
        @DisplayName("AppUserNotFoundException thrown")
        void testLoginAppUserNotFound() throws Exception {
            AuthenticationRequestDTO loginRequest = new AuthenticationRequestDTO("unknown@example.com", PASSWORD);
            String loginJsonRequest = objectMapper.writeValueAsString(loginRequest);

            given(authenticationService.loginAppUser(any(AuthenticationRequestDTO.class)))
                    .willThrow(new AppUserNotFoundException("User not found."));

            mockMvc.perform(post("/api/v1/authentication/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(loginJsonRequest))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("PasswordDoesNotMatchException thrown")
        void testLoginAppUserWrongPassword() throws Exception {
            AuthenticationRequestDTO loginRequest = new AuthenticationRequestDTO(EMAIL, "wrongPassword");
            String loginJsonRequest = objectMapper.writeValueAsString(loginRequest);

            given(authenticationService.loginAppUser(any(AuthenticationRequestDTO.class)))
                    .willThrow(new PasswordDoesNotMatchException("Wrong password."));

            mockMvc.perform(post("/api/v1/authentication/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(loginJsonRequest))
                    .andExpect(status().isForbidden());
        }
    }
}

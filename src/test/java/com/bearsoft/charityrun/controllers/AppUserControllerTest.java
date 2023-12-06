package com.bearsoft.charityrun.controllers;

import com.bearsoft.charityrun.models.domain.dtos.AppUserDTO;
import com.bearsoft.charityrun.models.domain.dtos.ChangePasswordDTO;
import com.bearsoft.charityrun.models.domain.entities.AppUser;
import com.bearsoft.charityrun.models.domain.entities.Role;
import com.bearsoft.charityrun.models.domain.enums.RoleType;
import com.bearsoft.charityrun.models.security.SecurityAppUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.List;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@Transactional
class AppUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String USERS_URL = "/api/v1/users";
    private static final String USERS_ME_URL = "/api/v1/users/me";
    private static final String USERS_ALL_URL = "/api/v1/users";
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";
    private static final String EMAIL = "john.doe@gmail.com";
    private static final String PASSWORD = "1234";

    @Nested
    @DisplayName("Test /users/me endpoint")
    class TestUsersEndpoint {

        @Nested
        @DisplayName("Test Get Logged User")
        class GetLoggedAppUserDataTests {

            @AfterEach
            public void cleanUp() {
                SecurityContextHolder.clearContext();
            }

            @Test
            @DisplayName("Get Logged User Data - Success")
            void testGetLoggedAppUserData_ShouldReturnUserData() throws Exception {
                setUpSecurityContextWithAuthenticatedUser(RoleType.ROLE_USER);
                mockMvc.perform(get(USERS_ME_URL)
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.firstName").value(FIRST_NAME))
                        .andExpect(jsonPath("$.lastName").value(LAST_NAME))
                        .andExpect(jsonPath("$.email").value(EMAIL));
            }

            @Test
            @DisplayName("Get Logged User Data - User Not Connected")
            void testGetLoggedAppUserData_Failure() throws Exception {
                mockMvc.perform(get(USERS_ME_URL)
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isForbidden());
            }
        }

        @Nested
        @DisplayName("Test Update Logged User")
        class UpdateLoggedAppUserDataTests {

            @AfterEach
            public void cleanUp() {
                SecurityContextHolder.clearContext();
            }

            @Test
            @DisplayName("Update Logged User Data - Success")
            void testUpdateLoggedAppUserData_Success() throws Exception {
                setUpSecurityContextWithAuthenticatedUser(RoleType.ROLE_USER);
                AppUserDTO appUserDTO = AppUserDTO.builder()
                        .firstName("Mike")
                        .email(EMAIL)
                        .build();
                String jsonRequest = objectMapper.writeValueAsString(appUserDTO);

                mockMvc.perform(put(USERS_ME_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonRequest))
                        .andExpect(status().isAccepted());
            }

            @Test
            @DisplayName("Update Logged User Data - User Not Connected")
            void testUpdateLoggedAppUserData_Failure() throws Exception {
                AppUserDTO appUserDTO = AppUserDTO.builder()
                        .firstName("Mike")
                        .build();
                String jsonRequest = objectMapper.writeValueAsString(appUserDTO);

                mockMvc.perform(put(USERS_ME_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonRequest))
                        .andExpect(status().isForbidden());
            }
        }

        @Nested
        @DisplayName("Test Change Logged User Password")
        class ChangeLoggedAppUserPasswordTests {

            @AfterEach
            public void cleanUp() {
                SecurityContextHolder.clearContext();
            }

            @Test
            @DisplayName("Change Logged User Password - Success")
            void testChangeLoggedAppUserPassword_Success() throws Exception {
                setUpSecurityContextWithAuthenticatedUser(RoleType.ROLE_USER);
                ChangePasswordDTO changePasswordDTO = ChangePasswordDTO
                        .builder()
                        .currentPassword(PASSWORD)
                        .newPassword("abcd")
                        .confirmationPassword("abcd")
                        .build();
                String jsonRequest = objectMapper.writeValueAsString(changePasswordDTO);

                mockMvc.perform(patch(USERS_ME_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonRequest))
                        .andExpect(status().isAccepted());
            }

            @Test
            @DisplayName("Change Logged User Password - Invalid Current Password")
            void testChangeLoggedAppUserPassword_Failure1() throws Exception {
                setUpSecurityContextWithAuthenticatedUser(RoleType.ROLE_USER);
                ChangePasswordDTO changePasswordDTO = ChangePasswordDTO
                        .builder()
                        .currentPassword("invalid password")
                        .newPassword("abcd")
                        .confirmationPassword("abcd")
                        .build();
                String jsonRequest = objectMapper.writeValueAsString(changePasswordDTO);

                mockMvc.perform(patch(USERS_ME_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonRequest))
                        .andExpect(status().isBadRequest());
            }

            @Test
            @DisplayName("Change Logged User Password - Passwords Do Not Match")
            void testChangeLoggedAppUserPassword_Failure2() throws Exception {
                setUpSecurityContextWithAuthenticatedUser(RoleType.ROLE_USER);
                ChangePasswordDTO changePasswordDTO = ChangePasswordDTO
                        .builder()
                        .currentPassword(PASSWORD)
                        .newPassword("abcd")
                        .confirmationPassword("a")
                        .build();
                String jsonRequest = objectMapper.writeValueAsString(changePasswordDTO);

                mockMvc.perform(patch(USERS_ME_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonRequest))
                        .andExpect(status().isBadRequest()); //
            }
        }

        @Nested
        @DisplayName("Test Delete Logged User")
        class DeletedLoggedAppUserTests {

            @Test
            @DisplayName("Delete Logged User - Success")
            void testDeletedLoggedAppUser_Success() throws Exception {
                setUpSecurityContextWithAuthenticatedUser(RoleType.ROLE_USER);
                mockMvc.perform(delete("/api/v1/users/me/{email}", EMAIL)
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isNoContent());
            }

            @Test
            @DisplayName("Delete Logged User - User Not Found")
            void testDeletedLoggedAppUser_Failure() {
                // Negative test case
            }
        }
    }

    @Nested
    @DisplayName(("Test /users endpoint"))
    class TestAdminEndPoints {

        @Nested
        @DisplayName("Test Admin Get All Users")
        class GetAllAppUsersTests {

            @BeforeEach
            public void setUp() {
                setUpSecurityContextWithAuthenticatedUser(RoleType.ROLE_ADMIN);
            }

            @AfterEach
            public void cleanUp() {
                SecurityContextHolder.clearContext();
            }

            @Test
            @DisplayName("Get All Users - Success")
            void testGetAllAppUsers_Success() throws Exception {
                String sql = "INSERT INTO app_user (first_name, last_name, email, password) VALUES (?,?,?,?)";
                jdbcTemplate.update(sql, FIRST_NAME, LAST_NAME, EMAIL, passwordEncoder.encode(PASSWORD));

                mockMvc.perform(get(USERS_ALL_URL)
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));

                sql = "DELETE FROM app_user WHERE email =?";
                jdbcTemplate.update(sql, EMAIL);
            }

            @Test
            @DisplayName("Get All Users - No Users Found")
            void testGetAllAppUsers_Failure() throws Exception {
                mockMvc.perform(get(USERS_ALL_URL)
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$", hasSize(0)));
            }
        }

        @Nested
        @DisplayName("Test Get User")
        class GetUserByEmailTests {

            @BeforeEach
            public void setUp() {
                String sql = "INSERT INTO app_user (first_name, last_name, email, password) VALUES (?,?,?,?)";
                jdbcTemplate.update(sql, FIRST_NAME, LAST_NAME, EMAIL, passwordEncoder.encode(PASSWORD));
                setUpSecurityContextWithAuthenticatedUser(RoleType.ROLE_ADMIN);
            }

            @AfterEach
            public void cleanUp() {
                String sql = "DELETE FROM app_user WHERE email =?";
                jdbcTemplate.update(sql, EMAIL);
                SecurityContextHolder.clearContext();
            }

            @Test
            @DisplayName("Get User By Email - Success")
            void testGetUserByEmail_Success() throws Exception {
                mockMvc.perform(get(USERS_URL + "/{email}", EMAIL)
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.email").value(EMAIL));
            }

            @Test
            @DisplayName("Get User By Email - User Not Found")
            void testGetUserByEmail_Failure() throws Exception {
                mockMvc.perform(get(USERS_URL + "/{email}", "nonexistent@example.com")
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest());
            }
        }

        @Nested
        @DisplayName("Test Delete User")
        class DeleteUserByEmailTests {

            @BeforeEach
            public void setUp(){
                String sql = "INSERT INTO app_user (first_name, last_name, email, password) VALUES(?,?,?,?)";
                jdbcTemplate.update(sql,FIRST_NAME, LAST_NAME, EMAIL, passwordEncoder.encode(PASSWORD));
                setUpSecurityContextWithAuthenticatedUser(RoleType.ROLE_ADMIN);
            }

            @AfterEach
            public void cleanUp(){
                String sql = "DELETE FROM app_user WHERE email=?";
                jdbcTemplate.update(sql,EMAIL);
                SecurityContextHolder.clearContext();
            }

            @Test
            @DisplayName("Delete User By Email - Success")
            void testDeleteUserByEmail_Success() throws Exception {
                mockMvc.perform(delete(USERS_URL + "/{email}", EMAIL)
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isNoContent());
            }

            @Test
            @DisplayName("Delete User By Email - User Not Found")
            void testDeleteUserByEmail_Failure() throws Exception {
                mockMvc.perform(delete(USERS_URL + "/{email}", "nonexistent@example.com")
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest());
            }
        }
    }

    private void setUpSecurityContextWithAuthenticatedUser(RoleType roleType) {
        String encodedPassword = passwordEncoder.encode(PASSWORD);
        AppUser appUser = AppUser.builder()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .email(EMAIL)
                .password(encodedPassword)
                .roles(new HashSet<>(List.of(Role.builder().roleType(roleType).build())))
                .build();

        SecurityAppUser securityAppUser = new SecurityAppUser(appUser);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(securityAppUser, null, securityAppUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
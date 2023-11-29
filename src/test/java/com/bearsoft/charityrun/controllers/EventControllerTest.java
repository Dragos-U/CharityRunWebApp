package com.bearsoft.charityrun.controllers;

import com.bearsoft.charityrun.models.domain.dtos.EventDTO;
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

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@Transactional
public class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String EVENTS_URL = "/api/v1/events";
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";
    private static final String EMAIL = "john.doe@gmail.com";
    private static final String PASSWORD = "1234";

    @Nested
    @DisplayName("Create Event")
    class CreateEvent {

        @AfterEach
        public void cleanUp() {
            SecurityContextHolder.clearContext();
        }

        @Test
        @DisplayName("Admin can create Event")
        void testCreateEventByAdmin() throws Exception {
            setUpSecurityContextWithAuthenticatedUser(RoleType.ROLE_ADMIN);
            EventDTO eventDTO = EventDTO.builder()
                    .name("Sport Guru")
                    .date(LocalDate.of(2024, 1, 24))
                    .venue("Timisoara")
                    .build();
            String jsonRequest = objectMapper.writeValueAsString(eventDTO);

            mockMvc.perform(post(EVENTS_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonRequest))
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("User cannot create Event")
        void testCreateEventByUser() throws Exception {
            setUpSecurityContextWithAuthenticatedUser(RoleType.ROLE_USER);
            EventDTO eventDTO = EventDTO.builder()
                    .name("Sport Guru")
                    .date(LocalDate.of(2024, 1, 24))
                    .venue("Timisoara")
                    .build();
            String jsonRequest = objectMapper.writeValueAsString(eventDTO);

            mockMvc.perform(post(EVENTS_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonRequest))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Event data must be valid")
        void testCreateEventByAdminInvalidInput() throws Exception {
            setUpSecurityContextWithAuthenticatedUser(RoleType.ROLE_ADMIN);
            EventDTO eventDTO = EventDTO.builder()
                    .name(null)
                    .date(LocalDate.of(2024, 1, 24))
                    .venue("Timisoara")
                    .build();
            String jsonRequest = objectMapper.writeValueAsString(eventDTO);

            mockMvc.perform(post(EVENTS_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonRequest))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Get Event")
    class GetEvent {

        @BeforeEach
        public void setUp() {
            String sql = "INSERT INTO event (id, date, name, venue) VALUES ('1','2024-04-01', 'Sport Guru', 'Liberty Square TM')";
            jdbcTemplate.update(sql);
        }

        @AfterEach
        public void cleanUp() {
            SecurityContextHolder.clearContext();

            String sql = "DELETE FROM event WHERE id =1";
            jdbcTemplate.update(sql);
        }

        @Test
        @DisplayName("Participant can get event details")
        void testGetEventByParticipant() throws Exception {
            setUpSecurityContextWithAuthenticatedUser(RoleType.ROLE_PARTICIPANT);
            mockMvc.perform(get(EVENTS_URL + "/1")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("User cannot get event details")
        void testGetEventByUser() throws Exception {
            setUpSecurityContextWithAuthenticatedUser(RoleType.ROLE_USER);
            mockMvc.perform(get(EVENTS_URL + "/1")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("Update Event")
    class UpdateEvent {

        @BeforeEach
        public void setUp() {
            String sql = "INSERT INTO event (id, date, name, venue) VALUES ('1','2024-04-01', 'Sport Guru', 'Liberty Square TM')";
            jdbcTemplate.update(sql);
        }

        @AfterEach
        public void cleanUp() {
            SecurityContextHolder.clearContext();

            String sql = "DELETE FROM event WHERE id =1";
            jdbcTemplate.update(sql);
        }

        @Test
        @DisplayName("Admin can update event details")
        void testUpdateEventByAdmin() throws Exception {
            setUpSecurityContextWithAuthenticatedUser(RoleType.ROLE_ADMIN);
            EventDTO eventDTO = EventDTO.builder()
                    .name("Liberty")
                    .date(LocalDate.of(2024, 1, 24))
                    .venue("Timisoara")
                    .build();
            String jsonRequest = objectMapper.writeValueAsString(eventDTO);

            mockMvc.perform(put(EVENTS_URL + "/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonRequest))
                    .andExpect(status().isAccepted());
        }

        @Test
        @DisplayName("User cannot update event details")
        void testUpdateEventByUser() throws Exception {
            setUpSecurityContextWithAuthenticatedUser(RoleType.ROLE_USER);
            EventDTO eventDTO = EventDTO.builder()
                    .name("Liberty")
                    .date(LocalDate.of(2024, 1, 24))
                    .venue("Timisoara")
                    .build();
            String jsonRequest = objectMapper.writeValueAsString(eventDTO);

            mockMvc.perform(put(EVENTS_URL + "/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonRequest))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("Delete Event")
    class DeleteEvent {
        @BeforeEach
        void setUp() {
            String sql = "INSERT INTO event (id, date, name, venue) VALUES ('1','2024-04-01', 'Sport Guru', 'Liberty Square TM')";
            jdbcTemplate.update(sql);
        }

        @AfterEach
        void cleanUp() {
            SecurityContextHolder.clearContext();
        }

        @Test
        @DisplayName("Admin can delete event")
        void testUpdateEventByAdmin() throws Exception {
            setUpSecurityContextWithAuthenticatedUser(RoleType.ROLE_ADMIN);
            mockMvc.perform(delete(EVENTS_URL)
                            .param("eventID","1")
                            .param("deleteApproval","true")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("User cannot delete event")
        void testUpdateEventByUser() throws Exception {
            setUpSecurityContextWithAuthenticatedUser(RoleType.ROLE_USER);
            mockMvc.perform(delete(EVENTS_URL)
                            .param("eventID","1")
                            .param("deleteApproval","true")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden());
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

package com.bearsoft.charityrun.controllers;


import com.bearsoft.charityrun.models.domain.dtos.CourseRegistrationDTO;
import com.bearsoft.charityrun.models.domain.entities.AppUser;
import com.bearsoft.charityrun.models.domain.entities.Role;
import com.bearsoft.charityrun.models.domain.enums.CourseType;
import com.bearsoft.charityrun.models.domain.enums.GenderType;
import com.bearsoft.charityrun.models.domain.enums.RoleType;
import com.bearsoft.charityrun.models.domain.enums.TShirtSize;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@Transactional
public class CourseRegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String COURSE_REGISTRATION_URL = "/api/v1/course-registrations";
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";
    private static final String EMAIL = "john.doe@gmail.com";
    private static final String PASSWORD = "1234";

    @Nested
    @DisplayName("Register Logged User to course")
    class testRegisterLoggedAppUserToCourse {

        @BeforeEach
        public void setUp() {
            String sql = "INSERT INTO event (id, date, name, venue) VALUES ('1','2024-04-01', 'Sport Guru', 'Liberty Square TM')";
            jdbcTemplate.update(sql);
            sql = "INSERT INTO course (event_id, start_time, course_type) VALUES ('1', '09:00:00', 'CROSS')";
            jdbcTemplate.update(sql);
        }

        @AfterEach
        public void cleanUp() {
            SecurityContextHolder.clearContext();

            String deleteUserSql = "DELETE FROM app_user WHERE course_registration_id IN (SELECT id FROM course_registration WHERE course_id IN (SELECT id FROM course WHERE event_id = 1))";
            jdbcTemplate.update(deleteUserSql);
            String deleteCourseRegistrationsSql = "DELETE FROM course_registration WHERE course_id IN (SELECT id FROM course WHERE event_id = 1)";
            jdbcTemplate.update(deleteCourseRegistrationsSql);
            String deleteCoursesSql = "DELETE FROM course WHERE event_id = 1";
            jdbcTemplate.update(deleteCoursesSql);
            String sql = "DELETE FROM event WHERE id =1";
            jdbcTemplate.update(sql);
        }

        @Test
        @DisplayName("User registered as participant")
        void testUserSuccessfulRegistration() throws Exception {
            setUpSecurityContextWithAuthenticatedUser(RoleType.ROLE_USER);
            CourseRegistrationDTO courseRegistrationDTO = CourseRegistrationDTO.builder()
                    .age(20)
                    .gender(GenderType.M)
                    .courseType(CourseType.CROSS)
                    .tShirtSize(TShirtSize.XL)
                    .build();
            String jsonRequest = objectMapper.writeValueAsString(courseRegistrationDTO);

            mockMvc.perform(post(COURSE_REGISTRATION_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonRequest))
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("Wrong User registration data")
        void testUserFailedRegistration() throws Exception {
            setUpSecurityContextWithAuthenticatedUser(RoleType.ROLE_USER);
            CourseRegistrationDTO courseRegistrationDTO = CourseRegistrationDTO.builder()
                    .age(2)
                    .gender(GenderType.M)
                    .courseType(CourseType.CROSS)
                    .tShirtSize(TShirtSize.XL)
                    .build();
            String jsonRequest = objectMapper.writeValueAsString(courseRegistrationDTO);

            mockMvc.perform(post(COURSE_REGISTRATION_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonRequest))
                    .andExpect(status().isBadRequest());
        }
    }

    private void setUpSecurityContextWithAuthenticatedUser(RoleType roleType) {
        String encodedPassword = passwordEncoder.encode(PASSWORD);
        Role participantRole = Role.builder()
                .roleType(roleType)
                .build();

        AppUser appUser = AppUser.builder()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .email(EMAIL)
                .password(encodedPassword)
                .roles(new HashSet<>(List.of(participantRole)))
                .build();

        SecurityAppUser securityAppUser = new SecurityAppUser(appUser);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(securityAppUser, null, securityAppUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
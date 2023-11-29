package com.bearsoft.charityrun.controllers;

import com.bearsoft.charityrun.models.domain.dtos.CourseDTO;
import com.bearsoft.charityrun.models.domain.entities.AppUser;
import com.bearsoft.charityrun.models.domain.entities.Role;
import com.bearsoft.charityrun.models.domain.enums.CourseType;
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

import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@Transactional
public class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String COURSES_URL = "/api/v1/courses";
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";
    private static final String EMAIL = "john.doe@gmail.com";
    private static final String PASSWORD = "1234";

    @Nested
    @DisplayName("Create Course")
    class CreateCourseTest {
        @BeforeEach
        public void setUp() {
            String sql = "INSERT INTO event (id, date, name, venue) VALUES ('1','2024-04-01', 'Sport Guru', 'Liberty Square TM')";
            jdbcTemplate.update(sql);
        }

        @AfterEach
        public void cleanUp() {
            SecurityContextHolder.clearContext();

            String deleteCoursesSql = "DELETE FROM course WHERE event_id = 1";
            jdbcTemplate.update(deleteCoursesSql);
            String sql = "DELETE FROM event WHERE id =1";
            jdbcTemplate.update(sql);
        }


        @Test
        @DisplayName("Admin can create courses")
        void testAdminCreateCourseSuccess() throws Exception {
            setUpSecurityContextWithAuthenticatedUser(RoleType.ROLE_ADMIN);

            CourseDTO courseDTO = CourseDTO.builder()
                    .courseType(CourseType.MARATHON)
                    .startTime(LocalTime.parse("09:00:00"))
                    .build();

            String jsonRequest = objectMapper.writeValueAsString(courseDTO);

            mockMvc.perform(post(COURSES_URL)
                            .param("eventID", "1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonRequest))
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("Users cannot create courses")
        void testUserCannotCreateCourse() throws Exception {
            setUpSecurityContextWithAuthenticatedUser(RoleType.ROLE_USER);

            CourseDTO courseDTO = CourseDTO.builder()
                    .courseType(CourseType.MARATHON)
                    .startTime(LocalTime.parse("09:00:00"))
                    .build();

            String jsonRequest = objectMapper.writeValueAsString(courseDTO);

            mockMvc.perform(post(COURSES_URL)
                            .param("eventID", "1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonRequest))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Course cannot be created by User")
        void testCreateCourseFailureRole() throws Exception {
            setUpSecurityContextWithAuthenticatedUser(RoleType.ROLE_USER);

            CourseDTO courseDTO = CourseDTO.builder()
                    .courseType(CourseType.MARATHON)
                    .startTime(LocalTime.parse("09:00:00"))
                    .eventID(1L)
                    .build();

            String jsonRequest = objectMapper.writeValueAsString(courseDTO);

            mockMvc.perform(post(COURSES_URL)
                            .param("eventID", "1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonRequest))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("CourseDTO must be valid")
        void testCreateCourseFailureDTO() throws Exception {
            setUpSecurityContextWithAuthenticatedUser(RoleType.ROLE_ADMIN);

            CourseDTO courseDTO = CourseDTO.builder()
                    .courseType(null)
                    .startTime(LocalTime.parse("09:00:00"))
                    .eventID(1L)
                    .build();

            String jsonRequest = objectMapper.writeValueAsString(courseDTO);

            mockMvc.perform(post(COURSES_URL)
                            .param("eventID", "1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonRequest))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Update Course")
    class UpdateCourseTimeStartTest {

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

            String deleteCoursesSql = "DELETE FROM course WHERE event_id = 1";
            jdbcTemplate.update(deleteCoursesSql);
            String deleteEventSql = "DELETE FROM event WHERE id =1";
            jdbcTemplate.update(deleteEventSql);
        }

        @Test
        @DisplayName("Course update - Success")
        void testUpdateCourseSuccess() throws Exception {
            setUpSecurityContextWithAuthenticatedUser(RoleType.ROLE_ADMIN);

            CourseDTO courseDTO = CourseDTO.builder()
                    .courseType(CourseType.CROSS)
                    .startTime(LocalTime.parse("09:15:00"))
                    .build();
            String jsonRequest = objectMapper.writeValueAsString(courseDTO);

            mockMvc.perform(patch(COURSES_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonRequest))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Course update - failure due to event")
        void testUpdateCourseFailureEvent() throws Exception {
            setUpSecurityContextWithAuthenticatedUser(RoleType.ROLE_ADMIN);

            CourseDTO courseDTO = CourseDTO.builder()
                    .courseType(CourseType.CROSS)
                    .startTime(LocalTime.parse("09:15:00"))
                    .build();
            String jsonRequest = objectMapper.writeValueAsString(courseDTO);

            mockMvc.perform(patch(COURSES_URL)
                            .param("eventID", "2")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonRequest))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Delete Course")
    class DeleteCourseByTypeTest {

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

            String deleteCoursesSql = "DELETE FROM course WHERE event_id = 1";
            jdbcTemplate.update(deleteCoursesSql);

            String deleteEventSql = "DELETE FROM event WHERE id =1";
            jdbcTemplate.update(deleteEventSql);
        }

        @Test
        @DisplayName("Course deleted - Success")
        void testDeleteCourseByType_Success() throws Exception {
            setUpSecurityContextWithAuthenticatedUser(RoleType.ROLE_ADMIN);

            mockMvc.perform(delete(COURSES_URL)
                            .param("eventID", "1")
                            .param("deleteApproval", "true")
                            .param("courseType", String.valueOf(CourseType.CROSS))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("Course type not found for given event")
        void testDeleteCourseByTypeFailureWrongType() throws Exception {
            setUpSecurityContextWithAuthenticatedUser(RoleType.ROLE_ADMIN);

            mockMvc.perform(delete(COURSES_URL)
                            .param("eventID", "1")
                            .param("deleteApproval", "true")
                            .param("courseType", "X")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("View courses")
    class GetAllCourses{

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

            String deleteCoursesSql = "DELETE FROM course WHERE event_id = 1";
            jdbcTemplate.update(deleteCoursesSql);

            String deleteEventSql = "DELETE FROM event WHERE id =1";
            jdbcTemplate.update(deleteEventSql);
        }

        @Test
        @DisplayName("Courses can be retrieved")
        public void testGetAllCourses() throws Exception {
            setUpSecurityContextWithAuthenticatedUser(RoleType.ROLE_PARTICIPANT);
            mockMvc.perform(get(COURSES_URL)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].courseType").value("CROSS"))
                    .andExpect(jsonPath("$[0].startTime").value("09:00"));
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

package com.bearsoft.charityrun.services;

import com.bearsoft.charityrun.models.domain.entities.AppUser;
import com.bearsoft.charityrun.repositories.AppUserRepository;
import com.bearsoft.charityrun.repositories.EventRepository;
import com.bearsoft.charityrun.services.impl.SchedulingServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class SchedulingServiceImplTest {

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private SchedulingServiceImpl schedulingService;

    @Test
    @DisplayName("Course reminder email sent")
    public void testSendCourseReminderEmail(){
//        List<AppUser> mockUsers = null;
//        when(appUserRepository.findAllRegisteredUsers().iterator(mockUsers));
//
//        // Act
//        schedulingService.sendCourseReminderEmail();
//
//        // Assert
//        verify(appUserRepository, times(1)).findAllRegisteredUsers();

    }
}

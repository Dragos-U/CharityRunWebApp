package com.bearsoft.charityrun.services.notifications;

import com.bearsoft.charityrun.models.domain.entities.AppUser;
import com.bearsoft.charityrun.repositories.AppUserRepository;
import com.bearsoft.charityrun.services.notifications.interfaces.EmailService;
import com.bearsoft.charityrun.services.notifications.interfaces.SchedulingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class SchedulingServiceImpl implements SchedulingService {

    private final AppUserRepository appUserRepository;
    private final EmailService emailService;

    @Override
    @Scheduled(cron = "0 27 12 * * ?")
    public void scheduleStartingDateReminder() {
        List<AppUser> registeredUsers = appUserRepository.findAllRegisteredUsers();
        LocalDate today = LocalDate.now();
        String subject = "Course reminder";

        for (AppUser appUser : registeredUsers) {
            LocalDate appUserCourseDate = appUser.getCourseRegistration().getCourse().getEvent().getDate();
            long daysUntilEvent = ChronoUnit.DAYS.between(today, appUserCourseDate);

            emailService.sendCourseReminderEmail(appUser, subject, daysUntilEvent);
//            if (daysUntilEvent == 30 || daysUntilEvent == 7) {
//                emailService.sendCourseReminderEmail(appUser, subject, daysUntilEvent);
//            }
        }
    }
}

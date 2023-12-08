package com.bearsoft.charityrun.services.notifications;

import com.bearsoft.charityrun.exceptions.email.EmailSendingException;
import com.bearsoft.charityrun.models.domain.dtos.AppUserDTO;
import com.bearsoft.charityrun.models.domain.dtos.CourseRegistrationDTO;
import com.bearsoft.charityrun.models.domain.entities.AppUser;
import com.bearsoft.charityrun.services.notifications.interfaces.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@RequiredArgsConstructor
@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private static final String USER_DETAILS_PAGE = "http://localhost:8080/api/v1/users/me";

    @Value("${spring.mail.username}")
    private String fromMail;

    @Override
    public void sendAppUserRegistrationEmail(AppUserDTO appUserDTO, String subject) {
        try {
            String toEmail = appUserDTO.getEmail();
            String appUserDTOFirstName = appUserDTO.getFirstName();
            String htmlTemplateName = "userRegistrationEmailTemplate";

            Context context = new Context();
            context.setVariable("firstName", appUserDTOFirstName);
            context.setVariable("userDetailsPage", USER_DETAILS_PAGE);

            sendEmail(toEmail, subject, htmlTemplateName, context);

        } catch (MessagingException e) {
            log.info(String.format("Cannot send email."));
            throw new EmailSendingException("Error during email sending.");
        }
    }

    @Override
    public void sendCourseRegistrationEmail(CourseRegistrationDTO courseRegistrationDTO, AppUser appUser, String subject) {
        try {
            String toEmail = appUser.getEmail();
            String eventName = appUser.getCourseRegistration().getCourse().getEvent().getName();
            String appUserDTOFirstName = appUser.getFirstName();
            String courseType = courseRegistrationDTO.getCourseType().toString();
            String tShirtSize = courseRegistrationDTO.getTShirtSize().toString();
            String bibNumber = String.valueOf(appUser.getCourseRegistration().getBib());
            String htmlTemplateName ="courseRegistrationEmailTemplate";

            Context context = new Context();
            context.setVariable("firstName", appUserDTOFirstName);
            context.setVariable("courseType", courseType);
            context.setVariable("tShirtSize", tShirtSize);
            context.setVariable("bibNumber", bibNumber);
            context.setVariable("eventName", eventName);

            sendEmail(toEmail, subject, htmlTemplateName, context);
        } catch (MessagingException e) {
            log.info(String.format("Cannot send email."));
            throw new EmailSendingException("Error during email sending.");
        }
    }

    @Override
    public void sendCourseReminderEmail(AppUser appUser, String subject, long daysUntilCourse) {
        try {
            String toEmail = appUser.getEmail();
            String appUserDTOFirstName = appUser.getFirstName();
            String eventName = appUser.getCourseRegistration().getCourse().getEvent().getName();
            String htmlTemplateName ="courseReminderTemplate";

            Context context = new Context();
            context.setVariable("firstName", appUserDTOFirstName);
            context.setVariable("daysUntilCourse", daysUntilCourse);
            context.setVariable("eventName", eventName);

            sendEmail(toEmail, subject, htmlTemplateName, context);
        } catch (MessagingException e) {
            log.info(String.format("Cannot send email."));
            throw new EmailSendingException("Error during email sending.");
        }
    }

    @Override
    public void sendTrainingPlanEmail(AppUser appUser, String subject, String openAiTrainingPlan) {
        try {
            String toEmail = appUser.getEmail();
            String appUserDTOFirstName = appUser.getFirstName();
            String eventName = appUser.getCourseRegistration().getCourse().getEvent().getName();
            String htmlTemplateName ="trainingPlanEmailTemplate";

            Context context = new Context();
            context.setVariable("firstName", appUserDTOFirstName);
            context.setVariable("openAiTrainingPlan", openAiTrainingPlan);
            context.setVariable("eventName", eventName);

            sendEmail(toEmail, subject, htmlTemplateName, context);
        } catch (MessagingException e) {
            log.info(String.format("Cannot send email."));
            throw new EmailSendingException("Error during email sending.");
        }
    }

    private void sendEmail(
            String toEmail,
            String subject,
            String templateName,
            Context context) throws MessagingException{
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

        String html = templateEngine.process(templateName, context);

        helper.setFrom(fromMail);
        helper.setTo(toEmail);
        helper.setSubject(subject);
        helper.setText(html, true);

        mailSender.send(mimeMessage);
    }
}

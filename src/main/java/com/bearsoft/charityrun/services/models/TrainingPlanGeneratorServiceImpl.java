package com.bearsoft.charityrun.services.models;

import com.bearsoft.charityrun.exceptions.appuser.InvalidUserAuthenticationException;
import com.bearsoft.charityrun.exceptions.email.EmailSendingException;
import com.bearsoft.charityrun.models.domain.dtos.OpenAiRequestDTO;
import com.bearsoft.charityrun.models.domain.entities.AppUser;
import com.bearsoft.charityrun.models.domain.entities.TrainingPlan;
import com.bearsoft.charityrun.models.security.SecurityAppUser;
import com.bearsoft.charityrun.repositories.CourseRegistrationRepository;
import com.bearsoft.charityrun.repositories.TrainingPlanRepository;
import com.bearsoft.charityrun.services.models.interfaces.TrainingPlanGeneratorService;
import com.bearsoft.charityrun.services.notifications.interfaces.EmailService;
import com.bearsoft.charityrun.services.openai.OpenAIClient;
import com.bearsoft.charityrun.validators.ObjectsValidator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@RequiredArgsConstructor
@Slf4j
@Service
public class TrainingPlanGeneratorServiceImpl implements TrainingPlanGeneratorService {

    private final OpenAIClient openAIClient;
    private final ObjectMapper objectMapper;
    private final EmailService emailService;
    private final TrainingPlanRepository trainingPlanRepository;
    private final ObjectsValidator<OpenAiRequestDTO> openAiRequestDTOObjectsValidator;

    public static final String ERROR_DURING_EMAIL_SENDING = "Error during email sending.";

    @Override
    public String generateTrainingPlan(OpenAiRequestDTO openAiRequestDTO, Principal connectedAppUser) {
        checkConnectedUserAuthentication(connectedAppUser);
        openAiRequestDTOObjectsValidator.validate(openAiRequestDTO);

        SecurityAppUser securityAppUser = extractUserFromPrincipal(connectedAppUser);
        var appUser = securityAppUser.getAppUser();

        var experienceType = openAiRequestDTO.getRunningExperienceType().toString();
        var courseType = appUser.getCourseRegistration().getCourse().getCourseType().toString();
        var runsPerWeek = openAiRequestDTO.getRunsPerWeek();
        var eventDate = appUser.getCourseRegistration().getCourse().getEvent().getDate();
        var weeksUntilEvent = ChronoUnit.WEEKS.between(LocalDate.now(), eventDate);

        String promptForOpenAi = generatePromptForOpenAi(experienceType, courseType, runsPerWeek, weeksUntilEvent);
        String openAiJsonResponse = openAIClient.getTrainingPlanFromOpenAI(promptForOpenAi);
        String openAiTrainingPlan = extractContentFromResponse(openAiJsonResponse);

        saveTrainingPlanToDB(appUser, openAiTrainingPlan);
        sendEmail(appUser, openAiTrainingPlan);

        return openAiTrainingPlan;
    }

    private void checkConnectedUserAuthentication(Principal connectedAppUser) {
        if (!(connectedAppUser instanceof UsernamePasswordAuthenticationToken)) {
            throw new InvalidUserAuthenticationException("Invalid user authentication");
        }
    }

    private SecurityAppUser extractUserFromPrincipal(Principal connectedAppUser) {
        checkConnectedUserAuthentication(connectedAppUser);
        return (SecurityAppUser) ((UsernamePasswordAuthenticationToken) connectedAppUser).getPrincipal();
    }

    private String generatePromptForOpenAi(String experienceType, String courseType, int runsPerWeek, Long weeksUntilEvent) {
        return "I am a " + experienceType + " runner preparing for a " + courseType + " event. " +
                "I plan to run " + runsPerWeek + " times per week and I have " + weeksUntilEvent +
                " weeks until the event. Please create a concise training plan that includes: " +
                "Input parameters:, "+
                "1. A weekly running schedule specifying which days to do long runs, speed workouts, and easy runs, " +
                "2. Suggested distances for each type of run based on my experience level and time until the event, " +
                "3. Recommendations for injury prevention and recovery strategies, " +
                "4. Nutrition advice tailored to support my training, " +
                "5. Tapering strategy for the final weeks leading up to the event, " +
                "6. Any additional tips for improving endurance and performance specific to the course type." +
                "All suggested running distances should be in kilometers." +
                "Remove the word Certainly and sure from the very beginning of your response.";
    }

    private String extractContentFromResponse(String jsonResponse) {
        try {
            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            JsonNode contentNode = rootNode.path("choices").get(0).path("message").path("content");
            return contentNode.asText();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String convertTextToHtml(String textContent){
        String htmlContent = textContent
                .replace("\n\n", "</p><p>")
                .replace("\n", "<br>");

        htmlContent = "<p>" + htmlContent + "</p>";
        return htmlContent;
    }

    private void saveTrainingPlanToDB(AppUser appUser, String openAiTrainingPlan) {
        var trainingPlan = TrainingPlan.builder()
                .trainingDetails(openAiTrainingPlan)
                .courseRegistration(appUser.getCourseRegistration())
                .build();
        trainingPlanRepository.saveAndFlush(trainingPlan);
    }

    private void sendEmail(AppUser appUser, String openAiTrainingPlan){
        try {
            String htmlContent = convertTextToHtml(openAiTrainingPlan);
            String subject = "Your training plan is ready.";
            emailService.sendTrainingPlanEmail(appUser, subject, htmlContent);
        } catch (EmailSendingException e) {
            log.error(ERROR_DURING_EMAIL_SENDING);
            throw new EmailSendingException(ERROR_DURING_EMAIL_SENDING);
        }
    }
}

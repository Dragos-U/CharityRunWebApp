package com.bearsoft.charityrun.services;

import com.bearsoft.charityrun.models.domain.dtos.OpenAiRequestDTO;
import com.bearsoft.charityrun.models.domain.enums.ExperienceType;
import com.bearsoft.charityrun.services.openai.OpenAIClient;
import com.bearsoft.charityrun.services.models.TrainingPlanGeneratorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.security.Principal;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

@AutoConfigureMockMvc(addFilters = false)
class TrainingPlanGeneratorServiceTest {

    @Mock
    private OpenAIClient openAIClient;

    @Mock
    private Principal connectedAppUser;

    @InjectMocks
    private TrainingPlanGeneratorServiceImpl trainingPlanGeneratorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGenerateTrainingPlanForBeginner() {
        // Arrange
        String expectedPlan = "Training Plan Details Here";
        OpenAiRequestDTO openAiRequestDTO = OpenAiRequestDTO.builder()
                .runsPerWeek(3)
                .runningExperienceType(ExperienceType.BEGINNER)
                .build();
        String predefinedPrompt = "Generate a training plan for a beginner user training for a marathon, 3 times a week";

        // Act
        when(openAIClient.getTrainingPlanFromOpenAI(predefinedPrompt)).thenReturn(expectedPlan);
        String actualPlan = trainingPlanGeneratorService.generateTrainingPlan(openAiRequestDTO, connectedAppUser);

        // Assert
        assertEquals(expectedPlan, actualPlan);
    }
}
package com.bearsoft.charityrun.services.openai;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@RequiredArgsConstructor
@Service
@Slf4j
public class OpenAIClient {

    private final RestTemplate restTemplate;

    @Value("${openai.api.url}")
    private String apiURL;

    @Value("${openai.model}")
    private String model;

    public String getTrainingPlanFromOpenAI(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestBody = String.format("{\"model\": \"%s\", \"messages\": [{\"role\": \"system\", \"content\": \"You are a training assistant.\"}, {\"role\": \"user\", \"content\": \"%s\"}]}", model, prompt);

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        try {
            log.info("Sending request to OpenAI...");
            ResponseEntity<String> response = restTemplate.postForEntity(apiURL, entity, String.class);
            log.info("Received response from OpenAI.");
            return response.getBody();
        } catch (HttpClientErrorException.TooManyRequests ex) {
            log.error("Error during OpenAI API request: ", ex);
            return handleRateLimit(ex, entity);
        } catch (Exception ex) {
            return "Error: " + ex.getMessage();
        }
    }

    private String handleRateLimit(HttpClientErrorException.TooManyRequests ex, HttpEntity<String> entity) {
        int maxRetries = 5;
        int retryCount = 0;
        long waitTime = 1000;

        while (retryCount < maxRetries) {
            try {
                Thread.sleep(waitTime);
                ResponseEntity<String> response = restTemplate.postForEntity(apiURL, entity, String.class);
                return response.getBody();
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                return "Error: Interrupted while waiting to retry";
            } catch (HttpClientErrorException.TooManyRequests e) {
                retryCount++;
                waitTime *= 2;
            } catch (Exception e) {
                return "Error: " + e.getMessage();
            }
        }
        return "Error: Exceeded maximum retries due to rate limits";
    }
}
package com.skillpulse.score_service.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.skillpulse.score_service.exceptions.ResourceNotFoundException;
import com.skillpulse.score_service.exceptions.ServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;

import java.time.Duration;
import java.util.Map;

@Component
@Slf4j
public class UserServiceClient {

    private final WebClient webClient;

    public UserServiceClient(@Value("${user-service.base-url}") String baseUrl) {
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(3));

        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    @CircuitBreaker(name = "userServiceCB", fallbackMethod = "getUserByIdFallback")
    @Retry(name = "userServiceRetry")
    public UserClientDTO getUserDetails(Long id) {
        return webClient.get()
                .uri("/{id}", id)
                .retrieve()
                .onStatus(status -> status.value() == 404,
                        response -> Mono.error(new ResourceNotFoundException("User not found! with id: " + id)))
                .onStatus(
                        HttpStatusCode::is5xxServerError,
                        clientResponse -> Mono.error(new ServiceUnavailableException("Service Unavailable!"))
                )
                .bodyToMono(UserClientDTO.class)
                .block();
    }

    public UserClientDTO getUserByIdFallback(Long userId, Exception ex) {
        if (ex instanceof ResourceNotFoundException) throw (ResourceNotFoundException) ex;
        throw new ServiceUnavailableException("User Service unavailable. Please try again.");
    }

    public void updateProfileStats(Long userId, Double newScore, Boolean passed) {
        try {
            webClient.patch()
                    .uri("/{userId}/profile/stats", userId)
                    .bodyValue(Map.of(
                            "newScore", newScore,
                            "passed", passed
                    ))
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, response -> {
                        log.warn("User {} not found when updating profile stats", userId);
                        return Mono.empty();
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, response -> {
                        log.error("User Service error when updating stats for user {}", userId);
                        return Mono.empty();
                    })
                    .bodyToMono(Void.class)
                    .block();

            log.info("Successfully updated profile stats for user {}", userId);
        } catch (Exception ex) {
            // Don't fail the score processing if user service is unavailable
            // Score is already saved — stats update is a best-effort call
            log.error("Failed to update profile stats for user {}: {}. " +
                    "Score already saved — this is non-critical.", userId, ex.getMessage());
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UserClientDTO {
        private Long id;
        private String fullName;
        private String email;
        private String role;
        private String status;
    }
}
package com.skillpulse.score_service.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
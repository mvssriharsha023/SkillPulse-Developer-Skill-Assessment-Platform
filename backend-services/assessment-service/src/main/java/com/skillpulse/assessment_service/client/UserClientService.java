package com.skillpulse.assessment_service.client;

import com.skillpulse.assessment_service.exceptions.ResourceNotFoundException;
import com.skillpulse.assessment_service.exceptions.ServiceUnavailableException;
import com.skillpulse.assessment_service.model.UserClientDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Service
public class UserClientService {

    private final WebClient webClient;

    public UserClientService(@Value("${user-service.base-url}") String baseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create().responseTimeout(Duration.ofSeconds(3))
                ))
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
}

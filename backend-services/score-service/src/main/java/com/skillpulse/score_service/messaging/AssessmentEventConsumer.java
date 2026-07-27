package com.skillpulse.score_service.messaging;

import com.skillpulse.score_service.config.KafkaConfig;
import com.skillpulse.score_service.model.AssessmentSubmittedEvent;
import com.skillpulse.score_service.service.ScoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AssessmentEventConsumer {

    private final ScoreService scoreService;

    public AssessmentEventConsumer(ScoreService scoreService) {
        this.scoreService = scoreService;
    }

    @KafkaListener(
            topics = KafkaConfig.ASSESSMENT_EVENTS_TOPIC,
            groupId = "score-service-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeAssessmentSubmittedEvent(
            AssessmentSubmittedEvent event,
            Acknowledgment acknowledgment,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        log.info("Received AssessmentSubmittedEvent: " +
                        "attemptId={}, userId={}, score={}%, partition={}, offset={}",
                event.getAttemptId(),
                event.getUserId(),
                event.getScorePercentage(),
                partition,
                offset);

        try {
            // Idempotency check — have we already scored this attempt?
            if (scoreService.isAlreadyScored(event.getAttemptId())) {
                log.warn("Duplicate event detected for attemptId={}. " +
                        "Already scored. Acknowledging and skipping.", event.getAttemptId());
                acknowledgment.acknowledge();
                return;
            }

            // Process the score
            scoreService.processScore(event);

            // Only acknowledge AFTER successful processing
            acknowledgment.acknowledge();
            log.info("Successfully processed and acknowledged: attemptId={}", event.getAttemptId());

        } catch (Exception ex) {
            log.error("Failed to process AssessmentSubmittedEvent for attemptId={}. " +
                            "NOT acknowledging — Kafka will redeliver. Error: {}",
                    event.getAttemptId(),
                    ex.getMessage(),
                    ex);
            // Intentionally NOT calling acknowledgment.acknowledge()
            // Kafka will redeliver this message to allow retry
            throw new RuntimeException("Score processing failed for attemptId: "
                    + event.getAttemptId(), ex);
        }
    }
}
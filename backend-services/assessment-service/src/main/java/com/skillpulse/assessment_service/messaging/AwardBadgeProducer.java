package com.skillpulse.assessment_service.messaging;

import com.skillpulse.assessment_service.model.AwardBadgeEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AwardBadgeProducer {

    private final KafkaTemplate<String, AwardBadgeEvent> kafkaTemplate;

    public AwardBadgeProducer(KafkaTemplate<String, AwardBadgeEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(AwardBadgeEvent awardBadgeEvent) {
        log.info("Sending kafka message from producer end with message {}", awardBadgeEvent);
        kafkaTemplate.send(
                "award-badge-events",
                awardBadgeEvent.getUserId().toString(),
                awardBadgeEvent
        );
    }
}

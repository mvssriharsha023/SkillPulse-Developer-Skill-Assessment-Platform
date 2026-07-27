package com.skillpulse.user_service.messaging;

import com.skillpulse.user_service.model.UserRegistrationEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {

    private final KafkaTemplate<Long, UserRegistrationEvent> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<Long, UserRegistrationEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(UserRegistrationEvent userRegistrationEvent) {
        kafkaTemplate.send(
                "user-events",
                userRegistrationEvent.getUserId(),
                userRegistrationEvent
        );
    }
}

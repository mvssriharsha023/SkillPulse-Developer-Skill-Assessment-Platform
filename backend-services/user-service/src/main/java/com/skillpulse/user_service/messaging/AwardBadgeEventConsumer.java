package com.skillpulse.user_service.messaging;

import com.skillpulse.user_service.config.KafkaConfig;
import com.skillpulse.user_service.model.AwardBadgeEvent;
import com.skillpulse.user_service.model.BadgeResponseDTO;
import com.skillpulse.user_service.service.BadgeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class AwardBadgeEventConsumer {

    private final BadgeService badgeService;

    public AwardBadgeEventConsumer(BadgeService badgeService) {
        this.badgeService = badgeService;
    }

    @KafkaListener(
            topics = KafkaConfig.AWARD_BADGE_EVENT_TOPIC,
            groupId = "badge-service-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(
            AwardBadgeEvent event,
            Acknowledgment acknowledgment,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset
    ) {
        log.info("Received AwardBadgeEvent with partition {} and offset {}", partition, offset);
        log.info("Message Received for user ID = {}", event.getUserId());

        badgeService.evaluateAndAwardBadge(event.getUserId(), event.getAttempts());

        acknowledgment.acknowledge();
    }
}

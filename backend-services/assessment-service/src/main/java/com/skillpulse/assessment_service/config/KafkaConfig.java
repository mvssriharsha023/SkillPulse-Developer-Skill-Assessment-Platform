package com.skillpulse.assessment_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class KafkaConfig {

    public static final String ASSESSMENT_EVENT_TOPIC = "assessment-events";
    public static final String AWARD_BADGE_EVENT_TOPIC = "award-badge-events";
    public static final int PARTITIONS = 3;

    @Autowired
    private ProducerFactory<?, ?> producerFactory;

    @Bean
    public NewTopic assessmentEventsTopic() {
        return TopicBuilder.name(ASSESSMENT_EVENT_TOPIC)
                .partitions(PARTITIONS)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic awardBadgeEventsTopic() {
        return TopicBuilder.name(AWARD_BADGE_EVENT_TOPIC)
                .partitions(PARTITIONS)
                .replicas(1)
                .build();
    }
}
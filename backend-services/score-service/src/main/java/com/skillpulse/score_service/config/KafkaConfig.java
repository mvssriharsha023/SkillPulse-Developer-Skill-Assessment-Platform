package com.skillpulse.score_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

@Configuration
@EnableKafka
public class KafkaConfig {

    // Topic names — must match Assessment Service exactly
    public static final String ASSESSMENT_EVENTS_TOPIC = "assessment-events";
}
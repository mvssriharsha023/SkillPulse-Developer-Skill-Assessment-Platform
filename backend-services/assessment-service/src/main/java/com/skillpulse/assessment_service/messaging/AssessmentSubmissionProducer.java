package com.skillpulse.assessment_service.messaging;

import com.skillpulse.assessment_service.model.AssessmentSubmittedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class AssessmentSubmissionProducer {

    private final KafkaTemplate<String, AssessmentSubmittedEvent> kafkaTemplate;

    public AssessmentSubmissionProducer(KafkaTemplate<String, AssessmentSubmittedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(AssessmentSubmittedEvent assessmentSubmittedEvent) {

        kafkaTemplate.send(
                "assessment-events",
                assessmentSubmittedEvent.getUserId().toString(),
                assessmentSubmittedEvent
        );
    }
}
package com.skillpulse.assessment_service.model;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttemptSubmissionResult {

    private Boolean success;

    private String message;

    private Integer correctCount;

    private Integer totalQuestions;
}

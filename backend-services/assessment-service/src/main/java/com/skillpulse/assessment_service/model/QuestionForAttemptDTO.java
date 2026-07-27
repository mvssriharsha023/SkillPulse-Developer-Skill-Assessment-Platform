package com.skillpulse.assessment_service.model;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionForAttemptDTO {

    private Long id;

    private Long assessmentId;

    private String questionText;

    private String questionType;

    private String optionA;

    private String optionB;

    private String optionC;

    private String optionD;

    private Integer points;

    private Integer displayOrder;
}

package com.skillpulse.assessment_service.model;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResponseDTO {

    private Long id;

    private Long assessmentId;

    private String questionText;

    private String questionType;

    private String optionA;

    private String optionB;

    private String optionC;

    private String optionD;

    private String correctOption;

    private Integer points;
}

package com.skillpulse.assessment_service.model;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddQuestionRequestDTO {

    @NotNull(message = "Assessment ID is required")
    private Long assessmentId;

    @NotBlank(message = "Question Text is required")
    private String questionText;

    @NotBlank(message = "Question Type is required")
    @Pattern(regexp = "MULTIPLE_CHOICE|TRUE_FALSE")
    private String questionType;

    @NotBlank(message = "Option A is required")
    private String optionA;

    @NotBlank(message = "Option B is required")
    private String optionB;

    private String optionC;

    private String optionD;

    @NotBlank(message = "Correct Option is required")
    @Pattern(regexp = "[ABCD]")
    private String correctOption;

    @NotNull(message = "Points is required")
    @Min(value = 1)
    @Max(value = 100)
    private Integer points;

    @NotNull(message = "Display Order is required")
    @Min(value = 1)
    private Integer displayOrder;
}

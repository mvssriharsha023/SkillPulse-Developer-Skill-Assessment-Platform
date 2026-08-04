package com.skillpulse.assessment_service.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateAssessmentRequestDTO {

    @NotNull(message = "ID is required")
    private Long id;

    @NotBlank(message = "Assessment title is required")
    private String title;

    private String description;

    @NotBlank(message = "Skill category is required")
    private String skillCategory;

    @NotBlank(message = "Difficulty of assessment is required")
    @Pattern(regexp = "BEGINNER|INTERMEDIATE|ADVANCED")
    private String difficulty;

    @NotNull(message = "Time limit of assessment is required")
    private Integer timeLimitMinutes;

    @NotNull(message = "Passing score of assessment is required")
    private Integer passingScore;
}

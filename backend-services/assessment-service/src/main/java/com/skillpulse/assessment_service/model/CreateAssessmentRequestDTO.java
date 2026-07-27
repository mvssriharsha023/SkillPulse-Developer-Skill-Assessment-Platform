package com.skillpulse.assessment_service.model;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAssessmentRequestDTO {

    @NotBlank(message = "Title is required")
    @Size(min = 5, max = 200, message = "Title should be between 5 to 200 characters")
    private String title;

    private String description;

    @NotBlank(message = "Skill Category is missing")
    private String skillCategory;

    @NotBlank(message = "Difficulty is required")
    @Pattern(regexp = "BEGINNER|INTERMEDIATE|ADVANCED")
    private String difficulty;

    @NotNull(message = "Time Limit is required in minutes")
    @Min(value = 5)
    @Max(value = 180)
    private Integer timeLimitMinutes;

    @NotNull(message = "Passing Score is required")
    @Min(value = 1)
    @Max(value = 100)
    private Integer passingScore;

    @NotNull(message = "Created User is required")
    private Long createdByUserId;
}

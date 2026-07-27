package com.skillpulse.assessment_service.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmitAnswerRequestDTO {

    @NotNull(message = "Attempt ID is required")
    private Long attemptId;

    @NotNull(message = "Question ID is required")
    private Long questionId;

    @NotBlank(message = "Selected option is required")
    @Pattern(regexp = "[ABCD]")
    private String selectedOption;
}

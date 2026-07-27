package com.skillpulse.assessment_service.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmitAttemptRequestDTO {

    @NotNull(message = "Attempt ID is required")
    private Long attemptId;

    @NotNull(message = "Time taken required in seconds")
    @Min(1)
    private Integer timeTakenSeconds;
}

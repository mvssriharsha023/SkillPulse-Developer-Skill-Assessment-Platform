package com.skillpulse.user_service.model;

import jakarta.validation.constraints.Min;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequestDTO {

    private String bio;

    private String githubUrl;

    @Min(value = 0)
    private Integer experienceYears;

    private String primarySkill;
}

package com.skillpulse.user_service.model;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileStatsRequestDTO {

    private Double newScore;

    private Boolean passed;
}

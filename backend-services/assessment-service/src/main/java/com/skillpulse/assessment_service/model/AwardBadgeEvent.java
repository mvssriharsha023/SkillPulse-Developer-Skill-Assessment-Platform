package com.skillpulse.assessment_service.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AwardBadgeEvent {

    private Long userId;

    private List<AttemptResultDTO> attempts;
}

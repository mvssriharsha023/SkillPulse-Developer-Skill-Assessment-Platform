package com.skillpulse.user_service.model;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BadgeResponseDTO {

    private Long id;

    private String name;

    private String description;

    private String iconUrl;

    private String criteriaType;

    private Integer criteriaValue;
}

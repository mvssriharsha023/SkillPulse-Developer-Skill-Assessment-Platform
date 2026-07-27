package com.skillpulse.assessment_service.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserClientDTO {

    private Long id;

    private String fullName;

    private String email;

    private String role;

    private String status;
}

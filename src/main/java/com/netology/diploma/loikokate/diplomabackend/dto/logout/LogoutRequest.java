package com.netology.diploma.loikokate.diplomabackend.dto.logout;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LogoutRequest {

    @JsonProperty("auth-token")
    private String authToken;
}

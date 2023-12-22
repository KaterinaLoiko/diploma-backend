package com.netology.diploma.loikokate.diplomabackend.dto.login;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class LoginResponse {

    @JsonProperty("auth-token")
    private String authToken;
}

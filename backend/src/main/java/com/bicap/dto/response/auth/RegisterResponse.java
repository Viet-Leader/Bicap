package com.bicap.dto.response.auth;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterResponse {

    private Long accountId;

    private String username;

    private String role;
}
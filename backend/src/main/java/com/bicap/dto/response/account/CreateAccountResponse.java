package com.bicap.dto.response.account;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateAccountResponse {

    private Long accountId;

    private String username;

    private String fullName;

    private String email;

    private String phone;

    private String role;

    private String status;

    private String message;

}
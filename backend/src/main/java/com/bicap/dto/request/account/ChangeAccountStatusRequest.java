package com.bicap.dto.request.account;

import com.bicap.common.enums.AccountStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChangeAccountStatusRequest {

    @NotNull
    private AccountStatus status;

}
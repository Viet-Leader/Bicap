package com.bicap.mapper;

import com.bicap.dto.request.account.ChangeAccountStatusRequest;
import com.bicap.dto.request.account.CreateAccountRequest;
import com.bicap.dto.request.account.UpdateAccountRequest;
import com.bicap.dto.request.auth.RegisterRequest;
import com.bicap.dto.response.account.AccountResponse;
import com.bicap.dto.response.account.CreateAccountResponse;
import com.bicap.dto.response.auth.RegisterResponse;
import com.bicap.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    // ==========================================================
    // REGISTER
    // ==========================================================

    /**
     * RegisterRequest -> Account
     */
    @Mapping(target = "accountId", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "farm", ignore = true)
    @Mapping(target = "retailer", ignore = true)
    @Mapping(target = "notifications", ignore = true)
    Account toAccount(RegisterRequest request);

    /**
     * Account -> RegisterResponse
     */
    @Mapping(source = "role.roleName", target = "role")
    RegisterResponse toRegisterResponse(Account account);

    // ==========================================================
    // CREATE ACCOUNT
    // ==========================================================

    /**
     * CreateAccountRequest -> Account
     */
    @Mapping(target = "accountId", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "farm", ignore = true)
    @Mapping(target = "retailer", ignore = true)
    @Mapping(target = "notifications", ignore = true)
    Account toAccount(CreateAccountRequest request);

    /**
     * Account -> CreateAccountResponse
     */
    @Mapping(source = "role.roleName", target = "role")
    @Mapping(source = "status", target = "status")
    @Mapping(target = "message", ignore = true)
    CreateAccountResponse toCreateAccountResponse(Account account);

    // ==========================================================
    // GET ACCOUNT
    // ==========================================================

    /**
     * Account -> AccountResponse
     */
    @Mapping(source = "role.roleName", target = "role")
    @Mapping(source = "status", target = "status")
    AccountResponse toAccountResponse(Account account);

    // ==========================================================
    // UPDATE ACCOUNT
    // ==========================================================

    /**
     * UpdateAccountRequest -> Account
     */
    @Mapping(target = "accountId", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "farm", ignore = true)
    @Mapping(target = "retailer", ignore = true)
    @Mapping(target = "notifications", ignore = true)
    void updateAccountFromRequest(
            UpdateAccountRequest request,
            @MappingTarget Account account
    );

    // ==========================================================
    // CHANGE ACCOUNT STATUS
    // ==========================================================

    /**
     * ChangeAccountStatusRequest -> Account
     */
    @Mapping(target = "accountId", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "fullName", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "phone", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "farm", ignore = true)
    @Mapping(target = "retailer", ignore = true)
    @Mapping(target = "notifications", ignore = true)
    @Mapping(source = "status", target = "status")
    void updateStatusFromRequest(
            ChangeAccountStatusRequest request,
            @MappingTarget Account account
    );
}
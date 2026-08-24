package com.bicap.service;

import java.util.List;

import com.bicap.dto.request.account.ChangeAccountStatusRequest;
import com.bicap.dto.request.account.ChangePasswordRequest;
import com.bicap.dto.request.account.CreateAccountRequest;
import com.bicap.dto.request.account.UpdateAccountRequest;
import com.bicap.dto.response.account.AccountResponse;
import com.bicap.dto.response.account.ChangePasswordResponse;
import com.bicap.dto.response.account.CreateAccountResponse;

public interface AccountService {

    CreateAccountResponse createAccount(CreateAccountRequest request);

     // ==========================================================
    // GET ACCOUNT
    // ==========================================================

    List<AccountResponse> getAllAccounts();

    AccountResponse getAccountById(Long accountId);

    // ==========================================================
    // UPDATE ACCOUNT
    // ==========================================================

    AccountResponse updateAccount(
            Long accountId,
            UpdateAccountRequest request
    );

    // ==========================================================
    // CHANGE ACCOUNT STATUS
    // ==========================================================

    AccountResponse changeAccountStatus(
            Long accountId,
            ChangeAccountStatusRequest request
    );

    // ==========================================================
    // CHANGE PASSWORD
    // ==========================================================

    ChangePasswordResponse changePassword(
            ChangePasswordRequest request
    );

}
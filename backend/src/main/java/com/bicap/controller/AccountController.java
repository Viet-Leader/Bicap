package com.bicap.controller;

import com.bicap.dto.request.account.ChangeAccountStatusRequest;
import com.bicap.dto.request.account.ChangePasswordRequest;
import com.bicap.dto.request.account.CreateAccountRequest;
import com.bicap.dto.request.account.UpdateAccountRequest;
import com.bicap.dto.response.account.AccountResponse;
import com.bicap.dto.response.account.ChangePasswordResponse;
import com.bicap.dto.response.account.CreateAccountResponse;
import com.bicap.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    // ==========================================================
    // CREATE ACCOUNT
    // ==========================================================

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CreateAccountResponse> createAccount(
            @Valid @RequestBody CreateAccountRequest request) {

        CreateAccountResponse response =
                accountService.createAccount(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // ==========================================================
    // GET ACCOUNT
    // ==========================================================

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AccountResponse>> getAllAccounts() {

        return ResponseEntity.ok(
                accountService.getAllAccounts()
        );
    }

    @GetMapping("/{accountId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AccountResponse> getAccountById(
            @PathVariable Long accountId) {

        return ResponseEntity.ok(
                accountService.getAccountById(accountId)
        );
    }

    // ==========================================================
    // UPDATE ACCOUNT
    // ==========================================================

    @PutMapping("/{accountId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AccountResponse> updateAccount(
            @PathVariable Long accountId,
            @Valid @RequestBody UpdateAccountRequest request) {

        return ResponseEntity.ok(
                accountService.updateAccount(accountId, request)
        );
    }

    // ==========================================================
    // CHANGE ACCOUNT STATUS
    // ==========================================================

    @PatchMapping("/{accountId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AccountResponse> changeAccountStatus(
            @PathVariable Long accountId,
            @Valid @RequestBody ChangeAccountStatusRequest request) {

        return ResponseEntity.ok(
                accountService.changeAccountStatus(accountId, request)
        );
    }

    // ==========================================================
    // CHANGE PASSWORD
    // ==========================================================

    @PutMapping("/me/password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ChangePasswordResponse> changePassword(
            @Valid @RequestBody ChangePasswordRequest request) {

        return ResponseEntity.ok(
                accountService.changePassword(request)
        );
    }

}
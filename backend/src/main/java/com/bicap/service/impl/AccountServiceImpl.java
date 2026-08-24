package com.bicap.service.impl;

import com.bicap.common.enums.AccountStatus;
import com.bicap.common.enums.RoleName;
import com.bicap.dto.request.account.CreateAccountRequest;
import com.bicap.dto.response.account.CreateAccountResponse;
import com.bicap.entity.Account;
import com.bicap.entity.Farm;
import com.bicap.entity.Retailer;
import com.bicap.entity.Role;
import com.bicap.exception.BadRequestException;
import com.bicap.exception.ResourceNotFoundException;
import com.bicap.mapper.AccountMapper;
import com.bicap.mapper.FarmMapper;
import com.bicap.mapper.RetailerMapper;
import com.bicap.repository.AccountRepository;
import com.bicap.repository.FarmRepository;
import com.bicap.repository.RetailerRepository;
import com.bicap.service.AccountService;
import com.bicap.service.RoleService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.bicap.dto.request.account.ChangeAccountStatusRequest;
import com.bicap.dto.request.account.ChangePasswordRequest;
import com.bicap.dto.request.account.UpdateAccountRequest;
import com.bicap.dto.response.account.AccountResponse;
import com.bicap.dto.response.account.ChangePasswordResponse;
import com.bicap.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    private final FarmRepository farmRepository;

    private final RetailerRepository retailerRepository;

    private final AccountMapper accountMapper;

    private final FarmMapper farmMapper;

    private final RetailerMapper retailerMapper;

    private final PasswordEncoder passwordEncoder;

    private final RoleService roleService;

    // ==========================================================
    // CREATE ACCOUNT
    // ==========================================================

    @Override
    @Transactional
    public CreateAccountResponse createAccount(CreateAccountRequest request) {

        validateCreateAccountRequest(request);

        Role role = roleService.getRole(request.getRole());

        Account account = createAccountEntity(request, role);

        switch (request.getRole()) {

            case FARM -> createFarm(account, request);

            case RETAILER -> createRetailer(account, request);

            default -> throw new BadRequestException("Invalid role.");
        }

                CreateAccountResponse response =
                accountMapper.toCreateAccountResponse(account);

        response.setMessage("Account created successfully.");

        return response;
    }
    //get all account
    @Override
    public List<AccountResponse> getAllAccounts() {

        return accountRepository.findAll()
                .stream()
                .map(accountMapper::toAccountResponse)
                .toList();
    }

    //get account by id
    @Override
    public AccountResponse getAccountById(Long accountId) {

        Account account = getAccount(accountId);

        return accountMapper.toAccountResponse(account);
    }

    //UPDATE ACCOUNT
    @Override
    @Transactional
    public AccountResponse updateAccount(
            Long accountId,
            UpdateAccountRequest request) {

        Account account = getAccount(accountId);

        validateUpdateAccount(accountId, request);

        accountMapper.updateAccountFromRequest(
                request,
                account
        );

        accountRepository.save(account);

        return accountMapper.toAccountResponse(account);
    }

    //CHANGE ACCOUNT STATUS
    @Override
    @Transactional
    public AccountResponse changeAccountStatus(
            Long accountId,
            ChangeAccountStatusRequest request) {

        Account account = getAccount(accountId);

        accountMapper.updateStatusFromRequest(
                request,
                account
        );

        accountRepository.save(account);

        return accountMapper.toAccountResponse(account);
    }

    //CHANGE PASSWORD
    @Override
    @Transactional
    public ChangePasswordResponse changePassword(
            ChangePasswordRequest request) {

        CustomUserDetails currentUser = getCurrentUser();

        Account account = currentUser.getAccount();

        if (!passwordEncoder.matches(
                request.getOldPassword(),
                account.getPasswordHash())) {

            throw new BadRequestException(
                    "Old password is incorrect."
            );
        }

        account.setPasswordHash(
                passwordEncoder.encode(request.getNewPassword())
        );

        accountRepository.save(account);

        return ChangePasswordResponse.builder()
                .message("Password changed successfully.")
                .build();
    }

    // ==========================================================
    // VALIDATION
    // ==========================================================

    private void validateCreateAccountRequest(CreateAccountRequest request) {

        validateCommonInformation(request);

        switch (request.getRole()) {

            case FARM -> validateFarmInformation(request);

            case RETAILER -> validateRetailerInformation(request);

            default -> throw new BadRequestException("Invalid role.");
        }

    }

    /**
     * Validate thông tin chung của Account
     */
    private void validateCommonInformation(CreateAccountRequest request) {

    // ==========================================
    // Validate dữ liệu bắt buộc
    // ==========================================

    if (request.getUsername() == null ||
            request.getUsername().isBlank()) {

        throw new BadRequestException("Username is required.");
    }

    if (request.getPassword() == null ||
            request.getPassword().isBlank()) {

        throw new BadRequestException("Password is required.");
    }

    if (request.getFullName() == null ||
            request.getFullName().isBlank()) {

        throw new BadRequestException("Full name is required.");
    }

    if (request.getEmail() == null ||
            request.getEmail().isBlank()) {

        throw new BadRequestException("Email is required.");
    }

    if (request.getPhone() == null ||
            request.getPhone().isBlank()) {

        throw new BadRequestException("Phone is required.");
    }

    // ==========================================
    // Validate dữ liệu trùng
    // ==========================================

    if (accountRepository.existsByUsername(request.getUsername())) {
        throw new BadRequestException("Username already exists.");
    }

    if (accountRepository.existsByEmail(request.getEmail())) {
        throw new BadRequestException("Email already exists.");
    }

    if (accountRepository.existsByPhone(request.getPhone())) {
        throw new BadRequestException("Phone already exists.");
    }

}

    /**
     * Validate dữ liệu Farm
     */
    private void validateFarmInformation(CreateAccountRequest request) {

        if (request.getFarmName() == null ||
                request.getFarmName().isBlank()) {

            throw new BadRequestException("Farm name is required.");
        }

        if (request.getBusinessLicense() == null ||
                request.getBusinessLicense().isBlank()) {

            throw new BadRequestException("Business license is required.");
        }

        if (request.getAddress() == null ||
                request.getAddress().isBlank()) {

            throw new BadRequestException("Address is required.");
        }

        if (farmRepository.existsByBusinessLicense(
                request.getBusinessLicense())) {

            throw new BadRequestException(
                    "Business license already exists."
            );
        }

    }

    /**
     * Validate dữ liệu Retailer
     */
    private void validateRetailerInformation(
            CreateAccountRequest request) {

        if (request.getRetailerName() == null ||
                request.getRetailerName().isBlank()) {

            throw new BadRequestException("Retailer name is required.");
        }

        if (request.getAddress() == null ||
                request.getAddress().isBlank()) {

            throw new BadRequestException("Address is required.");
        }

    }

    /**
 * Validate dữ liệu cập nhật Account
 */
private void validateUpdateAccount(
        Long accountId,
        UpdateAccountRequest request) {

    accountRepository.findByEmail(request.getEmail())
            .ifPresent(account -> {

                if (!account.getAccountId().equals(accountId)) {

                    throw new BadRequestException(
                            "Email already exists."
                    );

                }

            });

    accountRepository.findByPhone(request.getPhone())
            .ifPresent(account -> {

                if (!account.getAccountId().equals(accountId)) {

                    throw new BadRequestException(
                            "Phone already exists."
                    );

                }

            });

}
    // ==========================================================
    // CREATE ENTITY
    // ==========================================================
        /**
     * Tạo Account
     */
    private Account createAccountEntity(CreateAccountRequest request,
                                        Role role) {

        Account account = accountMapper.toAccount(request);

        account.setPasswordHash(
                passwordEncoder.encode(request.getPassword())
        );

        account.setRole(role);

        account.setStatus(AccountStatus.ACTIVE);

        return accountRepository.save(account);
    }

    /**
     * Tạo Farm
     */
    private Farm createFarm(Account account,
                            CreateAccountRequest request) {

        Farm farm = farmMapper.toFarm(request);

        farm.setAccount(account);

        farm.setStatus(AccountStatus.ACTIVE.name());

        return farmRepository.save(farm);
    }

    /**
     * Tạo Retailer
     */
    private Retailer createRetailer(Account account,
                                    CreateAccountRequest request) {

        Retailer retailer = retailerMapper.toRetailer(request);

        retailer.setAccount(account);

        return retailerRepository.save(retailer);
    }
    // Private Method
    // GET ACCOUNT

    private Account getAccount(Long accountId) {

    return accountRepository.findById(accountId)
            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Account not found."
                    ));
}
    
    //get current user
    private CustomUserDetails getCurrentUser() {

    Authentication authentication =
            SecurityContextHolder
                    .getContext()
                    .getAuthentication();

    return (CustomUserDetails) authentication.getPrincipal();
    }   
}
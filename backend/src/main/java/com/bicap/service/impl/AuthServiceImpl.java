package com.bicap.service.impl;

import com.bicap.common.enums.AccountStatus;
import com.bicap.common.enums.RoleName;
import com.bicap.dto.request.auth.LoginRequest;
import com.bicap.dto.request.auth.RegisterRequest;
import com.bicap.dto.response.auth.LoginResponse;
import com.bicap.dto.response.auth.RegisterResponse;
import com.bicap.entity.Account;
import com.bicap.entity.Retailer;
import com.bicap.entity.Role;
import com.bicap.exception.BadRequestException;
import com.bicap.exception.UnauthorizedException;
import com.bicap.mapper.AccountMapper;
import com.bicap.mapper.RetailerMapper;
import com.bicap.repository.AccountRepository;
import com.bicap.repository.RetailerRepository;
import com.bicap.security.JwtService;
import com.bicap.service.AuthService;
import com.bicap.service.RoleService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;

    private final AccountRepository accountRepository;

    private final RetailerRepository retailerRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final AccountMapper accountMapper;

    private final RetailerMapper retailerMapper;

    private final RoleService roleService;

    // ==========================================================
    // LOGIN
    // ==========================================================

    @Override
    public LoginResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        Account account = accountRepository
                .findByUsername(request.getUsername())
                .orElseThrow(() ->
                        new UnauthorizedException("Invalid username or password."));

        String token = jwtService.generateToken(account);

        return LoginResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .username(account.getUsername())
                .role(account.getRole().getRoleName())
                .build();
    }

    // ==========================================================
    // REGISTER
    // ==========================================================

    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest request) {

        validateRegisterRequest(request);

        Role role = roleService.getRole(RoleName.RETAILER);

        Account account = createAccount(request, role);

        createRetailer(account, request);

        return accountMapper.toRegisterResponse(account);
    }

    // ==========================================================
    // VALIDATION
    // ==========================================================

    private void validateRegisterRequest(RegisterRequest request) {

        if (accountRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already exists.");
        }

        if (accountRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists.");
        }
    }

    // ==========================================================
    // CREATE ENTITY
    // ==========================================================

    private Account createAccount(RegisterRequest request, Role role) {

        Account account = accountMapper.toAccount(request);

        account.setPasswordHash(
                passwordEncoder.encode(request.getPassword())
        );

        account.setRole(role);

        account.setStatus(AccountStatus.ACTIVE);

        return accountRepository.save(account);
    }

    private Retailer createRetailer(Account account,
                                    RegisterRequest request) {

        Retailer retailer = retailerMapper.toRetailer(request);

        retailer.setAccount(account);

        return retailerRepository.save(retailer);
    }

}
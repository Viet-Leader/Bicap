package com.bicap.service;

import com.bicap.dto.request.auth.LoginRequest;
import com.bicap.dto.request.auth.RegisterRequest;
import com.bicap.dto.response.auth.LoginResponse;
import com.bicap.dto.response.auth.RegisterResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    RegisterResponse register(RegisterRequest request);
}
package com.bicap.dto.request.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "Username is required.")
    @Size(max = 50)
    private String username;

    @NotBlank(message = "Password is required.")
    @Size(min = 6, max = 255)
    private String password;

    @NotBlank(message = "Full name is required.")
    @Size(max = 100)
    private String fullName;

    @NotBlank(message = "Email is required.")
    @Email(message = "Invalid email format.")
    @Size(max = 100)
    private String email;

    @NotBlank(message = "Phone is required.")
    @Size(max = 20)
    private String phone;

    @NotBlank(message = "Retailer name is required.")
    @Size(max = 150)
    private String retailerName;

    @NotBlank(message = "Address is required.")
    @Size(max = 255)
    private String address;
}
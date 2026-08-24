package com.bicap.dto.request.account;

import com.bicap.common.enums.RoleName;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateAccountRequest {

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
    @Email
    @Size(max = 100)
    private String email;

    @NotBlank(message = "Phone is required.")
    @Size(max = 20)
    private String phone;

    @NotNull(message = "Role is required.")
    private RoleName role;

    // ===== FARM =====

    private String farmName;

    private String businessLicense;

    // ===== RETAILER =====

    private String retailerName;

    // ===== COMMON =====

    private String address;

    private String description;

}
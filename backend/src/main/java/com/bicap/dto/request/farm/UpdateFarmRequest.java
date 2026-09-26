package com.bicap.dto.request.farm;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO dùng để Farm cập nhật thông tin của chính mình.
 *
 * Chỉ cho phép cập nhật:
 * - Farm Name
 * - Address
 * - Description
 *
 * Không cho phép cập nhật:
 * - Business License
 * - Status
 * - Account
 */
@Data
public class UpdateFarmRequest {

    /**
     * Tên trang trại.
     */
    @NotBlank(message = "Farm name is required")
    @Size(max = 150, message = "Farm name must not exceed 150 characters")
    private String farmName;

    /**
     * Địa chỉ trang trại.
     */
    @NotBlank(message = "Address is required")
    @Size(max = 255, message = "Address must not exceed 255 characters")
    private String address;

    @Email(message = "Email must be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    @DecimalMin(value = "0", message = "Area size must not be negative")
    private BigDecimal areaSize;

    /**
     * Mô tả trang trại.
     */
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    private String imageUrl;

}
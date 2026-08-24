package com.bicap.dto.request.farm;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

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

    /**
     * Mô tả trang trại.
     */
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

}
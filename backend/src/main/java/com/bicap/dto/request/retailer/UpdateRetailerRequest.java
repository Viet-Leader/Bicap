package com.bicap.dto.request.retailer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateRetailerRequest {

    @NotBlank(message = "Tên cửa hàng không được để trống.")
    @Size(max = 150, message = "Tên cửa hàng không được vượt quá 150 ký tự.")
    private String retailerName;

    @NotBlank(message = "Địa chỉ không được để trống.")
    @Size(max = 255, message = "Địa chỉ không được vượt quá 255 ký tự.")
    private String address;

}
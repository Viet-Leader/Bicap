package com.bicap.service;

import com.bicap.dto.request.retailer.UpdateRetailerRequest;
import com.bicap.dto.response.retailer.RetailerResponse;

public interface RetailerService {

    /**
     * Lấy thông tin Retailer của người đang đăng nhập.
     *
     * @return RetailerResponse
     */
    RetailerResponse getMyRetailer();

    /**
     * Cập nhật thông tin Retailer của người đang đăng nhập.
     *
     * @param request dữ liệu cập nhật
     * @return RetailerResponse
     */
    RetailerResponse updateMyRetailer(UpdateRetailerRequest request);

}
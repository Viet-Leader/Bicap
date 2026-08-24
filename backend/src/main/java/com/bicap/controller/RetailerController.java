package com.bicap.controller;

import com.bicap.dto.request.retailer.UpdateRetailerRequest;
import com.bicap.dto.response.retailer.RetailerResponse;
import com.bicap.service.RetailerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/retailers")
@RequiredArgsConstructor
public class RetailerController {

    private final RetailerService retailerService;

    // ==========================================================
    // GET MY RETAILER
    // ==========================================================

    @GetMapping("/me")
    @PreAuthorize("hasRole('RETAILER')")
    public ResponseEntity<RetailerResponse> getMyRetailer() {

        return ResponseEntity.ok(
                retailerService.getMyRetailer()
        );
    }

    // ==========================================================
    // UPDATE MY RETAILER
    // ==========================================================

    @PutMapping("/me")
    @PreAuthorize("hasRole('RETAILER')")
    public ResponseEntity<RetailerResponse> updateMyRetailer(
            @Valid @RequestBody UpdateRetailerRequest request) {

        return ResponseEntity.ok(
                retailerService.updateMyRetailer(request)
        );
    }

}
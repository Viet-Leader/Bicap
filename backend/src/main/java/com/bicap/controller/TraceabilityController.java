package com.bicap.controller;

import com.bicap.dto.response.traceability.TraceabilityResponse;
import com.bicap.service.TraceabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/public/traceability")
public class TraceabilityController {

    private final TraceabilityService traceabilityService;

    /**
     * Tra cứu thông tin truy xuất nguồn gốc của Product Batch.
     */
    @GetMapping("/batch/{batchId}")
    public ResponseEntity<TraceabilityResponse> getByBatchId(
            @PathVariable Long batchId
    ) {

        return ResponseEntity.ok(
                traceabilityService.getByBatchId(batchId)
        );
    }
}
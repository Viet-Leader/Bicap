package com.bicap.controller;

import com.bicap.dto.response.blockchain.BlockchainTransactionResponse;
import com.bicap.dto.response.blockchain.BlockchainVerificationResponse;
import com.bicap.service.BlockchainService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/blockchain")
public class BlockchainController {

    private final BlockchainService blockchainService;

    @GetMapping("/transactions")
    public ResponseEntity<Page<BlockchainTransactionResponse>> getTransactionHistory(Pageable pageable) {
        return ResponseEntity.ok(blockchainService.getTransactionHistory(pageable));
    }

    /**
     * Verify a ProductBatch from the administration transaction screen.
     */
    @GetMapping("/verify/product-batch/{batchId}")
    public ResponseEntity<BlockchainVerificationResponse> verifyProductBatch(
            @PathVariable Long batchId
    ) {
        return ResponseEntity.ok(blockchainService.verifyProductBatch(batchId));
    }

    /**
     * Verify a SeasonActivity from the administration transaction screen.
     */
    @GetMapping("/verify/season-activity/{activityId}")
    public ResponseEntity<BlockchainVerificationResponse> verifySeasonActivity(
            @PathVariable Long activityId
    ) {
        return ResponseEntity.ok(blockchainService.verifySeasonActivity(activityId));
    }
}

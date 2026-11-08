package com.bicap.controller;

import com.bicap.dto.response.blockchain.BlockchainTransactionResponse;
import com.bicap.service.BlockchainService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
}

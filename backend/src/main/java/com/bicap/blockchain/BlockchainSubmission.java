package com.bicap.blockchain;

import java.math.BigInteger;
import java.time.LocalDateTime;

public record BlockchainSubmission(
        String txHash,
        BigInteger blockNumber,
        BigInteger gasUsed,
        LocalDateTime confirmedAt
) {}

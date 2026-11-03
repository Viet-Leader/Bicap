package com.bicap.blockchain;

import java.math.BigInteger;

/**
 * Immutable representation of a record returned by BicapTraceability.
 */
public record BlockchainRecord(
        String dataHash,
        BigInteger recordedAt,
        String recorder
) {

    public boolean isRecorded() {
        return dataHash != null
                && !dataHash.equalsIgnoreCase(
                        "0x0000000000000000000000000000000000000000000000000000000000000000"
                );
    }
}

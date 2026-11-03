package com.bicap.exception;

public class BlockchainUnavailableException extends RuntimeException {

    public BlockchainUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}

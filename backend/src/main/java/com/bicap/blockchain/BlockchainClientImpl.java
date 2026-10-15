package com.bicap.blockchain;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.response.PollingTransactionReceiptProcessor;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class BlockchainClientImpl implements BlockchainClient {

    private final Web3j web3j;
    private final TransactionManager transactionManager;
    private final String contractAddress;

    public BlockchainClientImpl(
            @Value("${blockchain.rpc-url}") String rpcUrl,
            @Value("${blockchain.private-key}") String privateKey,
            @Value("${blockchain.contract-address}") String contractAddress
    ) {

        String normalizedPrivateKey = privateKey == null
                ? ""
                : privateKey.trim()
                        .replace("\"", "")
                        .replace("'", "")
                        .replaceAll("\\s+", "");

        Matcher privateKeyMatcher = Pattern.compile(
                "(?i)(?:0x)?([0-9a-f]{64})"
        ).matcher(normalizedPrivateKey);

        if (privateKeyMatcher.find()) {
            normalizedPrivateKey = privateKeyMatcher.group(1);
        }

        if (!normalizedPrivateKey.matches("[0-9a-fA-F]{64}")) {
            throw new IllegalArgumentException(
                    "blockchain.private-key must be exactly 32 bytes (64 hex characters); "
                            + "received " + normalizedPrivateKey.length() + " hex characters."
            );
        }

        this.web3j = Web3j.build(
                new HttpService(rpcUrl)
        );

        Credentials credentials =
                Credentials.create(normalizedPrivateKey);

        this.transactionManager =
                new RawTransactionManager(
                        web3j,
                        credentials
                );

        this.contractAddress = contractAddress;

        log.info(
                "[Blockchain] signer configured — address: {}, rpc: {}, contract: {}",
                credentials.getAddress(),
                rpcUrl,
                contractAddress
        );
    }

    @Override
    public String submitTransaction(
            Long batchId,
            String dataHash
    ) {
        if (batchId == null || batchId <= 0) {
            throw new IllegalArgumentException("Invalid batch ID.");
        }

        return sendContractCall("recordProductBatch", batchId, dataHash);
    }

    @Override
    public String submitSeasonActivity(
            Long activityId,
            String dataHash
    ) {
        if (activityId == null || activityId <= 0) {
            throw new IllegalArgumentException("Invalid activity ID.");
        }

        return sendContractCall("recordSeasonActivity", activityId, dataHash);
    }

    // ============================================================
    // Shared: encode ABI & send transaction to the contract
    // ============================================================

        private synchronized String sendContractCall(
            String functionName,
            Long entityId,
            String dataHash
    ) {
        if (dataHash == null || dataHash.isBlank()) {
            throw new IllegalArgumentException("Data hash is required.");
        }

        if (!dataHash.startsWith("0x")) {
            dataHash = "0x" + dataHash;
        }

        if (dataHash.length() != 66) {
            throw new IllegalArgumentException(
                    "SHA-256 hash must contain 32 bytes (66 hex chars with 0x prefix)."
            );
        }

        String encodedData =
                org.web3j.abi.FunctionEncoder.encode(
                        new org.web3j.abi.datatypes.Function(
                                functionName,
                                List.of(
                                        new org.web3j.abi.datatypes.generated.Uint256(
                                                BigInteger.valueOf(entityId)
                                        ),
                                        new org.web3j.abi.datatypes.generated.Bytes32(
                                                Numeric.hexStringToByteArray(dataHash)
                                        )
                                ),
                                List.of()
                        )
                );

        try {

            var response = sendTransactionWithSigningRetry(encodedData, functionName);

            if (response.hasError()) {
                var error = response.getError();
                throw new IllegalStateException(
                        "RPC error (code " + error.getCode() + "): "
                                + error.getMessage()
                                + (error.getData() == null ? "" : " [" + error.getData() + "]")
                );
            }

            if (response.getTransactionHash() == null
                    || response.getTransactionHash().isBlank()) {
                throw new IllegalStateException(
                        "RPC returned no transaction hash for " + functionName
                );
            }

            TransactionReceipt receipt =
                    new PollingTransactionReceiptProcessor(
                            web3j,
                            1_000L,
                            30
                    ).waitForTransactionReceipt(response.getTransactionHash());

            if (!receipt.isStatusOK()) {
                throw new IllegalStateException(
                        "Transaction reverted with status " + receipt.getStatus()
                );
            }

            log.info(
                    "[Blockchain] {} submitted — txHash: {}",
                    functionName,
                    response.getTransactionHash()
            );

            return response.getTransactionHash();

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to submit transaction to blockchain: " + functionName
                            + " (" + e.getMessage() + ")",
                    e
            );
        }
    }

        private org.web3j.protocol.core.methods.response.EthSendTransaction
        sendTransactionWithSigningRetry(String encodedData, String functionName)
                        throws Exception {
                try {
                        return sendTransaction(encodedData);
                } catch (IllegalArgumentException firstFailure) {
                        if (firstFailure.getMessage() == null
                                        || !firstFailure.getMessage().contains("Scalar is not in the interval")) {
                                throw firstFailure;
                        }

                        log.warn(
                                        "[Blockchain] transient signing failure for {}; retrying once",
                                        functionName
                        );
                        return sendTransaction(encodedData);
                }
        }

        private org.web3j.protocol.core.methods.response.EthSendTransaction
        sendTransaction(String encodedData) throws Exception {
                return transactionManager.sendTransaction(
                                BigInteger.valueOf(1_000_000_000L),
                                BigInteger.valueOf(300_000L),
                                contractAddress,
                                encodedData,
                                BigInteger.ZERO
                );
        }
}

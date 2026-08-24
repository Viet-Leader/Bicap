package com.bicap.blockchain;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.List;

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

        this.web3j = Web3j.build(
                new HttpService(rpcUrl)
        );

        Credentials credentials =
                Credentials.create(privateKey);

        this.transactionManager =
                new RawTransactionManager(
                        web3j,
                        credentials
                );

        this.contractAddress = contractAddress;
    }

    @Override
    public String submitTransaction(
            Long batchId,
            String dataHash
    ) {

        if (batchId == null || batchId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid batch ID."
            );
        }

        if (dataHash == null || dataHash.isBlank()) {
            throw new IllegalArgumentException(
                    "Data hash is required."
            );
        }

        if (!dataHash.startsWith("0x")) {
            dataHash = "0x" + dataHash;
        }

        if (dataHash.length() != 66) {
            throw new IllegalArgumentException(
                    "SHA-256 hash must contain 32 bytes."
            );
        }

        String functionSelector =
                org.web3j.abi.FunctionEncoder.encode(
                        new org.web3j.abi.datatypes.Function(
                                "recordProductBatch",
                                List.of(
                                        new org.web3j.abi.datatypes.generated.Uint256(
                                                BigInteger.valueOf(batchId)
                                        ),
                                        new org.web3j.abi.datatypes.generated.Bytes32(
                                                Numeric.hexStringToByteArray(
                                                        dataHash
                                                )
                                        )
                                ),
                                List.of()
                        )
                );

        try {

           var response =
        transactionManager.sendTransaction(
                BigInteger.valueOf(1_000_000_000L),
                BigInteger.valueOf(300_000L),
                contractAddress,
                functionSelector,
                BigInteger.ZERO
        );

if (response.hasError()) {
    System.out.println("Blockchain error code: "
            + response.getError().getCode());

    System.out.println("Blockchain error message: "
            + response.getError().getMessage());

    System.out.println("Blockchain error data: "
            + response.getError().getData());
}

System.out.println(
        "Transaction hash: "
                + response.getTransactionHash()
);

return response.getTransactionHash();

        } catch (Exception e) {

            throw new IllegalStateException(
                    "Failed to submit transaction to blockchain.",
                    e
            );
        }
    }
}
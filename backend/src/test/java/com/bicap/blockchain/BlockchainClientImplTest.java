package com.bicap.blockchain;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.web3j.protocol.Web3j;

import java.security.MessageDigest;

import static org.junit.jupiter.api.Assertions.*;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.http.HttpService;
class BlockchainClientImplTest {

    private static BlockchainClientImpl blockchainClient;

    @BeforeAll
    static void setUp() {

    String rpcUrl = "http://127.0.0.1:8545";

    String privateKey =
            "0xac0974bec39a17e36ba4a6b4d238ff944bacb478cbed5efcae784d7bf4f2ff80";

    String contractAddress =
            "0x5FbDB2315678afecb367f032d93F642f64180aa3";

    blockchainClient = new BlockchainClientImpl(
            rpcUrl,
            privateKey,
            contractAddress
    );
}
@Test
void checkBlockchainAccount() throws Exception {

    String privateKey =
            "0xac0974bec39a17e36ba4a6b4d238ff944bacb478cbed5efcae784d7bf4f2ff80";

    Credentials credentials =
            Credentials.create(privateKey);

    System.out.println(
            "Private key: " + privateKey
    );

    System.out.println(
            "Address: " + credentials.getAddress()
    );

    Web3j web3j =
            Web3j.build(
                    new HttpService("http://127.0.0.1:8545")
            );

    var balance =
            web3j.ethGetBalance(
                    credentials.getAddress(),
                    org.web3j.protocol.core.DefaultBlockParameterName.LATEST
            ).send();

    System.out.println(
            "Balance: " +
            org.web3j.utils.Convert.fromWei(
                    balance.getBalance().toString(),
                    org.web3j.utils.Convert.Unit.ETHER
            ) +
            " ETH"
    );

    web3j.shutdown();
}
    @Test
    void submitTransaction_shouldReturnTransactionHash() throws Exception {

        Long batchId = 1L;

        String dataHash = sha256(
                "BICAP Product Batch Test"
        );

        String transactionHash =
                blockchainClient.submitTransaction(
                        batchId,
                        dataHash
                );

        assertNotNull(transactionHash);

        assertTrue(
                transactionHash.startsWith("0x"),
                "Transaction hash must start with 0x"
        );

        assertEquals(
                66,
                transactionHash.length(),
                "Transaction hash must contain 32 bytes"
        );

        System.out.println(
                "Transaction Hash: " + transactionHash
        );
    }

    @Test
    void submitTransaction_shouldRejectInvalidBatchId() {

        String dataHash =
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";

        assertThrows(
                IllegalArgumentException.class,
                () -> blockchainClient.submitTransaction(
                        0L,
                        dataHash
                )
        );
    }

    @Test
    void submitTransaction_shouldRejectEmptyDataHash() {

        assertThrows(
                IllegalArgumentException.class,
                () -> blockchainClient.submitTransaction(
                        1L,
                        ""
                )
        );
    }

    @Test
    void submitTransaction_shouldRejectInvalidHashLength() {

        String invalidHash =
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";

        assertThrows(
                IllegalArgumentException.class,
                () -> blockchainClient.submitTransaction(
                        1L,
                        invalidHash
                )
        );
    }

    private static String sha256(String value)
            throws Exception {

        MessageDigest digest =
                MessageDigest.getInstance("SHA-256");

        byte[] hash =
                digest.digest(value.getBytes());

        StringBuilder hex =
                new StringBuilder();

        for (byte b : hash) {
            hex.append(
                    String.format(
                            "%02x",
                            b
                    )
            );
        }

        return hex.toString();
    }
}
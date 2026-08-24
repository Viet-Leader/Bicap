import { expect } from "chai";
import { network } from "hardhat";

describe("BicapTraceability", function () {

    it("should record and retrieve product batch", async function () {

        const { ethers } = await network.connect();

        const [owner] = await ethers.getSigners();

        const BicapTraceability =
            await ethers.getContractFactory("BicapTraceability");

        const contract =
            await BicapTraceability.deploy();

        await contract.waitForDeployment();

        const batchId = 15n;

        const dataHash =
            ethers.keccak256(
                ethers.toUtf8Bytes("BICAP-BATCH-15")
            );

        const tx =
            await contract.recordProductBatch(
                batchId,
                dataHash
            );

        await tx.wait();

        const result =
            await contract.getProductBatch(batchId);

        expect(result[0]).to.equal(dataHash);
        expect(result[1]).to.be.greaterThan(0);
        expect(result[2]).to.equal(owner.address);
    });


    it("should reject invalid batch ID", async function () {

        const { ethers } = await network.connect();

        const BicapTraceability =
            await ethers.getContractFactory("BicapTraceability");

        const contract =
            await BicapTraceability.deploy();

        await contract.waitForDeployment();

        const dataHash =
            ethers.keccak256(
                ethers.toUtf8Bytes("BICAP-BATCH")
            );

        await expect(
            contract.recordProductBatch(
                0n,
                dataHash
            )
        ).to.be.revertedWith("Invalid batch ID");
    });


    it("should reject empty hash", async function () {

        const { ethers } = await network.connect();

        const BicapTraceability =
            await ethers.getContractFactory("BicapTraceability");

        const contract =
            await BicapTraceability.deploy();

        await contract.waitForDeployment();

        await expect(
            contract.recordProductBatch(
                15n,
                ethers.ZeroHash
            )
        ).to.be.revertedWith("Invalid data hash");
    });

});
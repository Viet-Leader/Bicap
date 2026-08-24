import { network } from "hardhat";

const { ethers } = await network.connect();

console.log("Deploying BicapTraceability...");

const [deployer] = await ethers.getSigners();

console.log("Deployer:", deployer.address);

const balance = await ethers.provider.getBalance(deployer.address);

console.log("Balance:", ethers.formatEther(balance), "ETH");

const BicapTraceability =
    await ethers.getContractFactory("BicapTraceability");

const contract =
    await BicapTraceability.deploy();

await contract.waitForDeployment();

const contractAddress =
    await contract.getAddress();

console.log("BicapTraceability deployed successfully!");
console.log("Contract address:", contractAddress);
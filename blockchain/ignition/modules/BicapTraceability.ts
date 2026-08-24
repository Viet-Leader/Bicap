import { buildModule } from "@nomicfoundation/hardhat-ignition/modules";

const BicapTraceabilityModule = buildModule(
    "BicapTraceabilityModule",
    (m) => {

        const bicapTraceability = m.contract(
            "BicapTraceability"
        );

        return {
            bicapTraceability,
        };
    }
);

export default BicapTraceabilityModule;
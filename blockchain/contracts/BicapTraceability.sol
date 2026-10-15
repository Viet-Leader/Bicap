// SPDX-License-Identifier: MIT
pragma solidity ^0.8.28;

contract BicapTraceability {

    // ============================================================
    // ProductBatch Records
    // ============================================================

    struct ProductBatchRecord {
        bytes32 dataHash;
        uint256 recordedAt;
        address recorder;
    }

    mapping(uint256 => ProductBatchRecord) private batchRecords;

    event ProductBatchRecorded(
        uint256 indexed batchId,
        bytes32 indexed dataHash,
        uint256 recordedAt,
        address recorder
    );

    function recordProductBatch(
        uint256 batchId,
        bytes32 dataHash
    ) external {

        require(batchId > 0, "Invalid batch ID");
        require(dataHash != bytes32(0), "Invalid data hash");

        batchRecords[batchId] = ProductBatchRecord({
            dataHash: dataHash,
            recordedAt: block.timestamp,
            recorder: msg.sender
        });

        emit ProductBatchRecorded(
            batchId,
            dataHash,
            block.timestamp,
            msg.sender
        );
    }

    function getProductBatch(
        uint256 batchId
    )
        external
        view
        returns (
            bytes32 dataHash,
            uint256 recordedAt,
            address recorder
        )
    {
        ProductBatchRecord memory record = batchRecords[batchId];

        return (
            record.dataHash,
            record.recordedAt,
            record.recorder
        );
    }

    // ============================================================
    // SeasonActivity Records
    // ============================================================

    struct SeasonActivityRecord {
        bytes32 dataHash;
        uint256 recordedAt;
        address recorder;
    }

    mapping(uint256 => SeasonActivityRecord) private activityRecords;

    event SeasonActivityRecorded(
        uint256 indexed activityId,
        bytes32 indexed dataHash,
        uint256 recordedAt,
        address recorder
    );

    function recordSeasonActivity(
        uint256 activityId,
        bytes32 dataHash
    ) external {

        require(activityId > 0, "Invalid activity ID");
        require(dataHash != bytes32(0), "Invalid data hash");

        activityRecords[activityId] = SeasonActivityRecord({
            dataHash: dataHash,
            recordedAt: block.timestamp,
            recorder: msg.sender
        });

        emit SeasonActivityRecorded(
            activityId,
            dataHash,
            block.timestamp,
            msg.sender
        );
    }

    function getSeasonActivity(
        uint256 activityId
    )
        external
        view
        returns (
            bytes32 dataHash,
            uint256 recordedAt,
            address recorder
        )
    {
        SeasonActivityRecord memory record = activityRecords[activityId];

        return (
            record.dataHash,
            record.recordedAt,
            record.recorder
        );
    }
}
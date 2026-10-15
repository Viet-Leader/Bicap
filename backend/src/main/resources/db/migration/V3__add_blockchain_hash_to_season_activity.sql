-- Transaction proof for farming activities recorded on the blockchain.
ALTER TABLE season_activity
    ADD COLUMN IF NOT EXISTS blockchain_tx_hash VARCHAR(255);
ALTER TABLE blockchain_transaction ALTER COLUMN tx_hash DROP NOT NULL;
ALTER TABLE blockchain_transaction ADD COLUMN IF NOT EXISTS data_hash VARCHAR(66);
ALTER TABLE blockchain_transaction ADD COLUMN IF NOT EXISTS block_number BIGINT;
ALTER TABLE blockchain_transaction ADD COLUMN IF NOT EXISTS gas_used BIGINT;
ALTER TABLE blockchain_transaction ADD COLUMN IF NOT EXISTS confirmed_at TIMESTAMP WITHOUT TIME ZONE;
ALTER TABLE blockchain_transaction ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL;
CREATE INDEX IF NOT EXISTS idx_blockchain_transaction_created_at ON blockchain_transaction (created_at DESC);

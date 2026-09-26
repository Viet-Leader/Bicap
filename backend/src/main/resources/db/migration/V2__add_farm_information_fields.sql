-- Farm Information fields used by the Farm Information screen.
ALTER TABLE farm ADD COLUMN IF NOT EXISTS email VARCHAR(100);
ALTER TABLE farm ADD COLUMN IF NOT EXISTS area_size NUMERIC(12, 2);
ALTER TABLE farm ADD COLUMN IF NOT EXISTS image_url TEXT;

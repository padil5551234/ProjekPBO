-- Add durasi column to orders table if it doesn't exist
ALTER TABLE orders ADD COLUMN IF NOT EXISTS durasi INT DEFAULT 1;

-- Update existing records to have default duration of 1 month
UPDATE orders SET durasi = 1 WHERE durasi IS NULL;
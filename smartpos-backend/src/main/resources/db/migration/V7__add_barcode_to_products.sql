-- Add barcode column to products table
-- Since we have existing data, we make it nullable first
ALTER TABLE products 
ADD COLUMN barcode VARCHAR(100) NULL AFTER code;

-- Create unique index on barcode (allowing NULL values)
-- This ensures barcodes are unique when they exist
CREATE UNIQUE INDEX idx_products_barcode ON products(barcode);

-- Add index for faster barcode lookups
CREATE INDEX idx_products_barcode_search ON products(barcode);

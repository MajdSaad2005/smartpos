-- Rename ticket_stocks table to stock_movements
RENAME TABLE ticket_stocks TO stock_movements;

-- Update index names to reflect new table name
ALTER TABLE stock_movements DROP INDEX idx_ticket_stocks_product_id;
ALTER TABLE stock_movements DROP INDEX idx_ticket_stocks_ticket_id;
ALTER TABLE stock_movements DROP INDEX idx_ticket_stocks_created_at;
ALTER TABLE stock_movements DROP INDEX idx_ticket_stocks_type;

-- Recreate indexes with new names
CREATE INDEX idx_stock_movements_product_id ON stock_movements(product_id);
CREATE INDEX idx_stock_movements_ticket_id ON stock_movements(ticket_id);
CREATE INDEX idx_stock_movements_created_at ON stock_movements(created_at);
CREATE INDEX idx_stock_movements_type ON stock_movements(type);

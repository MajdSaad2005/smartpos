-- Update ticket_stocks table (Stock Movement changes)
ALTER TABLE ticket_stocks 
    ADD COLUMN quantity_change INT NOT NULL DEFAULT 0 AFTER quantity,
    ADD COLUMN has_quantity_changed BOOLEAN NOT NULL DEFAULT TRUE AFTER quantity_change,
    ADD COLUMN is_defective BOOLEAN NOT NULL DEFAULT FALSE;

-- First, convert the type column to VARCHAR to allow updating values
ALTER TABLE ticket_stocks 
    MODIFY COLUMN type VARCHAR(50) NOT NULL;

-- Update existing data: map old DECREASE to SALE, INCREASE to RETURN
UPDATE ticket_stocks 
SET type = 'SALE', 
    quantity_change = -quantity,
    has_quantity_changed = TRUE
WHERE type = 'DECREASE';

UPDATE ticket_stocks 
SET type = 'RETURN', 
    quantity_change = quantity,
    has_quantity_changed = TRUE
WHERE type = 'INCREASE';

-- Now modify the type enum to use new values
ALTER TABLE ticket_stocks 
    MODIFY COLUMN type ENUM('SALE', 'RETURN', 'ADJUSTMENT') NOT NULL;

-- Add cashier_name to close_cash table
ALTER TABLE close_cash 
    ADD COLUMN cashier_name VARCHAR(255);

-- Update close_cash to allow null for closedAt (for open sessions)
ALTER TABLE close_cash 
    MODIFY COLUMN closed_at DATETIME NULL;

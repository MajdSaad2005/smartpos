-- Performance Optimization: Create indexes for frequently queried columns
-- Indexes improve query performance for JOINs, WHERE clauses, and ORDER BY

-- Index on ticket created_at for date range queries
CREATE INDEX idx_tickets_created_at ON tickets(created_at);

-- Index on ticket status for filtering by status
CREATE INDEX idx_tickets_status ON tickets(status);

-- Index on ticket customer_id for customer lookup queries
CREATE INDEX idx_tickets_customer_id ON tickets(customer_id);

-- Index on ticket close_cash_id for cash session queries
CREATE INDEX idx_tickets_close_cash_id ON tickets(close_cash_id);

-- Index on ticket type for filtering sales vs returns
CREATE INDEX idx_tickets_type ON tickets(type);

-- Composite index for date range + status queries (most common query pattern)
CREATE INDEX idx_tickets_created_status ON tickets(created_at, status);

-- Index on ticket_lines product_id for product sales analysis
CREATE INDEX idx_ticket_lines_product_id ON ticket_lines(product_id);

-- Index on ticket_lines ticket_id for joining with tickets
CREATE INDEX idx_ticket_lines_ticket_id ON ticket_lines(ticket_id);

-- Index on ticket_stocks for stock movement queries
CREATE INDEX idx_ticket_stocks_product_id ON ticket_stocks(product_id);
CREATE INDEX idx_ticket_stocks_ticket_id ON ticket_stocks(ticket_id);
CREATE INDEX idx_ticket_stocks_created_at ON ticket_stocks(created_at);
CREATE INDEX idx_ticket_stocks_type ON ticket_stocks(type);

-- Index on stock_current product_id for quick stock lookups
CREATE INDEX idx_stock_current_product_id ON stock_current(product_id);

-- Index on stock_levels product_id for minimum/maximum level checks
CREATE INDEX idx_stock_levels_product_id ON stock_levels(product_id);

-- Index on customers for search queries
CREATE INDEX idx_customers_code ON customers(code);
CREATE INDEX idx_customers_active ON customers(active);
CREATE INDEX idx_customers_name ON customers(first_name, last_name);

-- Index on products for search and filtering
CREATE INDEX idx_products_code ON products(code);
CREATE INDEX idx_products_active ON products(active);
CREATE INDEX idx_products_supplier_id ON products(supplier_id);

-- Index on suppliers
CREATE INDEX idx_suppliers_code ON suppliers(code);
CREATE INDEX idx_suppliers_active ON suppliers(active);

-- Index on close_cash for session management
CREATE INDEX idx_close_cash_reconciled ON close_cash(reconciled);
CREATE INDEX idx_close_cash_opened_at ON close_cash(opened_at);
CREATE INDEX idx_close_cash_closed_at ON close_cash(closed_at);

-- Index on coupons for validation queries
CREATE INDEX idx_coupons_code ON coupons(code);
CREATE INDEX idx_coupons_active ON coupons(active);
CREATE INDEX idx_coupons_valid_dates ON coupons(valid_from, valid_until);

-- Index on discounts for filtering
CREATE INDEX idx_discounts_active ON discounts(active);
CREATE INDEX idx_discounts_applicable_on ON discounts(applicable_on);
CREATE INDEX idx_discounts_valid_dates ON discounts(valid_from, valid_until);

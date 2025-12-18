-- Create coupons table
CREATE TABLE coupons (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255) NOT NULL,
    discount_type ENUM('PERCENTAGE', 'FIXED_AMOUNT') NOT NULL,
    discount_value DECIMAL(19, 2) NOT NULL,
    minimum_purchase_amount DECIMAL(19, 2),
    maximum_discount_amount DECIMAL(19, 2),
    valid_from DATETIME NOT NULL,
    valid_until DATETIME NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    max_usage_count INT,
    current_usage_count INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL,
    INDEX idx_code (code),
    INDEX idx_active_valid (active, valid_from, valid_until)
);

-- Create discounts table
CREATE TABLE discounts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    discount_type ENUM('PERCENTAGE', 'FIXED_AMOUNT') NOT NULL,
    discount_value DECIMAL(19, 2) NOT NULL,
    applicable_on ENUM('TOTAL', 'PRODUCT_CATEGORY', 'SPECIFIC_PRODUCT') NOT NULL,
    applicable_product_id BIGINT,
    minimum_purchase_amount DECIMAL(19, 2),
    maximum_discount_amount DECIMAL(19, 2),
    valid_from DATETIME NOT NULL,
    valid_until DATETIME NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    requires_customer BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL,
    INDEX idx_active_valid (active, valid_from, valid_until),
    INDEX idx_applicable_product (applicable_product_id)
);

-- Add discount and coupon columns to ticket_lines table
ALTER TABLE ticket_lines
    ADD COLUMN discount_amount DECIMAL(19, 2),
    ADD COLUMN discount_id BIGINT,
    ADD COLUMN coupon_id BIGINT,
    ADD CONSTRAINT fk_ticket_line_discount FOREIGN KEY (discount_id) REFERENCES discounts(id),
    ADD CONSTRAINT fk_ticket_line_coupon FOREIGN KEY (coupon_id) REFERENCES coupons(id);

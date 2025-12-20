-- Create Dashboard Statistics View
-- This view aggregates sales data to provide real-time dashboard metrics
-- Demonstrates: CREATE VIEW, CASE statements, aggregate functions, date functions

CREATE VIEW v_dashboard_stats AS
SELECT 
    DATE(t.created_at) as sale_date,
    t.close_cash_id as session_id,
    SUM(CASE WHEN t.type = 'SALE' AND t.status = 'COMPLETED' THEN t.total ELSE 0 END) as total_sales,
    SUM(CASE WHEN t.type = 'RETURN' AND t.status = 'COMPLETED' THEN t.total ELSE 0 END) as total_returns,
    SUM(CASE 
        WHEN t.type = 'SALE' AND t.status = 'COMPLETED' 
        THEN tl.quantity * (COALESCE(p.sale_price, tl.unit_price) - COALESCE(p.purchase_price, 0))
        WHEN t.type = 'RETURN' AND t.status = 'COMPLETED' 
        THEN -tl.quantity * (COALESCE(p.sale_price, tl.unit_price) - COALESCE(p.purchase_price, 0))
        ELSE 0 
    END) as net_profit,
    COUNT(DISTINCT t.id) as transaction_count
FROM tickets t
INNER JOIN ticket_lines tl ON t.id = tl.ticket_id
INNER JOIN products p ON tl.product_id = p.id
WHERE t.created_at >= DATE_SUB(NOW(), INTERVAL 30 DAY)
GROUP BY DATE(t.created_at), t.close_cash_id
ORDER BY sale_date DESC;

# SmartPOS Database Implementation

## Database Creation & Schema

The database schema is properly implemented using **Flyway migrations** with MySQL 8.0.

### Migration Files:
1. **V1__initial_schema.sql** - Base schema with all tables
2. **V2__update_stock_movement_and_close_cash.sql** - Stock movement enhancements
3. **V3__create_discount_coupon_tables.sql** - Discount and coupon system
4. **V4__create_performance_indexes.sql** - Performance optimization indexes

### Key Tables:
- `products`, `suppliers`, `customers` - Master data
- `tickets`, `ticket_lines` - Sales transactions
- `stock_movements`, `stock_current`, `stock_levels` - Inventory management
- `close_cash` - Cash session management
- `coupons`, `discounts` - Promotional features

## Query Functionality

### CRUD Operations
All entities support full CRUD operations through REST APIs:
- **Create**: POST /v1/{entity}
- **Read**: GET /v1/{entity}/{id}
- **Update**: PUT /v1/{entity}/{id}
- **Delete**: DELETE /v1/{entity}/{id}

### Complex Queries (ReportingService.java)

#### 1. Multi-Table JOIN #1: Sales Report
**File**: `ReportingService.java` - `getSalesReport()`
**Query Type**: 3-table JOIN (tickets, customers, close_cash, ticket_lines)
**Purpose**: Get sales with customer and cashier details

```sql
SELECT t.*, c.first_name, c.last_name, cc.cashier_name, COUNT(tl.id)
FROM tickets t
LEFT JOIN customers c ON t.customer_id = c.id
LEFT JOIN close_cash cc ON t.close_cash_id = cc.id
INNER JOIN ticket_lines tl ON t.id = tl.ticket_id
WHERE t.created_at BETWEEN ? AND ?
GROUP BY t.id
```

**Endpoint**: `GET /v1/reports/sales?startDate={date}&endDate={date}`

#### 2. Multi-Table JOIN #2: Product Sales Statistics
**File**: `ReportingService.java` - `getProductSalesStatistics()`
**Query Type**: 3-table JOIN with aggregation
**Purpose**: Analyze product performance

```sql
SELECT p.*, SUM(tl.quantity), SUM(tl.line_total), AVG(tl.unit_price), COUNT(DISTINCT t.id)
FROM products p
INNER JOIN ticket_lines tl ON p.id = tl.product_id
INNER JOIN tickets t ON tl.ticket_id = t.id
WHERE t.created_at BETWEEN ? AND ?
GROUP BY p.id
HAVING SUM(tl.quantity) > 0
```

**Endpoint**: `GET /v1/reports/product-stats?startDate={date}&endDate={date}`

#### 3. Nested Query (Subquery): Below Average Products
**File**: `ReportingService.java` - `getProductsBelowAverageSales()`
**Query Type**: Subquery in HAVING clause
**Purpose**: Find underperforming products

```sql
SELECT p.*, SUM(tl.line_total) as revenue
FROM products p
INNER JOIN ticket_lines tl ON p.id = tl.product_id
GROUP BY p.id
HAVING revenue < (
    SELECT AVG(product_revenue)
    FROM (
        SELECT SUM(tl2.line_total) as product_revenue
        FROM ticket_lines tl2
        GROUP BY tl2.product_id
    ) as revenue_per_product
)
```

**Endpoint**: `GET /v1/reports/products-below-average?startDate={date}&endDate={date}`

#### 4. Aggregate with GROUP BY: Customer Purchase Summary
**File**: `ReportingService.java` - `getCustomerPurchaseSummary()`
**Query Type**: Aggregation with GROUP BY, HAVING, COALESCE
**Purpose**: Analyze customer spending patterns

```sql
SELECT c.id, c.code, CONCAT(c.first_name, ' ', c.last_name),
       COUNT(DISTINCT t.id) as totalPurchases,
       COALESCE(SUM(t.total), 0) as totalSpent,
       COALESCE(AVG(t.total), 0) as averageTransactionValue
FROM customers c
LEFT JOIN tickets t ON c.id = t.customer_id
GROUP BY c.id
HAVING COUNT(DISTINCT t.id) > 0
ORDER BY totalSpent DESC
```

**Endpoint**: `GET /v1/reports/customer-summary?startDate={date}&endDate={date}`

#### 5. Set Operation (UNION): All Active Entities
**File**: `ReportingService.java` - `getAllActiveEntities()`
**Query Type**: UNION combining 3 tables
**Purpose**: Get consolidated list of active entities

```sql
SELECT CONCAT('CUSTOMER-', code, ': ', first_name, ' ', last_name)
FROM customers WHERE active = true
UNION
SELECT CONCAT('SUPPLIER-', code, ': ', name)
FROM suppliers WHERE active = true
UNION
SELECT CONCAT('PRODUCT-', code, ': ', name)
FROM products WHERE active = true
ORDER BY entity_name
```

**Endpoint**: `GET /v1/reports/active-entities`

#### 6. Nested Query with EXISTS: High-Value Customers
**File**: `ReportingService.java` - `getHighValueCustomers()`
**Query Type**: Correlated subquery with EXISTS
**Purpose**: Find customers with high-value transactions

```sql
SELECT c.*, COUNT(t.id), SUM(t.total)
FROM customers c
INNER JOIN tickets t ON c.id = t.customer_id
WHERE EXISTS (
    SELECT 1 FROM tickets t2
    WHERE t2.customer_id = c.id AND t2.total >= ?
)
GROUP BY c.id
```

**Endpoint**: `GET /v1/reports/high-value-customers?threshold={amount}`

## Transaction Management (ACID Properties)

### Transaction #1: Create Ticket
**File**: `TicketService.java` - `createTicket()`
**Type**: Multi-step transactional operation

**Steps**:
1. BEGIN TRANSACTION
2. Validate products exist
3. Create ticket record
4. Create ticket lines (multiple inserts)
5. Create stock movements (multiple inserts)
6. Update stock_current (multiple updates)
7. Calculate and save totals
8. COMMIT (or ROLLBACK on error)

**ACID Properties**:
- **Atomicity**: All steps succeed or all rollback
- **Consistency**: Stock quantities always match movements
- **Isolation**: Concurrent transactions don't interfere
- **Durability**: Committed data persists

**Annotation**: `@Transactional` at class level

### Transaction #2: Bulk Inventory Adjustment
**File**: `TicketService.java` - `performBulkInventoryAdjustment()`
**Type**: Complex multi-product adjustment

**Steps**:
1. BEGIN TRANSACTION
2. Validate all products exist
3. Create adjustment ticket
4. For each product:
   - Create ticket line
   - Create stock movement
   - Update stock_current
   - Validate no negative stock
5. Mark ticket completed
6. COMMIT (or ROLLBACK if any validation fails)

**Error Handling**:
```java
@Transactional(rollbackFor = Exception.class)
public TicketDTO performBulkInventoryAdjustment(...) {
    try {
        // Multi-step operations
        return result;
    } catch (Exception e) {
        // Automatic ROLLBACK
        throw new RuntimeException("Adjustment failed", e);
    }
}
```

**Endpoint**: `POST /v1/tickets/bulk-adjustment`
```json
{
  "adjustments": {
    "1": 10,
    "2": -5
  },
  "reason": "Physical inventory correction"
}
```

## Code Quality

### Organization
- **Layered Architecture**: Presentation → Application → Domain
- **Separation of Concerns**: Controllers, Services, Repositories
- **DTO Pattern**: Separate request/response objects

### Comments
- Comprehensive JavaDoc on all complex methods
- Inline comments explaining business logic
- SQL query documentation in service methods

### Error Handling
- Try-catch blocks in transactional methods
- Custom exceptions with meaningful messages
- Automatic rollback on errors
- Validation before database operations

## Performance Optimization

### Indexes (V4__create_performance_indexes.sql)

#### Single Column Indexes
- `idx_tickets_created_at` - Date range queries
- `idx_tickets_status` - Status filtering
- `idx_tickets_customer_id` - Customer lookups
- `idx_products_code` - Product searches
- `idx_customers_code` - Customer searches

#### Composite Indexes
- `idx_tickets_created_status` - Common query pattern (date + status)
- `idx_customers_name` - Full name searches (first_name, last_name)
- `idx_coupons_valid_dates` - Date range validation (valid_from, valid_until)

#### Foreign Key Indexes
- `idx_ticket_lines_product_id` - JOIN optimization
- `idx_ticket_lines_ticket_id` - JOIN optimization
- `idx_stock_movements_product_id` - Stock movement queries

### Query Optimization
- **Native SQL** for complex queries (bypasses ORM overhead)
- **Proper JOIN types**: INNER vs LEFT based on requirements
- **WHERE before GROUP BY**: Filter data early
- **HAVING for aggregates**: Post-aggregation filtering
- **Index-aware queries**: Use indexed columns in WHERE clauses

### Connection Pooling
- HikariCP configured in Spring Boot
- Connection reuse for better performance

## Testing the Implementation

### 1. Create Test Data
```bash
# Run backend
cd smartpos-backend
./mvnw spring-boot:run
```

### 2. Test CRUD Operations
```bash
# Create product
curl -X POST http://localhost:8080/v1/products -H "Content-Type: application/json" -d '{...}'

# Get product
curl http://localhost:8080/v1/products/1

# Update product
curl -X PUT http://localhost:8080/v1/products/1 -H "Content-Type: application/json" -d '{...}'

# Delete product
curl -X DELETE http://localhost:8080/v1/products/1
```

### 3. Test Complex Queries
```bash
# Sales report
curl "http://localhost:8080/v1/reports/sales?startDate=2025-01-01T00:00:00&endDate=2025-12-31T23:59:59"

# Product statistics
curl "http://localhost:8080/v1/reports/product-stats?startDate=2025-01-01T00:00:00&endDate=2025-12-31T23:59:59"

# Below average products
curl "http://localhost:8080/v1/reports/products-below-average?startDate=2025-01-01T00:00:00&endDate=2025-12-31T23:59:59"

# Active entities (UNION)
curl http://localhost:8080/v1/reports/active-entities

# Customer summary
curl "http://localhost:8080/v1/reports/customer-summary?startDate=2025-01-01T00:00:00&endDate=2025-12-31T23:59:59"

# High-value customers
curl "http://localhost:8080/v1/reports/high-value-customers?threshold=1000"
```

### 4. Test Transactions
```bash
# Bulk adjustment (will COMMIT if successful, ROLLBACK if any error)
curl -X POST http://localhost:8080/v1/tickets/bulk-adjustment \
  -H "Content-Type: application/json" \
  -d '{
    "adjustments": {"1": 10, "2": -5},
    "reason": "Physical inventory count"
  }'
```

## Database Best Practices Demonstrated

✅ **Normalization**: 3NF schema design
✅ **Foreign Keys**: Referential integrity constraints
✅ **Indexes**: Strategic indexing for performance
✅ **Transactions**: ACID-compliant operations
✅ **Query Optimization**: Efficient JOIN and aggregation
✅ **Error Handling**: Graceful failure and rollback
✅ **Code Documentation**: Comprehensive comments
✅ **Migration Management**: Version-controlled schema changes
✅ **Connection Pooling**: Efficient resource utilization
✅ **Prepared Statements**: SQL injection prevention

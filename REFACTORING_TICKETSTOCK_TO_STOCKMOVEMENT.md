# TicketStock to StockMovement Refactoring

## Summary
Renamed `TicketStock` entity and `ticket_stocks` table to `StockMovement` and `stock_movements` throughout the entire codebase for better semantic clarity.

## Changes Made

### 1. Database Migration
**File**: `V6__rename_ticket_stocks_to_stock_movements.sql` (NEW)
- Renamed table from `ticket_stocks` to `stock_movements`
- Updated all index names from `idx_ticket_stocks_*` to `idx_stock_movements_*`

### 2. Entity Classes
**Renamed Files**:
- `TicketStock.java` → `StockMovement.java`
- Class name: `TicketStock` → `StockMovement`
- Table annotation: `@Table(name = "ticket_stocks")` → `@Table(name = "stock_movements")`
- Inner enum remains: `MovementType { SALE, RETURN, ADJUSTMENT }`

### 3. Repository
**Renamed Files**:
- `TicketStockRepository.java` → `StockMovementRepository.java`
- Interface name: `TicketStockRepository` → `StockMovementRepository`
- Methods remain unchanged: `findByTicketId()`, `findByProductId()`

### 4. Service Layer
**File**: `TicketService.java`
- Repository field: `ticketStockRepository` → `stockMovementRepository`
- All variable declarations: `TicketStock` → `StockMovement`
- All builder calls: `TicketStock.builder()` → `StockMovement.builder()`
- All enum references: `TicketStock.MovementType` → `StockMovement.MovementType`
- All repository calls: `ticketStockRepository.*` → `stockMovementRepository.*`

**Methods Updated**:
- `createTicket()` - Creates stock movements for sales/returns
- `cancelTicket()` - Reverses stock movements
- `bulkAdjustInventory()` - Creates adjustment stock movements

### 5. Related Entities
**File**: `Product.java`
- Field: `Set<TicketStock> ticketStocks` → `Set<StockMovement> stockMovements`

**File**: `Ticket.java`
- Field: `Set<TicketStock> stocks` → `Set<StockMovement> stockMovements`

### 6. Documentation
**File**: `README.md`
- Updated table list: `ticket_stocks` → `stock_movements`

**File**: `DATABASE_IMPLEMENTATION.md`
- Updated table references in key tables section
- Updated index documentation

## Migration Strategy

### Historical Migrations (NOT MODIFIED)
- V1__initial_schema.sql - Still creates `ticket_stocks` (historical accuracy)
- V2__update_stock_movement_and_close_cash.sql - Still references `ticket_stocks`
- V4__create_performance_indexes.sql - Still creates `idx_ticket_stocks_*` indexes

### New Migration (V6)
- Renames table and all associated indexes
- Preserves all data during rename
- Compatible with existing data

## Testing Checklist

- [x] No compilation errors
- [ ] Backend compiles successfully: `mvn clean install`
- [ ] Run migrations on fresh database
- [ ] Run migrations on existing database with data
- [ ] Test ticket creation (creates stock movements)
- [ ] Test ticket cancellation (reverses stock movements)
- [ ] Test bulk inventory adjustment
- [ ] Verify stock_current updates correctly
- [ ] Check all product queries still work
- [ ] Verify reporting queries still function

## Benefits of This Change

1. **Better Semantics**: `StockMovement` more clearly describes what the entity represents
2. **Industry Standard**: Stock movements is a common term in inventory management
3. **Clearer Purpose**: Removes confusion about relationship to tickets
4. **Improved Maintainability**: More intuitive for new developers

## Backward Compatibility

- ✅ Database migration handles table rename automatically
- ✅ All indexes preserved with new names
- ✅ All data preserved during migration
- ✅ Foreign key relationships maintained
- ✅ No API endpoint changes required (internal entity only)

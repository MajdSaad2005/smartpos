# SmartPOS - Proper DDD Architecture Implementation

## Overview

The backend has been refactored to implement **true Domain-Driven Design (DDD)** with complete separation between domain logic and persistence concerns.

## Architecture Layers

```
┌─────────────────────────────────────────────┐
│        Presentation Layer                   │
│  (Controllers - REST Endpoints)             │
└──────────────────┬──────────────────────────┘
                   │
┌──────────────────▼──────────────────────────┐
│        Application Layer                    │
│  (Services - Use Cases & Business Logic)   │
│  Uses: Domain Models & Repositories         │
└──────────────────┬──────────────────────────┘
                   │
┌──────────────────▼──────────────────────────┐
│        Domain Layer                         │
│  (Pure Business Logic - NO Dependencies)   │
│  - Domain Models (ProductDomain, etc.)      │
│  - Repository Interfaces                    │
│  - Value Objects & Business Rules           │
│  - NO JPA, NO Spring, NO Framework          │
└──────────────────┬──────────────────────────┘
                   │
┌──────────────────▼──────────────────────────┐
│     Infrastructure Layer                    │
│  (Implementation Details)                   │
│  - JPA Entities (ProductJPA, etc.)         │
│  - Mappers (Domain ↔ JPA)                   │
│  - Repository Implementations               │
│  - Database Configuration                   │
│  - Spring Data Repositories                 │
└─────────────────────────────────────────────┘
```

## Directory Structure

```
src/main/java/com/smartpos/
├── domain/
│   ├── models/                          # Pure domain models (NO JPA)
│   │   ├── ProductDomain.java
│   │   ├── SupplierDomain.java
│   │   ├── CustomerDomain.java
│   │   ├── StockCurrentDomain.java
│   │   ├── StockLevelDomain.java
│   │   ├── TicketDomain.java
│   │   ├── TicketLineDomain.java
│   │   ├── TicketStockDomain.java
│   │   └── CloseCashDomain.java
│   └── repositories/                    # Repository interfaces
│       ├── ProductRepository.java
│       ├── SupplierRepository.java
│       ├── CustomerRepository.java
│       ├── StockCurrentRepository.java
│       ├── StockLevelRepository.java
│       ├── TicketRepository.java
│       ├── TicketLineRepository.java
│       ├── TicketStockRepository.java
│       └── CloseCashRepository.java
├── application/
│   ├── services/                        # Business logic & use cases
│   │   ├── ProductService.java
│   │   ├── SupplierService.java
│   │   ├── CustomerService.java
│   │   ├── StockLevelService.java
│   │   ├── TicketService.java
│   │   └── CloseCashService.java
│   └── dtos/                            # Data Transfer Objects
│       ├── ProductDTO.java
│       ├── SupplierDTO.java
│       └── ... (other DTOs)
├── presentation/
│   ├── controllers/                     # REST Controllers
│   │   ├── ProductController.java
│   │   ├── SupplierController.java
│   │   └── ... (other controllers)
│   └── exception_handlers/
│       └── GlobalExceptionHandler.java
└── infrastructure/
    └── persistence/
        ├── entities/                    # JPA Entities (DB mapping)
        │   ├── ProductJPA.java
        │   ├── SupplierJPA.java
        │   ├── CustomerJPA.java
        │   ├── StockCurrentJPA.java
        │   ├── StockLevelJPA.java
        │   ├── TicketJPA.java
        │   ├── TicketLineJPA.java
        │   ├── TicketStockJPA.java
        │   └── CloseCashJPA.java
        ├── mappers/                     # Domain ↔ JPA Converters
        │   ├── DomainMappers.java
        │   └── StockLevelMapper.java
        └── repositories/                # Repository implementations
            ├── ProductRepositoryImpl.java
            ├── SupplierRepositoryImpl.java
            └── ... (other implementations)
```

## Key Concepts

### 1. Domain Models (Pure Domain Logic)

**Location**: `domain/models/`

**Characteristics**:
- ✅ NO JPA annotations (`@Entity`, `@Table`, `@Column`, etc.)
- ✅ NO Spring annotations
- ✅ NO framework dependencies
- ✅ Contains business logic and validation
- ✅ Encapsulates domain rules

**Example - ProductDomain**:
```java
public class ProductDomain {
    private Long id;
    private String code;
    private BigDecimal salePrice;
    private BigDecimal purchasePrice;
    
    // Domain logic - not just getters/setters
    public BigDecimal calculateMargin() {
        // Business rule: margin calculation
    }
    
    public void updatePrices(BigDecimal purchase, BigDecimal sale) {
        // Business rule: validate prices
        validateProduct(code, name, purchase, sale);
        this.purchasePrice = purchase;
        this.salePrice = sale;
    }
}
```

### 2. JPA Entities (Persistence Mapping)

**Location**: `infrastructure/persistence/entities/`

**Characteristics**:
- ✅ Pure JPA mapping only
- ✅ Minimal business logic
- ✅ Maps to database tables
- ✅ Separated from domain logic

**Example - ProductJPA**:
```java
@Entity
@Table(name = "products")
public class ProductJPA {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String code;
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal purchasePrice;
    
    // Just mapping, no business logic
}
```

### 3. Mappers (Domain ↔ Persistence)

**Location**: `infrastructure/persistence/mappers/`

**Responsibility**:
- Convert JPA entities to domain models
- Convert domain models to JPA entities
- Handle transformations between layers

**Example**:
```java
public class DomainMappers {
    // JPA → Domain
    public static ProductDomain toDomain(ProductJPA jpa) {
        return new ProductDomain(jpa.getId(), jpa.getCode(), ...);
    }
    
    // Domain → JPA
    public static ProductJPA toProductJPA(ProductDomain domain) {
        return ProductJPA.builder()
            .id(domain.getId())
            .code(domain.getCode())
            .build();
    }
}
```

### 4. Repository Interfaces (Domain Layer)

**Location**: `domain/repositories/`

**Purpose**: Define persistence contracts for domain layer
```java
public interface ProductRepository {
    Optional<ProductDomain> findById(Long id);
    ProductDomain save(ProductDomain domain);
    // Domain works with domain models, not JPA entities
}
```

### 5. Repository Implementations (Infrastructure Layer)

**Location**: `infrastructure/persistence/repositories/`

**Purpose**: Implement repository with JPA details
```java
@Repository
public class ProductRepositoryImpl implements ProductRepository {
    private final ProductJPARepository jpaRepository;
    
    @Override
    public Optional<ProductDomain> findById(Long id) {
        return jpaRepository.findById(id)
            .map(DomainMappers::toDomain);  // Convert JPA → Domain
    }
    
    @Override
    public ProductDomain save(ProductDomain domain) {
        ProductJPA jpa = DomainMappers.toProductJPA(domain);  // Convert Domain → JPA
        ProductJPA saved = jpaRepository.save(jpa);
        return DomainMappers.toDomain(saved);
    }
}
```

## Data Flow Example: Creating a Product

```
1. Request comes to ProductController
   ↓
2. Controller calls ProductService.createProduct(CreateProductRequest)
   ↓
3. Service validates and creates ProductDomain (PURE domain logic)
   ↓
4. Service calls repository.save(productDomain)
   ↓
5. RepositoryImpl converts: ProductDomain → ProductJPA (via mapper)
   ↓
6. RepositoryImpl calls jpaRepository.save(productJPA)
   ↓
7. Database persists ProductJPA
   ↓
8. RepositoryImpl converts back: ProductJPA → ProductDomain
   ↓
9. Service returns domain model
   ↓
10. Controller converts: ProductDomain → ProductDTO (for API response)
    ↓
11. Response sent to client
```

## Benefits of This DDD Implementation

### 1. **Separation of Concerns**
- Domain logic is completely independent
- Persistence details don't leak into business logic
- Easy to understand what is business logic vs infrastructure

### 2. **Testability**
- Domain models can be tested WITHOUT database
- No need for integration tests for business logic
- Fast unit tests for domain rules

**Example**:
```java
@Test
public void testProductMarginCalculation() {
    ProductDomain product = new ProductDomain(
        1L, "P001", "Product", "Desc",
        new BigDecimal("100"), new BigDecimal("150"),
        new BigDecimal("10"), true, 1L);
    
    BigDecimal margin = product.calculateMargin();
    assertEquals(new BigDecimal("0.50"), margin);
}
// No database needed!
```

### 3. **Database Independence**
- Can switch from MySQL to PostgreSQL/MongoDB without changing domain
- Only infrastructure layer changes
- Domain logic remains the same

### 4. **Testability of Validations**
- All business rules are in domain models
- Easy to test all edge cases

**Example**:
```java
@Test(expected = IllegalArgumentException.class)
public void testStockValidation() {
    new StockCurrentDomain(1L, 1L, -5);  // Should throw
}
```

### 5. **Clear Business Intent**
- Code reads like business language
- Methods like `isBelowMinimum()`, `calculateReorderQuantity()`
- Easy for non-technical people to understand

### 6. **Easy to Extend**
- Add new business rules? Modify domain model
- Add new database? Extend repository implementation
- No ripple effects across layers

## Migration Path

If you want to update the existing services to use domain models:

1. **Update Repository Interfaces** (domain/repositories/)
   - Change return types from JPA entities to domain models

2. **Implement Repositories** (infrastructure/persistence/repositories/)
   - Use mappers to convert between layers

3. **Update Services** (application/services/)
   - Use domain models instead of JPA entities
   - Business logic stays the same

4. **Old Entities** (domain/entities/)
   - Keep for now if needed for Swagger/documentation
   - Eventually remove or deprecate

## Testing Examples

### Unit Test (No Database)
```java
@Test
public void testStockLevelValidation() {
    // Domain model validates business rules
    StockLevelDomain level = new StockLevelDomain(1L, 1L, 10, 100);
    
    assertTrue(level.isBelowMinimum(5));
    assertFalse(level.isBelowMinimum(15));
    assertEquals(80, level.calculateReorderQuantity(20));
}
```

### Integration Test (With Database)
```java
@SpringBootTest
public class ProductRepositoryIntegrationTest {
    @Autowired
    private ProductRepository repository;
    
    @Test
    public void testSaveAndRetrieve() {
        ProductDomain domain = new ProductDomain(...);
        ProductDomain saved = repository.save(domain);
        
        Optional<ProductDomain> retrieved = repository.findById(saved.getId());
        assertTrue(retrieved.isPresent());
    }
}
```

## Summary Table

| Aspect | Old (Mixed) | New (Pure DDD) |
|--------|------------|----------------|
| Domain Logic Location | JPA Entities | Domain Models |
| JPA Annotations | Everywhere | Only in Infrastructure |
| Database Dependency | Direct | Via Repository Interface |
| Testing Domain Logic | Requires DB | No DB needed |
| Code Organization | Scattered | Clear layers |
| Framework Independence | No | Yes |
| Business Logic Clarity | Mixed concerns | Pure business code |

## Next Steps

1. **Create Repository Implementations** for each domain model using the pattern shown
2. **Update Services** to use domain models instead of JPA entities
3. **Update Controllers** - no changes needed (they already use DTOs)
4. **Write Unit Tests** for domain models (fast, no DB)
5. **Keep Integration Tests** for repositories (with DB)

---

**This is true Domain-Driven Design where the domain layer is completely independent of persistence concerns!**

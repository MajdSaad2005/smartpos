# SmartPOS Backend

A robust Spring Boot-based Point of Sale Management System backend implementing Domain-Driven Design (DDD) architecture.

## Project Overview

SmartPOS Backend is a comprehensive REST API for managing all aspects of a retail point-of-sale system including:

- **Product Management**: Handle product catalog with pricing, tax, and supplier information
- **Supplier Management**: Manage supplier relationships and product sourcing
- **Stock Management**: Track current inventory levels and define stock reorder limits
- **Sales Processing**: Create and manage sales transactions with line items
- **Return Management**: Process customer returns and refunds
- **Customer Management**: Maintain customer database and purchase history
- **Cash Management**: Open/close cash registers and reconcile daily transactions

## Architecture

The project follows Domain-Driven Design (DDD) principles with clear separation of concerns:

```
src/main/java/com/smartpos/
├── domain/                    # Domain Layer (Business Logic)
│   ├── entities/             # JPA entities representing domain concepts
│   ├── repositories/         # Repository interfaces (contracts)
│   ├── services/             # Domain services
│   └── value_objects/        # Value objects (future)
├── application/              # Application Layer
│   ├── services/             # Use cases / application services
│   ├── dtos/                 # Data Transfer Objects
│   └── mappers/              # DTO mappers
├── infrastructure/           # Infrastructure Layer
│   ├── persistence/          # JPA repositories implementation
│   └── config/              # Spring configuration
└── presentation/             # Presentation Layer
    ├── controllers/          # REST API endpoints
    └── exception_handlers/   # Global exception handling
```

## Technology Stack

- **Framework**: Spring Boot 3.2.0
- **Language**: Java 17
- **Database**: MySQL 8.0+
- **Build Tool**: Maven 3.6+
- **ORM**: Spring Data JPA / Hibernate
- **Database Migration**: Flyway
- **API Documentation**: Springdoc OpenAPI (Swagger)
- **Mapping**: MapStruct
- **Lombok**: Reduce boilerplate code
- **Testing**: JUnit 5, REST Assured

## Prerequisites

- Java Development Kit (JDK) 17+
- Maven 3.6+
- MySQL 8.0+
- Git

## Setup Instructions

### 1. Clone and Navigate

```bash
cd smartpos-backend
```

### 2. Database Setup

Create a MySQL database:

```sql
CREATE DATABASE smartpos;
CREATE USER 'smartpos'@'localhost' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON smartpos.* TO 'smartpos'@'localhost';
FLUSH PRIVILEGES;
```

### 3. Configure Database Connection

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/smartpos?useSSL=false&serverTimezone=UTC
spring.datasource.username=smartpos
spring.datasource.password=password
```

### 4. Build and Run

```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

The API will be available at `http://localhost:8080/api`

Access Swagger UI at `http://localhost:8080/api/swagger-ui.html`

## API Endpoints

### Products
- `GET /v1/products` - List all products
- `GET /v1/products/{id}` - Get product by ID
- `GET /v1/products/code/{code}` - Get product by code
- `GET /v1/products/search?searchTerm=...` - Search products
- `POST /v1/products` - Create product
- `PUT /v1/products/{id}` - Update product
- `DELETE /v1/products/{id}` - Delete (deactivate) product

### Suppliers
- `GET /v1/suppliers` - List active suppliers
- `GET /v1/suppliers/all` - List all suppliers
- `GET /v1/suppliers/{id}` - Get supplier by ID
- `POST /v1/suppliers` - Create supplier
- `PUT /v1/suppliers/{id}` - Update supplier
- `DELETE /v1/suppliers/{id}` - Delete supplier

### Customers
- `GET /v1/customers` - List active customers
- `GET /v1/customers/{id}` - Get customer by ID
- `GET /v1/customers/search?searchTerm=...` - Search customers
- `POST /v1/customers` - Create customer
- `PUT /v1/customers/{id}` - Update customer
- `DELETE /v1/customers/{id}` - Delete customer

### Tickets (Sales/Returns)
- `GET /v1/tickets/{id}` - Get ticket by ID
- `GET /v1/tickets/number/{number}` - Get ticket by number
- `GET /v1/tickets/customer/{customerId}` - Get customer tickets
- `GET /v1/tickets/date-range?startDate=...&endDate=...` - Get tickets in date range
- `GET /v1/tickets/recent?limit=10` - Get recent tickets
- `POST /v1/tickets` - Create sale/return
- `DELETE /v1/tickets/{id}` - Cancel ticket

### Close Cash
- `POST /v1/close-cash/open` - Open cash register
- `POST /v1/close-cash/{id}/close` - Close register
- `GET /v1/close-cash/{id}` - Get cash session
- `GET /v1/close-cash/pending` - Get pending sessions
- `POST /v1/close-cash/{id}/reconcile` - Reconcile cash

### Stock Levels
- `GET /v1/stock-levels/product/{productId}` - Get stock levels for product
- `GET /v1/stock-levels/{id}` - Get stock level
- `POST /v1/stock-levels` - Create stock level
- `PUT /v1/stock-levels/{id}` - Update stock level
- `DELETE /v1/stock-levels/{id}` - Delete stock level

## Sample API Requests

### Create Product

```json
POST /v1/products
{
  "code": "PROD001",
  "name": "Laptop",
  "description": "High-performance laptop",
  "purchasePrice": 800.00,
  "salePrice": 1200.00,
  "taxPercentage": 10.00,
  "supplierId": 1
}
```

### Create Ticket (Sale)

```json
POST /v1/tickets
{
  "type": "SALE",
  "customerId": 1,
  "lines": [
    {
      "productId": 1,
      "quantity": 2
    },
    {
      "productId": 2,
      "quantity": 1
    }
  ]
}
```

## Database Schema

The Flyway migration creates the following main tables:

- **suppliers** - Supplier information
- **products** - Product catalog with prices and suppliers
- **stock_current** - Current inventory levels
- **stock_levels** - Minimum and maximum thresholds
- **customers** - Customer database
- **tickets** - Sales and return transactions
- **ticket_lines** - Individual items in transactions
- **stock_movements** - Stock movements (increases/decreases)
- **close_cash** - Cash register sessions

## Key Features

### Transaction Management
- Automatic stock updates when tickets are created
- Support for both sales and returns
- Proper calculation of subtotals, taxes, and totals

### Error Handling
- Global exception handler for consistent error responses
- Validation of all inputs
- Detailed error messages

### Logging
- Configured logging levels for debugging
- SQL query logging in development

### Security Considerations
- Input validation
- Proper HTTP status codes
- CORS configuration ready (can be added)

## Development Guidelines

### Code Style
- Follow Java conventions
- Use meaningful variable names
- Keep classes focused and single-responsibility

### Adding New Features

1. Define entities in `domain/entities/`
2. Create repository interfaces in `domain/repositories/`
3. Implement business logic in `application/services/`
4. Create DTOs in `application/dtos/`
5. Add REST endpoints in `presentation/controllers/`
6. Add database migrations in `src/main/resources/db/migration/`

## Testing

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=ProductControllerTest
```

## Troubleshooting

### Database Connection Issues
- Ensure MySQL is running
- Verify credentials in `application.properties`
- Check database exists and user has proper permissions

### Flyway Migration Failures
- Check SQL syntax in migration files
- Ensure database is writable
- Clear migrations history if needed: `DELETE FROM flyway_schema_history;`

### Port Already in Use
Change port in `application.properties`:
```properties
server.port=8081
```

## Performance Tips

- Index frequently queried fields (already done in migrations)
- Paginate large result sets (can be added)
- Use proper JPA fetch strategies (LAZY by default)
- Enable query caching for stable data

## Future Enhancements

- JWT authentication and authorization
- Advanced reporting and analytics
- Inventory forecasting
- Multi-location support
- Mobile app API
- Payment gateway integration
- Barcode scanning support
- Email receipts

## License

This project is licensed under the MIT License - see LICENSE file for details.

## Support

For issues, questions, or contributions, please contact the development team or create an issue in the repository.

---

**Version**: 1.0.0  
**Last Updated**: December 2025  
**Maintainer**: SmartPOS Development Team

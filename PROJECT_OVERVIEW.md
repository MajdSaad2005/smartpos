# SmartPOS - Complete Project Overview

## 📋 Project Structure

```
Database project/
├── smartpos-backend/              # Spring Boot REST API
│   ├── pom.xml                   # Maven configuration
│   ├── README.md                 # Backend documentation
│   ├── .gitignore
│   ├── src/main/java/com/smartpos/
│   │   ├── SmartPOSApplication.java
│   │   ├── domain/               # DDD Domain Layer
│   │   │   ├── entities/         # JPA Entities
│   │   │   ├── repositories/     # Repository interfaces
│   │   │   ├── services/         # Domain services
│   │   │   └── value_objects/    # Value objects
│   │   ├── application/          # Application Layer
│   │   │   ├── dtos/            # Data Transfer Objects
│   │   │   ├── services/        # Use cases
│   │   │   └── mappers/         # DTO Mappers
│   │   ├── infrastructure/       # Infrastructure Layer
│   │   │   ├── persistence/     # JPA Repositories
│   │   │   └── config/          # Spring Config
│   │   └── presentation/         # Presentation Layer
│   │       ├── controllers/      # REST Endpoints
│   │       └── exception_handlers/
│   └── src/main/resources/
│       ├── application.properties
│       └── db/migration/         # Flyway SQL migrations
│
└── smartpos-frontend/             # Next.js React App
    ├── package.json              # NPM dependencies
    ├── tsconfig.json             # TypeScript config
    ├── next.config.js            # Next.js config
    ├── tailwind.config.js        # Tailwind CSS
    ├── postcss.config.js         # PostCSS
    ├── README.md                 # Frontend documentation
    ├── .gitignore
    └── src/
        ├── pages/                # Next.js Pages
        │   ├── _app.tsx          # App wrapper
        │   ├── index.tsx          # Dashboard
        │   ├── sales.tsx          # POS Interface
        │   ├── products.tsx       # Product Management
        │   ├── customers.tsx      # Customer Management
        │   ├── suppliers.tsx      # Supplier Management
        │   └── settings.tsx       # Cash Register Management
        ├── components/            # React Components
        │   ├── layout/
        │   │   └── Layout.tsx     # Main layout with sidebar
        │   └── common/
        │       ├── FormElements.tsx  # Reusable UI components
        │       └── DataTable.tsx     # Data table component
        ├── lib/
        │   ├── axios.ts           # Axios instance
        │   ├── api.ts             # API service functions
        │   └── utils.ts           # Utility functions
        ├── hooks/                 # Custom React hooks
        ├── store/
        │   └── index.ts           # Zustand store
        ├── types/
        │   └── api.ts             # TypeScript type definitions
        └── globals.css            # Global styles
```

## 🚀 Quick Start

### Backend Setup

```bash
cd smartpos-backend

# Install dependencies (Maven)
mvn clean install

# Configure database in src/main/resources/application.properties
# Create MySQL database and user

# Run application
mvn spring-boot:run
```

API will be available at: `http://localhost:8080/api`

### Frontend Setup

```bash
cd smartpos-frontend

# Install dependencies
npm install

# Create .env.local file
echo "NEXT_PUBLIC_API_URL=http://localhost:8080/api" > .env.local

# Run development server
npm run dev
```

Frontend will be available at: `http://localhost:3000`

## 🏗️ Architecture Overview

### Backend - Domain-Driven Design (DDD)

The backend follows DDD principles with clear separation of concerns:

1. **Domain Layer** (`domain/`)
   - Core business entities and rules
   - Repository interfaces (abstractions)
   - Domain services for complex operations
   - No dependencies on frameworks

2. **Application Layer** (`application/`)
   - Use cases and application services
   - DTOs for data transfer
   - DTO mappers for entity-to-DTO conversion
   - Orchestrates domain layer

3. **Infrastructure Layer** (`infrastructure/`)
   - JPA repository implementations
   - Database configuration
   - Spring-specific configurations
   - Implements domain repository interfaces

4. **Presentation Layer** (`presentation/`)
   - REST API controllers
   - Global exception handler
   - Request/response handling

### Frontend - Modern Next.js

1. **Pages** - Next.js route pages
   - Dashboard with analytics
   - POS (Point of Sale) interface
   - Product, Customer, Supplier management
   - Cash register management

2. **Components** - Reusable React components
   - Layout components (Sidebar, Header, Content)
   - Form elements (Button, Input, Select, Alert, etc.)
   - Data table for displaying lists

3. **State Management** - Zustand
   - Global store for shared state
   - Minimal boilerplate

4. **API Integration** - Axios
   - Centralized API client
   - Service functions for each resource
   - Proper error handling

## 📊 Database Schema

### Key Entities

1. **Product** - Product catalog
   - Fields: code, name, purchasePrice, salePrice, taxPercentage, supplier
   - Relationships: belongs to Supplier, has StockCurrent, has many TicketLines

2. **Supplier** - Supplier information
   - Fields: code, name, email, phone, taxId, address
   - Relationships: has many Products

3. **StockCurrent** - Current inventory levels
   - Fields: product_id, quantity
   - Relationships: belongs to Product (1:1)

4. **StockLevel** - Stock reorder thresholds
   - Fields: product_id, minimumLevel, maximumLevel
   - Relationships: belongs to Product

5. **Customer** - Customer database
   - Fields: code, firstName, lastName, email, phone, taxId
   - Relationships: has many Tickets

6. **Ticket** - Sales/Return transactions
   - Fields: number, type, status, subtotal, taxAmount, total
   - Relationships: has Supplier, may have Customer, has many TicketLines, has many TicketStocks

7. **TicketLine** - Individual items in transaction
   - Fields: product_id, quantity, unitPrice, lineTotal, taxAmount
   - Relationships: belongs to Ticket and Product

8. **TicketStock** - Stock movements
   - Fields: product_id, quantity, type (INCREASE/DECREASE)
   - Relationships: belongs to Ticket and Product

9. **CloseCash** - Cash register sessions
   - Fields: openedAt, closedAt, totalSales, totalReturns, reconciled
   - Relationships: has many Tickets

## 🔄 Key Features

### Sales Processing
1. User selects products and quantities
2. System calculates subtotal and taxes
3. Optional customer selection
4. Stock is automatically decreased
5. Receipt/ticket is generated

### Return Processing
1. Similar to sales but with RETURN type
2. Stock is automatically increased
3. Reduces daily sales amount

### Cash Management
1. Open/close cash register sessions
2. Track all transactions in session
3. Calculate session totals
4. Reconcile cash at end of shift

### Inventory Management
1. Track current stock levels
2. Define minimum and maximum thresholds
3. Monitor stock movements
4. Easy product search and filtering

## 🎨 Frontend Features

- **Responsive Design**: Works on mobile, tablet, desktop
- **Modern UI**: Clean, intuitive interface with Tailwind CSS
- **Real-time Forms**: Fast, validated forms with React Hook Form
- **Data Tables**: Searchable, sortable tables for listings
- **Notifications**: Toast notifications for user feedback
- **Analytics Dashboard**: Sales charts and key metrics
- **Shopping Cart**: Intuitive cart for POS transactions

## 🔧 Configuration

### Backend (application.properties)

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/smartpos
spring.datasource.username=smartpos
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=validate
server.port=8080
server.servlet.context-path=/api
```

### Frontend (.env.local)

```
NEXT_PUBLIC_API_URL=http://localhost:8080/api
```

## 📝 API Examples

### Create Product

```bash
curl -X POST http://localhost:8080/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{
    "code": "PROD001",
    "name": "Laptop",
    "purchasePrice": 800,
    "salePrice": 1200,
    "taxPercentage": 10,
    "supplierId": 1
  }'
```

### Create Sale

```bash
curl -X POST http://localhost:8080/api/v1/tickets \
  -H "Content-Type: application/json" \
  -d '{
    "type": "SALE",
    "customerId": 1,
    "lines": [
      {"productId": 1, "quantity": 2},
      {"productId": 2, "quantity": 1}
    ]
  }'
```

## 🧪 Testing

### Backend

```bash
# Run all tests
mvn test

# Run specific test
mvn test -Dtest=ProductServiceTest

# Run with coverage
mvn test jacoco:report
```

### Frontend

```bash
# Run tests (when test files are added)
npm test

# Build for production
npm run build

# Start production server
npm start
```

## 📈 Performance Considerations

1. **Database Indexes** - Already created on frequently queried fields
2. **Lazy Loading** - JPA relationships use lazy loading by default
3. **DTO Mapping** - Reduces data transfer size
4. **Frontend Caching** - React component memoization
5. **API Caching** - Can be added for frequently accessed data

## 🔒 Security

Current implementation includes:

- Input validation on forms
- Proper HTTP status codes
- Global exception handling
- SQL injection protection (via JPA/Hibernate)

Future enhancements:

- JWT authentication
- Role-based access control
- CORS configuration
- Rate limiting
- API key authentication

## 🐛 Troubleshooting

### Backend won't start
- Check MySQL is running
- Verify database and credentials
- Check logs for error details

### Frontend can't connect to API
- Ensure backend is running on port 8080
- Check NEXT_PUBLIC_API_URL environment variable
- Check browser console for CORS errors

### Data not persisting
- Check database connection
- Verify Flyway migrations ran successfully
- Check database for tables

## 📚 Additional Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Next.js Documentation](https://nextjs.org/docs)
- [DDD Patterns](https://martinfowler.com/bliki/DomainDrivenDesign.html)
- [Tailwind CSS](https://tailwindcss.com)
- [React Hook Form](https://react-hook-form.com)

## 🤝 Contributing

Guidelines for contribution:

1. Follow the existing code structure
2. Use meaningful commit messages
3. Test your changes
4. Document new features
5. Ensure code quality

## 📄 License

MIT License - See LICENSE files in each project

## 👥 Team

SmartPOS Development Team

---

**Last Updated**: December 2025  
**Version**: 1.0.0

# SmartPOS - Setup & Deployment Guide

## Table of Contents
1. [System Requirements](#system-requirements)
2. [Local Development Setup](#local-development-setup)
3. [Production Deployment](#production-deployment)
4. [Database Setup](#database-setup)
5. [Configuration](#configuration)
6. [Troubleshooting](#troubleshooting)

## System Requirements

### Backend
- **Java**: JDK 17 or higher
- **Maven**: 3.6.0 or higher
- **MySQL**: 8.0 or higher
- **RAM**: Minimum 2GB
- **Disk Space**: Minimum 1GB

### Frontend
- **Node.js**: 18.0.0 or higher
- **NPM**: 9.0.0 or higher
- **RAM**: Minimum 1GB
- **Disk Space**: Minimum 500MB

### Development Machine
- **OS**: Windows, macOS, or Linux
- **Total RAM**: 4GB minimum (8GB recommended)
- **Internet**: Required for downloading dependencies

## Local Development Setup

### Prerequisites Installation

#### On Windows

1. **Java (JDK 17)**
   ```bash
   # Using Chocolatey
   choco install openjdk17
   
   # Or download from https://adoptium.net/
   # Add JAVA_HOME to environment variables
   ```

2. **Maven**
   ```bash
   choco install maven
   ```

3. **MySQL**
   ```bash
   choco install mysql
   
   # During installation, set root password
   # Add MySQL to system PATH
   ```

4. **Node.js**
   ```bash
   choco install nodejs
   ```

#### On macOS

```bash
# Install Homebrew if not present
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# Install Java
brew install openjdk@17
echo 'export PATH="/usr/local/opt/openjdk@17/bin:$PATH"' >> ~/.zshrc

# Install Maven
brew install maven

# Install MySQL
brew install mysql
brew services start mysql

# Install Node.js
brew install node
```

#### On Linux (Ubuntu/Debian)

```bash
# Update system
sudo apt update && sudo apt upgrade -y

# Install Java
sudo apt install openjdk-17-jdk-headless

# Install Maven
sudo apt install maven

# Install MySQL
sudo apt install mysql-server

# Install Node.js
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
sudo apt install nodejs
```

### Step 1: Backend Setup

```bash
# Navigate to backend directory
cd smartpos-backend

# Create MySQL database
mysql -u root -p
```

```sql
CREATE DATABASE smartpos CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'smartpos_user'@'localhost' IDENTIFIED BY 'smartpos_password_123';
GRANT ALL PRIVILEGES ON smartpos.* TO 'smartpos_user'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

### Step 2: Configure Backend

Edit `smartpos-backend/src/main/resources/application.properties`:

```properties
# Server Configuration
server.port=8080
server.servlet.context-path=/api

# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/smartpos?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=smartpos_user
spring.datasource.password=smartpos_password_123
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate Configuration
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.jpa.properties.hibernate.format_sql=true

# Flyway Configuration
spring.flyway.locations=classpath:db/migration
spring.flyway.baseline-on-migrate=true

# Logging
logging.level.root=INFO
logging.level.com.smartpos=DEBUG
```

### Step 3: Build and Run Backend

```bash
# Navigate to backend
cd smartpos-backend

# Build project
mvn clean install

# Run the application
mvn spring-boot:run

# Alternative: Build and run JAR
mvn clean package
java -jar target/smartpos-backend-1.0.0.jar
```

Backend should be running at: `http://localhost:8080/api`

Check health: `http://localhost:8080/api/swagger-ui.html`

### Step 4: Frontend Setup

```bash
# Navigate to frontend directory
cd smartpos-frontend

# Install dependencies
npm install

# Create environment file
echo "NEXT_PUBLIC_API_URL=http://localhost:8080/api" > .env.local

# Run development server
npm run dev
```

Frontend should be running at: `http://localhost:3000`

### Step 5: Verify Installation

1. Open `http://localhost:3000` in your browser
2. You should see the SmartPOS dashboard
3. Try navigating to different pages
4. Check browser console for any errors
5. Check backend logs for any issues

## Database Setup

### Initial Database Creation

The database is automatically created by Flyway during first run. All tables will be set up based on migration files in `src/main/resources/db/migration/`.

### Manual Database Verification

```sql
-- Connect to MySQL
mysql -u smartpos_user -p smartpos

-- List all tables
SHOW TABLES;

-- Check table structure
DESCRIBE products;
DESCRIBE tickets;

-- Check some initial data
SELECT * FROM suppliers LIMIT 5;
```

### Backup Database

```bash
# Create backup
mysqldump -u smartpos_user -p smartpos > smartpos_backup.sql

# Restore from backup
mysql -u smartpos_user -p smartpos < smartpos_backup.sql
```

## Configuration

### Backend Configuration Files

#### application.properties (Development)

```properties
spring.application.name=smartpos-backend
server.port=8080
spring.jpa.hibernate.ddl-auto=validate
```

#### application-prod.properties (Production)

```properties
spring.application.name=smartpos-backend
server.port=8080
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
logging.level.root=WARN
logging.level.com.smartpos=INFO
```

To use: `java -jar smartpos-backend-1.0.0.jar --spring.profiles.active=prod`

### Frontend Environment Variables

`.env.local` (Development):
```
NEXT_PUBLIC_API_URL=http://localhost:8080/api
```

`.env.production` (Production):
```
NEXT_PUBLIC_API_URL=https://api.yourdomain.com/api
```

## Production Deployment

### Backend Deployment (Using Docker)

Create `Dockerfile` in backend root:

```dockerfile
FROM openjdk:17-slim

WORKDIR /app

COPY target/smartpos-backend-1.0.0.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]
```

Build and run:

```bash
# Build Docker image
docker build -t smartpos-backend:1.0.0 .

# Run container
docker run -d \
  --name smartpos-backend \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/smartpos \
  -e SPRING_DATASOURCE_USERNAME=smartpos_user \
  -e SPRING_DATASOURCE_PASSWORD=smartpos_password \
  --link mysql:mysql \
  smartpos-backend:1.0.0
```

### Frontend Deployment (Vercel)

1. Push frontend to GitHub
2. Connect repository to Vercel
3. Set environment variable: `NEXT_PUBLIC_API_URL`
4. Deploy

```bash
# Or deploy manually
npm run build
npm run start
```

### Database for Production

```bash
# Use managed database service (AWS RDS, Google Cloud SQL, etc.)
# Or set up MySQL on separate server

# Enable SSL connections
# Configure proper backups
# Set up replication for high availability
```

## Performance Optimization

### Backend

```properties
# Connection pooling
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5

# Query optimization
spring.jpa.properties.hibernate.query.in_clause_parameter_padding=true

# Caching (add spring-boot-starter-cache)
spring.cache.type=simple
```

### Frontend

```bash
# Build optimization
npm run build

# Analyze bundle size
npm install --save-dev webpack-bundle-analyzer
```

## Monitoring

### Backend Monitoring

Add Spring Boot Actuator:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

Access metrics: `http://localhost:8080/api/actuator`

### Frontend Monitoring

- Use Vercel Analytics (if deployed on Vercel)
- Set up Google Analytics
- Monitor with Sentry for error tracking

## Troubleshooting

### Backend Issues

**Port Already in Use**
```bash
# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Linux/Mac
lsof -i :8080
kill -9 <PID>
```

**Database Connection Failed**
- Verify MySQL is running
- Check username/password
- Verify database exists
- Check firewall rules

**Flyway Migration Failed**
- Check SQL syntax in migration files
- Clear migration history if needed:
  ```sql
  DELETE FROM flyway_schema_history;
  ```
- Ensure database is writable

### Frontend Issues

**API Connection Failed**
- Check if backend is running
- Verify NEXT_PUBLIC_API_URL
- Check browser console for CORS errors
- Verify firewall rules

**Build Failures**
```bash
# Clear cache
rm -rf .next node_modules
npm install
npm run build
```

### Database Issues

**Check MySQL Status**
```bash
# Windows
net start MySQL80

# Linux
sudo systemctl status mysql

# Mac
brew services list
```

**Reset Database**
```bash
mysql -u root -p
DROP DATABASE smartpos;
CREATE DATABASE smartpos;
-- Restart backend to run migrations
```

## Backup & Recovery

### Regular Backups

```bash
# Daily backup script (add to cron/scheduler)
#!/bin/bash
BACKUP_DIR="/backups/smartpos"
DATE=$(date +%Y%m%d_%H%M%S)
mysqldump -u smartpos_user -p$PASSWORD smartpos > $BACKUP_DIR/backup_$DATE.sql
# Upload to cloud storage
```

### Recovery Procedure

```bash
# Restore from backup
mysql -u smartpos_user -p smartpos < backup_20251213_100000.sql

# Restart backend
# Verify data integrity
```

## Support & Documentation

- **Backend API Docs**: `http://localhost:8080/api/swagger-ui.html`
- **Frontend README**: `smartpos-frontend/README.md`
- **Backend README**: `smartpos-backend/README.md`
- **Project Overview**: `PROJECT_OVERVIEW.md`

## Next Steps

1. Set up version control (Git)
2. Configure CI/CD pipeline
3. Set up error tracking (Sentry)
4. Configure monitoring and alerts
5. Plan backup strategy
6. Set up load balancing for production
7. Configure SSL/TLS certificates
8. Implement authentication (JWT)

---

**Last Updated**: December 2025  
**Version**: 1.0.0

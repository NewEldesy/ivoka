# IVOKA Backend Setup & Deployment Guide

## Quick Start

### Prerequisites
- Java 11+
- Maven 3.6+
- MySQL 5.7+ or MariaDB

### Step 1: Database Setup
```bash
# Create database and tables
mysql -u root -p < backend/src/main/resources/schema.sql

# Verify connection
mysql -h localhost -u ivoka_user -p'ivoka_password' -D ivoka_db -e "SELECT COUNT(*) FROM products;"
```

### Step 2: Build Backend
```bash
cd backend
mvn clean package -DskipTests
```

### Step 3: Deploy & Run
```bash
# Option A: Local Tomcat server
mvn tomcat7:run

# Option B: Deploy WAR to existing Tomcat
cp target/ivoka-api.war /path/to/tomcat/webapps/

# Then access at http://localhost:8080/api/
```

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/auth` | Authentication (register, login, logout) |
| `GET` | `/api/products` | List all products |
| `GET` | `/api/cart` | Get user/guest cart |
| `POST` | `/api/cart` | Manage cart (add, remove, update, clear) |
| `POST` | `/api/messages` | Submit contact form |
| `GET/POST/PUT/DELETE` | `/api/users` | User management |

## Configuration

### Environment Variables (Production)
Override defaults by setting environment variables:
```bash
export DB_URL=jdbc:mysql://prod-db.example.com:3306/ivoka_db?useSSL=true&serverTimezone=UTC
export DB_USER=prod_user
export DB_PASSWORD=your-secure-password
export ALLOWED_ORIGIN=https://www.example.com
```

### Local Development
Edit `backend/src/main/resources/database.properties`:
```properties
db.url=jdbc:mysql://localhost:3306/ivoka_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
db.user=ivoka_user
db.password=ivoka_password
db.driver=com.mysql.cj.jdbc.Driver
```

## Database Schema

### Core Tables
- **users** — User accounts with password hashing
- **user_sessions** — Active session tokens
- **products** — Product catalog
- **carts** — Shopping carts (authenticated & guest)
- **cart_items** — Cart contents
- **messages** — Contact form submissions

## Key Features Implemented

### ✅ Authentication
- Secure password hashing (PBKDF2 + 100k iterations)
- Session token generation and validation
- User roles (customer, admin)
- 24-hour session expiry

### ✅ Shopping Cart
- Support for authenticated users
- Guest cart via session ID
- Item quantity management
- Cart persistence

### ✅ Product Management
- Browse all products
- Filter by category
- Availability status
- Timestamps (created_at, updated_at)

### ✅ Security
- Password hashing with salt
- Session token validation
- Prepared statements (SQL injection prevention)
- CORS headers (configurable)

## Troubleshooting

### Issue: `Access denied for user 'ivoka_user'@'localhost'`
**Solution:** Ensure the user exists and password matches:
```bash
mysql -u root -p -e "CREATE USER 'ivoka_user'@'localhost' IDENTIFIED BY 'ivoka_password';"
mysql -u root -p -e "GRANT ALL PRIVILEGES ON ivoka_db.* TO 'ivoka_user'@'localhost';"
mysql -u root -p -e "FLUSH PRIVILEGES;"
```

### Issue: `Table 'ivoka_db.users' doesn't have a column named 'password_hash'`
**Solution:** Re-run the schema initialization:
```bash
mysql -u root -p ivoka_db < backend/src/main/resources/schema.sql
```

### Issue: `Compilation error: package com.ivoka.api.dao does not exist`
**Solution:** Ensure all DAOs are present. CartDAO was recently added:
```bash
# File should exist at:
backend/src/main/java/com/ivoka/api/dao/CartDAO.java
```

## Testing with cURL

### Register User
```bash
curl -X POST http://localhost:8080/api/auth \
  -H "Content-Type: application/json" \
  -d '{
    "action": "register",
    "firstName": "Jean",
    "lastName": "Dupont",
    "email": "jean@example.com",
    "password": "Secure@123",
    "newsletter": true
  }'
```

### Login
```bash
curl -X POST http://localhost:8080/api/auth \
  -H "Content-Type: application/json" \
  -d '{
    "action": "login",
    "email": "jean@example.com",
    "password": "Secure@123"
  }'
```

### Get Products
```bash
curl -X GET http://localhost:8080/api/products
```

### Add to Cart
```bash
curl -X POST http://localhost:8080/api/cart \
  -H "Content-Type: application/json" \
  -d '{
    "action": "add",
    "productId": 1,
    "quantity": 2,
    "guestSessionId": "guest-session-id"
  }'
```

## Recent Changes & Fixes

### ✅ CartDAO Implementation
- Added missing `CartDAO.java` class (was causing compilation errors)
- Implements full cart lifecycle management
- Supports both authenticated users and guests

### ✅ Database Schema Enhancement
- Added `password_hash`, `role`, `updated_at` to users table
- Created `user_sessions` table for session management
- Created `carts` and `cart_items` tables for shopping cart
- Fixed `messages.read` → `messages.read_flag` (SQL keyword conflict)

## Architecture

```
backend/
├── pom.xml                    # Maven dependencies
├── src/main/
│   ├── java/com/ivoka/api/
│   │   ├── dao/              # Database access layer
│   │   ├── models/           # Data models (POJO)
│   │   ├── servlets/         # HTTP endpoints
│   │   ├── filters/          # Request filters (CORS, etc)
│   │   ├── listener/         # App lifecycle listeners
│   │   └── utils/            # Utilities (password, sessions)
│   └── resources/
│       ├── schema.sql        # Database schema
│       └── database.properties  # DB connection config
└── target/
    └── ivoka-api.war         # Built WAR package
```

## Next Steps

1. **Frontend Integration:** Configure CORS origin and session token exchange
2. **Production Deployment:** Move to environment variables, enable HTTPS
3. **Monitoring:** Add logging (SLF4J + Logback)
4. **Testing:** Add unit tests for DAOs and servlets
5. **CI/CD:** Set up automated builds and deployment

---

**Last Updated:** November 2025
**Status:** Ready for development & testing

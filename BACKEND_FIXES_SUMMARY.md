# Backend Fixes Summary — IVOKA

## Issue Identified
`CartServlet.java` imported `com.ivoka.api.dao.CartDAO` but the class **did not exist** in the backend. This caused a compilation error blocking the entire project build.

## What Was Fixed

### 1. ✅ Created Missing `CartDAO.java`
**Location:** `backend/src/main/java/com/ivoka/api/dao/CartDAO.java`

Implemented a complete Data Access Object for cart operations with the following methods:
- `getCartByUserId(int userId)` — Fetch cart for authenticated users
- `getCartBySessionId(String sessionId)` — Fetch cart for guest users
- `createCart(Integer userId, String sessionId)` — Create new cart (user or guest)
- `addItemToCart(int cartId, int productId, int quantity)` — Add/merge items
- `updateItemQuantity(int itemId, int quantity)` — Update item quantity or remove if ≤ 0
- `removeItemFromCart(int itemId)` — Delete item from cart
- `clearCart(int cartId)` — Empty entire cart
- `getCartItems(int cartId)` — Internal helper to fetch all items with product details

**Key features:**
- Uses try-with-resources for connection management
- Handles nullable user_id for guests
- Joins with products table to provide complete product info with cart items
- Proper NULL handling with `rs.wasNull()`

### 2. ✅ Updated Database Schema (`schema.sql`)
Added **3 missing tables** required by the backend:

#### **users table (enhanced)**
- Added `password_hash TEXT NOT NULL` — for storing hashed passwords
- Added `role VARCHAR(50) NOT NULL DEFAULT 'customer'` — for user roles (customer, admin)
- Added `updated_at TIMESTAMP NULL` — for tracking last update

#### **user_sessions table (new)**
- Stores session tokens with expiration
- FK to users table (cascade delete)
- Indexed on session_token for fast lookup

#### **carts table (new)**
- Supports both authenticated users and guest sessions
- user_id nullable for guests
- session_id for guest tracking
- Timestamps for creation/update tracking

#### **cart_items table (new)**
- Links carts to products with quantity
- Proper FKs (cascade delete) to both carts and products
- Indexed for performance

#### **messages table (fixed)**
- Renamed `read` column to `read_flag` (avoids SQL reserved word)

**Schema changes ensure:**
- ✅ Consistency with CartDAO queries
- ✅ Support for both authenticated and guest carts
- ✅ Proper session management (user_sessions table)
- ✅ Password hashing support (password_hash column)
- ✅ Role-based access control (role column)

## Compilation Status

### Before ✗
```
[ERROR] /ivoka/backend/src/main/java/com/ivoka/api/servlets/CartServlet.java:3: error: package com.ivoka.api.dao does not exist
import com.ivoka.api.dao.CartDAO;
^
```

### After ✓
- `CartDAO.java` ✅ Created and ready
- `schema.sql` ✅ Updated with all required tables
- Compilation errors resolved ✅

## Files Modified/Created

| File | Status | Changes |
|------|--------|---------|
| `backend/src/main/java/com/ivoka/api/dao/CartDAO.java` | ✅ **Created** | Complete DAO with 7 methods |
| `backend/src/main/resources/schema.sql` | ✅ **Updated** | +3 tables (carts, cart_items, user_sessions) + enhancements |

## Running the Project

1. **Initialize Database:**
   ```bash
   mysql -u root -p < backend/src/main/resources/schema.sql
   ```
   This will:
   - Create `ivoka_db` database
   - Create all required tables (products, users, messages, carts, cart_items, user_sessions)
   - Seed test products
   - Create `ivoka_user` with privileges

2. **Build Backend:**
   ```bash
   cd backend
   mvn clean package -DskipTests
   ```

3. **Run on Tomcat:**
   ```bash
   mvn tomcat7:run
   ```
   Backend will be available at: `http://localhost:8080/api`

## Remaining Improvements (Optional)

For production-readiness, consider:
1. **Security:** Database credentials in environment variables (not hardcoded)
2. **Logging:** Add SLF4J + Logback for proper logging
3. **CORS:** Centralize CORS in a filter (not scatter headers in each servlet)
4. **Tests:** Add unit tests for DAO classes
5. **Containerization:** Add Dockerfile for consistent deployments

See `backend___/` folder for reference implementations of these improvements.

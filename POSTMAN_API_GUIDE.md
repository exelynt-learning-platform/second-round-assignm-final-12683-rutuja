# E-Commerce API - Postman Guide

## Base URL
```
http://localhost:8080/api
```

## Authentication
- Most endpoints require JWT authentication in the `Authorization` header:
  ```
  Authorization: Bearer {accessToken}
  ```
- The accessToken is obtained after login/register and expires after 24 hours
- Refresh token can be used to get a new access token (expires after 7 days)

---

## 1. AUTHENTICATION ENDPOINTS (No Auth Required)

### 1.1 Register User
- **Method:** POST
- **URL:** `http://localhost:8080/api/auth/register`
- **Auth Required:** No
- **Request Body:**
```json
{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "Password123!",
  "firstName": "John",
  "lastName": "Doe",
  "phoneNumber": "+1-555-123-4567"
}
```
- **Response (201 Created):**
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "id": 1,
    "username": "john_doe",
    "email": "john@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 86400000
  }
}
```

---

### 1.2 Login User
- **Method:** POST
- **URL:** `http://localhost:8080/api/auth/login`
- **Auth Required:** No
- **Request Body:**
```json
{
  "email": "john@example.com",
  "password": "Password123!"
}
```
- **Response (200 OK):**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "id": 1,
    "username": "john_doe",
    "email": "john@example.com",
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 86400000
  }
}
```

---

### 1.3 Refresh Token
- **Method:** POST
- **URL:** `http://localhost:8080/api/auth/refresh`
- **Auth Required:** No
- **Headers:**
```
Refresh-Token: {refreshToken}
```
- **Response (200 OK):**
```json
{
  "success": true,
  "message": "Token refreshed",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 86400000
  }
}
```

---

## 2. USER ENDPOINTS (Auth Required)

### 2.1 Get Current User Profile
- **Method:** GET
- **URL:** `http://localhost:8080/api/users/me`
- **Auth Required:** Yes
- **Headers:**
```
Authorization: Bearer {accessToken}
```
- **Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "username": "john_doe",
    "email": "john@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "phoneNumber": "+1-555-123-4567",
    "address": "123 Main St",
    "role": "ROLE_USER",
    "enabled": true,
    "createdAt": "2026-04-01T10:30:00",
    "updatedAt": "2026-04-01T10:30:00"
  }
}
```

---

### 2.2 Update User Profile
- **Method:** PUT
- **URL:** `http://localhost:8080/api/users/me`
- **Auth Required:** Yes
- **Headers:**
```
Authorization: Bearer {accessToken}
```
- **Request Body (all fields optional):**
```json
{
  "firstName": "Johnny",
  "lastName": "Doe",
  "phoneNumber": "+1-555-999-8888",
  "address": "456 Oak Avenue"
}
```
- **Response (200 OK):**
```json
{
  "success": true,
  "message": "Profile updated successfully",
  "data": {
    "id": 1,
    "username": "john_doe",
    "email": "john@example.com",
    "firstName": "Johnny",
    "lastName": "Doe",
    "phoneNumber": "+1-555-999-8888",
    "address": "456 Oak Avenue",
    "role": "ROLE_USER",
    "enabled": true,
    "updatedAt": "2026-04-01T11:45:00"
  }
}
```

---

### 2.3 Change Password
- **Method:** PUT
- **URL:** `http://localhost:8080/api/users/me/password`
- **Auth Required:** Yes
- **Headers:**
```
Authorization: Bearer {accessToken}
```
- **Request Body:**
```json
{
  "oldPassword": "Password123!",
  "newPassword": "NewPassword456!"
}
```
- **Response (200 OK):**
```json
{
  "success": true,
  "message": "Password changed successfully",
  "data": null
}
```

---

## 3. PRODUCT ENDPOINTS

### 3.1 Get All Products (Public)
- **Method:** GET
- **URL:** `http://localhost:8080/api/products`
- **Auth Required:** No
- **Query Parameters:**
  - `page` (default: 0) - Page number (0-indexed)
  - `size` (default: 10) - Items per page
  - `sortBy` (default: "createdAt") - Sort field
  - `sortDir` (default: "desc") - Sort direction (asc/desc)
- **Example URL:**
```
http://localhost:8080/api/products?page=0&size=10&sortBy=price&sortDir=asc
```
- **Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 1,
        "name": "Laptop",
        "description": "High-performance laptop",
        "price": 999.99,
        "stockQuantity": 50,
        "imageUrl": "https://...",
        "category": "Electronics",
        "createdAt": "2026-04-01T10:00:00"
      }
    ],
    "page": 0,
    "size": 10,
    "totalElements": 100,
    "totalPages": 10,
    "last": false
  }
}
```

---

### 3.2 Search Products (Public)
- **Method:** GET
- **URL:** `http://localhost:8080/api/products/search`
- **Auth Required:** No
- **Query Parameters:**
  - `keyword` (optional) - Search in name/description
  - `category` (optional) - Filter by category
  - `page` (default: 0)
  - `size` (default: 10)
- **Example URL:**
```
http://localhost:8080/api/products/search?keyword=laptop&category=Electronics&page=0&size=10
```
- **Response (200 OK):** Same as Get All Products

---

### 3.3 Get Product by ID (Public)
- **Method:** GET
- **URL:** `http://localhost:8080/api/products/{id}`
- **Auth Required:** No
- **Example URL:**
```
http://localhost:8080/api/products/1
```
- **Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "name": "Laptop",
    "description": "High-performance laptop",
    "price": 999.99,
    "stockQuantity": 50,
    "imageUrl": "https://...",
    "category": "Electronics",
    "createdAt": "2026-04-01T10:00:00"
  }
}
```

---

### 3.4 Create Product (Admin Only)
- **Method:** POST
- **URL:** `http://localhost:8080/api/products`
- **Auth Required:** Yes (ADMIN role)
- **Headers:**
```
Authorization: Bearer {adminAccessToken}
Content-Type: application/json
```
- **Request Body:**
```json
{
  "name": "Wireless Mouse",
  "description": "Ergonomic wireless mouse",
  "price": 29.99,
  "stockQuantity": 200,
  "imageUrl": "https://example.com/mouse.jpg",
  "category": "Accessories"
}
```
- **Response (201 Created):**
```json
{
  "success": true,
  "message": "Product created successfully",
  "data": {
    "id": 101,
    "name": "Wireless Mouse",
    "description": "Ergonomic wireless mouse",
    "price": 29.99,
    "stockQuantity": 200,
    "imageUrl": "https://example.com/mouse.jpg",
    "category": "Accessories",
    "createdAt": "2026-04-01T12:00:00"
  }
}
```

---

### 3.5 Update Product (Admin Only)
- **Method:** PUT
- **URL:** `http://localhost:8080/api/products/{id}`
- **Auth Required:** Yes (ADMIN role)
- **Headers:**
```
Authorization: Bearer {adminAccessToken}
```
- **Request Body:** Same as Create Product (all fields optional)
- **Response (200 OK):** Same as Create Product response

---

### 3.6 Delete Product (Admin Only)
- **Method:** DELETE
- **URL:** `http://localhost:8080/api/products/{id}`
- **Auth Required:** Yes (ADMIN role)
- **Headers:**
```
Authorization: Bearer {adminAccessToken}
```
- **Response (200 OK):**
```json
{
  "success": true,
  "message": "Product deleted successfully",
  "data": null
}
```

---

## 4. CART ENDPOINTS (Auth Required)

### 4.1 Get My Cart
- **Method:** GET
- **URL:** `http://localhost:8080/api/cart`
- **Auth Required:** Yes
- **Headers:**
```
Authorization: Bearer {accessToken}
```
- **Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "userId": 1,
    "cartItems": [
      {
        "id": 1,
        "productId": 1,
        "productName": "Laptop",
        "quantity": 1,
        "unitPrice": 999.99,
        "totalPrice": 999.99
      }
    ],
    "totalPrice": 999.99,
    "createdAt": "2026-04-01T10:00:00"
  }
}
```

---

### 4.2 Add Item to Cart
- **Method:** POST
- **URL:** `http://localhost:8080/api/cart/items`
- **Auth Required:** Yes
- **Headers:**
```
Authorization: Bearer {accessToken}
```
- **Request Body:**
```json
{
  "productId": 1,
  "quantity": 2
}
```
- **Response (200 OK):** Same as Get My Cart

---

### 4.3 Update Cart Item Quantity
- **Method:** PUT
- **URL:** `http://localhost:8080/api/cart/items/{cartItemId}?quantity={newQuantity}`
- **Auth Required:** Yes
- **Headers:**
```
Authorization: Bearer {accessToken}
```
- **Example URL:**
```
http://localhost:8080/api/cart/items/1?quantity=3
```
- **Response (200 OK):** Same as Get My Cart

---

### 4.4 Remove Item from Cart
- **Method:** DELETE
- **URL:** `http://localhost:8080/api/cart/items/{cartItemId}`
- **Auth Required:** Yes
- **Headers:**
```
Authorization: Bearer {accessToken}
```
- **Response (200 OK):** Same as Get My Cart

---

### 4.5 Clear Cart
- **Method:** DELETE
- **URL:** `http://localhost:8080/api/cart`
- **Auth Required:** Yes
- **Headers:**
```
Authorization: Bearer {accessToken}
```
- **Response (200 OK):**
```json
{
  "success": true,
  "message": "Cart cleared",
  "data": null
}
```

---

## 5. ORDER ENDPOINTS (Auth Required)

### 5.1 Create Order
- **Method:** POST
- **URL:** `http://localhost:8080/api/orders`
- **Auth Required:** Yes
- **Headers:**
```
Authorization: Bearer {accessToken}
```
- **Request Body:**
```json
{
  "shippingAddress": "123 Main Street",
  "shippingCity": "New York",
  "shippingState": "NY",
  "shippingZipCode": "10001",
  "shippingCountry": "USA",
  "notes": "Please deliver between 9 AM - 5 PM"
}
```
- **Response (201 Created):**
```json
{
  "success": true,
  "message": "Order created successfully",
  "data": {
    "id": 1,
    "userId": 1,
    "orderNumber": "ORD-001",
    "totalAmount": 999.99,
    "orderStatus": "PENDING",
    "paymentStatus": "PENDING",
    "shippingAddress": "123 Main Street",
    "shippingCity": "New York",
    "shippingState": "NY",
    "shippingZipCode": "10001",
    "shippingCountry": "USA",
    "notes": "Please deliver between 9 AM - 5 PM",
    "orderItems": [
      {
        "id": 1,
        "productId": 1,
        "productName": "Laptop",
        "quantity": 1,
        "unitPrice": 999.99,
        "totalPrice": 999.99
      }
    ],
    "createdAt": "2026-04-01T13:00:00"
  }
}
```

---

### 5.2 Get My Orders
- **Method:** GET
- **URL:** `http://localhost:8080/api/orders`
- **Auth Required:** Yes
- **Headers:**
```
Authorization: Bearer {accessToken}
```
- **Query Parameters:**
  - `page` (default: 0)
  - `size` (default: 10)
- **Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 1,
        "userId": 1,
        "orderNumber": "ORD-001",
        "totalAmount": 999.99,
        "orderStatus": "PENDING",
        "paymentStatus": "PENDING",
        "createdAt": "2026-04-01T13:00:00"
      }
    ],
    "page": 0,
    "size": 10,
    "totalElements": 5,
    "totalPages": 1
  }
}
```

---

### 5.3 Get Order by ID
- **Method:** GET
- **URL:** `http://localhost:8080/api/orders/{orderId}`
- **Auth Required:** Yes
- **Headers:**
```
Authorization: Bearer {accessToken}
```
- **Response (200 OK):** Same structure as Create Order response

---

### 5.4 Cancel Order
- **Method:** POST
- **URL:** `http://localhost:8080/api/orders/{orderId}/cancel`
- **Auth Required:** Yes
- **Headers:**
```
Authorization: Bearer {accessToken}
```
- **Response (200 OK):**
```json
{
  "success": true,
  "message": "Order cancelled successfully",
  "data": {
    "id": 1,
    "orderStatus": "CANCELLED",
    "paymentStatus": "PENDING"
  }
}
```

---

### 5.5 Get All Orders (Admin Only)
- **Method:** GET
- **URL:** `http://localhost:8080/api/orders/admin/all`
- **Auth Required:** Yes (ADMIN role)
- **Headers:**
```
Authorization: Bearer {adminAccessToken}
```
- **Query Parameters:**
  - `page` (default: 0)
  - `size` (default: 20)
- **Response (200 OK):** Paginated list of all orders

---

### 5.6 Update Order Status (Admin Only)
- **Method:** PUT
- **URL:** `http://localhost:8080/api/orders/admin/{orderId}/status?status={newStatus}`
- **Auth Required:** Yes (ADMIN role)
- **Headers:**
```
Authorization: Bearer {adminAccessToken}
```
- **Status Values:** PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED
- **Example URL:**
```
http://localhost:8080/api/orders/admin/1/status?status=SHIPPED
```
- **Response (200 OK):**
```json
{
  "success": true,
  "message": "Order status updated",
  "data": {
    "id": 1,
    "orderStatus": "SHIPPED"
  }
}
```

---

## 6. PAYMENT ENDPOINTS (Auth Required)

### 6.1 Create Payment Intent
- **Method:** POST
- **URL:** `http://localhost:8080/api/payments/create-intent`
- **Auth Required:** Yes
- **Headers:**
```
Authorization: Bearer {accessToken}
```
- **Request Body:**
```json
{
  "orderId": 1,
  "paymentMethodId": "pm_1234567890"
}
```
- **Response (200 OK):**
```json
{
  "success": true,
  "message": "Payment intent created",
  "data": {
    "paymentIntentId": "pi_1234567890",
    "clientSecret": "pi_1234567890_secret_abcdef",
    "amount": 99999,
    "currency": "usd",
    "status": "requires_payment_method"
  }
}
```

---

### 6.2 Confirm Payment
- **Method:** POST
- **URL:** `http://localhost:8080/api/payments/confirm/{orderId}?paymentIntentId={intentId}`
- **Auth Required:** Yes
- **Headers:**
```
Authorization: Bearer {accessToken}
```
- **Example URL:**
```
http://localhost:8080/api/payments/confirm/1?paymentIntentId=pi_1234567890
```
- **Response (200 OK):**
```json
{
  "success": true,
  "message": "Payment confirmed",
  "data": {
    "paymentIntentId": "pi_1234567890",
    "status": "succeeded",
    "amount": 99999,
    "currency": "usd"
  }
}
```

---

### 6.3 Stripe Webhook (No Auth Required)
- **Method:** POST
- **URL:** `http://localhost:8080/api/payments/webhook`
- **Auth Required:** No
- **Headers:**
```
Content-Type: application/json
Stripe-Signature: {signature_header}
```
- **Note:** This endpoint is called by Stripe servers automatically. Not for manual testing.

---

## 7. HEALTH ENDPOINT (No Auth Required)

### 7.1 Health Check
- **Method:** GET
- **URL:** `http://localhost:8080/api/`
- **Auth Required:** No
- **Response (200 OK):**
```json
{
  "status": "up"
}
```

---

## Error Responses

### Validation Error (400)
```json
{
  "success": false,
  "message": "Validation failed",
  "errors": {
    "email": "Invalid email format",
    "password": "Password must be at least 8 characters"
  }
}
```

### Unauthorized (401)
```json
{
  "success": false,
  "message": "Unauthorized: Invalid or expired token",
  "timestamp": "2026-04-01T14:00:00"
}
```

### Forbidden (403)
```json
{
  "success": false,
  "message": "Forbidden: Admin role required",
  "timestamp": "2026-04-01T14:00:00"
}
```

### Not Found (404)
```json
{
  "success": false,
  "message": "Product not found",
  "timestamp": "2026-04-01T14:00:00"
}
```

### Internal Server Error (500)
```json
{
  "success": false,
  "message": "Internal server error",
  "timestamp": "2026-04-01T14:00:00"
}
```

---

## Testing Workflow

### 1. Register & Login
1. POST `/auth/register` with your details → Get `accessToken`
2. Or POST `/auth/login` → Get `accessToken`

### 2. Browse Products
1. GET `/products` → View all products (no auth needed)
2. GET `/products/{id}` → View product details

### 3. Shopping
1. POST `/cart/items` → Add product to cart
2. GET `/cart` → View cart
3. PUT `/cart/items/{id}?quantity=3` → Update quantity
4. DELETE `/cart/items/{id}` → Remove from cart

### 4. Checkout
1. POST `/orders` → Create order from cart
2. POST `/payments/create-intent` → Create payment
3. POST `/payments/confirm/{orderId}` → Confirm payment

### 5. Order Management
1. GET `/orders` → View your orders
2. GET `/orders/{id}` → View order details
3. POST `/orders/{id}/cancel` → Cancel order

### 6. User Profile
1. GET `/users/me` → View profile
2. PUT `/users/me` → Update profile
3. PUT `/users/me/password` → Change password

---

## Notes
- All timestamps are in ISO 8601 format (UTC)
- Prices are in the currency configured in the backend (default: USD)
- JWT tokens expire after 24 hours; use refresh token to get new one
- Admin endpoints require `ROLE_ADMIN` role
- Security is currently set to `permitAll` for development; restrict in production


# E-Commerce Backend System

A comprehensive Spring Boot REST API for an e-commerce platform with JWT authentication, product management, shopping cart, order processing, and Stripe payment integration.

## 📋 Features

### Authentication & Security
- ✅ User registration and login with JWT tokens
- ✅ Token refresh mechanism (24-hour expiry)
- ✅ BCrypt password encryption
- ✅ Spring Security with stateless sessions
- ✅ Role-based access control (USER, ADMIN)

### Product Management
- ✅ Browse all products with pagination
- ✅ Search and filter products by keyword/category
- ✅ Product CRUD operations (Admin only)
- ✅ Stock management

### Shopping Cart
- ✅ Add/remove items from cart
- ✅ Update item quantities
- ✅ View cart with total price calculation
- ✅ Clear entire cart

### Order Management
- ✅ Create orders from cart
- ✅ View order history with pagination
- ✅ Cancel orders
- ✅ Order status tracking (Admin)
- ✅ Shipping information management

### Payment Processing
- ✅ Stripe payment integration
- ✅ Payment intent creation
- ✅ Payment confirmation with webhook support
- ✅ Payment status tracking

### User Profile
- ✅ View user profile
- ✅ Update profile information
- ✅ Change password
- ✅ Manage user details (phone, address, etc.)

## 🛠️ Tech Stack

- **Framework:** Spring Boot 3.x
- **Language:** Java 21
- **Security:** Spring Security 6, JWT (JJWT)
- **Database:** MySQL 8.0
- **ORM:** JPA/Hibernate
- **Build Tool:** Maven
- **Payment:** Stripe API
- **API Documentation:** Postman

## 📦 Project Structure

```
com.ecommerce/
├── src/main/java/com/ecommerce/
│   ├── controller/              # REST API endpoints
│   ├── service/                 # Business logic
│   ├── entity/                  # JPA entities
│   ├── repository/              # Data access layer
│   ├── dto/                     # Request/Response DTOs
│   ├── security/                # JWT & Security config
│   ├── config/                  # Application configuration
│   ├── exception/               # Custom exceptions
│   └── util/                    # Utility classes
├── src/main/resources/
│   └── application.properties   # Configuration
└── pom.xml                      # Maven dependencies
```

## 🚀 Getting Started

### Prerequisites
- Java 21 or higher
- MySQL 8.0 or higher
- Maven 3.6+
- Git

### Installation

1. **Clone the repository:**
```bash
git clone https://github.com/yourusername/second-round-assignm-final-12683-rutuja.git
cd com.ecommerce
```

2. **Create database:**
```sql
CREATE DATABASE IF NOT EXISTS ecommerce_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

3. **Configure application.properties:**
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/ecommerce_db
spring.datasource.username=root
spring.datasource.password=your_password
```

4. **Build the project:**
```bash
mvn clean package -DskipTests
```

5. **Run the application:**
```bash
mvn spring-boot:run
```
or
```bash
java -jar target/com.ecommerce-0.0.1-SNAPSHOT.jar
```

The application will start on `http://localhost:8080/api`

## 📚 API Documentation

See `POSTMAN_API_GUIDE.md` for complete API documentation with:
- All endpoint URLs
- Request/response examples
- Authentication requirements
- Query parameters
- Error responses

### Quick API Examples

**Register:**
```bash
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "Password123!",
  "firstName": "John",
  "lastName": "Doe"
}
```

**Login:**
```bash
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "Password123!"
}
```

**Get All Products:**
```bash
GET http://localhost:8080/api/products?page=0&size=10
```

**Add to Cart:**
```bash
POST http://localhost:8080/api/cart/items
Authorization: Bearer {accessToken}
Content-Type: application/json

{
  "productId": 1,
  "quantity": 2
}
```

## 🔐 Security Features

- **JWT Authentication:** Stateless token-based authentication
- **Password Encryption:** BCrypt password hashing
- **CORS Enabled:** Cross-origin requests for frontend integration
- **Role-Based Access:** Admin and User roles
- **Exception Handling:** Global exception handler with consistent error responses

## 🗄️ Database Schema

The application automatically creates and updates tables using Hibernate's `ddl-auto: update` setting.

**Main Tables:**
- `users` - User accounts and authentication
- `products` - Product catalog
- `cart` - Shopping carts
- `cart_items` - Items in carts
- `orders` - Customer orders
- `order_items` - Items in orders

Run `database_schema.sql` for manual setup.

## 🔧 Configuration

### application.properties
```properties
server.port=8080
server.servlet.context-path=/api

spring.datasource.url=jdbc:mysql://localhost:3306/ecommerce_db
spring.datasource.username=root
spring.datasource.password=root

jwt.secret=<base64-encoded-secret>
jwt.expiration=86400000
jwt.refresh-expiration=604800000

stripe.api.key=<your-stripe-key>
stripe.webhook.secret=<your-webhook-secret>
```

## 📊 Error Handling

The API returns consistent error responses:

```json
{
  "success": false,
  "message": "Error description",
  "timestamp": "2026-04-01T14:00:00"
}
```

**HTTP Status Codes:**
- `200` - OK
- `201` - Created
- `400` - Bad Request (validation error)
- `401` - Unauthorized (invalid token)
- `403` - Forbidden (insufficient permissions)
- `404` - Not Found
- `500` - Internal Server Error

## 🧪 Testing

Run tests with:
```bash
mvn test
```

**Test Coverage:**
- AuthServiceTest
- AuthControllerTest
- CartServiceTest
- OrderServiceTest
- ProductServiceTest

## 🚢 Deployment

### Build for Production
```bash
mvn clean package -DskipTests -Pprod
```

### Environment Variables
Set these before running:
- `DB_USERNAME` - Database user
- `DB_PASSWORD` - Database password
- `JWT_SECRET` - JWT signing secret
- `STRIPE_SECRET_KEY` - Stripe API key
- `STRIPE_WEBHOOK_SECRET` - Stripe webhook secret

## 📋 Developer Notes

### Adding New Endpoints
1. Create controller in `controller/`
2. Create service interface in `service/`
3. Implement service in `service/impl/`
4. Add DTOs in `dto/request/` and `dto/response/`
5. Document in POSTMAN_API_GUIDE.md

### Database Migrations
Entities in `entity/` folder automatically create tables. Modify entities and restart the app.

### JWT Token Structure
Tokens include:
- Username (sub claim)
- Role
- Issued at (iat)
- Expiration (exp)

## 📝 License

This project is part of an assignment submission.

## 👨‍💻 Author

Created for the E-Commerce System Assignment

## 📞 Support

For issues or questions:
1. Check POSTMAN_API_GUIDE.md
2. Review error logs
3. Check database connection

---

**Last Updated:** April 1, 2026
**Current Version:** 0.0.1-SNAPSHOT
**Java Version:** 21
**Spring Boot Version:** 3.x


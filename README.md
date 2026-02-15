# RSA JWT Authentication with Spring Boot 3.x

A Spring Boot 3.x application demonstrating RSA-based JWT (JSON Web Token) authentication using Java 17.

## Features

- **Spring Boot 3.2.2** - Latest Spring Boot framework
- **Java 17** - Java LTS version (easily upgradeable to Java 21 or later)
- **RSA-256 Algorithm** - Asymmetric cryptography for JWT signing
- **Stateless Authentication** - No server-side session management
- **Spring Security** - Integrated security framework
- **REST API** - Sample endpoints for authentication and protected resources

## Technologies Used

- Spring Boot 3.2.2
- Spring Security 6.x
- Java 17 (upgradeable to Java 21, 25, or later)
- JWT (JSON Web Token) with JJWT library
- Maven

## Java Version Compatibility

This project currently uses Java 17 LTS. To use Java 21 or Java 25:

1. Install Java 21 or 25 on your system
2. Update the `java.version`, `maven.compiler.source`, and `maven.compiler.target` properties in `pom.xml` to `21` or `25`
3. Rebuild the project with `mvn clean install`

The code is compatible with Java 17, 21, 25, and future versions.

## Project Structure

```
rsa-jwt-java/
├── src/
│   ├── main/
│   │   ├── java/com/example/rsajwt/
│   │   │   ├── config/
│   │   │   │   └── SecurityConfig.java        # Spring Security configuration
│   │   │   ├── controller/
│   │   │   │   ├── AuthController.java        # Authentication endpoints
│   │   │   │   └── SampleController.java      # Sample protected endpoints
│   │   │   ├── filter/
│   │   │   │   └── JwtAuthenticationFilter.java # JWT validation filter
│   │   │   ├── model/
│   │   │   │   ├── AuthRequest.java           # Login request model
│   │   │   │   └── AuthResponse.java          # Login response model
│   │   │   ├── service/
│   │   │   │   └── JwtService.java            # JWT generation/validation
│   │   │   ├── util/
│   │   │   │   └── RsaKeyUtil.java            # RSA key utilities
│   │   │   └── RsaJwtApplication.java         # Main application
│   │   └── resources/
│   │       └── application.properties          # Configuration
│   └── test/
└── pom.xml                                     # Maven dependencies

```

## Prerequisites

- Java 17 or higher (Java 21, 25, or later also supported)
- Maven 3.6+

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/rohitpshelar/rsa-jwt-java.git
cd rsa-jwt-java
```

### 2. Build the project

```bash
mvn clean install
```

### 3. Run the application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## API Endpoints

### Public Endpoints (No authentication required)

#### 1. Login
```bash
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJSUzI1NiJ9...",
  "username": "admin"
}
```

#### 2. Validate Token
```bash
POST /api/auth/validate?token=YOUR_JWT_TOKEN
```

**Response:**
```json
{
  "valid": true,
  "username": "admin"
}
```

#### 3. Public Hello
```bash
GET /api/public/hello
```

**Response:**
```json
{
  "message": "Hello from public endpoint!",
  "timestamp": 1708000000000
}
```

### Protected Endpoints (Authentication required)

#### 1. Protected Hello
```bash
GET /api/protected/hello
Authorization: Bearer YOUR_JWT_TOKEN
```

**Response:**
```json
{
  "message": "Hello from protected endpoint!",
  "username": "admin",
  "timestamp": 1708000000000
}
```

#### 2. Get User Info
```bash
GET /api/protected/user
Authorization: Bearer YOUR_JWT_TOKEN
```

**Response:**
```json
{
  "username": "admin",
  "authenticated": true,
  "authorities": []
}
```

## Demo Users

The application comes with two demo users:

| Username | Password  |
|----------|-----------|
| admin    | admin123  |
| user     | user123   |

## How It Works

### RSA Key Generation

The application automatically generates an RSA key pair (2048-bit) on startup:
- **Private Key**: Used to sign JWT tokens
- **Public Key**: Used to verify JWT token signatures

### JWT Token Flow

1. **Login**: User sends credentials to `/api/auth/login`
2. **Token Generation**: Server validates credentials and generates a JWT token signed with RSA private key
3. **Token Response**: Server returns the JWT token to the client
4. **Authenticated Requests**: Client includes the JWT token in the `Authorization: Bearer <token>` header
5. **Token Validation**: `JwtAuthenticationFilter` validates the token using RSA public key
6. **Access Grant**: If valid, the request proceeds to the protected endpoint

### Security Features

- **Stateless**: No server-side session storage
- **RSA-256**: Asymmetric encryption for enhanced security
- **Token Expiration**: Tokens expire after 24 hours
- **CSRF Protection**: Disabled for stateless REST API (standard practice for JWT-based APIs)
  - Note: CSRF protection is not needed for stateless JWT APIs that don't use cookies
  - If serving web pages or using cookies, CSRF protection should be enabled
- **Password Encryption**: BCrypt password encoding

## Testing with cURL

### 1. Login and get token
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

### 2. Access protected endpoint
```bash
# Replace YOUR_TOKEN with the token from step 1
curl -X GET http://localhost:8080/api/protected/hello \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 3. Validate token
```bash
curl -X POST "http://localhost:8080/api/auth/validate?token=YOUR_TOKEN"
```

## Testing with Postman

1. **Login**:
   - Method: POST
   - URL: `http://localhost:8080/api/auth/login`
   - Body (JSON):
     ```json
     {
       "username": "admin",
       "password": "admin123"
     }
     ```

2. **Use the token**:
   - Copy the `token` from the response
   - For protected endpoints, add header:
     - Key: `Authorization`
     - Value: `Bearer <your-token-here>`

## Configuration

You can customize the application by modifying `src/main/resources/application.properties`:

```properties
# Change server port
server.port=8080

# Adjust logging levels
logging.level.com.example.rsajwt=DEBUG
```

## Building for Production

```bash
mvn clean package
java -jar target/rsa-jwt-java-1.0.0.jar
```

## License

This project is open source and available under the MIT License.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## Author

Rohit Shelar

## Production Considerations

This is a demonstration application. Before deploying to production, consider the following:

1. **Key Persistence**: The RSA keys are generated on startup. In production:
   - Store keys in a secure location (e.g., AWS KMS, Azure Key Vault, HashiCorp Vault)
   - Implement key rotation strategy
   - Use environment-specific keys

2. **User Management**: Replace the in-memory user store with:
   - Database-backed user repository (Spring Data JPA)
   - Integration with identity providers (OAuth2, LDAP, Active Directory)
   - Proper password policies and account management

3. **Token Storage**: 
   - Implement token revocation mechanism
   - Consider refresh tokens for better security
   - Use secure token storage on client side

4. **Security Enhancements**:
   - Add rate limiting to prevent brute force attacks
   - Implement CORS policies for frontend applications
   - Use HTTPS in production
   - Add input validation and sanitization
   - Implement proper error handling without exposing sensitive information

5. **Monitoring and Logging**:
   - Add structured logging
   - Implement audit trails
   - Set up monitoring and alerting
   - Track authentication failures

6. **Configuration**:
   - Use environment variables for sensitive data
   - Implement different profiles (dev, staging, production)
   - Secure application.properties with Spring Cloud Config or similar

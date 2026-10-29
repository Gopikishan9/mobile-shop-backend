# Mobile Shop Backend

A RESTful backend application for a mobile shop management system built with **Java and Spring Boot**. It provides secure authentication, role-based authorization, product management, image uploads, and API documentation.

## Features

- **User Authentication:** User registration and login using JWT.
- **Role-Based Authorization:** Separate access permissions for `USER` and `ADMIN` roles.
- **Product Management:** Create, retrieve, update, and delete products through REST APIs.
- **Database Integration:** MySQL integration using Spring Data JPA and Hibernate.
- **Image Uploads:** Cloudinary integration for product image storage.
- **Input Validation:** Request validation to maintain data integrity.
- **Global Exception Handling:** Consistent handling of application errors.
- **API Documentation:** Swagger UI and OpenAPI documentation.
- **Health Check:** Endpoint for checking API availability.
- **Unit Testing:** JUnit and Mockito tests for application components.

## Tech Stack

| Technology | Purpose |
|---|---|
| Java | Programming language |
| Spring Boot | Backend framework |
| Spring Security | Authentication and authorization |
| Spring Data JPA | Database access |
| Hibernate | ORM |
| MySQL | Relational database |
| JWT | Token-based authentication |
| Cloudinary | Image storage |
| Maven | Dependency management |
| Swagger / OpenAPI | API documentation |
| JUnit 5 & Mockito | Testing |
| Git & GitHub | Version control |

## Project Structure

```text
mobile-shop-backend/
├── src/
│   ├── main/
│   │   ├── java/com/gopikishan/mobileshop/
│   │   │   ├── config/
│   │   │   ├── controller/
│   │   │   ├── dto/
│   │   │   ├── entity/
│   │   │   ├── exception/
│   │   │   ├── filter/
│   │   │   ├── repository/
│   │   │   └── service/
│   │   └── resources/
│   │       └── application.properties
│   └── test/
├── .dockerignore
├── .gitignore
├── pom.xml
└── README.md
```

## Getting Started

### Prerequisites

Install the following tools before running the application:

- Java JDK compatible with the project configuration
- MySQL Server
- Maven (or use the included Maven Wrapper)
- A Cloudinary account for image uploads

### 1. Clone the Repository

```bash
git clone https://github.com/gopikishan9/mobile-shop-backend.git
cd mobile-shop-backend
```

Replace the GitHub URL if your username or repository name is different.

### 2. Configure the Database

Create a MySQL database for the application.

```sql
CREATE DATABASE mobile_shop;
```

Configure the database connection in your environment variables or local application configuration, according to the placeholders defined in `application.properties`.

### 3. Configure Environment Variables

Set the required environment variables for your database, JWT signing key, and Cloudinary credentials.

Typical configuration values include:

```env
DB_URL=your_database_url
DB_USERNAME=your_database_username
DB_PASSWORD=your_database_password
JWT_SECRET=your_long_random_secret
CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_api_key
CLOUDINARY_API_SECRET=your_api_secret
```

**Important:** These are example variable names. Make sure they match the variables referenced by your actual application configuration. Do not commit your real `.env` file or expose secrets in source control.

### 4. Run the Application

Using the Maven Wrapper on Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

Alternatively, run the main application class from your IDE.

The server uses port `8080` by default unless configured otherwise.

### 5. Access API Documentation

Once the application is running, open:

[Swagger UI](http://localhost:8080/docs)

If your Swagger configuration uses a different path, use the configured URL.

## API Overview

The application provides endpoints for authentication, product management, and health checks.

| Functionality | Access |
|---|---|
| User registration | Public |
| User login | Public |
| View products | Public |
| Create products | ADMIN |
| Update products | ADMIN |
| Delete products | ADMIN |
| Health check | As configured in security rules |

Consult Swagger UI for the exact endpoint paths, request formats, and response schemas.

## Authentication and Authorization

The application uses Spring Security and JSON Web Tokens (JWT).

1. Register a user account.
2. Log in using valid credentials.
3. Obtain the JWT returned by the login endpoint.
4. Include the token in the `Authorization` header for protected requests.

```http
Authorization: Bearer YOUR_JWT_TOKEN
```

Administrative product operations require an authenticated user with the `ADMIN` role.

## Running Tests

Execute the test suite using the Maven Wrapper:

```powershell
.\mvnw.cmd test
```

Tests use JUnit and Mockito to verify application behavior.

## Security Notes

- Keep `.env` and other secret configuration files out of Git.
- Never commit database passwords, JWT signing keys, or Cloudinary API secrets.
- Use strong, private secrets through environment variables.
- Configure production credentials and database access separately from local development.
- Review security settings before deploying the application publicly.

## Future Improvements

- Docker-based deployment
- Automated CI/CD pipeline
- Additional integration tests
- Production deployment and monitoring

## Author

**Gopikishan Rawat**

GitHub: [@gopikishan9](https://github.com/gopikishan9)

---

*Built as a backend development project to practise REST API development, authentication, authorization, database integration, and automated testing.*
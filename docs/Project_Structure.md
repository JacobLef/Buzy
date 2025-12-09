# Project Structure

## Overview

This document describes the directory structure, environment configuration, and dependencies for the Business Management Platform project.

## Directory Structure

```
final-project-final-project-team-10/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── edu/
│   │   │       └── neu/
│   │   │           └── csye6200/
│   │   │               │
│   │   │               ├── Driver.java                    # Main Spring Boot Application Entry Point
│   │   │               │
│   │   │               ├── config/                        # Spring Configuration Classes
│   │   │               │   ├── WebConfig.java            # Web-related configuration
│   │   │               │   ├── SecurityConfig.java        # Security configuration
│   │   │               │   └── CorsConfig.java           # CORS configuration
│   │   │               │
│   │   │               ├── model/                         # Data Models
│   │   │               │   ├── domain/                   # Core business entities (Business, BusinessPerson, Employee, Employer)
│   │   │               │   ├── payroll/                  # Payroll-related models (Paycheck, deductions)
│   │   │               │   └── training/                 # Training and certification models
│   │   │               │
│   │   │               ├── repository/                   # JPA Repositories for database operations
│   │   │               │
│   │   │               ├── service/                       # Service Layer
│   │   │               │   ├── interfaces/               # Service interfaces (PayrollService, EmployeeService, etc.)
│   │   │               │   └── impl/                     # Service implementations
│   │   │               │
│   │   │               ├── controller/                   # REST Controllers
│   │   │               │   └── HelloController.java      # Example controller
│   │   │               │
│   │   │               ├── dto/                         # Data Transfer Objects for API requests/responses
│   │   │               │
│   │   │               ├── factory/                     # Factory Pattern Implementations
│   │   │               │   └── BusinessPersonFactory.java
│   │   │               │
│   │   │               ├── strategy/                     # Strategy Pattern
│   │   │               │   ├── tax/                     # Tax calculation strategies
│   │   │               │   └── insurance/               # Insurance deduction strategies
│   │   │               │
│   │   │               ├── util/                        # Utility Classes
│   │   │               │   ├── csv/                     # CSV parsing utilities
│   │   │               │   └── comparators/            # Employee comparators for sorting
│   │   │               │
│   │   │               └── exception/                  # Custom Exceptions
│   │   │                   └── InvalidBusinessException.java
│   │   │
│   │   └── resources/
│   │       ├── application.properties                  # Main application configuration
│   │       ├── static/                                 # Static resources
│   │       │   ├── css/                                # Stylesheets
│   │       │   ├── js/                                 # JavaScript files
│   │       │   └── images/                            # Static images
│   │       ├── templates/                              # Thymeleaf templates
│   │       │   ├── auth/                               # Authentication pages
│   │       │   ├── dashboard/                          # Dashboard pages
│   │       │   ├── payroll/                           # Payroll pages
│   │       │   └── training/                          # Training pages
│   │       └── data/                                  # Data files
│   │           └── csv/                               # CSV seed files for database initialization
│   │
│   └── test/                                           # Test Directory
│       └── java/
│           └── edu/
│               └── neu/
│                   └── csye6200/
│                       ├── service/                     # Service layer tests
│                       ├── controller/                 # Controller integration tests
│                       └── util/                       # Utility class tests
│
├── pom.xml                                             # Maven configuration
├── .gitignore                                          # Git ignore rules
└── README.md                                           # Project documentation
```

## Main Application Entry Point

The main Spring Boot application entry point is **`Driver.java`** located at:
```
src/main/java/edu/neu/csye6200/Driver.java
```

Run the application using:
```bash
mvn spring-boot:run
```

Or directly run the `main` method in `Driver.java` from your IDE.

## Environment Configuration

### Application Properties

The main configuration file is located at:
```
src/main/resources/application.properties
```

### Key Configuration Sections

#### 1. Application Settings
```properties
spring.application.name=business-management-platform
server.port=8080
```
- **spring.application.name**: Application identifier
- **server.port**: HTTP server port (default: 8080)

#### 2. Database Configuration (MySQL)
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/business_management_db
spring.datasource.username=root
spring.datasource.password=mysql_password_here
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

**Setup Steps:**
1. Install and start MySQL server
2. Create the database:
   ```sql
   CREATE DATABASE business_management_db;
   ```
3. Update `spring.datasource.password` with your MySQL root password
4. Update `spring.datasource.username` if using a different user

#### 3. JPA/Hibernate Settings
```properties
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.jpa.properties.hibernate.format_sql=true
```
- **ddl-auto**: Automatically creates/updates database schema (`update`, `create`, `none`, etc.)
- **show-sql**: Logs SQL queries to console (useful for debugging)
- **format_sql**: Formats SQL output for readability

#### 4. File Upload Settings
```properties
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```
- Maximum file size for CSV imports and file uploads

#### 5. Logging Configuration
```properties
logging.level.edu.neu.csye6200=DEBUG
logging.level.org.springframework.web=INFO
```
- **DEBUG**: Detailed logging for application code
- **INFO**: Standard logging for Spring framework

### Environment-Specific Configuration

For different environments, create separate property files:
- `application-dev.properties` - Development environment
- `application-prod.properties` - Production environment
- `application-local.properties` - Local development (already in .gitignore)

Activate with:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## Dependencies

### Core Dependencies (from pom.xml)

#### 1. Spring Boot Web Starter
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```
**Purpose**: Provides REST API support, embedded Tomcat server, and Spring MVC framework.

#### 2. Spring Boot Data JPA
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```
**Purpose**: 
- JPA (Java Persistence API) support
- Hibernate ORM integration
- Repository pattern implementation
- Database abstraction layer

#### 3. MySQL Connector
```xml
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>
```
**Purpose**: 
- JDBC driver for MySQL database
- Runtime dependency (not needed at compile time)

#### 4. Spring Boot Test Starter
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```
**Purpose**: 
- Testing framework (JUnit, Mockito, AssertJ)
- Spring Test support
- Only available during test phase

### Spring Boot Version
- **Parent POM**: Spring Boot 3.2.0
- **Java Version**: 11

## Development Workflow

1. **Start MySQL Database**
   ```bash
   mysql.server start
   ```

2. **Configure Database Password**
   - Edit `src/main/resources/application.properties`
   - Update `spring.datasource.password`

3. **Run Application**
   ```bash
   mvn spring-boot:run
   ```
   Or run `Driver.java` main method from IDE

4. **Access Application**
   - Web Interface: http://localhost:8080
   - API Endpoints: http://localhost:8080/api/...

## Testing

Run tests with:
```bash
mvn test
```

Test structure mirrors main source structure:
- `src/test/java/edu/neu/csye6200/service/` - Service tests
- `src/test/java/edu/neu/csye6200/controller/` - Controller tests
- `src/test/java/edu/neu/csye6200/util/` - Utility tests

## Notes

- Empty directories contain `.gitkeep` files to ensure they are tracked by Git
- Personal documentation should be stored in `local/` directory (ignored by Git)
- Database schema is auto-generated based on JPA entities
- Logs are output to console (configure logging in `application.properties` for file output)


# Business Management Platform

A unified platform for small to medium-sized businesses to manage their organization, employees, and payroll operations.

## About

Many small businesses rely on disconnected spreadsheets or expensive enterprise software. This platform provides a streamlined alternative with:

- **Employee & Employer Management** — Track staff information, roles, and reporting relationships
- **Payroll Processing** — Automated calculations with configurable tax strategies and deductions
- **Training Compliance** — Monitor certifications and expiration dates
- **Organization Hierarchy** — Visualize company structure with an interactive tree view

## Tech Stack

- **Backend:** Java, Spring Boot
- **Frontend:** React, TypeScript
- **Database:** MySQL

## Getting Started

### Prerequisites

- Java 17+
- Node.js 18+
- MySQL 8.0+

### 1. Configure Database

Create a MySQL database:

```sql
CREATE DATABASE business_management;
```

Go to `backend/src/main/resources/` and create a `.env` file:

```env
DB_USERNAME=your_username
DB_PASSWORD=your_password
```

> **Note:** If your MySQL credentials are `root` with no password, you can skip the `.env` file.

### 2. Start the Backend

From the `backend/` directory:

```bash
./mvnw spring-boot:run
```

Or simply run the `BusinessManagementApplication` class from your IDE.

### 3. Start the Frontend

From the `frontend/` directory:

```bash
npm install
npm run dev
```

The app will be available at `http://localhost:5173`

## Documentation

- [Documentation](docs/)
- [Project Structure](docs/Project_Structure.md)
- [UI Components Guide](docs/UI_COMPONENTS_GUIDE.md)

## License

MIT License — see [LICENSE](LICENSE) for details.


# Supermarket Management System

![Spring Boot](https://img.shields.io/badge/SpringBoot-3.5.8-green)
![Vue](https://img.shields.io/badge/Vue-3-blue)
![Docker](https://img.shields.io/badge/Docker-enabled-blue)

## Overview

A full-stack supermarket management system built with Spring Boot (backend), Vue.js (frontend), MySQL (database), and Docker for deployment.

Live Demo: [Supermarket Management System](https://thiencuong.id.vn)

---

## Features

### Roles & Permissions

- **Manager**: Full system control including user, product, and inventory management
- **Sales Staff**: Handle in-store transactions via POS interface
- **Warehouse Staff**: Manage inventory and stock updates
- **Customer**: View products and purchase history only

### Core Features

- Authentication & Authorization (JWT-based)
- Product management
- Category management
- Inventory tracking
- Sales transaction processing
- User management

---

## Technologies Used

### Backend

- Java / Spring Boot
- Spring Security + JWT Authentication
- Spring Data JPA
- Hibernate
- RESTful API
- MySQL

### Frontend

- Vue 3
- TailwindCSS
- Admin dashboard template based on:
  [Admin One](https://github.com/justboil/admin-one-vue-tailwind)

### DevOps

- Docker & Docker Compose

---

## Project Structure

```text
.
├── LICENSE
├── README.md
├── docker-compose.yml
├── backend/    # Backend source code (Spring Boot)
├── frontend/   # Frontend source code (Vue.js)
└── mysql/      # MySQL-related scripts
```

---

## Setup and Usage

### Prerequisites

Before running the project, make sure you have installed:

- **Docker Desktop** (recommended for easier setup on Windows/macOS)
- **Git** (for cloning the repository)

### Quick Start

1. **Clone the repository**

```bash
git clone https://github.com/nhtc2005/supermarket-management-system.git
cd supermarket-management-system
```

2. **Create `.env` file**

Create a `.env` file in the root directory:

```env
# MySQL database configuration
MYSQL_ROOT_PASSWORD=your_root_password
MYSQL_DATABASE=your_database_name
MYSQL_USER=your_db_user
MYSQL_PASSWORD=your_db_password

# Application configuration
DATABASE_URL=jdbc:mysql://db:3306/your_database_name?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
DATABASE_USERNAME=your_db_user
DATABASE_PASSWORD=your_db_password
SECRET_KEY=your_secret_key
```

3. **Run with Docker**

```bash
docker-compose up --build
```

4. **Access the application**

Backend runs on:

```text
http://localhost:8080
```

Frontend runs on:

```text
http://localhost:3000
```

Swagger UI is available at:

```text
http://localhost:8080/api/v1/swagger-ui/index.html
```

---

## Demo Accounts

You can use the following sample accounts to log in:

| Role        | Username       | Password |
|-------------|----------------|----------|
| Manager     | manager1       | 123456   |
| Sales       | sales1         | 123456   |
| Warehouse   | warehouse1     | 123456   |
| Customer    | an@gmail.com   | 123456   |
| Customer    | binh@gmail.com | 123456   |

> **Note:** These accounts are for testing purposes only.

---

## Architecture

```text
Frontend (Vue.js) → Backend (Spring Boot) → MySQL
```

---

## Deployment

The system is currently deployed at: [Supermarket Management System](https://thiencuong.id.vn)

- Frontend: Nginx container (Vue build)
- Backend: Spring Boot REST API container
- Database: MySQL container (Docker volume persistence)

---

## License

This project is for educational and demonstration purposes.

---

## Acknowledgements

- Admin UI template inspired by: [Admin One](https://github.com/justboil/admin-one-vue-tailwind)
- Spring Boot community
- Vue.js ecosystem
- MySQL community
- Docker for containerization

Special thanks to all contributors and open-source libraries that made this project possible.

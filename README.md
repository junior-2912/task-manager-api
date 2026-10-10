# Task Manager API

REST API for a personal task management application, developed with Java and Spring Boot.

The project was created to help organize daily tasks, track their status and due dates, and practice backend development concepts through a complete application.

The API is integrated with an Angular frontend available in the [Task Manager Frontend](https://github.com/junior-2912/taskmanager-frontend) repository.

## Features

* Create, retrieve, update and delete tasks.
* Manage task statuses: `PENDING`, `IN_PROGRESS`, `FINISHED` and `CANCELED`.
* Organize tasks by category: `WORK`, `PERSONAL`, `IMPORTANT`, `STUDY` and `OTHER`.
* Filter tasks by status, category, title and overdue status.
* Paginate and sort task results.
* Validate task due dates.
* Enforce business rules for task deletion and status transitions.
* Handle application exceptions through a standardized error response.
* Persist data in PostgreSQL using Spring Data JPA.

## Technologies

* **Java 21**
* **Spring Boot 4.1.1**
* **Spring Web MVC**
* **Spring Data JPA**
* **Hibernate**
* **PostgreSQL**
* **Flyway**
* **Jakarta Bean Validation**
* **Maven**
* **Docker Compose**
* **JUnit and Mockito** for testing

## Architecture

The application follows a layered architecture:

* **Controller:** handles HTTP requests and responses.
* **Service:** implements application rules and coordinates operations.
* **Repository:** provides database access through Spring Data JPA.
* **Domain:** contains the task entity and its behavior.
* **DTOs:** define request data structures.
* **Specifications:** build dynamic database queries for optional filters.
* **Exceptions:** represent application errors and business rule violations.

Filtering and pagination are delegated to the database through Spring Data JPA, avoiding the need to retrieve every task and filter the results in application memory.

## API Endpoints

Base URL: `http://localhost:8080`

| Method   | Endpoint             | Description                                     |
| -------- | -------------------- | ----------------------------------------------- |
| `GET`    | `/tasks`             | List tasks with optional filters and pagination |
| `GET`    | `/tasks/{id}`        | Retrieve a task by ID                           |
| `POST`   | `/tasks`             | Create a task                                   |
| `PUT`    | `/tasks/{id}`        | Update a task                                   |
| `DELETE` | `/tasks/{id}`        | Delete a task, subject to business rules        |
| `PATCH`  | `/tasks/{id}/status` | Change a task's status                          |

### Filtering and pagination

The `GET /tasks` endpoint supports optional query parameters:

| Parameter  | Description                             |
| ---------- | --------------------------------------- |
| `status`   | Filter by task status                   |
| `category` | Filter by task category                 |
| `title`    | Search by title                         |
| `overdue`  | Filter overdue tasks when set to `true` |
| `page`     | Zero-based page index                   |
| `size`     | Number of tasks per page                |
| `sort`     | Sorting property and direction          |

Example:

```http
GET /tasks?status=PENDING&category=WORK&page=0&size=10&sort=dueDate,asc
```

Search for overdue tasks:

```http
GET /tasks?overdue=true&page=0&size=10
```

Filters can be combined in the same request.

### Creating a task

Example request:

```http
POST /tasks
Content-Type: application/json
```

```json
{
  "title": "Review weekly inventory",
  "description": "Check pending inventory tasks",
  "dueDate": "2026-10-20",
  "taskCategory": "WORK"
}
```

The request fields must match the validation rules and DTO definitions implemented by the API.

### Changing task status

Example request:

```http
PATCH /tasks/1/status
Content-Type: application/json
```

```json
{
  "status": "IN_PROGRESS"
}
```

Available status values are `PENDING`, `IN_PROGRESS`, `FINISHED` and `CANCELED`.

Some status transitions and deletion operations are restricted by business rules.

## Getting Started

### Prerequisites

Make sure you have installed:

* JDK 21
* Docker Desktop with Docker Compose
* Git

### 1. Clone the repository

```bash
git clone https://github.com/junior-2912/task-manager-api.git
cd task-manager-api
```

### 2. Configure the database

The project includes a `compose.yaml` file for running PostgreSQL with Docker Compose.

Create a `.env` file in the project root with the database variables expected by the Compose configuration:

```dotenv
POSTGRES_DB=library
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres
```

These are example values for local development. Change them as appropriate for your environment.

Start the database:

```bash
docker compose up -d
```

Ensure the Spring Boot application is configured to connect to the PostgreSQL instance on port `5432`.

### 3. Run the application

On Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

On Linux or macOS:

```bash
./mvnw spring-boot:run
```

The API should be available at:

`http://localhost:8080`

The database schema must be initialized and consistent with the JPA entities before the application starts, as Hibernate is configured to validate the schema.

## Running Tests

On Windows:

```powershell
.\mvnw.cmd test
```

On Linux or macOS:

```bash
./mvnw test
```

## Future Improvements

* Expand unit test coverage for service business rules.
* Add tests for filtering, pagination and repository specifications.
* Improve API documentation and automated endpoint testing.

## Related Project

Frontend: [junior-2912/taskmanager-frontend](https://github.com/junior-2912/taskmanager-frontend)

## Author

Developed by Junior (https://github.com/junior-2912) as a personal learning project focused on Java backend development, Spring Boot and relational database integration.


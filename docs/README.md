# Learn by building, not just reading.

We are picking a realistic, end-to-end Spring Boot project that touches almost every major area a senior Java engineer should know:
- architecture
- microservices
- databases
- messaging
- security
- monitoring
- CI/CD
- deployment
---

## Project: “TaskHub” – A Scalable Task Management Platform

Think of it like Trello / Asana Lite, but designed as a real production-grade microservices system.

It’ll cover:
- Spring Boot, JPA, and microservices
- REST + JWT auth
- Kafka (async events)
- Redis (caching)
- PostgreSQL (RDBMS)
- Docker + Kubernetes
- Observability (Prometheus, Grafana, OpenTelemetry)
- CI/CD (GitHub Actions)
- Proper architecture, security, testing, documentation


## Project Overview

### Architecture

We’ll use a modular microservice setup:

| Service              | Responsibility                           | Stack                            |
| -------------------- | ---------------------------------------- | -------------------------------- |
| Auth Service         | User registration, login, JWT, roles     | Spring Boot, Security, JPA       |
| Task Service         | CRUD tasks, assign users, due dates      | Spring Boot, JPA, Kafka producer |
| Notification Service | Async email/Slack notifications          | Spring Boot, Kafka consumer      |
| API Gateway          | Single entry point, routing, auth filter | Spring Cloud Gateway             |
| Config Server        | Centralized configuration                | Spring Cloud Config              |
| Eureka Server        | Service registry/discovery               | Spring Cloud Netflix Eureka      |

Supporting services:
- PostgreSQL – main DB
- Redis – cache layer for sessions, user lookups
- Kafka – async message broker
- Prometheus + Grafana – metrics + dashboard
- Zipkin / OpenTelemetry – distributed tracing


### What we’ll Learn from This Project

| Area                  | Key Learning                                              |
| --------------------- | --------------------------------------------------------- |
| Spring Boot Core      | Profiles, bean lifecycle, configuration, validation       |
| Spring Data JPA       | Entity relationships, transactions, pagination, caching   |
| Spring Security       | JWT, password encoding, role-based access                 |
| Spring Cloud          | Config, Eureka, Gateway                                   |
| Kafka                 | Producer/Consumer, message formats, retries, DLQ          |
| Testing               | Unit + Integration + Contract tests                       |
| Observability         | Micrometer metrics, distributed tracing, logs correlation |
| Docker/K8s            | Multi-container setup, service orchestration              |
| CI/CD                 | GitHub Actions pipeline for test + deploy                 |
| Design & Architecture | Service boundaries, DDD principles, event-driven design   |



## Phase Breakdown (Learning Path)

__Phase 1 — Core Setup (Spring Boot, Auth, JPA)__
- Create auth-service
- Register/Login APIs
- Store users in PostgreSQL
- Encrypt passwords (BCrypt)
- Generate JWT tokens
- Create task-service
- CRUD for tasks (title, description, dueDate, assignedUserId)
- Use JWT to secure endpoints
- Introduce Spring Profiles (dev/test/prod)
- Add Validation + Exception Handling
- Add basic unit + integration tests

> we’ll learn: REST APIs, Security, JPA, validation, exception handling, JWT, testing basics.

__Phase 2 — Asynchronous Communication (Kafka + Notification Service)__
- Add Kafka producer in task-service -> emits event when task created/assigned.
- Add notification-service -> consumes Kafka events, logs or emails notifications.
- Add retries + error handling (DLQ)
- Add Redis caching for recent notifications

> we’ll learn: Event-driven architecture, Kafka setup, producers/consumers, idempotency, retries, caching.

__Phase 3 — Service Discovery & API Gateway__
- Add Eureka Server (Spring Cloud Netflix)
- Add Config Server for centralized config
- Add API Gateway to route `/auth/**, /tasks/**, /notifications/**`
- Configure global filters for JWT verification and logging.

> we’ll learn: Service discovery, centralized config, gateway pattern, filters, load balancing.

__Phase 4 — Observability & Monitoring__
- Add Micrometer metrics to all services.
- Expose /actuator/prometheus endpoints.
- Integrate Prometheus & Grafana dashboards.
- Add distributed tracing (Zipkin / OpenTelemetry).

> we’ll learn: Monitoring, metrics, tracing, performance tuning.

__Phase 5 — Containerization & Deployment__
- Write Dockerfiles for all services.
- Create docker-compose.yml to bring everything up.
- Later, deploy on Minikube / Kubernetes with manifests or Helm.
- Add GitHub Actions pipeline for CI/CD.

> we’ll learn: Docker, orchestration, CI/CD automation, environment management.

__Phase 6 — Advanced Features (Optional but Strongly Recommended)__
- Add Role-based access control (Admin, User)
- Add Rate limiting or throttling in API Gateway.
- Add Audit logging (who changed what and when).
- Add Email or Slack integration for notifications.
- Integrate Flyway for DB migrations.

## Recommended Stack Versions

| Tool         | Recommended Version |
| ------------ | ------------------- |
| Java         | 17 (LTS)            |
| Spring Boot  | 3.3.x               |
| Spring Cloud | 2023.0.x            |
| PostgreSQL   | 15+                 |
| Kafka        | 3.x                 |
| Redis        | 7.x                 |
| Docker       | Latest stable       |
| Prometheus   | Latest              |
| Grafana      | Latest              |


## Development Environment Setup
- Use IntelliJ IDEA Ultimate (preferred for Spring)
- Use Docker Desktop for Kafka, Redis, and PostgreSQL.
- Create a Postman collection for all APIs.
- Version control with GitHub.
- Use Lombok, MapStruct, and OpenAPI (Swagger) for convenience.


## Testing Plan

| Type        | Tools                           | Target                       |
| ----------- | ------------------------------- | ---------------------------- |
| Unit        | JUnit 5, Mockito                | Service layer logic          |
| Integration | @SpringBootTest, TestContainers | DB + Kafka                   |
| Contract    | Pact                            | API Gateway → Services       |
| E2E         | RestAssured                     | End-to-end flows             |
| Performance | Gatling or JMeter               | Load test under real traffic |



## Folder Structure (per microservice)

```
auth-service/
 ├─ src/main/java/com/taskhub/auth
 │   ├─ controller/
 │   ├─ service/
 │   ├─ repository/
 │   ├─ model/
 │   ├─ security/
 │   └─ config/
 ├─ src/test/java/...
 ├─ resources/
 │   ├─ application.yml
 │   └─ db/migration/
 └─ Dockerfile
```

## TODO/Future Scope:

__After Phase 1__
- [ ] Break the Auth and Task into two separate services
- [ ] Transactionality in DB operations (saving the task, updating the task, ...)
- [ ] JWT auth security (what if someone steals the JWT token of a user)
- [ ] Spring profiles (dev/test/prod)
- [ ] Unit + Integration tests
- [ ] Logging
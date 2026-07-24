# 🔗 URL Shortener API

A production-oriented URL Shortener built with **Spring Boot**, **Spring Security (JWT)**, **PostgreSQL**, and **Redis**. The application provides secure URL management with JWT authentication while improving redirect performance using Redis caching.

---

## ✨ Features

- 🔐 JWT Authentication & Authorization
- 👤 User Registration & Login
- 🔗 Generate Short URLs
- ✏️ Custom Alias Support
- ⏰ URL Expiration
- 🚀 Redis Caching for Fast Redirects
- 🗄 PostgreSQL Persistent Storage
- ✅ Bean Validation
- ⚡ Cache-Aside Pattern
- 🏗 Layered Architecture

---

# 🏛 Tech Stack

| Category | Technology |
|----------|------------|
| Language | Java 24 |
| Framework | Spring Boot 4 |
| Security | Spring Security + JWT |
| Database | PostgreSQL |
| Cache | Redis |
| ORM | Spring Data JPA (Hibernate) |
| Build Tool | Maven |
| Validation | Jakarta Validation |
| API Testing | Postman |
| Containerization | Docker |

---

# 📂 Project Structure

```
src
├── Controller
├── Service
├── Repository
├── Entity
├── DTO
├── Security
├── Config
├── Exception
└── Utils
```

---

# 🏗 System Architecture

```text
                           +----------------------+
                           |      Client          |
                           +----------+-----------+
                                      |
                                      |
                             HTTP REST API
                                      |
                                      ▼
                         +------------------------+
                         |   Spring Boot API      |
                         +-----------+------------+
                                     |
         +---------------------------+--------------------------+
         |                           |                          |
         ▼                           ▼                          ▼
 Authentication               URL Service                Redis Cache
(Spring Security)          (Business Logic)          (CachedUrlDto)
         |                           |                          |
         |                           | Cache Miss               |
         |                           +------------+             |
         |                                        |             |
         ▼                                        ▼             |
     JWT Validation                       PostgreSQL            |
                                                 |              |
                                                 +--------------+
                                                        Cache Put
```

---

# 🔄 Redirect Flow

```text
Client
   │
   ▼
GET /abc123
   │
   ▼
Redis Cache
   │
   ├────────────── Cache Hit ──────────────► Return Cached URL
   │
   ▼
Cache Miss
   │
   ▼
PostgreSQL
   │
   ▼
Store in Redis
   │
   ▼
Redirect User
```

---

# 🔐 Authentication Flow

```text
Register
    │
    ▼
Save User
    │
    ▼
BCrypt Password

--------------------------

Login
    │
    ▼
AuthenticationManager
    │
    ▼
Generate JWT
    │
    ▼
Client stores Token
    │
    ▼
Authorization Header
Bearer <JWT>

--------------------------

Protected Endpoint
        │
        ▼
JWT Filter
        │
        ▼
Validate Token
        │
        ▼
Security Context
        │
        ▼
Controller
```

---

# 🚀 Cache Flow

```text
User Clicks Short URL
          │
          ▼
@Cacheable Method
          │
     Is Key Present?
      /          \
     Yes         No
     │            │
     ▼            ▼
Return Cache   Query Database
                    │
                    ▼
           Store CachedUrlDto
                    │
                    ▼
              Return Result
```

---

# 🗄 Database

## Users

- user_id
- username
- email
- password
- role

---

## URLs

- id
- shortUrl
- originalUrl
- status
- expiresAt
- clickCount
- createdAt
- user_id

---

# 📦 Redis

Current Cache Key

```
urlCache::<shortCode>
```

Example

```
urlCache::W4RleO
```

Cached Object

```java
CachedUrlDto
{
    id,
    url,
    status,
    expiresAt
}
```

---

# 🔥 Implemented Endpoints

## Authentication

| Method | Endpoint | Description |
|---------|----------|-------------|
| POST | /register | Register User |
| POST | /authenticate | Login & Generate JWT |

---

## URL

| Method | Endpoint | Description |
|---------|----------|-------------|
| POST | /api/urls | Create Short URL |
| GET | /{shortCode} | Redirect using Short URL |

---

# 🔄 Current Redirect Process

```
Client

↓

RedirectController

↓

getActiveUrlOrThrow()

↓

Redis

↓

Cache Hit?
    │
    ├── Yes → Return DTO
    │
    └── No
          │
          ▼
     PostgreSQL
          │
          ▼
      Cache Result
          │
          ▼
Redirect
```

---

# ⚡ Performance Improvement

Without Cache

```
Every Request

↓

Database Query

↓

Redirect
```

With Redis

```
First Request

↓

Database

↓

Redis

↓

Next Requests

↓

Redis

↓

Redirect
```

---

# 📌 Current Status

## ✅ Completed

- Spring Boot Setup
- PostgreSQL Integration
- JWT Authentication
- Spring Security
- BCrypt Password Encoding
- User Registration
- Login API
- URL Creation
- Custom Alias
- URL Expiration
- Redis Integration
- Redis Cache
- DTO-based Cache
- Docker Redis
- Validation
- Exception Handling

---

# 🚧 Planned Features

## Analytics

- Click Count
- Total Redirects
- Last Access Time
- URL Statistics Endpoint

---

## Cache

- Cache TTL
- Cache Eviction
- Refresh Cache
- Cache Monitoring

---

## URL Management

- Delete URL
- Disable URL
- Update URL
- User Dashboard

---

## Security

- Refresh Tokens
- Logout
- Token Blacklisting
- Rate Limiting

---

## Production Features

- Docker Compose
- Swagger/OpenAPI
- Unit Testing
- Integration Testing
- GitHub Actions CI/CD
- Logging
- Monitoring
- Global Metrics

---

# 🛠 Future Architecture

```text
                   Client
                      │
      ┌───────────────┴───────────────┐
      │                               │
 Authentication                  URL APIs
      │                               │
      ▼                               ▼
 Spring Security              URL Service
      │                               │
      │                    ┌──────────┴───────────┐
      │                    │                      │
      ▼                    ▼                      ▼
 JWT Filter            Redis Cache         PostgreSQL
                            │
                            ▼
                     Click Analytics
                            │
                            ▼
                       Dashboard API
```

---

# 🧪 Running Locally

## Clone

```bash
git clone https://github.com/yourusername/url-shortener.git
```

## PostgreSQL

Create a database

```
url_shortener
```

---

## Redis

```bash
docker run -d \
--name redis-cache \
-p 6379:6379 \
redis
```

---

## Run

```bash
mvn spring-boot:run
```

---

# 👨‍💻 Author

**Ayaan Pathan**

Backend Developer | Spring Boot | Java | PostgreSQL | Redis

---

## ⭐ Future Goal

Transform this project into a production-ready URL Shortener with:

- Analytics Dashboard
- Distributed Caching
- Docker Deployment
- CI/CD
- Monitoring
- Cloud Deployment (AWS)
- High Availability

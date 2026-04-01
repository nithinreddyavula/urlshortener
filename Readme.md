# URL Shortener API

A REST API built with Spring Boot 3 that converts long URLs into short codes and redirects users to the original URL.

## Tech Stack

- Java 17
- Spring Boot 3
- MySQL — stores URL mappings
- Redis — caching layer for fast redirects
- Docker — containerized Redis
- Maven

## Features

- Generate a short code for any long URL
- Redirect short URL to original URL with 302 status
- Redis caching for faster repeated requests
- Data persistence with MySQL

## Project Structure
```
src/main/java/com/nithin/urlshortener/
├── controller/
│   └── UrlController.java
├── model/
│   ├── Url.java
│   └── ShortenRequest.java
├── repository/
│   └── UrlRepository.java
├── service/
│   └── UrlService.java
└── UrlshortenerApplication.java
```

## API Endpoints

| Method | Endpoint | Description | Status Code |
|--------|----------|-------------|-------------|
| POST | /api/url/shorten | Accept long URL, return short code | 201 Created |
| GET | /api/url/{shortCode} | Redirect to original URL | 302 Found |

## How to Run

### Prerequisites
- Java 17
- MySQL
- Docker

### Step 1 — Start Redis using Docker
```bash
docker run -d --name redis -p 6379:6379 redis
```

### Step 2 — Create MySQL database
```sql
CREATE DATABASE urlshortener;
```

### Step 3 — Configure application.properties
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/urlshortener
spring.datasource.username=root
spring.datasource.password=yourpassword
spring.jpa.hibernate.ddl-auto=update
spring.data.redis.host=localhost
spring.data.redis.port=6379
```

### Step 4 — Run the application
```bash
mvn spring-boot:run
```

## API Usage

### Shorten a URL
```
POST /api/url/shorten
Content-Type: application/json

{
    "url": "https://www.google.com"
}
```
Response:
```
a1b2c3d4
```

### Redirect to original URL
```
GET /api/url/a1b2c3d4
```
Response: 302 redirect to https://www.google.com

## Database Schema
```sql
CREATE TABLE url (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    short_code VARCHAR(255) NOT NULL,
    original_url VARCHAR(255) NOT NULL,
    click_count INT DEFAULT 0
);
```

## GitHub

github.com/nithinreddyavula/urlshortener
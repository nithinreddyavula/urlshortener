# URL Shortener — High-Performance Caching Service

> Java · Spring Boot 3 · Redis · MySQL · Docker

A clean URL shortening service built with a Redis Cache-Aside pattern — demonstrating the performance difference between cache hits and database reads with real numbers.

**Performance:**
```
Cache hit   →  < 10ms
DB read     →    80ms
Improvement →    8x faster
```

---

## 🏗️ Architecture

```
Client
  │
  ▼
Spring Boot REST API
  │
  ├── Redirect request (GET /{shortCode})
  │     ├── Check Redis cache
  │     │     ├── HIT  → return long URL immediately (< 10ms)
  │     │     └── MISS → query MySQL → store in Redis → return long URL
  │     │
  │     └── Increment analytics counter
  │
  ├── Shorten URL (POST /api/shorten)
  │     └── Generate Base62 short code → store in MySQL + Redis
  │
  └── MySQL (persistent) · Redis (cache + analytics)
```

---

## ⚙️ Tech stack

| Layer | Technology |
|-------|-----------|
| Backend | Java 17 · Spring Boot 3 · Spring MVC · Spring Data JPA |
| Cache | Redis (Cache-Aside pattern) |
| Database | MySQL 8 |
| Containerisation | Docker · Docker Compose |

---

## 🔑 Key features

- **Cache-Aside pattern** — read from cache first, fall back to DB on miss, populate cache for future hits
- **Base62 encoding** — generates compact alphanumeric short codes (`[a-z][A-Z][0-9]`)
- **Analytics** — tracks redirect counts per short URL
- **Expiry support** — TTL-based URL expiration
- **Clean REST API** — shorten, redirect, analytics, delete

---

## 🚀 Running locally

```bash
# Clone the repo
git clone https://github.com/nithinreddyavula/url-shortener
cd url-shortener

# Start all services
docker-compose up -d

# API available at http://localhost:8080
```

**Prerequisites:** Docker · Docker Compose

---

## 📡 API endpoints

```
POST   /api/shorten            Shorten a URL
         Body: { "url": "https://example.com/very/long/path" }
         Returns: { "shortUrl": "http://localhost:8080/abc123" }

GET    /{shortCode}            Redirect to original URL

GET    /api/analytics/{code}   Get click analytics for a short URL

DELETE /api/{shortCode}        Delete a short URL
```

---

## 🧪 Example

```bash
# Shorten a URL
curl -X POST http://localhost:8080/api/shorten \
  -H "Content-Type: application/json" \
  -d '{"url": "https://github.com/nithinreddyavula/url-shortener"}'

# Response
{
  "shortUrl": "http://localhost:8080/xK9mP2",
  "originalUrl": "https://github.com/nithinreddyavula/url-shortener",
  "expiresAt": "2026-08-28T00:00:00Z"
}

# Use the short URL — redirects instantly from Redis cache
curl -L http://localhost:8080/xK9mP2
```

---

## 📐 Design decisions

**Why Cache-Aside and not Write-Through?**
URL redirects are read-heavy — the same short URL gets accessed many more times than it gets created. Cache-Aside is optimal here: only populate the cache on the first read, let Redis TTL handle expiry automatically.

**Why Base62?**
Base62 (`[a-z][A-Z][0-9]`) generates URL-safe short codes without special characters. 6 characters of Base62 gives 62^6 = ~56 billion unique codes — more than enough.

---

## 📊 Cache performance test

```bash
# First request — cache miss, hits MySQL
time curl http://localhost:8080/xK9mP2
# ~80ms

# Second request — cache hit, served from Redis
time curl http://localhost:8080/xK9mP2
# ~8ms
```

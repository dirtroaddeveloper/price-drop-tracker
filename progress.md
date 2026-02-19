# PRICE DROP TRACKER — PROGRESS LOG
> Seven 30 Games | Java 21 + Spring Boot 3.5.0 + PostgreSQL 18

---

## WHERE WE STOPPED / WHERE TO START NEXT
```
CURRENT STATUS: Week 1 COMPLETE — Week 2 ready to start
NEXT ACTION   : Build Week 2 — JWT is already done, start testing all endpoints then move to deploy
```
**When you come back, read this section first. It will always reflect exactly where to resume.**

### Specifically, next time start here:
- Test login + product CRUD endpoints with curl/Postman (auth works, verify products + alerts too)
- Then begin Week 2 items: input validation edge cases, error handling polish
- Then deploy to Railway

---

## WEEK 1 — THE ENGINE ✅ COMPLETE

| # | Task | Status |
|---|------|--------|
| 1.1 | Environment check (Java 21 ✓, Maven ✓, PostgreSQL 18 already installed ✓) | ✅ Done |
| 1.2 | PostgreSQL DB created (`pricedroptracker` + `tracker` user) | ✅ Done |
| 1.3 | Spring Boot 3.5.0 project generated + Jsoup + JJWT 0.12.6 added | ✅ Done |
| 1.4 | `application.properties` configured (DB, mail placeholder, JWT secret) | ✅ Done |
| 1.5 | JPA Entities: `User`, `Product`, `PriceHistory`, `Alert` | ✅ Done |
| 1.6 | Repository interfaces (all 4, with custom queries) | ✅ Done |
| 1.7 | Flyway migration `V1__init_schema.sql` (all 4 tables + indexes) | ✅ Done |
| 1.8 | `ScraperStrategy` interface + `AmazonScraper` (multi-selector, anti-bot) | ✅ Done |
| 1.9 | `GenericScraper` (fallback) + `ScraperDispatcher` (Strategy Pattern) | ✅ Done |
| 1.10 | `PriceCheckService` — scrapes, stores history, triggers alerts | ✅ Done |
| 1.11 | `PriceCheckScheduler` — @Scheduled every 30 min, staggered batching | ✅ Done |
| 1.12 | `NotificationService` — email alert on price drop, saves Alert record | ✅ Done |
| 1.13 | App boots ✓, DB connects ✓, Flyway migrates ✓, register endpoint returns JWT ✓ | ✅ Done |

---

## WEEK 2 — THE API

| # | Task | Status |
|---|------|--------|
| 2.1 | `JwtTokenProvider` + `JwtAuthFilter` | ✅ Done (built in Week 1) |
| 2.2 | `SecurityConfig` — filter chain, public/protected routes | ✅ Done (built in Week 1) |
| 2.3 | `AuthController` — register, login, refresh | ✅ Done (working) |
| 2.4 | `AuthService` — user creation, password hash, token gen | ✅ Done (working) |
| 2.5 | `ProductController` — full CRUD + manual check trigger | ✅ Done (needs test) |
| 2.6 | `ProductService` — business logic, validation | ✅ Done (needs test) |
| 2.7 | `AlertController` — GET alerts, DELETE alert | ✅ Done (needs test) |
| 2.8 | Price history endpoint with `?days=N` filter | ✅ Done (needs test) |
| 2.9 | DTOs: `AuthRequest`, `ProductRequest`, `PriceHistoryResponse`, `ProductResponse`, `AuthResponse` | ✅ Done |
| 2.10 | `GlobalExceptionHandler` + `ResourceNotFoundException` | ✅ Done |
| 2.11 | Input validation (`@Valid`, `@NotBlank`, `@Email`, `@URL`, `@DecimalMin`) | ✅ Done |
| 2.12 | `WebConfig` — CORS config (localhost:3000, localhost:5173) | ✅ Done |
| 2.13 | Full endpoint smoke test with Postman/curl | ⬜ Pending |
| 2.14 | Deploy to Railway with prod PostgreSQL | ⬜ Pending |

---

## WEEK 3 — THE POLISH

| # | Task | Status |
|---|------|--------|
| 3.1 | Price stats endpoint: min, max, avg, trend | ✅ Done (built in Week 1) |
| 3.2 | `BestBuyScraper` + `GenericScraper` (fallback done) | ⬜ BestBuyScraper pending |
| 3.3 | Browser bookmarklet (JS one-liner) | ⬜ Pending |
| 3.4 | Frontend — React or plain HTML with price chart | ⬜ Pending |
| 3.5 | README with architecture diagram + setup instructions | ⬜ Pending |
| 3.6 | Postman collection exported + documented | ⬜ Pending |

---

## PROJECT STRUCTURE (built)
```
pricetracker/src/main/java/com/seven30games/pricetracker/
├── config/          SecurityConfig, SchedulerConfig, WebConfig
├── controller/      AuthController, ProductController, AlertController
├── service/         AuthService, ProductService, PriceCheckService, NotificationService
├── scraper/         ScraperStrategy (interface), AmazonScraper, GenericScraper, ScraperDispatcher, PriceResult, ScrapingException
├── scheduler/       PriceCheckScheduler
├── model/           User, Product, PriceHistory, Alert
├── repository/      UserRepository, ProductRepository, PriceHistoryRepository, AlertRepository
├── dto/             AuthRequest, AuthResponse, ProductRequest, ProductResponse, PriceHistoryResponse
├── exception/       GlobalExceptionHandler, ResourceNotFoundException
└── security/        JwtTokenProvider, JwtAuthFilter

pricetracker/src/main/resources/
├── application.properties
└── db/migration/V1__init_schema.sql
```

## ENVIRONMENT NOTES
- **Java**: OpenJDK 21.0.5 (Temurin) ✅
- **Maven**: 3.9.12 ✅
- **PostgreSQL**: 18.2 (EDB install at `/Library/PostgreSQL/18`) ✅
  - postgres superuser password: `zxy123123`
  - app DB user: `tracker` / `tracker123`
  - DB name: `pricedroptracker`
- **psql PATH**: Added `/Library/PostgreSQL/18/bin` to `~/.zshrc` ✅
- **Spring Boot**: 3.5.0
- **Project root**: `/Users/rongoodwin/Documents/projects/pricedroptracker/pricetracker/`
- **Base package**: `com.seven30games.pricetracker`
- **Deployment target**: Railway (free tier) — Week 2
- **Key gotcha**: PostgreSQL CHAR(n) maps to `bpchar` type — use VARCHAR in SQL migrations for all string columns

## CONFIRMED WORKING ENDPOINTS
- `POST /api/auth/register` — returns `{accessToken, refreshToken, email}` ✅
- App boots clean, Flyway migrates, DB connects ✅

---

## COMPLETED LOG
- **2026-02-18** — Read design doc, created progress.md, confirmed Java 21 + Maven available
- **2026-02-18** — Discovered PostgreSQL 18 already installed via EDB; set up DB + user
- **2026-02-18** — Added PostgreSQL 18 to PATH in ~/.zshrc
- **2026-02-18** — Generated Spring Boot 3.5.0 project, added Jsoup + JJWT deps
- **2026-02-18** — Built full Week 1 + Week 2 backend (all entities, repos, services, controllers, JWT, security, scraper engine)
- **2026-02-18** — Fixed CHAR(3)/CHAR(64) → VARCHAR in Flyway migration (Hibernate validation strictness)
- **2026-02-18** — App boots ✅, register endpoint returns valid JWT ✅ — Week 1 complete

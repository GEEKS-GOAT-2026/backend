# SESSION CONTEXT LOG (APPEND ONLY)

## Rule (Must Keep)
- This file is append-only.
- Do not edit or delete existing lines.
- Add new entries only at the bottom with a new timestamped section.
- If something is wrong, append a correction note instead of modifying old content.

## Project Snapshot
- Project: Dongne backend for Inha club integration platform.
- Workspace root: backend_repo
- Backend module: backend_repo/dongnea
- Stack: Spring Boot 3.5.14, Java 21, Gradle 8.14.4, PostgreSQL (Docker), Spring Data JPA, springdoc-openapi.
- Architecture direction: domain/global/infra (domain-driven package structure).

## What Was Decided
- Keep domain-driven structure instead of classic controller-service-repository folders by layer.
- Auth strategy for MVP direction: Google OAuth + JWT(access/refresh), with @inha.ac.kr validation.
- Swagger/OpenAPI should be enabled early for FE/BE contract sync.
- Local DB should be managed with Docker Compose and connected by Spring datasource in application.yml.

## Work Completed So Far

### 1) Initial Analysis and Planning
- Reviewed existing backend skeleton and identified that implementation was mostly empty.
- Defined MVP focus with team context: login + infinite-scroll club list.
- Created planning docs and priorities in TODO.

### 2) Build / Dependencies
- Added OpenAPI dependency in Gradle:
  - org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.9
- Build verification completed successfully with:
  - ./gradlew clean build -x test

### 3) Swagger Base Configuration
- Added OpenAPI config class:
  - dongnea/src/main/java/geeks/dongnea/global/config/OpenApiConfig.java
- Added springdoc paths in app config:
  - api docs: /api-docs
  - swagger ui: /swagger
- Current app config file:
  - dongnea/src/main/resources/application.yml

### 4) Domain Skeleton (Bootstrap Endpoints)
- Created simple domain service/controller stubs to verify structure and Swagger exposure.
- User domain:
  - dongnea/src/main/java/geeks/dongnea/domain/user/service/UserService.java
  - dongnea/src/main/java/geeks/dongnea/domain/user/presentation/UserController.java
- Club domain:
  - dongnea/src/main/java/geeks/dongnea/domain/club/service/ClubService.java
  - dongnea/src/main/java/geeks/dongnea/domain/club/presentation/ClubController.java
- Application domain:
  - dongnea/src/main/java/geeks/dongnea/domain/application/service/ApplicationService.java
  - dongnea/src/main/java/geeks/dongnea/domain/application/presentation/ApplicationController.java
- Health endpoints added:
  - GET /api/v1/users/health
  - GET /api/v1/clubs/health
  - GET /api/v1/applications/health

### 5) Docker / DB Connection Troubleshooting History
- Repeated startup failure observed:
  - FATAL: role "admin" does not exist
- Root cause pattern encountered:
  - stale docker volume and mixed container initialization states.
- Eventually DB connection was reported as solved by user.
- Important behavior note:
  - if Postgres role mismatch happens again, recreate container + volume using the current compose file and ensure Spring datasource values exactly match POSTGRES_USER/POSTGRES_PASSWORD/POSTGRES_DB.

## Current Known Good State
- Gradle build passes (without tests):
  - ./gradlew clean build -x test
- Docker compose in use (latest discussed):
  - image: postgres:15
  - POSTGRES_USER: admin
  - POSTGRES_PASSWORD: 1234
  - POSTGRES_DB: dongnea_db
- Spring datasource in application.yml points to localhost:5432, user admin, password 1234, db dongnea_db.
- OpenAPI config class exists and compiles.
- Domain bootstrap controllers/services compile.

## Gaps / Remaining Work
- Runtime verification of actual Swagger page response still needed in a clean run:
  - /swagger
  - /api-docs
- JPA entities/repositories not implemented yet (domain stubs only).
- Auth flow endpoints and JWT implementation not started yet.
- Test setup still needs stabilization for DB-independent CI-like execution (H2 profile or explicit test profile policy).

## Recommended Next Sequence
1. Run backend from module directory only:
   - cd backend_repo/dongnea
   - ./gradlew bootRun
2. Verify API docs and stubs:
   - GET /swagger
   - GET /api-docs
   - GET /api/v1/users/health
3. Implement real entities in order:
   - User -> Club -> ClubManager
4. Add repository + service logic and replace stub responses.
5. Add auth module skeleton (Security config, OAuth callback, JWT issue/reissue/logout).

## Known Pitfalls Recorded
- Running ./gradlew from workspace root (backend_repo) fails because wrapper is inside dongnea.
- Using background runs from wrong cwd created confusing "no such file or directory: ./gradlew" logs.
- Database container can look healthy while role/db mismatch still breaks Spring startup.

## Quick Resume Checklist For Next Session
- Confirm cwd is backend_repo/dongnea before all gradle commands.
- Confirm docker container name and env values from dongnea/docker-compose.yml.
- Start server and verify /swagger and /api-docs first.
- Then continue entity/auth implementation from domain stubs.

---
Entry created: 2026-05-08 (KST)
Author: GitHub Copilot (GPT-5.3-Codex)

## Update Log - 2026-05-14 (KST)
- Updated MVP priority to focus on login, `/api/users/me`, club list infinite scroll, club filtering, and club detail flow.
- Refined `TODO.md` to mark `club list API stabilization` as the current in-progress task.
- Implemented club list filtering in backend:
  - `GET /api/clubs` now supports `category`, `keyword`, and `hasActiveRecruitment`.
  - `ClubRepository` now uses a custom JPQL query for filtered pagination.
  - `ClubService` now normalizes filter input before querying.
  - `ClubController` now passes filter params through to the service.
- Updated `README.md` with the filtered club list API usage example.
- Validation completed:
  - static file checks passed for the modified Java files
  - `./gradlew test` completed successfully

## Update Log - 2026-05-14 (KST) - Test Addition
- Added `ClubServiceTest` under `src/test/java/geeks/dongnea/domain/club/service`.
- Test coverage added for club list filtering behavior:
  - blank filter values are normalized to `null`
  - trimmed filter values are passed through to repository query
  - pagination and sort object are preserved
- Validation re-run completed successfully with `./gradlew test`.


## Update Log - 2026-05-14 (KST) - Test Page Removal & OAuth Handler
- Deprecated the local static OAuth test page `dongnea/oauth-club-test.mjs` and trimmed it to a placeholder. The project no longer recommends using the static test page; instead use the real frontend or integration tests for OAuth validation.
- OAuth handlers updated to use configurable `app.frontend.redirect-uri` and to deliver JWT in query parameter for development verification.
- `README.md` updated to remove references to the test page and document the `?token=<JWT>` redirect contract. Reminder: query-token delivery is for development only; switch to HTTP-only cookies or postMessage flow for production.
- Committed and pushed changes to `main` branch.

# TrailBook

**Every Journey. Every Detail. Every Story.**

Native Android app (Kotlin + Jetpack Compose) with Spring Boot backend for structured experience sharing.

## Project Structure

```
trailbook/
├── android/          # Kotlin + Jetpack Compose multi-module app
├── backend/          # Spring Boot 3 + PostgreSQL API
├── api/              # OpenAPI specification
├── docs/             # Architecture & deployment docs
├── postman/          # API collection
└── .github/workflows/
```

## Prerequisites

- JDK 21
- Android Studio Ladybug or newer
- Docker (local PostgreSQL)
- Maven 3.9+

## Quick Start

### Backend

```bash
cd backend
docker compose up -d
cp .env.example .env   # edit secrets if needed
mvn spring-boot:run
```

API: `http://localhost:8080/api/v1`  
Health: `http://localhost:8080/actuator/health`  
Swagger: `http://localhost:8080/swagger-ui.html`

### Android

1. Open `android/` in Android Studio
2. Copy `android/local.properties.example` → `local.properties` and set `sdk.dir`
3. Run on emulator or device (API 26+)

**Wizard behavior:** Each step auto-saves to Room locally. Tapping **Next** also syncs a server draft when a title is set. Cover/gallery images upload to Cloudinary via the system photo picker.

**Profile:** Shows your published experiences below your bio.

## Demo Content

After migrations, a sample published experience ("Weekend in Kyoto") is available in the public feed.

Register a new account via the app or `POST /api/v1/auth/register` to create experiences and use social features.

## Cloud Deployment

See [docs/deployment.md](docs/deployment.md) for Render + Supabase + Cloudinary setup.

## MVP Features

- Authentication (register, login, session)
- Home feed with pagination
- Explore / search
- Experience detail (structured sections)
- Create / edit experience (9-step wizard)
- Like, comment, bookmark, share
- Profile & bookmarks
- Light / dark theme

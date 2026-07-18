# Deployment Guide

## Supabase PostgreSQL

1. Create a Supabase project
2. Copy the connection string (Session mode / direct)
3. Set `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD` on Render

## Render (Spring Boot)

1. Connect GitHub repository
2. Create Web Service from `backend/Dockerfile`
3. Set environment variables from `backend/.env.example`
4. Deploy — Flyway runs migrations on startup

## Cloudinary

1. Create a Cloudinary account
2. Copy cloud name, API key, API secret to Render env vars
3. Android reads `CLOUDINARY_CLOUD_NAME` from BuildConfig

## Android Production Build

Set in `android/local.properties`:

```
API_BASE_URL=https://your-render-service.onrender.com/api/v1
CLOUDINARY_CLOUD_NAME=your-cloud-name
```

Build release AAB:

```bash
cd android
./gradlew bundleRelease
```

## Firebase (optional MVP)

Add `google-services.json` to `android/app/` for FCM push notifications.

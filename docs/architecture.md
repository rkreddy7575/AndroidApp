# TrailBook Architecture

## Overview

TrailBook uses a feature-first Clean Architecture on Android and a layered Spring Boot backend.

## Android Layers

- **UI**: Jetpack Compose screens + ViewModels exposing `StateFlow`
- **Domain**: Use cases and repository interfaces
- **Data**: Repository implementations, Retrofit APIs, Room DAOs, DataStore

## Backend Layers

- **Controllers**: REST endpoints, validation
- **Services**: Business logic, transactions
- **Repositories**: Spring Data JPA

## Auth Flow

1. User registers/logs in → receives access + refresh JWT
2. Android stores tokens in Encrypted DataStore
3. Retrofit interceptor attaches `Authorization: Bearer <access>`
4. On 401, refresh token flow retries request

## Experience Model

Experiences are structured documents with nested sections (timeline, budget, etc.) stored relationally and returned as a single detail payload.

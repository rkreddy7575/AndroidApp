# TrailBook API Contract

Base URL: `/api/v1`

All responses use:

```json
{ "success": true, "message": null, "data": { } }
```

## Authentication

| Method | Path | Auth |
|--------|------|------|
| POST | `/auth/register` | Public |
| POST | `/auth/login` | Public |
| POST | `/auth/refresh` | Public |
| POST | `/auth/logout` | Bearer |

## Experiences

| Method | Path | Auth |
|--------|------|------|
| GET | `/experiences/feed?page&size` | Public |
| GET | `/experiences/search?q&destination&sort` | Public |
| GET | `/experiences/{id}` | Public (drafts: owner only) |
| POST | `/experiences` | Bearer |
| PUT | `/experiences/{id}` | Bearer (owner) |
| POST | `/experiences/{id}/publish` | Bearer (owner) |
| DELETE | `/experiences/{id}` | Bearer (owner) |

## Social

| Method | Path |
|--------|------|
| POST/DELETE | `/experiences/{id}/like` |
| POST/DELETE | `/experiences/{id}/bookmark` |
| GET/POST | `/experiences/{id}/comments` |

## Users

| Method | Path |
|--------|------|
| GET/PUT | `/users/me` |
| GET | `/users/{id}` |
| GET | `/users/me/bookmarks` |
| GET | `/users/{id}/experiences` |

## Media

| Method | Path |
|--------|------|
| POST | `/media/cloudinary-signature` |

See [api/openapi.yaml](../api/openapi.yaml) for machine-readable spec.

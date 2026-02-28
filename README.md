# Task Tracker

A full-stack web application for tracking tasks and todos. Built with Angular, Spring Boot, and PostgreSQL.

**Live Demo:** [task-web-app-mu.vercel.app](https://task-web-app-mu.vercel.app)


*Note: Render may cold start. Allow up to a minute or two.*

---

## Features

- Frontend: Angular
- Backend: Spring Boot
- Database: PostgreSQL

---

## Deployment

| Service | Provider | URL |
|---------|----------|-----|
| Frontend | Vercel | Auto-deployed on push to `main` |
| Backend | Render | Auto-deployed on push to `main` |
| Database | Neon / H2 | Managed PostgreSQL |

*Note: For demo purposes, we will simply use an in memory db with the create-drop configuration to make use of renders periodic shutdowns to start fresh.*

### Environment Variables (Backend)

Set the following on Render:

```
DATABASE_URL      = jdbc:postgresql://<neon-host>/neondb?sslmode=require&channel_binding=require
DATABASE_USER     = <neon-username>
DATABASE_PASS     = <neon-password>
```
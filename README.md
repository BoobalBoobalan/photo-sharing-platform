# Photo Sharing Platform 📸

A full-stack, enterprise-grade photo sharing application built for photography/event teams to collaboratively upload photos, curate gallery selections, and publish PIN-protected customer galleries without requiring customer account registration.

Built for the **TrizenAI Full-Stack Internship Challenge**.

---

## 🌐 Live Application Links

- **Live Frontend Web Application**: [https://photo-sharing-platform-ri1c.vercel.app](https://photo-sharing-platform-ri1c.vercel.app)
- **Live Backend REST API**: [https://photo-sharing-platform-u4t3.onrender.com](https://photo-sharing-platform-u4t3.onrender.com)
- **Customer Demo PIN Gallery**: [https://photo-sharing-platform-ri1c.vercel.app/gallery/abc123](https://photo-sharing-platform-ri1c.vercel.app/gallery/abc123) (PIN: `482917`)

---

## 🌟 Key Features

- **Multi-Role User Access**:
  - **Admin / Lead**: Create events, assign team members, view all team uploads, select photos, publish galleries with custom PINs & shareable links.
  - **Team Member**: View assigned events, bulk upload photographs with file progress, view personal uploads. Restricted from publishing or managing others' photos.
  - **Customer**: Access published event galleries via shareable link & PIN without registration. Lightbox photo viewer & full resolution download.
- **Object Storage Integration**: Supports AWS S3 SDK v2 with seamless local filesystem fallback for zero-config local development.
- **Dual Database Capability**: Configured for PostgreSQL (Docker / Cloud) with persistent H2 in-memory PostgreSQL compatibility mode fallback.
- **JWT & Role-Based Security**: Secure REST APIs protected by Spring Security, password hashing (BCrypt), and JWT Bearer Tokens.
- **Operational State Monitoring**: Live counters for total uploaded photos vs selected photos for publication.
- **1-Command Docker Deployment**: Ready to launch using `docker compose up --build`.

---

## 🛠 Technology Stack

### Backend
- **Framework**: Java 21 / Spring Boot 3.3.3
- **Security & Auth**: Spring Security 6, JJWT 0.12.5 (JSON Web Tokens)
- **Persistence**: Spring Data JPA / Hibernate ORM
- **Database**: PostgreSQL (Production/Docker) / H2 (Development & Testing)
- **Object Storage**: AWS SDK v2 S3 (`software.amazon.awssdk:s3`) / Local Storage Service
- **Testing**: JUnit 5, Spring Security Test, Spring Boot Test, MockMvc

### Frontend
- **Framework**: React 18 (Vite build tool)
- **Routing**: React Router DOM v6
- **HTTP Client**: Axios (with Request/Response Interceptors)
- **UI Design System**: Vanilla CSS design system (Glassmorphism, CSS Grid, custom themes)
- **Icons**: Lucide React
- **Web Server**: Nginx (Docker) / Vercel (Production Cloud)

---

## 🔑 Demo Credentials

| Role | Email | Password | Description |
| :--- | :--- | :--- | :--- |
| **Admin / Lead** | `admin@photoshare.com` | `Admin@123` | Full administrative control |
| **Team Member** | `team@photoshare.com` | `Team@123` | Photographer assigned to event |
| **Team Member 2** | `sarah@photoshare.com` | `Team@123` | Assistant photographer |
| **Customer Link & PIN** | Link: `/gallery/abc123` | PIN: `482917` | Public access (No login needed) |

---

## 🧪 Testing Verification

Run the full automated JUnit 5 test suite:

```bash
C:\maven\bin\mvn.cmd test -f "e:\Photo Sharing Platform\backend\pom.xml"
```
All 10 integration and unit tests pass 100%.

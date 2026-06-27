<div align="center">

```
██╗     ██╗███████╗███████╗     ██████╗ ███████╗
██║     ██║██╔════╝██╔════╝    ██╔═══██╗██╔════╝
██║     ██║█████╗  █████╗      ██║   ██║███████╗
██║     ██║██╔══╝  ██╔══╝      ██║   ██║╚════██║
███████╗██║██║     ███████╗    ╚██████╔╝███████║
╚══════╝╚═╝╚═╝     ╚══════╝     ╚═════╝ ╚══════╝
                                                  
        ██████╗  █████╗  ██████╗██╗  ██╗███████╗███╗  ██╗██████╗
        ██╔══██╗██╔══██╗██╔════╝██║ ██╔╝██╔════╝████╗ ██║██╔══██╗
        ██████╔╝███████║██║     █████╔╝ █████╗  ██╔██╗██║██║  ██║
        ██╔══██╗██╔══██║██║     ██╔═██╗ ██╔══╝  ██║╚████║██║  ██║
        ██████╔╝██║  ██║╚██████╗██║  ██╗███████╗██║ ╚███║██████╔╝
        ╚═════╝ ╚═╝  ╚═╝ ╚═════╝╚═╝  ╚═╝╚══════╝╚═╝  ╚══╝╚═════╝
```

<br/>

[![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.x-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Latest-316192?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![JWT](https://img.shields.io/badge/JWT-Auth-000000?style=for-the-badge&logo=JSON%20web%20tokens&logoColor=white)](https://jwt.io/)
[![Maven](https://img.shields.io/badge/Maven-Build-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)](https://maven.apache.org/)

[![License](https://img.shields.io/badge/License-Educational-blue?style=flat-square)](https://github.com/Pragatees/Life-OS-Backend)
[![Status](https://img.shields.io/badge/Status-Active_Development-brightgreen?style=flat-square)]()
[![Author](https://img.shields.io/badge/Author-Pragateesh_Hari-purple?style=flat-square)](https://github.com/Pragatees)

</div>

---

<div align="center">

> **"Your entire life, orchestrated through one intelligent backend."**
>
> Life OS is a productivity powerhouse — and this is the engine under the hood.

</div>

---

## 🌌 What is Life OS?

**Life OS Backend** is a robust, secure, and scalable **Spring Boot REST API** designed to power the Life OS productivity platform. Built with modern Java practices, it handles everything from authentication to AI-assisted productivity — helping users take control of their daily life.

```
┌─────────────────────────────────────────────────────────────┐
│                      LIFE OS ECOSYSTEM                      │
│                                                             │
│   [Mobile / Web Client]                                     │
│          │                                                  │
│          ▼                                                  │
│   [Life OS Backend API]  ◄──── JWT ────► [Spring Security]  │
│          │                                                  │
│          ├──────────► [PostgreSQL Database]                 │
│          ├──────────► [Email Service (OTP)]                 │
│          └──────────► [AI Assistant (coming soon)]          │
└─────────────────────────────────────────────────────────────┘
```

---

## ✨ Feature Showcase

<table>
<tr>
<td width="50%">

### 🔐 Authentication Suite
```
✅ User Registration
✅ Secure Login
✅ JWT Token Auth
✅ BCrypt Encryption
✅ Forgot Password Flow
✅ Email OTP Verification
✅ Password Reset
```

</td>
<td width="50%">

### 🛡️ Security Layer
```
✅ Spring Security Integration
✅ JWT-based Authorization
✅ Password Hashing (BCrypt)
✅ Request Validation
✅ Stateless Architecture
```

</td>
</tr>
<tr>
<td width="50%">

### 🗄️ Data Layer
```
✅ PostgreSQL Integration
✅ Spring Data JPA
✅ Hibernate ORM
✅ Entity Relationship Mapping
```

</td>
<td width="50%">

### 🔮 Coming Soon
```
🚧 Task Management APIs
🚧 Dashboard APIs
🚧 User Profile
📅 Google OAuth
📅 Push Notifications
📅 AI Productivity Assistant
📅 Calendar Integration
```

</td>
</tr>
</table>

---

## 🛠️ Tech Stack

| Layer | Technology | Version |
|-------|-----------|---------|
| 🧠 Language | Java | `21` |
| 🌱 Framework | Spring Boot | `3.5.x` |
| 🛡️ Security | Spring Security + JWT (JJWT) | Latest |
| 🗄️ Database | PostgreSQL | Latest |
| 🔗 ORM | Spring Data JPA + Hibernate | Latest |
| 🔨 Build | Apache Maven | Latest |
| 📧 Mailer | Spring Mail | Latest |

---

## 📂 Project Architecture

```
src/
├── main/
│   ├── java/com.lifeos/
│   │   │
│   │   ├── 🔧 config/           ← App & Security Configuration
│   │   ├── 🎮 controller/       ← REST API Endpoints
│   │   ├── 📦 dto/              ← Request & Response Models
│   │   ├── 🏛️  entity/          ← Database Entities
│   │   ├── 🗃️  repository/      ← Data Access Layer (JPA)
│   │   ├── 🔒 security/         ← JWT Filters & Auth Logic
│   │   ├── ⚙️  service/         ← Business Logic
│   │   └── 🧰 util/             ← Helper Utilities
│   │
│   └── resources/
│       └── application.properties
│
└── test/                        ← Unit & Integration Tests
```

---

## 🔐 API Reference

### `POST` `/api/auth/signup` — Register a new user

<details>
<summary>📥 Request Body</summary>

```json
{
  "fullName": "John Doe",
  "username": "john",
  "email": "john@example.com",
  "password": "Password@123"
}
```
</details>

---

### `POST` `/api/auth/login` — Authenticate and receive token

<details>
<summary>📥 Request Body</summary>

```json
{
  "email": "john@example.com",
  "password": "Password@123"
}
```
</details>

<details>
<summary>📤 Response</summary>

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "userId": "550e8400-e29b-41d4-a716",
  "username": "john",
  "fullName": "John Doe",
  "email": "john@example.com"
}
```
</details>

---

### `POST` `/api/auth/forgot-password` — Trigger OTP email

<details>
<summary>📥 Request Body</summary>

```json
{
  "email": "john@example.com"
}
```
</details>

<details>
<summary>📤 Response</summary>

```
If an account exists, a password reset code has been sent.
```
</details>

---

### `POST` `/api/auth/verify-otp` — Validate the OTP

<details>
<summary>📥 Request Body</summary>

```json
{
  "email": "john@example.com",
  "otp": "123456"
}
```
</details>

---

### `POST` `/api/auth/reset-password` — Set a new password

<details>
<summary>📥 Request Body</summary>

```json
{
  "email": "john@example.com",
  "otp": "123456",
  "newPassword": "NewPassword@123"
}
```
</details>

<details>
<summary>📤 Response</summary>

```
Password reset successfully.
```
</details>

---

## 🗄️ Database Schema

```
┌──────────────────────┐       ┌──────────────────────────────┐
│       users          │       │    password_reset_tokens      │
├──────────────────────┤       ├──────────────────────────────┤
│ 🔑 id (UUID)         │──────►│ 🔑 id (UUID)                 │
│ 👤 full_name         │       │ 📧 email                     │
│ 🪪 username          │       │ 🔢 otp                       │
│ 📧 email             │       │ ⏰ expires_at                 │
│ 🔒 password (hash)   │       │ ✅ used                      │
│ 📅 created_at        │       └──────────────────────────────┘
└──────────────────────┘

        [ Coming Soon ]
┌──────────────┐   ┌────────────────┐   ┌──────────────────┐
│    tasks     │   │  user_profiles │   │  notifications   │
├──────────────┤   ├────────────────┤   ├──────────────────┤
│  id, title   │   │  bio, avatar   │   │  type, message   │
│  status, due │   │  preferences   │   │  read, sent_at   │
└──────────────┘   └────────────────┘   └──────────────────┘
```

---

## ⚙️ Environment Setup

Create a `.env` file in the project root:

```env
# ─── Database ─────────────────────────────────────────
SPRING_DATASOURCE_URL=your_db_url
SPRING_DATASOURCE_USERNAME=your_db_user
SPRING_DATASOURCE_PASSWORD=your_db_password

# ─── JWT ──────────────────────────────────────────────
JWT_SECRET=your_super_secret_key_minimum_256_bits
JWT_EXPIRATION=604800000        # 7 days in milliseconds

# ─── Mail ─────────────────────────────────────────────
MAIL_USERNAME=your_email@gmail.com
MAIL_PASSWORD=your_app_password
```

> ⚠️ **Never commit your `.env` file or secrets to version control.**
> Add `.env` to your `.gitignore` immediately.

---

## ▶️ Getting Started

```bash
# 1️⃣  Clone the repository
git clone https://github.com/Pragatees/Life-OS-Backend.git

# 2️⃣  Navigate into the project
cd Life-OS-Backend

# 3️⃣  Configure your .env file (see above)

# 4️⃣  Build the project
./mvnw clean install

# 5️⃣  Launch 🚀
./mvnw spring-boot:run
```

```
  ✅  Server running at → http://localhost:8080
  ✅  API base path    → http://localhost:8080/api
```

---

## 📊 Development Progress

```
Authentication      ████████████████████  100% ✅
Security Layer      ████████████████████  100% ✅
PostgreSQL Setup    ████████████████████  100% ✅
Task Management     ██████░░░░░░░░░░░░░░   30% 🚧
Dashboard APIs      ████░░░░░░░░░░░░░░░░   20% 🚧
User Profiles       ████░░░░░░░░░░░░░░░░   20% 🚧
Google OAuth        ░░░░░░░░░░░░░░░░░░░░    0% 📅
Push Notifications  ░░░░░░░░░░░░░░░░░░░░    0% 📅
AI Assistant        ░░░░░░░░░░░░░░░░░░░░    0% 📅
```

---

## 🤝 Contributing

Contributions are what make the open-source community thrive. Any contributions you make are **greatly appreciated**.

```
1. 🍴  Fork the repository
2. 🌿  Create your feature branch  →  git checkout -b feature/AmazingFeature
3. 💾  Commit your changes         →  git commit -m 'Add some AmazingFeature'
4. 📤  Push to the branch          →  git push origin feature/AmazingFeature
5. 🔃  Open a Pull Request
```

---

## 👨‍💻 Author

<div align="center">

```
╔══════════════════════════════════════╗
║          PRAGATEESH HARI             ║
║  B.Tech — AI & Data Science          ║
║  Sri Eshwar College of Engineering   ║
╚══════════════════════════════════════╝
```

[![GitHub](https://img.shields.io/badge/GitHub-Pragatees-181717?style=for-the-badge&logo=github&logoColor=white)](https://github.com/Pragatees)

</div>

---

## 📄 License

This project is developed for **learning, portfolio, and educational purposes**.

---

<div align="center">

```
  ___________________________
 |                           |
 |   Built with ❤️ and ☕    |
 |   by Pragateesh Hari      |
 |___________________________|
```

⭐ **Star this repo** if you found it useful — it means a lot!

[![Stars](https://img.shields.io/github/stars/Pragatees/Life-OS-Backend?style=social)](https://github.com/Pragatees/Life-OS-Backend/stargazers)

</div>

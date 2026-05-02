# Smart Campus Events

A Spring Boot web application for managing campus events with student self-registration and confirmation emails.

---

## ✉️ Email Setup (Gmail)

Students receive a confirmation email when they register. To enable this:

### 1. Create a Gmail App Password
1. Go to [myaccount.google.com](https://myaccount.google.com)
2. **Security → 2-Step Verification** (enable if not already)
3. **Security → App Passwords**
4. Select app: **Mail**, device: **Other** → type `SmartCampus` → click Generate
5. Copy the **16-character code** shown

### 2. Set environment variables

**Locally (Windows):**
```
set MAIL_USERNAME=youraddress@gmail.com
set MAIL_PASSWORD=xxxx xxxx xxxx xxxx
mvn spring-boot:run
```

**Locally (Mac/Linux):**
```bash
export MAIL_USERNAME=youraddress@gmail.com
export MAIL_PASSWORD="xxxx xxxx xxxx xxxx"
mvn spring-boot:run
```

> If `MAIL_USERNAME` or `MAIL_PASSWORD` is not set, emails are silently skipped — registration still works normally.

---

## 🚀 Deploy to Railway (Free)

### Step 1 — Push to GitHub
```bash
git init
git add .
git commit -m "Initial commit"
git remote add origin https://github.com/YOUR_USERNAME/smart-campus-events.git
git push -u origin main
```

### Step 2 — Create Railway project
1. Go to [railway.app](https://railway.app) and sign in with GitHub
2. Click **New Project → Deploy from GitHub repo**
3. Select your `smart-campus-events` repository
4. Railway auto-detects the Java/Maven project and builds it

### Step 3 — Set environment variables on Railway
In your Railway project → **Variables** tab, add:

| Variable | Value |
|---|---|
| `MAIL_USERNAME` | youraddress@gmail.com |
| `MAIL_PASSWORD` | your 16-char app password |
| `SHOW_H2_CONSOLE` | false |

> Railway automatically provides the `PORT` variable — no need to set it.

### Step 4 — Get your URL
Railway assigns a public URL like `https://smart-campus-events-production.up.railway.app`.

---

## 🏃 Run Locally

```bash
mvn spring-boot:run
```
Open: http://localhost:8080

**Admin login:** `admin` / `admin123`

---

## 📦 Tech Stack
- Java 17 + Spring Boot 3.2
- Thymeleaf templates
- Spring Security
- Spring Data JPA + H2 (in-memory)
- Spring Mail (Gmail SMTP)

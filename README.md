# 📔 JournalApp — Production-Ready Spring Boot Application

A full-featured, secure, and scalable **E2EE Journaling REST API** built with **Spring Boot**, **MongoDB**, **Spring Security**, **Redis Caching**, **Apache Kafka**, and **JavaMailSender**. 

This application allows users to securely maintain personal journals with sentiment tracking, automated weekly sentiment digests delivered via Kafka event streaming and email, Redis-backed external weather API caching, and role-based administrative control.

---

## 📑 Table of Contents
- [✨ Key Features](#-key-features)
- [🏗️ System Architecture](#️-system-architecture)
- [🛠️ Tech Stack](#️-tech-stack)
- [📂 Project Structure](#-project-structure)
- [⚙️ Configuration & Setup](#️-configuration--setup)
- [🚀 Getting Started](#-getting-started)
- [📡 API Documentation & Endpoints](#-api-documentation--endpoints)
  - [1. Public Endpoints](#1-public-endpoints)
  - [2. User Endpoints](#2-user-endpoints)
  - [3. Journal Entry Endpoints](#3-journal-entry-endpoints)
  - [4. Admin Endpoints](#4-admin-endpoints)
- [⚡ Asynchronous Kafka Event Pipeline](#-asynchronous-kafka-event-pipeline)
- [⏱️ Schedulers & Caching](#️-schedulers--caching)
- [🔒 Security & Authentication](#-security--authentication)
- [📝 Logging](#-logging)

---

## ✨ Key Features

- **🔐 Robust Authentication & Security**:
  - Stateless HTTP Basic Authentication with **Spring Security**.
  - **BCrypt** password hashing for secure credential storage.
  - Role-Based Access Control (`USER`, `ADMIN`).

- **📖 Journal Management (CRUD)**:
  - Create, read, update, and delete journal entries linked specifically to authenticated users.
  - Automatic timestamping and sentiment tagging (`POSITIVE`, `NEUTRAL`, `NEGATIVE`).

- **📊 Sentiment Tracking & Analytics**:
  - Analyzes journal entries over the last 7 days to calculate user mood trends and sentiment summaries.

- **📨 Event-Driven Kafka Messaging**:
  - Decoupled, asynchronous processing using **Apache Kafka**.
  - Schedulers produce `SentimentData` events to the `weekly-sentiments` topic.
  - Kafka consumer listens, deserializes JSON messages, and triggers email notifications asynchronously.

- **📧 Automated Email Notifications**:
  - Integration with **Spring Boot Starter Mail (SMTP/Gmail)** to send automated weekly mood reports to users.

- **⚡ Redis In-Memory Caching**:
  - Redis cache layer for external Weather API responses with a 300-second TTL to minimize external API rate limits.

- **🌤️ External Weather API Integration**:
  - Fetches real-time weather and temperature insights using `RestTemplate` from Weatherbit API.

- **⏰ Automated Background Schedulers**:
  - `@Scheduled` tasks to trigger sentiment evaluations and refresh in-memory application caches (`AppCache`).

- **📜 Production-Grade Logging**:
  - Logback configuration with console logging and rolling file appenders (`SizeAndTimeBasedRollingPolicy`).

---

## 🏗️ System Architecture

```
                                      +-------------------------+
                                      |   Client / Postman / UI  |
                                      +------------+------------+
                                                   |
                                            (HTTP Basic Auth)
                                                   v
                                      +------------+------------+
                                      |     Spring Security     |
                                      +------------+------------+
                                                   |
                   +-------------------------------+-------------------------------+
                   |                               |                               |
                   v                               v                               v
         +---------+---------+           +---------+---------+           +---------+---------+
         | Public Controller |           |  User / Journal   |           | Admin Controller  |
         |  (Signup, Health) |           |    Controllers    |           | (User mgmt, Cache)|
         +-------------------+           +---------+---------+           +-------------------+
                                                   |
                       +---------------------------+---------------------------+
                       |                           |                           |
                       v                           v                           v
             +---------+---------+       +---------+---------+       +---------+---------+
             |   MongoDB Atlas   |       |    Redis Cache    |       |   External Weather|
             |   (User & Journal)|       | (300s TTL Cache)  |       |        API        |
             +-------------------+       +-------------------+       +-------------------+
                       ^
                       |
             +---------+---------+
             |  UserScheduler    | ----(Produces SentimentData)----> +-------------------+
             | (Every Sun / 30s) |                                   |   Apache Kafka    |
             +-------------------+                                   | weekly-sentiments |
                                                                     +---------+---------+
                                                                               |
                                                                       (Consumes Event)
                                                                               v
                                                                     +---------+---------+
                                                                     | SentimentConsumer |
                                                                     +---------+---------+
                                                                               |
                                                                        (Triggers SMTP)
                                                                               v
                                                                     +---------+---------+
                                                                     |   Gmail SMTP /    |
                                                                     | Email Notification|
                                                                     +-------------------+
```

---

## 🛠️ Tech Stack

| Technology | Purpose |
| :--- | :--- |
| **Java 17** | Core programming language |
| **Spring Boot 2.7.16** | Core framework |
| **Spring Data MongoDB** | NoSQL document database persistence |
| **Spring Security** | Authentication, authorization, BCrypt encryption |
| **Spring Kafka** | Event-driven asynchronous messaging pipeline |
| **Spring Data Redis (Jedis)** | High-performance distributed caching |
| **Spring Starter Mail** | SMTP email dispatch |
| **Lombok** | Boilerplate reduction (Getters, Setters, Builders) |
| **Logback / SLF4J** | Structured logging and rolling file logs |
| **Maven** | Dependency management & build tool |

---

## 📂 Project Structure

```
journalApp/
├── src/
│   ├── main/
│   │   ├── java/net/engineeringdigest/journalApp/
│   │   │   ├── api/response/            # External API response DTOs (WeatherResponse)
│   │   │   ├── cache/                   # In-memory application cache (AppCache)
│   │   │   ├── config/                  # SpringSecurity & RedisConfig
│   │   │   ├── constants/               # Placeholders and constant strings
│   │   │   ├── controller/              # REST Controllers (Public, User, Journal, Admin)
│   │   │   ├── entity/                  # MongoDB Entities (User, JournalEntry, ConfigJournalApp)
│   │   │   ├── enums/                   # Enums (Sentiment: POSITIVE, NEUTRAL, NEGATIVE)
│   │   │   ├── model/                   # Data transfer objects (SentimentData)
│   │   │   ├── repository/              # MongoDB Repositories & Custom Criteria queries
│   │   │   ├── scheduler/               # Scheduled cron jobs (UserScheduler)
│   │   │   ├── service/                 # Business logic, Kafka producer/consumer, Mail, Redis
│   │   │   └── JournalApplication.java  # Main Spring Boot entry point
│   │   └── resources/
│   │       ├── application-dev.yml      # Development environment configurations
│   │       ├── application-prod.yml     # Production configurations
│   │       ├── application.yml          # Active profile switch
│   │       └── logback.xml              # Logging configuration
│   └── test/                            # Unit and integration tests
├── pom.xml                              # Maven dependencies and build configuration
└── README.md
```

---

## ⚙️ Configuration & Setup

Create or update your `src/main/resources/application-dev.yml`:

```yaml
spring:
  # Redis Configuration
  redis:
    url: redis://default:<REDIS_PASSWORD>@<REDIS_HOST>:<REDIS_PORT>

  # MongoDB Configuration
  data:
    mongodb:
      uri: mongodb+srv://<USERNAME>:<PASSWORD>@<CLUSTER_URL>/journaldb
      database: journaldb
      auto-index-creation: true

  # Email Configuration (SMTP)
  mail:
    host: smtp.gmail.com
    port: 587
    username: your-email@gmail.com
    password: your-app-password # Use Google App Password
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true

  # Kafka Configuration
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: weekly-sentiment-group
      auto-offset-reset: earliest
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
      properties:
        spring.json.trusted.packages: "*"
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer

server:
  port: 8080
  servlet:
    context-path: /journals

weather:
  api:
    key: your_weatherbit_api_key
```

In `src/main/resources/application.yml`:
```yaml
spring:
  profiles:
    active: dev
```

---

## 🚀 Getting Started

### 1. Prerequisites
- **JDK 17** installed and configured (`JAVA_HOME`).
- **MongoDB** (Local instance or MongoDB Atlas cluster).
- **Redis** (Local instance or Redis Cloud).
- **Apache Kafka** running locally on port `9092` (with Zookeeper / KRaft).

### 2. Start Kafka Broker (Local)
```bash
# Start Zookeeper
bin/zookeeper-server-start.sh config/zookeeper.properties

# Start Kafka Server
bin/kafka-server-start.sh config/server.properties

# Create Topic
bin/kafka-topics.sh --create --topic weekly-sentiments --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
```

### 3. Build & Run Application
```bash
# Clone the repository
git clone https://github.com/dheerajkr8287/JournalApp.git

# Navigate to project directory
cd JournalApp

# Run with Maven
./mvnw spring-boot:run
```

The application will start at: `http://localhost:8080/journals`

---

## 📡 API Documentation & Endpoints

> **Note**: Base URL context is `/journals`. Protected endpoints require **HTTP Basic Authentication** headers (`username` & `password`).

### 1. Public Endpoints
No authentication required.

| Method | Endpoint | Description | Request Body |
| :--- | :--- | :--- | :--- |
| `GET` | `/journals/public/health-check` | Server health check | None |
| `POST` | `/journals/public/create-user` | Register a new user | `User` JSON |

#### Example Request (`POST /public/create-user`):
```json
{
  "userName": "john_doe",
  "password": "mySecurePassword123",
  "email": "john@example.com",
  "sentimentAnalysis": true
}
```

---

### 2. User Endpoints
Requires **Authenticated User**.

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/journals/user` | User personalized greeting with live Weather data (cached via Redis) |
| `PUT` | `/journals/user` | Update authenticated user credentials |
| `DELETE` | `/journals/user` | Delete the authenticated user account and their journal entries |

---

### 3. Journal Entry Endpoints
Requires **Authenticated User** (Access restricted to own entries).

| Method | Endpoint | Description | Request Body |
| :--- | :--- | :--- | :--- |
| `GET` | `/journals/journal` | Get all journal entries of authenticated user | None |
| `POST` | `/journals/journal` | Create a new journal entry | `JournalEntry` JSON |
| `GET` | `/journals/journal/id/{id}` | Get specific journal entry by ObjectId | None |
| `PUT` | `/journals/journal/id/{id}` | Update journal entry by ObjectId | `JournalEntry` JSON |
| `DELETE` | `/journals/journal/id/{id}` | Delete journal entry by ObjectId | None |

#### Example Request (`POST /journal`):
```json
{
  "title": "A Great Productive Day",
  "content": "Completed Kafka integration and tested Redis caching!",
  "sentiment": "POSITIVE"
}
```

---

### 4. Admin Endpoints
Requires **Role: ADMIN**.

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/journals/admin/all-users` | Retrieve all registered users across the system |
| `POST` | `/journals/admin/create-admin-user` | Create a user with `ROLE_ADMIN` permissions |
| `GET` | `/journals/admin/clear-app-cache` | Force-refresh in-memory DB configuration cache |

---

## ⚡ Asynchronous Kafka Event Pipeline

```
[ Scheduled Job ]
       │
       ▼  Finds users with `sentimentAnalysis: true`
[ Calculate Dominant Sentiment (Last 7 Days) ]
       │
       ▼  Builds SentimentData payload
[ KafkaProducerService ] ───▶ Topic: `weekly-sentiments`
                                      │
                                      ▼
                             [ SentimentConsumerService ]
                                      │
                                      ▼
                             [ EmailService (SMTP) ] ───▶ User Inbox
```

1. **`UserScheduler`** queries users who opted in for sentiment analysis (`sentimentAnalysis: true`).
2. Calculates the dominant sentiment for each user based on entries over the past 7 days.
3. Produces a serialized JSON payload `SentimentData` to the Kafka topic `weekly-sentiments`.
4. **`SentimentConsumerService`** listens to the topic, deserializes `SentimentData`, and invokes `EmailService` to send an email report asynchronously.

---

## ⏱️ Schedulers & Caching

### Schedulers (`@EnableScheduling`)
- **Weekly Sentiment Analysis**: Configurable via cron expression in `UserScheduler.java`.
- **AppCache Refresher**: Clears and reloads system properties from `config_journal_app` collection every 10 minutes (`cron = "0 0/10 * ? * *"`).

### Redis Caching
- **Weather API Caching**: Caches weather responses for 300 seconds (`RedisService.set(key, value, 300L)`).
- Subsequent user greeting requests hit Redis cache directly, eliminating redundant HTTP calls to Weatherbit API.

---

## 🔒 Security & Authentication

- **HTTP Basic Authentication**: Enforced via `SpringSecurity` configuration extending `WebSecurityConfigurerAdapter`.
- **Password Protection**: Passwords are encrypted before saving using `BCryptPasswordEncoder`.
- **Stateless Session**: Configured with `SessionCreationPolicy.STATELESS` and CSRF disabled for REST APIs.
- **Role Isolation**:
  - `/public/**` $\rightarrow$ Open to all.
  - `/journal/**`, `/user/**` $\rightarrow$ Requires valid authentication.
  - `/admin/**` $\rightarrow$ Requires authority `ROLE_ADMIN`.

---

## 📝 Logging

Configured via `src/main/resources/logback.xml`:
- **Console Output**: Real-time structured log output.
- **Rolling File Log**: Appends to `journalApp.log` with automatic rolling into `journalApp-yyyy-MM-dd_HH-mm.i.log` (max 10MB per file, 10 days history retention).

# Enterprise Word Processor API

A high-performance, thread-safe Spring Boot microservice designed to process lists of words based on dynamic, user-defined business rules. Built with enterprise-grade architecture, this application features in-memory rate limiting, strictly enforced code coverage, and production-ready containerization.

## 🚀 Features

* **Dynamic Rule Processing:** Update string-filtering and action rules on the fly without restarting the server.
* **Lock-Free Concurrency:** Utilizes `AtomicReference` for thread-safe rule updates, preventing race conditions under high load without the bottleneck of synchronized blocks.
* **API Rate Limiting:** Global IP-based rate limiting using **Bucket4j** to protect the API from spam and abuse (returns `429 Too Many Requests`).
* **Security & Payload Limits:** Custom Servlet Filters and Tomcat configurations automatically reject massive JSON payloads (returns `413 Payload Too Large`), protecting the JVM from OutOfMemory (OOM) attacks.
* **Strict Code Quality:** Integrated **JaCoCo** enforces an 80% instruction and 75% branch coverage minimum. The CI/CD build will automatically fail if standards drop.
* **12-Factor App Ready:** Configuration is fully decoupled from the codebase using Docker Environment variables for seamless deployment across environments.

## 🛠️ Tech Stack

* **Language:** Java 17
* **Framework:** Spring Boot 3.3.0
* **Build Tool:** Maven
* **Rate Limiting:** Bucket4j
* **Testing:** JUnit 5, Mockito, Spring Boot Test
* **Code Coverage:** JaCoCo
* **Containerization:** Docker (Alpine JRE)
* **API Documentation:** Springdoc OpenAPI (Swagger UI)

---

## 💻 Getting Started (Local Development)

### Prerequisites
* Java 17+ installed
* Maven 3.8+ installed
* Docker (for containerization)
* Make (optional, for build automation)

### ⚡ Build Automation (Makefile)
This project includes a `Makefile` to simplify all Maven and Docker operations into single-word commands. To view the interactive menu of all available commands, run:
```bash
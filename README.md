# URL Shortener

## Overview

This project is a URL shortener service built with Java, Spring Boot, WebFlux and MongoDB. It provides functionality to shorten URLs, expand them, and handle expiration. The service is designed to handle high volumes of traffic and to be easily scaled.

## Prerequisites

Before getting started, make sure you have the following installed:

- **Java 21**
- **Docker and Docker Compose**: needed for spring-boot-docker-compose maven package, which will spin up MongoDB when the service is started.

## Setup

**Build and run:**:
   ```bash
   ./mvnw clean install -DskipTests=true
   ./mvnw spring-boot:run
   ```

### Testing

**Unit and integration tests can be run using Maven:**
```bash
./mvnw test
```



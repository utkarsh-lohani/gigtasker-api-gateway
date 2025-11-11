````markdown
# 🚀 GigTasker API Gateway

This service is the **"Front Door"** (or "Switchboard Operator") for the entire GigTasker microservice platform. It is the single, public-facing entry point for all incoming requests from the `gigtasker-ui` (Angular) frontend.

Built on **Spring Cloud Gateway**, this is a fully reactive, non-blocking service designed to handle high-throughput routing. It intelligently routes traffic to the correct downstream microservice (like `user-service` or `task-service`) based on path rules.

---

## ✨ Core Responsibilities

This service has three primary jobs:

1.  **Dynamic Routing & Load Balancing:** It connects to the `service-registry` (Eureka) to discover the real-time locations (IPs and ports) of all other services. It routes traffic using their service names (e.g., `lb://user-service`), providing simple load balancing.

2.  **Global CORS:** It is the *single source of truth* for **CORS** (Cross-Origin Resource Sharing). It adds the `Access-Control-Allow-Origin: http://localhost:4200` header to all responses, which is what allows the browser to let our Angular app talk to our backend.

3.  **Path Rewriting:** It keeps our public API clean and decoupled from our internal service-level APIs. It translates clean external paths (e.g., `/api/users/me`) into the specific internal paths the downstream services expect (e.g., `/api/v1/users/me`).

### 🔒 Security Note
This gateway is currently a **pass-through router**. It **does not** validate JWTs itself. It relies on the downstream services (`user-service`, `task-service`, etc.) to be secure **Resource Servers** and independently validate the `Authorization: Bearer ...` token on every request they receive.

---

## 🛠️ Tech Stack

* **Spring Boot 3**
* **Java 25**
* **Spring Cloud Gateway** (Reactive / WebFlux)
* **Spring Cloud Netflix Eureka Client** (For service discovery)
* **Spring Cloud Config Client** (For centralized configuration)

---

## ⚙️ Configuration

This service is "brainless." Its entire routing table, CORS policy, and server port are configured externally in the **`gigtasker-config`** repository.

On startup, it connects to the `config-server` (using its `bootstrap.yml`) and fetches its properties from:
1.  `application.yml` (for the global Eureka address)
2.  `api-gateway.yml` (for its base routes and CORS config)
3.  `api-gateway-local.yml` (for the `local` profile-specific overrides)
4.  `api-gateway-prod.yml` (for the `prod` for Prod Configuration)

### Example Route (from `api-gateway.yml`)
```yaml
spring:
  cloud:
    gateway:
      server:
        webflux:
          globalcors:
            cors-configurations:
              '[/**]':
                allowedOrigins: "http://localhost:4200"
                # ...
          routes:
            - id: user-service-route
              uri: lb://user-service 
              predicates:
                - Path=/api/users/**
              filters:
                - RewritePath=/api/users(?<segment>/?.*), /api/v1/users$\{segment}
````

-----

## 🚀 How to Run

1.  **Start Dependencies (CRITICAL):**

    * Run `docker-compose up -d` (for Postgres, Rabbit, Keycloak).
    * Start the `config-server`.
    * Start the `service-registry`.

2.  **Run this Service:**
    Once the config and registry are running, you can start this service.

    ```bash
    # From your IDE, run ApiGatewayApplication.java
    # Or, from the command line:
    java -jar target/api-gateway-0.0.1.jar
    ```

This service will start on port **`9090`** (as defined in your `config-repo`). All frontend requests from `http://localhost:4200` should be directed to this port.

```
```
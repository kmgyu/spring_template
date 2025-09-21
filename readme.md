Project Overview and Purpose
This project is a **Spring Boot-based web application backend & REST API template**. It's designed to accelerate development and maintain a consistent structure when starting a new web service or API server. The template leverages the benefits of modern tech stacks while adopting a stable configuration to avoid the instability of frequent upgrades, making it suitable for **long-term maintenance**.

**Key Technologies**: Java 21 (LTS), Spring Boot 3.5.x (LTS), Gradle (Kotlin DSL), MariaDB (Hibernate JPA), Flyway, Spring Security, Thymeleaf

**Core Goal**: Provide a Spring Boot template that remains **stable and compatible** without falling behind modern trends, and is **future-proof** to avoid becoming legacy.

**Usage**: It can be used as a backend for a web app or a pure API server. It provides a foundation to **immediately start development** by pre-implementing essential boilerplate code like user login/signup, common exception handling, and database migration.

-----

## How to Run

### Running in a Development Environment

1.  **Prepare the Database**: Start a MariaDB database locally or using Docker. The default configuration uses a MariaDB instance on `localhost:3307`. For example: `docker run -p 3307:3306 -e MARIADB_ROOT_PASSWORD=pass -e MARIADB_DATABASE=template-board mariadb:latest`. After setting up MariaDB, you need to create a database and a user for the application. Refer to the "DB Migration and Configuration" section for an example. Enter the connection details in `src/main/resources/application-secret.properties`.

2.  **Run the Application**: Execute the application from the project root using the Gradle Wrapper:

<!-- end list -->

```bash
./gradlew bootRun -Dspring.profiles.active=dev,secret
```

This command runs the application with the `dev` and `secret` profiles activated. (The `secret` profile contains sensitive information like the local DB password.) After starting, you can access the application at `http://localhost:8080` in a browser or call the API endpoints with a client. (The default port is 8080 and can be changed in `application.properties` via `server.port`.)

-----

### Running in a Production Environment

1.  **Build and Package**: To create a deployable JAR file, use the following command:

<!-- end list -->

```bash
./gradlew clean build
```

This will generate `build/libs/spring_template-0.0.1-SNAPSHOT.jar` (the version may vary).

2.  **Set Profiles and Run**: Configure your production DB settings and options in `application-prod.properties` and `application-secret.properties`, then run the JAR:

<!-- end list -->

```bash
java -jar spring_template-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod,secret
```

This command starts the application with the `prod` profile (production settings) and `secret` profile (sensitive information). Always use the `prod` profile in a production environment to ensure development-specific settings (H2, DEBUG logs, etc.) are not applied. Logs are output to the console by default; to write them to a file in production, you'll need to add a separate Logback configuration.

**Note**: A Dockerfile for container deployment is planned. Once Docker support is added, instructions for building and running images via `docker build/docker run` will be updated in this README.

-----

## Directory Structure

```
spring_template/
├── build.gradle.kts                      # Gradle build script (Kotlin DSL)
├── src/
│   ├── main/
│   │   ├── java/example/spring_template/
│   │   │   ├── auth/                     # Authentication/authorization classes (UserDetails, services, etc.)
│   │   │   ├── config/                   # Global configuration classes (SecurityConfig, etc.)
│   │   │   ├── playground/               # Example domain package (sample implementation for user, dashboard, comment)
│   │   │   └── SpringTemplateApplication.java  # Main application class
│   │   └── resources/
│   │       ├── application.properties          # Base application settings
│   │       ├── application-dev.properties      # Development profile settings (e.g., local DB, debugging)
│   │       ├── application-prod.properties     # Production profile settings (e.g., real DB info, optimization)
│   │       ├── application-secret.properties   # Separate sensitive settings (DB password, not committed to git)
│   │       ├── db/
│   │       │   ├── migration/                  # Flyway migration SQL file location
│   │       │   ├── V1__init.sql                # Initial schema (e.g., users, posts, comments tables)
│   │       │   └── ...                        
│   │       └── templates/                      # Thymeleaf templates (login, signup HTML pages, etc.)
│   └── test/
│       └── java/example/spring_template/       # Test code (mirrors the main package structure)
│           └── ... (Test classes)
└── README.md (This file) and other Gradle settings, `.gitignore`, `gradlew` script, etc.
```

- `auth/` – Contains authentication and authorization-related code using Spring Security (e.g., `AuthUser` entity, `AuthUserService`, login filters). Related code for future OAuth2 adoption can be added here.
- `config/` – Houses global configuration classes. Currently includes `SecurityConfig`; Web MVC or CORS settings can also be added here.
- `playground/` – A package for sample domain implementation, containing example **bulletin board** functionality. It includes `User`, `Post`, and `Comment` entities and their respective Controller/Service classes. It's recommended to remove this package or rename it to a real domain name for your project.
- `resources/` – Contains `application.properties` (base settings) and profile-specific settings. This path also holds Flyway SQL migration files (`db/migration`) and Thymeleaf templates (`templates`).
- `test` – The test code mirrors the package structure of `src/main/java`. For example, tests for code in `example.spring_template.playground.user` should be placed in the same package path under `src/test`.

-----

## Authentication Method (Current Implementation & Future Plans)

This template implements **session-based login authentication** using Spring Security. The `/api/**` endpoints are protected, and login is handled by a custom filter (`JsonUsernamePasswordAuthFilter`) that processes JSON credentials sent to `/api/login`. Upon successful login, authentication information is stored in an **HTTP session**, and a JSON success message is returned to the client. (`SecurityConfig` also configures `/.logout()` to handle session logout when `/api/logout` is called.) For form-based login, you can use the Thymeleaf login page available at `/playground/login`. (You'll be redirected if you're already logged in.)

Passwords are saved to the DB using **BCrypt hashing**, and user roles are managed by the `role` field in the `AuthUser` entity. There is no initial admin account; you can test user signup via the `/api/signup` endpoint (or `/playground/signup` page). New users are assigned the default **`ROLE_USER`** authority.

The structure is designed for **easy expansion for future OAuth integration**. You can add Spring Security's OAuth2 client functionality to support social logins (Google, Facebook, etc.) or evolve it into a self-hosted **OAuth2/JWT token server** using Spring Authorization Server. For example, to add social login, you'd add OAuth2 login settings to `SecurityConfig` and open the `/oauth2/**` endpoints. To switch to JWT-based authentication, you'd change the session management to `.sessionManagement().sessionCreationPolicy(STATELESS)` and implement a `OncePerRequestFilter` to validate the JWT token in the `Authorization` header. These changes are designed to be straightforward with the current structure (e.g., separate `UserDetails` service, filter chain management).

**In short**: It currently uses form login + sessions but can be flexibly switched to **social login or token-based authentication**.

-----

## DB Migration and Configuration

**Database**: The default database is **MariaDB 10.x** (or compatible MySQL). It uses JPA (Hibernate 6) for data access and **Flyway for schema version control**. Flyway automatically updates the schema to the latest version by executing SQL files from the `classpath:db/migration` path at application startup. The included initial migration (`V1__init.sql`) creates the necessary `users`, `posts`, and `comments` tables for the example domain.

The template sets `spring.jpa.hibernate.ddl-auto=validate`, which causes the application to fail at startup if there's a mismatch between the entity classes and the DB schema. Therefore, **schema changes must be made via Flyway migrations**. (You can temporarily use `ddl-auto=create-drop` during development, but it's recommended to manage all final changes with migration SQL files.)

**Initial DB Setup for Development**: After starting MariaDB, you need to create the database and user for the project once. The following SQL example creates a `template-board` DB and a `template_user` account with permissions:

```sql
CREATE DATABASE IF NOT EXISTS `template-board` 
    DEFAULT CHARACTER SET utf8mb4 
    DEFAULT COLLATE utf8mb4_general_ci;

CREATE USER 'template_user'@'%' IDENTIFIED BY 'template_pass';
GRANT ALL PRIVILEGES ON `template-board`.* TO 'template_user'@'%';

FLUSH PRIVILEGES;
```

Add the DB name, username, and password to the `src/main/resources/application-secret.properties` file, for example:

```properties
spring.datasource.url=jdbc:mariadb://localhost:3307/template-board
spring.datasource.username=template_user
spring.datasource.password=template_pass

# For development convenience:
spring.jpa.show-sql=true               # Print SQL queries to the console
spring.jpa.hibernate.ddl-auto=create-drop  # Initialize schema at app start (for dev)
```

**Warning**: The `application-secret.properties` file contains sensitive information and should be **managed so it's not committed to git** (e.g., by `.gitignore`). In a production environment, this file or environment variables will contain real production DB credentials, and `ddl-auto=create-drop` will not be used.

**Applying Migrations**: Flyway manages versions in order (`V1__...sql`, `V2__...sql`, etc.). When adding a new entity or changing a column, create the next numbered SQL migration file in `src/main/resources/db/migration`. Flyway will automatically apply it on application startup. Filenames follow the convention `V<version>__<description>.sql` and contain DDL or DML queries. By sharing these SQL files, all developers can track the DB schema history, and new team members can get the latest schema by simply starting the application.

-----

## Logging and Profile Management

This project uses Spring Boot's default logging configuration (Logback-based), which outputs logs to the **console** without a separate Logback file. Logging and profile management allow different logging strategies for development vs. production environments. For example, in `dev` profile, you can enable detailed logs like `logging.level.org.hibernate=DEBUG` and show SQL queries for debugging. In `prod`, you'd set most packages to INFO level, logging only necessary warnings and errors. (Currently, `application.properties` sets `logging.level.org.flywaydb=DEBUG` to see migration logs, which is useful during development but should be lowered to INFO in production.)

**Profile Management**: Spring Boot profiles enable a single build artifact to support multiple environments. `application.properties` is loaded by default, and additional settings are applied based on the `spring.profiles.active` setting. This template uses a separate `secret` profile for sensitive values like DB passwords and activates the `dev` or `prod` profiles for development and production, respectively.

For example, when developing, activating `dev` and `secret` profiles applies development settings (`application-dev.properties`) plus common and secret settings. For production deployment, activating `prod` and `secret` applies production settings plus common and secret settings. Profiles can be specified via command-line options (`--spring.profiles.active`) or environment variables, as explained in the "How to Run" section.

**In production**, always use the `prod` profile to avoid unnecessary settings. If needed, a Logback configuration file (`logback-spring.xml`) can be added to output logs to files (e.g., daily file rotation, size limits). During development, the console output and IDE console are sufficient. **Devtools** are also enabled, so the application will automatically restart upon code changes, providing quick feedback.

**Pro Tip**: You can add custom profiles, such as a `staging` profile. Running with `-Dspring.profiles.active=staging,secret` would apply a separate `application-staging.properties` file. Using profile hierarchies or includes can minimize configuration duplication by keeping common settings in the base file and environment-specific differences in separate files.

-----

## Other Notes and Extensibility

**Package Naming and Example Code**: When starting a real project based on this template, it is highly recommended to **change the default package name** (`example.spring_template`) to match your organization or project. Also, the example code in the `playground` package should be removed or replaced with your actual service code. The template code itself is lightweight, so there is little burden in modifying the initial setup.

**Dependency Updates**: The library versions managed by Gradle are set to LTS or the latest stable versions to ensure stability (e.g., Spring Boot 3.5.x). This strategy is effective for long-term projects, but you should update dependencies as security issues arise or new LTS versions are released. Always run all tests locally and refer to migration guides when upgrading.

**Adding New Features**: This template is designed to be **easily extensible**. For example:

- **Monitoring**: Add **Spring Boot Actuator** for health checks, metrics, and environment info.
- **API Documentation**: Integrate **SpringDoc (OpenAPI)** or Swagger UI.
- **Complex Queries**: Use **QueryDSL** for type-safe query building (a commented-out Gradle plugin for QueryDSL is included).
- **Caching**: Connect to **Redis** with Spring Cache.
- **Messaging**: Add **Spring for Kafka**.

**In short**: The template is lightweight, so you can integrate almost any module from the Spring ecosystem without major conflicts.

**Production Deployment Checklist**: Before deploying a service, you need to review several items:

- **HTTPS** and certificate management.
- **CORS settings** (for APIs, define allowed domains).
- Database **connection pool size** and timeout tuning.
- Final log level check (ensure no `DEBUG` logs remain).

The template provides default settings for these (e.g., HikariCP default pool), but you must tune them for your production environment. For security, always follow the latest Spring Security guidelines. If you introduce OAuth2, pay attention to access token validity periods and refresh strategies. When using cookies, ensure you set `HttpOnly`/`SameSite` attributes correctly.
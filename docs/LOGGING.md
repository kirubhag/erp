# Logging configuration and activation

This project uses Log4j2 via Spring Boot's `spring-boot-starter-log4j2`.

Files
- `src/main/resources/log4j2-spring.xml` - default logging configuration used when no active profile-specific file is found.
- `src/main/resources/log4j2-spring-dev.xml` - development configuration (console + dev rolling file), root level = DEBUG.
- `src/main/resources/log4j2-spring-prod.xml` - production configuration (rolling file + console), root level = INFO.

Log file location
- The log file location is configurable via the environment variable `APP_LOG_PATH`.
- If `APP_LOG_PATH` is not set, the default is `/var/log/erp`. The application will attempt to create the directory at startup.

Examples
- Run in development mode (verbose, console + file):

```bash
# Using env var for path, and Spring profile 'dev'
export APP_LOG_PATH=/var/tmp/erp-logs
export SPRING_PROFILES_ACTIVE=dev
./mvnw spring-boot:run
```

- Run in production mode:

```bash
# Production mode using env var and prod profile
export APP_LOG_PATH=/var/log/erp
export SPRING_PROFILES_ACTIVE=prod
./mvnw spring-boot:run
```

CI/CD
- In your CI/CD pipeline or production environment, set the Spring profile to `prod`.
  - Example (Unix-like): `export SPRING_PROFILES_ACTIVE=prod`
  - Prefer providing an absolute path for `APP_LOG_PATH` so logs are written to a controlled location (for example `/var/log/erp` or a mounted volume).

Deployment snippets
- Systemd unit example is in `docs/deployment/erp.service`. It sets `APP_LOG_PATH=/var/log/erp` and `SPRING_PROFILES_ACTIVE=prod`.
- Kubernetes example is in `docs/deployment/erp-deployment-k8s.yaml`. It shows how to set the environment variables and mount `/var/log/erp` as a hostPath (replace with a PVC for production).

Default change
- The default fallback logging directory is `/var/log/erp`. If you prefer a different default, set `APP_LOG_PATH` explicitly in your environment.

Notes
- The application contains a small startup component that attempts to create the configured logs directory before logging starts. If directory creation fails, a message is printed to stderr.
-- The Log4j2 configuration files now use the environment variable `APP_LOG_PATH` with a fallback to `/var/log/erp`.

Runtime validation
To validate access logs locally without changing production settings, run the app with a temporary log path and exercise a few endpoints. Example (one-liner):

```bash
# Make a temporary log dir
export APP_LOG_PATH=$(mktemp -d /tmp/erp-logs-XXXXX)
export SPRING_PROFILES_ACTIVE=dev
# Start the app in background (runs in this shell)
./mvnw -DskipTests spring-boot:run &
APP_PID=$!
echo "Started app PID=$APP_PID, waiting for boot..."
sleep 8
# Hit an endpoint (adjust path to an existing controller)
curl -s -o /dev/null -w "%{http_code}\n" http://localhost:8080/ || true
# Show last access log lines
ls -l $APP_LOG_PATH
tail -n 40 $APP_LOG_PATH/access-log-$(date +%d-%m-%Y).log || true

# When done, stop the app
kill $APP_PID || true
```

If you'd like, I can:
- Create environment-specific example files for systemd or Kubernetes manifests showing how to set the env var.
- Change the default to another absolute path like `/var/log/erp` if that suits your deployment environment.

Database migrations and compatibility
----------------------------------

Important: some Flyway migration scripts (for example `V999__add_audit_fields_and_custom_tables.sql`) previously used non-portable DDL like `ADD COLUMN IF NOT EXISTS` which can fail on older MySQL versions.

What we changed:
- The V999 migration has been rewritten to use information_schema checks + dynamic ALTER statements so it is safe across MySQL versions (the new file is `src/main/resources/db/migration/V999__add_audit_fields_and_custom_tables.sql`).

Local development recommendations:
- By default the application has Flyway disabled in `application.properties` to avoid applying production migrations accidentally during local runs.
- To run migrations locally (recommended only when your local DB matches production engine/version): set `SPRING_PROFILES_ACTIVE=prod` and point your DB env vars to a compatible MySQL instance.

CI guidance:
- A GitHub Actions workflow has been added at `.github/workflows/integration-mysql.yml` which starts a MySQL 8.0 service and runs integration tests with the `prod` profile. This provides a safe CI gate for migrations.

If you want I can:
- Add a small `docker-compose` file used by developers to bootstrap a MySQL instance with the expected version for local testing.
- Expand the CI workflow to publish artifacts or add matrix builds for multiple MySQL versions.

# Simplified Dockerfile using pre-built WAR file
# Use Eclipse Temurin JRE image
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy the pre-built WAR file
COPY target/erp-0.0.1-SNAPSHOT.war app.war

# Expose port 8080
EXPOSE 8080

# Run the application with docker profile and override port to 8080
CMD ["java", "-Dspring.profiles.active=docker", "-Dserver.port=8080", "-jar", "app.war"]

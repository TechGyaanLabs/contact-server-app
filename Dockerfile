# Alternative Dockerfile with explicit main class configuration
FROM maven:3.9.5-eclipse-temurin-21 AS build
WORKDIR /app

# Copy dependencies first for better caching
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source and build
COPY . .
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Install curl for health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Copy the JAR file
COPY --from=build /app/target/contact-server-app-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Start the application with explicit main class
ENTRYPOINT ["java", "-cp", "app.jar", "com.careerit.cbook.ContactServerAppApplication"]

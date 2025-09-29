# Multi-stage build for optimized image size
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
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
# Use a base image with Maven and Java 21
FROM maven:3.9.5-eclipse-temurin-21 AS build

# Set the working directory for the build
WORKDIR /app

# Copy the pom.xml and dependencies first to leverage Docker cache
COPY pom.xml .

# Download dependencies
RUN mvn dependency:go-offline -B

# Copy the entire project
COPY . .

# Build the project
RUN mvn clean install -DskipTests

# -------------------------------------------
# Stage 2: Create a smaller runtime image
# -------------------------------------------
FROM eclipse-temurin:21-jdk-jammy

# Set the working directory for the application
WORKDIR /contact-server-app

# Copy the built JAR file from the build stage
COPY --from=build /app/target/contact-server-app.jar contact-server-app.jar

# Expose the default Spring Boot port
EXPOSE 8080

# Set the command to run the application
ENTRYPOINT ["java", "-jar", "contact-server-app.jar"]
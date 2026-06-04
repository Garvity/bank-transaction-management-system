# Stage 1: Build the application
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder
WORKDIR /app

# Copy pom.xml and cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy src directory and build package
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Production JRE runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Create a non-root system group and user for container security compliance
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy built artifact from the builder stage
COPY --from=builder /app/target/bank-management-0.0.1-SNAPSHOT.jar app.jar

# Expose default Spring Boot port
EXPOSE 8080

# Configure default profile to dev (H2 in-memory sandbox)
ENV SPRING_PROFILES_ACTIVE=dev

# Run execution
ENTRYPOINT ["java", "-jar", "app.jar"]

# --- Build Stage ---
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Copy gradle files
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Grant execution rights to gradlew
RUN chmod +x gradlew

# Download dependencies (caching layer)
RUN ./gradlew dependencies --no-daemon

# Copy source code
COPY src src

# Build executable jar
RUN ./gradlew bootJar -x test --no-daemon

# --- Runtime Stage ---
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy built jar from builder stage
COPY --from=build /app/build/libs/*.jar app.jar

# Expose server port
EXPOSE 8080

# Run JVM server
ENTRYPOINT ["java", "-jar", "app.jar"]

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

# Create writeable data directory for H2 database
RUN mkdir -p /app/data && chmod 777 /app/data

# Copy built executable jar specifically (avoiding plain jar conflict)
COPY --from=build /app/build/libs/demo-0.0.1-SNAPSHOT.jar app.jar

# Expose server port
EXPOSE 8080

# Run JVM server with memory optimization constraints for 512MB RAM environments
ENTRYPOINT ["java", "-XX:+UseSerialGC", "-Xss256k", "-Xms128m", "-Xmx256m", "-jar", "app.jar"]

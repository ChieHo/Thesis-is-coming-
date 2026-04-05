# Stage 1: Build
FROM gradle:8.4-jdk21 AS build
WORKDIR /app

# Copy only necessary files for caching dependencies
COPY build.gradle settings.gradle gradle.* ./
COPY gradle ./gradle
RUN gradle --version

# Copy source code
COPY . ./


# Build the application
RUN gradle clean bootJar --no-daemon

# Stage 2: Run
FROM eclipse-temurin:21-jdk

WORKDIR /app

# Copy the bootJar explicitly
COPY --from=build /app/build/libs/*-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]


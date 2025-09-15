# Build stage
FROM gradle:9.0.0-jdk21-alpine AS build
WORKDIR /ui/
COPY --chown=gradle:gradle . .
RUN gradle assemble --no-daemon

# Run the application (using the JRE, not the JDK)
FROM amazoncorretto:21-alpine-jdk AS ui
COPY --from=build /ui/build/libs/*.jar app.jar
CMD ["java", "-jar", "/app.jar"]

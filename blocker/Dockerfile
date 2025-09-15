# Build stage
FROM gradle:9.0.0-jdk21-alpine AS build
WORKDIR /blocker/
COPY --chown=gradle:gradle . .
RUN gradle assemble --no-daemon

# Run the application (using the JRE, not the JDK)
FROM amazoncorretto:21-alpine-jdk AS blocker
COPY --from=build /blocker/build/libs/*.jar app.jar
CMD ["java", "-jar", "/app.jar"]

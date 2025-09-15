# Build stage
FROM gradle:9.0.0-jdk21-alpine AS build
WORKDIR /exchange_generator/
COPY --chown=gradle:gradle . .
RUN gradle assemble --no-daemon

# Run the application (using the JRE, not the JDK)
FROM amazoncorretto:21-alpine-jdk AS exchange_generator
COPY --from=build /exchange_generator/build/libs/*.jar app.jar
CMD ["java", "-jar", "/app.jar"]

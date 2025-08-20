# Build stage
FROM gradle:9.0.0-jdk21-alpine AS build
WORKDIR /bankapp/
COPY --chown=gradle:gradle . .
RUN gradle assemble --no-daemon

# Run the application (using the JRE, not the JDK)
FROM amazoncorretto:21-alpine-jdk AS ui
COPY --from=build /bankapp/ui/build/libs/*.jar app.jar
CMD ["java", "-jar", "/app.jar"]

# Run the application (using the JRE, not the JDK)
FROM amazoncorretto:21-alpine-jdk AS accounts
COPY --from=build /bankapp/accounts/build/libs/*.jar app.jar
CMD ["java", "-jar", "/app.jar"]

# Run the application (using the JRE, not the JDK)
FROM amazoncorretto:21-alpine-jdk AS gateway
COPY --from=build /bankapp/gateway/build/libs/*.jar app.jar
CMD ["java", "-jar", "/app.jar"]

# Run the application (using the JRE, not the JDK)
FROM amazoncorretto:21-alpine-jdk AS transfer
COPY --from=build /bankapp/transfer/build/libs/*.jar app.jar
CMD ["java", "-jar", "/app.jar"]

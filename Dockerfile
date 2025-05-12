FROM maven:3.9.8-eclipse-temurin-21 AS build

COPY src /app/src
COPY pom.xml /app
WORKDIR /app

# Disable tests to prevent database dependency during build
RUN mvn clean install -U -DskipTests

# Build the application and run it with the production profile
FROM openjdk:21
COPY --from=build /app/target/common-0.0.1-SNAPSHOT.jar /app/app.jar
COPY src/main/resources/application-prod.properties /app/application-prod.properties

WORKDIR /app

EXPOSE 8080

# Run the application with the prod profile
CMD ["java", "-jar", "app.jar", "--spring.config.location=/app/application-prod.properties"]

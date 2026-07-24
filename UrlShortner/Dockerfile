# ---- Build stage ----
FROM maven:3.9-eclipse-temurin-24 AS build
WORKDIR /app

# Copy only pom.xml first — lets Docker cache dependencies
# separately from source code, so re-builds are much faster
# when you only change Java files, not dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests -B

# ---- Run stage ----
FROM eclipse-temurin:24-jre-alpine
WORKDIR /app

# Copy only the built jar from the build stage — none of the
# Maven cache, source code, or build tools end up in the final image
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
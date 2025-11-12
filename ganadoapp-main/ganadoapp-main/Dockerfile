# Simple Dockerfile: expects jar already built locally with mvnw.cmd clean package
# This avoids compiling inside Docker (which can be slow/problematic with OneDrive)
FROM eclipse-temurin:17-jre-focal

WORKDIR /app

# Copy the pre-built JAR from local target/ folder
# Make sure to run: mvnw.cmd clean package -DskipTests before docker compose up
COPY target/ganadoapp-main-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
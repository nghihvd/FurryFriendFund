# Stage 1: Build the application with Maven
FROM openjdk:19-jdk AS build

# Set working directory
WORKDIR /app

# Copy the pom.xml and source files (to leverage Docker layer caching)
COPY pom.xml .
COPY src/ src/

# Copy the Maven wrapper and grant execution permissions
COPY mvnw .
COPY .mvn .mvn
RUN chmod +x mvnw

# Build the application (skip tests for faster builds, can be removed in production)
RUN ./mvnw clean package -DskipTests

# Stage 2: Create the final image with only the runtime dependencies
FROM openjdk:19-jdk

# Create a volume for temporary files (optional, depending on your use case)
VOLUME /tmp

# Copy the JAR from the build stage into the final image
COPY --from=build /app/target/*.jar app.jar

# Define the command to run the application
ENTRYPOINT ["java", "-jar", "/app.jar"]

# Expose the port (adjust to your app's actual port if different)
EXPOSE 8081

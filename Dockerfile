# Use an official OpenJDK 17 runtime as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the current directory contents into the container at /app
COPY . .

# Ensure Maven wrapper files have execute permission
RUN chmod +x ./mvnw

# Build the application, skipping tests
RUN ./mvnw clean install -DskipTests

# Run the JAR file
CMD ["java", "-jar", "target/InterniD-0.0.1-SNAPSHOT.jar"]

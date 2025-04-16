# Use an official Java runtime as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the jar file to the container
COPY target/student-survey-service-0.0.1-SNAPSHOT.jar app.jar

# Expose port 8088
EXPOSE 8088

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]

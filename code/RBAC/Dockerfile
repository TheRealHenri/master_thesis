# Use the Maven image for building the app
FROM maven:3.8.1-jdk-11 as build

# Set the working directory in the image
WORKDIR /app

# Copy the pom.xml file into the image
COPY pom.xml .

# Download dependencies
RUN mvn dependency:go-offline -B

# Copy the src code into the image
COPY src/ /app/src/

# Build the application
RUN mvn clean install

# Use OpenJDK base image for running the app
FROM openjdk:11-jre-slim

# Copy the jar file from the build stage
COPY --from=build /app/target/RBAC-1.0-SNAPSHOT-jar-with-dependencies.jar /RBAC-1.0-SNAPSHOT-jar-with-dependencies.jar

# Set the startup command
CMD ["java", "-jar", "/RBAC-1.0-SNAPSHOT-jar-with-dependencies.jar"]
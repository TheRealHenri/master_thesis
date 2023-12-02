# Use the Maven image for building the app
FROM maven:3.8.1-jdk-11 as build

# Set the working directory in the image
WORKDIR /app

# Copy the pom.xml file into the image
COPY pom.xml .

# Download dependencies and provide debugging info
RUN mvn dependency:go-offline -B -X

# Copy the src code into the image
COPY src/ /app/src/

CMD ["mvn", "clean", "install"]
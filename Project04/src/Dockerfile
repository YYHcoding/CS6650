# Use OpenJDK 11 slim image as the base
FROM openjdk:11-jdk-slim

# Set the working directory inside the container
WORKDIR /usr/src/app

# Copy the entire project into the container
COPY . .

# Compile all Java source files
# The -d . option specifies the destination for .class files
# Using find to include all Java files recursively
RUN find . -name "*.java" > sources.txt && javac -d . @sources.txt && rm sources.txt

# Expose necessary ports
# Coordinator Server on port 9999
EXPOSE 9999
# RMI Server ports (adjust as needed)
EXPOSE 1231
EXPOSE 1232
EXPOSE 1233
EXPOSE 1234
EXPOSE 1235

# Set the default command to run the Coordinator Server
# This can be overridden by Docker run commands for RMI Servers or Clients
CMD ["java", "-cp", ".", "server.MyCoordinatorServer", "my-rmi-coordinator"]

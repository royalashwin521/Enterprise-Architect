# 1. Base Image: Use a lightweight JRE (No need for the full JDK in production)
FROM eclipse-temurin:17-jre-alpine

# 2. Security: Create a non-root user (Strict requirement for Kubernetes)
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# 3. Workspace: Set the working directory inside the container
WORKDIR /app

# 4. Artifact: Copying WordProcessor JAR from the target folder
COPY target/wordprocessor-0.0.1-SNAPSHOT.jar app.jar

# 5. Configuration: Set default environment variables
ENV JAVA_OPTS="-Xms256m -Xmx512m"
ENV SERVER_PORT=8080
ENV MAX_POST_SIZE=10240

# 6. Documentation: Document the port the container listens on
EXPOSE ${SERVER_PORT}

# 7. Execution: Run the application with dynamic JVM flags
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -Dserver.port=${SERVER_PORT} -Dserver.tomcat.max-http-post-size=${MAX_POST_SIZE} -jar app.jar"]
======================================================
WORD PROCESSOR API - DEVELOPER COMMANDS CHEAT SHEET
======================================================

🛑 IMPORTANT: RUN LOCATION 🛑
Before running any of the commands below, your terminal MUST
be in the root directory of the project (the 'wordprocessor'
folder that contains the pom.xml and Dockerfile).

Example:
> cd /path/to/your/Enterprise Architect/wordprocessor

======================================================

--- 1. LOCAL DEVELOPMENT ---

Build the application (Compiles, runs tests, enforces coverage, and packages the JAR):
> mvn clean verify

Run the application locally (Starts embedded Tomcat on port 8080):
> mvn spring-boot:run


--- 2. DOCKER PRODUCTION (CONTAINERIZATION) ---

Step 1: Build the Docker Image
(Run this AFTER making code changes to package them into the image)
> docker build -t wordprocessor:1.0.0 .

Step 2: Run the Docker Container
(Runs the app in the background, maps port 8080, and injects memory/security configs)
> docker run -d --name wordprocessor-app -p 8080:8080 -e SERVER_PORT=8080 -e MAX_POST_SIZE=10240 -e JAVA_OPTS="-Xms256m -Xmx512m" wordprocessor:1.0.0

--- 3. DOCKER UTILITIES ---

View the live logs of the running container:
> docker logs -f wordprocessor-app

Stop the running container:
> docker stop wordprocessor-app

Delete the stopped container (so you can run a fresh one):
> docker rm wordprocessor-app
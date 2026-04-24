FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY target/task-worker-1.0.0.jar app.jar

ENTRYPOINT ["java","-jar","/app/app.jar"]
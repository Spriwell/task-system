FROM eclipse-temurin:21-jre
WORKDIR /app
COPY task-api/target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]
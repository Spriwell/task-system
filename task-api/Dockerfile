FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY task-common ./task-common
COPY task-api ./task-api
COPY pom.xml .
RUN cd task-common && ../task-api/mvnw clean install -DskipTests
RUN cd task-api && ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/task-api/target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]
FROM maven:3-eclipse-temurin-21 as build

WORKDIR /code

COPY pom.xml .
RUN mvn verify --fail-never

COPY . .
RUN mvn package -DskipTests

FROM eclipse-temurin:21-jre

COPY --from=build /code/target/spring-boot-resource-server-0.0.1-SNAPSHOT.jar /application.jar
ENTRYPOINT [ "java", "-jar", "/application.jar" ]
FROM maven:3-eclipse-temurin-21 as build

WORKDIR /code

COPY pom.xml .
RUN mvn verify --fail-never

COPY . .
RUN mvn package -DskipTests

FROM eclipse-temurin:21-jre

ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v2.5.0/opentelemetry-javaagent.jar .

COPY --from=build /code/target/oteldemo-0.0.1-SNAPSHOT.jar /application.jar

ENV JAVA_TOOL_OPTIONS "-javaagent:./opentelemetry-javaagent.jar -Dotel.service.name=info-service"
ENTRYPOINT [ "java", "-jar", "/application.jar" ]
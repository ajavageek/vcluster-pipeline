#docker build -t vcluster-pipeline:1.0 .
FROM eclipse-temurin:21-jdk-jammy AS build

COPY .mvn .mvn
COPY mvnw .
COPY pom.xml .
COPY src src

RUN --mount=type=cache,target=/root/.m2,rw ./mvnw package -DskipTests

FROM eclipse-temurin:21-jre-jammy

COPY --from=build target/vcluster-pipeline-1.0.jar vcluster-pipeline.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "vcluster-pipeline.jar"]

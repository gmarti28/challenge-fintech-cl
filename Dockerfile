# Multi-Stage build 
# ------------------------------------------------------------------------
FROM openjdk:11.0.4-jdk-slim as build
WORKDIR /workspace/app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

RUN ./mvnw install -DskipTests
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

# ------------------------------------------------------------------------
FROM openjdk:11.0.4-jre-slim
# currently there is no official jre alpine 11 so a bigger footprint is generated


# Generate multiple layers instead of a single fat jar layer for better usage of registry storage
VOLUME /tmp
ARG DEPENDENCY=/workspace/app/target/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java","-cp","app:app/lib/*","com.gastonmartin.desafio.DesafioApplication"]
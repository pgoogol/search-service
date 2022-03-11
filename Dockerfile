FROM openjdk:11 as build
WORKDIR /workspace/a_master

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

RUN ./mvnw clean package verify
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

FROM openjdk:11
VOLUME /tmp
ENV APP_FILE $ARTIFACT_ID-$VERSION.jar
COPY --from=build /workspace/a_master/target/$APP_FILE /usr/apps
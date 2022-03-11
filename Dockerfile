FROM openjdk:11 as build
WORKDIR /workspace/a_master

COPY pom.xml .
COPY src src

RUN mvn clean package verify
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

FROM openjdk:11
VOLUME /tmp
ENV APP_FILE $ARTIFACT_ID-$VERSION.jar
COPY --from=build /workspace/app/target/$APP_FILE /usr/apps
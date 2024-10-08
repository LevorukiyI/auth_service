FROM maven:3.9.8-amazoncorretto-21 AS build
RUN yum install -y binutils

RUN mkdir /usr/src/project
COPY . /usr/src/project
WORKDIR /usr/src/project
RUN mvn package -DskipTests
RUN jar xf target/auth_service-0.0.1-SNAPSHOT.jar
RUN jdeps --ignore-missing-deps  \
    -q \
    --recursive \
    --multi-release 21 \
    --class-path 'BOOT-INF/lib/*'  \
    --print-module-deps target/auth_service-0.0.1-SNAPSHOT.jar > deps.info
RUN jlink --add-modules $(cat deps.info) \
    --strip-debug \
    --compress=2 \
    --no-header-files \
    --no-man-pages \
    --output /myjre
FROM debian:bookworm-slim as runtime
ENV JAVA_HOME=/user/java/jdk21
ENV PATH=$JAVA_HOME/bin:$PATH
COPY --from=build /myjre $JAVA_HOME
RUN mkdir /project/
COPY --from=build /usr/src/project/target/auth_service-0.0.1-SNAPSHOT.jar /project/
WORKDIR /project
ENTRYPOINT ["java", "-jar", "auth_service-0.0.1-SNAPSHOT.jar"]




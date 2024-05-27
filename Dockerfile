# define base docker image
FROM openjdk:17
LABEL maintainer="QRBATS"
ADD target/qrbats-0.0.1-SNAPSHOT.jar qrbats-docker.jar
ENTRYPOINT ["java", "-jar", "qrbats-docker.jar"]
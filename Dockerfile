# define base docker image
FROM openjdk:17
LABEL maintainer="QRBATS"
ADD target/qrbats-backend.jar qrbats-backend.jar
ENTRYPOINT ["java", "-jar", "qrbats-backend.jar"]
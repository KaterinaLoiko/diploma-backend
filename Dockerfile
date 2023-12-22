FROM openjdk:17-jdk-slim-buster

EXPOSE 8081

ADD target/diploma-backend-0.0.1-SNAPSHOT.jar /app/app.jar
COPY target/classes/application.yaml /app/application.yaml

RUN apt-get update && apt-get -y dist-upgrade
RUN apt install -y netcat

ENTRYPOINT ["java" ,"-Djava.security.egd=file:/dev/./urandom -Dspring.config.location=file:/app/application.yaml", "-jar", "/app/app.jar"]
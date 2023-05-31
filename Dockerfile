FROM openjdk:17-jdk
LABEL authors="denMoskvin"

WORKDIR /app

COPY target/messenger_spring_boot-0.0.1-SNAPSHOT.jar .


CMD ["java", "-jar", "messenger_spring_boot-0.0.1-SNAPSHOT.jar"]
FROM openjdk:17-jdk-slim

EXPOSE 8080

RUN mkdir /app

COPY build/libs/*.jar /app/calculate-server.jar

ENTRYPOINT ["java","-jar","/app/calculate-server.jar"]
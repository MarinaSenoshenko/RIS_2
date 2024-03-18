FROM openjdk:11-jdk-slim AS build
WORKDIR /worker
COPY . /worker

WORKDIR /
ENTRYPOINT ["java","-jar","/worker/build/libs/worker.jar"]
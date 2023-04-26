FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY ./target/postal-items-tracker-0.0.1.jar app/postal-items-tracker.jar
ENTRYPOINT ["java","-jar","app/postal-items-tracker.jar"]
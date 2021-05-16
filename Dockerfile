FROM openjdk:15
COPY target/demo-0.0.1-SNAPSHOT.jar app.jar
COPY ./target/classes/ ./.
ENTRYPOINT ["java","-jar","app.jar"]
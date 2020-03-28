FROM openjdk:8

ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

EXPOSE 8092

ENTRYPOINT ["java","-Dserver.port=8092","-jar","/app.jar"]
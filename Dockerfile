FROM amazoncorretto:17-alpine

ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

ARG SPRING_PROFILES_ACTIVE=local
ENV SPRING_PROFILES_ACTIVE=$SPRING_PROFILES_ACTIVE

ENTRYPOINT ["java", "-jar", "/app.jar"]
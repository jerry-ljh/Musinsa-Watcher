FROM adoptopenjdk/openjdk11:latest

MAINTAINER JunHyeok Lee <j005580@naver.com>

ENV PROPERTIES=applitcation.properties
ENV PROFILE=default

ARG JAR_FILE=*.jar

ADD ${JAR_FILE} app.jar

CMD ["java","-jar", "-Dspring.config.location:/config/${PROPERTIES}", "-Dspring.profiles.active=${PROFILE}", "/app.jar"]

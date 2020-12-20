FROM java:8

MAINTAINER JunHyeok Lee <j005580@naver.com>

ENV PROPERTIES=applitcation.properties
ENV PROFILE=default

ARG JAR_FILE=*.jar

ADD ${JAR_FILE} app.jar

ADD ./deploy/* /deploy/

CMD ["java","-jar", "-Dspring.config.location:/config/${PROPERTIES}", "-Dspring.profiles.active=${PROFILE}", "/app.jar"]

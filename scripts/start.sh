#!/bin/bash

ABSPATH=$(readlink -f $0)
ABSDIR=$(dirname $ABSPATH)
source ${ABSDIR}/profile.sh

REPOSITORY=/home/ubuntu/musinsa-watcher/backend/deploy/
PROJECT_NAME=watcher

echo "> 이전 버전 삭제"

sudo rm $REPOSITORY/watcher*

echo "> Build 파일 복사"

cp $REPOSITORY/zip/*.jar $REPOSITORY/

echo "> 새 애플리케이션 배포"

JAR_NAME=$(ls -tr $REPOSITORY/*.jar | tail -n 1)

echo "> JAR_NAME : $JAR_NAME"

echo "> JAR_NAME에 실행권한 추가"

chmod +x $JAR_NAME

echo "> $JAR_NAME 실행"

IDLE_PROFILE=$(find_idle_profile)

echo "> $JAR_NAME 를  profile=$IDLE_PROFILE 로 실행합니다"

cd $REPOSITORY

nohup java -jar  -Duser.timezone=Asia/Seoul -Dspring.config.location:$REPOSITORY/application-$IDLE_PROFILE.properties -Dspring.profiles.active=$IDLE_PROFILE $JAR_NAME > $REPOSITORY/nohup.out 2>&1 &
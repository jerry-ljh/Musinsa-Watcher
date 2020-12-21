#!/bin/bash

source ~/deploy/profile.sh

IDLE_PROFILE=$(find_idle_profile)

echo "> deploy 폴더 이동"

cd ~/deploy

echo "> docker image 업데이트"

docker-compose pull

echo "> profile=$IDLE_PROFILE로 컨테이너를 실행합니다"
docker-compose up -d $IDLE_PROFILE
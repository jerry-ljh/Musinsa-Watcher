#!/usr/bin/env bash
source ~/deploy/profile.sh
IDLE_PORT=$(find_idle_port)

echo "> $IDLE_PORT 에서 구동 중인 애플리케이션 pid 확인"
IDLE_PID=$(lsof -ti tcp:${IDLE_PORT})

cd ~/deploy

if [ -z ${IDLE_PID} ]
then
  echo "> 현재 구동중인 애플리케이션이 없으므로 종료하지 않습니다."
else
  if [ ${IDLE_PORT} -eq 8081 ]
  then
    echo "> port : $IDLE_PID로 실행중인 real1을 컨테이너 종료합니다."
    #docker-compose kill real1
  elif [ ${IDLE_PORT} -eq 8082 ]
  then
    echo "> port : $IDLE_PID로 실행중인 real2을 컨테이너 종료합니다."
    #docker-compose kill real2
  fi
  sleep 10
fi
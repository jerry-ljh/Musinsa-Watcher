#!/usr/bin/env bash

BACKEND_STATUS=$(curl -s -o /dev/null https://www.musinsa.cf/api/v1/cache/sync -w "%{http_code}")

FRONT_STATUS=$(curl -s -o /dev/null https://www.musinsa.cf -w "%{http_code}")

if [ $BACKEND_STATUS -ne 200 ]
then
 curl -X POST --data-urlencode "payload={\"username\" : \"알림봇\", \"text\" : \"서버 health check fail\"}" https://hooks.slack.com/services/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
elif [ $FRONT_STATUS -ne 200 ]
then
 curl -X POST --data-urlencode "payload={\"username\" : \"알림봇\", \"text\" : \"프론트 서버 health check fail\"}" https://hooks.slack.com/services/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
fi
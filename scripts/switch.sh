#!/usr/bin/env bash
source ~/deploy/profile.sh

function switch_proxy() {
    IDLE_PORT=$(find_idle_port)
    echo "> 전환할 Port: $IDLE_PORT"
    echo "> Port 전환"
    echo "set \$service_url http://127.0.0.1:${IDLE_PORT};" | sudo tee /etc/nginx/conf.d/service-url.inc
    echo "> 엔진엑스 Reload"
    service nginx reload
}
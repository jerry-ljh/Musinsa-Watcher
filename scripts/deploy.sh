#!/usr/bin/env bash
ln -s ~/deploy/nginx.conf /etc/nginx/sites-enabled
bash stop.sh
bash start.sh
bash health.sh

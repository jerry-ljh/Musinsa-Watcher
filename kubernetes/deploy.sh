#!/usr/bin/env bash

cd ~/kubernetes/

CONFIG_FILE=$(echo kubeconfig*)

kubectl rollout restart deployments/watcher-api-dp
#!/usr/bin/env bash

cd ~/kubernetes/

CONFIG_FILE=$(echo kubeconfig*)

kubectl --kubeconfig=$CONFIG_FILE rollout restart deployments/watcher-front-dp
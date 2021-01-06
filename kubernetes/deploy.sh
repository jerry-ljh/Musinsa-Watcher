#!/usr/bin/env bash

cd ~/kubernetes/

CONFIG_FILE=$(echo kubeconfig*)

kubectl --kubeconfig=$CONFIG_FILE rollout restart deployments/api-server1
#!/bin/bash

cd $(dirname "$0")
export HOST_APPLICATION="host.docker.internal"
export HOST_DOCKER_ENGINE="localhost"
docker-compose -p tenpo up -d --build --remove-orphans
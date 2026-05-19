#!/bin/bash

echo "Stopping User Service..."

docker stop user-service
docker rm user-service

echo "User Service stopped and removed"

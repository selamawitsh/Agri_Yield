#!/bin/bash

echo "Starting User Service..."

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "Docker is not running. Please start Docker first."
    exit 1
fi

# Build first
./build.sh

# Run the service
docker run -d \
    --name user-service \
    --network agriyield-network \
    -p 8081:8081 \
    -p 9081:9081 \
    -e SPRING_PROFILES_ACTIVE=docker \
    -e JWT_SECRET="agriyield-platform-secret-key-must-be-256-bits-long-2024!" \
    agriyield/user-service:latest

echo "User Service started on port 8081 (REST) and 9081 (gRPC)"
echo "Check logs: docker logs -f user-service"

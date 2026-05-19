#!/bin/bash

echo "Building User Service..."

# Clean and package
mvn clean package -DskipTests

# Check if build was successful
if [ $? -eq 0 ]; then
    echo "Build successful!"
    
    # Build Docker image
    docker build -t agriyield/user-service:latest .
    
    echo "Docker image built: agriyield/user-service:latest"
else
    echo "Build failed!"
    exit 1
fi

#!/bin/sh
echo "Starting Docker Compose services..."
docker-compose -f eureka-server/docker-compose.yml up -d

echo "Docker Compose services started successfully."
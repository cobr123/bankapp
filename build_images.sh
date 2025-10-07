#!/bin/bash

# Exit immediately if a command exits with a non-zero status.
set -e

docker build -t accounts:latest ./accounts
docker build -t blocker:latest ./blocker
docker build -t cash:latest ./cash
docker build -t exchange:latest ./exchange
docker build -t exchange_generator:latest ./exchange_generator
docker build -t notifications:latest ./notifications
docker build -t transfer:latest ./transfer
docker build -t ui:latest ./ui
docker build -t keycloak:latest ./keycloak
docker build -t kafka:latest ./kafka
docker build -t zipkin:latest ./zipkin
docker build -t prometheus:latest ./prometheus
docker build -t grafana:latest ./grafana

echo "Docker images built successfully!"

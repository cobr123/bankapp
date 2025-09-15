#!/bin/bash

# Exit immediately if a command exits with a non-zero status.
set -e

minikube image load accounts:latest
minikube image load blocker:latest
minikube image load cash:latest
minikube image load exchange:latest
minikube image load exchange_generator:latest
minikube image load notifications:latest
minikube image load transfer:latest
minikube image load ui:latest

echo "Docker images loaded to minikube successfully!"

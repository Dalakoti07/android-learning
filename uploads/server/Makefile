# Makefile for managing the Go server Docker container

# Docker image name
IMAGE_NAME := go-uploads

# Container name
CONTAINER_NAME := go-uploads-instance

# Default target to do everything
all: stop clean-unused build

# Build the Docker image
build:
	@echo "Building Docker image $(IMAGE_NAME)..."
	docker build -t $(IMAGE_NAME) .

# Run the Docker container with memory constraints
run:
	@echo "Running container $(CONTAINER_NAME)..."
	docker run -m 1024m --name $(CONTAINER_NAME) -p 8080:8080 -d $(IMAGE_NAME) || true

# Stop and remove the Docker container
stop:
	@echo "Stopping container $(CONTAINER_NAME) if it exists..."
	docker stop $(CONTAINER_NAME) || true
	docker rm $(CONTAINER_NAME) || true

# Clean up unused Docker images
clean-unused:
	@echo "Cleaning up unused Docker images..."
	docker image prune -f || true

# Makefile PHONY targets
.PHONY: all build run stop clean-unused

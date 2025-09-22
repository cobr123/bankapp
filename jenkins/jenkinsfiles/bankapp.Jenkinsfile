pipeline {
    agent any

    environment {
        IMAGE_TAG       = "latest"
    }

    stages {
        stage('Build & Unit Tests') {
            steps {
                sh 'gradle clean test'
            }
        }

        stage('Build Docker Images') {
            steps {
                sh """
                docker build -t accounts:${IMAGE_TAG} accounts
                docker build -t blocker:${IMAGE_TAG} blocker
                docker build -t cash:${IMAGE_TAG} cash
                docker build -t exchange:${IMAGE_TAG} exchange
                docker build -t exchange_generator:${IMAGE_TAG} exchange_generator
                docker build -t notifications:${IMAGE_TAG} notifications
                docker build -t transfer:${IMAGE_TAG} transfer
                docker build -t ui:${IMAGE_TAG} ui
                docker build -t keycloak:${IMAGE_TAG} keycloak
                """
            }
        }

        stage('Helm Deploy to TEST') {
            steps {
                sh """
                helm dependency build ./helm_charts
                """
                sh """
                helm upgrade --install bankapp ./helm_charts \\
                  --namespace test --create-namespace \\
                  --set image.tag=${IMAGE_TAG}
                """
            }
        }

    }
}
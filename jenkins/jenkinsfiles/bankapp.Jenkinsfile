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
                docker build -t kafka:${IMAGE_TAG} kafka
                docker build -t zipkin:${IMAGE_TAG} zipkin
                docker build -t grafana:${IMAGE_TAG} grafana
                docker build -t prometheus:${IMAGE_TAG} prometheus
                """
            }
        }

        stage('Helm Deploy to TEST') {
            steps {
                sh """
                helm dependency build ./helm_charts/charts/accounts
                helm dependency build ./helm_charts/charts/blocker
                helm dependency build ./helm_charts/charts/cash
                helm dependency build ./helm_charts/charts/exchange
                helm dependency build ./helm_charts/charts/exchange-generator
                helm dependency build ./helm_charts/charts/notifications
                helm dependency build ./helm_charts/charts/transfer
                helm dependency build ./helm_charts/charts/ui
                helm dependency build ./helm_charts/charts/keycloak
                helm dependency build ./helm_charts/charts/kafka
                helm dependency build ./helm_charts/charts/grafana
                helm dependency build ./helm_charts/charts/prometheus
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
pipeline {
    agent any

    environment {
        IMAGE_TAG       = "latest"
    }

    stages {
        stage('Build & Unit Tests') {
            steps {
                sh 'gradle :exchange_generator:clean :exchange_generator:test'
            }
        }

        stage('Build Docker Images') {
            steps {
                sh """
                docker build -t exchange_generator:${IMAGE_TAG} exchange_generator
                """
            }
        }

        stage('Helm Deploy to TEST') {
            steps {
                sh """
                helm dependency build ./helm_charts/charts/exchange-generator
                """
                sh """
                helm upgrade --install exchange-generator ./helm_charts/charts/exchange-generator \\
                  --namespace test --create-namespace \\
                  --set image.tag=${IMAGE_TAG}
                """
            }
        }

    }
}
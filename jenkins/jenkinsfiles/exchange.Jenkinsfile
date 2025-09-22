pipeline {
    agent any

    environment {
        IMAGE_TAG       = "latest"
    }

    stages {
        stage('Build & Unit Tests') {
            steps {
                sh 'gradle :exchange:clean :exchange:test'
            }
        }

        stage('Build Docker Images') {
            steps {
                sh """
                docker build -t exchange:${IMAGE_TAG} exchange
                """
            }
        }

        stage('Helm Deploy to TEST') {
            steps {
                sh """
                helm dependency build ./helm_charts/charts/exchange
                """
                sh """
                helm upgrade --install exchange ./helm_charts/charts/exchange \\
                  --namespace test --create-namespace \\
                  --set image.tag=${IMAGE_TAG}
                """
            }
        }

    }
}
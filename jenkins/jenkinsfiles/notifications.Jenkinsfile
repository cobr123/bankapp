pipeline {
    agent any

    environment {
        IMAGE_TAG       = "latest"
    }

    stages {
        stage('Build & Unit Tests') {
            steps {
                sh 'gradle :notifications:clean :notifications:test'
            }
        }

        stage('Build Docker Images') {
            steps {
                sh """
                docker build -t notifications:${IMAGE_TAG} notifications
                """
            }
        }

        stage('Helm Deploy to TEST') {
            steps {
                sh """
                helm dependency build ./helm_charts/charts/notifications
                """
                sh """
                helm upgrade --install notifications ./helm_charts/charts/notifications \\
                  --namespace test --create-namespace \\
                  --set image.tag=${IMAGE_TAG}
                """
            }
        }

    }
}
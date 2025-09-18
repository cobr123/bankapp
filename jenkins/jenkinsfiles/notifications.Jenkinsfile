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
                helm upgrade --install notifications ./helm_charts/charts/notifications \\
                  --namespace test --create-namespace \\
                  --set image.tag=${IMAGE_TAG}
                """
            }
        }

        stage('Manual Approval for PROD') {
            steps {
                input message: 'Deploy to PROD environment?', ok: 'Yes, deploy'
            }
        }

        stage('Helm Deploy to PROD') {
            steps {
                sh """
                helm upgrade --install notifications ./helm_charts/charts/notifications \\
                  --namespace prod --create-namespace \\
                  --set image.tag=${IMAGE_TAG}
                """
            }
        }
    }
}
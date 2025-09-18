pipeline {
    agent any

    environment {
        IMAGE_TAG       = "latest"
    }

    stages {
        stage('Build & Unit Tests') {
            steps {
                sh 'gradle :transfer:clean :transfer:test'
            }
        }

        stage('Build Docker Images') {
            steps {
                sh """
                docker build -t transfer:${IMAGE_TAG} transfer
                """
            }
        }

        stage('Helm Deploy to TEST') {
            steps {
                sh """
                helm upgrade --install transfer ./helm_charts/charts/transfer \\
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
                helm upgrade --install transfer ./helm_charts/charts/transfer \\
                  --namespace prod --create-namespace \\
                  --set image.tag=${IMAGE_TAG}
                """
            }
        }
    }
}
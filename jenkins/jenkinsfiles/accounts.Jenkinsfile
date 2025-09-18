pipeline {
    agent any

    environment {
        IMAGE_TAG       = "latest"
    }

    stages {
        stage('Build & Unit Tests') {
            steps {
                sh 'gradle :accounts:clean :accounts:test'
            }
        }

        stage('Build Docker Images') {
            steps {
                sh """
                docker build -t accounts:${IMAGE_TAG} accounts
                """
            }
        }

        stage('Helm Deploy to TEST') {
            steps {
                sh """
                helm upgrade --install accounts ./helm_charts/charts/accounts \\
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
                helm upgrade --install accounts ./helm_charts/charts/accounts \\
                  --namespace prod --create-namespace \\
                  --set image.tag=${IMAGE_TAG}
                """
            }
        }
    }
}
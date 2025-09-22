pipeline {
    agent any

    environment {
        IMAGE_TAG       = "latest"
    }

    stages {
        stage('Build & Unit Tests') {
            steps {
                sh 'gradle :ui:clean :ui:test'
            }
        }

        stage('Build Docker Images') {
            steps {
                sh """
                docker build -t ui:${IMAGE_TAG} ui
                """
            }
        }

        stage('Helm Deploy to TEST') {
            steps {
                sh """
                helm dependency build ./helm_charts/charts/ui
                """
                sh """
                helm upgrade --install ui ./helm_charts/charts/ui \\
                  --namespace test --create-namespace \\
                  --set image.tag=${IMAGE_TAG}
                """
            }
        }

    }
}
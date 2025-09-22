pipeline {
    agent any

    environment {
        IMAGE_TAG       = "latest"
    }

    stages {
        stage('Build & Unit Tests') {
            steps {
                sh 'gradle :blocker:clean :blocker:test'
            }
        }

        stage('Build Docker Images') {
            steps {
                sh """
                docker build -t blocker:${IMAGE_TAG} blocker
                """
            }
        }

        stage('Helm Deploy to TEST') {
            steps {
                sh """
                helm dependency build ./helm_charts/charts/blocker
                """
                sh """
                helm upgrade --install blocker ./helm_charts/charts/blocker \\
                  --namespace test --create-namespace \\
                  --set image.tag=${IMAGE_TAG}
                """
            }
        }

    }
}
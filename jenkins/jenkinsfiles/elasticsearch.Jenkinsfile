pipeline {
    agent any

    environment {
        IMAGE_TAG       = "latest"
    }

    stages {
        stage('Build Docker Images') {
            steps {
                sh """
                docker build -t elasticsearch:${IMAGE_TAG} elasticsearch
                """
            }
        }

        stage('Helm Deploy to TEST') {
            steps {
                sh """
                helm dependency build ./helm_charts/charts/elasticsearch
                """
                sh """
                helm upgrade --install elasticsearch ./helm_charts/charts/elasticsearch \\
                  --namespace test --create-namespace \\
                  --set image.tag=${IMAGE_TAG}
                """
            }
        }

    }
}
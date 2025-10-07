pipeline {
    agent any

    environment {
        IMAGE_TAG       = "latest"
    }

    stages {
        stage('Build Docker Images') {
            steps {
                sh """
                docker build -t prometheus:${IMAGE_TAG} prometheus
                """
            }
        }

        stage('Helm Deploy to TEST') {
            steps {
                sh """
                helm dependency build ./helm_charts/charts/prometheus
                """
                sh """
                helm upgrade --install prometheus ./helm_charts/charts/prometheus \\
                  --namespace test --create-namespace \\
                  --set image.tag=${IMAGE_TAG}
                """
            }
        }

    }
}
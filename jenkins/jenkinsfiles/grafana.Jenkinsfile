pipeline {
    agent any

    environment {
        IMAGE_TAG       = "latest"
    }

    stages {
        stage('Build Docker Images') {
            steps {
                sh """
                docker build -t grafana:${IMAGE_TAG} grafana
                """
            }
        }

        stage('Helm Deploy to TEST') {
            steps {
                sh """
                helm dependency build ./helm_charts/charts/grafana
                """
                sh """
                helm upgrade --install grafana ./helm_charts/charts/grafana \\
                  --namespace test --create-namespace \\
                  --set image.tag=${IMAGE_TAG}
                """
            }
        }

    }
}
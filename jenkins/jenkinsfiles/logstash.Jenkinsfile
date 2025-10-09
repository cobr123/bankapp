pipeline {
    agent any

    environment {
        IMAGE_TAG       = "latest"
    }

    stages {
        stage('Build Docker Images') {
            steps {
                sh """
                docker build -t logstash:${IMAGE_TAG} logstash
                """
            }
        }

        stage('Helm Deploy to TEST') {
            steps {
                sh """
                helm dependency build ./helm_charts/charts/logstash
                """
                sh """
                helm upgrade --install logstash ./helm_charts/charts/logstash \\
                  --namespace test --create-namespace \\
                  --set image.tag=${IMAGE_TAG}
                """
            }
        }

    }
}
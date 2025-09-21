pipeline {
    agent any

    environment {
        IMAGE_TAG       = "latest"
    }

    stages {
        stage('Build Docker Images') {
            steps {
                sh """
                docker build -t keycloak:${IMAGE_TAG} keycloak
                """
            }
        }

        stage('Helm Deploy to TEST') {
            steps {
                sh """
                helm dependency build ./helm_charts/charts/keycloak
                """
                sh """
                helm upgrade --install keycloak ./helm_charts/charts/keycloak \\
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
                helm dependency build ./helm_charts/charts/keycloak
                """
                sh """
                helm upgrade --install keycloak ./helm_charts/charts/keycloak \\
                  --namespace prod --create-namespace \\
                  --set image.tag=${IMAGE_TAG}
                """
            }
        }
    }
}
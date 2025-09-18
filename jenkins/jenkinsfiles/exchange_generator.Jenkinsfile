pipeline {
    agent any

    environment {
        IMAGE_TAG       = "latest"
    }

    stages {
        stage('Build & Unit Tests') {
            parallel {
                stage('exchange_generator service') {
                    steps {
                        sh 'gradle :exchange_generator:clean :exchange_generator:test'
                    }
                }
            }
        }

        stage('Build Docker Images') {
            steps {
                sh """
                docker build -t exchange_generator:${IMAGE_TAG} exchange_generator
                """
            }
        }

        stage('Helm Deploy to TEST') {
            steps {
                sh """
                helm upgrade --install exchange_generator ./helm_charts/charts/exchange_generator \\
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
                helm upgrade --install exchange_generator ./helm_charts/charts/exchange_generator \\
                  --namespace prod --create-namespace \\
                  --set image.tag=${IMAGE_TAG}
                """
            }
        }
    }
}
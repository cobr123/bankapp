pipeline {
    agent any

    environment {
        IMAGE_TAG       = "latest"
    }

    stages {
        stage('Build & Unit Tests') {
            parallel {
                stage('blocker service') {
                    steps {
                        sh 'gradle :blocker:clean :blocker:test'
                    }
                }
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
                helm upgrade --install blocker ./helm_charts/charts/blocker \\
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
                helm upgrade --install blocker ./helm_charts/charts/blocker \\
                  --namespace prod --create-namespace \\
                  --set image.tag=${IMAGE_TAG}
                """
            }
        }
    }
}
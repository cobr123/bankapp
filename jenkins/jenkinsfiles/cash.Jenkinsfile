pipeline {
    agent any

    environment {
        IMAGE_TAG       = "latest"
    }

    stages {
        stage('Build & Unit Tests') {
            steps {
                sh 'gradle :cash:clean :cash:test'
            }
        }

        stage('Build Docker Images') {
            steps {
                sh """
                docker build -t cash:${IMAGE_TAG} cash
                """
            }
        }

        stage('Helm Deploy to TEST') {
            steps {
                sh """
                helm dependency build ./helm_charts/charts/cash
                """
                sh """
                helm upgrade --install cash ./helm_charts/charts/cash \\
                  --namespace test --create-namespace \\
                  --set image.tag=${IMAGE_TAG}
                """
            }
        }

//         stage('Manual Approval for PROD') {
//             steps {
//                 input message: 'Deploy to PROD environment?', ok: 'Yes, deploy'
//             }
//         }
//
//         stage('Helm Deploy to PROD') {
//             steps {
//                 sh """
//                 helm dependency build ./helm_charts/charts/cash
//                 """
//                 sh """
//                 helm upgrade --install cash ./helm_charts/charts/cash \\
//                   --namespace prod --create-namespace \\
//                   --set image.tag=${IMAGE_TAG}
//                 """
//             }
//         }
    }
}
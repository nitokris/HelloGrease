pipeline {
    agent any

    environment {
        DOCKER_REGISTRY = "192.168.1.236"
        DOCKER_CREDENTIALS_ID = "docker-harbor-creds"
    }

    stages {
        stage('构建 & 推送镜像') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: "${DOCKER_CREDENTIALS_ID}",
                    usernameVariable: 'USERNAME',
                    passwordVariable: 'PASSWORD'
                )]) {
                    sh """
                        echo \$PASSWORD | docker login ${DOCKER_REGISTRY} -u \$USERNAME --password-stdin

                        docker-compose build

                        docker-compose push

                        docker logout ${DOCKER_REGISTRY}
                    """
                }
            }
        }
    }

    post {
        always {
            cleanWs()
        }
    }
}

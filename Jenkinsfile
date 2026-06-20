pipeline {
    agent any

    environment {
        DOCKER_IMAGE = "hbi991017/sesacbook"
        EC2_HOST = "52.79.158.202"
        DEPLOY_DIR = "~/SeSACBook"
    }

    stages {
        stage('Build Docker Image') {
            steps {
                sh '''
                    docker build -t $DOCKER_IMAGE:latest -t $DOCKER_IMAGE:$BUILD_NUMBER .
                '''
            }
        }

        stage('Push to DockerHub') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-credentials',
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    sh '''
                        echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
                        docker push $DOCKER_IMAGE:latest
                        docker push $DOCKER_IMAGE:$BUILD_NUMBER
                    '''
                }
            }
        }

        stage('Deploy to EC2') {
            steps {
                withCredentials([sshUserPrivateKey(
                    credentialsId: 'ec2-ssh-key',
                    keyFileVariable: 'SSH_KEY',
                    usernameVariable: 'SSH_USER'
                )]) {
                    sh '''
                        ssh -o StrictHostKeyChecking=no -i "$SSH_KEY" "$SSH_USER"@$EC2_HOST "cd $DEPLOY_DIR && docker compose pull && docker compose up -d"
                    '''
                }
            }
        }
    }

    post {
        success {
            echo "배포 성공"
        }
        failure {
            echo "배포 실패 - 로그 확인 필요"
        }
        always {
            sh 'docker logout || true'
        }
    }
}
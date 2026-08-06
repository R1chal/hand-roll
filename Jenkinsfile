pipeline {
    agent any

    tools {
        jdk 'jdk23'
        maven 'maven3'
    }

    stages {
        stage('拉取代码') {
            steps {
                checkout scm
                sh 'git log --oneline -1'
            }
        }
        stage('编译打包') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }
        stage('发布到 Nexus') {
            steps {
                sh 'mvn deploy -DskipTests'
            }
        }
    }

    post {
        success { echo '✅ 流水线成功' }
        failure { echo '❌ 流水线失败' }
    }
}
